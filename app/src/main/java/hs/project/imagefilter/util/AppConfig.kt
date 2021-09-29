package hs.project.imagefilter.util

import android.app.Application
import hs.project.imagefilter.dependency_injection.repositoryModule
import hs.project.imagefilter.dependency_injection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppConfig: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppConfig)
            modules(listOf(repositoryModule, viewModelModule))
        }
    }
}