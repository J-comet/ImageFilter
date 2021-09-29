package hs.project.imagefilter.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hs.project.imagefilter.repository.SavedImagesRepository
import hs.project.imagefilter.util.Coroutines
import java.io.File

class SavedImageViewModel(private val savedImagesRepository: SavedImagesRepository) : ViewModel() {

    private val _savedImagesDataState = MutableLiveData<SavedImageDataState>()
    val savedImageDataState : LiveData<SavedImageDataState> get() = _savedImagesDataState

    fun loadSavedImages(){
        Coroutines.io {
            runCatching {
                emitSavedImagesUiState(isLoading = true)
                savedImagesRepository.loadSavedImages()
            }.onSuccess {
                savedImages ->
                if (savedImages.isNullOrEmpty()) {
                    emitSavedImagesUiState(error = "No Image found")
                } else {
                    emitSavedImagesUiState(savedImages = savedImages)
                }
            }.onFailure {
                emitSavedImagesUiState(error = it.message.toString())
            }
        }
    }

    private fun emitSavedImagesUiState(
        isLoading: Boolean = false,
        savedImages: List<Pair<File, Bitmap>>? = null,
        error: String? = null
    ) {
        val dataState = SavedImageDataState(isLoading, savedImages, error)
        _savedImagesDataState.postValue(dataState)
    }

    data class SavedImageDataState(
        val isLoading : Boolean,
        val savedImages: List<Pair<File, Bitmap>>?,
        val error: String?
    )
}