package hs.project.imagefilter.listener

import hs.project.imagefilter.data.ImageFilter

interface ImageFilterListener {
    fun onFilterSelected(imageFilter: ImageFilter)
}