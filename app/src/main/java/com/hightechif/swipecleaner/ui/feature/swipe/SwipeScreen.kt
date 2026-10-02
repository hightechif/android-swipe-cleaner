package com.hightechif.swipecleaner.ui.feature.swipe

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hightechif.swipecleaner.R
import com.hightechif.swipecleaner.ui.component.TrashRequestEffectComp
import com.hightechif.swipecleaner.ui.component.AlbumSelectorDialogComp
import com.hightechif.swipecleaner.ui.component.ConfirmDialogComp
import com.hightechif.swipecleaner.ui.component.EmptySwipeViewComp
import com.hightechif.swipecleaner.ui.component.FullscreenImageViewerComp
import com.hightechif.swipecleaner.ui.component.KeptTabContentComp
import com.hightechif.swipecleaner.ui.component.MilestoneDialogComp
import com.hightechif.swipecleaner.ui.component.SessionCompletedViewComp
import com.hightechif.swipecleaner.ui.component.SwipeBottomBarComp
import com.hightechif.swipecleaner.ui.component.SwipeContentComp
import com.hightechif.swipecleaner.ui.component.TrashTabContentComp
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

    BackHandler(enabled = activeViewerUri != null) {
        setActiveViewerUri(null)
    }
    val (photoToResetFromKept, setPhotoToResetFromKept) = remember { mutableStateOf<String?>(null) }
    val (photoToRestoreFromTrash, setPhotoToRestoreFromTrash) = remember {
        mutableStateOf<String?>(
            null
        )
    }
    val (showResetAllKeptDialog, setShowResetAllKeptDialog) = remember { mutableStateOf(false) }
    val (showAlbumSelectorDialog, setShowAlbumSelectorDialog) = remember { mutableStateOf(false) }

    TrashRequestEffectComp(
        trashEvents = viewModel.trashEvent,
        onTrashCompleted = viewModel::onTrashRequestCompleted
    )

    if (showAlbumSelectorDialog) {
        AlbumSelectorDialogComp(
            albums = state.albums,
            onAlbumSelected = { album ->
                viewModel.selectAlbum(album)
                setShowAlbumSelectorDialog(false)
            },
            onDismiss = { setShowAlbumSelectorDialog(false) }
        )
    }

    if (state.showMilestoneDialog) {
        MilestoneDialogComp(
            swipeCount = state.sessionSwipeCount,
            trashCount = state.deleteQueue.size,
            onReviewTrash = {
                viewModel.dismissMilestoneDialog()
                viewModel.setActiveTab(SwipeTab.TRASH)
            },
            onEmptyTrash = {
                viewModel.dismissMilestoneDialog()
                viewModel.executeTrashRequest()
            },
            onDismiss = { viewModel.dismissMilestoneDialog() }
        )
    }

    photoToResetFromKept?.let { uri ->
        ConfirmDialogComp(
            title = stringResource(R.string.kept_restore_photo_title),
            message = stringResource(R.string.kept_restore_photo_desc),
            confirmLabel = stringResource(R.string.action_reset),
            confirmColor = Color(0xFFE91E63),
            onConfirm = {
                viewModel.restoreFromKept(uri)
                setPhotoToResetFromKept(null)
            },
            onDismiss = { setPhotoToResetFromKept(null) }
        )
    }

    photoToRestoreFromTrash?.let { uri ->
        ConfirmDialogComp(
            title = stringResource(R.string.trash_restore_title),
            message = stringResource(R.string.trash_restore_desc),
            confirmLabel = stringResource(R.string.action_restore),
            confirmColor = Color(0xFF6C63FF),
            onConfirm = {
                viewModel.restoreFromTrash(uri)
                setPhotoToRestoreFromTrash(null)
            },
            onDismiss = { setPhotoToRestoreFromTrash(null) }
        )
    }

    if (showResetAllKeptDialog) {
        ConfirmDialogComp(
            title = stringResource(R.string.kept_reset_all),
            message = stringResource(R.string.kept_reset_all_desc),
            confirmLabel = stringResource(R.string.action_restore_all),
            confirmColor = Color(0xFFE91E63),
            onConfirm = {
                setShowResetAllKeptDialog(false)
                viewModel.resetAllKeptPhotos()
            },
            onDismiss = { setShowResetAllKeptDialog(false) }
        )
    }

    Scaffold(
        bottomBar = {
            SwipeBottomBarComp(
                activeTab = state.activeTab,
                keptCount = keptPhotos.size,
                trashCount = state.deleteQueue.size,
                onTabSelected = viewModel::setActiveTab
            )
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
                            text = stringResource(R.string.app_name),
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
                                text = state.selectedAlbum?.name ?: stringResource(R.string.swipe_all_photos),
                                color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = stringResource(R.string.swipe_select_folder),
                                tint = Color.LightGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (!state.isLoading && state.photoPool.isNotEmpty() && !state.isSessionFinished) {
                            Text(
                                text = stringResource(R.string.swipe_progress, state.currentIndex, state.photoPool.size),
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
                                            text = stringResource(R.string.swipe_loading),
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
