package com.example.matrixscreen.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for MatrixSettings domain model.
 * 
 * Tests default values, validation, and utility functions.
 */
class MatrixSettingsTest {
    
    @Test
    fun `default settings have valid values`() {
        // Given default settings
        val defaultSettings = MatrixSettings.DEFAULT
        
        // Then all values are within valid ranges
        assertTrue("Fall speed should be in valid range", defaultSettings.fallSpeed in 0.5f..5.0f)
        assertTrue("Column count should be in valid range", defaultSettings.columnCount in 50..200)
        assertTrue("Line spacing should be in valid range", defaultSettings.lineSpacing in 0.5f..2.0f)
        assertTrue("Active percentage should be in valid range", defaultSettings.activePercentage in 0.1f..1.0f)
        assertTrue("Speed variance should be in valid range", defaultSettings.speedVariance in 0.0f..0.1f)
        assertTrue("Glow intensity should be in valid range", defaultSettings.glowIntensity in 0.0f..3.0f)
        assertTrue("Jitter amount should be in valid range", defaultSettings.jitterAmount in 0.0f..5.0f)
        assertTrue("Flicker amount should be in valid range", defaultSettings.flickerAmount in 0.0f..1.0f)
        assertTrue("Mutation rate should be in valid range", defaultSettings.mutationRate in 0.0f..0.2f)
        assertTrue("Grain density should be in valid range", defaultSettings.grainDensity in 0..1000)
        assertTrue("Grain opacity should be in valid range", defaultSettings.grainOpacity in 0.0f..1.0f)
        assertTrue("Target FPS should be in valid range", defaultSettings.targetFps in 5..120)
        assertTrue("Font size should be in valid range", defaultSettings.fontSize in 8..32)
        
        // Color values should be valid ARGB
        assertTrue("Background color should be valid", defaultSettings.backgroundColor in 0x00000000L..0xFFFFFFFFL)
        assertTrue("Head color should be valid", defaultSettings.headColor in 0x00000000L..0xFFFFFFFFL)
        assertTrue("Bright trail color should be valid", defaultSettings.brightTrailColor in 0x00000000L..0xFFFFFFFFL)
        assertTrue("Trail color should be valid", defaultSettings.trailColor in 0x00000000L..0xFFFFFFFFL)
        assertTrue("Dim color should be valid", defaultSettings.dimColor in 0x00000000L..0xFFFFFFFFL)
        assertTrue("UI accent should be valid", defaultSettings.uiAccent in 0x00000000L..0xFFFFFFFFL)
        assertTrue("UI overlay background should be valid", defaultSettings.uiOverlayBg in 0x00000000L..0xFFFFFFFFL)
        assertTrue("UI selection background should be valid", defaultSettings.uiSelectionBg in 0x00000000L..0xFFFFFFFFL)
    }
    
    @Test
    fun `getEffectiveFps clamps to supported rates`() {
        // Given settings with various FPS values
        val settings60 = MatrixSettings.DEFAULT.copy(targetFps = 60)
        val settings90 = MatrixSettings.DEFAULT.copy(targetFps = 90)
        val settings120 = MatrixSettings.DEFAULT.copy(targetFps = 120)
        val settings200 = MatrixSettings.DEFAULT.copy(targetFps = 200) // Out of range
        
        val supportedRates = listOf(60, 90, 120)
        
        // When getting effective FPS
        val effective60 = settings60.getEffectiveFps(supportedRates)
        val effective90 = settings90.getEffectiveFps(supportedRates)
        val effective120 = settings120.getEffectiveFps(supportedRates)
        val effective200 = settings200.getEffectiveFps(supportedRates)
        
        // Then FPS is clamped to supported rates
        assertEquals(60, effective60)
        assertEquals(90, effective90)
        assertEquals(120, effective120)
        assertEquals(120, effective200) // Should clamp to nearest supported rate
    }
    
    @Test
    fun `getEffectiveFps uses default when no supported rates`() {
        // Given settings with no supported rates
        val settings = MatrixSettings.DEFAULT.copy(targetFps = 60)
        
        // When getting effective FPS with empty list
        val effectiveFps = settings.getEffectiveFps(emptyList())
        
        // Then default FPS is returned
        assertEquals(60, effectiveFps)
    }
    
    @Test
    fun `hasValidColors returns true for valid colors`() {
        // Given settings with valid colors
        val validSettings = MatrixSettings.DEFAULT
        
        // When checking if colors are valid
        val isValid = validSettings.hasValidColors()
        
        // Then it returns true
        assertTrue(isValid)
    }
    
    @Test
    fun `update modifies specified fields`() {
        // Given default settings
        val originalSettings = MatrixSettings.DEFAULT
        
        // When updating specific fields
        val updatedSettings = originalSettings.update(mapOf(
            "fallSpeed" to 3.0f,
            "columnCount" to 100,
            "targetFps" to 30
        ))
        
        // Then only specified fields are changed
        assertEquals(3.0f, updatedSettings.fallSpeed, 0.01f)
        assertEquals(100, updatedSettings.columnCount)
        assertEquals(30, updatedSettings.targetFps)
        
        // Other fields remain unchanged
        assertEquals(originalSettings.glowIntensity, updatedSettings.glowIntensity, 0.01f)
        assertEquals(originalSettings.backgroundColor, updatedSettings.backgroundColor)
        assertEquals(originalSettings.fontSize, updatedSettings.fontSize)
    }
    
    @Test
    fun `update ignores unknown keys`() {
        // Given default settings
        val originalSettings = MatrixSettings.DEFAULT
        
        // When updating with unknown key
        val updatedSettings = originalSettings.update(mapOf(
            "unknownKey" to "unknownValue",
            "fallSpeed" to 2.5f
        ))
        
        // Then only known fields are updated
        assertEquals(2.5f, updatedSettings.fallSpeed, 0.01f)
        assertEquals(originalSettings.columnCount, updatedSettings.columnCount)
    }
}
