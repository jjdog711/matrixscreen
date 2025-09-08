package com.example.matrixscreen.di

import androidx.datastore.core.DataStore
import com.example.matrixscreen.data.proto.MatrixSettingsProto
import com.example.matrixscreen.data.repo.SettingsRepository
import com.example.matrixscreen.data.repo.SettingsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DI module for data layer dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    /**
     * Provides the SettingsRepository implementation.
     */
    @Provides
    @Singleton
    fun provideSettingsRepository(
        dataStore: DataStore<MatrixSettingsProto>
    ): SettingsRepository {
        return SettingsRepositoryImpl(dataStore)
    }
}
