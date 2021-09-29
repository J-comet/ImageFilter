package hs.project.imagefilter.dependency_injection

import hs.project.imagefilter.viewmodel.EditImageViewModel
import hs.project.imagefilter.viewmodel.SavedImageViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { EditImageViewModel(editImageRepository = get()) }
    viewModel { SavedImageViewModel(savedImagesRepository = get()) }
}