package com.example.alarmosaic

import android.app.NotificationChannel
import android.app.NotificationManager

import android.content.Context

import android.os.Build

object NotificationHelper{

    private const val CHANNEL_ID = "alarm_channel"

    fun createNotificationChannel(context: Context){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarms",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }
}