package com.example.alarmosaic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height



import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.onGloballyPositioned

import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope

import androidx.lifecycle.Lifecycle 
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

import kotlinx.coroutines.launch

import java.util.Calendar

@Composable
fun AlarmScreen() {

    var showAddAlarm by remember { mutableStateOf(false) }
    
    var showMediaLibrary by remember { 
        mutableStateOf(false)
    }

    var showImageLibrary by remember { 
        mutableStateOf(false)
    }

    var alarms by remember { mutableStateOf(listOf<Alarm>())}
    
    var pendingAlarm by remember { 
        mutableStateOf<Alarm?>(null)
    }
    var showPermissionDialog by remember { mutableStateOf(false)}

    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    
    val storage = remember { 
        AlarmStorage(context)
    }

    val scheduler = remember{
        AlarmScheduler(context)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner){
        val observer = LifecycleEventObserver { _,event -> 
            if(event == Lifecycle.Event.ON_RESUME){
                coroutineScope.launch {
                    val alarm = pendingAlarm

                    if(alarm != null && scheduler.canScheduleExactAlarms()){
                        val updatedAlarms = alarms + alarm

                        alarms = updatedAlarms
                        storage.saveAlarms(updatedAlarms)
                        scheduler.schedule(alarm)

                        pendingAlarm = null 
                        showAddAlarm = false 
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        try {
            kotlinx.coroutines.awaitCancellation()
        }finally{
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var rootSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    LaunchedEffect(Unit){
        alarms = storage.loadAlarms()
    }

    if(showAddAlarm){
        AddAlarmScreen(
            onBack = {
                showAddAlarm = false
            },
            onSave = { hour, minute, soundPath,audioArtifact, label,repeatDays,backgroundPath ->
                
                coroutineScope.launch {

                    val idStorage = IdStorage(context)

                    val alarmId = idStorage.nextAlarmId()

                    if(audioArtifact !=null){
                        val audioArtifactStorage = AudioArtifactStorage(context)
                        audioArtifactStorage.save(audioArtifact)
                    }

                    val newAlarm = Alarm(
                        id = alarmId,
                        hour = hour,
                        minute = minute,
                        label = label,
                        soundPath = soundPath,
                        repeatDays = repeatDays,
                        backgroundPath = backgroundPath
                    )
                    if (scheduler.canScheduleExactAlarms()){
                        val updatedAlarms = alarms + newAlarm
    
                        alarms = updatedAlarms
    
                        storage.saveAlarms(updatedAlarms)
                        
                        scheduler.schedule(newAlarm)
    
                        showAddAlarm = false
                    }else{
                        pendingAlarm = newAlarm
                        showPermissionDialog = true
                    }
                }

            }
        )
    }

    else if(showMediaLibrary){
        MediaLibraryScreen(
            onBack = {
                showMediaLibrary = false
            }
        )
    }

    else if (showImageLibrary){
        ImageLibraryScreen(
            onBack = {
                showImageLibrary = false 
            },
            alarms = alarms,
            onDeleteAlarm = { alarmToDelete -> 
                coroutineScope.launch {
                    scheduler.cancel(alarmToDelete)
                    storage.delete(alarmToDelete.id)
                    alarms = storage.loadAlarms()
                }
            },
            onDeleteImage = { imagePath, deleteAnyway -> 
                val currentAlarms = storage.loadAlarms()
                val affected = currentAlarms.filter {
                    it.backgroundPath == imagePath
                }

                if(affected.isNotEmpty() && !deleteAnyway){
                    false
                } else {
                    val updatedAlarms = currentAlarms.map { alarm -> 
                        if(alarm.backgroundPath == imagePath){
                            alarm.copy(backgroundPath = null)
                        }else{
                            alarm
                        }
                    }

                    if(affected.isNotEmpty()){
                        storage.saveAlarms(updatedAlarms)
                        alarms = updatedAlarms
                    }

                    val deleted = ImageStorage(context).deleteImage(imagePath)

                    if(deleted){
                        alarms = storage.loadAlarms()
                    }
                    
                    deleted
                }
            }
        )
    }

    else{

        Box(
            modifier = Modifier.fillMaxSize()
                        .onGloballyPositioned{ coordinates -> 
                            rootSize = coordinates.size
                        }

        ){
            SkyBackground()
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Button(
                    onClick = {
                        showMediaLibrary = true
                    }
                ){
                    Text("🎵MEDIA LIBRARY")
                }

                Text(
                    text = "AlarMosaic"
                )

                Button(
                    onClick = {
                        showImageLibrary = true 
                    }
                ){
                    Text("🖼️IMAGE LIBRARY")
                }
            }
            Spacer(
                modifier = Modifier.height(5.dp)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    if(alarms.isEmpty()){
                        Text("No Alarms")
                    }
                    else{
                        
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                                .weight(1f)
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 48.dp
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
    
                        ){
                            items(alarms) { alarm -> 
        
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        rootSize = rootSize
                                )
                                    
                                {
        
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        // horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){
        
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.Center
                                        ){
                                            Text(
                                                text = String.format("%02d : %02d", alarm.hour,alarm.minute),
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            
                                            if(alarm.label.isNotBlank()){
                                                Text(
                                                    text = alarm.label,
                                                    fontSize = 16.sp
                                                )
                                            }

                                            Text(
                                                text = formatRepeatSchedule(alarm.repeatDays)
                                            )

                                            Text(
                                                text = if(alarm.soundPath!=null){
                                                    "🎶✨ Custom Sound ✨🎶"
                                                }else{
                                                    "🔔 Default Sound 🔔"
                                                },
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ){
                                            Switch(
                                                checked = alarm.enabled,
                                                onCheckedChange = { enabled -> 
                
                                                    val updatedAlarms = alarms.map { currentAlarm -> 
                                                        if(currentAlarm.id == alarm.id){
                                                            currentAlarm.copy(enabled = enabled)
                                                        }else{
                                                            currentAlarm
                                                        }
                                                    }
                                                    alarms = updatedAlarms

                                                    val updated = updatedAlarms.first { it.id == alarm.id }

                                                    coroutineScope.launch { 
                                                        storage.saveAlarms(updatedAlarms)
                                                        if(updated.enabled){
                                                            scheduler.schedule(updated)
                                                        }else{
                                                            scheduler.cancel(updated)
                                                        }
                                                    }
                                                }
                                            )
                                            Button(
                                                onClick = {
                
                                                    scheduler.cancel(alarm)
                                                    coroutineScope.launch{
                                                        storage.delete(alarm.id)
                                                        alarms = storage.loadAlarms()
                                                    }
                                                }
                                            ){
                                                Text("Delete")
                                            }

                                        }
        
                                    }
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

fun formatRepeatSchedule(repeatDays: Set<Int>): String {
    if(repeatDays.isEmpty()){
        return "Once"
    }

    val allDays = setOf(
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
        Calendar.SUNDAY
    )

    if(repeatDays == allDays){
        return "Every Day"
    }

    val dayNames = mapOf(
        Calendar.MONDAY to "Mon",
        Calendar.TUESDAY to "Tue",
        Calendar.WEDNESDAY to "Wed",
        Calendar.THURSDAY to "Thu",
        Calendar.FRIDAY to "Fri",
        Calendar.SATURDAY to "Sat",
        Calendar.SUNDAY to "Sun"
    )

    return repeatDays.sortedBy { day ->
        when (day) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 7
        }
    }.joinToString(" • ") { day ->
        dayNames[day] ?: ""
    }
}