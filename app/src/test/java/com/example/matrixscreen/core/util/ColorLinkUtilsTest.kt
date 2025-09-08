package com.example.matrixscreen.core.util

import androidx.compose.ui.graphics.Color
import com.example.matrixscreen.data.model.MatrixSettings
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class ColorLinkUtilsTest {

    private val testSettings = MatrixSettings(
        backgroundColor = 0xFF000000L, // Black
        headColor = 0xFF00FF00L, // Green
        brightTrailColor = 0xFF00CC00L, // Dimmer green
        trailColor = 0xFF008800L, // Medium green
        dimColor = 0xFF004400L, // Dark green
        uiAccent = 0xFF0000FFL, // Blue
        uiOverlayBg = 0x80000000L, // Semi-transparent black
        uiSelectionBg = 0x4000FF00L, // Semi-transparent green
        linkUiAndRainColors = false
    )

    @Test
    fun `applyColorLinking does nothing when link is disabled`() {
        val result = applyColorLinking(testSettings)
        
        // Should return original settings unchanged
        assertEquals(testSettings, result)
    }

    @Test
    fun `applyColorLinking links colors when enabled`() {
        val settingsWithLink = testSettings.copy(linkUiAndRainColors = true)
        val result = applyColorLinking(settingsWithLink)
        
        // UI accent should be linked to head color
        assertEquals(testSettings.headColor, result.uiAccent)
        
        // Other colors should remain unchanged with default mapping
        assertEquals(testSettings.uiOverlayBg, result.uiOverlayBg)
        assertEquals(testSettings.uiSelectionBg, result.uiSelectionBg)
    }

    @Test
    fun `applyColorLinking respects custom mapping configuration`() {
        val settingsWithLink = testSettings.copy(linkUiAndRainColors = true)
        val customMapping = ColorLinkMapping(
            rainHeadToUiAccent = false,
            rainBrightTrailToUiOverlay = true,
            rainTrailToUiSelection = true,
            backgroundColorIndependent = true
        )
        
        val result = applyColorLinking(settingsWithLink, customMapping)
        
        // UI accent should remain unchanged
        assertEquals(testSettings.uiAccent, result.uiAccent)
        
        // UI overlay should be linked to bright trail color
        assertEquals(testSettings.brightTrailColor, result.uiOverlayBg)
        
        // UI selection should be linked to trail color
        assertEquals(testSettings.trailColor, result.uiSelectionBg)
    }

    @Test
    fun `getEffectiveUiAccentColor returns linked color when enabled`() {
        val settingsWithLink = testSettings.copy(linkUiAndRainColors = true)
        val effectiveColor = getEffectiveUiAccentColor(settingsWithLink)
        
        assertEquals(testSettings.headColor, effectiveColor)
    }

    @Test
    fun `getEffectiveUiAccentColor returns original color when disabled`() {
        val effectiveColor = getEffectiveUiAccentColor(testSettings)
        
        assertEquals(testSettings.uiAccent, effectiveColor)
    }

    @Test
    fun `getEffectiveUiOverlayColor returns dimmed color when linked`() {
        val settingsWithLink = testSettings.copy(linkUiAndRainColors = true)
        val effectiveColor = getEffectiveUiOverlayColor(settingsWithLink)
        
        // Should be a dimmed version of bright trail color
        val brightTrailColor = Color(testSettings.brightTrailColor)
        val effectiveColorObj = Color(effectiveColor)
        
        // Alpha should be reduced (dimmed)
        assertTrue(effectiveColorObj.alpha < brightTrailColor.alpha)
    }

    @Test
    fun `getEffectiveUiOverlayColor returns original color when not linked`() {
        val effectiveColor = getEffectiveUiOverlayColor(testSettings)
        
        assertEquals(testSettings.uiOverlayBg, effectiveColor)
    }

    @Test
    fun `getEffectiveUiSelectionColor returns dimmed color when linked`() {
        val settingsWithLink = testSettings.copy(linkUiAndRainColors = true)
        val effectiveColor = getEffectiveUiSelectionColor(settingsWithLink)
        
        // Should be a dimmed version of trail color
        val trailColor = Color(testSettings.trailColor)
        val effectiveColorObj = Color(effectiveColor)
        
        // Alpha should be reduced (dimmed)
        assertTrue(effectiveColorObj.alpha < trailColor.alpha)
    }

    @Test
    fun `getEffectiveUiSelectionColor returns original color when not linked`() {
        val effectiveColor = getEffectiveUiSelectionColor(testSettings)
        
        assertEquals(testSettings.uiSelectionBg, effectiveColor)
    }

    @Test
    fun `checkColorLinkContrast returns no warnings when link is disabled`() {
        val warnings = checkColorLinkContrast(testSettings)
        
        assertTrue(warnings.isEmpty())
    }

    @Test
    fun `checkColorLinkContrast returns warnings for poor contrast combinations`() {
        val settingsWithLink = testSettings.copy(
            linkUiAndRainColors = true,
            backgroundColor = 0xFF00FF00L, // Green background
            headColor = 0xFF00FF00L // Same green for head (will have poor contrast)
        )
        
        val warnings = checkColorLinkContrast(settingsWithLink)
        
        // Should have warnings about poor contrast
        assertTrue(warnings.isNotEmpty())
        assertTrue(warnings.any { it.contains("contrast") })
    }

    @Test
    fun `getSuggestedColorAdjustments returns adjustments for poor contrast`() {
        val settingsWithLink = testSettings.copy(
            linkUiAndRainColors = true,
            backgroundColor = 0xFF00FF00L, // Green background
            headColor = 0xFF00FF00L // Same green for head (will have poor contrast)
        )
        
        val adjustments = getSuggestedColorAdjustments(settingsWithLink)
        
        // Should suggest adjustments for UI colors
        assertTrue(adjustments.isNotEmpty())
    }

    @Test
    fun `getSuggestedColorAdjustments returns empty map when link is disabled`() {
        val adjustments = getSuggestedColorAdjustments(testSettings)
        
        assertTrue(adjustments.isEmpty())
    }

    @Test
    fun `applyAutomaticContrastAdjustments improves contrast when needed`() {
        val settingsWithLink = testSettings.copy(
            linkUiAndRainColors = true,
            backgroundColor = 0xFF00FF00L, // Green background
            headColor = 0xFF00FF00L // Same green for head (will have poor contrast)
        )
        
        val adjustedSettings = applyAutomaticContrastAdjustments(settingsWithLink)
        
        // UI accent should be adjusted for better contrast
        val originalUiAccent = Color(getEffectiveUiAccentColor(settingsWithLink))
        val adjustedUiAccent = Color(adjustedSettings.uiAccent)
        
        // Adjusted color should be different from original
        assertTrue(adjustedUiAccent != originalUiAccent)
    }

    @Test
    fun `applyAutomaticContrastAdjustments returns original settings when link is disabled`() {
        val adjustedSettings = applyAutomaticContrastAdjustments(testSettings)
        
        assertEquals(testSettings, adjustedSettings)
    }

    @Test
    fun `color linking maintains color relationships`() {
        val settingsWithLink = testSettings.copy(linkUiAndRainColors = true)
        val result = applyColorLinking(settingsWithLink)
        
        // When linking is enabled, UI accent should match head color
        assertEquals(result.headColor, result.uiAccent)
        
        // Background color should remain independent
        assertEquals(testSettings.backgroundColor, result.backgroundColor)
    }

    @Test
    fun `dimColor reduces alpha appropriately`() {
        val originalColor = 0xFFFF0000L // Red with full alpha
        val dimmedColor = dimColor(originalColor, 0.5f)
        
        val originalColorObj = Color(originalColor)
        val dimmedColorObj = Color(dimmedColor)
        
        // Alpha should be reduced
        assertTrue(dimmedColorObj.alpha < originalColorObj.alpha)
        
        // RGB components should remain the same
        assertEquals(originalColorObj.red, dimmedColorObj.red)
        assertEquals(originalColorObj.green, dimmedColorObj.green)
        assertEquals(originalColorObj.blue, dimmedColorObj.blue)
    }
}
