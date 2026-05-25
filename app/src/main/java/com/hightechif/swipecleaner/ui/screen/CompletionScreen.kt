package com.hightechif.swipecleaner.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hightechif.swipecleaner.ui.viewmodel.SwipeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CompletionScreen(
    onNavigateToKept: () -> Unit,
    onNavigateToSwipe: () -> Unit,
    viewModel: SwipeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Confetti lottie composition
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.Url("https://assets10.lottiefiles.com/packages/lf20_l4xxtfd3.json")
    )

    // Activity launcher to execute MediaStore trash request dialog
    val trashLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { _ ->
        viewModel.onTrashRequestCompleted()
    }

    LaunchedEffect(Unit) {
        viewModel.trashEvent.collect { intentSender ->
            val request = IntentSenderRequest.Builder(intentSender).build()
            trashLauncher.launch(request)
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
        // Confetti effect covering the screen
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.Center
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Congratulations! 🎉",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "You've completed reviewing your gallery!",
                color = Color.LightGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF252538).copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Session Summary",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SummaryRow(label = "Total Photos Reviewed", value = uiState.photoPool.size.toString())
                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryRow(label = "Photos Kept", value = uiState.keptCount.toString(), color = Color(0xFF4CAF50))
                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryRow(label = "Photos Marked for Deletion", value = uiState.deleteQueue.size.toString(), color = Color(0xFFE91E63))
                }
            }

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
                    onClick = {
                        onNavigateToSwipe()
                    },
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
}

@Composable
fun SummaryRow(
    label: String,
    value: String,
    color: Color = Color.White
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = color,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
