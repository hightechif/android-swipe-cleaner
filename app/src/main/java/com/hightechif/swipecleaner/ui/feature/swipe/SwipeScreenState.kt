package com.hightechif.swipecleaner.ui.feature.swipe

import com.hightechif.swipecleaner.domain.model.Album
import com.hightechif.swipecleaner.domain.model.MediaImage

data class SwipeScreenState(
    val photoPool: List<String> = emptyList(),
    val currentIndex: Int = 0,
    val deleteQueue: List<String> = emptyList(),
    val keptCount: Int = 0,
    val isLoading: Boolean = true,
    val isSessionFinished: Boolean = false,
    val activeTab: SwipeTab = SwipeTab.SWIPE,
    val sessionSwipeCount: Int = 0,
    val showMilestoneDialog: Boolean = false,
    val albums: List<Album> = emptyList(),
    val selectedAlbum: Album? = null,
    val mediaImages: List<MediaImage> = emptyList()
)
