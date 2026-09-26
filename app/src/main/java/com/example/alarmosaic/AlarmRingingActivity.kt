package com.example.alarmosaic

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmRingingActivity : ComponentActivity(){

    private val stopReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?){
            if(intent?.action == AlarmService.ACTION_ALARM_STOPPED){
                finishAndRemoveTask()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }else{
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        val alarmTime = SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(Date())

        ContextCompat.registerReceiver(
            this,
            stopReceiver,
            IntentFilter(AlarmService.ACTION_ALARM_STOPPED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        
        setContent{

            AlarmRingingScreen(
                alarmTime = alarmTime,
                onStop = {
                    stopService(
                        android.content.Intent(
                            this,
                            AlarmService::class.java
                        )
                    )
                    finishAndRemoveTask()
                }
            )
        }
    }

    override fun onDestroy(){
        unregisterReceiver(stopReceiver)
        super.onDestroy()
    }
}

@Composable
fun AlarmRingingScreen(
    alarmTime: String,
    onStop: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        SkyBackground()

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "⏰🚨",
                fontSize = 64.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = alarmTime,
                fontSize = 72.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "ALARM",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(48.dp)
            )

            Button(
                onClick = onStop
            ){
                Text(
                    text = "STOP ALARM",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}