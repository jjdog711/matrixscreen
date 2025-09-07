package com.example.matrixscreen.ui.settings.model

/**
 * Data-driven control metadata for consistent UI generation
 * This model allows the UI to build controls consistently without one-off slider code per screen
 */
sealed class ControlType { 
    object Slider : ControlType()
    object Toggle : ControlType() 
    object Select : ControlType()
    object Color : ControlType()
    object Preset : ControlType()
}

/**
 * Specification for a setting control
 */
data class SettingSpec(
    val key: String,                 // "fallSpeed" - matches MatrixSettings field
    val label: String,               // "Rain Speed" - user-facing name
    val type: ControlType,
    val range: ClosedFloatingPointRange<Float>? = null,  // For sliders
    val step: Float? = null,         // Step size for sliders
    val unit: String? = null,        // "%", "px", "s", "dp" - display unit
    val help: String? = null,        // Tooltip/help text
    val options: List<String>? = null, // For selects
    val performanceImpact: Boolean = false, // Show ⚡ icon for FPS-affecting controls
    val category: SettingCategory = SettingCategory.MOTION
)

/**
 * Setting categories for the 6-tab navigation
 */
enum class SettingCategory(
    val displayName: String,
    val description: String,
    val icon: String
) {
    THEME("Theme", "Colors and visual themes", "🎨"),
    CHARACTERS("Characters", "Symbols, fonts, and text", "🔤"),
    MOTION("Motion", "Rain speed, columns, and flow", "🌊"),
    EFFECTS("Effects", "Glow, jitter, and visual effects", "✨"),
    TIMING("Timing", "Spawn delays and timing", "⏱️"),
    BACKGROUND("Background", "Film grain and background effects", "🎬")
}

/**
 * Preset definitions for quick configuration
 */
data class SettingPreset(
    val name: String,
    val description: String,
    val settings: Map<String, Any> // key -> value mapping
)

/**
 * Predefined presets for different use cases
 */
object SettingPresets {
    val FILM_ACCURATE = SettingPreset(
        name = "Film-Accurate",
        description = "Cinematic baseline with authentic Matrix feel",
        settings = mapOf(
            "targetFps" to 30f,
            "colorTint" to "GREEN",
            "advancedColorsEnabled" to false,
            "fallSpeed" to 2.0f,
            "columnCount" to 150,
            "rowHeightMultiplier" to 0.9f,
            "glowIntensity" to 1.5f,
            "flickerRate" to 0.15f,
            "mutationRate" to 0.05f,
            "grainDensity" to 220,
            "grainOpacity" to 0.03f
        )
    )
    
    val PERFORMANCE = SettingPreset(
        name = "Performance",
        description = "Optimized for low-end devices",
        settings = mapOf(
            "targetFps" to 60f,
            "columnCount" to 90,
            "glowIntensity" to 0.8f,
            "grainDensity" to 120,
            "maxTrailLength" to 60,
            "maxBrightTrailLength" to 8
        )
    )
    
    val SHOWCASE = SettingPreset(
        name = "Showcase",
        description = "Maximum visual flair and effects",
        settings = mapOf(
            "glowIntensity" to 2.5f,
            "flickerRate" to 0.3f,
            "mutationRate" to 0.15f,
            "maxTrailLength" to 120,
            "maxBrightTrailLength" to 20,
            "columnCount" to 200,
            "jitterAmount" to 2.5f
        )
    )
    
    val ALL_PRESETS = listOf(FILM_ACCURATE, PERFORMANCE, SHOWCASE)
}
