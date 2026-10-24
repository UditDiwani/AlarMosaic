package com.example.alarmosaic

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.background 
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding 
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button 

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp 
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.IntSize

import coil.compose.AsyncImage

import java.io.File 

import kotlinx.coroutines.launch

@Composable
fun ImageLibraryScreen(
    onBack: () -> Unit,
    onSelectImage: ((String) -> Unit)? = null,
    alarms: List<Alarm> = emptyList(),
    onDeleteAlarm: ((Alarm) -> Unit)? = null,
    onDeleteImage: (suspend (String, Boolean) -> Boolean)? = null  
){
    BackHandler{
        onBack()
    }

    val context = LocalContext.current 

    val coroutineScope = rememberCoroutineScope()

    var images by remember { mutableStateOf(ImageStorage(context).listImages()) }

    var selectedImage by remember {
        mutableStateOf<File?>(null)
    }

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var showDeleteAnywayConfirmation by remember { mutableStateOf(false) }

    val affectedAlarms = selectedImage?.let { image -> 
        alarms.filter { alarm -> 
            alarm.backgroundPath == image.absolutePath
        }
    } ?: emptyList()

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        SkyBackground()

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 24.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                TextButton(
                    onClick = onBack
                ){
                    Text(
                        text = "← Back",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "IMAGE LIBRARY",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            if(images.isEmpty()){
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = "🖼️",
                            fontSize = 48.sp 
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Your gallery is empty",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Images saved as alarm backgrounds \n will appear here.",
                            fontSize = 14.sp 
                        )
                    }
                }
            } else {
                Text(
                    text = "${images.size} saved image${if (images.size == 1) "" else "s"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    items(
                        items = images,
                        key = { it.absolutePath }
                    ){ image -> 
                        Card(
                            modifier = Modifier.fillMaxWidth().aspectRatio(0.85f).clickable {
                                selectedImage = image
                            },
                            shape = RoundedCornerShape(116.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ){
                            AsyncImage(
                                model = image,
                                contentDescription = "Saved alarm background",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop 
                            )
                        }
                    }
                }
            }
        }
    }

    selectedImage?.let { image -> 

        Dialog(
            onDismissRequest = {
                selectedImage = null 
            }
        ){
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ){
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    AsyncImage(
                        model = image,
                        contentDescription = "Full image preview",
                        modifier = Modifier.fillMaxWidth().height(420.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Fit 
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = image.name,
                        fontSize = 13.sp 
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    if(onSelectImage != null){
                        TextButton(
                            onClick = {
                                onSelectImage(image.absolutePath)
                                selectedImage = null 
                                onBack()
                            }
                        ){
                            Text("✓ Use This Image")
                        }
                    }

                    if(onSelectImage == null){
                        TextButton(
                            onClick = {
                                showDeleteConfirmation = true 
                            }
                        ){
                            Text(
                                text = "🗑 Delete Image",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            selectedImage = null 
                        }
                    ){
                        Text("Close Preview")
                    }
                }
            }
        }
    }

    if(showDeleteConfirmation){
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false 
            },
            title = {
                Text("Delete Image?")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    if(affectedAlarms.isEmpty()){
                        Text("Are you sure you want to delete this image?")
                    }else{
                        Text(
                            text = "This image is currently used by" + 
                            "${affectedAlarms.size} alarm(s).",
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().height(240.dp)
                        ){
                            items(
                                items = affectedAlarms,
                                key = { it.id }
                            ){ alarm ->
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                    rootSize = IntSize.Zero 
                                ){
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ){
                                            Text(
                                                text = String.format(
                                                    "%02d : %02d",
                                                    alarm.hour,
                                                    alarm.minute
                                                ),
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold 
                                            )

                                            if (alarm.label.isNotBlank()){
                                                Text(alarm.label)
                                            }

                                            Text(
                                                text = formatRepeatSchedule(
                                                    alarm.repeatDays
                                                )
                                            )
                                        }
                                        if(onDeleteAlarm != null){
                                            Button(
                                                onClick = {
                                                    onDeleteAlarm(alarm)
                                                }
                                            ){
                                                Text("Delete")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Delete these alarm(s) individually first," +
                            "or Delete Anyway to keep them " +
                            "with their default backgrounds.",
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val image = selectedImage
                        val deleteCallBack = onDeleteImage

                        if(image != null && deleteCallBack != null){
                            val deleteAnyway = affectedAlarms.isNotEmpty()

                            coroutineScope.launch {
                                val deleted = deleteCallBack(
                                    image.absolutePath,
                                    deleteAnyway
                                )

                                if(deleted) {
                                    images = ImageStorage(context).listImages()
                                    selectedImage = null
                                    showDeleteConfirmation = false
                                }
                            } 
                        }
                    }
                ){
                    Text(
                        text = if (affectedAlarms.isEmpty()){
                            "Delete"
                        }else{
                            "Delete Anyway"
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false 
                    }
                ){
                    Text("Cancel")
                }
            }
        )
    }
}