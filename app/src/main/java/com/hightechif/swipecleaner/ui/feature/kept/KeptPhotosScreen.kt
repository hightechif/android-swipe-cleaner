package com.hightechif.swipecleaner.ui.feature.kept

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hightechif.swipecleaner.R
import com.hightechif.swipecleaner.ui.component.ConfirmDialogComp
import com.hightechif.swipecleaner.ui.component.FullscreenImageViewerComp
import com.hightechif.swipecleaner.ui.component.KeptPhotosGridComp
import com.hightechif.swipecleaner.ui.component.KeptViewMode
import com.hightechif.swipecleaner.ui.component.KeptViewModeSelectorComp
import org.koin.androidx.compose.koinViewModel

@Composable
fun KeptPhotosScreen(
    onNavigateBack: () -> Unit,
    onResetComplete: () -> Unit,
    viewModel: KeptPhotosViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val keptPhotos = state.keptPhotos
    val keptAlbums by viewModel.keptAlbums.collectAsStateWithLifecycle()

    var viewMode by remember { mutableStateOf(KeptViewMode.ALL_PHOTOS) }
    var selectedAlbumId by remember { mutableStateOf<String?>(null) }
    var showResetAllDialog by remember { mutableStateOf(false) }
    var photoToRestore by remember { mutableStateOf<String?>(null) }
    var activeViewerUri by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = activeViewerUri != null) {
        activeViewerUri = null
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
                        contentDescription = stringResource(R.string.action_back),
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
                        text = stringResource(R.string.kept_title),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    KeptViewModeSelectorComp(
                        viewMode = viewMode,
                        onViewModeSelected = {
                            viewMode = it
                            selectedAlbumId = null
                        }
                    )
                }

                if (keptPhotos.isNotEmpty()) {
                    IconButton(onClick = { showResetAllDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = stringResource(R.string.kept_cd_reset_progress),
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
                            text = stringResource(R.string.kept_no_photos_title),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.kept_no_photos_desc),
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                KeptPhotosGridComp(
                    keptPhotos = keptPhotos,
                    keptAlbums = keptAlbums,
                    viewMode = viewMode,
                    selectedAlbumId = selectedAlbumId,
                    onAlbumSelected = { selectedAlbumId = it },
                    onPhotoClick = { activeViewerUri = it },
                    onPhotoLongClick = { photoToRestore = it },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        activeViewerUri?.let { uri ->
            FullscreenImageViewerComp(imageUri = uri, onDismiss = { activeViewerUri = null })
        }

        if (showResetAllDialog) {
            ConfirmDialogComp(
                title = stringResource(R.string.kept_screen_reset_title),
                message = stringResource(R.string.kept_screen_reset_desc),
                confirmLabel = stringResource(R.string.action_reset),
                confirmColor = Color(0xFFE91E63),
                onConfirm = {
                    showResetAllDialog = false
                    viewModel.resetProgress { onResetComplete() }
                },
                onDismiss = { showResetAllDialog = false }
            )
        }

        photoToRestore?.let { uri ->
            ConfirmDialogComp(
                title = stringResource(R.string.kept_screen_restore_title),
                message = stringResource(R.string.kept_screen_restore_desc),
                confirmLabel = stringResource(R.string.action_restore),
                confirmColor = Color(0xFFE91E63),
                onConfirm = {
                    viewModel.restoreKeptPhoto(uri)
                    photoToRestore = null
                },
                onDismiss = { photoToRestore = null }
            )
        }
    }
}
