package com.example.matrixscreen.data.repo

import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.model.toDomain
import com.example.matrixscreen.data.model.toProto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for SettingsRepository implementation.
 * 
 * Tests mapping and validation logic without DataStore complexity.
 */
class SettingsRepositoryTest {
    
    @Test
    fun `mapping clamps out-of-range values`() {
        // Given settings with out-of-range values
        val invalidSettings = MatrixSettings(
            fallSpeed = 10.0f, // Should be clamped to 5.0f
            columnCount = 500, // Should be clamped to 200
            targetFps = 200, // Should be clamped to 120
            glowIntensity = -1.0f, // Should be clamped to 0.0f
            backgroundColor = -1L // Should be clamped to valid range
        )
        
        // When converting to proto and back
        val proto = invalidSettings.toProto()
        val clampedSettings = proto.toDomain()
        
        // Then values are clamped to valid ranges
        assertTrue("Fall speed should be clamped", clampedSettings.fallSpeed <= 5.0f)
        assertTrue("Column count should be clamped", clampedSettings.columnCount <= 200)
        assertTrue("Target FPS should be clamped", clampedSettings.targetFps <= 120)
        assertTrue("Glow intensity should be clamped", clampedSettings.glowIntensity >= 0.0f)
        assertTrue("Background color should be valid", clampedSettings.backgroundColor in 0x00000000L..0xFFFFFFFFL)
    }
    
    @Test
    fun `round-trip conversion preserves valid values`() {
        // Given settings with valid values
        val originalSettings = MatrixSettings(
            fallSpeed = 2.5f,
            columnCount = 120,
            lineSpacing = 1.2f,
            activePercentage = 0.6f,
            speedVariance = 0.05f,
            glowIntensity = 1.8f,
            jitterAmount = 1.5f,
            flickerAmount = 0.15f,
            mutationRate = 0.1f,
            grainDensity = 300,
            grainOpacity = 0.05f,
            targetFps = 90,
            backgroundColor = 0xFF000080L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L,
            uiAccent = 0xFF00CC00L,
            uiOverlayBg = 0x80000000L,
            uiSelectionBg = 0x4000FF00L,
            fontSize = 16
        )
        
        // When converting to proto and back
        val proto = originalSettings.toProto()
        val convertedSettings = proto.toDomain()
        
        // Then values are preserved
        assertEquals(originalSettings, convertedSettings)
    }
    
    @Test
    fun `default settings are valid`() {
        // Given default settings
        val defaultSettings = MatrixSettings.DEFAULT
        
        // Then all values should be within valid ranges
        assertTrue("Fall speed should be valid", defaultSettings.fallSpeed in 0.5f..5.0f)
        assertTrue("Column count should be valid", defaultSettings.columnCount in 50..200)
        assertTrue("Line spacing should be valid", defaultSettings.lineSpacing in 0.5f..2.0f)
        assertTrue("Active percentage should be valid", defaultSettings.activePercentage in 0.1f..1.0f)
        assertTrue("Speed variance should be valid", defaultSettings.speedVariance in 0.0f..0.1f)
        assertTrue("Glow intensity should be valid", defaultSettings.glowIntensity in 0.0f..3.0f)
        assertTrue("Jitter amount should be valid", defaultSettings.jitterAmount in 0.0f..5.0f)
        assertTrue("Flicker amount should be valid", defaultSettings.flickerAmount in 0.0f..1.0f)
        assertTrue("Mutation rate should be valid", defaultSettings.mutationRate in 0.0f..0.2f)
        assertTrue("Grain density should be valid", defaultSettings.grainDensity in 0..1000)
        assertTrue("Grain opacity should be valid", defaultSettings.grainOpacity in 0.0f..1.0f)
        assertTrue("Target FPS should be valid", defaultSettings.targetFps in 5..120)
        assertTrue("Font size should be valid", defaultSettings.fontSize in 8..32)
        
        // Colors should be valid ARGB values
        val colors = listOf(
            defaultSettings.backgroundColor,
            defaultSettings.headColor,
            defaultSettings.brightTrailColor,
            defaultSettings.trailColor,
            defaultSettings.dimColor,
            defaultSettings.uiAccent,
            defaultSettings.uiOverlayBg,
            defaultSettings.uiSelectionBg
        )
        colors.forEach { color ->
            assertTrue("Color should be valid ARGB", color in 0x00000000L..0xFFFFFFFFL)
        }
    }
}