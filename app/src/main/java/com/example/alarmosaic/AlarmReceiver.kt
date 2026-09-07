package com.example.alarmosaic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

import android.util.Log


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
            putExtra("SOUND_PATH",soundPath)
        }

        context.startForegroundService(serviceIntent)

    }
}