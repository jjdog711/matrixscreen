package com.example.matrixscreen.core.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.matrixscreen.data.model.MatrixSettings

/**
 * Utility functions for linking UI and rain colors.
 * 
 * When the "Link UI & Rain colors" option is enabled, these utilities
 * automatically map rain colors to UI colors to maintain visual consistency.
 */

/**
 * Color mapping configuration for linking UI and rain colors.
 */
data class ColorLinkMapping(
    val rainHeadToUiAccent: Boolean = true,
    val rainBrightTrailToUiOverlay: Boolean = false,
    val rainTrailToUiSelection: Boolean = false,
    val backgroundColorIndependent: Boolean = true
)

/**
 * Default color link mapping configuration.
 */
val DEFAULT_COLOR_LINK_MAPPING = ColorLinkMapping(
    rainHeadToUiAccent = true,
    rainBrightTrailToUiOverlay = false,
    rainTrailToUiSelection = false,
    backgroundColorIndependent = true
)

/**
 * Apply color linking to settings when the link is enabled.
 * 
 * @param settings Current settings
 * @param mapping Color link mapping configuration
 * @return Updated settings with linked colors
 */
fun applyColorLinking(settings: MatrixSettings, mapping: ColorLinkMapping = DEFAULT_COLOR_LINK_MAPPING): MatrixSettings {
    if (!settings.linkUiAndRainColors) {
        return settings
    }
    
    return settings.copy(
        uiAccent = if (mapping.rainHeadToUiAccent) settings.headColor else settings.uiAccent,
        uiOverlayBg = if (mapping.rainBrightTrailToUiOverlay) settings.brightTrailColor else settings.uiOverlayBg,
        uiSelectionBg = if (mapping.rainTrailToUiSelection) settings.trailColor else settings.uiSelectionBg
    )
}

/**
 * Get the effective UI accent color considering color linking.
 * 
 * @param settings Current settings
 * @return Effective UI accent color
 */
fun getEffectiveUiAccentColor(settings: MatrixSettings): Long {
    return if (settings.linkUiAndRainColors) {
        settings.headColor
    } else {
        settings.uiAccent
    }
}

/**
 * Get the effective UI overlay background color considering color linking.
 * 
 * @param settings Current settings
 * @return Effective UI overlay background color
 */
fun getEffectiveUiOverlayColor(settings: MatrixSettings): Long {
    return if (settings.linkUiAndRainColors) {
        // Use a dimmed version of the bright trail color for overlay
        dimColor(settings.brightTrailColor, 0.3f)
    } else {
        settings.uiOverlayBg
    }
}

/**
 * Get the effective UI selection background color considering color linking.
 * 
 * @param settings Current settings
 * @return Effective UI selection background color
 */
fun getEffectiveUiSelectionColor(settings: MatrixSettings): Long {
    return if (settings.linkUiAndRainColors) {
        // Use a dimmed version of the trail color for selection
        dimColor(settings.trailColor, 0.4f)
    } else {
        settings.uiSelectionBg
    }
}

/**
 * Dim a color by reducing its alpha or brightness.
 * 
 * @param color Color to dim (as Long ARGB)
 * @param factor Dimming factor (0.0 = transparent, 1.0 = no change)
 * @return Dimmed color
 */
private fun dimColor(color: Long, factor: Float): Long {
    val argb = color.toInt()
    val alpha = ((argb shr 24) and 0xFF) * factor
    val red = (argb shr 16) and 0xFF
    val green = (argb shr 8) and 0xFF
    val blue = argb and 0xFF
    
    return ((alpha.toInt() shl 24) or (red shl 16) or (green shl 8) or blue).toLong()
}

/**
 * Check if color linking would create any contrast issues.
 * 
 * @param settings Current settings
 * @return List of contrast warnings
 */
fun checkColorLinkContrast(settings: MatrixSettings): List<String> {
    val warnings = mutableListOf<String>()
    
    if (!settings.linkUiAndRainColors) {
        return warnings
    }
    
    val backgroundColor = Color(settings.backgroundColor)
    val uiAccentColor = Color(getEffectiveUiAccentColor(settings))
    val uiOverlayColor = Color(getEffectiveUiOverlayColor(settings))
    val uiSelectionColor = Color(getEffectiveUiSelectionColor(settings))
    
    // Check UI accent contrast
    if (!isColorCombinationSafe(uiAccentColor, backgroundColor, 3.0)) {
        warnings.add("UI accent color may have poor contrast against background")
    }
    
    // Check UI overlay contrast
    if (!isColorCombinationSafe(uiOverlayColor, backgroundColor, 2.0)) {
        warnings.add("UI overlay color may have poor contrast against background")
    }
    
    // Check UI selection contrast
    if (!isColorCombinationSafe(uiSelectionColor, backgroundColor, 2.0)) {
        warnings.add("UI selection color may have poor contrast against background")
    }
    
    return warnings
}

/**
 * Get suggested color adjustments for better contrast when linking is enabled.
 * 
 * @param settings Current settings
 * @return Map of suggested color adjustments
 */
fun getSuggestedColorAdjustments(settings: MatrixSettings): Map<String, Long> {
    val adjustments = mutableMapOf<String, Long>()
    
    if (!settings.linkUiAndRainColors) {
        return adjustments
    }
    
    val backgroundColor = Color(settings.backgroundColor)
    
    // Suggest adjustments for UI colors
    val currentUiAccent = Color(getEffectiveUiAccentColor(settings))
    val adjustedUiAccent = adjustColorForContrast(currentUiAccent, backgroundColor, 3.0)
    if (adjustedUiAccent != currentUiAccent) {
        adjustments["uiAccent"] = adjustedUiAccent.toArgb().toLong()
    }
    
    val currentUiOverlay = Color(getEffectiveUiOverlayColor(settings))
    val adjustedUiOverlay = adjustColorForContrast(currentUiOverlay, backgroundColor, 2.0)
    if (adjustedUiOverlay != currentUiOverlay) {
        adjustments["uiOverlay"] = adjustedUiOverlay.toArgb().toLong()
    }
    
    val currentUiSelection = Color(getEffectiveUiSelectionColor(settings))
    val adjustedUiSelection = adjustColorForContrast(currentUiSelection, backgroundColor, 2.0)
    if (adjustedUiSelection != currentUiSelection) {
        adjustments["uiSelection"] = adjustedUiSelection.toArgb().toLong()
    }
    
    return adjustments
}

/**
 * Apply automatic color adjustments to improve contrast.
 * 
 * @param settings Current settings
 * @return Settings with improved contrast
 */
fun applyAutomaticContrastAdjustments(settings: MatrixSettings): MatrixSettings {
    if (!settings.linkUiAndRainColors) {
        return settings
    }
    
    val adjustments = getSuggestedColorAdjustments(settings)
    
    return settings.copy(
        uiAccent = adjustments["uiAccent"] ?: settings.uiAccent,
        uiOverlayBg = adjustments["uiOverlay"] ?: settings.uiOverlayBg,
        uiSelectionBg = adjustments["uiSelection"] ?: settings.uiSelectionBg
    )
}
