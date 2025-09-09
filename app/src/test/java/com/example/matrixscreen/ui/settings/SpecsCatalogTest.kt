package com.example.matrixscreen.ui.settings

import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.settings.model.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Sanity tests for the SpecsCatalog to ensure data integrity and consistency.
 * 
 * These tests verify that:
 * - All SettingIds exist and are properly referenced
 * - Default values are within their specified ranges
 * - All keys are unique across all specs
 * - All specs have valid configurations
 * - The catalog structure is consistent
 */
class SpecsCatalogTest {

    @Test
    fun `all specs have valid SettingIds`() {
        // Test that all specs reference valid SettingIds
        val allSpecs = ALL_SPECS
        
        allSpecs.forEach { spec ->
            assertNotNull("Spec should have a valid SettingId", spec.id)
            assertNotNull("Spec should have a valid key", spec.id.key)
            assertTrue("Spec key should not be empty", spec.id.key.isNotBlank())
        }
    }

    @Test
    fun `all slider specs have defaults in range`() {
        val sliderSpecs = ALL_SPECS.filterIsInstance<SliderSpec>()
        
        sliderSpecs.forEach { spec ->
            assertTrue(
                "Default value ${spec.default} should be within range ${spec.range} for ${spec.id.key}",
                spec.default in spec.range
            )
            assertTrue("Step should be positive for ${spec.id.key}", spec.step > 0)
            assertTrue("Range should be valid for ${spec.id.key}", spec.range.start < spec.range.endInclusive)
        }
    }

    @Test
    fun `all int slider specs have defaults in range`() {
        val intSliderSpecs = ALL_SPECS.filterIsInstance<IntSliderSpec>()
        
        intSliderSpecs.forEach { spec ->
            assertTrue(
                "Default value ${spec.default} should be within range ${spec.range} for ${spec.id.key}",
                spec.default in spec.range
            )
            assertTrue("Step should be positive for ${spec.id.key}", spec.step > 0)
            assertTrue("Range should be valid for ${spec.id.key}", spec.range.first <= spec.range.last)
        }
    }

    @Test
    fun `all select specs have defaults in options`() {
        val selectSpecs = ALL_SPECS.filterIsInstance<SelectSpec<*>>()
        
        selectSpecs.forEach { spec ->
            @Suppress("UNCHECKED_CAST")
            val typedSpec = spec as SelectSpec<Any>
            assertTrue(
                "Default value ${spec.default} should be in options ${spec.options} for ${spec.id.key}",
                typedSpec.options.contains(spec.default)
            )
            assertTrue("Options should not be empty for ${spec.id.key}", spec.options.isNotEmpty())
        }
    }

    @Test
    fun `all specs have non-blank labels`() {
        ALL_SPECS.forEach { spec ->
            assertTrue("Label should not be blank for ${spec.id.key}", spec.label.isNotBlank())
            assertTrue("Label should not be empty for ${spec.id.key}", spec.label.isNotEmpty())
        }
    }

    @Test
    fun `all SettingIds are unique across specs`() {
        val allIds = ALL_SPECS.map { it.id }
        val uniqueIds = allIds.distinctBy { it.key }
        
        assertEquals(
            "All SettingIds should be unique",
            allIds.size,
            uniqueIds.size
        )
    }

    @Test
    fun `all spec keys are unique`() {
        val allKeys = ALL_SPECS.map { it.id.key }
        val uniqueKeys = allKeys.distinct()
        
        assertEquals(
            "All spec keys should be unique",
            allKeys.size,
            uniqueKeys.size
        )
    }

    @Test
    fun `motion specs contain expected settings`() {
        val motionKeys = MOTION_SPECS.map { it.id.key }
        
        assertTrue("Should contain fallSpeed", motionKeys.contains("fallSpeed"))
        assertTrue("Should contain columnCount", motionKeys.contains("columnCount"))
        assertTrue("Should contain lineSpacing", motionKeys.contains("lineSpacing"))
        assertTrue("Should contain activePercentage", motionKeys.contains("activePercentage"))
        assertTrue("Should contain speedVariance", motionKeys.contains("speedVariance"))
        
        assertEquals("Should have 5 motion specs", 5, MOTION_SPECS.size)
    }

    @Test
    fun `effects specs contain expected settings`() {
        val effectsKeys = EFFECTS_SPECS.map { it.id.key }
        
        assertTrue("Should contain glowIntensity", effectsKeys.contains("glowIntensity"))
        assertTrue("Should contain jitterAmount", effectsKeys.contains("jitterAmount"))
        assertTrue("Should contain flickerAmount", effectsKeys.contains("flickerAmount"))
        assertTrue("Should contain mutationRate", effectsKeys.contains("mutationRate"))
        
        assertEquals("Should have 4 effects specs", 4, EFFECTS_SPECS.size)
    }

    @Test
    fun `background specs contain expected settings`() {
        val backgroundKeys = BACKGROUND_SPECS.map { it.id.key }
        
        assertTrue("Should contain grainDensity", backgroundKeys.contains("grainDensity"))
        assertTrue("Should contain grainOpacity", backgroundKeys.contains("grainOpacity"))
        assertTrue("Should contain targetFps", backgroundKeys.contains("targetFps"))
        
        assertEquals("Should have 3 background specs", 3, BACKGROUND_SPECS.size)
    }

