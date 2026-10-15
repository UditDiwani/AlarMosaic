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

        if(!alarm.enabled){
            cancel(alarm)

            Log.d(
                "AlarMosaicAlarm",
                "SKIPPING DISABLED ALARM: ${alarm.id}"
            )
            return 
        }

        val triggerTime = getNextTriggerTime(alarm)

        val pendingIntent = getPendingIntent(alarm)

        Log.d(
            "AlarMosaicAlarm",
            "ALARM SCHEDULED FOR ${Calendar.getInstance().apply { 
                timeInMillis = triggerTime
            }.time}"
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    private fun getNextTriggerTime(alarm: Alarm): Long {
        val now = Calendar.getInstance()

        val candidate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        //One-time
        if(alarm.repeatDays.isEmpty()){
            if(candidate.timeInMillis <= System.currentTimeMillis()){
                candidate.add(Calendar.DAY_OF_YEAR,1)
            }

            return candidate.timeInMillis
        }
        //Repeating Alarm
        for (daysAhaed in 0..6){
            val dayCandidate = candidate.clone() as Calendar

            dayCandidate.add(
                Calendar.DAY_OF_YEAR,
                daysAhaed
            )

            val weekday = dayCandidate.get(Calendar.DAY_OF_WEEK)

            if(weekday in alarm.repeatDays && dayCandidate.timeInMillis > now.timeInMillis){
                return dayCandidate.timeInMillis
            }
        }
        //Just-in-case
        candidate.add(Calendar.DAY_OF_YEAR,7)
        return candidate.timeInMillis
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
            putExtra("ALARM_LABEL",alarm.label)
            putExtra("ALARM_TIME",String.format("%02d : %02d",alarm.hour,alarm.minute))

            if(alarm.soundPath != null){
                putExtra("SOUND_PATH",alarm.soundPath)
            }
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


