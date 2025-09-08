package com.example.matrixscreen.data.model

import com.example.matrixscreen.data.proto.MatrixSettingsProto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Proto-Domain mapping functions.
 * 
 * Tests validation, clamping, and round-trip conversion.
 */
class MappersTest {
    
    @Test
    fun `toDomain maps proto to domain with default values`() {
        // Given default proto
        val proto = MatrixSettingsProto.getDefaultInstance()
        
        // When converting to domain
        val domain = proto.toDomain()
        
        // Then default values are mapped correctly (proto3 defaults are 0, but our mapper clamps them)
        assertEquals(1, domain.schemaVersion) // We set this to 1 in our mapper
        assertEquals(0.5f, domain.fallSpeed, 0.01f) // Proto3 default is 0.0f, clamped to 0.5f
        assertEquals(50, domain.columnCount) // Proto3 default is 0, clamped to 50
        assertEquals(5, domain.targetFps) // Proto3 default is 0, clamped to 5
        assertEquals(0x00000000L, domain.backgroundColor) // Proto3 default is 0L, valid range
        assertEquals(0x00000000L, domain.headColor) // Proto3 default is 0L, valid range
    }
    
    @Test
    fun `toDomain clamps out-of-range values`() {
        // Given proto with out-of-range values
        val proto = MatrixSettingsProto.newBuilder()
            .setFallSpeed(10.0f) // Should be clamped to 5.0f
            .setColumnCount(500) // Should be clamped to 200
            .setTargetFps(200) // Should be clamped to 120
            .setGlowIntensity(-1.0f) // Should be clamped to 0.0f
            .setBackgroundColor(-1L) // Should be clamped
            .build()
        
        // When converting to domain
        val domain = proto.toDomain()
        
        // Then values are clamped to valid ranges
        assertTrue("Fall speed should be clamped", domain.fallSpeed <= 5.0f)
        assertTrue("Column count should be clamped", domain.columnCount <= 200)
        assertTrue("Target FPS should be clamped", domain.targetFps <= 120)
        assertTrue("Glow intensity should be clamped", domain.glowIntensity >= 0.0f)
        assertTrue("Background color should be valid", domain.backgroundColor in 0x00000000L..0xFFFFFFFFL)
    }
    
    @Test
    fun `toProto maps domain to proto correctly`() {
        // Given domain settings
        val domain = MatrixSettings(
            fallSpeed = 3.0f,
            columnCount = 100,
            targetFps = 30,
            glowIntensity = 1.5f,
            backgroundColor = 0xFFFF0000L,
            headColor = 0xFF00FF00L
        )
        
        // When converting to proto
        val proto = domain.toProto()
        
        // Then values are mapped correctly
        assertEquals(domain.schemaVersion, proto.schemaVersion)
        assertEquals(domain.fallSpeed, proto.fallSpeed, 0.01f)
        assertEquals(domain.columnCount, proto.columnCount)
        assertEquals(domain.targetFps, proto.targetFps)
        assertEquals(domain.glowIntensity, proto.glowIntensity, 0.01f)
        assertEquals(domain.backgroundColor, proto.backgroundColor)
        assertEquals(domain.headColor, proto.headColor)
    }
    
    @Test
    fun `round-trip conversion preserves valid values`() {
        // Given domain settings with valid values
        val originalDomain = MatrixSettings(
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
        val proto = originalDomain.toProto()
        val convertedDomain = proto.toDomain()
        
        // Then values are preserved
        assertEquals(originalDomain, convertedDomain)
    }
    
    @Test
    fun `clampSettingValue clamps individual values correctly`() {
        // Test various clamping scenarios
        assertEquals(5.0f, clampSettingValue("fallSpeed", 10.0f) as Float, 0.01f)
        assertEquals(0.5f, clampSettingValue("fallSpeed", 0.1f) as Float, 0.01f)
        assertEquals(200, clampSettingValue("columnCount", 500) as Int)
        assertEquals(50, clampSettingValue("columnCount", 10) as Int)
        assertEquals(120, clampSettingValue("targetFps", 200) as Int)
        assertEquals(5, clampSettingValue("targetFps", 1) as Int)
        assertEquals(0.0f, clampSettingValue("glowIntensity", -1.0f) as Float, 0.01f)
        assertEquals(3.0f, clampSettingValue("glowIntensity", 5.0f) as Float, 0.01f)
        assertEquals(32, clampSettingValue("fontSize", 50) as Int)
        assertEquals(8, clampSettingValue("fontSize", 5) as Int)
    }
    
    @Test
    fun `clampSettingValue returns original value for unknown keys`() {
        // Given unknown key
        val originalValue = "unknown_value"
        
        // When clamping
        val result = clampSettingValue("unknownKey", originalValue)
        
        // Then original value is returned
        assertEquals(originalValue, result)
    }
    
    @Test
    fun `createDefaultProto creates valid default proto`() {
        // When creating default proto
        val defaultProto = createDefaultProto()
        
        // Then it has valid default values
        assertEquals(1, defaultProto.schemaVersion)
        assertEquals(2.0f, defaultProto.fallSpeed, 0.01f)
        assertEquals(150, defaultProto.columnCount)
        assertEquals(60, defaultProto.targetFps)
        assertEquals(0xFF000000L, defaultProto.backgroundColor)
        assertEquals(0xFF00FF00L, defaultProto.headColor)
        
        // And converting to domain gives default domain
        val domain = defaultProto.toDomain()
        assertEquals(MatrixSettings.DEFAULT, domain)
    }
}
