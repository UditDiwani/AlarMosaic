package com.example.alarmosaic

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate

fun DrawScope.drawSkyBackground(
    canvasSize: Size
){
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE8F8FF),
                Color(0xFF9EDFF5),
                Color(0xFFFFE3CC)
            ),
            startY = 0f,
            endY = canvasSize.height
        ),
        size = canvasSize
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.55f),
                Color.Transparent
            )
        ),
        radius = 260f,
        center = Offset(
            canvasSize.width * 0.18f,
            canvasSize.height * 0.25f
        )
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.60f),
                Color.White.copy(alpha = 0.60f),
                Color.Transparent
            )
        ),
        radius = 330f,
        center = Offset(
            canvasSize.width * 0.85f,
            canvasSize.height * 0.38f
        )
    )
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFF4D6).copy(alpha = 0.55f),
                Color.Transparent
            )
        ),
        radius = 300f,
        center = Offset(
            canvasSize.width * 0.55f,
            canvasSize.height * 0.75f
        )
    )
    drawCircle(
    color = Color.White.copy(alpha = 0.65f),
    radius = 90f,
    center = Offset(
        canvasSize.width * 0.25f,
        canvasSize.height * 0.42f
    )
)

    drawCircle(
        color = Color.White.copy(alpha = 0.55f),
        radius = 65f,
        center = Offset(
            canvasSize.width * 0.33f,
            canvasSize.height * 0.44f
        )
    )

}

@Composable
fun SkyBackground(
    modifier: Modifier = Modifier
){
    Canvas(
        modifier = modifier.fillMaxSize()
    ){
        drawSkyBackground(size)
    }
}

@Composable
fun SkyPatch(
    fullSize: Size,
    position: Offset,
    modifier: Modifier = Modifier
){
    Canvas(
        modifier = modifier
    ){
        translate(
            left = -position.x,
            top = -position.y
        ){
            drawSkyBackground(fullSize)
        }
    }
}