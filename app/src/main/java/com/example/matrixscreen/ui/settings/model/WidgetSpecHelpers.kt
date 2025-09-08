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
 * @throws NoSuchElementException if no matching spec is found
 */
@Suppress("UNCHECKED_CAST")
fun <T> List<WidgetSpec<*>>.specFor(id: SettingId<T>): WidgetSpec<T> =
    first { it.id == id } as WidgetSpec<T>
