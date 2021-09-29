package hs.project.imagefilter.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hs.project.imagefilter.data.ImageFilter
import hs.project.imagefilter.repository.EditImageRepository
import hs.project.imagefilter.util.Coroutines

class EditImageViewModel(private val editImageRepository: EditImageRepository) : ViewModel() {

    // region:: Prepare image preview

    private val _imagePreviewDataState = MutableLiveData<ImagePreviewDataState>()

    val imagePreviewDataState: LiveData<ImagePreviewDataState> get() = _imagePreviewDataState

    fun prepareImagePreview(imageUri: Uri) {
        Coroutines.io {
            runCatching {
                emitImagePreviewUiState(isLoading = true)
                editImageRepository.prepareImagePreview(imageUri = imageUri)
            }.onSuccess { bitmap ->
                if (bitmap != null) {
                    emitImagePreviewUiState(bitmap = bitmap)
                } else {
                    emitImagePreviewUiState(error = "Unable to prepare image preview")
                }
            }.onFailure {
                emitImagePreviewUiState(error = it.message.toString())
            }
        }
    }

    private fun emitImagePreviewUiState(
        isLoading: Boolean = false,
        bitmap: Bitmap? = null,
        error: String? = null
    ) {
        val dataState = ImagePreviewDataState(isLoading, bitmap, error)
        _imagePreviewDataState.postValue(dataState)
    }

    data class ImagePreviewDataState(
        val isLoading: Boolean,
        val bitmap: Bitmap?,
        val error: String?
    )

    // endregion Prepare image preview

    // region:: Load image filters

    private val _imageFilterDataState = MutableLiveData<ImageFiltersDataState>()
    val imageFilterDataState: LiveData<ImageFiltersDataState> get() = _imageFilterDataState

    fun loadImageFilters(originalImage: Bitmap) {
        Coroutines.io {
            runCatching {
                emitImageFilterUiState(isLoading = true)
                editImageRepository.getImageFilters(getPreviewImage(originalImage))
            }.onSuccess { imageFilters ->
                emitImageFilterUiState(imageFilters = imageFilters)
            }.onFailure {
                emitImageFilterUiState(error = it.message.toString())
            }
        }
    }

    private fun getPreviewImage(originalImage: Bitmap): Bitmap {
        return runCatching {
            val previewWidth = 150
            val previewHeight = originalImage.height * previewWidth / originalImage.width
            Bitmap.createScaledBitmap(originalImage, previewWidth, previewHeight,false)
        }.getOrDefault(originalImage)
    }

    private fun emitImageFilterUiState(
        isLoading: Boolean = false,
        imageFilters: List<ImageFilter>? = null,
        error: String? = null
    ) {
        val dataState = ImageFiltersDataState(isLoading, imageFilters, error)
        _imageFilterDataState.postValue(dataState)
    }

    data class ImageFiltersDataState(
        val isLoading: Boolean,
        val imageFilters: List<ImageFilter>?,
        val error: String?
    )

    // endregion


    // region:: Save filtered image

    private val _saveFilteredImageDataState = MutableLiveData<SaveFilteredImageDataState>()
    val saveFilteredImageDataState: LiveData<SaveFilteredImageDataState> get() = _saveFilteredImageDataState

    fun saveFilteredImage(filterBitmap: Bitmap){
        Coroutines.io {
            runCatching {
                emitSaveFilteredImageUiState(isLoading = true)
                editImageRepository.saveFilteredImage(filterBitmap)
            }.onSuccess { savedImageUri ->
                emitSaveFilteredImageUiState(uri = savedImageUri)
            }.onFailure {
                emitSaveFilteredImageUiState(error = it.message.toString())
            }
        }
    }

    private fun emitSaveFilteredImageUiState(
        isLoading: Boolean = false,
        uri: Uri? = null,
        error: String? = null
    ) {
        val dataState = SaveFilteredImageDataState(isLoading,uri,error)
        _saveFilteredImageDataState.postValue(dataState)

    }


    data class SaveFilteredImageDataState(
        val isLoading: Boolean,
        val uri: Uri?,
        val error: String?
    )

    // endregion
}