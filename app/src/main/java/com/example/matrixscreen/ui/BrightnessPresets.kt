package com.example.matrixscreen.ui

import androidx.compose.ui.graphics.Color
import com.example.matrixscreen.ui.theme.MatrixColorTheme
import com.example.matrixscreen.ui.theme.MatrixColorThemePresets

/**
 * Enhanced brightness preset configurations that integrate with existing theme systems
 * Combines brightness multipliers with theme-aware color profiles
 */
data class BrightnessPreset(
    val name: String,
    val description: String,
    val leadBrightness: Float,
    val leadAlpha: Float,
    val brightTrailBrightness: Float,
    val brightTrailAlpha: Float,
    val trailBrightness: Float,
    val trailAlpha: Float,
    val dimTrailBrightness: Float,
    val dimTrailAlpha: Float,
    val themeBrightnessProfile: String = "balanced"
)

/**
 * Theme brightness profiles that work with existing MatrixColorTheme system
 */
enum class ThemeBrightnessProfile(val displayName: String, val description: String) {
    BALANCED("Balanced", "Even brightness across all levels"),
    HEAD_FOCUSED("Head Focused", "Emphasize lead characters"),
    TRAIL_FOCUSED("Trail Focused", "Emphasize trail characters"),
    UNIFORM("Uniform", "Minimal brightness variation")
}

object BrightnessPresets {
    
    // Core brightness presets
    val DEFAULT = BrightnessPreset(
        name = "Default",
        description = "Original Matrix brightness behavior",
        leadBrightness = 1.0f, leadAlpha = 1.0f,
        brightTrailBrightness = 1.0f, brightTrailAlpha = 1.0f,
        trailBrightness = 1.0f, trailAlpha = 1.0f,
        dimTrailBrightness = 1.0f, dimTrailAlpha = 1.0f,
        themeBrightnessProfile = "balanced"
    )
    
    val ENHANCED = BrightnessPreset(
        name = "Enhanced",
        description = "More dramatic brightness contrast",
        leadBrightness = 1.3f, leadAlpha = 1.1f,
        brightTrailBrightness = 1.2f, brightTrailAlpha = 1.0f,
        trailBrightness = 0.9f, trailAlpha = 0.9f,
        dimTrailBrightness = 0.8f, dimTrailAlpha = 0.8f,
        themeBrightnessProfile = "head_focused"
    )
    
    val SUBTLE = BrightnessPreset(
        name = "Subtle",
        description = "Gentle brightness variations",
        leadBrightness = 0.8f, leadAlpha = 0.9f,
        brightTrailBrightness = 0.9f, brightTrailAlpha = 0.9f,
        trailBrightness = 1.1f, trailAlpha = 1.0f,
        dimTrailBrightness = 1.2f, dimTrailAlpha = 1.1f,
        themeBrightnessProfile = "trail_focused"
    )
    
    val DRAMATIC = BrightnessPreset(
        name = "Dramatic",
        description = "High contrast for maximum impact",
        leadBrightness = 1.5f, leadAlpha = 1.2f,
        brightTrailBrightness = 1.1f, brightTrailAlpha = 0.8f,
        trailBrightness = 0.7f, trailAlpha = 0.7f,
        dimTrailBrightness = 0.5f, dimTrailAlpha = 0.6f,
        themeBrightnessProfile = "head_focused"
    )
    
    val UNIFORM = BrightnessPreset(
        name = "Uniform",
        description = "Minimal brightness variation",
        leadBrightness = 1.0f, leadAlpha = 1.0f,
        brightTrailBrightness = 1.0f, brightTrailAlpha = 1.0f,
        trailBrightness = 1.0f, trailAlpha = 1.0f,
        dimTrailBrightness = 1.0f, dimTrailAlpha = 1.0f,
        themeBrightnessProfile = "uniform"
    )
    
    // Theme-enhanced presets that work with existing MatrixColorTheme system
    val THEME_ENHANCED = BrightnessPreset(
        name = "Theme Enhanced",
        description = "Enhanced brightness for theme colors",
        leadBrightness = 1.2f, leadAlpha = 1.1f,
        brightTrailBrightness = 1.1f, brightTrailAlpha = 1.0f,
        trailBrightness = 0.95f, trailAlpha = 0.95f,
        dimTrailBrightness = 0.9f, dimTrailAlpha = 0.9f,
        themeBrightnessProfile = "balanced"
    )
    
