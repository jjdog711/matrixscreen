package com.example.matrixscreen.ui.settings.model

import com.example.matrixscreen.data.model.MatrixSettings

/**
 * Centralized binding functions for MatrixSettings mapping.
 * 
 * This provides a single source of truth for mapping between SettingId<T> keys
 * and MatrixSettings field values. All UI components and ViewModels should use
 * these functions instead of directly accessing MatrixSettings fields.
 * 
 * The binding functions provide type safety and ensure that all SettingId objects
 * are properly handled in the mapping logic.
 */

/**
 * Get the value for a specific setting from MatrixSettings.
 * 
 * @param id The SettingId to retrieve
 * @return The current value for the setting
 * @throws IllegalArgumentException if the SettingId is not handled
 */
@Suppress("UNCHECKED_CAST")
fun <T> MatrixSettings.get(id: SettingId<T>): T {
    return when (id) {
        // Motion settings
        Speed -> fallSpeed as T
        Columns -> columnCount as T
        LineSpace -> lineSpacing as T
        ActivePct -> activePercentage as T
        SpeedVar -> speedVariance as T
        
        // Effects settings
        Glow -> glowIntensity as T
        Jitter -> jitterAmount as T
        Flicker -> flickerAmount as T
        Mutation -> mutationRate as T
        
        // Background settings
        GrainD -> grainDensity as T
        GrainO -> grainOpacity as T
        Fps -> targetFps as T
        
        // Color settings
        BgColor -> backgroundColor as T
        HeadColor -> headColor as T
        BrightColor -> brightTrailColor as T
        TrailColor -> trailColor as T
        DimColor -> dimColor as T
        
        // UI theme colors
        UiAccent -> uiAccent as T
        UiOverlay -> uiOverlayBg as T
        UiSelectBg -> uiSelectionBg as T
        
        // Advanced color system
        AdvancedColorsEnabled -> advancedColorsEnabled as T
        LinkUiAndRainColors -> linkUiAndRainColors as T
        
        // Character settings
        FontSize -> fontSize as T
        
        // Symbol set settings
        SymbolSetId -> symbolSetId as T
        SavedCustomSets -> savedCustomSets as T
        ActiveCustomSetId -> activeCustomSetId as T
        
        // Trail length settings
        MaxTrailLength -> maxTrailLength as T
        MaxBrightTrailLength -> maxBrightTrailLength as T
        
        // Theme preset settings
        ThemePresetId -> themePresetId as T
        
        // Timing settings
        ColumnStartDelay -> columnStartDelay as T
        ColumnRestartDelay -> columnRestartDelay as T
        
        // Developer settings
        AlwaysShowHints -> alwaysShowHints as T
        
        // Test-only Boolean setting
        TestBooleanSetting -> throw UnsupportedOperationException("TestBooleanSetting is for testing only")
    }
}

/**
 * Create a new MatrixSettings instance with the specified setting updated.
 * 
 * @param id The SettingId to update
 * @param value The new value for the setting
 * @return A new MatrixSettings instance with the updated value
 * @throws IllegalArgumentException if the SettingId is not handled
 */
@Suppress("UNCHECKED_CAST")
fun <T> MatrixSettings.with(id: SettingId<T>, value: T): MatrixSettings {
    return when (id) {
        // Motion settings
        Speed -> copy(fallSpeed = value as Float)
        Columns -> copy(columnCount = value as Int)
        LineSpace -> copy(lineSpacing = value as Float)
        ActivePct -> copy(activePercentage = value as Float)
        SpeedVar -> copy(speedVariance = value as Float)
        
        // Effects settings
        Glow -> copy(glowIntensity = value as Float)
        Jitter -> copy(jitterAmount = value as Float)
        Flicker -> copy(flickerAmount = value as Float)
        Mutation -> copy(mutationRate = value as Float)
        
        // Background settings
        GrainD -> copy(grainDensity = value as Int)
        GrainO -> copy(grainOpacity = value as Float)
        Fps -> copy(targetFps = value as Int)
        
        // Color settings
        BgColor -> copy(backgroundColor = value as Long)
        HeadColor -> copy(headColor = value as Long)
        BrightColor -> copy(brightTrailColor = value as Long)
        TrailColor -> copy(trailColor = value as Long)
        DimColor -> copy(dimColor = value as Long)
        
        // UI theme colors
        UiAccent -> copy(uiAccent = value as Long)
        UiOverlay -> copy(uiOverlayBg = value as Long)
        UiSelectBg -> copy(uiSelectionBg = value as Long)
        
        // Advanced color system
        AdvancedColorsEnabled -> copy(advancedColorsEnabled = value as Boolean)
        LinkUiAndRainColors -> copy(linkUiAndRainColors = value as Boolean)
        
        // Character settings
        FontSize -> copy(fontSize = value as Int)
        
        // Symbol set settings
        SymbolSetId -> copy(symbolSetId = value as String)
        SavedCustomSets -> copy(savedCustomSets = value as List<com.example.matrixscreen.data.custom.CustomSymbolSet>)
        ActiveCustomSetId -> copy(activeCustomSetId = value as String?)
        
        // Trail length settings
        MaxTrailLength -> copy(maxTrailLength = value as Int)
        MaxBrightTrailLength -> copy(maxBrightTrailLength = value as Int)
        
        // Theme preset settings
        ThemePresetId -> copy(themePresetId = value as String?)
        
        // Timing settings
        ColumnStartDelay -> copy(columnStartDelay = value as Float)
        ColumnRestartDelay -> copy(columnRestartDelay = value as Float)
        
        // Developer settings
        AlwaysShowHints -> copy(alwaysShowHints = value as Boolean)
        
        // Test-only Boolean setting
        TestBooleanSetting -> throw UnsupportedOperationException("TestBooleanSetting is for testing only")
    }
}
