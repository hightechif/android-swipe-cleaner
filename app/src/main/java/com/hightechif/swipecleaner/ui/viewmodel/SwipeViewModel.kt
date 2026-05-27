package com.hightechif.swipecleaner.ui.viewmodel

import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightechif.swipecleaner.data.db.KeptPhotoEntity
import com.hightechif.swipecleaner.data.repository.KeptPhotosRepository
import com.hightechif.swipecleaner.data.repository.TrashedPhotosRepository
import com.hightechif.swipecleaner.data.repository.MediaStoreRepository
import com.hightechif.swipecleaner.data.repository.Album
import com.hightechif.swipecleaner.data.repository.MediaImage
import com.hightechif.swipecleaner.domain.usecase.ExecuteTrashRequestUseCase
import com.hightechif.swipecleaner.domain.usecase.GetShuffledPhotoPoolUseCase
import com.hightechif.swipecleaner.domain.usecase.MarkImageKeptUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SwipeTab {
    SWIPE, KEPT, TRASH
}

data class SwipeUiState(
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
    val selectedAlbum: Album? = null
)

class SwipeViewModel(
    private val getShuffledPhotoPoolUseCase: GetShuffledPhotoPoolUseCase,
    private val markImageKeptUseCase: MarkImageKeptUseCase,
    private val executeTrashRequestUseCase: ExecuteTrashRequestUseCase,
    private val keptPhotosRepository: KeptPhotosRepository,
    private val trashedPhotosRepository: TrashedPhotosRepository,
    private val mediaStoreRepository: MediaStoreRepository
) : ViewModel() {

    companion object {
        private const val MILESTONE_CHECKPOINT_NUMBER = 50
    }

    private val _uiState = MutableStateFlow(SwipeUiState())
    val uiState: StateFlow<SwipeUiState> = _uiState.asStateFlow()

    private val _trashEvent = MutableSharedFlow<IntentSender>()
    val trashEvent: SharedFlow<IntentSender> = _trashEvent.asSharedFlow()

    private val _mediaImages = MutableStateFlow<List<MediaImage>>(emptyList())
    val mediaImages: StateFlow<List<MediaImage>> = _mediaImages.asStateFlow()

    // Observe the database-backed kept photos list in real time
    val keptPhotos: StateFlow<List<KeptPhotoEntity>> = keptPhotosRepository.getKeptPhotosFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            trashedPhotosRepository.getTrashedPhotosFlow().collect { entities ->
                _uiState.update { state ->
                    state.copy(deleteQueue = entities.map { it.uri })
                }
            }
        }
        loadAlbums()
        loadMediaImages()
        loadPhotoPool()
    }

    fun loadPhotoPool() {
        _uiState.update { it.copy(isLoading = true, isSessionFinished = false) }
        viewModelScope.launch {
            try {
                val selectedAlbumId = _uiState.value.selectedAlbum?.id
                val pool = getShuffledPhotoPoolUseCase(selectedAlbumId)
                _uiState.update { state ->
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
                loadAlbums()
                loadMediaImages()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun selectAlbum(album: Album?) {
        _uiState.update { it.copy(selectedAlbum = album) }
        loadPhotoPool()
    }

    fun loadAlbums() {
        viewModelScope.launch {
            try {
                val albumsList = mediaStoreRepository.queryAllAlbums()
                _uiState.update { it.copy(albums = albumsList) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadMediaImages() {
        viewModelScope.launch {
            try {
                val images = mediaStoreRepository.queryAllMediaImages()
                _mediaImages.value = images
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun swipeRight() {
        val state = _uiState.value
        if (state.currentIndex >= state.photoPool.size) return
        
        val currentUri = state.photoPool[state.currentIndex]
        viewModelScope.launch {
            markImageKeptUseCase(currentUri)
            _uiState.update {
                val nextIndex = it.currentIndex + 1
                val newSwipeCount = it.sessionSwipeCount + 1
                val triggerMilestone = newSwipeCount % MILESTONE_CHECKPOINT_NUMBER == 0
                it.copy(
                    currentIndex = nextIndex,
                    keptCount = it.keptCount + 1,
                    sessionSwipeCount = newSwipeCount,
                    showMilestoneDialog = triggerMilestone,
                    isSessionFinished = nextIndex >= it.photoPool.size
                )
            }
            loadMediaImages() // refresh kept mapping
        }
    }

    fun swipeLeft() {
        val state = _uiState.value
        if (state.currentIndex >= state.photoPool.size) return

        val currentUri = state.photoPool[state.currentIndex]
        viewModelScope.launch {
            try {
                trashedPhotosRepository.insertTrashedPhoto(currentUri)
                _uiState.update {
                    val nextIndex = it.currentIndex + 1
                    val newSwipeCount = it.sessionSwipeCount + 1
                    val triggerMilestone = newSwipeCount % MILESTONE_CHECKPOINT_NUMBER == 0
                    it.copy(
                        currentIndex = nextIndex,
                        sessionSwipeCount = newSwipeCount,
                        showMilestoneDialog = triggerMilestone,
                        isSessionFinished = nextIndex >= it.photoPool.size
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun restoreFromTrash(uri: String) {
        viewModelScope.launch {
            try {
                trashedPhotosRepository.deleteTrashedPhoto(uri)
                _uiState.update { state ->
                    val newPhotoPool = state.photoPool.toMutableList()
                    val insertIndex = state.currentIndex.coerceIn(0, newPhotoPool.size)
                    newPhotoPool.add(insertIndex, uri)
                    state.copy(
                        photoPool = newPhotoPool,
                        sessionSwipeCount = (state.sessionSwipeCount - 1).coerceAtLeast(0),
                        isSessionFinished = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun restoreFromKept(uri: String) {
        viewModelScope.launch {
            try {
                keptPhotosRepository.deleteKeptPhoto(uri)
                _uiState.update { state ->
                    val newPhotoPool = state.photoPool.toMutableList()
                    val insertIndex = state.currentIndex.coerceIn(0, newPhotoPool.size)
                    newPhotoPool.add(insertIndex, uri)
                    state.copy(
                        keptCount = (state.keptCount - 1).coerceAtLeast(0),
                        photoPool = newPhotoPool,
                        sessionSwipeCount = (state.sessionSwipeCount - 1).coerceAtLeast(0),
                        isSessionFinished = false
                    )
                }
                loadMediaImages() // refresh kept mapping
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetAllKeptPhotos() {
        viewModelScope.launch {
            try {
                keptPhotosRepository.clearAllKeptPhotos()
                loadPhotoPool()
                _uiState.update { it.copy(activeTab = SwipeTab.SWIPE) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setActiveTab(tab: SwipeTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun dismissMilestoneDialog() {
        _uiState.update { it.copy(showMilestoneDialog = false) }
    }

    fun executeTrashRequest() {
        viewModelScope.launch {
            val state = _uiState.value
            val sender = executeTrashRequestUseCase(state.deleteQueue)
            if (sender != null) {
                _trashEvent.emit(sender)
            }
        }
    }

    fun onTrashRequestCompleted() {
        viewModelScope.launch {
            try {
                trashedPhotosRepository.clearAllTrashedPhotos()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
