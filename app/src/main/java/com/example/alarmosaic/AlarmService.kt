package com.example.alarmosaic

import android.app.Service 
import android.app.PendingIntent

import android.content.Intent

import android.os.IBinder

import android.media.Ringtone
import android.media.RingtoneManager

import androidx.core.app.NotificationCompat

class AlarmService : Service(){

    private var ringtone: Ringtone? = null

    companion object {
        const val ACTION_STOP = "STOP_ALARM"
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        started: Int
    ): Int{

        if(intent?.action == ACTION_STOP) {
            ringtone?.stop()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        val alarmId = intent?.getLongExtra(
            "ALARM_ID",
            -1L
        )?: -1L

        val stopIntent = Intent(
            this, 
            AlarmService::class.java
        ).apply {
            action = ACTION_STOP
        }
        
        val stopPendingIntent = PendingIntent.getService(
            this,
            alarmId.toInt(),
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            this,
            "alarm_channel"
        )
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("AlarMosaic")
        .setContentText("YOUR PHONEE.. LINGING! 2")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setOngoing(true)
        .addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            "STOP",
            stopPendingIntent
        )
        .build()

        startForeground(
            alarmId.toInt(),
            notification
        )

        val alarmUri = RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_ALARM
        )

        ringtone = RingtoneManager.getRingtone(
            this,
            alarmUri
        )

        ringtone?.play()

        return START_NOT_STICKY
    }

    override fun onDestroy(){
        ringtone?.stop()
        ringtone = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null 
    }
}