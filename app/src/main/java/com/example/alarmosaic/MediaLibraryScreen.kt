package com.example.alarmosaic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth


import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import android.media.MediaPlayer

import kotlinx.coroutines.launch

@Composable
fun MediaLibraryScreen(
    onBack: () -> Unit
){

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()

    var artifacts by remember { 
        mutableStateOf<List<AudioArtifact>>(emptyList())
    }

    var alarms by remember {
        mutableStateOf<List<Alarm>>(emptyList())
    }

    var showDeleteWarning by remember {
        mutableStateOf(false)
    }

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    var artifactPendingDeletion by remember {
        mutableStateOf<AudioArtifact?>(null)
    }

    var previewPlayer by remember { 
        mutableStateOf<MediaPlayer?>(null)
    }

    var playingArtifactId by remember {
        mutableStateOf<Long?>(null)
    }

    var rootSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    var pendingAudioPath by remember { mutableStateOf<String?>(null) }
    var showNameDialog by remember { mutableStateOf(false) } 
    var mediaName by remember { mutableStateOf("") }

    var audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ){ uri ->
        if(uri != null){
            coroutineScope.launch {
                val audioStorage = AudioStorage(context)

                val path = audioStorage.copyAudio(uri)

                pendingAudioPath = path 
                mediaName = ""
                showNameDialog = true 
            }
        }
    }

    DisposableEffect(Unit){

        onDispose {
            previewPlayer?.release()
            previewPlayer = null
            playingArtifactId = null
        }
    }

    LaunchedEffect(Unit) {
        val audioStorage = AudioArtifactStorage(context)
        val alarmStorage = AlarmStorage(context)

        artifacts = audioStorage.loadAll()
        alarms = alarmStorage.loadAlarms()
    }

    Button(
        onClick = onBack
    ){
        Text("← Back")
    }

    Column(
        modifier = Modifier.fillMaxSize().onGloballyPositioned{rootSize = it.size}.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top 
    ){
        Text(
            text = "🎵 MEDIA LIBRARY 🎵"
        )

        Button(
            onClick = {
                audioPickerLauncher.launch(arrayOf("audio/*"))
            }
        ){
            Text("+ ADD MEDIA")
        }
        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if(artifacts.isEmpty()){
            Text(
                text = "No media yet."
            )
        } else {
            artifacts.forEach { artifact ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    rootSize = rootSize
                ) { 
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(20.dp)
                    ){
                        Text(
                            text = "🎵 ${artifact.name}"
                        )
                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )
                        Text(
                            text = "Audio • ID ${artifact.id}"
                        )
                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ){
                            Button(
                                onClick = {
                                    if(playingArtifactId == artifact.id){
                                        previewPlayer?.release()
                                        previewPlayer = null 
                                        playingArtifactId = null 
                                    } else {
                                        previewPlayer?.release()
                                        val player = MediaPlayer()

                                        player.setDataSource(artifact.filePath)
                                        player.prepare()
                                        player.start()
                                        player.setOnCompletionListener {
                                            playingArtifactId = null 
                                            

                                            if (previewPlayer == it){
                                                previewPlayer = null 
                                            }
                                            it.release()
                                        }
                                        previewPlayer = player
                                        playingArtifactId = artifact.id 
                                    }
                                }
                            ){
                                Text(
                                    text = if(playingArtifactId == artifact.id)
                                              "⏹️ Stop"
                                           else
                                              "▶️ Preview"
                                )
                            }

                            Button(
                                onClick = {
                                    previewPlayer?.release()
                                    previewPlayer = null 
                                    playingArtifactId = null 

                                    val isUsedByAlarm = alarms.any { alarm ->
                                        alarm.soundPath == artifact.filePath
                                    }

                                    if(isUsedByAlarm){
                                        showDeleteWarning = true 
                                    }
                                    else{
                                        artifactPendingDeletion = artifact
                                        showDeleteConfirmation = true
                                    }
                                }
                            ){
                                Text("🗑️ Delete")
                            }
                        }
                    }
                }
            }
        }
    }

    if(showDeleteWarning){
        AlertDialog(
            onDismissRequest = {
                showDeleteWarning = false
            },
            title = {
                Text("⚠️ Can't delete this media")
            },
            text = {
                Text("This sound is currently being used by one or more alarms.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteWarning = false
                    }
                ){
                    Text("OK")
                }
            }
        )
    }
    if(showDeleteConfirmation && artifactPendingDeletion != null){
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false 
                artifactPendingDeletion = null 
            },
            title = {
                Text("🗑️ Delete media?")
            },
            text = {
                Text("\"${artifactPendingDeletion!!.name}\" will be removed from your Media Library.")
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false 
                        artifactPendingDeletion = null 
                    }
                ){
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val artifact = artifactPendingDeletion

                        showDeleteConfirmation = false 
                        artifactPendingDeletion = null 

                        if(artifact !=null ){
                            coroutineScope.launch {
                                val artifactStorage = AudioArtifactStorage(context)
                                val audioStorage = AudioStorage(context)

                                audioStorage.deleteAudio(artifact.filePath)
                                artifactStorage.delete(artifact.id)

                                artifacts = artifactStorage.loadAll()
                            }
                        }
                    }
                ){
                    Text("Delete")
                }
            }
        )
    }
    if (showNameDialog){
        AlertDialog(
            onDismissRequest = {
                val path = pendingAudioPath

                if(path!=null){
                    coroutineScope.launch {
                        val audioStorage = AudioStorage(context)
                        audioStorage.deleteAudio(path)
                    }
                }

                showNameDialog = false 
                pendingAudioPath = null 
                mediaName = ""
            },
            title = {
                Text("🎵 Name your media🎵")
            },
            text = {
                OutlinedTextField(
                    value = mediaName,
                    onValueChange = { mediaName = it },
                    label = {
                        Text("Media name")
                    },
                    singleLine = true 
                )
            },
            dismissButton = {
                Button(
                    onClick = {
                        val path = pendingAudioPath

                        if(path!=null){
                            coroutineScope.launch {
                                val audioStorage = AudioStorage(context)
                                audioStorage.deleteAudio(path)
                            }
                        }
                        showNameDialog = false
                        pendingAudioPath = null
                        mediaName = ""
                    }
                ){
                    Text("Cancel")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val path = pendingAudioPath

                        if(path != null && mediaName.isNotBlank()){
                            coroutineScope.launch {
                                val idStorage = IdStorage(context)
                                val artifactId = idStorage.nextAudioArtifactId()
                                val artifact = AudioArtifact(
                                    id = artifactId,
                                    name = mediaName.trim(),
                                    filePath = path
                                )

                                val artifactStorage = AudioArtifactStorage(context)

                                artifactStorage.save(artifact)

                                artifacts = artifactStorage.loadAll()

                                showNameDialog = false
                                pendingAudioPath = null
                                mediaName =""
                            }
                        }
                    },
                    enabled = mediaName.isNotBlank()
                ){
                    Text("Save")
                }
            }
        )
    }
}