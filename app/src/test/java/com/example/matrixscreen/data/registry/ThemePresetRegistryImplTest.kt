package com.example.matrixscreen.data.registry

import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for ThemePresetRegistryImpl
 */
class ThemePresetRegistryImplTest {
    
    private val registry = ThemePresetRegistryImpl()
    
    @Test
    fun `getColors returns correct colors for built-in themes`() {
        val greenColors = registry.getColors(BuiltInThemes.MATRIX_GREEN)
        assertEquals(0xFF000000L, greenColors.backgroundColor)
        assertEquals(0xFF00FF00L, greenColors.headColor)
        assertEquals(0xFF00CC00L, greenColors.brightTrailColor)
        assertEquals(0xFF008800L, greenColors.trailColor)
        assertEquals(0xFF004400L, greenColors.dimColor)
        assertEquals(0xFF00CC00L, greenColors.uiAccent)
        
        val blueColors = registry.getColors(BuiltInThemes.MATRIX_BLUE)
        assertEquals(0xFF000000L, blueColors.backgroundColor)
        assertEquals(0xFF0080FFL, blueColors.headColor)
        assertEquals(0xFF0066CCL, blueColors.brightTrailColor)
    }
    
    @Test
    fun `getDisplayName returns correct display names`() {
        assertEquals("Matrix Green", registry.getDisplayName(BuiltInThemes.MATRIX_GREEN))
        assertEquals("Matrix Blue", registry.getDisplayName(BuiltInThemes.MATRIX_BLUE))
        assertEquals("Matrix Red", registry.getDisplayName(BuiltInThemes.MATRIX_RED))
        assertEquals("Matrix Purple", registry.getDisplayName(BuiltInThemes.MATRIX_PURPLE))
        assertEquals("Matrix Orange", registry.getDisplayName(BuiltInThemes.MATRIX_ORANGE))
        assertEquals("Matrix White", registry.getDisplayName(BuiltInThemes.MATRIX_WHITE))
    }
    
    @Test
    fun `isValid returns true for valid theme preset IDs`() {
        assertTrue(registry.isValid(BuiltInThemes.MATRIX_GREEN))
        assertTrue(registry.isValid(BuiltInThemes.MATRIX_BLUE))
        assertTrue(registry.isValid(BuiltInThemes.MATRIX_RED))
        assertTrue(registry.isValid(BuiltInThemes.MATRIX_PURPLE))
        assertTrue(registry.isValid(BuiltInThemes.MATRIX_ORANGE))
        assertTrue(registry.isValid(BuiltInThemes.MATRIX_WHITE))
    }
    
    @Test
    fun `isValid returns false for invalid theme preset IDs`() {
        assertFalse(registry.isValid(ThemePresetId("INVALID")))
        assertFalse(registry.isValid(ThemePresetId("")))
    }
    
    @Test
    fun `getAllIds returns all built-in theme presets`() {
        val allIds = registry.getAllIds()
        assertEquals(6, allIds.size)
        assertTrue(allIds.contains(BuiltInThemes.MATRIX_GREEN))
        assertTrue(allIds.contains(BuiltInThemes.MATRIX_BLUE))
        assertTrue(allIds.contains(BuiltInThemes.MATRIX_RED))
        assertTrue(allIds.contains(BuiltInThemes.MATRIX_PURPLE))
        assertTrue(allIds.contains(BuiltInThemes.MATRIX_ORANGE))
        assertTrue(allIds.contains(BuiltInThemes.MATRIX_WHITE))
    }
    
    @Test
    fun `getColors returns default colors for invalid theme preset ID`() {
        val invalidColors = registry.getColors(ThemePresetId("INVALID"))
        val defaultColors = registry.getColors(BuiltInThemes.MATRIX_GREEN)
        
        assertEquals(defaultColors.backgroundColor, invalidColors.backgroundColor)
        assertEquals(defaultColors.headColor, invalidColors.headColor)
        assertEquals(defaultColors.brightTrailColor, invalidColors.brightTrailColor)
    }
}
