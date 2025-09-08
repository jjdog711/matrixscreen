package com.example.matrixscreen.core

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/**
 * Qualifier for IO dispatcher used in data operations.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/**
 * DI module for providing coroutine dispatchers.
 */
@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    
    /**
     * Provides IO dispatcher for data operations.
     */
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
