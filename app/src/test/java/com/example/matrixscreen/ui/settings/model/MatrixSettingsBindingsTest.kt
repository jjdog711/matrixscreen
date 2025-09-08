package com.example.matrixscreen.ui.settings.model

import com.example.matrixscreen.data.custom.CustomSymbolSet
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for MatrixSettings binding functions
 */
class MatrixSettingsBindingsTest {
    
    @Test
    fun `get returns correct values for all SettingId types`() {
        val settings = com.example.matrixscreen.data.model.MatrixSettings(
            fallSpeed = 2.5f,
            columnCount = 100,
            lineSpacing = 0.8f,
            activePercentage = 0.3f,
            speedVariance = 0.02f,
            glowIntensity = 1.5f,
            jitterAmount = 1.0f,
            flickerAmount = 0.1f,
            mutationRate = 0.05f,
            grainDensity = 150,
            grainOpacity = 0.02f,
            targetFps = 90,
            backgroundColor = 0xFF111111L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            uiAccent = 0xFF00CC00L,
            uiOverlayBg = 0x80000000L,
            uiSelectionBg = 0x4000FF00L,
            advancedColorsEnabled = true,
            linkUiAndRainColors = false,
            fontSize = 16,
            symbolSetId = "MATRIX_BINARY",
            savedCustomSets = listOf(
                CustomSymbolSet(id = "1", name = "Test Set", characters = "ABC")
            ),
            activeCustomSetId = "1",
            maxTrailLength = 80,
            maxBrightTrailLength = 12,
            themePresetId = "MATRIX_BLUE",
            columnStartDelay = 0.02f,
            columnRestartDelay = 0.8f
        )
        
        // Test motion settings
        assertEquals(2.5f, settings.get(Speed))
        assertEquals(100, settings.get(Columns))
        assertEquals(0.8f, settings.get(LineSpace))
        assertEquals(0.3f, settings.get(ActivePct))
        assertEquals(0.02f, settings.get(SpeedVar))
        
        // Test effects settings
        assertEquals(1.5f, settings.get(Glow))
        assertEquals(1.0f, settings.get(Jitter))
        assertEquals(0.1f, settings.get(Flicker))
        assertEquals(0.05f, settings.get(Mutation))
        
        // Test background settings
        assertEquals(150, settings.get(GrainD))
        assertEquals(0.02f, settings.get(GrainO))
        assertEquals(90, settings.get(Fps))
        
        // Test color settings
        assertEquals(0xFF111111L, settings.get(BgColor))
        assertEquals(0xFF00FF00L, settings.get(HeadColor))
        assertEquals(0xFF00CC00L, settings.get(BrightColor))
        assertEquals(0xFF008800L, settings.get(TrailColor))
        assertEquals(0xFF004400L, settings.get(DimColor))
        
        // Test UI theme colors
        assertEquals(0xFF00CC00L, settings.get(UiAccent))
        assertEquals(0x80000000L, settings.get(UiOverlay))
        assertEquals(0x4000FF00L, settings.get(UiSelectBg))
        
        // Test advanced color system
        assertEquals(true, settings.get(AdvancedColorsEnabled))
        assertEquals(false, settings.get(LinkUiAndRainColors))
        
        // Test character settings
        assertEquals(16, settings.get(FontSize))
        
        // Test symbol set settings
        assertEquals("MATRIX_BINARY", settings.get(SymbolSetId))
        assertEquals(1, settings.get(SavedCustomSets).size)
        assertEquals("1", settings.get(ActiveCustomSetId))
        
        // Test trail length settings
        assertEquals(80, settings.get(MaxTrailLength))
        assertEquals(12, settings.get(MaxBrightTrailLength))
        
        // Test theme preset settings
        assertEquals("MATRIX_BLUE", settings.get(ThemePresetId))
        
        // Test timing settings
        assertEquals(0.02f, settings.get(ColumnStartDelay))
        assertEquals(0.8f, settings.get(ColumnRestartDelay))
    }
    
    @Test
    fun `with creates new instance with updated values`() {
        val original = com.example.matrixscreen.data.model.MatrixSettings()
        val updated = original.with(Speed, 3.0f)
        
        assertEquals(2.0f, original.fallSpeed) // Original unchanged
        assertEquals(3.0f, updated.fallSpeed) // Updated value
        assertNotSame(original, updated) // Different instances
    }
    
    @Test
    fun `with updates multiple fields correctly`() {
        val original = com.example.matrixscreen.data.model.MatrixSettings()
        val updated = original
            .with(Speed, 3.0f)
            .with(Columns, 200)
            .with(Glow, 2.5f)
        
        assertEquals(3.0f, updated.fallSpeed)
        assertEquals(200, updated.columnCount)
        assertEquals(2.5f, updated.glowIntensity)
    }
    
    @Test
    fun `with handles nullable values correctly`() {
        val original = com.example.matrixscreen.data.model.MatrixSettings()
        val updated = original.with(ActiveCustomSetId, "test-id")
        
        assertEquals("test-id", updated.activeCustomSetId)
        
        val updatedNull = updated.with(ActiveCustomSetId, null)
        assertEquals(null, updatedNull.activeCustomSetId)
    }
    
    @Test
    fun `with handles list values correctly`() {
        val original = com.example.matrixscreen.data.model.MatrixSettings()
        val customSets = listOf(
            CustomSymbolSet(id = "1", name = "Test", characters = "ABC")
        )
        val updated = original.with(SavedCustomSets, customSets)
        
        assertEquals(1, updated.savedCustomSets.size)
        assertEquals("Test", updated.savedCustomSets[0].name)
    }
}
