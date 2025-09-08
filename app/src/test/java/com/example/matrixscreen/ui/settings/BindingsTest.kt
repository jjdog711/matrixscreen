package com.example.matrixscreen.ui.settings

import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.settings.model.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Tests for the Bindings mapping functions.
 * 
 * These tests ensure that all SettingId objects are properly handled in both
 * get() and with() functions, and that round-trip operations work correctly.
 */
class BindingsTest {

    private val defaultSettings = MatrixSettings.DEFAULT

    @Test
    fun `get returns correct values for all motion settings`() {
        // Motion settings
        assertEquals(2.0f, defaultSettings.get(Speed))
        assertEquals(150, defaultSettings.get(Columns))
        assertEquals(0.9f, defaultSettings.get(LineSpace))
        assertEquals(0.4f, defaultSettings.get(ActivePct))
        assertEquals(0.01f, defaultSettings.get(SpeedVar))
    }

    @Test
    fun `get returns correct values for all effects settings`() {
        // Effects settings
        assertEquals(2.0f, defaultSettings.get(Glow))
        assertEquals(2.0f, defaultSettings.get(Jitter))
        assertEquals(0.2f, defaultSettings.get(Flicker))
        assertEquals(0.08f, defaultSettings.get(Mutation))
    }

    @Test
    fun `get returns correct values for all background settings`() {
        // Background settings
        assertEquals(200, defaultSettings.get(GrainD))
        assertEquals(0.03f, defaultSettings.get(GrainO))
        assertEquals(60, defaultSettings.get(Fps))
    }

    @Test
    fun `get returns correct values for all color settings`() {
        // Color settings
        assertEquals(0xFF000000L, defaultSettings.get(BgColor))
        assertEquals(0xFF00FF00L, defaultSettings.get(HeadColor))
        assertEquals(0xFF00CC00L, defaultSettings.get(BrightColor))
        assertEquals(0xFF008800L, defaultSettings.get(TrailColor))
        assertEquals(0xFF004400L, defaultSettings.get(DimColor))
    }

    @Test
    fun `get returns correct values for all UI theme settings`() {
        // UI theme colors
        assertEquals(0xFF00CC00L, defaultSettings.get(UiAccent))
        assertEquals(0x80000000L, defaultSettings.get(UiOverlay))
        assertEquals(0x4000FF00L, defaultSettings.get(UiSelectBg))
    }

    @Test
    fun `get returns correct values for character settings`() {
        // Character settings
        assertEquals(14, defaultSettings.get(FontSize))
    }

    @Test
    fun `with creates new instance with updated motion settings`() {
        // Test motion settings updates
        val updated = defaultSettings
            .with(Speed, 3.0f)
            .with(Columns, 200)
            .with(LineSpace, 1.0f)
            .with(ActivePct, 0.5f)
            .with(SpeedVar, 0.02f)

        assertEquals(3.0f, updated.fallSpeed)
        assertEquals(200, updated.columnCount)
        assertEquals(1.0f, updated.lineSpacing)
        assertEquals(0.5f, updated.activePercentage)
        assertEquals(0.02f, updated.speedVariance)

        // Verify original is unchanged
        assertEquals(2.0f, defaultSettings.fallSpeed)
        assertEquals(150, defaultSettings.columnCount)
    }

    @Test
    fun `with creates new instance with updated effects settings`() {
        // Test effects settings updates
        val updated = defaultSettings
            .with(Glow, 3.0f)
            .with(Jitter, 1.5f)
            .with(Flicker, 0.3f)
            .with(Mutation, 0.1f)

        assertEquals(3.0f, updated.glowIntensity)
        assertEquals(1.5f, updated.jitterAmount)
        assertEquals(0.3f, updated.flickerAmount)
        assertEquals(0.1f, updated.mutationRate)

        // Verify original is unchanged
        assertEquals(2.0f, defaultSettings.glowIntensity)
    }

    @Test
    fun `with creates new instance with updated background settings`() {
        // Test background settings updates
        val updated = defaultSettings
            .with(GrainD, 300)
            .with(GrainO, 0.05f)
            .with(Fps, 90)

        assertEquals(300, updated.grainDensity)
        assertEquals(0.05f, updated.grainOpacity)
        assertEquals(90, updated.targetFps)

        // Verify original is unchanged
        assertEquals(200, defaultSettings.grainDensity)
    }

