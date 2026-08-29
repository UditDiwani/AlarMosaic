package com.example.alarmosaic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement


import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext


import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Switch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope

import kotlinx.coroutines.launch

@Composable
fun AlarmScreen() {

    var showAddAlarm by remember { mutableStateOf(false) }
    var alarms by remember { mutableStateOf(listOf<Alarm>())}

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val storage = remember { 
        AlarmStorage(context)
    }

    LaunchedEffect(Unit){
        alarms = storage.loadAlarms()
    }

    if(showAddAlarm){
        AddAlarmScreen(
            onBack = {
                showAddAlarm = false
            },
            onSave = { hour, minute -> 
                val updatedAlarms = alarms + Alarm(hour,minute)
                alarms = updatedAlarms
                scope.launch {
                    storage.saveAlarms(updatedAlarms)
                }
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
                if(alarms.isEmpty()){
                    Text("No Alarms")
                }
                else{
                    alarms.forEach { alarm -> 
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = String.format("%02d : %02d", alarm.hour,alarm.minute)
                            )
                            Switch(
                                checked = alarm.enabled,
                                onCheckedChange = { enabled -> 
                                    val updatedAlarms = alarms.map { currentAlarm -> 
                                        if(currentAlarm == alarm){
                                            alarm.copy(enabled = enabled)
                                        }else{
                                            currentAlarm
                                        }
                                    }
                                    alarms = updatedAlarms

                                    scope.launch { 
                                        storage.saveAlarms(updatedAlarms)
                                    }
                                }
                            )
                        }
                    }
                }
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