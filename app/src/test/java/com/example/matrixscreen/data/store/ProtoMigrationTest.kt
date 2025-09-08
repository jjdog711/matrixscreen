package com.example.matrixscreen.data.store

import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.model.createDefaultProto
import com.example.matrixscreen.data.model.toDomain
import com.example.matrixscreen.data.model.toProto
import com.example.matrixscreen.data.proto.MatrixSettingsProto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for Proto DataStore migration from SharedPreferences.
 * 
 * Tests migration logic and data validation.
 */
class ProtoMigrationTest {
    
    @Test
    fun `default proto has correct schema version`() {
        // Given default proto
        val defaultProto = createDefaultProto()
        
        // Then it has the correct schema version
        assertEquals(1, defaultProto.schemaVersion)
    }
    
    @Test
    fun `default proto has valid default values`() {
        // Given default proto
        val defaultProto = createDefaultProto()
        
        // When converting to domain
        val domain = defaultProto.toDomain()
        
        // Then it matches the default domain settings
        assertEquals(MatrixSettings.DEFAULT, domain)
    }
    
    @Test
    fun `migration mapping preserves valid values`() {
        // Given settings with valid values
        val validSettings = MatrixSettings(
            fallSpeed = 3.0f,
            columnCount = 100,
            targetFps = 30,
            glowIntensity = 1.5f,
            backgroundColor = 0xFFFF0000L,
            headColor = 0xFF00FF00L
        )
        
        // When converting to proto and back
        val proto = validSettings.toProto()
        val convertedSettings = proto.toDomain()
        
        // Then values are preserved
        assertEquals(validSettings, convertedSettings)
    }
    
    @Test
    fun `migration mapping clamps out-of-range values`() {
        // Given settings with out-of-range values
        val invalidSettings = MatrixSettings(
            fallSpeed = 10.0f, // Should be clamped to 5.0f
            columnCount = 500, // Should be clamped to 200
            targetFps = 200, // Should be clamped to 120
            glowIntensity = -1.0f // Should be clamped to 0.0f
        )
        
        // When converting to proto and back
        val proto = invalidSettings.toProto()
        val clampedSettings = proto.toDomain()
        
        // Then values are clamped to valid ranges
        assertTrue("Fall speed should be clamped", clampedSettings.fallSpeed <= 5.0f)
        assertTrue("Column count should be clamped", clampedSettings.columnCount <= 200)
        assertTrue("Target FPS should be clamped", clampedSettings.targetFps <= 120)
        assertTrue("Glow intensity should be clamped", clampedSettings.glowIntensity >= 0.0f)
    }
    
    @Test
    fun `migration handles missing fields with defaults`() {
        // Given proto with only some fields set
        val partialProto = MatrixSettingsProto.newBuilder()
            .setSchemaVersion(1)
            .setFallSpeed(3.0f)
            .setColumnCount(120)
            // Missing other fields - should use defaults
            .build()
        
        // When converting to domain
        val domain = partialProto.toDomain()
        
        // Then custom values are preserved and defaults are used for missing fields
        assertEquals(3.0f, domain.fallSpeed, 0.01f)
        assertEquals(120, domain.columnCount)
        // Proto3 defaults are 0, but our mapper clamps them to valid ranges
        assertEquals(0.5f, domain.lineSpacing, 0.01f) // Proto3 default 0.0f, clamped to 0.5f
        assertEquals(5, domain.targetFps) // Proto3 default 0, clamped to 5
        assertEquals(0x00000000L, domain.backgroundColor) // Proto3 default 0L, valid range
    }
}
