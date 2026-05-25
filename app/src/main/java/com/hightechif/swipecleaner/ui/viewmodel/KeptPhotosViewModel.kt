package com.hightechif.swipecleaner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightechif.swipecleaner.data.db.KeptPhotoEntity
import com.hightechif.swipecleaner.domain.usecase.GetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.usecase.ResetKeptPhotosUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KeptPhotosViewModel(
    getKeptPhotosUseCase: GetKeptPhotosUseCase,
    private val resetKeptPhotosUseCase: ResetKeptPhotosUseCase
) : ViewModel() {

    val keptPhotos: StateFlow<List<KeptPhotoEntity>> = getKeptPhotosUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun resetProgress(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            resetKeptPhotosUseCase()
            onComplete()
        }
    }
}
