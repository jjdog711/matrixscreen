package com.example.matrixscreen.ui.settings.model

import com.example.matrixscreen.data.MatrixColor
import com.example.matrixscreen.data.SymbolSet

/**
 * Aligned specifications for all settings organized by category.
 * 
 * This is a backup copy of SettingsSpecs.kt with all keys aligned to match
 * the actual SettingId and MatrixSettings fields. This ensures spec-driven
 * rendering is stable going forward.
 * 
 * All keys now correspond to real SettingId and MatrixSettings fields.
 * Specs for unimplemented fields are commented out with TODO markers.
 */
object SettingsSpecsAligned {
    
    /**
     * Theme settings - colors, presets, and visual themes
     */
    val THEME_SPECS = listOf(
        // TODO(Phase X): add field + id, then restore this spec
        // SettingSpec(
        //     key = "colorTint",
        //     label = "Color Theme",
        //     type = ControlType.Select,
        //     options = MatrixColor.values().map { it.name },
        //     help = "Choose from predefined color themes",
        //     category = SettingCategory.THEME
        // ),
        // TODO(Phase X): add field + id, then restore this spec
        // SettingSpec(
        //     key = "advancedColorsEnabled",
        //     label = "Advanced Colors",
        //     type = ControlType.Toggle,
        //     help = "Enable per-channel color control",
        //     category = SettingCategory.THEME
        // ),
        SettingSpec(
            key = "headColor",
            label = "Head Color",
            type = ControlType.Color,
            help = "Color of the leading character in each column",
            category = SettingCategory.THEME
        ),
        SettingSpec(
            key = "brightTrailColor",
            label = "Bright Trail",
            type = ControlType.Color,
            help = "Color of the bright trail characters",
            category = SettingCategory.THEME
        ),
        SettingSpec(
            key = "trailColor",
            label = "Trail Color",
            type = ControlType.Color,
            help = "Color of the regular trail characters",
            category = SettingCategory.THEME
        ),
        SettingSpec(
            key = "dimColor",
            label = "Dim Trail",
            type = ControlType.Color,
            help = "Color of the dim trail characters",
            category = SettingCategory.THEME
        ),
        // TODO(Phase X): add field + id, then restore this spec
        // SettingSpec(
        //     key = "uiColor",
        //     label = "UI Color",
        //     type = ControlType.Color,
        //     help = "Color for UI elements and text",
        //     category = SettingCategory.THEME
        // ),
        SettingSpec(
            key = "backgroundColor",
            label = "Background",
            type = ControlType.Color,
            help = "Background color behind the rain",
            category = SettingCategory.THEME
        )
    )
    
    /**
     * Character settings - symbols, fonts, and text appearance
     */
    val CHARACTERS_SPECS = listOf(
        // TODO(Phase X): add field + id, then restore this spec
        // SettingSpec(
        //     key = "symbolSet",
        //     label = "Symbol Set",
        //     type = ControlType.Select,
        //     options = SymbolSet.values().map { it.displayName },
        //     help = "Choose the character set for the rain",
        //     category = SettingCategory.CHARACTERS
        // ),
        SettingSpec(
            key = "fontSize",
            label = "Size",
            type = ControlType.Slider,
            range = 8f..24f,
            step = 1f,
            unit = "dp",
            help = "Character size in density-independent pixels",
            category = SettingCategory.CHARACTERS
        )
    )
    
    /**
     * Motion settings - rain speed, columns, and flow control
     */
    val MOTION_SPECS = listOf(
        SettingSpec(
            key = "fallSpeed",
            label = "Rain Speed",
            type = ControlType.Slider,
            range = 0.5f..5.0f,
            step = 0.1f,
            help = "Speed of the falling rain effect",
            category = SettingCategory.MOTION
        ),
        SettingSpec(
            key = "columnCount",
            label = "Columns",
            type = ControlType.Slider,
            range = 50f..200f,
            step = 1f,
            help = "Number of rain columns on screen",
            performanceImpact = true,
            category = SettingCategory.MOTION
        ),
        SettingSpec(
            key = "lineSpacing",
            label = "Line Spacing",
            type = ControlType.Slider,
            range = 0.7f..1.2f,
            step = 0.05f,
            help = "Vertical spacing between characters",
            category = SettingCategory.MOTION
        ),
        SettingSpec(
            key = "activePercentage",
            label = "Active Columns",
            type = ControlType.Slider,
            range = 0.1f..0.9f,
            step = 0.01f,
            unit = "%",
            help = "Percentage of columns that are active",
            category = SettingCategory.MOTION
        ),
        SettingSpec(
            key = "speedVariance",
            label = "Speed Variance",
            type = ControlType.Slider,
            range = 0.0f..0.2f,
            step = 0.005f,
            help = "Random variation in column speeds",
            category = SettingCategory.MOTION
        )
    )
    
