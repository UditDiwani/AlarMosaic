package com.example.alarmosaic

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Text
import androidx.compose.material3.Button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import android.content.Intent

@Composable
fun AddAlarmScreen(
    onBack: () -> Unit,
    onSave: (Int, Int, String?) -> Unit
){
    BackHandler{
        onBack()
    }
    var hour by remember { mutableStateOf(7) }
    var minute by remember { mutableStateOf(30) }

    var selectedSoundUri by remember {
        mutableStateOf<String?>(null)
    }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ){
        uri -> 

        if(uri != null){
            selectedSoundUri = uri.toString()
        }
    }

    

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Row{
            Text(
                text = String.format("%02d : %02d", hour, minute)
            )
        }

        Row{
            Button(
                onClick = {
                    hour = (hour + 1) % 24
                }
            ){
                Text("+ 1 Hour")
            }
            Button(
                onClick = {
                    minute = (minute + 1) % 60
                }
            ){
                Text("+ 1 Minutes")
            }
        }

        Row{
            Button(
                onClick = {
                    hour = (hour - 1 + 24) %24
                }
            ){
                Text("- 1 Hour")
            }
    
    
            Button(
                onClick = {
                    minute = (minute - 1 + 60) % 60
                }
            ){
                Text("- 1 Minutes")
            }
        }

        Button(
            onClick = {
                audioPicker.launch(
                    arrayOf("audio/*")
                )
            }
        ){
            Text("🎶 Choose Audio 🎶")
        }
        if(selectedSoundUri != null){
            Text(
                text = selectedSoundUri!!
            )
        }

        Button(
            onClick = {
                onSave(hour,minute,selectedSoundUri)
            }
        ){
            Text("Save Alarm")
        }

        Button(
            onClick = onBack
        ){
            Text("Back")
        }
    }
}