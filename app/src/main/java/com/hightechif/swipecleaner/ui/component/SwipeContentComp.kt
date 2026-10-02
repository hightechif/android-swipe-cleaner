package com.hightechif.swipecleaner.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hightechif.swipecleaner.R
import com.hightechif.swipecleaner.ui.component.SwipeableCardComp

@Composable
fun SwipeContentComp(
    photoPool: List<String>,
    currentIndex: Int,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onCardClick: () -> Unit
) {
    val totalCount = photoPool.size

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (currentIndex + 1 < totalCount) {
                SwipeableCardComp(
                    imageUri = photoPool[currentIndex + 1],
                    onSwipeLeft = {}, onSwipeRight = {}, onCardClick = {},
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .graphicsLayer {
                            scaleX = 0.92f; scaleY = 0.92f; translationY = 30f; alpha = 0.6f
                        }
                )
            }
            if (currentIndex < totalCount) {
                SwipeableCardComp(
                    imageUri = photoPool[currentIndex],
                    onSwipeLeft = onSwipeLeft,
                    onSwipeRight = onSwipeRight,
                    onCardClick = onCardClick
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.swipe_hint),
            color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}
