package com.example.matrixscreen.ui.settings.model

import com.example.matrixscreen.ui.settings.model.SettingId.*
import com.example.matrixscreen.ui.settings.model.WidgetSpec.*

/**
 * Catalog of WidgetSpecs organized by category.
 * 
 * This catalog provides the data-driven specifications for all MatrixScreen settings,
 * organized into logical categories. Each category contains a list of WidgetSpecs that
 * describe how the settings should be rendered in the UI.
 * 
 * The specifications include:
 * - Type-safe SettingId references
 * - Human-readable labels
 * - Valid ranges and default values
 * - Performance impact indicators
 * - Help text for user guidance
 * 
 * This enables the spec-driven UI system to generate consistent, accessible controls
 * for all settings without hardcoding UI logic.
 */

// Motion Settings - Controls for matrix rain movement and behavior
val MOTION_SPECS = listOf(
    SliderSpec(
        id = Speed,
        label = "Fall Speed",
        range = 0.5f..10.0f,
        step = 0.1f,
        default = 2.0f,
        unit = "units/sec",
        affectsPerf = true,
        help = "Controls how fast the matrix rain falls down the screen"
    ),
    
    IntSliderSpec(
        id = Columns,
        label = "Column Count",
        range = 50..500,
        step = 10,
        default = 150,
        unit = "columns",
        affectsPerf = true,
        help = "Number of matrix columns to display. Higher values may impact performance"
    ),
    
    SliderSpec(
        id = LineSpace,
        label = "Line Spacing",
        range = 0.5f..2.0f,
        step = 0.1f,
        default = 0.9f,
        unit = "ratio",
        affectsPerf = false,
        help = "Spacing between matrix lines. Lower values create denser rain"
    ),
    
    SliderSpec(
        id = ActivePct,
        label = "Active Percentage",
        range = 0.1f..1.0f,
        step = 0.05f,
        default = 0.4f,
        unit = "%",
        affectsPerf = true,
        help = "Percentage of columns that are actively raining at any time"
    ),
    
    SliderSpec(
        id = SpeedVar,
        label = "Speed Variance",
        range = 0.0f..0.5f,
        step = 0.01f,
        default = 0.01f,
        unit = "ratio",
        affectsPerf = false,
        help = "Random variation in fall speed between columns"
    )
)

// Effects Settings - Visual effects and animations
val EFFECTS_SPECS = listOf(
    SliderSpec(
        id = Glow,
        label = "Glow Intensity",
        range = 0.0f..5.0f,
        step = 0.1f,
        default = 2.0f,
        unit = "intensity",
        affectsPerf = true,
        help = "Intensity of the glow effect around matrix characters"
    ),
    
    SliderSpec(
        id = Jitter,
        label = "Jitter Amount",
        range = 0.0f..5.0f,
        step = 0.1f,
        default = 2.0f,
        unit = "pixels",
        affectsPerf = true,
        help = "Amount of random horizontal movement for matrix characters"
    ),
    
    SliderSpec(
        id = Flicker,
        label = "Flicker Amount",
        range = 0.0f..1.0f,
        step = 0.05f,
        default = 0.2f,
        unit = "intensity",
        affectsPerf = false,
        help = "Amount of random brightness flickering for matrix characters"
    ),
    
    SliderSpec(
        id = Mutation,
        label = "Mutation Rate",
        range = 0.0f..0.5f,
        step = 0.01f,
        default = 0.08f,
        unit = "rate",
        affectsPerf = false,
        help = "Rate at which matrix characters change to new symbols"
    )
)

// Background Settings - Background effects and performance
val BACKGROUND_SPECS = listOf(
    IntSliderSpec(
        id = GrainD,
        label = "Grain Density",
        range = 0..1000,
        step = 25,
        default = 200,
        unit = "particles",
        affectsPerf = true,
        help = "Number of grain particles in the background. Higher values may impact performance"
    ),
    
    SliderSpec(
        id = GrainO,
        label = "Grain Opacity",
        range = 0.0f..0.2f,
        step = 0.01f,
        default = 0.03f,
        unit = "opacity",
        affectsPerf = false,
        help = "Opacity of the grain effect in the background"
    ),
    
    SelectSpec(
        id = Fps,
        label = "Target FPS",
        options = listOf(30, 60, 90, 120),
        toLabel = { "$it FPS" },
        default = 60,
        help = "Target frame rate for the matrix animation. Higher values may impact battery life"
    )
)

