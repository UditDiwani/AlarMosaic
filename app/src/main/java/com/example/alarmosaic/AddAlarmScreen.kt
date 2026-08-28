package com.example.alarmosaic

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

@Composable
fun AddAlarmScreen(
    onBack: () -> Unit
){

    var hour by remember { mutableStateOf(7) }
    var minute by remember { mutableStateOf(30) }

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
                    minute = (minute + 5) % 60
                }
            ){
                Text("+ 5 Minutes")
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
                    minute = (minute - 5 + 60) % 60
                }
            ){
                Text("- 5 Minutes")
            }
        }

        Button(
            onClick = onBack
        ){
            Text("Back")
        }
    }
}