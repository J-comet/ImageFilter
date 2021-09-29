package hs.project.imagefilter.listener

import java.io.File

interface SavedImageListener {
    fun onImageClicked(file: File)
}