package com.example.matrixscreen.domain.usecase

import android.content.Context
import com.example.matrixscreen.core.util.FpsCoercionUtil
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.engine.uniforms.RendererParams
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Use case for mapping MatrixSettings to RendererParams with device-aware FPS coercion.
 * 
 * This use case handles the transformation from user settings to engine parameters,
 * ensuring that FPS values are properly coerced to device-supported rates and
 * all parameters are in the correct format for the rendering engine.
 */
@Singleton
class MapSettingsToRendererParams @Inject constructor(
    private val resolveColors: ResolveColors
) {
    
    /**
     * Map MatrixSettings to RendererParams with device-aware FPS coercion.
     * 
     * @param settings The user settings to map
     * @param context Android context for device capability detection
     * @return RendererParams with effective FPS and all other parameters mapped
     */
    fun execute(settings: MatrixSettings, context: Context): RendererParams {
        // Get device-supported refresh rates
        val supportedRates = FpsCoercionUtil.getSupportedRefreshRates(context)
        
        // Coerce FPS to device-supported rate
        val effectiveFps = FpsCoercionUtil.coerceFps(
            targetFps = settings.targetFps,
            supportedRates = supportedRates
        ).toFloat()
        
        // Resolve final colors (handles theme presets, advanced colors, UI/Rain linking)
        val resolvedColors = resolveColors.execute(settings)
        
        return RendererParams(
            // Performance parameters with device-aware FPS
            effectiveFps = effectiveFps,
            targetFps = settings.targetFps.toFloat(),
            
            // Background effects (from BACKGROUND_SPECS)
            grainDensity = settings.grainDensity,
            grainOpacity = settings.grainOpacity,
            
            // Motion parameters
            fallSpeed = settings.fallSpeed,
            columnCount = settings.columnCount,
            lineSpacing = settings.lineSpacing,
            activePercentage = settings.activePercentage,
            speedVariance = settings.speedVariance,
            
            // Visual effects
            glowIntensity = settings.glowIntensity,
            jitterAmount = settings.jitterAmount,
            flickerAmount = settings.flickerAmount,
            mutationRate = settings.mutationRate,
            
            // Timing parameters
            columnStartDelay = settings.columnStartDelay,
            columnRestartDelay = settings.columnRestartDelay,
            initialActivePercentage = settings.activePercentage,
            speedVariationRate = settings.speedVariance,
            
            // Character parameters
            fontSize = settings.fontSize.toFloat(),
            maxTrailLength = settings.maxTrailLength,
            maxBrightTrailLength = settings.maxBrightTrailLength,
            
            // Color parameters (ARGB Long values)
            backgroundColor = resolvedColors.backgroundColor,
            headColor = resolvedColors.headColor,
            brightTrailColor = resolvedColors.brightTrailColor,
            trailColor = resolvedColors.trailColor,
            dimColor = resolvedColors.dimColor,
            
            // UI theme colors - resolved from color pipeline
            uiAccent = resolvedColors.uiAccent,
            uiOverlayBg = resolvedColors.uiOverlayBg,
            uiSelectionBg = resolvedColors.uiSelectionBg
        )
    }
    
    /**
     * Map MatrixSettings to RendererParams with pre-computed effective FPS.
     * 
     * This overload allows for cases where FPS coercion has already been computed,
     * avoiding redundant device capability detection.
     * 
     * @param settings The user settings to map
     * @param effectiveFps The pre-computed effective FPS value
     * @return RendererParams with the provided effective FPS
     */
    fun execute(settings: MatrixSettings, effectiveFps: Float): RendererParams {
        // Resolve final colors (handles theme presets, advanced colors, UI/Rain linking)
        val resolvedColors = resolveColors.execute(settings)
        
        return RendererParams(
            // Performance parameters with provided effective FPS
            effectiveFps = effectiveFps,
            targetFps = settings.targetFps.toFloat(),
            
            // Background effects (from BACKGROUND_SPECS)
            grainDensity = settings.grainDensity,
            grainOpacity = settings.grainOpacity,
            
            // Motion parameters
            fallSpeed = settings.fallSpeed,
            columnCount = settings.columnCount,
            lineSpacing = settings.lineSpacing,
            activePercentage = settings.activePercentage,
            speedVariance = settings.speedVariance,
            
            // Visual effects
            glowIntensity = settings.glowIntensity,
            jitterAmount = settings.jitterAmount,
            flickerAmount = settings.flickerAmount,
            mutationRate = settings.mutationRate,
            
            // Timing parameters
            columnStartDelay = settings.columnStartDelay,
            columnRestartDelay = settings.columnRestartDelay,
            initialActivePercentage = settings.activePercentage,
            speedVariationRate = settings.speedVariance,
            
            // Character parameters
            fontSize = settings.fontSize.toFloat(),
            maxTrailLength = settings.maxTrailLength,
            maxBrightTrailLength = settings.maxBrightTrailLength,
            
            // Color parameters (ARGB Long values)
            backgroundColor = resolvedColors.backgroundColor,
            headColor = resolvedColors.headColor,
            brightTrailColor = resolvedColors.brightTrailColor,
            trailColor = resolvedColors.trailColor,
            dimColor = resolvedColors.dimColor,
            
            // UI theme colors - resolved from color pipeline
            uiAccent = resolvedColors.uiAccent,
            uiOverlayBg = resolvedColors.uiOverlayBg,
            uiSelectionBg = resolvedColors.uiSelectionBg
        )
    }
}
