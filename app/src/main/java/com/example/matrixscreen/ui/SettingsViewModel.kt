package com.example.matrixscreen.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.matrixscreen.data.MatrixColor
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.data.MatrixSettingsRepository
import com.example.matrixscreen.data.SymbolSet
import com.example.matrixscreen.data.matrixSettingsDataStore
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Settings state for overlay management
 */
sealed class SettingsState {
    object MatrixScreen : SettingsState()
    object SettingsList : SettingsState()
    data class Editing(val setting: MatrixSettingType) : SettingsState()
}

/**
 * Types of settings that can be edited in overlay mode
 * Ordered for logical navigation flow (most commonly adjusted first)
 */
enum class MatrixSettingType {
    // Basic Visual Settings (most commonly adjusted)
    FallSpeed,
    SymbolSet,
    ColorAndBrightness, // Unified color and brightness settings
    ColorTint, // Legacy - to be removed
    ColorPicker, // Legacy - to be removed
    
    // Animation Core Settings
    FontSize,
    ColumnCount,
    TargetFps,
    
    // Trail & Visual Effects
    MaxTrailLength,
    MaxBrightTrailLength,
    GlowIntensity,
    BrightnessControls, // New enhanced brightness controls
    
    // Individual Color Controls (for advanced mode)
    RainHeadColor,
    RainBrightTrailColor,
    RainTrailColor,
    RainDimTrailColor,
    UiColor,
    
    // Individual Brightness Controls (for advanced mode)
    LeadBrightnessMultiplier,
    BrightTrailBrightnessMultiplier,
    TrailBrightnessMultiplier,
    DimTrailBrightnessMultiplier,
    
    // Character Effects
    JitterAmount,
    FlickerRate,
    MutationRate,
    
    // Layout & Spacing
    RowHeightMultiplier,
    
    // Timing & Behavior
    ColumnStartDelay,
    ColumnRestartDelay,
    InitialActivePercentage,
    SpeedVariationRate,
    
    // Background Effects
    GrainDensity,
    GrainOpacity,
    
    // System Actions
    ResetAll;
    
    companion object {
        /**
         * Get the next setting in navigation order with wrap-around
         */
        fun getNext(current: MatrixSettingType): MatrixSettingType {
            val values = values()
            val currentIndex = values.indexOf(current)
            return values[(currentIndex + 1) % values.size]
        }
        
        /**
         * Get the previous setting in navigation order with wrap-around
         */
        fun getPrevious(current: MatrixSettingType): MatrixSettingType {
            val values = values()
            val currentIndex = values.indexOf(current)
            return values[(currentIndex - 1 + values.size) % values.size]
        }
        
        /**
         * Get the position of a setting (1-based index)
         */
        fun getPosition(setting: MatrixSettingType): Int {
            return values().indexOf(setting) + 1
        }
        
        /**
         * Get total number of settings
         */
        fun getTotalCount(): Int {
            return values().size
        }
        
        /**
         * Check if a setting requires animation restart
         */
        fun requiresRestart(setting: MatrixSettingType): Boolean {
            return when (setting) {
                // Only ResetAll requires restart
                ResetAll -> true
                else -> false
            }
        }
    }
}

