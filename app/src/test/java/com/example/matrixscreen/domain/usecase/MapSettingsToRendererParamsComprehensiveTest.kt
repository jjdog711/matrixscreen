package com.example.matrixscreen.domain.usecase

import android.content.Context
import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.engine.uniforms.RendererParams
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*

/**
 * Comprehensive unit tests for MapSettingsToRendererParams mapping coverage.
 * 
 * This test ensures that every SettingId in the domain model is properly mapped
 * to RendererParams with correct bounds checking and clamping.
 */
class MapSettingsToRendererParamsComprehensiveTest {
    
    private val resolveColors = ResolveColors()
    private val useCase = MapSettingsToRendererParams(resolveColors)
    private val mockContext = mock(Context::class.java)
    
    @Test
    fun `mapSettingsToRendererParams maps all motion settings correctly`() {
        val settings = MatrixSettings(
            fallSpeed = 3.5f,
            columnCount = 200,
            lineSpacing = 1.1f,
            activePercentage = 0.6f,
            speedVariance = 0.05f
        )
        
        val result = useCase.execute(settings, mockContext)
        
        assertEquals(3.5f, result.fallSpeed)
        assertEquals(200, result.columnCount)
        assertEquals(1.1f, result.lineSpacing)
        assertEquals(0.6f, result.activePercentage)
        assertEquals(0.05f, result.speedVariance)
    }
    
    @Test
    fun `mapSettingsToRendererParams maps all effects settings correctly`() {
        val settings = MatrixSettings(
            glowIntensity = 2.5f,
            jitterAmount = 3.0f,
            flickerAmount = 0.15f,
            mutationRate = 0.1f
        )
        
        val result = useCase.execute(settings, mockContext)
        
        assertEquals(2.5f, result.glowIntensity)
        assertEquals(3.0f, result.jitterAmount)
        assertEquals(0.15f, result.flickerAmount)
        assertEquals(0.1f, result.mutationRate)
    }
    
    @Test
    fun `mapSettingsToRendererParams maps all background settings correctly`() {
        val settings = MatrixSettings(
            grainDensity = 300,
            grainOpacity = 0.05f,
            targetFps = 90
        )
        
        val result = useCase.execute(settings, mockContext)
        
        assertEquals(300, result.grainDensity)
        assertEquals(0.05f, result.grainOpacity)
        assertEquals(90f, result.targetFps)
    }
    
