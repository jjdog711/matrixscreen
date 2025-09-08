package com.example.matrixscreen.ui.preview.components

import androidx.compose.ui.graphics.Color
import com.example.matrixscreen.data.model.MatrixSettings

/**
 * Fake data for component previews.
 * 
 * This provides sample data for previewing UI components without
 * requiring real settings or theme data.
 */

// Fake MatrixSettings for previews
val FakeMatrixSettings = MatrixSettings(
    // Motion settings
    fallSpeed = 2.5f,
    columnCount = 200,
    lineSpacing = 1.0f,
    activePercentage = 0.6f,
    speedVariance = 0.02f,
    
    // Effects settings
    glowIntensity = 2.5f,
    jitterAmount = 1.8f,
    flickerAmount = 0.3f,
    mutationRate = 0.1f,
    
    // Background settings
    grainDensity = 250,
    grainOpacity = 0.04f,
    targetFps = 90,
    
    // Color settings
    backgroundColor = 0xFF000000L,
    headColor = 0xFF00FF00L,
    brightTrailColor = 0xFF00CC00L,
    trailColor = 0xFF008800L,
    dimColor = 0xFF004400L,
    
    // UI theme colors
    uiAccent = 0xFF00FF00L, // Use bright electric green like matrix rain
    uiOverlayBg = 0x26000000L, // Much more transparent for floating glass effect
    uiSelectionBg = 0x4000FF00L,
    
    // Character settings
    fontSize = 16
)

// Fake color values for previews - Cyberpunk 2077 style
val FakeColors = listOf(
    0xFF00FF00L, // Matrix green (accent)
    0xFFFFFFFF, // Pure white (primary text)
    0xFFCCCCCCL, // Light gray (secondary text)
    0xFF00FFFFL, // Cyan
    0xFFFF00FFL, // Magenta
    0xFFFFFF00L, // Yellow
    0xFFFF0000L, // Red
    0xFF0000FFL  // Blue
)

// Fake FPS options for previews
val FakeFpsOptions = listOf(15, 30, 45, 60, 90, 120)

// Fake boolean options for previews
val FakeBooleanOptions = listOf(true, false)
