package com.example.alarmosaic

data class Alarm(
    val hour: Int,
    val minute: Int,
    val enabled: Boolean = true
)