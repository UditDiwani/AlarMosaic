package com.example.alarmosaic

import android.app.AlarmManager
import android.app.PendingIntent

import android.content.Context
import android.content.Intent

import android.net.Uri

import android.provider.Settings

import java.util.Calendar

import android.util.Log

class AlarmScheduler(
    private val context: Context
){
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarm: Alarm){

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if(calendar.timeInMillis <= System.currentTimeMillis()){
            calendar.add(Calendar.DAY_OF_YEAR,1)
        }

        val pendingIntent = getPendingIntent(alarm)

        Log.d(
            "AlarMosaicAlarm",
            "ALARM SCHEDULED FOR ${calendar.time}"
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
    
    fun canScheduleExactAlarms(): Boolean {
        return alarmManager.canScheduleExactAlarms()
    }

    fun openExactAlarmSettings(){
        val intent = Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            Uri.parse("package:${context.packageName}")
        )

        context.startActivity(intent)
    }

    private fun getPendingIntent(alarm: Alarm): PendingIntent {

        val intent = Intent(
            context,
            AlarmReceiver::class.java
        ).apply {
            putExtra("ALARM_ID", alarm.id)
        }

        return PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or 
                    PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun cancel(alarm: Alarm){

        val pendingIntent = getPendingIntent(alarm)

        alarmManager.cancel(pendingIntent)

        Log.d(
            "AlarMosaicAlarm",
            "ALARM CANCELLED: ${alarm.id}"
        )
    }
}


