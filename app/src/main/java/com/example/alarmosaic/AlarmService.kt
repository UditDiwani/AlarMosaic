package com.example.alarmosaic

import android.app.Service 
import android.app.PendingIntent

import android.content.Intent

import android.os.IBinder

import android.media.Ringtone
import android.media.RingtoneManager
import android.media.AudioAttributes
import android.media.MediaPlayer


import androidx.core.app.NotificationCompat

class AlarmService : Service(){
    // private var ringtone: Ringtone? = null
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        const val ACTION_STOP = "STOP_ALARM"
        const val ACTION_ALARM_STOPPED = "com.example.alarmosaic.ACTION_ALARM_STOPPED"
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        started: Int
    ): Int{

        if(intent?.action == ACTION_STOP) {
            // ringtone?.stop()
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            stopForeground(STOP_FOREGROUND_REMOVE)
            sendBroadcast(
                Intent(ACTION_ALARM_STOPPED)
            )
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

        val ringingIntent = Intent(
            this,
            AlarmRingingActivity::class.java
        ).apply{
            putExtra("ALARM_ID",alarmId)
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val ringingPendingIntent = PendingIntent.getActivity(
            this,
            alarmId.toInt(),
            ringingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or 
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            this,
            "alarm_channel"
        )
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("AlarMosaic")
        .setContentText("YOUR PHONEE.. LINGING! 2")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setOngoing(true)
        .setFullScreenIntent(
            ringingPendingIntent,
            true
        )
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

        val soundPath = intent?.getStringExtra("SOUND_PATH")

        if(soundPath != null){
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(
                        AudioAttributes.CONTENT_TYPE_MUSIC
                    )
                    .build()
                )

                setDataSource(
                    soundPath
                )

                isLooping = true
                prepare()
                start()
            }
        }

        // val alarmUri = RingtoneManager.getDefaultUri(
        //     RingtoneManager.TYPE_ALARM
        // )

        // ringtone = RingtoneManager.getRingtone(
        //     this,
        //     alarmUri
        // )

        // ringtone?.play()

        return START_NOT_STICKY
    }

    override fun onDestroy(){
        // ringtone?.stop()
        // ringtone = null
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null 
    }
}