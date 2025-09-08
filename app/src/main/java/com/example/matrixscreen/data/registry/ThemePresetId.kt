package com.example.matrixscreen.data.registry

/**
 * Value class for theme preset identifiers.
 * 
 * This provides type safety for theme preset IDs while maintaining
 * efficient storage as strings.
 */
@JvmInline
value class ThemePresetId(val value: String) {
    override fun toString(): String = value
}

/**
 * Registry interface for theme preset resolution.
 * 
 * This allows the system to resolve theme preset IDs to their
 * actual color configurations.
 */
interface ThemePresetRegistry {
    /**
     * Get the color configuration for a given theme preset ID.
     * 
     * @param id The theme preset ID
     * @return The color configuration for this theme preset
     */
    fun getColors(id: ThemePresetId): ThemeColorConfig
    
    /**
     * Get the display name for a given theme preset ID.
     * 
     * @param id The theme preset ID
     * @return The display name for this theme preset
     */
    fun getDisplayName(id: ThemePresetId): String
    
    /**
     * Check if a theme preset ID is valid.
     * 
     * @param id The theme preset ID
     * @return true if the ID is valid
     */
    fun isValid(id: ThemePresetId): Boolean
    
    /**
     * Get all available theme preset IDs.
     * 
     * @return List of all available theme preset IDs
     */
    fun getAllIds(): List<ThemePresetId>
}

/**
 * Color configuration for a theme preset.
 */
data class ThemeColorConfig(
    val backgroundColor: Long,
    val headColor: Long,
    val brightTrailColor: Long,
    val trailColor: Long,
    val dimColor: Long,
    val uiAccent: Long,
    val uiOverlayBg: Long,
    val uiSelectionBg: Long
)

/**
 * Built-in theme preset IDs.
 */
object BuiltInThemes {
    val MATRIX_GREEN = ThemePresetId("MATRIX_GREEN")
    val MATRIX_BLUE = ThemePresetId("MATRIX_BLUE")
    val MATRIX_RED = ThemePresetId("MATRIX_RED")
    val MATRIX_PURPLE = ThemePresetId("MATRIX_PURPLE")
    val MATRIX_ORANGE = ThemePresetId("MATRIX_ORANGE")
    val MATRIX_WHITE = ThemePresetId("MATRIX_WHITE")
    
    val ALL_BUILT_IN = listOf(
        MATRIX_GREEN,
        MATRIX_BLUE,
        MATRIX_RED,
        MATRIX_PURPLE,
        MATRIX_ORANGE,
        MATRIX_WHITE
    )
}
