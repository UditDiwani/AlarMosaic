package com.example.alarmosaic

import android.content.Context

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

import kotlinx.coroutines.flow.first

private val Context.audioArtifactDataStore by preferencesDataStore(
    name = "audio_artifact"
)

class AudioArtifactStorage(
    private val context: Context
){
    private val artifactsKey = stringPreferencesKey("audio_artifacts")

    suspend fun save(artifact: AudioArtifact){
        val existingArtifacts = loadAll()

        val updatedArtifacts = existingArtifacts.filter { it.id != artifact.id } + artifact

        val data = updatedArtifacts.joinToString("|") { item -> 
            "${item.id},${item.name},${item.filePath}"
        }

        context.audioArtifactDataStore.edit { preferences ->
            preferences[artifactsKey] = data
        }
    }

    suspend fun loadAll(): List<AudioArtifact>{
        val data = context.audioArtifactDataStore.data.first()[artifactsKey] ?: return emptyList()

        if (data.isEmpty()){
            return emptyList()
        }

        return data.split("|").map { artifactData -> 
            val parts = artifactData.split(",")

            AudioArtifact(
                id = parts[0].toLong(),
                name = parts[1],
                filePath = parts[2]
            )
        }
    }

    suspend fun delete(id: Long){
        val existingArtifacts = loadAll()

        val updatedArtifacts = existingArtifacts.filter { it.id != id }

        val data = updatedArtifacts.joinToString("|") { item ->
            "${item.id},${item.name},${item.filePath}"
        }

        context.audioArtifactDataStore.edit { preferences -> 
            preferences[artifactsKey] = data
        }

        if(updatedArtifacts.isEmpty()){
            val idStorage = IdStorage(context)
            idStorage.resetAudioArtifactId()
        }
    }
}