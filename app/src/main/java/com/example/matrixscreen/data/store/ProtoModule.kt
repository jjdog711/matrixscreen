package com.example.matrixscreen.data.store

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStore
import com.example.matrixscreen.data.model.createDefaultProto
import com.example.matrixscreen.data.proto.MatrixSettingsProto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File
import javax.inject.Singleton

/**
 * Proto DataStore module for MatrixScreen settings.
 * 
 * Provides a versioned, typed DataStore for MatrixSettingsProto with
 * migration support and proper lifecycle management.
 */
@Module
@InstallIn(SingletonComponent::class)
object ProtoModule {
    
    private const val DATASTORE_FILE_NAME = "matrix_settings.pb"
    
    /**
     * Provides the Proto DataStore for MatrixSettingsProto.
     * 
     * Uses a custom DataStoreFactory with proper scope management,
     * file-based storage for persistence, and migration support.
     */
    @Provides
    @Singleton
    fun provideMatrixSettingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<MatrixSettingsProto> {
        return DataStoreFactory.create(
            serializer = MatrixSettingsSerializer,
            produceFile = { File(context.filesDir, DATASTORE_FILE_NAME) },
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            migrations = listOf(SharedPreferencesMigration(context)),
            corruptionHandler = ReplaceFileCorruptionHandler { createDefaultProto() }
        )
    }
}

/**
 * Serializer for MatrixSettingsProto.
 * 
 * Handles reading and writing of protobuf data with proper
 * error handling and default value support.
 */
private val MatrixSettingsSerializer = object : androidx.datastore.core.Serializer<MatrixSettingsProto> {
    override val defaultValue: MatrixSettingsProto = createDefaultProto()
    
    override suspend fun readFrom(input: java.io.InputStream): MatrixSettingsProto {
        try {
            return MatrixSettingsProto.parseFrom(input)
        } catch (exception: Exception) {
            throw androidx.datastore.core.CorruptionException("Cannot read proto.", exception)
        }
    }
    
    override suspend fun writeTo(t: MatrixSettingsProto, output: java.io.OutputStream) {
        t.writeTo(output)
    }
}

/**
 * Data migration from SharedPreferences to Proto DataStore.
 * 
 * Migrates legacy settings from SharedPreferences to the new Proto DataStore format.
 * This is a one-time migration that runs when the DataStore is first created.
 */
internal class SharedPreferencesMigration(
    private val context: Context
) : DataMigration<MatrixSettingsProto> {
    
    private val legacyPrefsName = "matrix_screen_prefs"
    
    override suspend fun shouldMigrate(currentData: MatrixSettingsProto): Boolean {
        // Only migrate if we have legacy SharedPreferences and no existing proto data
        val legacyPrefs = context.getSharedPreferences(legacyPrefsName, Context.MODE_PRIVATE)
        val hasLegacyData = legacyPrefs.all.isNotEmpty()
        val hasProtoData = currentData != MatrixSettingsProto.getDefaultInstance()
        
        return hasLegacyData && !hasProtoData
    }
    
    override suspend fun migrate(currentData: MatrixSettingsProto): MatrixSettingsProto {
        val legacyPrefs = context.getSharedPreferences(legacyPrefsName, Context.MODE_PRIVATE)
        
        return MatrixSettingsProto.newBuilder()
            .setSchemaVersion(1)
            
            // Motion settings
            .setFallSpeed(legacyPrefs.getFloat("fall_speed", 2.0f))
            .setColumnCount(legacyPrefs.getInt("column_count", 150))
            .setLineSpacing(legacyPrefs.getFloat("line_spacing", 0.9f))
            .setActivePercentage(legacyPrefs.getFloat("active_percentage", 0.4f))
            .setSpeedVariance(legacyPrefs.getFloat("speed_variance", 0.01f))
            
            // Effects settings
            .setGlowIntensity(legacyPrefs.getFloat("glow_intensity", 2.0f))
            .setJitterAmount(legacyPrefs.getFloat("jitter_amount", 2.0f))
            .setFlickerAmount(legacyPrefs.getFloat("flicker_amount", 0.2f))
            .setMutationRate(legacyPrefs.getFloat("mutation_rate", 0.08f))
            
            // Background settings
            .setGrainDensity(legacyPrefs.getInt("grain_density", 200))
            .setGrainOpacity(legacyPrefs.getFloat("grain_opacity", 0.03f))
            .setTargetFps(legacyPrefs.getInt("target_fps", 60))
            
            // Color settings
            .setBackgroundColor(legacyPrefs.getLong("background_color", 0xFF000000L))
            .setHeadColor(legacyPrefs.getLong("head_color", 0xFF00FF00L))
            .setBrightTrailColor(legacyPrefs.getLong("bright_trail_color", 0xFF00CC00L))
            .setTrailColor(legacyPrefs.getLong("trail_color", 0xFF008800L))
            .setDimColor(legacyPrefs.getLong("dim_color", 0xFF004400L))
            
            // UI theme colors
            .setUiAccent(legacyPrefs.getLong("ui_accent", 0xFF00CC00L))
            .setUiOverlayBg(legacyPrefs.getLong("ui_overlay_bg", 0x80000000L))
            .setUiSelectionBg(legacyPrefs.getLong("ui_selection_bg", 0x4000FF00L))
            
            // Character settings
            .setFontSize(legacyPrefs.getInt("font_size", 14))
            .build()
    }
    
    override suspend fun cleanUp() {
        // Clean up legacy SharedPreferences after successful migration
        val legacyPrefs = context.getSharedPreferences(legacyPrefsName, Context.MODE_PRIVATE)
        legacyPrefs.edit().clear().apply()
    }
}
