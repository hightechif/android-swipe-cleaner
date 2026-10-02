package com.hightechif.swipecleaner.ui.feature.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightechif.swipecleaner.domain.model.Album
import com.hightechif.swipecleaner.domain.use_case.AddToTrashUseCase
import com.hightechif.swipecleaner.domain.use_case.GetAllMediaImagesUseCase
import com.hightechif.swipecleaner.domain.use_case.GetFilteredAlbumsUseCase
import com.hightechif.swipecleaner.domain.use_case.GetShuffledPhotoPoolUseCase
import com.hightechif.swipecleaner.domain.use_case.MarkImageKeptUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class SwipeViewModel(
    private val getShuffledPhotoPoolUseCase: GetShuffledPhotoPoolUseCase,
    private val markImageKeptUseCase: MarkImageKeptUseCase,
    private val addToTrashUseCase: AddToTrashUseCase,
    private val getMediaImagesUseCase: GetAllMediaImagesUseCase,
    private val getFilteredAlbumsUseCase: GetFilteredAlbumsUseCase
) : ViewModel() {

    companion object {
        private const val MILESTONE_CHECKPOINT_NUMBER = 50
    }

    private val _state = MutableStateFlow(SwipeScreenState())
    val state: StateFlow<SwipeScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getFilteredAlbumsUseCase(_state.map { it.mediaImages }).collect { albums ->
                _state.update { it.copy(albums = albums) }
            }
        }
        loadMediaImages()
        loadPhotoPool()
    }

    fun loadPhotoPool() {
        _state.update { it.copy(isLoading = true, isSessionFinished = false) }
        viewModelScope.launch {
            try {
                val pool = getShuffledPhotoPoolUseCase(_state.value.selectedAlbum?.id)
                _state.update { state ->
                    state.copy(
                        photoPool = pool,
                        currentIndex = 0,
                        keptCount = 0,
                        sessionSwipeCount = 0,
                        showMilestoneDialog = false,
                        isLoading = false,
                        isSessionFinished = pool.isEmpty()
                    )
                }
                loadMediaImages()
            } catch (e: Exception) {
                Timber.e(e, "Failed to load photo pool")
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectAlbum(album: Album?) {
        _state.update { it.copy(selectedAlbum = album) }
        loadPhotoPool()
    }

    fun loadMediaImages() {
        viewModelScope.launch {
            try {
                val images = getMediaImagesUseCase()
                _state.update { it.copy(mediaImages = images) }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load media images")
            }
        }
    }

    fun swipeRight() {
        val state = _state.value
        if (state.currentIndex >= state.photoPool.size) return

        val currentUri = state.photoPool[state.currentIndex]
        viewModelScope.launch {
            markImageKeptUseCase(currentUri)
            _state.update { it.advanced().copy(keptCount = it.keptCount + 1) }
        }
    }

    fun swipeLeft() {
        val state = _state.value
        if (state.currentIndex >= state.photoPool.size) return

        val currentUri = state.photoPool[state.currentIndex]
        viewModelScope.launch {
            try {
                addToTrashUseCase(currentUri)
                _state.update { it.advanced() }
            } catch (e: Exception) {
                Timber.e(e, "Failed to swipe left")
            }
        }
    }

    /**
     * Puts a photo that was restored from the trash or kept list back into the deck,
     * at the current position, so it is reviewed again.
     */
    fun onPhotoRestored(uri: String, wasKept: Boolean) {
        _state.update { state ->
            val newPhotoPool = state.photoPool.toMutableList()
            newPhotoPool.add(state.currentIndex.coerceIn(0, newPhotoPool.size), uri)
            state.copy(
                photoPool = newPhotoPool,
                keptCount = if (wasKept) (state.keptCount - 1).coerceAtLeast(0) else state.keptCount,
                sessionSwipeCount = (state.sessionSwipeCount - 1).coerceAtLeast(0),
                isSessionFinished = false
            )
        }
    }

    fun onAllKeptPhotosReset() {
        loadPhotoPool()
        _state.update { it.copy(activeTab = SwipeTab.SWIPE) }
    }

    fun setActiveTab(tab: SwipeTab) {
        _state.update { it.copy(activeTab = tab) }
    }

    fun dismissMilestoneDialog() {
        _state.update { it.copy(showMilestoneDialog = false) }
    }

    private fun SwipeScreenState.advanced(): SwipeScreenState {
        val nextIndex = currentIndex + 1
        val newSwipeCount = sessionSwipeCount + 1
        return copy(
            currentIndex = nextIndex,
            sessionSwipeCount = newSwipeCount,
            showMilestoneDialog = newSwipeCount % MILESTONE_CHECKPOINT_NUMBER == 0,
            isSessionFinished = nextIndex >= photoPool.size
        )
    }
}
