package com.hightechif.swipecleaner.ui.feature.swipe

import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightechif.swipecleaner.domain.model.Album
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.use_case.AddToTrashUseCase
import com.hightechif.swipecleaner.domain.use_case.ClearTrashedPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.ExecuteTrashRequestUseCase
import com.hightechif.swipecleaner.domain.use_case.GetAllMediaImagesUseCase
import com.hightechif.swipecleaner.domain.use_case.GetFilteredAlbumsUseCase
import com.hightechif.swipecleaner.domain.use_case.GetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.GetShuffledPhotoPoolUseCase
import com.hightechif.swipecleaner.domain.use_case.GetTrashedPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.MarkImageKeptUseCase
import com.hightechif.swipecleaner.domain.use_case.ResetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.RestoreFromKeptUseCase
import com.hightechif.swipecleaner.domain.use_case.RestoreFromTrashUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

enum class SwipeTab {
    SWIPE, KEPT, TRASH
}

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

class SwipeViewModel(
    private val getShuffledPhotoPoolUseCase: GetShuffledPhotoPoolUseCase,
    private val markImageKeptUseCase: MarkImageKeptUseCase,
    private val executeTrashRequestUseCase: ExecuteTrashRequestUseCase,
    private val getKeptPhotosUseCase: GetKeptPhotosUseCase,
    private val getTrashedPhotosUseCase: GetTrashedPhotosUseCase,
    private val addToTrashUseCase: AddToTrashUseCase,
    private val restoreFromTrashUseCase: RestoreFromTrashUseCase,
    private val clearTrashedPhotosUseCase: ClearTrashedPhotosUseCase,
    private val restoreFromKeptUseCase: RestoreFromKeptUseCase,
    private val resetKeptPhotosUseCase: ResetKeptPhotosUseCase,
    private val getMediaImagesUseCase: GetAllMediaImagesUseCase,
    private val getFilteredAlbumsUseCase: GetFilteredAlbumsUseCase
) : ViewModel() {

    companion object {
        private const val MILESTONE_CHECKPOINT_NUMBER = 50
    }

    private val _state = MutableStateFlow(SwipeScreenState())
    val state: StateFlow<SwipeScreenState> = _state.asStateFlow()

    private val _trashEvent = MutableSharedFlow<IntentSender>()
    val trashEvent: SharedFlow<IntentSender> = _trashEvent.asSharedFlow()

    val keptPhotos: StateFlow<List<KeptPhoto>> = getKeptPhotosUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            getTrashedPhotosUseCase().collect { photos ->
                _state.update { it.copy(deleteQueue = photos.map { p -> p.uri }) }
            }
        }
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
            _state.update {
                val nextIndex = it.currentIndex + 1
                val newSwipeCount = it.sessionSwipeCount + 1
                it.copy(
                    currentIndex = nextIndex,
                    keptCount = it.keptCount + 1,
                    sessionSwipeCount = newSwipeCount,
                    showMilestoneDialog = newSwipeCount % MILESTONE_CHECKPOINT_NUMBER == 0,
                    isSessionFinished = nextIndex >= it.photoPool.size
                )
            }
            loadMediaImages()
        }
    }

    fun swipeLeft() {
        val state = _state.value
        if (state.currentIndex >= state.photoPool.size) return

        val currentUri = state.photoPool[state.currentIndex]
        viewModelScope.launch {
            try {
                addToTrashUseCase(currentUri)
                _state.update {
                    val nextIndex = it.currentIndex + 1
                    val newSwipeCount = it.sessionSwipeCount + 1
                    it.copy(
                        currentIndex = nextIndex,
                        sessionSwipeCount = newSwipeCount,
                        showMilestoneDialog = newSwipeCount % MILESTONE_CHECKPOINT_NUMBER == 0,
                        isSessionFinished = nextIndex >= it.photoPool.size
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to swipe left")
            }
        }
    }

    fun restoreFromTrash(uri: String) {
        viewModelScope.launch {
            try {
                restoreFromTrashUseCase(uri)
                _state.update { state ->
                    val newPhotoPool = state.photoPool.toMutableList()
                    newPhotoPool.add(state.currentIndex.coerceIn(0, newPhotoPool.size), uri)
                    state.copy(
                        photoPool = newPhotoPool,
                        sessionSwipeCount = (state.sessionSwipeCount - 1).coerceAtLeast(0),
                        isSessionFinished = false
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to restore from trash")
            }
        }
    }

    fun restoreFromKept(uri: String) {
        viewModelScope.launch {
            try {
                restoreFromKeptUseCase(uri)
                _state.update { state ->
                    val newPhotoPool = state.photoPool.toMutableList()
                    newPhotoPool.add(state.currentIndex.coerceIn(0, newPhotoPool.size), uri)
                    state.copy(
                        keptCount = (state.keptCount - 1).coerceAtLeast(0),
                        photoPool = newPhotoPool,
                        sessionSwipeCount = (state.sessionSwipeCount - 1).coerceAtLeast(0),
                        isSessionFinished = false
                    )
                }
                loadMediaImages()
            } catch (e: Exception) {
                Timber.e(e, "Failed to restore from kept")
            }
        }
    }

    fun resetAllKeptPhotos() {
        viewModelScope.launch {
            try {
                resetKeptPhotosUseCase()
                loadPhotoPool()
                _state.update { it.copy(activeTab = SwipeTab.SWIPE) }
            } catch (e: Exception) {
                Timber.e(e, "Failed to reset all kept photos")
            }
        }
    }

    fun setActiveTab(tab: SwipeTab) {
        _state.update { it.copy(activeTab = tab) }
    }

    fun dismissMilestoneDialog() {
        _state.update { it.copy(showMilestoneDialog = false) }
    }

    fun executeTrashRequest() {
        viewModelScope.launch {
            val sender = executeTrashRequestUseCase(_state.value.deleteQueue)
            if (sender != null) _trashEvent.emit(sender)
        }
    }

    fun onTrashRequestCompleted() {
        viewModelScope.launch {
            try {
                clearTrashedPhotosUseCase()
            } catch (e: Exception) {
                Timber.e(e, "Failed to clear trashed photos after request")
            }
        }
    }
}
