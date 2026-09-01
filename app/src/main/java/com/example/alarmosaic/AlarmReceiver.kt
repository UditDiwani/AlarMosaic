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
        val time = SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        ).format(Date())
        Log.d(
            "AlarMosaicAlarm",
            "ALARM FIRED!! at $time"
        )
    }
}