/**
 * ViewModel for managing Matrix settings with DataStore persistence and overlay state
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = try {
        MatrixSettingsRepository(
            application.matrixSettingsDataStore
        )
    } catch (e: Exception) {
        android.util.Log.e("SettingsViewModel", "Repository initialization failed: ${e.message}")
        // This will be handled by the settings StateFlow fallback
        throw e
    }
    
    /**
     * Current settings as StateFlow for Compose integration
     */
    val settings: StateFlow<MatrixSettings> = try {
        repository.settingsFlow.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = MatrixSettings()
        )
    } catch (e: Exception) {
        // Fallback to default settings if DataStore fails
        MutableStateFlow(MatrixSettings()).apply {
            android.util.Log.e("SettingsViewModel", "DataStore initialization failed: ${e.message}")
        }
    }
    
    /**
     * Overlay state management
     */
    private val _settingsState = MutableStateFlow<SettingsState>(SettingsState.MatrixScreen)
    val settingsState: StateFlow<SettingsState> = _settingsState
    
    /**
     * Live preview settings - these are temporary values used during editing
     */
    private val _livePreviewSettings = MutableStateFlow<MatrixSettings?>(null)
    val livePreviewSettings: StateFlow<MatrixSettings?> = _livePreviewSettings
    
    /**
     * Last confirmed settings for cancel functionality
     */
    private var lastConfirmedSettings: MatrixSettings? = null
    
    /**
     * Track if current setting requires restart
     */
    private val _requiresRestart = MutableStateFlow(false)
    val requiresRestart: StateFlow<Boolean> = _requiresRestart
    
    /**
     * Start editing a specific setting in overlay mode
     */
    fun startEditingSetting(setting: MatrixSettingType) {
        lastConfirmedSettings = settings.value
        _livePreviewSettings.value = settings.value
        _settingsState.value = SettingsState.Editing(setting)
        _requiresRestart.value = MatrixSettingType.requiresRestart(setting)
    }
    
    /**
     * Update live preview setting during editing
     */
    fun updateLivePreviewSetting(setting: MatrixSettingType, value: Any) {
        val currentSettings = _livePreviewSettings.value ?: settings.value
        val updatedSettings = when (setting) {
            MatrixSettingType.FallSpeed -> currentSettings.copy(fallSpeed = value as Float)
            MatrixSettingType.SymbolSet -> currentSettings.copy(symbolSet = value as SymbolSet)
            MatrixSettingType.ColorAndBrightness -> {
                // Handle unified color and brightness updates
                when {
                    value is com.example.matrixscreen.ui.theme.MatrixColorTheme -> {
                        // Apply theme
                        val theme = value
                        currentSettings.copy(
                            advancedColorsEnabled = true,
                            rainHeadColor = theme.headColor.toArgb().toLong(),
                            rainBrightTrailColor = theme.brightTrailColor.toArgb().toLong(),
                            rainTrailColor = theme.trailColor.toArgb().toLong(),
                            rainDimTrailColor = theme.dimTrailColor.toArgb().toLong(),
                            uiColor = theme.headColor.toArgb().toLong(),
                            backgroundColor = theme.backgroundColor.toArgb().toLong(),
                            selectedThemeName = theme.name
                        )
                    }
                    value is MatrixColor -> {
                        // Apply basic color
                        currentSettings.copy(
                            advancedColorsEnabled = false,
                            colorTint = value
                        )
                    }
                    value is Boolean -> {
                        // Mode toggle
                        if (value) {
                            // Switch to advanced mode
                            val primaryColor = currentSettings.getEffectivePrimaryColor()
                            val generatedSettings = currentSettings.generateAdvancedColorsFromPrimary(primaryColor)
                            generatedSettings.copy(advancedColorsEnabled = true)
                        } else {
                            // Switch to basic mode
                            val primaryColor = currentSettings.getPrimaryColorForBasicMode()
                            val closestMatrixColor = findClosestMatrixColor(primaryColor)
                            currentSettings.copy(
                                advancedColorsEnabled = false,
                                colorTint = closestMatrixColor
                            )
                        }
                    }
                    else -> currentSettings
                }
            }
            MatrixSettingType.ColorTint -> {
                // Handle theme selection - apply the full theme
                if (value is com.example.matrixscreen.ui.theme.MatrixColorTheme) {
                    // Apply the theme directly using the existing applyMatrixTheme logic
                    val theme = value
                    currentSettings.copy(
                        // Enable advanced mode for themes
                        advancedColorsEnabled = true,
                        // Apply all theme colors
                        rainHeadColor = theme.headColor.toArgb().toLong(),
                        rainBrightTrailColor = theme.brightTrailColor.toArgb().toLong(),
                        rainTrailColor = theme.trailColor.toArgb().toLong(),
                        rainDimTrailColor = theme.dimTrailColor.toArgb().toLong(),
                        uiColor = theme.headColor.toArgb().toLong(), // Use head color for UI
                        backgroundColor = theme.backgroundColor.toArgb().toLong(),
                        // Save the selected theme name
                        selectedThemeName = theme.name
                    )
                } else {
                    // Fallback for legacy MatrixColor (shouldn't happen in new system)
                    currentSettings.copy(colorTint = value as MatrixColor)
                }
            }
            MatrixSettingType.ColorPicker -> {
                // Handle ColorPicker updates
                when {
                    value is Boolean -> {
                        // Mode toggle - handle proper transitions and persist immediately
                        val newMode = value
                        if (newMode != currentSettings.advancedColorsEnabled) {
                            val updatedSettings = if (newMode) {
                                // Switching to Advanced mode - generate colors from current primary
                                val primaryColor = currentSettings.getEffectivePrimaryColor()
                                val generatedSettings = currentSettings.generateAdvancedColorsFromPrimary(primaryColor)
                                generatedSettings.copy(advancedColorsEnabled = true)
                            } else {
                                // Switching to Basic mode - use primary color for basic mode
                                val primaryColor = currentSettings.getPrimaryColorForBasicMode()
                                val closestMatrixColor = findClosestMatrixColor(primaryColor)
                                currentSettings.copy(
                                    advancedColorsEnabled = false,
                                    colorTint = closestMatrixColor
                                )
                            }
                            
                            // Persist mode change immediately
                            viewModelScope.launch {
                                if (newMode) {
                                    // Persist all advanced colors
                                    repository.updateAdvancedColors(
                                        rainHeadColor = updatedSettings.rainHeadColor,
                                        rainBrightTrailColor = updatedSettings.rainBrightTrailColor,
                                        rainTrailColor = updatedSettings.rainTrailColor,
                                        rainDimTrailColor = updatedSettings.rainDimTrailColor,
                                        uiColor = updatedSettings.uiColor,
                                        backgroundColor = updatedSettings.backgroundColor
                                    )
                                } else {
                                    // Persist basic mode settings
                                    repository.updateColorTint(updatedSettings.colorTint)
                                }
                                repository.updateAdvancedColorsEnabled(newMode)
                            }
                            
                            updatedSettings
                        } else {
                            currentSettings
                        }
                    }
                    value is Map<*, *> -> {
                        // Advanced color update with type and color
                        val type = value["type"] as? String
                        val color = value["color"] as? Long
                        
                        if (type != null && color != null) {
                            when (type) {
                                "rainHead" -> currentSettings.copy(rainHeadColor = color)
                                "rainBrightTrail" -> currentSettings.copy(rainBrightTrailColor = color)
                                "rainTrail" -> currentSettings.copy(rainTrailColor = color)
                                "rainDimTrail" -> currentSettings.copy(rainDimTrailColor = color)
                                "ui" -> currentSettings.copy(uiColor = color)
                                "background" -> {
                                    android.util.Log.d("ColorPicker", "ViewModel: Background color updated to: ${color.toString(16)}")
                                    currentSettings.copy(backgroundColor = color)
                                }
                                else -> currentSettings
                            }
                        } else {
                            currentSettings
                        }
                    }
                    value is Long -> {
                        // Legacy color value update for advanced mode
                        if (currentSettings.advancedColorsEnabled) {
                            // Update the head color (primary color in advanced mode)
                            currentSettings.copy(rainHeadColor = value)
                        } else {
                            currentSettings
                        }
                    }
                    else -> currentSettings
                }
            }
            MatrixSettingType.FontSize -> currentSettings.copy(fontSize = value as Float)
            MatrixSettingType.ColumnCount -> currentSettings.copy(columnCount = value as Int)
            MatrixSettingType.TargetFps -> currentSettings.copy(targetFps = value as Float)
            MatrixSettingType.RowHeightMultiplier -> currentSettings.copy(rowHeightMultiplier = value as Float)
            MatrixSettingType.MaxTrailLength -> currentSettings.copy(maxTrailLength = value as Int)
            MatrixSettingType.MaxBrightTrailLength -> currentSettings.copy(maxBrightTrailLength = value as Int)
            MatrixSettingType.GlowIntensity -> currentSettings.copy(glowIntensity = value as Float)
            MatrixSettingType.BrightnessControls -> currentSettings.copy(brightnessControlsEnabled = value as Boolean)
            MatrixSettingType.JitterAmount -> currentSettings.copy(jitterAmount = value as Float)
            MatrixSettingType.FlickerRate -> currentSettings.copy(flickerRate = value as Float)
            MatrixSettingType.MutationRate -> currentSettings.copy(mutationRate = value as Float)
            MatrixSettingType.ColumnStartDelay -> currentSettings.copy(columnStartDelay = value as Float)
            MatrixSettingType.ColumnRestartDelay -> currentSettings.copy(columnRestartDelay = value as Float)
            MatrixSettingType.InitialActivePercentage -> currentSettings.copy(initialActivePercentage = value as Float)
            MatrixSettingType.SpeedVariationRate -> currentSettings.copy(speedVariationRate = value as Float)
            MatrixSettingType.GrainDensity -> currentSettings.copy(grainDensity = value as Int)
            MatrixSettingType.GrainOpacity -> currentSettings.copy(grainOpacity = value as Float)
            
            // Individual Color Controls
            MatrixSettingType.RainHeadColor -> currentSettings.copy(rainHeadColor = value as Long)
            MatrixSettingType.RainBrightTrailColor -> currentSettings.copy(rainBrightTrailColor = value as Long)
            MatrixSettingType.RainTrailColor -> currentSettings.copy(rainTrailColor = value as Long)
            MatrixSettingType.RainDimTrailColor -> currentSettings.copy(rainDimTrailColor = value as Long)
            MatrixSettingType.UiColor -> currentSettings.copy(uiColor = value as Long)
            
            // Individual Brightness Controls
            MatrixSettingType.LeadBrightnessMultiplier -> currentSettings.copy(leadBrightnessMultiplier = value as Float)
            MatrixSettingType.BrightTrailBrightnessMultiplier -> currentSettings.copy(brightTrailBrightnessMultiplier = value as Float)
            MatrixSettingType.TrailBrightnessMultiplier -> currentSettings.copy(trailBrightnessMultiplier = value as Float)
            MatrixSettingType.DimTrailBrightnessMultiplier -> currentSettings.copy(dimTrailBrightnessMultiplier = value as Float)
            
            MatrixSettingType.ResetAll -> currentSettings // ResetAll doesn't update individual settings
        }
        _livePreviewSettings.value = updatedSettings
    }
    
    /**
     * Confirm the current live preview settings
     */
    fun confirmSettings() {
        val liveSettings = _livePreviewSettings.value ?: return
        // Apply the live settings to the repository
        applySettingsToRepository(liveSettings)
        _settingsState.value = SettingsState.SettingsList
        _livePreviewSettings.value = null
        lastConfirmedSettings = null
    }
    
    /**
     * Apply the current live preview settings without closing the overlay
     */
    fun applySettingsWithoutClosing() {
        val liveSettings = _livePreviewSettings.value ?: return
        // Apply the live settings to the repository
        applySettingsToRepository(liveSettings)
        // Keep the overlay open and update the current settings
        lastConfirmedSettings = liveSettings
        // Clear live preview settings so matrix rain uses actual repository settings
        _livePreviewSettings.value = null
    }
    
    
    /**
     * Show the overlay by going to settings list
     */
    fun showOverlay() {
        _settingsState.value = SettingsState.SettingsList
    }
    
    /**
     * Hide the overlay by going back to matrix screen
     */
    fun hideOverlay() {
        _settingsState.value = SettingsState.MatrixScreen
    }
    
    /**
     * Cancel editing and revert to last confirmed settings
     */
    fun cancelEditing() {
        _settingsState.value = SettingsState.SettingsList
        _livePreviewSettings.value = null
        lastConfirmedSettings = null
    }
    
    /**
     * Go back to settings list without confirming changes
     */
    fun backToSettingsList() {
        _settingsState.value = SettingsState.SettingsList
        _livePreviewSettings.value = null
        lastConfirmedSettings = null
    }
    
    /**
     * Navigate to next setting in overlay
     */
    fun navigateToNextSetting() {
        val currentState = _settingsState.value
        if (currentState is SettingsState.Editing) {
            val nextSetting = MatrixSettingType.getNext(currentState.setting)
            _settingsState.value = SettingsState.Editing(nextSetting)
            _requiresRestart.value = MatrixSettingType.requiresRestart(nextSetting)
            
            // Update live preview settings to show current value of new setting
            val currentSettings = settings.value
            _livePreviewSettings.value = currentSettings
        }
    }
    
    /**
     * Navigate to previous setting in overlay
     */
    fun navigateToPreviousSetting() {
        val currentState = _settingsState.value
        if (currentState is SettingsState.Editing) {
            val previousSetting = MatrixSettingType.getPrevious(currentState.setting)
            _settingsState.value = SettingsState.Editing(previousSetting)
            _requiresRestart.value = MatrixSettingType.requiresRestart(previousSetting)
            
            // Update live preview settings to show current value of new setting
            val currentSettings = settings.value
            _livePreviewSettings.value = currentSettings
        }
    }
    
    /**
     * Apply settings to repository (helper method)
     */
    private fun applySettingsToRepository(settings: MatrixSettings) {
        viewModelScope.launch {
            repository.updateFallSpeed(settings.fallSpeed)
            repository.updateSymbolSet(settings.symbolSet)
            repository.updateColorTint(settings.colorTint)
            repository.updateFontSize(settings.fontSize)
            repository.updateColumnCount(settings.columnCount)
            repository.updateTargetFps(settings.targetFps)
            repository.updateRowHeightMultiplier(settings.rowHeightMultiplier)
            repository.updateMaxTrailLength(settings.maxTrailLength)
            repository.updateMaxBrightTrailLength(settings.maxBrightTrailLength)
            repository.updateGlowIntensity(settings.glowIntensity)
            repository.updateJitterAmount(settings.jitterAmount)
            repository.updateFlickerRate(settings.flickerRate)
            repository.updateMutationRate(settings.mutationRate)
            repository.updateColumnStartDelay(settings.columnStartDelay)
            repository.updateColumnRestartDelay(settings.columnRestartDelay)
            repository.updateInitialActivePercentage(settings.initialActivePercentage)
            repository.updateSpeedVariationRate(settings.speedVariationRate)
            repository.updateGrainDensity(settings.grainDensity)
            repository.updateGrainOpacity(settings.grainOpacity)
            
            // Advanced color settings
            repository.updateAdvancedColorsEnabled(settings.advancedColorsEnabled)
            repository.updateRainHeadColor(settings.rainHeadColor)
            repository.updateRainBrightTrailColor(settings.rainBrightTrailColor)
            repository.updateRainTrailColor(settings.rainTrailColor)
            repository.updateRainDimTrailColor(settings.rainDimTrailColor)
            repository.updateUiColor(settings.uiColor)
            repository.updateBackgroundColor(settings.backgroundColor)
            
            // Theme preset system
            repository.updateSelectedThemeName(settings.selectedThemeName)
        }
    }
    
    /**
     * Update fall speed setting
     */
    fun updateFallSpeed(speed: Float) {
        viewModelScope.launch {
            repository.updateFallSpeed(speed)
        }
    }
    
    /**
     * Update symbol set setting
     */
    fun updateSymbolSet(symbolSet: SymbolSet) {
        viewModelScope.launch {
            repository.updateSymbolSet(symbolSet)
        }
    }
    
    /**
     * Update color tint setting
     */
    fun updateColorTint(color: MatrixColor) {
        viewModelScope.launch {
            repository.updateColorTint(color)
        }
    }
    
    /**
     * Update animation configuration settings
     */
    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            repository.updateFontSize(size)
        }
    }
    
    fun updateColumnCount(count: Int) {
        viewModelScope.launch {
            repository.updateColumnCount(count)
        }
    }
    
    fun updateTargetFps(fps: Float) {
        viewModelScope.launch {
            repository.updateTargetFps(fps)
        }
    }
    
    fun updateRowHeightMultiplier(multiplier: Float) {
        viewModelScope.launch {
            repository.updateRowHeightMultiplier(multiplier)
        }
    }
    
    fun updateMaxTrailLength(length: Int) {
        viewModelScope.launch {
            repository.updateMaxTrailLength(length)
        }
    }
    
    fun updateMaxBrightTrailLength(length: Int) {
        viewModelScope.launch {
            repository.updateMaxBrightTrailLength(length)
        }
    }
    
    /**
     * Update visual effects settings
     */
    fun updateGlowIntensity(intensity: Float) {
        viewModelScope.launch {
            repository.updateGlowIntensity(intensity)
        }
    }
    
    fun updateJitterAmount(amount: Float) {
        viewModelScope.launch {
            repository.updateJitterAmount(amount)
        }
    }
    
    fun updateFlickerRate(rate: Float) {
        viewModelScope.launch {
            repository.updateFlickerRate(rate)
        }
    }
    
    fun updateMutationRate(rate: Float) {
        viewModelScope.launch {
            repository.updateMutationRate(rate)
        }
    }
    
    /**
     * Update timing & behavior settings
     */
    fun updateColumnStartDelay(delay: Float) {
        viewModelScope.launch {
            repository.updateColumnStartDelay(delay)
        }
    }
    
    fun updateColumnRestartDelay(delay: Float) {
        viewModelScope.launch {
            repository.updateColumnRestartDelay(delay)
        }
    }
    
    fun updateInitialActivePercentage(percentage: Float) {
        viewModelScope.launch {
            repository.updateInitialActivePercentage(percentage)
        }
    }
    
    fun updateSpeedVariationRate(rate: Float) {
        viewModelScope.launch {
            repository.updateSpeedVariationRate(rate)
        }
    }
    
    /**
     * Update background effects settings
     */
    fun updateGrainDensity(density: Int) {
        viewModelScope.launch {
            repository.updateGrainDensity(density)
        }
    }
    
    fun updateGrainOpacity(opacity: Float) {
        viewModelScope.launch {
            repository.updateGrainOpacity(opacity)
        }
    }
    
    /**
     * Custom Symbol Set Management
     */
    fun saveCustomSymbolSet(customSet: com.example.matrixscreen.data.CustomSymbolSet) {
        viewModelScope.launch {
            val currentSettings = settings.value
            val updatedSets = if (currentSettings.savedCustomSets.any { it.id == customSet.id }) {
                // Update existing set
                currentSettings.savedCustomSets.map { if (it.id == customSet.id) customSet else it }
            } else {
                // Add new set
                currentSettings.savedCustomSets + customSet
            }
            repository.updateSavedCustomSets(updatedSets)
        }
    }
    
    fun deleteCustomSymbolSet(setId: String) {
        viewModelScope.launch {
            val currentSettings = settings.value
            val updatedSets = currentSettings.savedCustomSets.filter { it.id != setId }
            repository.updateSavedCustomSets(updatedSets)
            
            // If the deleted set was active, clear the active set
            if (currentSettings.activeCustomSetId == setId) {
                repository.updateActiveCustomSetId(null)
                // Also switch back to Matrix Authentic
                repository.updateSymbolSet(com.example.matrixscreen.data.SymbolSet.MATRIX_AUTHENTIC)
            }
        }
    }
    
    fun selectCustomSymbolSet(setId: String) {
        viewModelScope.launch {
            repository.updateActiveCustomSetId(setId)
            repository.updateSymbolSet(com.example.matrixscreen.data.SymbolSet.CUSTOM)
        }
    }
    
    fun clearActiveCustomSet() {
        viewModelScope.launch {
            repository.updateActiveCustomSetId(null)
            repository.updateSymbolSet(com.example.matrixscreen.data.SymbolSet.MATRIX_AUTHENTIC)
        }
    }
    
    /**
     * Advanced Color System Management
     */
    fun updateAdvancedColorsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAdvancedColorsEnabled(enabled)
        }
    }
    
    fun updateRainHeadColor(color: Long) {
        viewModelScope.launch {
            repository.updateRainHeadColor(color)
        }
    }
    
    fun updateRainBrightTrailColor(color: Long) {
        viewModelScope.launch {
            repository.updateRainBrightTrailColor(color)
        }
    }
    
    fun updateRainTrailColor(color: Long) {
        viewModelScope.launch {
            repository.updateRainTrailColor(color)
        }
    }
    
    fun updateRainDimTrailColor(color: Long) {
        viewModelScope.launch {
            repository.updateRainDimTrailColor(color)
        }
    }
    
    fun updateUiColor(color: Long) {
        viewModelScope.launch {
            repository.updateUiColor(color)
        }
    }
    
    fun updateBackgroundColor(color: Long) {
        viewModelScope.launch {
            repository.updateBackgroundColor(color)
        }
    }
    
    fun updateSelectedThemeName(themeName: String?) {
        viewModelScope.launch {
            repository.updateSelectedThemeName(themeName)
        }
    }
    
    /**
     * Theme Preset System
     */
    fun applyMatrixTheme(theme: com.example.matrixscreen.ui.theme.MatrixColorTheme) {
        viewModelScope.launch {
            // Apply all theme colors to advanced mode
            repository.updateAdvancedColors(
                rainHeadColor = theme.headColor.toArgb().toLong(),
                rainBrightTrailColor = theme.brightTrailColor.toArgb().toLong(),
                rainTrailColor = theme.trailColor.toArgb().toLong(),
                rainDimTrailColor = theme.dimTrailColor.toArgb().toLong(),
                uiColor = theme.headColor.toArgb().toLong(), // Use head color for UI
                backgroundColor = theme.backgroundColor.toArgb().toLong()
            )
            
            // Enable advanced mode to ensure all colors are applied
            repository.updateAdvancedColorsEnabled(true)
            
            // Save the selected theme name
            repository.updateSelectedThemeName(theme.name)
        }
    }
    
    /**
     * Mode transition logic
     */
    fun switchToAdvancedMode() {
        viewModelScope.launch {
            val currentSettings = settings.value
            val primaryColor = currentSettings.getEffectivePrimaryColor()
            
            // Generate advanced colors from current primary color
            val newSettings = currentSettings.generateAdvancedColorsFromPrimary(primaryColor)
            
            // Update all advanced colors at once
            repository.updateAdvancedColors(
                rainHeadColor = newSettings.rainHeadColor,
                rainBrightTrailColor = newSettings.rainBrightTrailColor,
                rainTrailColor = newSettings.rainTrailColor,
                rainDimTrailColor = newSettings.rainDimTrailColor,
                uiColor = newSettings.uiColor,
                backgroundColor = newSettings.backgroundColor
            )
            
            // Enable advanced mode
            repository.updateAdvancedColorsEnabled(true)
        }
    }
    
    fun switchToBasicMode() {
        viewModelScope.launch {
            val currentSettings = settings.value
            val primaryColor = currentSettings.getPrimaryColorForBasicMode()
            
            // Find the closest MatrixColor enum value
            val closestMatrixColor = findClosestMatrixColor(primaryColor)
            repository.updateColorTint(closestMatrixColor)
            
            // Disable advanced mode
            repository.updateAdvancedColorsEnabled(false)
        }
    }
    
    /**
     * Find the closest MatrixColor enum value to a given color
     */
    private fun findClosestMatrixColor(colorValue: Long): com.example.matrixscreen.data.MatrixColor {
        val targetColor = androidx.compose.ui.graphics.Color(colorValue)
        var closestColor = com.example.matrixscreen.data.MatrixColor.GREEN
        var minDistance = Float.MAX_VALUE
        
        com.example.matrixscreen.data.MatrixColor.values().forEach { matrixColor ->
            val distance = calculateColorDistance(targetColor, matrixColor.color)
            if (distance < minDistance) {
                minDistance = distance
                closestColor = matrixColor
            }
        }
        
        return closestColor
    }
    
    /**
     * Calculate color distance using Euclidean distance in RGB space
     */
    private fun calculateColorDistance(color1: Color, color2: Color): Float {
        val rDiff = color1.red - color2.red
        val gDiff = color1.green - color2.green
        val bDiff = color1.blue - color2.blue
        return kotlin.math.sqrt(rDiff * rDiff + gDiff * gDiff + bDiff * bDiff)
    }
    
    /**
     * Validate color selection (prevent UI/Background conflicts)
     */
    fun validateColorSelection(uiColor: Long, backgroundColor: Long): Boolean {
        return uiColor != backgroundColor
    }
    
    /**
     * Update live preview for color picker mode transitions
     */
    fun updateColorPickerMode(enabled: Boolean) {
        val currentSettings = _livePreviewSettings.value ?: settings.value
        val updatedSettings = currentSettings.copy(advancedColorsEnabled = enabled)
        _livePreviewSettings.value = updatedSettings
    }
    
    /**
     * Update live preview for individual advanced colors
     */
    fun updateLivePreviewAdvancedColor(colorType: String, color: Long) {
        val currentSettings = _livePreviewSettings.value ?: settings.value
        val updatedSettings = when (colorType) {
            "rainHead" -> currentSettings.copy(rainHeadColor = color)
            "rainBrightTrail" -> currentSettings.copy(rainBrightTrailColor = color)
            "rainTrail" -> currentSettings.copy(rainTrailColor = color)
            "rainDimTrail" -> currentSettings.copy(rainDimTrailColor = color)
            "ui" -> currentSettings.copy(uiColor = color)
            "background" -> currentSettings.copy(backgroundColor = color)
            else -> currentSettings
        }
        _livePreviewSettings.value = updatedSettings
    }
    
    /**
     * Update color by type string for the color picker dialog
     */
    fun updateColorByType(colorType: String, color: Long) {
        viewModelScope.launch {
            when (colorType) {
                "rainHeadColor" -> repository.updateRainHeadColor(color)
                "rainBrightTrailColor" -> repository.updateRainBrightTrailColor(color)
                "rainTrailColor" -> repository.updateRainTrailColor(color)
                "rainDimTrailColor" -> repository.updateRainDimTrailColor(color)
                "uiColor" -> repository.updateUiColor(color)
                "backgroundColor" -> repository.updateBackgroundColor(color)
            }
        }
    }
    
    
    /**
     * Reset all settings to defaults while preserving custom symbol sets
     */
    fun resetSettings() {
        viewModelScope.launch {
            val currentSettings = settings.value
            // Save custom symbol sets before reset
            val savedCustomSets = currentSettings.savedCustomSets
            val activeCustomSetId = currentSettings.activeCustomSetId
            
            // Reset to defaults
            repository.resetToDefaults()
            
            // Restore custom symbol sets if they exist
            if (savedCustomSets.isNotEmpty()) {
                repository.updateSavedCustomSets(savedCustomSets)
                if (activeCustomSetId != null) {
                    repository.updateActiveCustomSetId(activeCustomSetId)
                }
            }
        }
    }
    
    /**
     * Reset a specific setting to its default value
     */
    fun resetCurrentSetting(setting: MatrixSettingType) {
        val defaultSettings = MatrixSettings()
        when (setting) {
            MatrixSettingType.FallSpeed -> updateLivePreviewSetting(setting, defaultSettings.fallSpeed)
            MatrixSettingType.SymbolSet -> updateLivePreviewSetting(setting, defaultSettings.symbolSet)
            MatrixSettingType.ColorAndBrightness -> {
                // Reset to basic mode with default color
                updateLivePreviewSetting(setting, defaultSettings.colorTint)
            }
            MatrixSettingType.ColorTint -> updateLivePreviewSetting(setting, defaultSettings.colorTint)
            MatrixSettingType.FontSize -> updateLivePreviewSetting(setting, defaultSettings.fontSize)
            MatrixSettingType.ColumnCount -> updateLivePreviewSetting(setting, defaultSettings.columnCount)
            MatrixSettingType.TargetFps -> updateLivePreviewSetting(setting, defaultSettings.targetFps)
            MatrixSettingType.RowHeightMultiplier -> updateLivePreviewSetting(setting, defaultSettings.rowHeightMultiplier)
            MatrixSettingType.MaxTrailLength -> updateLivePreviewSetting(setting, defaultSettings.maxTrailLength)
            MatrixSettingType.MaxBrightTrailLength -> updateLivePreviewSetting(setting, defaultSettings.maxBrightTrailLength)
            MatrixSettingType.GlowIntensity -> updateLivePreviewSetting(setting, defaultSettings.glowIntensity)
            MatrixSettingType.BrightnessControls -> updateLivePreviewSetting(setting, defaultSettings.brightnessControlsEnabled)
            MatrixSettingType.JitterAmount -> updateLivePreviewSetting(setting, defaultSettings.jitterAmount)
            MatrixSettingType.FlickerRate -> updateLivePreviewSetting(setting, defaultSettings.flickerRate)
            MatrixSettingType.MutationRate -> updateLivePreviewSetting(setting, defaultSettings.mutationRate)
            MatrixSettingType.ColumnStartDelay -> updateLivePreviewSetting(setting, defaultSettings.columnStartDelay)
            MatrixSettingType.ColumnRestartDelay -> updateLivePreviewSetting(setting, defaultSettings.columnRestartDelay)
            MatrixSettingType.InitialActivePercentage -> updateLivePreviewSetting(setting, defaultSettings.initialActivePercentage)
            MatrixSettingType.SpeedVariationRate -> updateLivePreviewSetting(setting, defaultSettings.speedVariationRate)
            MatrixSettingType.GrainDensity -> updateLivePreviewSetting(setting, defaultSettings.grainDensity)
            MatrixSettingType.GrainOpacity -> updateLivePreviewSetting(setting, defaultSettings.grainOpacity)
            MatrixSettingType.ColorPicker -> {
                // Reset color picker to basic mode with default colors
                updateLivePreviewSetting(setting, false) // Switch to basic mode
                updateLivePreviewSetting(MatrixSettingType.ColorTint, defaultSettings.colorTint)
            }
            
            // Individual Color Controls
            MatrixSettingType.RainHeadColor -> updateLivePreviewSetting(setting, defaultSettings.rainHeadColor)
            MatrixSettingType.RainBrightTrailColor -> updateLivePreviewSetting(setting, defaultSettings.rainBrightTrailColor)
            MatrixSettingType.RainTrailColor -> updateLivePreviewSetting(setting, defaultSettings.rainTrailColor)
            MatrixSettingType.RainDimTrailColor -> updateLivePreviewSetting(setting, defaultSettings.rainDimTrailColor)
            MatrixSettingType.UiColor -> updateLivePreviewSetting(setting, defaultSettings.uiColor)
            
            // Individual Brightness Controls
            MatrixSettingType.LeadBrightnessMultiplier -> updateLivePreviewSetting(setting, defaultSettings.leadBrightnessMultiplier)
            MatrixSettingType.BrightTrailBrightnessMultiplier -> updateLivePreviewSetting(setting, defaultSettings.brightTrailBrightnessMultiplier)
            MatrixSettingType.TrailBrightnessMultiplier -> updateLivePreviewSetting(setting, defaultSettings.trailBrightnessMultiplier)
            MatrixSettingType.DimTrailBrightnessMultiplier -> updateLivePreviewSetting(setting, defaultSettings.dimTrailBrightnessMultiplier)
            
            MatrixSettingType.ResetAll -> {
                // This will be handled by the ResetAll editor
            }
        }
    }
    
    /**
     * Update brightness multiplier for specific level
     */
    fun updateBrightnessMultiplier(level: Int, multiplier: Float) {
        viewModelScope.launch {
            when (level) {
                4 -> repository.updateLeadBrightnessMultiplier(multiplier)
                3 -> repository.updateBrightTrailBrightnessMultiplier(multiplier)
                2 -> repository.updateTrailBrightnessMultiplier(multiplier)
                1 -> repository.updateDimTrailBrightnessMultiplier(multiplier)
            }
        }
    }
    
    /**
     * Update alpha multiplier for specific level
     */
    fun updateAlphaMultiplier(level: Int, multiplier: Float) {
        viewModelScope.launch {
            when (level) {
                4 -> repository.updateLeadAlphaMultiplier(multiplier)
                3 -> repository.updateBrightTrailAlphaMultiplier(multiplier)
                2 -> repository.updateTrailAlphaMultiplier(multiplier)
                1 -> repository.updateDimTrailAlphaMultiplier(multiplier)
            }
        }
    }
    
    /**
     * Apply brightness preset
     */
    fun applyBrightnessPreset(presetName: String) {
        viewModelScope.launch {
            repository.updateBrightnessPreset(presetName)
            val preset = com.example.matrixscreen.ui.BrightnessPresets.getPreset(presetName)
            repository.updateLeadBrightnessMultiplier(preset.leadBrightness)
            repository.updateBrightTrailBrightnessMultiplier(preset.brightTrailBrightness)
            repository.updateTrailBrightnessMultiplier(preset.trailBrightness)
            repository.updateDimTrailBrightnessMultiplier(preset.dimTrailBrightness)
            repository.updateLeadAlphaMultiplier(preset.leadAlpha)
            repository.updateBrightTrailAlphaMultiplier(preset.brightTrailAlpha)
            repository.updateTrailAlphaMultiplier(preset.trailAlpha)
            repository.updateDimTrailAlphaMultiplier(preset.dimTrailAlpha)
            repository.updateThemeBrightnessProfile(preset.themeBrightnessProfile)
        }
    }
    
    /**
     * Reset brightness controls to defaults
     */
    fun resetBrightnessControls() {
        viewModelScope.launch {
            repository.updateBrightnessControlsEnabled(false)
            repository.updateLeadBrightnessMultiplier(1.0f)
            repository.updateBrightTrailBrightnessMultiplier(1.0f)
            repository.updateTrailBrightnessMultiplier(1.0f)
            repository.updateDimTrailBrightnessMultiplier(1.0f)
            repository.updateLeadAlphaMultiplier(1.0f)
            repository.updateBrightTrailAlphaMultiplier(1.0f)
            repository.updateTrailAlphaMultiplier(1.0f)
            repository.updateDimTrailAlphaMultiplier(1.0f)
            repository.updateBrightnessPreset("default")
            repository.updateThemeBrightnessProfile("balanced")
        }
    }
    
    /**
     * Apply a preset configuration
     */
    fun applyPreset(presetName: String) {
        viewModelScope.launch {
            when (presetName) {
                "Film-Accurate" -> applyFilmAccuratePreset()
                "Performance" -> applyPerformancePreset()
                "Showcase" -> applyShowcasePreset()
            }
        }
    }
    
    /**
     * Film-Accurate preset: Cinematic baseline with authentic Matrix feel
     */
    private suspend fun applyFilmAccuratePreset() {
        repository.updateTargetFps(30f)
        repository.updateColorTint(MatrixColor.GREEN)
        repository.updateAdvancedColorsEnabled(false)
        repository.updateFallSpeed(2.0f)
        repository.updateColumnCount(150)
        repository.updateRowHeightMultiplier(0.9f)
        repository.updateGlowIntensity(1.5f)
        repository.updateFlickerRate(0.15f)
        repository.updateMutationRate(0.05f)
        repository.updateGrainDensity(220)
        repository.updateGrainOpacity(0.03f)
    }
    
    /**
     * Performance preset: Optimized for low-end devices
     */
    private suspend fun applyPerformancePreset() {
        repository.updateTargetFps(60f)
        repository.updateColumnCount(90)
        repository.updateGlowIntensity(0.8f)
        repository.updateGrainDensity(120)
        repository.updateMaxTrailLength(60)
        repository.updateMaxBrightTrailLength(8)
    }
    
    /**
     * Showcase preset: Maximum visual flair and effects
     */
    private suspend fun applyShowcasePreset() {
        repository.updateGlowIntensity(2.5f)
        repository.updateFlickerRate(0.3f)
        repository.updateMutationRate(0.15f)
        repository.updateMaxTrailLength(120)
        repository.updateMaxBrightTrailLength(20)
        repository.updateColumnCount(200)
        repository.updateJitterAmount(2.5f)
    }
}
