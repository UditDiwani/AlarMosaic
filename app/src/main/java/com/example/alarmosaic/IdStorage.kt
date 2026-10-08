package com.example.alarmosaic

import android.content.Context

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.idDataStore by preferencesDataStore(name = "ids")

class IdStorage(private val context: Context){
    private val nextAlarmIdKey = longPreferencesKey("next_alarm_id")

    private val nextAudioArtifactIdKey = longPreferencesKey("next_audio_artifact_id")

    suspend fun nextAlarmId(): Long{
        var generatedId = 0L

        context.idDataStore.edit { preferences -> 
            val currentId = preferences[nextAlarmIdKey] ?: 1L

            generatedId = currentId

            preferences[nextAlarmIdKey] = currentId + 1
        }

        return generatedId
    }

    suspend fun nextAudioArtifactId(): Long {
        var generatedId = 0L

        context.idDataStore.edit { preferences -> 
            val currentId = preferences[nextAudioArtifactIdKey] ?: 1L

            generatedId = currentId

            preferences[nextAudioArtifactIdKey] = currentId + 1
        }

        return generatedId
    }

    suspend fun resetAlarmId() {
        context.idDataStore.edit {
            it[nextAlarmIdKey] = 1L
        }
    }

    suspend fun resetAudioArtifactId() {
        context.idDataStore.edit {
            it[nextAudioArtifactIdKey] = 1L
        }
    }
}