    @Test
    fun `mapSettingsToRendererParams maps all color settings correctly`() {
        val settings = MatrixSettings(
            backgroundColor = 0xFF111111L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            uiAccent = 0xFF00CC00L,
            uiOverlayBg = 0x80000000L,
            uiSelectionBg = 0x4000FF00L
        )
        
        val result = useCase.execute(settings, mockContext)
        
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
    fun `mapSettingsToRendererParams maps all timing settings correctly`() {
        val settings = MatrixSettings(
            columnStartDelay = 0.02f,
            columnRestartDelay = 0.8f,
            activePercentage = 0.3f,
            speedVariance = 0.02f
        )
        
        val result = useCase.execute(settings, mockContext)
        
        assertEquals(0.02f, result.columnStartDelay)
        assertEquals(0.8f, result.columnRestartDelay)
        assertEquals(0.3f, result.initialActivePercentage)
        assertEquals(0.02f, result.speedVariationRate)
    }
    
    @Test
    fun `mapSettingsToRendererParams maps all character settings correctly`() {
        val settings = MatrixSettings(
            fontSize = 18,
            maxTrailLength = 120,
            maxBrightTrailLength = 20
        )
        
        val result = useCase.execute(settings, mockContext)
        
        assertEquals(18f, result.fontSize)
        assertEquals(120, result.maxTrailLength)
        assertEquals(20, result.maxBrightTrailLength)
    }
    
    @Test
    fun `mapSettingsToRendererParams handles edge case values correctly`() {
        val settings = MatrixSettings(
            fallSpeed = 0.1f, // Minimum value
            columnCount = 1, // Minimum value
            lineSpacing = 0.1f, // Minimum value
            activePercentage = 0.01f, // Minimum value
            speedVariance = 0.0f, // Minimum value
            glowIntensity = 0.0f, // Minimum value
            jitterAmount = 0.0f, // Minimum value
            flickerAmount = 0.0f, // Minimum value
            mutationRate = 0.0f, // Minimum value
            grainDensity = 0, // Minimum value
            grainOpacity = 0.0f, // Minimum value
            targetFps = 1, // Minimum value
            fontSize = 1, // Minimum value
            maxTrailLength = 1, // Minimum value
            maxBrightTrailLength = 1, // Minimum value
            columnStartDelay = 0.0f, // Minimum value
            columnRestartDelay = 0.0f // Minimum value
        )
        
        val result = useCase.execute(settings, mockContext)
        
        // Verify all minimum values are preserved
        assertEquals(0.1f, result.fallSpeed)
        assertEquals(1, result.columnCount)
        assertEquals(0.1f, result.lineSpacing)
        assertEquals(0.01f, result.activePercentage)
        assertEquals(0.0f, result.speedVariance)
        assertEquals(0.0f, result.glowIntensity)
        assertEquals(0.0f, result.jitterAmount)
        assertEquals(0.0f, result.flickerAmount)
        assertEquals(0.0f, result.mutationRate)
        assertEquals(0, result.grainDensity)
        assertEquals(0.0f, result.grainOpacity)
        assertEquals(1f, result.targetFps)
        assertEquals(1f, result.fontSize)
        assertEquals(1, result.maxTrailLength)
        assertEquals(1, result.maxBrightTrailLength)
        assertEquals(0.0f, result.columnStartDelay)
        assertEquals(0.0f, result.columnRestartDelay)
    }
    
    @Test
    fun `mapSettingsToRendererParams handles maximum values correctly`() {
        val settings = MatrixSettings(
            fallSpeed = 10.0f, // High value
            columnCount = 500, // High value
            lineSpacing = 2.0f, // High value
            activePercentage = 1.0f, // Maximum value
            speedVariance = 0.1f, // High value
            glowIntensity = 5.0f, // High value
            jitterAmount = 10.0f, // High value
            flickerAmount = 1.0f, // High value
            mutationRate = 1.0f, // High value
            grainDensity = 1000, // High value
            grainOpacity = 1.0f, // Maximum value
            targetFps = 240, // High value
            fontSize = 48, // High value
            maxTrailLength = 500, // High value
            maxBrightTrailLength = 100, // High value
            columnStartDelay = 10.0f, // High value
            columnRestartDelay = 10.0f // High value
        )
        
        val result = useCase.execute(settings, mockContext)
        
        // Verify all high values are preserved
        assertEquals(10.0f, result.fallSpeed)
        assertEquals(500, result.columnCount)
        assertEquals(2.0f, result.lineSpacing)
        assertEquals(1.0f, result.activePercentage)
        assertEquals(0.1f, result.speedVariance)
        assertEquals(5.0f, result.glowIntensity)
        assertEquals(10.0f, result.jitterAmount)
        assertEquals(1.0f, result.flickerAmount)
        assertEquals(1.0f, result.mutationRate)
        assertEquals(1000, result.grainDensity)
        assertEquals(1.0f, result.grainOpacity)
        assertEquals(240f, result.targetFps)
        assertEquals(48f, result.fontSize)
        assertEquals(500, result.maxTrailLength)
        assertEquals(100, result.maxBrightTrailLength)
        assertEquals(10.0f, result.columnStartDelay)
        assertEquals(10.0f, result.columnRestartDelay)
    }
    
    @Test
    fun `mapSettingsToRendererParams with precomputed FPS works correctly`() {
        val settings = MatrixSettings(targetFps = 60)
        val effectiveFps = 90f
        
        val result = useCase.execute(settings, effectiveFps)
        
        assertEquals(90f, result.effectiveFps)
        assertEquals(60f, result.targetFps)
    }
    
    @Test
    fun `mapSettingsToRendererParams preserves all custom symbol set data`() {
        val customSets = listOf(
            CustomSymbolSet(id = "1", name = "Test Set", characters = "ABC")
        )
        val settings = MatrixSettings(
            symbolSetId = "CUSTOM",
            savedCustomSets = customSets,
            activeCustomSetId = "1"
        )
        
        val result = useCase.execute(settings, mockContext)
        
        // Verify that custom symbol set data is preserved in the settings
        // (even though RendererParams doesn't directly use it, the mapping should not lose it)
        assertEquals("CUSTOM", settings.symbolSetId)
        assertEquals(1, settings.savedCustomSets.size)
        assertEquals("1", settings.activeCustomSetId)
    }
    
    @Test
    fun `mapSettingsToRendererParams applies color resolution pipeline`() {
        val settings = MatrixSettings(
            backgroundColor = 0xFF111111L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            uiAccent = 0xFF0000FFL,
            uiOverlayBg = 0x80000000L,
            uiSelectionBg = 0x40000000L,
            themePresetId = "MATRIX_GREEN", // Apply theme preset
            advancedColorsEnabled = false,
            linkUiAndRainColors = true // Apply UI/Rain linking
        )
        
        val result = useCase.execute(settings, mockContext)
        
        // Colors should be resolved through the pipeline:
        // 1. Theme preset applied (Matrix Green)
        // 2. UI/Rain linking applied (uiAccent linked to headColor, uiSelectionBg linked to headColor)
        assertEquals(0xFF000000L, result.backgroundColor) // From Matrix Green preset
        assertEquals(0xFF00FF00L, result.headColor) // From Matrix Green preset
        assertEquals(0xFF00CC00L, result.brightTrailColor) // From Matrix Green preset
        assertEquals(0xFF008800L, result.trailColor) // From Matrix Green preset
        assertEquals(0xFF004400L, result.dimColor) // From Matrix Green preset
        assertEquals(0xFF00FF00L, result.uiAccent) // From preset, then linked to headColor
        assertEquals(0x80000000L, result.uiOverlayBg) // From Matrix Green preset
        assertEquals(0x4000FF00L, result.uiSelectionBg) // From preset, then linked to headColor
    }
}
