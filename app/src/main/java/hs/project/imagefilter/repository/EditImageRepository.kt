package hs.project.imagefilter.repository

import android.graphics.Bitmap
import android.net.Uri
import hs.project.imagefilter.data.ImageFilter

interface EditImageRepository {
     suspend fun prepareImagePreview(imageUri:Uri): Bitmap?
     suspend fun getImageFilters(image: Bitmap): List<ImageFilter>
     suspend fun saveFilteredImage(filteredBitmap: Bitmap): Uri?
}