package com.example.matrixscreen.domain.usecase

import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.registry.ThemePresetId
import com.example.matrixscreen.data.registry.ThemePresetRegistryImpl
import com.example.matrixscreen.data.registry.ThemeColorConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for resolving final ARGB colors from MatrixSettings.
 * 
 * This handles the color derivation pipeline:
 * 1. Apply theme preset if selected
 * 2. Apply advanced color logic (advancedColorsEnabled, linkUiAndRainColors)
 * 3. Return final resolved colors for the renderer
 */
@Singleton
class ResolveColors @Inject constructor() {
    
    /**
     * Resolve final ARGB colors from MatrixSettings.
     * 
     * @param settings The user settings to resolve colors from
     * @return ResolvedColors with final ARGB values for the renderer
     */
    fun execute(settings: MatrixSettings): ThemeColorConfig {
        // Step 1: Start with base colors from settings
        var resolvedColors = ThemeColorConfig(
            backgroundColor = settings.backgroundColor,
            headColor = settings.headColor,
            brightTrailColor = settings.brightTrailColor,
            trailColor = settings.trailColor,
            dimColor = settings.dimColor,
            uiAccent = settings.uiAccent,
            uiOverlayBg = settings.uiOverlayBg,
            uiSelectionBg = settings.uiSelectionBg
        )
        
        // Step 2: Apply theme preset if selected
        if (settings.themePresetId != null) {
            val themeRegistry = ThemePresetRegistryImpl()
            val themeId = ThemePresetId(settings.themePresetId)
            val presetColors = themeRegistry.getColors(themeId)
            
            resolvedColors = resolvedColors.copy(
                backgroundColor = presetColors.backgroundColor,
                headColor = presetColors.headColor,
                brightTrailColor = presetColors.brightTrailColor,
                trailColor = presetColors.trailColor,
                dimColor = presetColors.dimColor,
                uiAccent = presetColors.uiAccent,
                uiOverlayBg = presetColors.uiOverlayBg,
                uiSelectionBg = presetColors.uiSelectionBg
            )
        }
        
        // Step 3: Apply advanced color logic
        if (settings.advancedColorsEnabled) {
            resolvedColors = applyAdvancedColorLogic(resolvedColors, settings)
        }
        
        // Step 4: Apply UI/Rain color linking if enabled
        if (settings.linkUiAndRainColors) {
            resolvedColors = applyUiRainColorLinking(resolvedColors)
        }
        
        return resolvedColors
    }
    
    /**
     * Apply advanced color logic (placeholder for future enhancements).
     */
    private fun applyAdvancedColorLogic(colors: ThemeColorConfig, settings: MatrixSettings): ThemeColorConfig {
        // For now, just return colors as-is
        // Future: Could apply color temperature, saturation, brightness adjustments, etc.
        return colors
    }
    
    /**
     * Apply UI/Rain color linking logic.
     */
    private fun applyUiRainColorLinking(colors: ThemeColorConfig): ThemeColorConfig {
        // Link UI accent color to the brightest rain color (headColor)
        return colors.copy(
            uiAccent = colors.headColor,
            uiSelectionBg = (colors.headColor and 0x40FFFFFFL) // 25% opacity of head color
        )
    }
}

// Using ThemeColorConfig from the registry system instead of a separate ResolvedColors class
