package com.example.matrixscreen.data.registry

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ThemePresetRegistry that provides predefined color themes.
 */
@Singleton
class ThemePresetRegistryImpl @Inject constructor() : ThemePresetRegistry {
    
    override fun getColors(id: ThemePresetId): ThemeColorConfig {
        return when (id.value) {
            "MATRIX_GREEN" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFF00FF00L,
                brightTrailColor = 0xFF00CC00L,
                trailColor = 0xFF008800L,
                dimColor = 0xFF004400L,
                uiAccent = 0xFF00FF00L,
                uiOverlayBg = 0x26000000L,
                uiSelectionBg = 0x4000FF00L
            )
            "MATRIX_BLUE" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFF0080FFL,
                brightTrailColor = 0xFF0066CCL,
                trailColor = 0xFF004499L,
                dimColor = 0xFF002266L,
                uiAccent = 0xFF0066CCL,
                uiOverlayBg = 0x26000000L,
                uiSelectionBg = 0x400080FFL
            )
            "TRON_CYAN" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFF00F0FFL,
                brightTrailColor = 0xFF00C8FFL,
                trailColor = 0xFF00A0CCL,
                dimColor = 0xFF006B88L,
                uiAccent = 0xFF00E5FFL,
                uiOverlayBg = 0x26000000L,
                uiSelectionBg = 0x4000E5FFL
            )
            "MATRIX_PURPLE" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFF8000FFL,
                brightTrailColor = 0xFF6600CCL,
                trailColor = 0xFF4D0099L,
                dimColor = 0xFF330066L,
                uiAccent = 0xFF6600CCL,
                uiOverlayBg = 0x26000000L,
                uiSelectionBg = 0x408000FFL
            )
            "NEON_MAGENTA" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFFFF00FFL,
                brightTrailColor = 0xFFCC00CCL,
                trailColor = 0xFF990099L,
                dimColor = 0xFF660066L,
                uiAccent = 0xFFFF00FFL,
                uiOverlayBg = 0x26000000L,
                uiSelectionBg = 0x40FF00FFL
            )
            "CYBERPUNK_NEON" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFFFF2BD7L,
                brightTrailColor = 0xFF00E0FFL,
                trailColor = 0xFF00AACCL,
                dimColor = 0xFF005A6BL,
                uiAccent = 0xFFFF2BD7L,
                uiOverlayBg = 0x26000000L,
                uiSelectionBg = 0x40FF2BD7L
            )
            "HIGH_CONTRAST_WHITE" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFFFFFFFFL,
                brightTrailColor = 0xFFCCCCCCL,
                trailColor = 0xFF999999L,
                dimColor = 0xFF666666L,
                uiAccent = 0xFFFFFFFFL,
                uiOverlayBg = 0x33000000L,
                uiSelectionBg = 0x40FFFFFFL
            )
            "RETRO_AMBER" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFFFFBF00L,
                brightTrailColor = 0xFFE6A600L,
                trailColor = 0xFFCC8C00L,
                dimColor = 0xFF996600L,
                uiAccent = 0xFFFFBF00L,
                uiOverlayBg = 0x26000000L,
                uiSelectionBg = 0x40FFBF00L
            )
            "RETRO_PHOSPHOR_GREEN" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFF00FF41L,
                brightTrailColor = 0xFF00CC33L,
                trailColor = 0xFF009926L,
                dimColor = 0xFF00661AL,
                uiAccent = 0xFF00FF41L,
                uiOverlayBg = 0x26000000L,
                uiSelectionBg = 0x4000FF41L
            )
            else -> getColors(BuiltInThemes.MATRIX_GREEN) // Default fallback
        }
    }
    
    override fun getDisplayName(id: ThemePresetId): String {
        return when (id.value) {
            "MATRIX_GREEN" -> "Matrix Green"
            "MATRIX_BLUE" -> "Matrix Blue"
            "MATRIX_PURPLE" -> "Matrix Purple"
            "TRON_CYAN" -> "Tron Cyan"
            "NEON_MAGENTA" -> "Neon Magenta"
            "CYBERPUNK_NEON" -> "Cyberpunk Neon"
            "HIGH_CONTRAST_WHITE" -> "High Contrast White"
            "RETRO_AMBER" -> "Retro Amber"
            "RETRO_PHOSPHOR_GREEN" -> "Retro Phosphor Green"
            else -> "Unknown"
        }
    }
    
    override fun isValid(id: ThemePresetId): Boolean {
        return when (id.value) {
            "MATRIX_GREEN", "MATRIX_BLUE", "MATRIX_PURPLE",
            "TRON_CYAN", "NEON_MAGENTA", "CYBERPUNK_NEON",
            "HIGH_CONTRAST_WHITE", "RETRO_AMBER", "RETRO_PHOSPHOR_GREEN" -> true
            else -> false
        }
    }
    
    override fun getAllIds(): List<ThemePresetId> {
        return BuiltInThemes.ALL_BUILT_IN
    }
}
