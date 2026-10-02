package com.hightechif.swipecleaner.ui.feature.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightechif.swipecleaner.domain.model.PendingSystemAction
import com.hightechif.swipecleaner.domain.use_case.ClearTrashedPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.ExecuteTrashRequestUseCase
import com.hightechif.swipecleaner.domain.use_case.GetTrashedPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.RestoreFromTrashUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class TrashViewModel(
    getTrashedPhotosUseCase: GetTrashedPhotosUseCase,
    private val executeTrashRequestUseCase: ExecuteTrashRequestUseCase,
    private val clearTrashedPhotosUseCase: ClearTrashedPhotosUseCase,
    private val restoreFromTrashUseCase: RestoreFromTrashUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TrashScreenState())
    val state: StateFlow<TrashScreenState> = _state.asStateFlow()

    private val _trashEvent = MutableSharedFlow<PendingSystemAction>()
    val trashEvent: SharedFlow<PendingSystemAction> = _trashEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            getTrashedPhotosUseCase().collect { photos ->
                _state.update { it.copy(deleteQueue = photos.map { photo -> photo.uri }) }
            }
        }
    }

    fun executeTrashRequest() {
        viewModelScope.launch {
            val result = executeTrashRequestUseCase(_state.value.deleteQueue)
            if (result.handle != null) _trashEvent.emit(result)
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

    fun restoreFromTrash(uri: String, onRestored: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                restoreFromTrashUseCase(uri)
                onRestored()
            } catch (e: Exception) {
                Timber.e(e, "Failed to restore from trash")
            }
        }
    }
}