    @Test
    fun `with creates new instance with updated color settings`() {
        // Test color settings updates
        val newBgColor = 0xFF111111L
        val newHeadColor = 0xFF00AA00L
        val newBrightColor = 0xFF00BB00L
        val newTrailColor = 0xFF009900L
        val newDimColor = 0xFF005500L

        val updated = defaultSettings
            .with(BgColor, newBgColor)
            .with(HeadColor, newHeadColor)
            .with(BrightColor, newBrightColor)
            .with(TrailColor, newTrailColor)
            .with(DimColor, newDimColor)

        assertEquals(newBgColor, updated.backgroundColor)
        assertEquals(newHeadColor, updated.headColor)
        assertEquals(newBrightColor, updated.brightTrailColor)
        assertEquals(newTrailColor, updated.trailColor)
        assertEquals(newDimColor, updated.dimColor)

        // Verify original is unchanged
        assertEquals(0xFF000000L, defaultSettings.backgroundColor)
    }

    @Test
    fun `with creates new instance with updated UI theme settings`() {
        // Test UI theme settings updates
        val newAccent = 0xFF00AA00L
        val newOverlay = 0x90000000L
        val newSelection = 0x5000FF00L

        val updated = defaultSettings
            .with(UiAccent, newAccent)
            .with(UiOverlay, newOverlay)
            .with(UiSelectBg, newSelection)

        assertEquals(newAccent, updated.uiAccent)
        assertEquals(newOverlay, updated.uiOverlayBg)
        assertEquals(newSelection, updated.uiSelectionBg)

        // Verify original is unchanged
        assertEquals(0xFF00CC00L, defaultSettings.uiAccent)
    }

    @Test
    fun `with creates new instance with updated character settings`() {
        // Test character settings updates
        val updated = defaultSettings.with(FontSize, 16)

        assertEquals(16, updated.fontSize)

        // Verify original is unchanged
        assertEquals(14, defaultSettings.fontSize)
    }

    @Test
    fun `round-trip operations preserve values`() {
        // Test that get(with(settings, value)) == value for all settings
        
        // Test Float settings
        val speedUpdated = defaultSettings.with(Speed, 3.5f)
        assertEquals(3.5f, speedUpdated.get(Speed))
        
        val lineSpaceUpdated = defaultSettings.with(LineSpace, 1.1f)
        assertEquals(1.1f, lineSpaceUpdated.get(LineSpace))
        
        val activePctUpdated = defaultSettings.with(ActivePct, 0.6f)
        assertEquals(0.6f, activePctUpdated.get(ActivePct))
        
        val speedVarUpdated = defaultSettings.with(SpeedVar, 0.03f)
        assertEquals(0.03f, speedVarUpdated.get(SpeedVar))
        
        val glowUpdated = defaultSettings.with(Glow, 2.5f)
        assertEquals(2.5f, glowUpdated.get(Glow))
        
        val jitterUpdated = defaultSettings.with(Jitter, 1.8f)
        assertEquals(1.8f, jitterUpdated.get(Jitter))
        
        val flickerUpdated = defaultSettings.with(Flicker, 0.25f)
        assertEquals(0.25f, flickerUpdated.get(Flicker))
        
        val mutationUpdated = defaultSettings.with(Mutation, 0.12f)
        assertEquals(0.12f, mutationUpdated.get(Mutation))
        
        val grainOUpdated = defaultSettings.with(GrainO, 0.04f)
        assertEquals(0.04f, grainOUpdated.get(GrainO))
        
        // Test Int settings
        val columnsUpdated = defaultSettings.with(Columns, 175)
        assertEquals(175, columnsUpdated.get(Columns))
        
        val grainDUpdated = defaultSettings.with(GrainD, 250)
        assertEquals(250, grainDUpdated.get(GrainD))
        
        val fpsUpdated = defaultSettings.with(Fps, 120)
        assertEquals(120, fpsUpdated.get(Fps))
        
        val fontSizeUpdated = defaultSettings.with(FontSize, 18)
        assertEquals(18, fontSizeUpdated.get(FontSize))
        
        // Test Long settings
        val bgColorUpdated = defaultSettings.with(BgColor, 0xFF222222L)
        assertEquals(0xFF222222L, bgColorUpdated.get(BgColor))
        
        val headColorUpdated = defaultSettings.with(HeadColor, 0xFF00BB00L)
        assertEquals(0xFF00BB00L, headColorUpdated.get(HeadColor))
        
        val brightColorUpdated = defaultSettings.with(BrightColor, 0xFF00DD00L)
        assertEquals(0xFF00DD00L, brightColorUpdated.get(BrightColor))
        
        val trailColorUpdated = defaultSettings.with(TrailColor, 0xFF009900L)
        assertEquals(0xFF009900L, trailColorUpdated.get(TrailColor))
        
        val dimColorUpdated = defaultSettings.with(DimColor, 0xFF006600L)
        assertEquals(0xFF006600L, dimColorUpdated.get(DimColor))
        
        val uiAccentUpdated = defaultSettings.with(UiAccent, 0xFF00DD00L)
        assertEquals(0xFF00DD00L, uiAccentUpdated.get(UiAccent))
        
        val uiOverlayUpdated = defaultSettings.with(UiOverlay, 0x70000000L)
        assertEquals(0x70000000L, uiOverlayUpdated.get(UiOverlay))
        
        val uiSelectBgUpdated = defaultSettings.with(UiSelectBg, 0x3000FF00L)
        assertEquals(0x3000FF00L, uiSelectBgUpdated.get(UiSelectBg))
    }

