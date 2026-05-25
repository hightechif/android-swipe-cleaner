package com.hightechif.swipecleaner.ui.viewmodel

import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightechif.swipecleaner.domain.usecase.ExecuteTrashRequestUseCase
import com.hightechif.swipecleaner.domain.usecase.GetShuffledPhotoPoolUseCase
import com.hightechif.swipecleaner.domain.usecase.MarkImageKeptUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SwipeUiState(
    val photoPool: List<String> = emptyList(),
    val currentIndex: Int = 0,
    val deleteQueue: List<String> = emptyList(),
    val keptCount: Int = 0,
    val isLoading: Boolean = true,
    val isSessionFinished: Boolean = false
)

class SwipeViewModel(
    private val getShuffledPhotoPoolUseCase: GetShuffledPhotoPoolUseCase,
    private val markImageKeptUseCase: MarkImageKeptUseCase,
    private val executeTrashRequestUseCase: ExecuteTrashRequestUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SwipeUiState())
    val uiState: StateFlow<SwipeUiState> = _uiState.asStateFlow()

    private val _trashEvent = MutableSharedFlow<IntentSender>()
    val trashEvent: SharedFlow<IntentSender> = _trashEvent.asSharedFlow()

    init {
        loadPhotoPool()
    }

    fun loadPhotoPool() {
        _uiState.update { it.copy(isLoading = true, isSessionFinished = false) }
        viewModelScope.launch {
            try {
                val pool = getShuffledPhotoPoolUseCase()
                _uiState.update {
                    it.copy(
                        photoPool = pool,
                        currentIndex = 0,
                        deleteQueue = emptyList(),
                        keptCount = 0,
                        isLoading = false,
                        isSessionFinished = pool.isEmpty()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
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
                it.copy(
                    currentIndex = nextIndex,
                    keptCount = it.keptCount + 1,
                    isSessionFinished = nextIndex >= it.photoPool.size
                )
            }
        }
    }

    fun swipeLeft() {
        val state = _uiState.value
        if (state.currentIndex >= state.photoPool.size) return

        val currentUri = state.photoPool[state.currentIndex]
        _uiState.update {
            val nextIndex = it.currentIndex + 1
            it.copy(
                currentIndex = nextIndex,
                deleteQueue = it.deleteQueue + currentUri,
                isSessionFinished = nextIndex >= it.photoPool.size
            )
        }
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
        // Clear delete queue after triggering the trash system dialog
        _uiState.update { it.copy(deleteQueue = emptyList()) }
    }
}