    val THEME_DRAMATIC = BrightnessPreset(
        name = "Theme Dramatic",
        description = "Dramatic brightness for theme colors",
        leadBrightness = 1.4f, leadAlpha = 1.2f,
        brightTrailBrightness = 1.2f, brightTrailAlpha = 0.9f,
        trailBrightness = 0.8f, trailAlpha = 0.8f,
        dimTrailBrightness = 0.6f, dimTrailAlpha = 0.7f,
        themeBrightnessProfile = "head_focused"
    )
    
    // All available presets
    val allPresets = listOf(
        DEFAULT, ENHANCED, SUBTLE, DRAMATIC, UNIFORM, 
        THEME_ENHANCED, THEME_DRAMATIC
    )
    
    // Core presets (without theme-specific ones)
    val corePresets = listOf(DEFAULT, ENHANCED, SUBTLE, DRAMATIC, UNIFORM)
    
    // Theme-enhanced presets
    val themePresets = listOf(THEME_ENHANCED, THEME_DRAMATIC)
    
    /**
     * Get preset by name
     */
    fun getPreset(name: String): BrightnessPreset {
        return allPresets.find { it.name == name } ?: DEFAULT
    }
    
    /**
     * Get theme brightness profile by name
     */
    fun getThemeBrightnessProfile(name: String): ThemeBrightnessProfile {
        return ThemeBrightnessProfile.values().find { it.name.equals(name, ignoreCase = true) } 
            ?: ThemeBrightnessProfile.BALANCED
    }
    
    /**
     * Get presets suitable for basic color tint mode
     */
    fun getBasicColorPresets(): List<BrightnessPreset> {
        return corePresets
    }
    
    /**
     * Get presets suitable for advanced theme mode
     */
    fun getAdvancedThemePresets(): List<BrightnessPreset> {
        return allPresets
    }
    
    /**
     * Check if a preset is theme-specific
     */
    fun isThemeSpecific(presetName: String): Boolean {
        return themePresets.any { it.name == presetName }
    }
}

/**
 * Integration with existing MatrixColorTheme system
 * Provides brightness-aware theme application
 */
object ThemeBrightnessIntegration {
    
    /**
     * Apply brightness preset to a MatrixColorTheme
     * Returns modified colors with brightness adjustments
     */
    fun applyBrightnessToTheme(
        theme: MatrixColorTheme, 
        preset: BrightnessPreset
    ): MatrixColorTheme {
        return theme.copy(
            headColor = applyBrightnessToColor(theme.headColor, preset.leadBrightness, preset.leadAlpha),
            brightTrailColor = applyBrightnessToColor(theme.brightTrailColor, preset.brightTrailBrightness, preset.brightTrailAlpha),
            trailColor = applyBrightnessToColor(theme.trailColor, preset.trailBrightness, preset.trailAlpha),
            dimTrailColor = applyBrightnessToColor(theme.dimTrailColor, preset.dimTrailBrightness, preset.dimTrailAlpha)
        )
    }
    
    /**
     * Apply brightness and alpha multipliers to a color
     */
    private fun applyBrightnessToColor(
        color: Color, 
        brightnessMultiplier: Float, 
        alphaMultiplier: Float
    ): Color {
        val newAlpha = (color.alpha * alphaMultiplier).coerceIn(0f, 1f)
        val newRed = (color.red * brightnessMultiplier).coerceIn(0f, 1f)
        val newGreen = (color.green * brightnessMultiplier).coerceIn(0f, 1f)
        val newBlue = (color.blue * brightnessMultiplier).coerceIn(0f, 1f)
        
        return Color(
            red = newRed,
            green = newGreen,
            blue = newBlue,
            alpha = newAlpha
        )
    }
    
    /**
     * Get recommended brightness preset for a theme
     */
    fun getRecommendedPresetForTheme(themeName: String): BrightnessPreset {
        return when {
            themeName.contains("Classic", ignoreCase = true) -> BrightnessPresets.ENHANCED
            themeName.contains("Dramatic", ignoreCase = true) || 
            themeName.contains("Flame", ignoreCase = true) -> BrightnessPresets.DRAMATIC
            themeName.contains("Subtle", ignoreCase = true) || 
            themeName.contains("Snow", ignoreCase = true) -> BrightnessPresets.SUBTLE
            themeName.contains("Terminal", ignoreCase = true) || 
            themeName.contains("Print", ignoreCase = true) -> BrightnessPresets.UNIFORM
            else -> BrightnessPresets.THEME_ENHANCED
        }
    }
}
