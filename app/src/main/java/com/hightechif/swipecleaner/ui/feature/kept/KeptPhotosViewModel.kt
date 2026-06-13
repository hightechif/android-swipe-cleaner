package com.hightechif.swipecleaner.ui.feature.kept

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hightechif.swipecleaner.domain.model.KeptPhoto
import com.hightechif.swipecleaner.domain.model.MediaImage
import com.hightechif.swipecleaner.domain.use_case.GetAllMediaImagesUseCase
import com.hightechif.swipecleaner.domain.use_case.GetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.ResetKeptPhotosUseCase
import com.hightechif.swipecleaner.domain.use_case.RestoreFromKeptUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

data class KeptPhotosScreenState(
    val keptPhotos: List<KeptPhoto> = emptyList(),
    val mediaImages: List<MediaImage> = emptyList()
)

class KeptPhotosViewModel(
    getKeptPhotosUseCase: GetKeptPhotosUseCase,
    private val resetKeptPhotosUseCase: ResetKeptPhotosUseCase,
    private val getMediaImagesUseCase: GetAllMediaImagesUseCase,
    private val restoreFromKeptUseCase: RestoreFromKeptUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(KeptPhotosScreenState())
    val state: StateFlow<KeptPhotosScreenState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getKeptPhotosUseCase().collect { photos ->
                _state.update { it.copy(keptPhotos = photos) }
            }
        }
        viewModelScope.launch {
            try {
                val images = getMediaImagesUseCase()
                _state.update { it.copy(mediaImages = images) }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load media images")
            }
        }
    }

    fun resetProgress(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            resetKeptPhotosUseCase()
            onComplete()
        }
    }

    fun restoreKeptPhoto(uri: String) {
        viewModelScope.launch {
            try {
                restoreFromKeptUseCase(uri)
            } catch (e: Exception) {
                Timber.e(e, "Failed to restore kept photo")
            }
        }
    }
}
