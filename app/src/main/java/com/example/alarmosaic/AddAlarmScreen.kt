package com.example.alarmosaic

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card 

import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color 
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.window.Dialog

import kotlinx.coroutines.launch

import android.content.Intent
import android.media.MediaPlayer
import com.example.alarmosaic.AudioArtifact
import java.awt.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmScreen(
    onBack: () -> Unit,
    onSave: (Int, Int, String?,AudioArtifact?, String) -> Unit
){
    BackHandler{
        onBack()
    }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var hour by remember { mutableStateOf(7) }
    var minute by remember { mutableStateOf(30) }
    var showTimePicker by remember  {
        mutableStateOf(false)
    }
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )

    var selectedSoundPath by remember {
        mutableStateOf<String?>(null)
    }

    var audioName by remember { mutableStateOf("") }

    var alarmLabel by remember { mutableStateOf("") }

    var pendingAudioArtifact by remember {
        mutableStateOf<AudioArtifact?>(null)
    }

    var isPlaying by remember { mutableStateOf(false) }

    val previewPlayer = remember { mutableStateOf<MediaPlayer?>(null) }

    var showMediaLibraryPicker by remember { mutableStateOf(false) }

    var mediaArtifacts by remember { mutableStateOf<List<AudioArtifact>>(emptyList())}

    var mediaPreviewPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    var playingArtifactId by remember { mutableStateOf<Long?>(null) }

    var isExistingMediaSelection by remember { mutableStateOf(false) }

    var selectedAudioName by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            previewPlayer.value?.release()
            previewPlayer.value = null

            mediaPreviewPlayer?.release()
            mediaPreviewPlayer = null 
            playingArtifactId = null 
        }
    }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ){
        uri -> 

        if(uri != null){
            
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    takeFlags
                )

                val audioStorage = AudioStorage(context)

                val copiedPath = audioStorage.copyAudio(uri)

                selectedSoundPath = copiedPath
                isExistingMediaSelection = false
                pendingAudioArtifact = null
                audioName = ""
                selectedAudioName = null 
            }
            catch(e: SecurityException){
                e.printStackTrace()
            }
        }
    }

    var rootSize by remember {mutableStateOf(IntSize.Zero)}

    LaunchedEffect(showMediaLibraryPicker) {
        if(showMediaLibraryPicker){
            val storage = AudioArtifactStorage(context)
            mediaArtifacts = storage.loadAll()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().onGloballyPositioned{ coordinates -> 
            rootSize = coordinates.size
        }
    ){
        SkyBackground()

        Column(
            modifier = Modifier.fillMaxSize().padding(
                horizontal = 20.dp,
                vertical = 24.dp
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            //Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                TextButton(
                    onClick = onBack
                ){
                    Text(
                        text = "<- Back",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "ADD ALARM",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(
                    modifier = Modifier.width(16.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(36.dp)
            )

            //Time Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                rootSize = rootSize
            ){
                Column(
                    modifier = Modifier.fillMaxWidth().padding(
                        vertical = 28.dp,
                        horizontal = 20.dp
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Spacer(
                        modifier = Modifier.height(40.dp)
                    )

                    Text(
                        text = String.format("%02d : %02d", hour, minute),
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            showTimePicker = true
                        }
                    )
                        
                    Text(
                        text = "Tap to choose time",
                        fontSize = 14.sp
                    )
                }
            }
                    
            Spacer(
                modifier = Modifier.height(20.dp)
            )

            OutlinedTextField(
                value = alarmLabel,
                onValueChange = { alarmLabel = it },
                label = { Text("Alarm Label")},
                placeholder = { Text("e.g. Wake up!") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
                    
            //Sound Card
                    
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                rootSize = rootSize
            ){
                Column(
                    modifier = Modifier.fillMaxWidth().padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "🎵 Alarm Sound 🎵",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                        
                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )
                            
                    if(selectedSoundPath == null){

                        Text(
                            text = "Default sound",
                            fontSize = 16.sp
                        )
                    } else {

                        Text(
                            text = if(isExistingMediaSelection){
                                "🎵 ${selectedAudioName ?: "Library audio"}"
                            }else{
                                "Audio imported! ✅"
                            },
                            fontSize = 16.sp
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Button(
                                onClick = {
                                    if(isPlaying){
                                        previewPlayer.value?.pause()
                                        isPlaying = false
                                    }else{
                                        previewPlayer.value?.release()

                                        val player = MediaPlayer().apply {
                                            setDataSource(selectedSoundPath)

                                            setOnCompletionListener {
                                                isPlaying = false
                                            }

                                            prepare()
                                            start()
                                        }

                                        previewPlayer.value = player
                                        isPlaying = true 
                                    }
                                }
                            ){
                                Text(
                                    text = if(isPlaying) "⏸ Pause" else "▶ Play"
                                )
                            }

                            Button(
                                onClick = {
                                    previewPlayer.value?.stop()
                                    previewPlayer.value?.release()
                                    previewPlayer.value = null 
                                    isPlaying = false 
                                }
                            ){
                                Text("⏹ Stop")
                            }
                        }

                        if(!isExistingMediaSelection){

                            OutlinedTextField(
                                value = audioName,
                                onValueChange = {
                                    audioName = it 
                                },
                                label = {
                                    Text("Name this audio")
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
    
                            Button(
                                onClick = {
                                    val path = selectedSoundPath
    
                                    if(path!=null && audioName.isNotBlank()){
                                        coroutineScope.launch {
    
                                            val idStorage = IdStorage(context)
    
                                            val artifactId = idStorage.nextAudioArtifactId()
    
                                            pendingAudioArtifact = AudioArtifact(
                                                id = artifactId,
                                                name = audioName.trim(),
                                                filePath = path 
                                            )
                                        }
                                    }
                                },
                                enabled = audioName.isNotBlank()
                            ){
                                Text("Confirm")
                            }
                        }

                    }
                                
                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )
                                    
                    Button(
                        onClick = {
                            audioPicker.launch(
                                arrayOf("audio/*")
                            )
                        }
                    ){

                        Text("📥 Import New Audio")
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Button(
                        onClick = {
                            showMediaLibraryPicker = true 
                        }
                    ){
                        Text("📚 Media Library")
                    }
                }
            }

            //Pushing action buttons down
            Spacer(
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    onSave(hour,minute,selectedSoundPath,pendingAudioArtifact,alarmLabel)
                },
                modifier = Modifier.fillMaxWidth().height(58.dp)
            ){
                Text(
                    text = "Save Alarm",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
    
            TextButton(
                onClick = onBack
            ){
                Text("Cancel")
            }
            
        }
        
    }

    if(showMediaLibraryPicker){
        Dialog(
            onDismissRequest = {
                showMediaLibraryPicker = false
            }
        ){
            Card(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.75f),
                shape = RoundedCornerShape(24.dp)
            ){
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp)
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "🎶 Select Media 🎶",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold 
                        )
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            onClick = {
                                showMediaLibraryPicker = false 
                            }
                        ){
                            Text("✕")
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    if(mediaArtifacts.isEmpty()){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center 
                        ){
                            Text(
                                text = "No media available yet."
                            )
                        }
                    }else{
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ){
                            items(mediaArtifacts) { artifact -> 
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    rootSize = rootSize
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                                    ){
                                        Text(
                                            text = "🎵 ${artifact.name}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold 
                                        )

                                        Spacer(
                                            modifier = Modifier.height(12.dp)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ){
                                            Button(
                                                onClick = {
                                                    if(playingArtifactId == artifact.id){
                                                        mediaPreviewPlayer?.stop()
                                                        mediaPreviewPlayer?.release()
                                                        mediaPreviewPlayer = null 
                                                        playingArtifactId = null 
                                                    }else{
                                                        mediaPreviewPlayer?.release()

                                                        val player = MediaPlayer().apply {
                                                            setDataSource(artifact.filePath)

                                                            setOnCompletionListener {
                                                                mediaPreviewPlayer?.release()
                                                                mediaPreviewPlayer = null 
                                                                playingArtifactId = null 
                                                            }

                                                            prepare()
                                                            start()
                                                        }

                                                        mediaPreviewPlayer = player 
                                                        playingArtifactId = artifact.id 
                                                    }
                                                },
                                                modifier = Modifier.weight(1f)
                                            ){
                                                Text(
                                                    if(playingArtifactId == artifact.id)
                                                        "⏹ Stop"
                                                    else 
                                                        "▶ Play"
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    mediaPreviewPlayer?.stop()
                                                    mediaPreviewPlayer?.release()
                                                    mediaPreviewPlayer = null 
                                                    playingArtifactId = null 

                                                    selectedSoundPath = artifact.filePath
                                                    selectedAudioName = artifact.name 
                                                    isExistingMediaSelection = true 
                                                    pendingAudioArtifact = null 
                                                    audioName = ""
                                                    showMediaLibraryPicker = false 
                                                },
                                                modifier = Modifier.weight(1f)
                                            ){
                                                Text("Select")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if(showTimePicker){
        TimePickerDialog(
            onDismissRequest = {
                showTimePicker = false
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        hour = timePickerState.hour
                        minute = timePickerState.minute
                        showTimePicker = false
                    }
                ){
                    Text("OK")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ){
                    Text("Cancel")
                }
            },

            title = {
                Text("Set Alarm Time")
            }
        ){
            TimePicker(
                state = timePickerState
            )
        }
    }
}