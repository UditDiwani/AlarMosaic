package com.example.alarmosaic

import android.content.BroadcastReceiver
import android.content.Context 
import android.content.Intent
import android.util.Log 

import kotlinx.coroutines.CoroutineScope 
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sun.rmi.server.Dispatcher 

class BootReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent){
        if(intent.action != Intent.ACTION_BOOT_COMPLETED){
            return
        }

        Log.d(
            "AlarMosaicBoot",
            "Device boot completed. Restoring alarms..."
        )

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try{
                val storage = AlarmStorage(context)
                val scheduler = AlarmScheduler(context)

                val alarms = storage.loadAlarms()

                if(!scheduler.canScheduleExactAlarms()){
                    Log.w(
                        "AlarMosaicBoot",
                        "Exact alarm permission unavailable. Skipping restoration"
                    )

                    return@launch
                }

                alarms.filter { it.enabled }.forEach { alarm -> 
                    scheduler.schedule(alarm)

                    Log.d(
                        "AlarMosaicBoot",
                        "Restored alarm ID: ${alarm.id}"
                    )
                }

                Log.d(
                    "AlarMosaicBoot",
                    "Alarm restoration completed."
                )
            } catch (exception: Exception){
                Log.e(
                    "AlarMosaicBoot",
                    "Failed to restore alarms after reboot.",
                    exception
                )
            }finally{
                pendingResult.finish()
            }
        }
    }
}