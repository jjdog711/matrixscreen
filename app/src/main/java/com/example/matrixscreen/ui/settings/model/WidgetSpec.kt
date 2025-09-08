package com.example.matrixscreen.ui.settings.model

/**
 * Widget specifications that describe settings elements as data.
 * 
 * The WidgetSpec system provides a data-driven approach to UI generation,
 * where each setting is defined by a typed specification that describes
 * how it should be rendered and what constraints it has.
 * 
 * This enables the UI to be generated from specifications rather than
 * hardcoded components, making it easy to add, remove, or reorder settings
 * without modifying UI code.
 */
sealed interface WidgetSpec<T> {
    /** The SettingId that this spec represents */
    val id: SettingId<T>
    
    /** Human-readable label for the setting */
    val label: String
    
    /** Optional help text for the setting */
    val help: String?
}

/**
 * Specification for a floating-point slider control.
 * 
 * @param id The SettingId for this setting
 * @param label Human-readable label
 * @param range Valid range for the slider value
 * @param step Step size for the slider
 * @param default Default value for the setting
 * @param unit Optional unit string (e.g., "ms", "px")
 * @param affectsPerf Whether this setting affects performance
 * @param help Optional help text
 */
data class SliderSpec(
    override val id: SettingId<Float>,
    override val label: String,
    val range: ClosedFloatingPointRange<Float>,
    val step: Float,
    val default: Float,
    val unit: String? = null,
    val affectsPerf: Boolean = false,
    override val help: String? = null
) : WidgetSpec<Float>

/**
 * Specification for an integer slider control.
 * 
 * @param id The SettingId for this setting
 * @param label Human-readable label
 * @param range Valid range for the slider value
 * @param step Step size for the slider (defaults to 1)
 * @param default Default value for the setting
 * @param unit Optional unit string (e.g., "fps", "px")
 * @param affectsPerf Whether this setting affects performance
 * @param help Optional help text
 */
data class IntSliderSpec(
    override val id: SettingId<Int>,
    override val label: String,
    val range: IntRange,
    val step: Int = 1,
    val default: Int,
    val unit: String? = null,
    val affectsPerf: Boolean = false,
    override val help: String? = null
) : WidgetSpec<Int>

/**
 * Specification for a toggle/switch control.
 * 
 * @param id The SettingId for this setting
 * @param label Human-readable label
 * @param default Default value for the setting
 * @param help Optional help text
 */
data class ToggleSpec(
    override val id: SettingId<Boolean>,
    override val label: String,
    val default: Boolean,
    override val help: String? = null
) : WidgetSpec<Boolean>

/**
 * Specification for a boolean setting (alias for ToggleSpec for clarity).
 * 
 * @param id The SettingId for this setting
 * @param label Human-readable label
 * @param default Default value for the setting
 * @param help Optional help text
 */
data class BooleanSpec(
    override val id: SettingId<Boolean>,
    override val label: String,
    val default: Boolean,
    override val help: String? = null
) : WidgetSpec<Boolean>

/**
 * Specification for a color picker control.
 * 
 * @param id The SettingId for this setting
 * @param label Human-readable label
 * @param help Optional help text
 */
data class ColorSpec(
    override val id: SettingId<Long>,
    override val label: String,
    override val help: String? = null
) : WidgetSpec<Long>

/**
 * Specification for a selection control (dropdown/chips).
 * 
 * @param id The SettingId for this setting
 * @param label Human-readable label
 * @param options List of available options
 * @param toLabel Function to convert option to display label
 * @param default Default value for the setting
 * @param help Optional help text
 */
data class SelectSpec<T : Any>(
    override val id: SettingId<T>,
    override val label: String,
    val options: List<T>,
    val toLabel: (T) -> String,
    val default: T,
    override val help: String? = null
) : WidgetSpec<T>
