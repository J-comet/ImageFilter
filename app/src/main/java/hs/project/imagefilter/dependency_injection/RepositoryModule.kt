package hs.project.imagefilter.dependency_injection

import hs.project.imagefilter.repository.EditImageRepository
import hs.project.imagefilter.repository.EditImageRepositoryImpl
import hs.project.imagefilter.repository.SavedImagesRepository
import hs.project.imagefilter.repository.SavedImagesRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<EditImageRepository> { EditImageRepositoryImpl(androidContext()) }
    factory<SavedImagesRepository> { SavedImagesRepositoryImpl(androidContext()) }
}