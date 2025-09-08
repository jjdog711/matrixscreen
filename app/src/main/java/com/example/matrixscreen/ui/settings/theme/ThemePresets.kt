package com.example.matrixscreen.ui.settings.theme

/**
 * Theme preset data class
 */
data class ThemePreset(
    val name: String,
    val description: String,
    val colors: Map<String, Long> // colorType -> color value
)

/**
 * Predefined theme presets for quick configuration
 */
object ThemePresets {
    
    val presets = listOf(
        ThemePreset(
            name = "Film-Accurate",
            description = "Authentic Matrix movie colors",
            colors = mapOf(
                "backgroundColor" to 0xFF000000L,
                "headColor" to 0xFF00FF00L,
                "brightTrailColor" to 0xFF00CC00L,
                "trailColor" to 0xFF008800L,
                "dimColor" to 0xFF004400L,
                "uiAccent" to 0xFF00CC00L,
                "uiOverlayBg" to 0x80000000L,
                "uiSelectionBg" to 0x4000FF00L
            )
        ),
        
        ThemePreset(
            name = "Neon",
            description = "Bright cyberpunk neon colors",
            colors = mapOf(
                "backgroundColor" to 0xFF000000L,
                "headColor" to 0xFF00FFFFL,
                "brightTrailColor" to 0xFF0080FFL,
                "trailColor" to 0xFF0040FFL,
                "dimColor" to 0xFF002080L,
                "uiAccent" to 0xFF00FFFFL,
                "uiOverlayBg" to 0x80000000L,
                "uiSelectionBg" to 0x4000FFFFL
            )
        ),
        
        ThemePreset(
            name = "Emerald",
            description = "Rich emerald green theme",
            colors = mapOf(
                "backgroundColor" to 0xFF000000L,
                "headColor" to 0xFF00FF80L,
                "brightTrailColor" to 0xFF00CC60L,
                "trailColor" to 0xFF009940L,
                "dimColor" to 0xFF006620L,
                "uiAccent" to 0xFF00CC60L,
                "uiOverlayBg" to 0x80000000L,
                "uiSelectionBg" to 0x4000FF80L
            )
        ),
        
        ThemePreset(
            name = "Monochrome",
            description = "Classic black and white",
            colors = mapOf(
                "backgroundColor" to 0xFF000000L,
                "headColor" to 0xFFFFFFFFL,
                "brightTrailColor" to 0xFFCCCCCCL,
                "trailColor" to 0xFF888888L,
                "dimColor" to 0xFF444444L,
                "uiAccent" to 0xFFFFFFFFL,
                "uiOverlayBg" to 0x80000000L,
                "uiSelectionBg" to 0x40FFFFFFL
            )
        ),
        
        ThemePreset(
            name = "Cyberpunk",
            description = "Purple and pink cyberpunk",
            colors = mapOf(
                "backgroundColor" to 0xFF000000L,
                "headColor" to 0xFFFF00FFL,
                "brightTrailColor" to 0xFF8000FFL,
                "trailColor" to 0xFF4000A0L,
                "dimColor" to 0xFF200050L,
                "uiAccent" to 0xFFFF00FFL,
                "uiOverlayBg" to 0x80000000L,
                "uiSelectionBg" to 0x40FF00FFL
            )
        ),
        
        ThemePreset(
            name = "Fire",
            description = "Red and orange fire theme",
            colors = mapOf(
                "backgroundColor" to 0xFF000000L,
                "headColor" to 0xFFFF4000L,
                "brightTrailColor" to 0xFFFF8000L,
                "trailColor" to 0xFFCC4000L,
                "dimColor" to 0xFF802000L,
                "uiAccent" to 0xFFFF4000L,
                "uiOverlayBg" to 0x80000000L,
                "uiSelectionBg" to 0x40FF4000L
            )
        )
    )
}
