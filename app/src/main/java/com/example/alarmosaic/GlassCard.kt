package com.example.alarmosaic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
){
    val shape = RoundedCornerShape(28.dp)

    Card(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.55f),
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.22f)
        )               
    ){
        Box(
            modifier = Modifier.background(Color.White.copy(alpha = 0.08f)),
            content = content
        )
    }
}