    @Test
    fun `theme specs contain expected color settings`() {
        val themeKeys = THEME_SPECS.map { it.id.key }
        
        assertTrue("Should contain backgroundColor", themeKeys.contains("backgroundColor"))
        assertTrue("Should contain headColor", themeKeys.contains("headColor"))
        assertTrue("Should contain brightTrailColor", themeKeys.contains("brightTrailColor"))
        assertTrue("Should contain trailColor", themeKeys.contains("trailColor"))
        assertTrue("Should contain dimColor", themeKeys.contains("dimColor"))
        assertTrue("Should contain uiAccent", themeKeys.contains("uiAccent"))
        assertTrue("Should contain uiOverlayBg", themeKeys.contains("uiOverlayBg"))
        assertTrue("Should contain uiSelectionBg", themeKeys.contains("uiSelectionBg"))
        assertTrue("Should contain advancedColorsEnabled", themeKeys.contains("advancedColorsEnabled"))
        assertTrue("Should contain linkUiAndRainColors", themeKeys.contains("linkUiAndRainColors"))
        
        assertEquals("Should have 10 theme specs", 10, THEME_SPECS.size)
    }

    @Test
    fun `characters specs contain expected settings`() {
        val charactersKeys = CHARACTERS_SPECS.map { it.id.key }
        
        assertTrue("Should contain fontSize", charactersKeys.contains("fontSize"))
        
        assertEquals("Should have 1 character spec", 1, CHARACTERS_SPECS.size)
    }

    @Test
    fun `all categories are properly defined`() {
        assertEquals("Should have 6 categories", 6, ALL_SPEC_CATEGORIES.size)
        
        assertTrue("Should contain Motion category", ALL_SPEC_CATEGORIES.containsKey("Motion"))
        assertTrue("Should contain Effects category", ALL_SPEC_CATEGORIES.containsKey("Effects"))
        assertTrue("Should contain Background category", ALL_SPEC_CATEGORIES.containsKey("Background"))
        assertTrue("Should contain Timing category", ALL_SPEC_CATEGORIES.containsKey("Timing"))
        assertTrue("Should contain Characters category", ALL_SPEC_CATEGORIES.containsKey("Characters"))
        assertTrue("Should contain Theme category", ALL_SPEC_CATEGORIES.containsKey("Theme"))
        
        // Timing specs is currently empty (FPS is in Background specs)
        assertEquals("Timing specs should be empty for now", 0, TIMING_SPECS.size)
    }

    @Test
    fun `total spec count is correct`() {
        val expectedCount = MOTION_SPECS.size + EFFECTS_SPECS.size + BACKGROUND_SPECS.size + 
                           TIMING_SPECS.size + CHARACTERS_SPECS.size + THEME_SPECS.size
        
        assertEquals("Total spec count should match sum of all categories", expectedCount, TOTAL_SPEC_COUNT)
        assertEquals("ALL_SPECS should have correct count", expectedCount, ALL_SPECS.size)
    }

    @Test
    fun `all specs can be used with MatrixSettings defaults`() {
        val defaultSettings = MatrixSettings.DEFAULT
        
        // Test that all specs can retrieve values from default settings
        ALL_SPECS.forEach { spec ->
            try {
                val value = defaultSettings.get(spec.id)
                assertNotNull("Should be able to get value for ${spec.id.key}", value)
            } catch (e: Exception) {
                fail("Failed to get value for ${spec.id.key}: ${e.message}")
            }
        }
    }

    @Test
    fun `all specs can create updated MatrixSettings`() {
        val defaultSettings = MatrixSettings.DEFAULT
        
        // Test that all specs can create updated settings
        ALL_SPECS.forEach { spec ->
            try {
                val currentValue = defaultSettings.get(spec.id)
                // Use the update method instead of with to avoid type issues
                val updatedSettings = defaultSettings.update(mapOf(spec.id.key to (currentValue ?: "")))
                assertNotNull("Should be able to create updated settings for ${spec.id.key}", updatedSettings)
            } catch (e: Exception) {
                fail("Failed to create updated settings for ${spec.id.key}: ${e.message}")
            }
        }
    }

    @Test
    fun `performance-affecting specs are properly marked`() {
        val performanceSpecs = ALL_SPECS.filter { spec ->
            when (spec) {
                is SliderSpec -> spec.affectsPerf
                is IntSliderSpec -> spec.affectsPerf
                else -> false
            }
        }
        
        val performanceKeys = performanceSpecs.map { it.id.key }
        
        // These settings are known to affect performance
        assertTrue("fallSpeed should affect performance", performanceKeys.contains("fallSpeed"))
        assertTrue("columnCount should affect performance", performanceKeys.contains("columnCount"))
        assertTrue("activePercentage should affect performance", performanceKeys.contains("activePercentage"))
        assertTrue("glowIntensity should affect performance", performanceKeys.contains("glowIntensity"))
        assertTrue("jitterAmount should affect performance", performanceKeys.contains("jitterAmount"))
        assertTrue("grainDensity should affect performance", performanceKeys.contains("grainDensity"))
    }

    @Test
    fun `help text is provided for all specs`() {
        ALL_SPECS.forEach { spec ->
            assertNotNull("Help text should be provided for ${spec.id.key}", spec.help)
            assertTrue("Help text should not be blank for ${spec.id.key}", spec.help?.isNotBlank() == true)
        }
    }
}
