package com.example.matrixscreen.data.repo

import com.example.matrixscreen.data.model.MatrixSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for MatrixScreen settings persistence.
 * 
 * Provides a clean abstraction over the underlying storage mechanism,
 * allowing the UI layer to work with domain models without knowing
 * about the specific persistence implementation.
 */
interface SettingsRepository {
    
    /**
     * Observe the current settings state.
     * 
     * @return Flow that emits the current settings and updates when changed
     */
    fun observe(): Flow<MatrixSettings>
    
    /**
     * Save new settings to persistent storage.
     * 
     * @param settings The settings to save
     */
    suspend fun save(settings: MatrixSettings)
    
    /**
     * Get the current settings synchronously.
     * 
     * @return Current settings instance
     */
    suspend fun getCurrent(): MatrixSettings
    
    /**
     * Reset all settings to default values.
     */
    suspend fun resetToDefaults()
}
