package com.example.matrixscreen.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Typeface
import com.example.matrixscreen.R

/**
 * Simple font utility to replace the old font managers
 * Uses the new Typography system with Compose font families
 */
object FontUtils {
    
    /**
     * Get list of available font files (for compatibility with old font managers)
     */
    fun getAvailableFontFiles(): List<String> = listOf(
        "matrix_code_nfi.ttf",
        "space_grotesk_regular.ttf",
        "space_grotesk_medium.ttf", 
        "space_grotesk_semibold.ttf",
        "jetbrains_mono_light.ttf",
        "jetbrains_mono_regular.ttf",
        "cascadia_mono.ttf",
        "digital_7.ttf",
        "orbitron.ttf"
    )
    
    /**
     * Get display name for font file (for compatibility with old font managers)
     */
    fun getFontDisplayName(fontFileName: String): String = when (fontFileName) {
        "matrix_code_nfi.ttf" -> "Matrix Code NFI"
        "space_grotesk_regular.ttf" -> "Space Grotesk Regular"
        "space_grotesk_medium.ttf" -> "Space Grotesk Medium"
        "space_grotesk_semibold.ttf" -> "Space Grotesk SemiBold"
        "jetbrains_mono_light.ttf" -> "JetBrains Mono Light"
        "jetbrains_mono_regular.ttf" -> "JetBrains Mono Regular"
        "cascadia_mono.ttf" -> "Cascadia Mono"
        "digital_7.ttf" -> "Digital 7"
        "orbitron.ttf" -> "Orbitron"
        else -> fontFileName
    }
    
    /**
     * Get FontFamily for a given font file name
     */
    @Composable
    fun getFontFamily(fontFileName: String): FontFamily = when (fontFileName) {
        "matrix_code_nfi.ttf" -> MatrixCodeNFI
        "space_grotesk_regular.ttf" -> SpaceGrotesk
        "space_grotesk_medium.ttf" -> SpaceGrotesk
        "space_grotesk_semibold.ttf" -> SpaceGrotesk
        "jetbrains_mono_light.ttf" -> JetBrainsMono
        "jetbrains_mono_regular.ttf" -> JetBrainsMono
        "cascadia_mono.ttf" -> CascadiaMono
        "digital_7.ttf" -> Digital7
        "orbitron.ttf" -> Orbitron
        else -> FontFamily.Default
    }
    
    /**
     * Legacy compatibility method - returns null since we use FontFamily now
     * This is kept for compatibility during migration
     */
    fun getTypefaceForCustomSet(@Suppress("UNUSED_PARAMETER") fontFileName: String): Typeface? {
        // Return null - this method is deprecated in favor of getFontFamily
        return null
    }
}
