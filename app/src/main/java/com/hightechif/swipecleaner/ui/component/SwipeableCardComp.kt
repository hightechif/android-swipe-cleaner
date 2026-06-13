package com.hightechif.swipecleaner.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import com.hightechif.swipecleaner.ui.theme.SwipeCleanerTheme
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

@Composable
fun SwipeableCardComp(
    imageUri: String,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val swipeThreshold = screenWidthPx * 0.35f

    val offsetX = remember(imageUri) { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Card(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(imageUri) {
                detectTapGestures(onTap = { onCardClick() })
            }
            .pointerInput(imageUri) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val targetOffsetX = offsetX.value
                        scope.launch {
                            when {
                                targetOffsetX > swipeThreshold -> {
                                    offsetX.animateTo(targetValue = screenWidthPx * 1.2f, animationSpec = spring())
                                    onSwipeRight()
                                }
                                targetOffsetX < -swipeThreshold -> {
                                    offsetX.animateTo(targetValue = -screenWidthPx * 1.2f, animationSpec = spring())
                                    onSwipeLeft()
                                }
                                else -> {
                                    offsetX.animateTo(0f, spring())
                                }
                            }
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                    }
                )
            }
            .offset { IntOffset(offsetX.value.toInt(), 0) }
            .graphicsLayer {
                rotationZ = (offsetX.value / screenWidthPx) * 15f
                val scale = min(1f, 1f - (abs(offsetX.value) / screenWidthPx) * 0.05f)
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF252538)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(model = imageUri),
                contentDescription = "Gallery photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp))
            )

            val dragPercentage = min(1f, abs(offsetX.value) / swipeThreshold)
            val overlayAlpha = dragPercentage * 0.4f

            if (offsetX.value > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF4CAF50).copy(alpha = overlayAlpha))
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(50))
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Keep Action",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.graphicsLayer {
                                val iconScale = 1f + dragPercentage * 0.5f
                                scaleX = iconScale
                                scaleY = iconScale
                            }
                        )
                    }
                }
            } else if (offsetX.value < 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFE91E63).copy(alpha = overlayAlpha))
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(50))
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Action",
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.graphicsLayer {
                                val iconScale = 1f + dragPercentage * 0.5f
                                scaleX = iconScale
                                scaleY = iconScale
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14)
@Composable
private fun SwipeableCardCompPreview() {
    SwipeCleanerTheme {
        SwipeableCardComp(
            imageUri = "",
            onSwipeLeft = {},
            onSwipeRight = {},
            onCardClick = {}
        )
    }
}
