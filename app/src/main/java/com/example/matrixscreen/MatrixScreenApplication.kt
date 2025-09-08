package com.example.matrixscreen

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for MatrixScreen with Hilt dependency injection.
 * 
 * This class initializes the Hilt dependency injection graph for the entire application.
 * All DI modules are automatically discovered and configured.
 */
@HiltAndroidApp
class MatrixScreenApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Application initialization can be added here if needed
    }
}
