package com.hightechif.swipecleaner.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hightechif.swipecleaner.ui.component.SwipeableCard
import com.hightechif.swipecleaner.ui.viewmodel.SwipeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SwipeScreen(
    onNavigateToKept: () -> Unit,
    onNavigateToCompletion: () -> Unit,
    viewModel: SwipeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Auto-navigate to completion when pool is finished
    LaunchedEffect(uiState.isSessionFinished) {
        if (uiState.isSessionFinished && !uiState.isLoading && uiState.photoPool.isNotEmpty()) {
            onNavigateToCompletion()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E2C),
                        Color(0xFF0F0F14)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            uiState.isLoading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF6C63FF))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading gallery pool...",
                        color = Color.LightGray,
                        fontSize = 15.sp
                    )
                }
            }

            uiState.photoPool.isEmpty() -> {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Photos Found",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your phone gallery is empty or SwipeCleaner doesn't have permission to access photos.",
                        color = Color.LightGray,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Move to Trash Button
                        Button(
                            onClick = { viewModel.executeTrashRequest() },
                            enabled = uiState.deleteQueue.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE91E63),
                                contentColor = Color.White,
                                disabledContainerColor = Color(0xFFE91E63).copy(alpha = 0.3f),
                                disabledContentColor = Color.White.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = if (uiState.deleteQueue.isNotEmpty()) {
                                    "Move ${uiState.deleteQueue.size} Photos to Trash"
                                } else {
                                    "No Photos to Delete"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // See Kept Photos Button
                        Button(
                            onClick = onNavigateToKept,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6C63FF),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "See Kept Photos",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Reset and restart
                        OutlinedButton(
                            onClick = onNavigateToKept,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Review Remaining Photos",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }
            }

            else -> {
                SwipeContent(
                    photoPool = uiState.photoPool,
                    currentIndex = uiState.currentIndex,
                    onSwipeLeft = { viewModel.swipeLeft() },
                    onSwipeRight = { viewModel.swipeRight() }
                )
            }
        }
    }
}

@Composable
fun SwipeContent(
    photoPool: List<String>,
    currentIndex: Int,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val totalCount = photoPool.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App header with progress indicator
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "SwipeCleaner",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Progress: $currentIndex / $totalCount",
                color = Color.LightGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stacked Card layout
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Next card (rendered underneath for stack effect)
            if (currentIndex + 1 < totalCount) {
                SwipeableCard(
                    imageUri = photoPool[currentIndex + 1],
                    onSwipeLeft = {},
                    onSwipeRight = {},
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .graphicsLayer {
                            scaleX = 0.92f
                            scaleY = 0.92f
                            translationY = 30f
                            alpha = 0.6f
                        }
                )
            }

            // Top active card
            if (currentIndex < totalCount) {
                SwipeableCard(
                    imageUri = photoPool[currentIndex],
                    onSwipeLeft = onSwipeLeft,
                    onSwipeRight = onSwipeRight
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Basic helper UI controls at bottom
        Text(
            text = "◀ Swipe left to trash  |  Swipe right to keep ▶",
            color = Color.Gray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}