// Timing Settings - Spawn and respawn delay controls
val TIMING_SPECS = listOf(
    SliderSpec(
        id = ColumnStartDelay,
        label = "Spawn Delay",
        range = 0.0f..0.5f,
        step = 0.01f,
        default = 0.01f,
        unit = "s",
        affectsPerf = false,
        help = "Delay before new columns start falling"
    ),
    
    SliderSpec(
        id = ColumnRestartDelay,
        label = "Respawn Delay",
        range = 0.0f..0.5f,
        step = 0.01f,
        default = 0.5f,
        unit = "s",
        affectsPerf = false,
        help = "Delay before columns restart after finishing"
    )
)

// Characters Settings - Text and symbol configuration
val CHARACTERS_SPECS = listOf(
    IntSliderSpec(
        id = FontSize,
        label = "Font Size",
        range = 8..32,
        step = 1,
        default = 14,
        unit = "px",
        affectsPerf = false,
        help = "Size of the matrix characters. Larger sizes may impact performance"
    )
)

// Theme Settings - Colors and visual theming
val THEME_SPECS = listOf(
    ColorSpec(
        id = BgColor,
        label = "Background Color",
        help = "Color of the background behind the matrix rain"
    ),
    
    ColorSpec(
        id = HeadColor,
        label = "Head Color",
        help = "Color of the leading character in each matrix column"
    ),
    
    ColorSpec(
        id = BrightColor,
        label = "Bright Trail Color",
        help = "Color of the brightest part of the matrix trail"
    ),
    
    ColorSpec(
        id = TrailColor,
        label = "Trail Color",
        help = "Color of the main matrix trail"
    ),
    
    ColorSpec(
        id = DimColor,
        label = "Dim Color",
        help = "Color of the dimmest part of the matrix trail"
    ),
    
    ColorSpec(
        id = UiAccent,
        label = "UI Accent Color",
        help = "Primary accent color for the user interface"
    ),
    
    ColorSpec(
        id = UiOverlay,
        label = "UI Overlay Background",
        help = "Background color for UI overlays and panels"
    ),
    
    ColorSpec(
        id = UiSelectBg,
        label = "UI Selection Background",
        help = "Background color for selected UI elements"
    ),
    
    // Advanced color system
    BooleanSpec(
        id = AdvancedColorsEnabled,
        label = "Advanced Colors",
        default = false,
        help = "Enable per-channel color control for fine-tuning"
    ),
    
    BooleanSpec(
        id = LinkUiAndRainColors,
        label = "Link UI & Rain Colors",
        default = false,
        help = "When enabled, UI colors automatically match rain colors"
    )
)

// Developer Settings - Advanced options for developers
val DEVELOPER_SPECS = listOf(
    ToggleSpec(
        id = AlwaysShowHints,
        label = "Always Show Hints",
        default = false,
        help = "Always display swipe-up hints instead of showing them only once"
    )
)

/**
 * All available spec categories for easy iteration.
 */
val ALL_SPEC_CATEGORIES = mapOf(
    "Motion" to MOTION_SPECS,
    "Effects" to EFFECTS_SPECS,
    "Background" to BACKGROUND_SPECS,
    "Timing" to TIMING_SPECS,
    "Characters" to CHARACTERS_SPECS,
    "Theme" to THEME_SPECS,
    "Developer" to DEVELOPER_SPECS
)

/**
 * Get all specs from all categories as a flat list.
 */
val ALL_SPECS = ALL_SPEC_CATEGORIES.values.flatten()

/**
 * Get the total count of all specs across all categories.
 */
val TOTAL_SPEC_COUNT = ALL_SPECS.size
