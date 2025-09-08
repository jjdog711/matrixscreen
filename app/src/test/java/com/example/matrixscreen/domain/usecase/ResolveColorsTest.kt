package com.example.matrixscreen.domain.usecase

import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.registry.BuiltInThemes
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for ResolveColors use case.
 * 
 * Tests the color resolution pipeline including theme presets,
 * advanced colors, and UI/Rain color linking.
 */
class ResolveColorsTest {
    
    private val resolveColors = ResolveColors()
    
    @Test
    fun `resolveColors returns base colors when no presets or advanced logic applied`() {
        val settings = MatrixSettings(
            backgroundColor = 0xFF111111L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            uiAccent = 0xFF00CC00L,
            uiOverlayBg = 0x80000000L,
            uiSelectionBg = 0x4000FF00L,
            themePresetId = null,
            advancedColorsEnabled = false,
            linkUiAndRainColors = false
        )
        
        val result = resolveColors.execute(settings)
        
        assertEquals(0xFF111111L, result.backgroundColor)
        assertEquals(0xFF00FF00L, result.headColor)
        assertEquals(0xFF00CC00L, result.brightTrailColor)
        assertEquals(0xFF008800L, result.trailColor)
        assertEquals(0xFF004400L, result.dimColor)
        assertEquals(0xFF00CC00L, result.uiAccent)
        assertEquals(0x80000000L, result.uiOverlayBg)
        assertEquals(0x4000FF00L, result.uiSelectionBg)
    }
    
    @Test
    fun `resolveColors applies theme preset when themePresetId is set`() {
        val settings = MatrixSettings(
            backgroundColor = 0xFF111111L, // Will be overridden by preset
            headColor = 0xFF00FF00L, // Will be overridden by preset
            brightTrailColor = 0xFF00CC00L, // Will be overridden by preset
            trailColor = 0xFF008800L, // Will be overridden by preset
            dimColor = 0xFF004400L, // Will be overridden by preset
            uiAccent = 0xFF00CC00L, // Will be overridden by preset
            uiOverlayBg = 0x80000000L, // Will be overridden by preset
            uiSelectionBg = 0x4000FF00L, // Will be overridden by preset
            themePresetId = "MATRIX_GREEN", // Apply Matrix Green preset
            advancedColorsEnabled = false,
            linkUiAndRainColors = false
        )
        
        val result = resolveColors.execute(settings)
        
        // Should use Matrix Green preset colors, not the base colors
        assertEquals(0xFF000000L, result.backgroundColor) // Matrix Green preset
        assertEquals(0xFF00FF00L, result.headColor) // Matrix Green preset
        assertEquals(0xFF00CC00L, result.brightTrailColor) // Matrix Green preset
        assertEquals(0xFF008800L, result.trailColor) // Matrix Green preset
        assertEquals(0xFF004400L, result.dimColor) // Matrix Green preset
        assertEquals(0xFF00CC00L, result.uiAccent) // Matrix Green preset
        assertEquals(0x80000000L, result.uiOverlayBg) // Matrix Green preset
        assertEquals(0x4000FF00L, result.uiSelectionBg) // Matrix Green preset
    }
    
    @Test
    fun `resolveColors applies UI Rain color linking when enabled`() {
        val settings = MatrixSettings(
            backgroundColor = 0xFF000000L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            uiAccent = 0xFF0000FFL, // Will be overridden by linking
            uiOverlayBg = 0x80000000L,
            uiSelectionBg = 0x40000000L, // Will be overridden by linking
            themePresetId = null,
            advancedColorsEnabled = false,
            linkUiAndRainColors = true // Enable UI/Rain linking
        )
        
        val result = resolveColors.execute(settings)
        
        // UI accent should be linked to head color
        assertEquals(0xFF00FF00L, result.uiAccent) // Linked to headColor
        // UI selection background should be 25% opacity of head color
        assertEquals(0x4000FF00L, result.uiSelectionBg) // 25% opacity of headColor
        
        // Other colors should remain unchanged
        assertEquals(0xFF000000L, result.backgroundColor)
        assertEquals(0xFF00FF00L, result.headColor)
        assertEquals(0xFF00CC00L, result.brightTrailColor)
        assertEquals(0xFF008800L, result.trailColor)
        assertEquals(0xFF004400L, result.dimColor)
        assertEquals(0x80000000L, result.uiOverlayBg)
    }
    