    /**
     * Effects settings - glow, jitter, flicker, and visual effects
     */
    val EFFECTS_SPECS = listOf(
        SettingSpec(
            key = "glowIntensity",
            label = "Glow",
            type = ControlType.Slider,
            range = 0.0f..2.0f,
            step = 0.05f,
            help = "Intensity of the glow effect around characters",
            performanceImpact = true,
            category = SettingCategory.EFFECTS
        ),
        SettingSpec(
            key = "jitterAmount",
            label = "Jitter",
            type = ControlType.Slider,
            range = 0.0f..3.0f,
            step = 0.1f,
            unit = "px",
            help = "Random horizontal movement of characters",
            category = SettingCategory.EFFECTS
        ),
        SettingSpec(
            key = "flickerAmount",
            label = "Flicker",
            type = ControlType.Slider,
            range = 0.0f..1.0f,
            step = 0.01f,
            help = "Rate of character flickering effect",
            category = SettingCategory.EFFECTS
        ),
        SettingSpec(
            key = "mutationRate",
            label = "Character Mutation",
            type = ControlType.Slider,
            range = 0.0f..0.25f,
            step = 0.01f,
            help = "Rate at which characters change during animation",
            category = SettingCategory.EFFECTS
        ),
        // TODO(Phase X): add field + id, then restore this spec
        // SettingSpec(
        //     key = "maxTrailLength",
        //     label = "Max Trail Length",
        //     type = ControlType.Slider,
        //     range = 20f..120f,
        //     step = 1f,
        //     help = "Maximum length of character trails",
        //     performanceImpact = true,
        //     category = SettingCategory.EFFECTS
        // ),
        // TODO(Phase X): add field + id, then restore this spec
        // SettingSpec(
        //     key = "maxBrightTrailLength",
        //     label = "Bright Trail Length",
        //     type = ControlType.Slider,
        //     range = 2f..20f,
        //     step = 1f,
        //     help = "Length of the bright trail behind each character",
        //     category = SettingCategory.EFFECTS
        // )
    )
    
    /**
     * Timing settings - spawn delays and timing control
     */
    val TIMING_SPECS: List<SettingSpec> = listOf(
        // TODO(Phase X): add field + id, then restore this spec
        // SettingSpec(
        //     key = "columnStartDelay",
        //     label = "Spawn Delay",
        //     type = ControlType.Slider,
        //     range = 0.0f..0.5f,
        //     step = 0.01f,
        //     unit = "s",
        //     help = "Delay before new columns start falling",
        //     category = SettingCategory.TIMING
        // ),
        // TODO(Phase X): add field + id, then restore this spec
        // SettingSpec(
        //     key = "columnRestartDelay",
        //     label = "Respawn Delay",
        //     type = ControlType.Slider,
        //     range = 0.0f..0.5f,
        //     step = 0.01f,
        //     unit = "s",
        //     help = "Delay before columns restart after finishing",
        //     category = SettingCategory.TIMING
        // )
    )
    
    /**
     * Background settings - film grain and background effects
     */
    val BACKGROUND_SPECS = listOf(
        SettingSpec(
            key = "grainDensity",
            label = "Film Grain Density",
            type = ControlType.Slider,
            range = 0f..400f,
            step = 10f,
            help = "Number of grain particles on screen",
            performanceImpact = true,
            category = SettingCategory.BACKGROUND
        ),
        SettingSpec(
            key = "grainOpacity",
            label = "Film Grain Opacity",
            type = ControlType.Slider,
            range = 0.0f..0.2f,
            step = 0.005f,
            help = "Opacity of the film grain effect",
            category = SettingCategory.BACKGROUND
        ),
        SettingSpec(
            key = "targetFps",
            label = "Frame Rate",
            type = ControlType.Select,
            options = listOf("15", "30", "45", "60", "90", "120"),
            help = "Target frames per second for the animation",
            category = SettingCategory.BACKGROUND
        )
    )
    
    /**
     * Get all specs for a specific category
     */
    fun getSpecsForCategory(category: SettingCategory): List<SettingSpec> {
        return when (category) {
            SettingCategory.THEME -> THEME_SPECS
            SettingCategory.CHARACTERS -> CHARACTERS_SPECS
            SettingCategory.MOTION -> MOTION_SPECS
            SettingCategory.EFFECTS -> EFFECTS_SPECS
            SettingCategory.TIMING -> TIMING_SPECS
            SettingCategory.BACKGROUND -> BACKGROUND_SPECS
        }
    }
    
    /**
     * Get all specs as a flat list
     */
    fun getAllSpecs(): List<SettingSpec> {
        return listOf(
            *THEME_SPECS.toTypedArray(),
            *CHARACTERS_SPECS.toTypedArray(),
            *MOTION_SPECS.toTypedArray(),
            *EFFECTS_SPECS.toTypedArray(),
            *TIMING_SPECS.toTypedArray(),
            *BACKGROUND_SPECS.toTypedArray()
        )
    }
    
    /**
     * Find a spec by key
     */
    fun getSpecByKey(key: String): SettingSpec? {
        return getAllSpecs().find { it.key == key }
    }
}