package com.hightechif.swipecleaner.ui.feature.swipe

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.hightechif.swipecleaner.ui.theme.SwipeCleanerTheme
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.ui.component.SummaryRowComp
import com.hightechif.swipecleaner.ui.feature.kept.KeptAlbum
import com.hightechif.swipecleaner.ui.component.SwipeableCardComp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeScreen(
    viewModel: SwipeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keptPhotos by viewModel.keptPhotos.collectAsStateWithLifecycle()
    val keptAlbums by viewModel.keptAlbums.collectAsStateWithLifecycle()

    val (activeViewerUri, setActiveViewerUri) = remember { mutableStateOf<String?>(null) }
    val (photoToResetFromKept, setPhotoToResetFromKept) = remember { mutableStateOf<String?>(null) }
    val (photoToRestoreFromTrash, setPhotoToRestoreFromTrash) = remember {
        mutableStateOf<String?>(
            null
        )
    }
    val (showResetAllKeptDialog, setShowResetAllKeptDialog) = remember { mutableStateOf(false) }
    val (showAlbumSelectorDialog, setShowAlbumSelectorDialog) = remember { mutableStateOf(false) }

    val trashLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onTrashRequestCompleted()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.trashEvent.collect { intentSender ->
            val request = IntentSenderRequest.Builder(intentSender).build()
            trashLauncher.launch(request)
        }
    }

    if (showAlbumSelectorDialog) {
        AlertDialog(
            onDismissRequest = { setShowAlbumSelectorDialog(false) },
            containerColor = Color(0xFF252538),
            title = {
                Text(
                    text = "Select Folder",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectAlbum(null); setShowAlbumSelectorDialog(
                                    false
                                )
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(
                                        Color(0xFF6C63FF).copy(alpha = 0.2f),
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Collections,
                                    contentDescription = "All Photos",
                                    tint = Color(0xFF6C63FF)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = "All Photos",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                    items(state.albums) { album ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectAlbum(album); setShowAlbumSelectorDialog(
                                    false
                                )
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(model = album.coverPhotoUri),
                                contentDescription = album.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = album.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "${album.photoCount} photos",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { setShowAlbumSelectorDialog(false) }) {
                    Text(
                        "Close",
                        color = Color.Gray
                    )
                }
            }
        )
    }

    if (state.showMilestoneDialog) {
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
                    text = "You have swiped ${state.sessionSwipeCount} photos in this session. Would you like to review them now or empty your trash?",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                    onClick = { viewModel.dismissMilestoneDialog(); viewModel.setActiveTab(SwipeTab.TRASH) }
                ) { Text("Review Trash (${state.deleteQueue.size})", color = Color.White) }
            },
            dismissButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.deleteQueue.isNotEmpty()) {
                        TextButton(onClick = { viewModel.dismissMilestoneDialog(); viewModel.executeTrashRequest() }) {
                            Text("Empty Trash", color = Color(0xFFE91E63))
                        }
                    }
                    TextButton(onClick = { viewModel.dismissMilestoneDialog() }) {
                        Text(
                            "Keep Swiping",
                            color = Color.Gray
                        )
                    }
                }
            }
        )
    }

    if (photoToResetFromKept != null) {
        AlertDialog(
            onDismissRequest = { setPhotoToResetFromKept(null) },
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
                    color = Color.LightGray, fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    onClick = {
                        viewModel.restoreFromKept(photoToResetFromKept); setPhotoToResetFromKept(
                        null
                    )
                    }
                ) { Text("Reset", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { setPhotoToResetFromKept(null) }) {
                    Text(
                        "Cancel",
                        color = Color.Gray
                    )
                }
            }
        )
    }

    if (photoToRestoreFromTrash != null) {
        AlertDialog(
            onDismissRequest = { setPhotoToRestoreFromTrash(null) },
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
                    color = Color.LightGray, fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                    onClick = {
                        viewModel.restoreFromTrash(photoToRestoreFromTrash); setPhotoToRestoreFromTrash(
                        null
                    )
                    }
                ) { Text("Restore", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { setPhotoToRestoreFromTrash(null) }) {
                    Text(
                        "Cancel",
                        color = Color.Gray
                    )
                }
            }
        )
    }

    if (showResetAllKeptDialog) {
        AlertDialog(
            onDismissRequest = { setShowResetAllKeptDialog(false) },
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
                    color = Color.LightGray, fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                    onClick = { setShowResetAllKeptDialog(false); viewModel.resetAllKeptPhotos() }
                ) { Text("Restore All", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { setShowResetAllKeptDialog(false) }) {
                    Text(
                        "Cancel",
                        color = Color.Gray
                    )
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1E1E2C), tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = state.activeTab == SwipeTab.SWIPE,
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
                    selected = state.activeTab == SwipeTab.KEPT,
                    onClick = { viewModel.setActiveTab(SwipeTab.KEPT) },
                    icon = {
                        BadgedBox(badge = {
                            if (keptPhotos.isNotEmpty()) {
                                Badge(containerColor = Color(0xFF4CAF50)) {
                                    Text(text = keptPhotos.size.toString(), color = Color.White)
                                }
                            }
                        }) { Icon(Icons.Default.Favorite, contentDescription = "Kept") }
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
                    selected = state.activeTab == SwipeTab.TRASH,
                    onClick = { viewModel.setActiveTab(SwipeTab.TRASH) },
                    icon = {
                        BadgedBox(badge = {
                            if (state.deleteQueue.isNotEmpty()) {
                                Badge(containerColor = Color(0xFFE91E63)) {
                                    Text(
                                        text = state.deleteQueue.size.toString(),
                                        color = Color.White
                                    )
                                }
                            }
                        }) { Icon(Icons.Default.Delete, contentDescription = "Trash") }
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
            when (state.activeTab) {
                SwipeTab.SWIPE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
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
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF252538))
                                .clickable { setShowAlbumSelectorDialog(true) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.selectedAlbum?.name ?: "All Photos",
                                color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Folder",
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!state.isLoading && state.photoPool.isNotEmpty() && !state.isSessionFinished) {
                            Text(
                                text = "Progress: ${state.currentIndex} / ${state.photoPool.size}",
                                color = Color.LightGray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                state.isLoading -> {
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

                                state.photoPool.isEmpty() -> {
                                    EmptySwipeViewComp(
                                        deleteQueueSize = state.deleteQueue.size,
                                        onExecuteTrash = { viewModel.executeTrashRequest() },
                                        onSeeKept = { viewModel.setActiveTab(SwipeTab.KEPT) }
                                    )
                                }

                                state.isSessionFinished -> {
                                    SessionCompletedViewComp(
                                        totalPoolSize = state.photoPool.size,
                                        keptCount = state.keptCount,
                                        deleteQueueSize = state.deleteQueue.size,
                                        onExecuteTrash = { viewModel.executeTrashRequest() },
                                        onSeeKept = { viewModel.setActiveTab(SwipeTab.KEPT) }
                                    )
                                }

                                else -> {
                                    SwipeContentComp(
                                        photoPool = state.photoPool,
                                        currentIndex = state.currentIndex,
                                        onSwipeLeft = { viewModel.swipeLeft() },
                                        onSwipeRight = { viewModel.swipeRight() },
                                        onCardClick = { setActiveViewerUri(state.photoPool[state.currentIndex]) }
                                    )
                                }
                            }
                        }
                    }
                }

                SwipeTab.KEPT -> {
                    KeptTabContentComp(
                        keptPhotos = keptPhotos,
                        keptAlbums = keptAlbums,
                        onPhotoClick = { setActiveViewerUri(it) },
                        onPhotoLongClick = { setPhotoToResetFromKept(it) },
                        onResetAll = { setShowResetAllKeptDialog(true) }
                    )
                }

                SwipeTab.TRASH -> {
                    TrashTabContentComp(
                        deleteQueue = state.deleteQueue,
                        onRestorePhoto = { setPhotoToRestoreFromTrash(it) },
                        onExecuteTrash = { viewModel.executeTrashRequest() }
                    )
                }
            }

            activeViewerUri?.let { uri ->
                FullscreenImageViewerComp(imageUri = uri, onDismiss = { setActiveViewerUri(null) })
            }
        }
    }
}

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
            text = "◀ Swipe left to trash | Tap to zoom |  Swipe right to keep  ▶",
            color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun EmptySwipeViewComp(
    deleteQueueSize: Int,
    onExecuteTrash: () -> Unit,
    onSeeKept: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
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
                    containerColor = Color(0xFFE91E63), contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE91E63).copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = if (deleteQueueSize > 0) "Move $deleteQueueSize Photos to Trash" else "No Photos to Delete",
                    fontSize = 16.sp, fontWeight = FontWeight.Bold
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
fun SessionCompletedViewComp(
    totalPoolSize: Int,
    keptCount: Int,
    deleteQueueSize: Int,
    onExecuteTrash: () -> Unit,
    onSeeKept: () -> Unit
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(com.hightechif.swipecleaner.R.raw.completion_animation)
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.fillMaxSize(),
            alignment = Alignment.Center
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
                    SummaryRowComp(
                        label = "Total Photos Reviewed",
                        value = totalPoolSize.toString()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryRowComp(
                        label = "Photos Kept",
                        value = keptCount.toString(),
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SummaryRowComp(
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
                        containerColor = Color(0xFFE91E63), contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE91E63).copy(alpha = 0.3f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = if (deleteQueueSize > 0) "Move $deleteQueueSize Photos to Trash" else "No Photos to Delete",
                        fontSize = 16.sp, fontWeight = FontWeight.Bold
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
}

enum class KeptViewMode { ALL_PHOTOS, ALBUMS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeptTabContentComp(
    keptPhotos: List<KeptPhoto>,
    keptAlbums: List<KeptAlbum>,
    onPhotoClick: (String) -> Unit,
    onPhotoLongClick: (String) -> Unit,
    onResetAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var keptViewMode by remember { mutableStateOf(KeptViewMode.ALL_PHOTOS) }
    var selectedAlbumId by remember { mutableStateOf<String?>(null) }
    var showViewModeDropdown by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(28.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Kept Photos",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF252538))
                            .clickable { showViewModeDropdown = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (keptViewMode == KeptViewMode.ALL_PHOTOS) "All Kept Photos" else "Kept Photos Albums",
                            color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select View Mode",
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showViewModeDropdown,
                        onDismissRequest = { showViewModeDropdown = false },
                        modifier = Modifier.background(Color(0xFF252538))
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "All Kept Photos",
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            },
                            onClick = {
                                keptViewMode = KeptViewMode.ALL_PHOTOS; selectedAlbumId =
                                null; showViewModeDropdown = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Kept Photos Albums",
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            },
                            onClick = {
                                keptViewMode = KeptViewMode.ALBUMS; selectedAlbumId =
                                null; showViewModeDropdown = false
                            }
                        )
                    }
                }
            }
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
            Box(modifier = Modifier
                .fillMaxSize()
                .weight(1f), contentAlignment = Alignment.Center) {
                Text(text = "No kept photos yet.", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            when (keptViewMode) {
                KeptViewMode.ALL_PHOTOS -> {
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
                            KeptPhotoGridItemComp(
                                uri = photo.uri,
                                onClick = { onPhotoClick(photo.uri) },
                                onLongClick = { onPhotoLongClick(photo.uri) })
                        }
                    }
                }

                KeptViewMode.ALBUMS -> {
                    if (selectedAlbumId == null) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(keptAlbums, key = { it.id }) { album ->
                                KeptAlbumCardComp(
                                    album = album,
                                    onClick = { selectedAlbumId = album.id })
                            }
                        }
                    } else {
                        val activeAlbum = keptAlbums.find { it.id == selectedAlbumId }
                        if (activeAlbum != null) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedAlbumId = null }
                                        .padding(horizontal = 24.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color(0xFF6C63FF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Back to Albums (${activeAlbum.name})",
                                        color = Color(0xFF6C63FF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(activeAlbum.photos, key = { it.uri }) { photo ->
                                        KeptPhotoGridItemComp(
                                            uri = photo.uri,
                                            onClick = { onPhotoClick(photo.uri) },
                                            onLongClick = { onPhotoLongClick(photo.uri) })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeptPhotoGridItemComp(
    uri: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = uri),
            contentDescription = "Kept Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun KeptAlbumCardComp(
    album: KeptAlbum,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(model = album.coverPhotoUri),
                contentDescription = album.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(4.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = album.name,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(text = "(${album.photoCount})", color = Color.LightGray, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun TrashTabContentComp(
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
            Box(modifier = Modifier
                .fillMaxSize()
                .weight(1f), contentAlignment = Alignment.Center) {
                Text(text = "Trash is empty.", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            Column(modifier = Modifier
                .fillMaxSize()
                .weight(1f)) {
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

@Composable
fun FullscreenImageViewerComp(
    imageUri: String,
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidth = with(LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight = with(LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
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
                            scale = 1f; offset = Offset.Zero
                        } else {
                            scale = 2.5f; offset = Offset.Zero
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

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14)
@Composable
private fun EmptySwipeViewCompPreview() {
    SwipeCleanerTheme {
        EmptySwipeViewComp(deleteQueueSize = 5, onExecuteTrash = {}, onSeeKept = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14)
@Composable
private fun TrashTabContentCompPreview() {
    SwipeCleanerTheme {
        TrashTabContentComp(
            deleteQueue = listOf("uri1", "uri2", "uri3"),
            onRestorePhoto = {},
            onExecuteTrash = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F0F14)
@Composable
private fun KeptPhotoGridItemCompPreview() {
    SwipeCleanerTheme {
        KeptPhotoGridItemComp(uri = "", onClick = {}, onLongClick = {})
    }
}
