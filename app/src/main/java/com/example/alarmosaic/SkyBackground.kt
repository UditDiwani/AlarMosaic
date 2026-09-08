package com.example.alarmosaic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun SKyBackground(
    modifier: Modifier = Modifier
){
    Canvas(
        modifier = modifier.fillMaxSize()
    ){
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFE8F8FF),
                    Color(0xFF9EDFF5),
                    Color(0xFFFFE3CC)
                )
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.55f),
                    Color.White.copy(alpha = 0.0f)
                )
            ),
            radius = 260f,
            center = Offset(
                size.width * 0.18f,
                size.height * 0.25f
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.45f),
                    Color.White.copy(alpha = 0.0f)
                )
            ),
            radius = 330f,
            center = Offset(
                size.width * 0.85f,
                size.height * 0.38f
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF4D6).copy(alpha = 0.45f),
                    Color(0xFFFFF4D6).copy(alpha = 0.0f)
                )
            ),
            radius = 300f,
            center = Offset(
                size.width * 0.55f,
                size.height * 0.75f
            )
        )
    }
}