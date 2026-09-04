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
import androidx.compose.material3.AlertDialog

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
    
    var pendingAlarm by remember { 
        mutableStateOf<Alarm?>(null)
    }
    var showPermissionDialog by remember { mutableStateOf(false)}

    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    
    val storage = remember { 
        AlarmStorage(context)
    }

    val scheduler = remember{
        AlarmScheduler(context)
    }



    LaunchedEffect(Unit){
        alarms = storage.loadAlarms()
    }

    if(showAddAlarm){
        AddAlarmScreen(
            onBack = {
                showAddAlarm = false
            },
            onSave = { hour, minute, soundPath -> 
                val newAlarm = Alarm(
                    id = System.currentTimeMillis(),
                    hour = hour,
                    minute = minute,
                    soundPath = soundPath
                )

                if (scheduler.canScheduleExactAlarms()){
                    val updatedAlarms = alarms + newAlarm

                    alarms = updatedAlarms

                    scope.launch {
                        storage.saveAlarms(updatedAlarms)
                    }

                    scheduler.schedule(newAlarm)

                    showAddAlarm = false
                }else{
                    pendingAlarm = newAlarm
                    showPermissionDialog = true
                }
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

                                    if(enabled){
                                        scheduler.schedule(alarm)
                                    }else{
                                        scheduler.cancel(alarm)
                                    }

                                    val updatedAlarms = alarms.map { currentAlarm -> 
                                        if(currentAlarm.id == alarm.id){
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
                            Button(
                                onClick = {

                                    scheduler.cancel(alarm)

                                    val updatedAlarms = alarms.filter {
                                        it.id != alarm.id
                                    }
                                    alarms = updatedAlarms
                                    scope.launch{
                                        storage.saveAlarms(updatedAlarms)
                                    }
                                }
                            ){
                                Text("Delete")
                            }
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
    if(showPermissionDialog){
        AlertDialog(
            onDismissRequest = {
                showPermissionDialog = false
                pendingAlarm = null
            },
            title = {
                Text("Exact alarm access")
            },
            text = {
                Text(
                    "AlarMosaic needs permission to wake your phone at the exact time you choose"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        scheduler.openExactAlarmSettings()
                    }
                ){
                    Text("Continue")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showPermissionDialog = false
                        pendingAlarm = null
                    }
                ){
                    Text("Cancel")
                }
            }
        )
    }
}