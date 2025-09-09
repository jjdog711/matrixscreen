package com.example.matrixscreen.data.model

import com.example.matrixscreen.data.MatrixColor
import com.example.matrixscreen.data.SymbolSet
import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.registry.SymbolSetId
import java.util.UUID

/**
 * Extension functions to provide legacy compatibility for MatrixSettings
 * This allows legacy components to access fields that don't exist in the domain model
 */
fun MatrixSettings.getSymbolSet(): SymbolSet {
    return when (this.symbolSetId) {
        "MATRIX_AUTHENTIC" -> SymbolSet.MATRIX_AUTHENTIC
        "MATRIX_GLITCH" -> SymbolSet.MATRIX_GLITCH
        "BINARY" -> SymbolSet.BINARY
        "HEX" -> SymbolSet.HEX
        "MIXED" -> SymbolSet.MIXED
        "LATIN" -> SymbolSet.LATIN
        "KATAKANA" -> SymbolSet.KATAKANA
        "NUMBERS" -> SymbolSet.NUMBERS
        "CUSTOM" -> SymbolSet.CUSTOM
        else -> SymbolSet.MATRIX_AUTHENTIC // Default fallback
    }
}

fun MatrixSettings.getColorTint(): MatrixColor = MatrixColor.GREEN // Default for now

fun MatrixSettings.getRowHeightMultiplier(): Float = this.lineSpacing

fun MatrixSettings.getMaxTrailLength(): Int = this.maxTrailLength

fun MatrixSettings.getMaxBrightTrailLength(): Int = this.maxBrightTrailLength

fun MatrixSettings.getFlickerRate(): Float = this.flickerAmount

fun MatrixSettings.getColumnStartDelay(): Float = this.columnStartDelay

fun MatrixSettings.getColumnRestartDelay(): Float = this.columnRestartDelay

fun MatrixSettings.getInitialActivePercentage(): Float = this.activePercentage

fun MatrixSettings.getSpeedVariationRate(): Float = this.speedVariance

fun MatrixSettings.getSavedCustomSets(): List<CustomSymbolSet> {
    // Return domain CustomSymbolSet directly (no legacy conversion needed)
    return this.savedCustomSets
}

fun MatrixSettings.getActiveCustomSetId(): String? = this.activeCustomSetId

fun MatrixSettings.getAdvancedColorsEnabled(): Boolean = this.advancedColorsEnabled

fun MatrixSettings.getSelectedThemeName(): String? = null // Default null

fun MatrixSettings.getBrightnessControlsEnabled(): Boolean = false // Default false

fun MatrixSettings.getLeadBrightnessMultiplier(): Float = 1.0f // Default 1.0

fun MatrixSettings.getBrightTrailBrightnessMultiplier(): Float = 1.0f // Default 1.0

fun MatrixSettings.getTrailBrightnessMultiplier(): Float = 1.0f // Default 1.0

fun MatrixSettings.getDimTrailBrightnessMultiplier(): Float = 1.0f // Default 1.0

fun MatrixSettings.getLeadAlphaMultiplier(): Float = 1.0f // Default 1.0

fun MatrixSettings.getBrightTrailAlphaMultiplier(): Float = 1.0f // Default 1.0

fun MatrixSettings.getTrailAlphaMultiplier(): Float = 1.0f // Default 1.0

fun MatrixSettings.getDimTrailAlphaMultiplier(): Float = 1.0f // Default 1.0

fun MatrixSettings.getBrightnessPreset(): String = "default" // Default preset

fun MatrixSettings.getThemeBrightnessProfile(): String = "balanced" // Default profile

// Legacy color field accessors
fun MatrixSettings.getRainHeadColor(): Long = this.headColor

fun MatrixSettings.getRainBrightTrailColor(): Long = this.brightTrailColor

fun MatrixSettings.getRainTrailColor(): Long = this.trailColor

fun MatrixSettings.getRainDimTrailColor(): Long = this.dimColor

fun MatrixSettings.getUiColor(): Long = this.uiAccent

fun MatrixSettings.getEffectiveBackgroundColor(): Long = this.backgroundColor

fun MatrixSettings.getEffectiveUiColor(): Long = this.uiAccent

fun MatrixSettings.getEffectivePrimaryColor(): Long = this.uiAccent

// Legacy conversion function removed - no longer needed
