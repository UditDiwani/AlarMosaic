package com.example.alarmosaic

import android.content.Context

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.first
import kotlin.collections.listOf

private val Context.alarmDataStore by preferencesDataStore(
    name = "alarms"
)

class AlarmStorage(
    private val context: Context
){
    private val alarmsKey = stringPreferencesKey("alarms")

    suspend fun saveAlarms( alarms: List<Alarm>){
        val data = alarms.joinToString("|") { alarm -> 
            "${alarm.hour},${alarm.minute},${alarm.enabled}"
        }
        
        context.alarmDataStore.edit { preferences -> 
            preferences[alarmsKey] = data
        }
    }

    suspend fun loadAlarms(): List<Alarm>{
        val data = context.alarmDataStore.data.first()[alarmsKey] ?: return emptyList()
        
        if (data.isEmpty()) return emptyList()

        return data.split("|").map { alarmData -> 
            val parts = alarmData.split(",")

            Alarm(
                hour = parts[0].toInt(),
                minute = parts[1].toInt(),
                enabled = parts[2].toBoolean()
            )
        } 
    }
}