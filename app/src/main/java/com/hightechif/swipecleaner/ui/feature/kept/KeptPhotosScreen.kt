package com.hightechif.swipecleaner.ui.feature.kept

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import org.koin.androidx.compose.koinViewModel

enum class KeptPhotosViewMode { ALL_PHOTOS, ALBUMS }

data class ResolvedKeptPhoto(
    val uri: String,
    val keptAt: Long,
    val bucketId: String,
    val bucketName: String
)

data class KeptAlbum(
    val id: String,
    val name: String,
    val coverPhotoUri: String,
    val photoCount: Int,
    val photos: List<ResolvedKeptPhoto>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeptPhotosScreen(
    onNavigateBack: () -> Unit,
    onResetComplete: () -> Unit,
    viewModel: KeptPhotosViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keptPhotos = state.keptPhotos
    val mediaImages = state.mediaImages

    var keptViewMode by remember { mutableStateOf(KeptPhotosViewMode.ALL_PHOTOS) }
    var selectedAlbumId by remember { mutableStateOf<String?>(null) }
    var showViewModeDropdown by remember { mutableStateOf(false) }
    var showResetAllDialog by remember { mutableStateOf(false) }
    var photoToRestore by remember { mutableStateOf<String?>(null) }
    var activeViewerUri by remember { mutableStateOf<String?>(null) }

    val mediaImagesMap = remember(mediaImages) { mediaImages.associateBy { it.uri } }

    val resolvedKeptPhotos = remember(keptPhotos, mediaImagesMap) {
        keptPhotos.map { entity ->
            val mediaImage = mediaImagesMap[entity.uri]
            ResolvedKeptPhoto(
                uri = entity.uri,
                keptAt = entity.keptAt,
                bucketId = mediaImage?.bucketId ?: "unknown",
                bucketName = mediaImage?.bucketName ?: "Others"
            )
        }
    }

    val keptAlbums = remember(resolvedKeptPhotos) {
        resolvedKeptPhotos.groupBy { it.bucketId }.map { (bucketId, photos) ->
            KeptAlbum(
                id = bucketId,
                name = photos.first().bucketName,
                coverPhotoUri = photos.first().uri,
                photoCount = photos.size,
                photos = photos
            )
        }.sortedBy { it.name }
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
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Kept Photos",
                        color = Color.White,
                        fontSize = 20.sp,
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
                                text = if (keptViewMode == KeptPhotosViewMode.ALL_PHOTOS) "All Kept Photos" else "Kept Photos Albums",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
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
                                    keptViewMode = KeptPhotosViewMode.ALL_PHOTOS
                                    selectedAlbumId = null
                                    showViewModeDropdown = false
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
                                    keptViewMode = KeptPhotosViewMode.ALBUMS
                                    selectedAlbumId = null
                                    showViewModeDropdown = false
                                }
                            )
                        }
                    }
                }

                if (keptPhotos.isNotEmpty()) {
                    IconButton(onClick = { showResetAllDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Reset Progress",
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "No Kept Photos Yet",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Photos you swipe right will show up here.",
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                when (keptViewMode) {
                    KeptPhotosViewMode.ALL_PHOTOS -> {
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
                                KeptPhotoCardComp(
                                    uri = photo.uri,
                                    onClick = { activeViewerUri = photo.uri },
                                    onLongClick = { photoToRestore = photo.uri }
                                )
                            }
                        }
                    }

                    KeptPhotosViewMode.ALBUMS -> {
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
                                    AlbumCardComp(
                                        album = album,
                                        onClick = { selectedAlbumId = album.id })
                                }
                            }
                        } else {
                            val activeAlbum = keptAlbums.find { it.id == selectedAlbumId }
                            if (activeAlbum != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f)
                                ) {
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
                                            KeptPhotoCardComp(
                                                uri = photo.uri,
                                                onClick = { activeViewerUri = photo.uri },
                                                onLongClick = { photoToRestore = photo.uri }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        activeViewerUri?.let { uri ->
            FullscreenViewerComp(imageUri = uri, onDismiss = { activeViewerUri = null })
        }

        if (showResetAllDialog) {
            AlertDialog(
                onDismissRequest = { showResetAllDialog = false },
                containerColor = Color(0xFF252538),
                title = {
                    Text(
                        text = "Reset Progress?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "This will clear all your kept photos. All photos will appear in your swipe session again from the beginning.",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                        onClick = {
                            showResetAllDialog = false
                            viewModel.resetProgress { onResetComplete() }
                        }
                    ) { Text("Reset", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { showResetAllDialog = false }) {
                        Text(
                            "Cancel",
                            color = Color.Gray
                        )
                    }
                }
            )
        }

        if (photoToRestore != null) {
            AlertDialog(
                onDismissRequest = { photoToRestore = null },
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
                        text = "Do you want to restore this photo? It will be removed from Kept and put back on your swipe deck.",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                        onClick = {
                            val uri = photoToRestore
                            if (uri != null) viewModel.restoreKeptPhoto(uri)
                            photoToRestore = null
                        }
                    ) { Text("Restore", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { photoToRestore = null }) {
                        Text(
                            "Cancel",
                            color = Color.Gray
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeptPhotoCardComp(
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
fun AlbumCardComp(
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
fun FullscreenViewerComp(
    imageUri: String,
    onDismiss: () -> Unit
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth =
        with(androidx.compose.ui.platform.LocalDensity.current) { configuration.screenWidthDp.dp.toPx() }
    val screenHeight =
        with(androidx.compose.ui.platform.LocalDensity.current) { configuration.screenHeightDp.dp.toPx() }

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
            contentDescription = "Fullscreen photo",
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
