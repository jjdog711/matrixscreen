package com.example.matrixscreen.engine.uniforms

/**
 * Renderer parameters for the Matrix animation engine.
 * 
 * This data class contains all the parameters needed by the rendering engine,
 * including effective FPS (after device coercion) and grain parameters.
 * The engine uses these parameters directly without needing to know about
 * device capabilities or user settings.
 */
data class RendererParams(
    // Performance parameters
    val effectiveFps: Float,
    val targetFps: Float,
    
    // Background effects
    val grainDensity: Int,
    val grainOpacity: Float,
    
    // Motion parameters
    val fallSpeed: Float,
    val columnCount: Int,
    val lineSpacing: Float,
    val activePercentage: Float,
    val speedVariance: Float,
    
    // Visual effects
    val glowIntensity: Float,
    val jitterAmount: Float,
    val flickerAmount: Float,
    val mutationRate: Float,
    
    // Timing parameters
    val columnStartDelay: Float,
    val columnRestartDelay: Float,
    val initialActivePercentage: Float,
    val speedVariationRate: Float,
    
    // Character parameters
    val fontSize: Float,
    val maxTrailLength: Int,
    val maxBrightTrailLength: Int,
    
    // Color parameters (stored as ARGB Long values)
    val backgroundColor: Long,
    val headColor: Long,
    val brightTrailColor: Long,
    val trailColor: Long,
    val dimColor: Long,
    
    // UI theme colors
    val uiAccent: Long,
    val uiOverlayBg: Long,
    val uiSelectionBg: Long
) {
    companion object {
        /**
         * Create default renderer parameters.
         */
        fun default(): RendererParams {
            return RendererParams(
                effectiveFps = 60f,
                targetFps = 60f,
                grainDensity = 200,
                grainOpacity = 0.03f,
                fallSpeed = 2.0f,
                columnCount = 150,
                lineSpacing = 0.9f,
                activePercentage = 0.4f,
                speedVariance = 0.01f,
                glowIntensity = 2.0f,
                jitterAmount = 2.0f,
                flickerAmount = 0.2f,
                mutationRate = 0.08f,
                columnStartDelay = 0.01f,
                columnRestartDelay = 0.01f,
                initialActivePercentage = 0.4f,
                speedVariationRate = 0.01f,
                fontSize = 14f,
                maxTrailLength = 100,
                maxBrightTrailLength = 15,
                backgroundColor = 0xFF000000L,
                headColor = 0xFF00FF00L,
                brightTrailColor = 0xFF00CC00L,
                trailColor = 0xFF008800L,
                dimColor = 0xFF004400L,
                uiAccent = 0xFF00CC00L,
                uiOverlayBg = 0x80000000L,
                uiSelectionBg = 0x4000FF00L
            )
        }
    }
}
