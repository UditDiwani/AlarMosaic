package com.example.alarmosaic

import android.content.Context
import android.net.Uri 

import java.io.File 
import java.io.IOException
import java.util.UUID

class ImageStorage(
    private val context: Context
){
    private val backgroundsDirectory: File
        get() = File(context.filesDir,"backgrounds")
    
    fun copyImage(uri: Uri): String {
        val directory = backgroundsDirectory

        if(!directory.exists() && !directory.mkdirs()) {
            throw IOException("Could not create backgrounds directory")
        }

        val mimeType = context.contentResolver.getType(uri)

        val extension = when (mimeType){
            "image/" -> ".jpg"
            "image/png" -> ".png"
            "image/webp" -> ".webp"
            "image/gif" -> ".gif"
            "image/avif" -> ".avif"
            "image/heic" -> ".heic"
            "image/heif" -> ".heif"
            else -> ".img"
        }

        val destination = File(
            directory,
            "background_${UUID.randomUUID()}$extension"
        )

        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: throw IOException("Could not open the selected image")

            inputStream.use { input -> 
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            if(destination.length() == 0L){
                throw IOException("The selected image is empty")
            }
            return destination.absolutePath
        } catch (exception: Exception){
            destination.delete()
            throw exception
        }
    }

    fun listImages(): List<File> {
        return backgroundsDirectory.listFiles()?.filter { file ->
            file.isFile && 
            file.extension.lowercase() in setOf(
                "jpg",
                "jpeg",
                "webp",
                "gif",
                "avif",
                "heic",
                "heif",
                "img"
            )
        }
        ?.sortedByDescending { it.lastModified() }
        ?: emptyList()
    }
}