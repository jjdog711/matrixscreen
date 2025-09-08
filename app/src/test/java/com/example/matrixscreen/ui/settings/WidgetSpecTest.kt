package com.example.matrixscreen.ui.settings

import com.example.matrixscreen.ui.settings.model.*
import org.junit.Test
import org.junit.Assert.*

/**
 * Sanity tests for WidgetSpec implementations.
 * 
 * These tests ensure that all WidgetSpec types have valid defaults,
 * non-blank labels, and proper constraints. More comprehensive tests
 * will be added in Session 5 when concrete specs are implemented.
 */
class WidgetSpecTest {

    @Test
    fun `SliderSpec has valid constraints`() {
        val spec = SliderSpec(
            id = Speed,
            label = "Fall Speed",
            range = 0.1f..10.0f,
            step = 0.1f,
            default = 2.0f,
            unit = "units/sec",
            affectsPerf = true,
            help = "Controls how fast the rain falls"
        )

        // Test basic properties
        assertEquals(Speed, spec.id)
        assertEquals("Fall Speed", spec.label)
        assertEquals(0.1f..10.0f, spec.range)
        assertEquals(0.1f, spec.step)
        assertEquals(2.0f, spec.default)
        assertEquals("units/sec", spec.unit)
        assertTrue(spec.affectsPerf)
        assertEquals("Controls how fast the rain falls", spec.help)

        // Test constraints
        assertTrue("Default should be in range", spec.default in spec.range)
        assertTrue("Step should be positive", spec.step > 0)
        assertTrue("Label should not be blank", spec.label.isNotBlank())
    }

    @Test
    fun `IntSliderSpec has valid constraints`() {
        val spec = IntSliderSpec(
            id = Columns,
            label = "Column Count",
            range = 10..500,
            step = 5,
            default = 150,
            unit = "columns",
            affectsPerf = true,
            help = "Number of rain columns"
        )

        // Test basic properties
        assertEquals(Columns, spec.id)
        assertEquals("Column Count", spec.label)
        assertEquals(10..500, spec.range)
        assertEquals(5, spec.step)
        assertEquals(150, spec.default)
        assertEquals("columns", spec.unit)
        assertTrue(spec.affectsPerf)
        assertEquals("Number of rain columns", spec.help)

        // Test constraints
        assertTrue("Default should be in range", spec.default in spec.range)
        assertTrue("Step should be positive", spec.step > 0)
        assertTrue("Label should not be blank", spec.label.isNotBlank())
    }

    @Test
    fun `ToggleSpec has valid constraints`() {
        val spec = ToggleSpec(
            id = TestBooleanSetting,
            label = "Enable Feature",
            default = true,
            help = "Turn this feature on or off"
        )

        // Test basic properties
        assertEquals(TestBooleanSetting, spec.id)
        assertEquals("Enable Feature", spec.label)
        assertTrue(spec.default)
        assertEquals("Turn this feature on or off", spec.help)

        // Test constraints
        assertTrue("Label should not be blank", spec.label.isNotBlank())
    }

    @Test
    fun `ColorSpec has valid constraints`() {
        val spec = ColorSpec(
            id = BgColor,
            label = "Background Color",
            help = "Choose the background color"
        )

        // Test basic properties
        assertEquals(BgColor, spec.id)
        assertEquals("Background Color", spec.label)
        assertEquals("Choose the background color", spec.help)

        // Test constraints
        assertTrue("Label should not be blank", spec.label.isNotBlank())
    }

    @Test
    fun `SelectSpec has valid constraints`() {
        val fpsOptions = listOf(30, 60, 90, 120)
        val spec = SelectSpec(
            id = Fps,
            label = "Target FPS",
            options = fpsOptions,
            toLabel = { "$it FPS" },
            default = 60,
            help = "Choose the target frame rate"
        )

        // Test basic properties
        assertEquals(Fps, spec.id)
        assertEquals("Target FPS", spec.label)
        assertEquals(fpsOptions, spec.options)
        assertEquals(60, spec.default)
        assertEquals("Choose the target frame rate", spec.help)

        // Test constraints
        assertTrue("Default should be in options", spec.default in spec.options)
        assertTrue("Options should not be empty", spec.options.isNotEmpty())
        assertTrue("Label should not be blank", spec.label.isNotBlank())
        
        // Test toLabel function
        assertEquals("60 FPS", spec.toLabel(60))
        assertEquals("120 FPS", spec.toLabel(120))
    }

