package com.example.matrixscreen.engine.uniforms

import com.example.matrixscreen.MatrixAnimationConfig
import com.example.matrixscreen.data.MatrixColor

/**
 * Mapper for converting RendererParams to MatrixAnimationConfig.
 * 
 * This mapper maintains compatibility with the existing engine while allowing
 * the new parameter mapping system to work. It converts the new RendererParams
 * structure to the existing MatrixAnimationConfig that the engine expects.
 */
object RendererParamsMapper {
    
    /**
     * Convert RendererParams to MatrixAnimationConfig for engine compatibility.
     * 
     * @param params The renderer parameters to convert
     * @param screenRows Number of screen rows (calculated from screen dimensions)
     * @param rowHeight Row height in pixels (calculated from font size)
     * @param matrixColor The matrix color enum (converted from ARGB values)
     * @return MatrixAnimationConfig compatible with existing engine
     */
    fun toMatrixAnimationConfig(
        params: RendererParams,
        screenRows: Int,
        rowHeight: Float,
        matrixColor: MatrixColor = MatrixColor.GREEN
    ): MatrixAnimationConfig {
        return MatrixAnimationConfig(
            // Basic parameters
            fontSize = params.fontSize,
            columnCount = params.columnCount,
            rowHeight = rowHeight,
            screenRows = screenRows,
            targetFps = params.effectiveFps, // Use effective FPS for engine
            
            // Motion parameters
            printSpeedMultiplier = params.fallSpeed,
            matrixColor = matrixColor,
            maxTrailLength = params.maxTrailLength,
            maxBrightTrailLength = params.maxBrightTrailLength,
            
            // Visual effects
            glowIntensity = params.glowIntensity,
            jitterAmount = params.jitterAmount,
            flickerRate = params.flickerAmount,
            mutationRate = params.mutationRate,
            
            // Timing & Behavior
            columnStartDelay = params.columnStartDelay,
            columnRestartDelay = params.columnRestartDelay,
            initialActivePercentage = params.initialActivePercentage,
            speedVariationRate = params.speedVariationRate,
            
            // Background Effects (from BACKGROUND_SPECS)
            grainDensity = params.grainDensity,
            grainOpacity = params.grainOpacity
        )
    }
    
    /**
     * Convert RendererParams to MatrixAnimationConfig with default screen parameters.
     * 
     * @param params The renderer parameters to convert
     * @return MatrixAnimationConfig with default screen parameters
     */
    fun toMatrixAnimationConfig(params: RendererParams): MatrixAnimationConfig {
        return toMatrixAnimationConfig(
            params = params,
            screenRows = 50, // Default screen rows
            rowHeight = params.fontSize * 1.2f, // Default row height
            matrixColor = MatrixColor.GREEN
        )
    }
}
