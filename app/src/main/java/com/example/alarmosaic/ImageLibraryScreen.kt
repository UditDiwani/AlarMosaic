package com.example.alarmosaic

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.background 
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding 
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp 
import androidx.compose.ui.window.Dialog

import coil.compose.AsyncImage

import java.io.File 

@Composable
fun ImageLibraryScreen(
    onBack: () -> Unit 
){
    BackHandler{
        onBack()
    }

    val context = LocalContext.current 

    val images = remember { ImageStorage(context).listImages() }

    var selectedImage by remember {
        mutableStateOf<File?>(null)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        SkyBackground()

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 24.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                TextButton(
                    onClick = onBack
                ){
                    Text(
                        text = "← Back",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "IMAGE LIBRARY",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            if(images.isEmpty()){
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ){
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = "🖼️",
                            fontSize = 48.sp 
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Your gallery is empty",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "Images saved as alarm backgrounds \n will appear here.",
                            fontSize = 14.sp 
                        )
                    }
                }
            } else {
                Text(
                    text = "${images.size} saved image${if (images.size == 1) "" else "s"}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ){
                    items(
                        items = images,
                        key = { it.absolutePath }
                    ){ image -> 
                        Card(
                            modifier = Modifier.fillMaxWidth().aspectRatio(0.85f).clickable {
                                selectedImage = image
                            },
                            shape = RoundedCornerShape(116.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp
                            )
                        ){
                            AsyncImage(
                                model = image,
                                contentDescription = "Saved alarm background",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop 
                            )
                        }
                    }
                }
            }
        }
    }

    selectedImage?.let { image -> 
        Dialog(
            onDismissRequest = {
                selectedImage = null 
            }
        ){
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ){
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    AsyncImage(
                        model = image,
                        contentDescription = "Full image preview",
                        modifier = Modifier.fillMaxWidth().height(420.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                        contentScale = ContentScale.Fit 
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = image.name,
                        fontSize = 13.sp 
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    TextButton(
                        onClick = {
                            selectedImage = null 
                        }
                    ){
                        Text("Close Preview")
                    }
                }
            }
        }
    }
}