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
                uiAccent = 0xFF00CC00L,
                uiOverlayBg = 0x80000000L,
                uiSelectionBg = 0x4000FF00L
            )
            "MATRIX_BLUE" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFF0080FFL,
                brightTrailColor = 0xFF0066CCL,
                trailColor = 0xFF004499L,
                dimColor = 0xFF002266L,
                uiAccent = 0xFF0066CCL,
                uiOverlayBg = 0x80000000L,
                uiSelectionBg = 0x400080FFL
            )
            "MATRIX_RED" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFFFF0000L,
                brightTrailColor = 0xFFCC0000L,
                trailColor = 0xFF990000L,
                dimColor = 0xFF660000L,
                uiAccent = 0xFFCC0000L,
                uiOverlayBg = 0x80000000L,
                uiSelectionBg = 0x40FF0000L
            )
            "MATRIX_PURPLE" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFF8000FFL,
                brightTrailColor = 0xFF6600CCL,
                trailColor = 0xFF4D0099L,
                dimColor = 0xFF330066L,
                uiAccent = 0xFF6600CCL,
                uiOverlayBg = 0x80000000L,
                uiSelectionBg = 0x408000FFL
            )
            "MATRIX_ORANGE" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFFFF8000L,
                brightTrailColor = 0xFFCC6600L,
                trailColor = 0xFF994D00L,
                dimColor = 0xFF663300L,
                uiAccent = 0xFFCC6600L,
                uiOverlayBg = 0x80000000L,
                uiSelectionBg = 0x40FF8000L
            )
            "MATRIX_WHITE" -> ThemeColorConfig(
                backgroundColor = 0xFF000000L,
                headColor = 0xFFFFFFFFL,
                brightTrailColor = 0xFFCCCCCCL,
                trailColor = 0xFF999999L,
                dimColor = 0xFF666666L,
                uiAccent = 0xFFCCCCCCL,
                uiOverlayBg = 0x80000000L,
                uiSelectionBg = 0x40FFFFFFL
            )
            else -> getColors(BuiltInThemes.MATRIX_GREEN) // Default fallback
        }
    }
    
    override fun getDisplayName(id: ThemePresetId): String {
        return when (id.value) {
            "MATRIX_GREEN" -> "Matrix Green"
            "MATRIX_BLUE" -> "Matrix Blue"
            "MATRIX_RED" -> "Matrix Red"
            "MATRIX_PURPLE" -> "Matrix Purple"
            "MATRIX_ORANGE" -> "Matrix Orange"
            "MATRIX_WHITE" -> "Matrix White"
            else -> "Unknown"
        }
    }
    
    override fun isValid(id: ThemePresetId): Boolean {
        return when (id.value) {
            "MATRIX_GREEN", "MATRIX_BLUE", "MATRIX_RED", 
            "MATRIX_PURPLE", "MATRIX_ORANGE", "MATRIX_WHITE" -> true
            else -> false
        }
    }
    
    override fun getAllIds(): List<ThemePresetId> {
        return BuiltInThemes.ALL_BUILT_IN
    }
}
