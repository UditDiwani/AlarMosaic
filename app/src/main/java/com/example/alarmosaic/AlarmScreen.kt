package com.example.alarmosaic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Arrangement


import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment


import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

@Composable
fun AlarmScreen() {
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
    }
}