package com.example.alarmosaic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement


import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment


import androidx.compose.material3.Text
import androidx.compose.material3.Button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

@Composable
fun AlarmScreen() {

    var showAddAlarm by remember { mutableStateOf(false) }

    if(showAddAlarm){
        AddAlarmScreen(
            onBack = {
                showAddAlarm = false
            }
        )
    }
    else{
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "AlarMosaic"
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Text(
                    text = "No Alarms"
                )
            }
            Button(
                onClick = { 
                    showAddAlarm = true 
                }
            ){
                Text("+ ADD ALARM")
            }
        }
    }
}