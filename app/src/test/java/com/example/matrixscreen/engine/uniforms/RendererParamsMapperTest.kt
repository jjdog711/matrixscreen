package com.example.matrixscreen.engine.uniforms

import com.example.matrixscreen.data.MatrixColor
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for RendererParamsMapper.
 * 
 * Tests the conversion from RendererParams to MatrixAnimationConfig,
 * ensuring compatibility with the existing engine.
 */
class RendererParamsMapperTest {
    
    @Test
    fun `toMatrixAnimationConfig should map all parameters correctly`() {
        // Given
        val params = RendererParams(
            effectiveFps = 90f,
            targetFps = 90f,
            grainDensity = 300,
            grainOpacity = 0.05f,
            fallSpeed = 2.5f,
            columnCount = 200,
            lineSpacing = 1.0f,
            activePercentage = 0.5f,
            speedVariance = 0.02f,
            glowIntensity = 3.0f,
            jitterAmount = 2.5f,
            flickerAmount = 0.3f,
            mutationRate = 0.1f,
            columnStartDelay = 0.01f,
            columnRestartDelay = 0.01f,
            initialActivePercentage = 0.5f,
            speedVariationRate = 0.02f,
            fontSize = 16f,
            maxTrailLength = 120,
            maxBrightTrailLength = 20,
            backgroundColor = 0xFF111111L,
            headColor = 0xFF00FF00L,
            brightTrailColor = 0xFF00CC00L,
            trailColor = 0xFF008800L,
            dimColor = 0xFF004400L
        )
        
        val screenRows = 60
        val rowHeight = 20f
        val matrixColor = MatrixColor.GREEN
        
        // When
        val result = RendererParamsMapper.toMatrixAnimationConfig(
            params = params,
            screenRows = screenRows,
            rowHeight = rowHeight,
            matrixColor = matrixColor
        )
        
        // Then
        assertEquals(16f, result.fontSize)
        assertEquals(200, result.columnCount)
        assertEquals(rowHeight, result.rowHeight)
        assertEquals(screenRows, result.screenRows)
        assertEquals(90f, result.targetFps) // Should use effective FPS
        assertEquals(2.5f, result.printSpeedMultiplier)
        assertEquals(matrixColor, result.matrixColor)
        assertEquals(120, result.maxTrailLength)
        assertEquals(20, result.maxBrightTrailLength)
        assertEquals(3.0f, result.glowIntensity)
        assertEquals(2.5f, result.jitterAmount)
        assertEquals(0.3f, result.flickerRate)
        assertEquals(0.1f, result.mutationRate)
        assertEquals(0.01f, result.columnStartDelay)
        assertEquals(0.01f, result.columnRestartDelay)
        assertEquals(0.5f, result.initialActivePercentage)
        assertEquals(0.02f, result.speedVariationRate)
        assertEquals(300, result.grainDensity)
        assertEquals(0.05f, result.grainOpacity)
    }
    
    @Test
    fun `toMatrixAnimationConfig with defaults should use default values`() {
        // Given
        val params = RendererParams.default()
        
        // When
        val result = RendererParamsMapper.toMatrixAnimationConfig(params)
        
        // Then
        assertEquals(14f, result.fontSize)
        assertEquals(150, result.columnCount)
        assertEquals(16.8f, result.rowHeight) // fontSize * 1.2f
        assertEquals(50, result.screenRows) // Default screen rows
        assertEquals(60f, result.targetFps) // Default effective FPS
        assertEquals(2.0f, result.printSpeedMultiplier)
        assertEquals(MatrixColor.GREEN, result.matrixColor)
        assertEquals(100, result.maxTrailLength)
        assertEquals(15, result.maxBrightTrailLength)
        assertEquals(2.0f, result.glowIntensity)
        assertEquals(2.0f, result.jitterAmount)
        assertEquals(0.2f, result.flickerRate)
        assertEquals(0.08f, result.mutationRate)
        assertEquals(0.01f, result.columnStartDelay)
        assertEquals(0.01f, result.columnRestartDelay)
        assertEquals(0.4f, result.initialActivePercentage)
        assertEquals(0.01f, result.speedVariationRate)
        assertEquals(200, result.grainDensity)
        assertEquals(0.03f, result.grainOpacity)
    }
    
    @Test
    fun `toMatrixAnimationConfig should handle different matrix colors`() {
        // Given
        val params = RendererParams.default()
        val matrixColor = MatrixColor.BLUE
        
        // When
        val result = RendererParamsMapper.toMatrixAnimationConfig(
            params = params,
            screenRows = 50,
            rowHeight = 20f,
            matrixColor = matrixColor
        )
        
        // Then
        assertEquals(matrixColor, result.matrixColor)
    }
    
    @Test
    fun `toMatrixAnimationConfig should handle different screen dimensions`() {
        // Given
        val params = RendererParams.default()
        val screenRows = 100
        val rowHeight = 25f
        
        // When
        val result = RendererParamsMapper.toMatrixAnimationConfig(
            params = params,
            screenRows = screenRows,
            rowHeight = rowHeight,
            matrixColor = MatrixColor.GREEN
        )
        
        // Then
        assertEquals(screenRows, result.screenRows)
        assertEquals(rowHeight, result.rowHeight)
    }
    
    @Test
    fun `toMatrixAnimationConfig should preserve effective FPS`() {
        // Given
        val params = RendererParams(
            effectiveFps = 120f,
            targetFps = 90f, // Different from effective
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
            dimColor = 0xFF004400L
        )
        
        // When
        val result = RendererParamsMapper.toMatrixAnimationConfig(params)
        
        // Then
        assertEquals(120f, result.targetFps) // Should use effective FPS, not target FPS
    }
}
