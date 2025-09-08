package com.example.matrixscreen.ui.settings.theme

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class ThemePresetsTest {

    @Test
    fun `presets list is not empty`() {
        assertTrue(ThemePresets.presets.isNotEmpty())
    }

    @Test
    fun `all presets have valid names`() {
        ThemePresets.presets.forEach { preset ->
            assertTrue(preset.name.isNotBlank(), "Preset name should not be blank")
            assertTrue(preset.name.length > 2, "Preset name should be meaningful")
        }
    }

    @Test
    fun `all presets have valid descriptions`() {
        ThemePresets.presets.forEach { preset ->
            assertTrue(preset.description.isNotBlank(), "Preset description should not be blank")
            assertTrue(preset.description.length > 10, "Preset description should be meaningful")
        }
    }

    @Test
    fun `all presets have required color mappings`() {
        val requiredColors = listOf(
            "backgroundColor",
            "headColor",
            "brightTrailColor",
            "trailColor",
            "dimColor",
            "uiAccent",
            "uiOverlayBg",
            "uiSelectionBg"
        )
        
        ThemePresets.presets.forEach { preset ->
            requiredColors.forEach { colorKey ->
                assertTrue(
                    preset.colors.containsKey(colorKey),
                    "Preset '${preset.name}' should have '$colorKey' mapping"
                )
            }
        }
    }

    @Test
    fun `all color values are valid ARGB values`() {
        ThemePresets.presets.forEach { preset ->
            preset.colors.values.forEach { colorValue ->
                assertTrue(
                    colorValue >= 0L && colorValue <= 0xFFFFFFFFL,
                    "Color value $colorValue should be a valid ARGB value"
                )
            }
        }
    }

    @Test
    fun `film accurate preset has correct colors`() {
        val filmAccurate = ThemePresets.presets.find { it.name == "Film-Accurate" }
        assertNotNull(filmAccurate, "Film-Accurate preset should exist")
        
        // Should have classic Matrix green colors
        assertEquals(0xFF000000L, filmAccurate.colors["backgroundColor"])
        assertEquals(0xFF00FF00L, filmAccurate.colors["headColor"])
        assertEquals(0xFF00CC00L, filmAccurate.colors["brightTrailColor"])
        assertEquals(0xFF008800L, filmAccurate.colors["trailColor"])
        assertEquals(0xFF004400L, filmAccurate.colors["dimColor"])
    }

    @Test
    fun `neon preset has blue colors`() {
        val neon = ThemePresets.presets.find { it.name == "Neon" }
        assertNotNull(neon, "Neon preset should exist")
        
        // Should have blue/cyan colors
        val headColor = neon.colors["headColor"]!!
        val brightTrailColor = neon.colors["brightTrailColor"]!!
        
        // Check that colors are in the blue/cyan range
        assertTrue(headColor and 0xFF0000L == 0L, "Head color should be blue/cyan (no red component)")
        assertTrue(brightTrailColor and 0xFF0000L == 0L, "Bright trail color should be blue/cyan (no red component)")
    }

    @Test
    fun `monochrome preset has grayscale colors`() {
        val monochrome = ThemePresets.presets.find { it.name == "Monochrome" }
        assertNotNull(monochrome, "Monochrome preset should exist")
        
        // All colors should be grayscale (R = G = B)
        monochrome.colors.values.forEach { colorValue ->
            val red = (colorValue shr 16) and 0xFF
            val green = (colorValue shr 8) and 0xFF
            val blue = colorValue and 0xFF
            
            assertEquals(red, green, "Red and green should be equal for grayscale")
            assertEquals(green, blue, "Green and blue should be equal for grayscale")
        }
    }

    @Test
    fun `preset names are unique`() {
        val names = ThemePresets.presets.map { it.name }
        val uniqueNames = names.distinct()
        
        assertEquals(names.size, uniqueNames.size, "All preset names should be unique")
    }

    @Test
    fun `preset descriptions are unique`() {
        val descriptions = ThemePresets.presets.map { it.description }
        val uniqueDescriptions = descriptions.distinct()
        
        assertEquals(descriptions.size, uniqueDescriptions.size, "All preset descriptions should be unique")
    }

    @Test
    fun `background colors are typically black or dark`() {
        ThemePresets.presets.forEach { preset ->
            val backgroundColor = preset.colors["backgroundColor"]!!
            val red = (backgroundColor shr 16) and 0xFF
            val green = (backgroundColor shr 8) and 0xFF
            val blue = backgroundColor and 0xFF
            
            // Background should be dark (low RGB values)
            val totalBrightness = red + green + blue
            assertTrue(
                totalBrightness < 200,
                "Background color for '${preset.name}' should be dark (brightness < 200)"
            )
        }
    }

    @Test
    fun `head colors are typically bright`() {
        ThemePresets.presets.forEach { preset ->
            val headColor = preset.colors["headColor"]!!
            val red = (headColor shr 16) and 0xFF
            val green = (headColor shr 8) and 0xFF
            val blue = headColor and 0xFF
            
            // Head color should be bright (high RGB values)
            val totalBrightness = red + green + blue
            assertTrue(
                totalBrightness > 200,
                "Head color for '${preset.name}' should be bright (brightness > 200)"
            )
        }
    }

    @Test
    fun `color progression from head to dim is logical`() {
        ThemePresets.presets.forEach { preset ->
            val headColor = preset.colors["headColor"]!!
            val brightTrailColor = preset.colors["brightTrailColor"]!!
            val trailColor = preset.colors["trailColor"]!!
            val dimColor = preset.colors["dimColor"]!!
            
            // Calculate brightness for each color
            val headBrightness = calculateBrightness(headColor)
            val brightTrailBrightness = calculateBrightness(brightTrailColor)
            val trailBrightness = calculateBrightness(trailColor)
            val dimBrightness = calculateBrightness(dimColor)
            
            // Brightness should generally decrease from head to dim
            assertTrue(
                headBrightness >= brightTrailBrightness,
                "Head color should be brighter than or equal to bright trail color"
            )
            assertTrue(
                brightTrailBrightness >= trailBrightness,
                "Bright trail color should be brighter than or equal to trail color"
            )
            assertTrue(
                trailBrightness >= dimBrightness,
                "Trail color should be brighter than or equal to dim color"
            )
        }
    }

    private fun calculateBrightness(color: Long): Int {
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF
        return red + green + blue
    }
}
