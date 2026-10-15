package com.example.alarmosaic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

import android.util.Log

import kotlinx.coroutines.CoroutineScope 
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch 

class AlarmReceiver : BroadcastReceiver(){
    override fun onReceive(
        context: Context,
        intent: Intent
    ){

        val alarmId = intent.getLongExtra(
            "ALARM_ID",
            -1L
        )
        
        val soundPath = intent.getStringExtra(
            "SOUND_PATH"
        )

        val alarmLabel = intent.getStringExtra(
            "ALARM_LABEL"
        )
        
        val alarmTime = intent.getStringExtra(
            "ALARM_TIME"
        )

        val time = SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        Log.d(
            "AlarMosaicAlarm",
            "ALARM FIRED!! ID = $alarmId at $time"
        )

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("ALARM_ID",alarmId)
            putExtra("ALARM_LABEL",alarmLabel)
            putExtra("ALARM_TIME",alarmTime)

            if(soundPath!=null){
                putExtra("SOUND_PATH",soundPath)
            }
        }

        context.startForegroundService(serviceIntent)

        val alarmStorage = AlarmStorage(context)
        val alarmScheduler = AlarmScheduler(context)

        CoroutineScope(Dispatchers.IO).launch {
            val alarm = alarmStorage.getAlarm(alarmId)

            if(alarm!=null && alarm.repeatDays.isNotEmpty()){
                alarmScheduler.schedule(alarm)
            }
        }
    }
}