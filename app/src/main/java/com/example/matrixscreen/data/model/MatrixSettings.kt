package com.example.matrixscreen.data.model

import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.registry.SymbolSetId
import com.example.matrixscreen.data.registry.ThemePresetId

/**
 * Domain model for MatrixScreen settings.
 * 
 * This immutable data class represents the current state of all MatrixScreen
 * settings and serves as the bridge between the UI layer and persistence layer.
 * 
 * All settings are stored as primitive types for simplicity and performance.
 * Colors are stored as Long values representing ARGB color values.
 */
data class MatrixSettings(
    // Schema version for migration compatibility
    val schemaVersion: Int = 1,
    
    // Motion settings
    val fallSpeed: Float = 2.0f,
    val columnCount: Int = 150,
    val lineSpacing: Float = 0.9f,
    val activePercentage: Float = 0.4f,
    val speedVariance: Float = 0.01f,
    
    // Effects settings
    val glowIntensity: Float = 2.0f,
    val jitterAmount: Float = 2.0f,
    val flickerAmount: Float = 0.2f,
    val mutationRate: Float = 0.08f,
    
    // Background settings
    val grainDensity: Int = 200,
    val grainOpacity: Float = 0.03f,
    val targetFps: Int = 60,
    
    // Color settings (stored as int64 ARGB values)
    val backgroundColor: Long = 0xFF000000L,
    val headColor: Long = 0xFF00FF00L,
    val brightTrailColor: Long = 0xFF00CC00L,
    val trailColor: Long = 0xFF008800L,
    val dimColor: Long = 0xFF004400L,
    
    // UI theme colors
    val uiAccent: Long = 0xFF00FF00L, // Use bright electric green like matrix rain
    val uiOverlayBg: Long = 0x26000000L, // Much more transparent for floating glass effect
    val uiSelectionBg: Long = 0x4000FF00L,
    
    // Advanced color system
    val advancedColorsEnabled: Boolean = false,
    val linkUiAndRainColors: Boolean = false,
    
    // Character settings
    val fontSize: Int = 14,
    
    // Symbol set settings (using registry IDs)
    val symbolSetId: String = "MATRIX_AUTHENTIC", // SymbolSetId as String
    val savedCustomSets: List<CustomSymbolSet> = emptyList(),
    val activeCustomSetId: String? = null,
    
    // Trail length settings
    val maxTrailLength: Int = 100,
    val maxBrightTrailLength: Int = 15,
    
    // Theme preset settings (using registry IDs)
    val themePresetId: String? = null, // ThemePresetId as String, null = custom colors
    
    // Timing settings
    val columnStartDelay: Float = 0.01f,
    val columnRestartDelay: Float = 0.5f,
    
    // Developer settings
    val alwaysShowHints: Boolean = false
) {
    
    /**
     * Get the effective FPS clamped to device-supported rates.
     * 
     * @param supportedRates List of device-supported refresh rates
     * @return The nearest supported rate within the valid range [5..120]
     */
    fun getEffectiveFps(supportedRates: List<Int> = listOf(60, 90, 120)): Int {
        val clampedFps = targetFps.coerceIn(5, 120)
        return supportedRates.minByOrNull { kotlin.math.abs(it - clampedFps) } ?: 60
    }
    
    /**
     * Check if the current settings have valid color values.
     * 
     * @return true if all colors are valid ARGB values
     */
    fun hasValidColors(): Boolean {
        val colors = listOf(backgroundColor, headColor, brightTrailColor, trailColor, dimColor, uiAccent, uiOverlayBg, uiSelectionBg)
        return colors.all { it in 0x00000000L..0xFFFFFFFFL }
    }
    
    /**
     * Get a copy of this settings with updated values.
     * 
     * @param updates Map of field names to new values
     * @return New MatrixSettings instance with updated values
     */
    fun update(updates: Map<String, Any>): MatrixSettings {
        var updated = this
        updates.forEach { (key, value) ->
            updated = when (key) {
                "fallSpeed" -> updated.copy(fallSpeed = value as Float)
                "columnCount" -> updated.copy(columnCount = value as Int)
                "lineSpacing" -> updated.copy(lineSpacing = value as Float)
                "activePercentage" -> updated.copy(activePercentage = value as Float)
                "speedVariance" -> updated.copy(speedVariance = value as Float)
                "glowIntensity" -> updated.copy(glowIntensity = value as Float)
                "jitterAmount" -> updated.copy(jitterAmount = value as Float)
                "flickerAmount" -> updated.copy(flickerAmount = value as Float)
                "mutationRate" -> updated.copy(mutationRate = value as Float)
                "grainDensity" -> updated.copy(grainDensity = value as Int)
                "grainOpacity" -> updated.copy(grainOpacity = value as Float)
                "targetFps" -> updated.copy(targetFps = value as Int)
                "backgroundColor" -> updated.copy(backgroundColor = value as Long)
                "headColor" -> updated.copy(headColor = value as Long)
                "brightTrailColor" -> updated.copy(brightTrailColor = value as Long)
                "trailColor" -> updated.copy(trailColor = value as Long)
                "dimColor" -> updated.copy(dimColor = value as Long)
                "uiAccent" -> updated.copy(uiAccent = value as Long)
                "uiOverlayBg" -> updated.copy(uiOverlayBg = value as Long)
                "uiSelectionBg" -> updated.copy(uiSelectionBg = value as Long)
                "fontSize" -> updated.copy(fontSize = value as Int)
                "columnStartDelay" -> updated.copy(columnStartDelay = value as Float)
                "columnRestartDelay" -> updated.copy(columnRestartDelay = value as Float)
                "alwaysShowHints" -> updated.copy(alwaysShowHints = value as Boolean)
                else -> updated
            }
        }
        return updated
    }
    
    companion object {
        /**
         * Default settings instance with all recommended values.
         */
        val DEFAULT = MatrixSettings()
    }
}
