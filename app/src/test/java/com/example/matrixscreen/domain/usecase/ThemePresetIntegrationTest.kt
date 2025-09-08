package com.example.matrixscreen.domain.usecase

import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.registry.BuiltInThemes
import com.example.matrixscreen.data.registry.ThemePresetId
import com.example.matrixscreen.data.registry.ThemePresetRegistryImpl
import org.junit.Test
import org.junit.Assert.*

/**
 * Integration test for the complete theme preset system.
 * 
 * Tests the full flow from theme selection to renderer parameters:
 * 1. Theme preset selection in UI
 * 2. Color resolution pipeline
 * 3. RendererParams mapping
 */
class ThemePresetIntegrationTest {
    
    private val themeRegistry = ThemePresetRegistryImpl()
    private val resolveColors = ResolveColors()
    private val mapSettingsToRendererParams = MapSettingsToRendererParams(resolveColors)
    
    @Test
    fun `theme preset system complete flow works correctly`() {
        // Step 1: Start with base settings (no preset)
        val baseSettings = MatrixSettings(
            backgroundColor = 0xFF111111L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            uiAccent = 0xFF00CC00L,
            uiOverlayBg = 0x80000000L,
            uiSelectionBg = 0x4000FF00L,
            themePresetId = null // No preset initially
        )
        
        // Step 2: Simulate theme preset selection (Matrix Blue)
        val selectedPreset = BuiltInThemes.MATRIX_BLUE
        val settingsWithPreset = baseSettings.copy(
            themePresetId = selectedPreset.value
        )
        
        // Step 3: Verify theme registry can resolve the preset
        val presetColors = themeRegistry.getColors(selectedPreset)
        assertEquals("Matrix Blue", themeRegistry.getDisplayName(selectedPreset))
        assertEquals(0xFF000000L, presetColors.backgroundColor)
        assertEquals(0xFF0080FFL, presetColors.headColor)
        assertEquals(0xFF0066CCL, presetColors.brightTrailColor)
        assertEquals(0xFF004499L, presetColors.trailColor)
        assertEquals(0xFF002266L, presetColors.dimColor)
        assertEquals(0xFF0066CCL, presetColors.uiAccent)
        assertEquals(0x80000000L, presetColors.uiOverlayBg)
        assertEquals(0x400080FFL, presetColors.uiSelectionBg)
        
        // Step 4: Verify color resolution pipeline applies the preset
        val resolvedColors = resolveColors.execute(settingsWithPreset)
        assertEquals(0xFF000000L, resolvedColors.backgroundColor) // From Matrix Blue preset
        assertEquals(0xFF0080FFL, resolvedColors.headColor) // From Matrix Blue preset
        assertEquals(0xFF0066CCL, resolvedColors.brightTrailColor) // From Matrix Blue preset
        assertEquals(0xFF004499L, resolvedColors.trailColor) // From Matrix Blue preset
        assertEquals(0xFF002266L, resolvedColors.dimColor) // From Matrix Blue preset
        assertEquals(0xFF0066CCL, resolvedColors.uiAccent) // From Matrix Blue preset
        assertEquals(0x80000000L, resolvedColors.uiOverlayBg) // From Matrix Blue preset
        assertEquals(0x400080FFL, resolvedColors.uiSelectionBg) // From Matrix Blue preset
        
        // Step 5: Verify RendererParams gets the resolved colors
        val rendererParams = mapSettingsToRendererParams.execute(settingsWithPreset, 60f)
        assertEquals(0xFF000000L, rendererParams.backgroundColor) // Resolved from preset
        assertEquals(0xFF0080FFL, rendererParams.headColor) // Resolved from preset
        assertEquals(0xFF0066CCL, rendererParams.brightTrailColor) // Resolved from preset
        assertEquals(0xFF004499L, rendererParams.trailColor) // Resolved from preset
        assertEquals(0xFF002266L, rendererParams.dimColor) // Resolved from preset
        assertEquals(0xFF0066CCL, rendererParams.uiAccent) // Resolved from preset
        assertEquals(0x80000000L, rendererParams.uiOverlayBg) // Resolved from preset
        assertEquals(0x400080FFL, rendererParams.uiSelectionBg) // Resolved from preset
    }
    
