package com.example.matrixscreen.ui.settings.model

/**
 * Helper functions for working with WidgetSpec collections.
 * 
 * These utilities provide type-safe ways to find and work with WidgetSpecs,
 * centralizing the only necessary cast in the system.
 */

/**
 * Find a WidgetSpec by its SettingId with proper type safety.
 * 
 * This function centralizes the only cast needed in the system, providing
 * a clean way to find typed WidgetSpecs from collections.
 * 
 * @param id The SettingId to search for
 * @return The matching WidgetSpec with proper type
 * @throws IllegalArgumentException if no matching spec is found
 */
@Suppress("UNCHECKED_CAST")
fun <T> List<WidgetSpec<*>>.specFor(id: SettingId<T>): WidgetSpec<T> {
    return find { it.id == id }?.let { it as WidgetSpec<T> }
        ?: throw IllegalArgumentException("No spec found for SettingId: ${id.key}")
}

/**
 * Get the default value from a spec by extracting it based on the spec type.
 */
fun <T> WidgetSpec<T>.getDefault(): T {
    @Suppress("UNCHECKED_CAST")
    return when (this) {
        is SliderSpec -> default as T
        is IntSliderSpec -> default as T
        is ToggleSpec -> default as T
        is BooleanSpec -> default as T
        is ColorSpec -> 0L as T // ColorSpec doesn't have a default field
        is SelectSpec<*> -> default as T
    }
}

/**
 * Get the default value from a spec collection by SettingId.
 */
fun <T> List<WidgetSpec<*>>.defaultFor(id: SettingId<T>): T {
    return specFor(id).getDefault()
}