package com.example.alarmosaic

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import sun.java2d.pipe.RenderBuffer

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    rootSize: IntSize,
    content: @Composable BoxScope.() -> Unit
){
    val shape = RoundedCornerShape(28.dp)

    var cardPosition by remember {
        mutableStateOf(Offset.Zero)
    }

    Box(
        modifier = modifier
            .onGloballyPositioned{coordinates ->
                cardPosition = coordinates.positionInRoot()
            }
            .shadow(
                elevation = 14.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.10f),
                spotColor = Color.Black.copy(alpha = 0.18f)
            )
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.42f),
                        Color.White.copy(alpha = 0.20f),
                        Color(0xFFDDF7FF).copy(alpha = 0.32f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.90f),
                        Color.White.copy(alpha = 0.25f),
                        Color.White.copy(alpha = 0.45f)
                    )
                ),
                shape = shape
            ),             
    ){

        if(rootSize != IntSize.Zero){
            Box(
                modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                                renderEffect = RenderEffect.createBlurEffect(
                                    22f,
                                    22f,
                                    Shader.TileMode.CLAMP
                                )
                                .asComposeRenderEffect()
                            }
                        }
            ){
                SkyPatch(
                    fullSize = Size(
                        rootSize.width.toFloat(),
                        rootSize.height.toFloat()
                    ),
                    position = cardPosition,
                    modifier = Modifier.matchParentSize()
                )
            }
        }

        Box(
            modifier = Modifier
                    .matchParentSize()
                    .background(
                        Color.White.copy(alpha = 0.22f)
                    )
        )

        //directional shine
        Box(
            modifier = Modifier.matchParentSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.32f),
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.06f)
                            )
                        )
                    )
        )
        //bottom tint
        Box(
            modifier = Modifier.matchParentSize()
                        .background(
                            brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color(0xFFB8E8F5).copy(alpha = 0.10f)
                            )
                        )
            )
        )
        

        content()
        
    }
}