    @Test
    fun `all spec types have non-blank labels`() {
        val specs = listOf(
            SliderSpec(Speed, "Fall Speed", 0.1f..10.0f, 0.1f, 2.0f),
            IntSliderSpec(Columns, "Column Count", 10..500, 1, 150),
            ToggleSpec(TestBooleanSetting, "Enable Feature", true),
            ColorSpec(BgColor, "Background Color"),
            SelectSpec(Fps, "Target FPS", listOf(30, 60, 90, 120), { "$it FPS" }, 60)
        )

        specs.forEach { spec ->
            assertTrue("Spec label should not be blank: ${spec::class.simpleName}", 
                spec.label.isNotBlank())
        }
    }

    @Test
    fun `slider specs have defaults in range`() {
        val sliderSpec = SliderSpec(
            id = Speed,
            label = "Fall Speed",
            range = 0.1f..10.0f,
            step = 0.1f,
            default = 2.0f
        )

        val intSliderSpec = IntSliderSpec(
            id = Columns,
            label = "Column Count",
            range = 10..500,
            step = 5,
            default = 150
        )

        assertTrue("SliderSpec default should be in range", 
            sliderSpec.default in sliderSpec.range)
        assertTrue("IntSliderSpec default should be in range", 
            intSliderSpec.default in intSliderSpec.range)
    }

    @Test
    fun `select specs have defaults in options`() {
        val selectSpec = SelectSpec(
            id = Fps,
            label = "Target FPS",
            options = listOf(30, 60, 90, 120),
            toLabel = { "$it FPS" },
            default = 60
        )

        assertTrue("SelectSpec default should be in options", 
            selectSpec.default in selectSpec.options)
    }

    @Test
    fun `slider specs have positive steps`() {
        val sliderSpec = SliderSpec(
            id = Speed,
            label = "Fall Speed",
            range = 0.1f..10.0f,
            step = 0.1f,
            default = 2.0f
        )

        val intSliderSpec = IntSliderSpec(
            id = Columns,
            label = "Column Count",
            range = 10..500,
            step = 5,
            default = 150
        )

        assertTrue("SliderSpec step should be positive", sliderSpec.step > 0)
        assertTrue("IntSliderSpec step should be positive", intSliderSpec.step > 0)
    }

    @Test
    fun `select specs have non-empty options`() {
        val selectSpec = SelectSpec(
            id = Fps,
            label = "Target FPS",
            options = listOf(30, 60, 90, 120),
            toLabel = { "$it FPS" },
            default = 60
        )

        assertTrue("SelectSpec options should not be empty", 
            selectSpec.options.isNotEmpty())
    }

    @Test
    fun `widget specs can be created with minimal parameters`() {
        // Test that all spec types can be created with minimal required parameters
        val sliderSpec = SliderSpec(
            id = Speed,
            label = "Fall Speed",
            range = 0.1f..10.0f,
            step = 0.1f,
            default = 2.0f
        )

        val intSliderSpec = IntSliderSpec(
            id = Columns,
            label = "Column Count",
            range = 10..500,
            default = 150
        )

        val toggleSpec = ToggleSpec(
            id = TestBooleanSetting,
            label = "Enable Feature",
            default = true
        )

        val colorSpec = ColorSpec(
            id = BgColor,
            label = "Background Color"
        )

        val selectSpec = SelectSpec(
            id = Fps,
            label = "Target FPS",
            options = listOf(30, 60, 90, 120),
            toLabel = { "$it FPS" },
            default = 60
        )

        // All specs should be created successfully
        assertNotNull(sliderSpec)
        assertNotNull(intSliderSpec)
        assertNotNull(toggleSpec)
        assertNotNull(colorSpec)
        assertNotNull(selectSpec)
    }
}
