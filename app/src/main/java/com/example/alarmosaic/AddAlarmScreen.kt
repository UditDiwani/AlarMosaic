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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color 
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.onGloballyPositioned

import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlarmScreen(
    onBack: () -> Unit,
    onSave: (Int, Int, String?) -> Unit
){
    BackHandler{
        onBack()
    }

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
            }
            catch(e: SecurityException){
                e.printStackTrace()
            }
        }
    }

    var rootSize by remember {mutableStateOf(IntSize.Zero)}

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
                            
                    Text(
                        text = if(selectedSoundPath == null){
                            "Default sound"
                        }else{
                            "🎶 Custom Sound Selected 🎶"
                        },
                        fontSize = 14.sp 
                    )
                                
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

                        Text(
                            if(selectedSoundPath == null)
                                "Choose Audio"
                            else
                                "Change Audio"
                        )
                    }               
                }
            }

            //Pushing action buttons down
            Spacer(
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    onSave(hour,minute,selectedSoundPath)
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