    @Test
    fun `resolveColors applies both theme preset and UI Rain linking`() {
        val settings = MatrixSettings(
            backgroundColor = 0xFF111111L, // Will be overridden by preset
            headColor = 0xFF00FF00L, // Will be overridden by preset
            brightTrailColor = 0xFF00CC00L, // Will be overridden by preset
            trailColor = 0xFF008800L, // Will be overridden by preset
            dimColor = 0xFF004400L, // Will be overridden by preset
            uiAccent = 0xFF0000FFL, // Will be overridden by preset, then by linking
            uiOverlayBg = 0x80000000L, // Will be overridden by preset
            uiSelectionBg = 0x40000000L, // Will be overridden by preset, then by linking
            themePresetId = "MATRIX_GREEN", // Apply Matrix Green preset
            advancedColorsEnabled = false,
            linkUiAndRainColors = true // Then apply UI/Rain linking
        )
        
        val result = resolveColors.execute(settings)
        
        // Should first apply Matrix Green preset, then apply UI/Rain linking
        assertEquals(0xFF000000L, result.backgroundColor) // From preset
        assertEquals(0xFF00FF00L, result.headColor) // From preset
        assertEquals(0xFF00CC00L, result.brightTrailColor) // From preset
        assertEquals(0xFF008800L, result.trailColor) // From preset
        assertEquals(0xFF004400L, result.dimColor) // From preset
        assertEquals(0xFF00FF00L, result.uiAccent) // From preset, then linked to headColor
        assertEquals(0x80000000L, result.uiOverlayBg) // From preset
        assertEquals(0x4000FF00L, result.uiSelectionBg) // From preset, then linked to headColor
    }
    
    @Test
    fun `resolveColors handles advanced colors enabled flag`() {
        val settings = MatrixSettings(
            backgroundColor = 0xFF000000L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            uiAccent = 0xFF00CC00L,
            uiOverlayBg = 0x80000000L,
            uiSelectionBg = 0x4000FF00L,
            themePresetId = null,
            advancedColorsEnabled = true, // Enable advanced colors
            linkUiAndRainColors = false
        )
        
        val result = resolveColors.execute(settings)
        
        // For now, advanced colors logic is a placeholder, so colors should be unchanged
        assertEquals(0xFF000000L, result.backgroundColor)
        assertEquals(0xFF00FF00L, result.headColor)
        assertEquals(0xFF00CC00L, result.brightTrailColor)
        assertEquals(0xFF008800L, result.trailColor)
        assertEquals(0xFF004400L, result.dimColor)
        assertEquals(0xFF00CC00L, result.uiAccent)
        assertEquals(0x80000000L, result.uiOverlayBg)
        assertEquals(0x4000FF00L, result.uiSelectionBg)
    }
    
    @Test
    fun `resolveColors handles unknown theme preset gracefully`() {
        val settings = MatrixSettings(
            backgroundColor = 0xFF111111L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            uiAccent = 0xFF00CC00L,
            uiOverlayBg = 0x80000000L,
            uiSelectionBg = 0x4000FF00L,
            themePresetId = "UNKNOWN_PRESET", // Unknown preset
            advancedColorsEnabled = false,
            linkUiAndRainColors = false
        )
        
        val result = resolveColors.execute(settings)
        
        // Should fall back to base colors when preset is unknown
        assertEquals(0xFF111111L, result.backgroundColor)
        assertEquals(0xFF00FF00L, result.headColor)
        assertEquals(0xFF00CC00L, result.brightTrailColor)
        assertEquals(0xFF008800L, result.trailColor)
        assertEquals(0xFF004400L, result.dimColor)
        assertEquals(0xFF00CC00L, result.uiAccent)
        assertEquals(0x80000000L, result.uiOverlayBg)
        assertEquals(0x4000FF00L, result.uiSelectionBg)
    }
}