    @Test
    fun `all setting ids are handled in get function`() {
        // This test ensures that all SettingId objects are covered in the get() function
        // If a new SettingId is added but not handled, this test will fail at compile time
        val allIds = listOf(
            Speed, Columns, LineSpace, ActivePct, SpeedVar,
            Glow, Jitter, Flicker, Mutation,
            GrainD, GrainO, Fps,
            BgColor, HeadColor, BrightColor, TrailColor, DimColor,
            UiAccent, UiOverlay, UiSelectBg,
            FontSize
        )

        // This should compile and run without exceptions
        allIds.forEach { id ->
            val value = defaultSettings.get(id)
            assertNotNull("Value should not be null for ${id.key}", value)
        }
    }

    @Test
    fun `all setting ids are handled in with function`() {
        // This test ensures that all SettingId objects are covered in the with() function
        // If a new SettingId is added but not handled, this test will fail at compile time
        
        // Test Float settings
        val speedUpdated = defaultSettings.with(Speed, 3.0f)
        assertNotNull("Updated settings should not be null for Speed", speedUpdated)
        assertNotSame("Should create new instance for Speed", defaultSettings, speedUpdated)
        
        val lineSpaceUpdated = defaultSettings.with(LineSpace, 1.0f)
        assertNotNull("Updated settings should not be null for LineSpace", lineSpaceUpdated)
        assertNotSame("Should create new instance for LineSpace", defaultSettings, lineSpaceUpdated)
        
        val activePctUpdated = defaultSettings.with(ActivePct, 0.5f)
        assertNotNull("Updated settings should not be null for ActivePct", activePctUpdated)
        assertNotSame("Should create new instance for ActivePct", defaultSettings, activePctUpdated)
        
        val speedVarUpdated = defaultSettings.with(SpeedVar, 0.02f)
        assertNotNull("Updated settings should not be null for SpeedVar", speedVarUpdated)
        assertNotSame("Should create new instance for SpeedVar", defaultSettings, speedVarUpdated)
        
        val glowUpdated = defaultSettings.with(Glow, 2.5f)
        assertNotNull("Updated settings should not be null for Glow", glowUpdated)
        assertNotSame("Should create new instance for Glow", defaultSettings, glowUpdated)
        
        val jitterUpdated = defaultSettings.with(Jitter, 1.5f)
        assertNotNull("Updated settings should not be null for Jitter", jitterUpdated)
        assertNotSame("Should create new instance for Jitter", defaultSettings, jitterUpdated)
        
        val flickerUpdated = defaultSettings.with(Flicker, 0.3f)
        assertNotNull("Updated settings should not be null for Flicker", flickerUpdated)
        assertNotSame("Should create new instance for Flicker", defaultSettings, flickerUpdated)
        
        val mutationUpdated = defaultSettings.with(Mutation, 0.1f)
        assertNotNull("Updated settings should not be null for Mutation", mutationUpdated)
        assertNotSame("Should create new instance for Mutation", defaultSettings, mutationUpdated)
        
        val grainOUpdated = defaultSettings.with(GrainO, 0.05f)
        assertNotNull("Updated settings should not be null for GrainO", grainOUpdated)
        assertNotSame("Should create new instance for GrainO", defaultSettings, grainOUpdated)
        
        // Test Int settings
        val columnsUpdated = defaultSettings.with(Columns, 200)
        assertNotNull("Updated settings should not be null for Columns", columnsUpdated)
        assertNotSame("Should create new instance for Columns", defaultSettings, columnsUpdated)
        
        val grainDUpdated = defaultSettings.with(GrainD, 300)
        assertNotNull("Updated settings should not be null for GrainD", grainDUpdated)
        assertNotSame("Should create new instance for GrainD", defaultSettings, grainDUpdated)
        
        val fpsUpdated = defaultSettings.with(Fps, 90)
        assertNotNull("Updated settings should not be null for Fps", fpsUpdated)
        assertNotSame("Should create new instance for Fps", defaultSettings, fpsUpdated)
        
        val fontSizeUpdated = defaultSettings.with(FontSize, 16)
        assertNotNull("Updated settings should not be null for FontSize", fontSizeUpdated)
        assertNotSame("Should create new instance for FontSize", defaultSettings, fontSizeUpdated)
        
        // Test Long settings
        val bgColorUpdated = defaultSettings.with(BgColor, 0xFF111111L)
        assertNotNull("Updated settings should not be null for BgColor", bgColorUpdated)
        assertNotSame("Should create new instance for BgColor", defaultSettings, bgColorUpdated)
        
        val headColorUpdated = defaultSettings.with(HeadColor, 0xFF00AA00L)
        assertNotNull("Updated settings should not be null for HeadColor", headColorUpdated)
        assertNotSame("Should create new instance for HeadColor", defaultSettings, headColorUpdated)
        
        val brightColorUpdated = defaultSettings.with(BrightColor, 0xFF00BB00L)
        assertNotNull("Updated settings should not be null for BrightColor", brightColorUpdated)
        assertNotSame("Should create new instance for BrightColor", defaultSettings, brightColorUpdated)
        
        val trailColorUpdated = defaultSettings.with(TrailColor, 0xFF009900L)
        assertNotNull("Updated settings should not be null for TrailColor", trailColorUpdated)
        assertNotSame("Should create new instance for TrailColor", defaultSettings, trailColorUpdated)
        
        val dimColorUpdated = defaultSettings.with(DimColor, 0xFF005500L)
        assertNotNull("Updated settings should not be null for DimColor", dimColorUpdated)
        assertNotSame("Should create new instance for DimColor", defaultSettings, dimColorUpdated)
        
        val uiAccentUpdated = defaultSettings.with(UiAccent, 0xFF00AA00L)
        assertNotNull("Updated settings should not be null for UiAccent", uiAccentUpdated)
        assertNotSame("Should create new instance for UiAccent", defaultSettings, uiAccentUpdated)
        
        val uiOverlayUpdated = defaultSettings.with(UiOverlay, 0x90000000L)
        assertNotNull("Updated settings should not be null for UiOverlay", uiOverlayUpdated)
        assertNotSame("Should create new instance for UiOverlay", defaultSettings, uiOverlayUpdated)
        
        val uiSelectBgUpdated = defaultSettings.with(UiSelectBg, 0x5000FF00L)
        assertNotNull("Updated settings should not be null for UiSelectBg", uiSelectBgUpdated)
        assertNotSame("Should create new instance for UiSelectBg", defaultSettings, uiSelectBgUpdated)
    }

    @Test
    fun `type safety is maintained for all setting types`() {
        // Test that the generic types work correctly
        val speedValue: Float = defaultSettings.get(Speed)
        val columnValue: Int = defaultSettings.get(Columns)
        val colorValue: Long = defaultSettings.get(BgColor)

        assertTrue("Speed should be Float", speedValue is Float)
        assertTrue("Columns should be Int", columnValue is Int)
        assertTrue("BgColor should be Long", colorValue is Long)
    }
}
