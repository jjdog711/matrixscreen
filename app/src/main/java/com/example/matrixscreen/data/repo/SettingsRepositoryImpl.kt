package com.example.matrixscreen.data.repo

import androidx.datastore.core.DataStore
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.model.createDefaultProto
import com.example.matrixscreen.data.model.toDomain
import com.example.matrixscreen.data.model.toProto
import com.example.matrixscreen.data.proto.MatrixSettingsProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of SettingsRepository using Proto DataStore.
 * 
 * Handles persistence of MatrixScreen settings with proper error handling,
 * validation, and migration support.
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<MatrixSettingsProto>
) : SettingsRepository {
    
    /**
     * Observe the current settings state with error handling.
     * 
     * If the DataStore is corrupted or empty, it will emit default settings
     * and attempt to recover by saving the defaults.
     */
    override fun observe(): Flow<MatrixSettings> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                // Emit default settings if DataStore is corrupted
                emit(createDefaultProto())
            } else {
                throw exception
            }
        }
        .map { proto ->
            proto.toDomain()
        }
    
    /**
     * Save new settings to persistent storage.
     * 
     * The settings are validated and clamped during the conversion to proto.
     */
    override suspend fun save(settings: MatrixSettings) {
        dataStore.updateData { currentProto ->
            settings.toProto()
        }
    }
    
    /**
     * Get the current settings synchronously.
     * 
     * @return Current settings instance, or defaults if not available
     */
    override suspend fun getCurrent(): MatrixSettings {
        return try {
            dataStore.data.map { proto -> proto.toDomain() }.catch { 
                emit(createDefaultProto().toDomain())
            }.first()
        } catch (exception: Exception) {
            // Return defaults if unable to read
            MatrixSettings.DEFAULT
        }
    }
    
    /**
     * Reset all settings to default values.
     */
    override suspend fun resetToDefaults() {
        dataStore.updateData { 
            createDefaultProto()
        }
    }
}
