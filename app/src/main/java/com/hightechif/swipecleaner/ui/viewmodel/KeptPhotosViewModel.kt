package com.hightechif.swipecleaner.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightechif.swipecleaner.data.db.KeptPhotoEntity
import com.hightechif.swipecleaner.data.repository.MediaStoreRepository
import com.hightechif.swipecleaner.data.repository.MediaImage
import com.hightechif.swipecleaner.data.repository.KeptPhotosRepository
import com.hightechif.swipecleaner.domain.usecase.GetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.usecase.ResetKeptPhotosUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class KeptPhotosViewModel(
    getKeptPhotosUseCase: GetKeptPhotosUseCase,
    private val resetKeptPhotosUseCase: ResetKeptPhotosUseCase,
    private val mediaStoreRepository: MediaStoreRepository,
    private val keptPhotosRepository: KeptPhotosRepository
) : ViewModel() {

    val keptPhotos: StateFlow<List<KeptPhotoEntity>> = getKeptPhotosUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val mediaImages: StateFlow<List<MediaImage>> = flow {
        try {
            emit(mediaStoreRepository.queryAllMediaImages())
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }.stateIn(
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

    fun restoreKeptPhoto(uri: String) {
        viewModelScope.launch {
            try {
                keptPhotosRepository.deleteKeptPhoto(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
