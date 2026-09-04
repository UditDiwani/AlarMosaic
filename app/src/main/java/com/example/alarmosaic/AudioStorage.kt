package com.example.alarmosaic

import android.content.Context

import android.net.Uri

import java.io.File

class AudioStorage(
    private val context: Context
){
    fun copyAudio(uri: Uri): String {

        val soundsDirectory = File(
            context.filesDir,
            "sounds"
        )

        if(!soundsDirectory.exists()){
            soundsDirectory.mkdirs()
        }

        val fileName = "alarm_${System.currentTimeMillis()}.mp3"

        val destinationFile = File(
            soundsDirectory,
            fileName
        )

        context.contentResolver.openInputStream(uri).use { inputStream -> 

            requireNotNull(inputStream){
                "Unable to open selected audio file"
            }

            destinationFile.outputStream().use { outputStream ->

                inputStream.copyTo(outputStream)
            }
        }

        return destinationFile.absolutePath
    }
}