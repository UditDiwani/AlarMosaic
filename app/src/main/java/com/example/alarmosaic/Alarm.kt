package com.example.alarmosaic

data class Alarm(
    val id: Long,
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true,
    val soundPath: String? = null
)