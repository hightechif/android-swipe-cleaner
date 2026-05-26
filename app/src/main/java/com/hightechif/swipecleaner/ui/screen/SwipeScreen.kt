package com.hightechif.swipecleaner.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hightechif.swipecleaner.data.db.KeptPhotoEntity
import com.hightechif.swipecleaner.ui.component.SwipeableCard
import com.hightechif.swipecleaner.ui.viewmodel.SwipeTab
import com.hightechif.swipecleaner.ui.viewmodel.SwipeViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeScreen(
    onNavigateToKept: () -> Unit,
    onNavigateToCompletion: () -> Unit,
    viewModel: SwipeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keptPhotos by viewModel.keptPhotos.collectAsState()

    var activeViewerUri by remember { mutableStateOf<String?>(null) }
    var photoToResetFromKept by remember { mutableStateOf<String?>(null) }
    var photoToRestoreFromTrash by remember { mutableStateOf<String?>(null) }
    var showResetAllKeptDialog by remember { mutableStateOf(false) }

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

    // Milestone dialog alert every 50 swipes
    if (uiState.showMilestoneDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMilestoneDialog() },
            containerColor = Color(0xFF252538),
            title = {
                Text(
                    text = "Milestone Reached! 🎯",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "You have swiped ${uiState.sessionSwipeCount} photos in this session. Would you like to review them now or empty your trash?",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                    onClick = {
                        viewModel.dismissMilestoneDialog()
                        viewModel.setActiveTab(SwipeTab.TRASH)
                    }
                ) {
                    Text("Review Trash (${uiState.deleteQueue.size})", color = Color.White)
                }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.deleteQueue.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.dismissMilestoneDialog()
                                viewModel.executeTrashRequest()
                            }
                        ) {
                            Text("Empty Trash", color = Color(0xFFE91E63))
                        }
                    }
                    TextButton(onClick = { viewModel.dismissMilestoneDialog() }) {
                        Text("Keep Swiping", color = Color.Gray)
                    }
                }
            }
        )
    }

    // Undo dialog for kept item
    if (photoToResetFromKept != null) {
        AlertDialog(
            onDismissRequest = { photoToResetFromKept = null },
            containerColor = Color(0xFF252538),
            title = {
                Text(
                    text = "Reset Photo?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Do you want to reset this photo? It will be removed from Kept and put back on your swipe deck.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    onClick = {
                        photoToResetFromKept?.let { viewModel.restoreFromKept(it) }
                        photoToResetFromKept = null
                    }
                ) {
                    Text("Reset", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToResetFromKept = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Undo dialog for trash item
    if (photoToRestoreFromTrash != null) {
        AlertDialog(
            onDismissRequest = { photoToRestoreFromTrash = null },
            containerColor = Color(0xFF252538),
            title = {
                Text(
                    text = "Restore Photo?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Do you want to restore this photo back to your active swipe deck?",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                    onClick = {
                        photoToRestoreFromTrash?.let { viewModel.restoreFromTrash(it) }
                        photoToRestoreFromTrash = null
                    }
                ) {
                    Text("Restore", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToRestoreFromTrash = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Reset all kept photos dialog
    if (showResetAllKeptDialog) {
        AlertDialog(
            onDismissRequest = { showResetAllKeptDialog = false },
            containerColor = Color(0xFF252538),
            title = {
                Text(
                    text = "Restore All Kept Photos?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will clear all your kept photos. They will reappear in your swipe deck from the beginning.",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    onClick = {
                        showResetAllKeptDialog = false
                        viewModel.resetAllKeptPhotos()
                    }
                ) {
                    Text("Restore All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetAllKeptDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF1E1E2C),
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = uiState.activeTab == SwipeTab.SWIPE,
                    onClick = { viewModel.setActiveTab(SwipeTab.SWIPE) },
                    icon = { Icon(Icons.Default.Collections, contentDescription = "Swipe") },
                    label = { Text("Swipe") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF6C63FF),
                        selectedTextColor = Color(0xFF6C63FF),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF252538)
                    )
                )

                NavigationBarItem(
                    selected = uiState.activeTab == SwipeTab.KEPT,
                    onClick = { viewModel.setActiveTab(SwipeTab.KEPT) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (keptPhotos.isNotEmpty()) {
                                    Badge(containerColor = Color(0xFF4CAF50)) {
                                        Text(text = keptPhotos.size.toString(), color = Color.White)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = "Kept")
                        }
                    },
                    label = { Text("Kept") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF4CAF50),
                        selectedTextColor = Color(0xFF4CAF50),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF252538)
                    )
                )

                NavigationBarItem(
                    selected = uiState.activeTab == SwipeTab.TRASH,
                    onClick = { viewModel.setActiveTab(SwipeTab.TRASH) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.deleteQueue.isNotEmpty()) {
                                    Badge(containerColor = Color(0xFFE91E63)) {
                                        Text(
                                            text = uiState.deleteQueue.size.toString(),
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Trash")
                        }
                    },
                    label = { Text("Trash") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE91E63),
                        selectedTextColor = Color(0xFFE91E63),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color(0xFF252538)
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E1E2C),
                            Color(0xFF0F0F14)
                        )
                    )
                )
        ) {
            when (uiState.activeTab) {
                SwipeTab.SWIPE -> {
                    when {
                        uiState.isLoading -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
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
                            EmptySwipeView(
                                deleteQueueSize = uiState.deleteQueue.size,
                                onExecuteTrash = { viewModel.executeTrashRequest() },
                                onSeeKept = { viewModel.setActiveTab(SwipeTab.KEPT) }
                            )
                        }

                        uiState.isSessionFinished -> {
                            SessionCompletedView(
                                totalPoolSize = uiState.photoPool.size,
                                keptCount = uiState.keptCount,
                                deleteQueueSize = uiState.deleteQueue.size,
                                onExecuteTrash = { viewModel.executeTrashRequest() },
                                onSeeKept = { viewModel.setActiveTab(SwipeTab.KEPT) },
                                onResetRemaining = { viewModel.loadPhotoPool(keepDeleteQueue = true) }
                            )
                        }

                        else -> {
                            SwipeContent(
                                photoPool = uiState.photoPool,
                                currentIndex = uiState.currentIndex,
                                onSwipeLeft = { viewModel.swipeLeft() },
                                onSwipeRight = { viewModel.swipeRight() },
                                onCardClick = {
                                    activeViewerUri = uiState.photoPool[uiState.currentIndex]
                                }
                            )
                        }
                    }
                }

                SwipeTab.KEPT -> {
                    KeptTabContent(
                        keptPhotos = keptPhotos,
                        onResetPhoto = { photoToResetFromKept = it },
                        onResetAll = { showResetAllKeptDialog = true }
                    )
                }

                SwipeTab.TRASH -> {
                    TrashTabContent(
                        deleteQueue = uiState.deleteQueue,
                        onRestorePhoto = { photoToRestoreFromTrash = it },
                        onExecuteTrash = { viewModel.executeTrashRequest() }
                    )
                }
            }

            // Zoomable/pannable full screen image viewer overlay
            activeViewerUri?.let { uri ->
                FullscreenImageViewer(
                    imageUri = uri,
                    onDismiss = { activeViewerUri = null }
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
    onSwipeRight: () -> Unit,
    onCardClick: () -> Unit
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
                    onCardClick = {},
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
                    onSwipeRight = onSwipeRight,
                    onCardClick = onCardClick
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Basic helper UI controls at bottom
        Text(
            text = "◀ Swipe left to trash | Tap to zoom |  Swipe right to keep  ▶",
            color = Color.Gray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun FullscreenImageViewer(
    imageUri: String,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)

        // Clamp panning offsets to restrict image from panning off screen
        val maxOffsetX = (scale - 1f) * screenWidth / 2
        val maxOffsetY = (scale - 1f) * screenHeight / 2
        offset = Offset(
            x = (offset.x + panChange.x).coerceIn(-maxOffsetX, maxOffsetX),
            y = (offset.y + panChange.y).coerceIn(-maxOffsetY, maxOffsetY)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .pointerInput(imageUri) {
                detectTapGestures(
                    onTap = { onDismiss() },
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 2.5f
                            offset = Offset.Zero
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = imageUri),
            contentDescription = "Fullscreen detailed photo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                )
                .transformable(state = transformState)
        )

        // Close Floating Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 24.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(24.dp))
                    .clickable { onDismiss() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "✕ Close",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun EmptySwipeView(
    deleteQueueSize: Int,
    onExecuteTrash: () -> Unit,
    onSeeKept: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onExecuteTrash,
                enabled = deleteQueueSize > 0,
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
                    text = if (deleteQueueSize > 0) "Move $deleteQueueSize Photos to Trash" else "No Photos to Delete",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = onSeeKept,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF),
                    contentColor = Color.White
                )
            ) {
                Text("See Kept Photos", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SessionCompletedView(
    totalPoolSize: Int,
    keptCount: Int,
    deleteQueueSize: Int,
    onExecuteTrash: () -> Unit,
    onSeeKept: () -> Unit,
    onResetRemaining: () -> Unit
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.Url("https://assets10.lottiefiles.com/packages/lf20_l4xxtfd3.json")
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF252538).copy(alpha = 0.9f)),
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
                    SummaryRow(label = "Total Photos Reviewed", value = totalPoolSize.toString())
                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryRow(
                        label = "Photos Kept",
                        value = keptCount.toString(),
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryRow(
                        label = "Photos Marked for Deletion",
                        value = deleteQueueSize.toString(),
                        color = Color(0xFFE91E63)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onExecuteTrash,
                    enabled = deleteQueueSize > 0,
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
                        text = if (deleteQueueSize > 0) "Move $deleteQueueSize Photos to Trash" else "No Photos to Delete",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onSeeKept,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6C63FF),
                        contentColor = Color.White
                    )
                ) {
                    Text("See Kept Photos", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onResetRemaining,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Review Remaining Photos", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun KeptTabContent(
    keptPhotos: List<KeptPhotoEntity>,
    onResetPhoto: (String) -> Unit,
    onResetAll: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Kept Photos",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            if (keptPhotos.isNotEmpty()) {
                IconButton(onClick = onResetAll) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Restore All Kept",
                        tint = Color(0xFFE91E63)
                    )
                }
            }
        }

        if (keptPhotos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No kept photos yet.",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(keptPhotos, key = { it.uri }) { photo ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable { onResetPhoto(photo.uri) }
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = photo.uri),
                            contentDescription = "Kept Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrashTabContent(
    deleteQueue: List<String>,
    onRestorePhoto: (String) -> Unit,
    onExecuteTrash: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Trash Queue",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        if (deleteQueue.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Trash is empty.",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(deleteQueue, key = { it }) { uri ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable { onRestorePhoto(uri) }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = uri),
                                contentDescription = "Trash Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }

                Button(
                    onClick = onExecuteTrash,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Move ${deleteQueue.size} Photos to Trash",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
