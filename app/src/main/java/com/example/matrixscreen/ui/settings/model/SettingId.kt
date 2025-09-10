package com.example.matrixscreen.ui.settings.model

/**
 * Typed keys for MatrixScreen settings.
 * 
 * Each SettingId represents a specific setting field in MatrixSettings with its type.
 * This provides type safety when working with settings and enables the spec-driven
 * UI system to render appropriate controls for each setting type.
 * 
 * All SettingId objects follow the naming convention from the canonical definitions
 * in the Agent Standards & Specs document.
 */
sealed interface SettingId<T> {
    /** The key string that maps to the corresponding field in MatrixSettings */
    val key: String
}

// Motion settings
object Speed : SettingId<Float> { 
    override val key = "fallSpeed" 
}

object Columns : SettingId<Int> { 
    override val key = "columnCount" 
}

object LineSpace : SettingId<Float> { 
    override val key = "lineSpacing" 
}

object ActivePct : SettingId<Float> { 
    override val key = "activePercentage" 
}

object SpeedVar : SettingId<Float> { 
    override val key = "speedVariance" 
}

// Effects settings
object Glow : SettingId<Float> { 
    override val key = "glowIntensity" 
}

object Jitter : SettingId<Float> { 
    override val key = "jitterAmount" 
}

object Flicker : SettingId<Float> { 
    override val key = "flickerAmount" 
}

object Mutation : SettingId<Float> { 
    override val key = "mutationRate" 
}

// Background settings
object GrainD : SettingId<Int> { 
    override val key = "grainDensity" 
}

object GrainO : SettingId<Float> { 
    override val key = "grainOpacity" 
}

object Fps : SettingId<Int> { 
    override val key = "targetFps" 
}

// Color settings (stored as Long ARGB values)
object BgColor : SettingId<Long> { 
    override val key = "backgroundColor" 
}

object HeadColor : SettingId<Long> { 
    override val key = "headColor" 
}

object BrightColor : SettingId<Long> { 
    override val key = "brightTrailColor" 
}

object TrailColor : SettingId<Long> { 
    override val key = "trailColor" 
}

object DimColor : SettingId<Long> { 
    override val key = "dimColor" 
}

// UI theme colors
object UiAccent : SettingId<Long> { 
    override val key = "uiAccent" 
}

object UiOverlay : SettingId<Long> { 
    override val key = "uiOverlayBg" 
}

object UiSelectBg : SettingId<Long> { 
    override val key = "uiSelectionBg" 
}

// Advanced color system
object AdvancedColorsEnabled : SettingId<Boolean> { 
    override val key = "advancedColorsEnabled" 
}

object LinkUiAndRainColors : SettingId<Boolean> { 
    override val key = "linkUiAndRainColors" 
}

// Character settings
object FontSize : SettingId<Int> { 
    override val key = "fontSize" 
}

// Symbol set settings
object SymbolSetId : SettingId<String> { 
    override val key = "symbolSetId" 
}

object SavedCustomSets : SettingId<List<com.example.matrixscreen.data.custom.CustomSymbolSet>> { 
    override val key = "savedCustomSets" 
}

object ActiveCustomSetId : SettingId<String?> { 
    override val key = "activeCustomSetId" 
}

// Trail length settings
object MaxTrailLength : SettingId<Int> { 
    override val key = "maxTrailLength" 
}

object MaxBrightTrailLength : SettingId<Int> { 
    override val key = "maxBrightTrailLength" 
}

// Theme preset settings
object ThemePresetId : SettingId<String?> { 
    override val key = "themePresetId" 
}

// Timing settings
object ColumnStartDelay : SettingId<Float> { 
    override val key = "columnStartDelay" 
}

object ColumnRestartDelay : SettingId<Float> { 
    override val key = "columnRestartDelay" 
}

// Developer settings
object AlwaysShowHints : SettingId<Boolean> { 
    override val key = "alwaysShowHints" 
}

// Test-only Boolean SettingId for WidgetSpec testing
object TestBooleanSetting : SettingId<Boolean> {
    override val key = "testBoolean"
}