    @Test
    fun `theme preset with UI Rain linking works correctly`() {
        // Test theme preset + UI/Rain linking combination
        val settings = MatrixSettings(
            backgroundColor = 0xFF111111L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            uiAccent = 0xFF0000FFL, // Will be overridden by linking
            uiOverlayBg = 0x80000000L,
            uiSelectionBg = 0x40000000L, // Will be overridden by linking
            themePresetId = "MATRIX_GREEN", // Apply Matrix Green preset
            advancedColorsEnabled = false,
            linkUiAndRainColors = true // Enable UI/Rain linking
        )
        
        val resolvedColors = resolveColors.execute(settings)
        
        // Should first apply Matrix Green preset, then apply UI/Rain linking
        assertEquals(0xFF000000L, resolvedColors.backgroundColor) // From Matrix Green preset
        assertEquals(0xFF00FF00L, resolvedColors.headColor) // From Matrix Green preset
        assertEquals(0xFF00CC00L, resolvedColors.brightTrailColor) // From Matrix Green preset
        assertEquals(0xFF008800L, resolvedColors.trailColor) // From Matrix Green preset
        assertEquals(0xFF004400L, resolvedColors.dimColor) // From Matrix Green preset
        assertEquals(0xFF00FF00L, resolvedColors.uiAccent) // From preset, then linked to headColor
        assertEquals(0x80000000L, resolvedColors.uiOverlayBg) // From Matrix Green preset
        assertEquals(0x4000FF00L, resolvedColors.uiSelectionBg) // From preset, then linked to headColor
    }
    
    @Test
    fun `all built-in themes are properly configured`() {
        // Verify all built-in themes have proper configurations
        BuiltInThemes.ALL_BUILT_IN.forEach { themeId ->
            val colors = themeRegistry.getColors(themeId)
            val displayName = themeRegistry.getDisplayName(themeId)
            
            // Verify display name is not empty
            assertTrue("Display name should not be empty for $themeId", displayName.isNotEmpty())
            
            // Verify all colors are valid ARGB values
            assertTrue("Background color should be valid for $themeId", colors.backgroundColor > 0)
            assertTrue("Head color should be valid for $themeId", colors.headColor > 0)
            assertTrue("Bright trail color should be valid for $themeId", colors.brightTrailColor > 0)
            assertTrue("Trail color should be valid for $themeId", colors.trailColor > 0)
            assertTrue("Dim color should be valid for $themeId", colors.dimColor > 0)
            assertTrue("UI accent color should be valid for $themeId", colors.uiAccent > 0)
            assertTrue("UI overlay background should be valid for $themeId", colors.uiOverlayBg > 0)
            assertTrue("UI selection background should be valid for $themeId", colors.uiSelectionBg > 0)
            
            // Verify color hierarchy (head > bright > trail > dim)
            assertTrue("Head color should be brightest for $themeId", 
                colors.headColor >= colors.brightTrailColor)
            assertTrue("Bright trail should be brighter than trail for $themeId", 
                colors.brightTrailColor >= colors.trailColor)
            assertTrue("Trail should be brighter than dim for $themeId", 
                colors.trailColor >= colors.dimColor)
        }
    }
    
    @Test
    fun `theme preset persistence works correctly`() {
        // Test that themePresetId is properly stored and retrieved
        val settings = MatrixSettings(
            themePresetId = "MATRIX_PURPLE"
        )
        
        assertEquals("MATRIX_PURPLE", settings.themePresetId)
        
        // Verify the preset can be resolved
        val themeId = ThemePresetId(settings.themePresetId!!)
        val colors = themeRegistry.getColors(themeId)
        
        // Matrix Purple should have purple colors
        assertEquals(0xFF8000FFL, colors.headColor) // Purple head color
        assertEquals(0xFF6600CCL, colors.brightTrailColor) // Purple bright trail
        assertEquals(0xFF4D0099L, colors.trailColor) // Purple trail
        assertEquals(0xFF330066L, colors.dimColor) // Purple dim
    }
}
