package com.example.matrixscreen.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.matrixscreen.data.MatrixSettings

/**
 * Modern color scheme system for MatrixScreen UI
 * Dynamically pulls colors from MatrixSettings with proper fallbacks
 */

/**
 * UI Color scheme based on MatrixSettings
 * All colors are dynamically derived from user preferences
 */
data class MatrixUIColorScheme(
    // Primary colors from MatrixSettings
    val primary: Color,
    val primaryDim: Color,
    val primaryBright: Color,
    
    // Background colors
    val background: Color,
    val backgroundSecondary: Color,
    val overlayBackground: Color, // 85% opacity overlay
    
    // Border colors
    val border: Color,
    val borderDim: Color,
    
    // Text colors
    val textPrimary: Color,
    val textSecondary: Color,
    val textAccent: Color,
    
    // Interactive element colors
    val buttonConfirm: Color,
    val buttonCancel: Color,
    val buttonCancelText: Color,
    
    // Slider colors
    val sliderActive: Color,
    val sliderInactive: Color,
    
    // Selection colors
    val selectionBackground: Color,
    
    // Glow colors (derived from textGlowIntensity)
    val textGlow: Color,
    val buttonGlow: Color
)

/**
 * Generate UI color scheme from MatrixSettings
 * Handles both basic and advanced color modes
 */
@Composable
fun getMatrixUIColorScheme(settings: MatrixSettings): MatrixUIColorScheme {
    // Get base color from settings with error handling
    val baseColor = try {
        if (settings.advancedColorsEnabled) {
            Color(settings.getEffectiveUiColor())
        } else {
            settings.colorTint.color
        }
    } catch (e: Exception) {
        android.util.Log.e("ColorScheme", "Error getting base color: ${e.message}")
        Color(0xFF00CC00) // Fallback to Matrix green
    }
    
    // Calculate glow colors based on textGlowIntensity
    val glowIntensity = settings.glowIntensity.coerceIn(0f, 2f)
    val textGlow = baseColor.copy(alpha = (glowIntensity * 0.3f).coerceIn(0f, 0.6f))
    val buttonGlow = baseColor.copy(alpha = (glowIntensity * 0.2f).coerceIn(0f, 0.4f))
    
    return MatrixUIColorScheme(
        // Primary colors
        primary = baseColor,
        primaryDim = baseColor.copy(alpha = 0.7f),
        primaryBright = baseColor.copy(alpha = 1.0f),
        
        // Background colors - use dynamic background from settings with error handling
        background = try {
            Color(settings.getEffectiveBackgroundColor())
        } catch (e: Exception) {
            android.util.Log.e("ColorScheme", "Error getting background color: ${e.message}")
            Color(0xFF000000) // Fallback to black
        },
        backgroundSecondary = try {
            Color(settings.getEffectiveBackgroundColor()).copy(alpha = 0.8f)
        } catch (e: Exception) {
            Color(0xFF000000).copy(alpha = 0.8f)
        },
        overlayBackground = try {
            Color(settings.getEffectiveBackgroundColor()).copy(alpha = 0.85f) // 85% opacity
        } catch (e: Exception) {
            Color(0xFF000000).copy(alpha = 0.85f)
        },
        
        // Border colors
        border = baseColor,
        borderDim = baseColor.copy(alpha = 0.3f),
        
        // Text colors - adapt to background with error handling
        textPrimary = try {
            if (isLightBackground(settings.getEffectiveBackgroundColor())) {
                Color(0xFF1A1A1A) // Dark text for light backgrounds
            } else {
                Color(0xFFCCCCCC) // Light text for dark backgrounds
            }
        } catch (e: Exception) {
            android.util.Log.e("ColorScheme", "Error calculating text color: ${e.message}")
            Color(0xFFCCCCCC) // Fallback to light text
        },
        textSecondary = try {
            if (isLightBackground(settings.getEffectiveBackgroundColor())) {
                Color(0xFF666666)
            } else {
                Color(0xFF666666)
            }
        } catch (e: Exception) {
            Color(0xFF666666) // Fallback
        },
        textAccent = baseColor,
        
        // Interactive element colors
        buttonConfirm = baseColor.copy(alpha = 0.2f),
        buttonCancel = Color(0xFF330000),
        buttonCancelText = Color(0xFFFF6666),
        
        // Slider colors
        sliderActive = baseColor,
        sliderInactive = baseColor.copy(alpha = 0.3f),
        
        // Selection colors
        selectionBackground = baseColor.copy(alpha = 0.2f),
        
        // Glow colors
        textGlow = textGlow,
        buttonGlow = buttonGlow
    )
}

/**
 * Fallback color scheme for error states or missing settings
 */
@Composable
fun getFallbackUIColorScheme(): MatrixUIColorScheme {
    val fallbackGreen = Color(0xFF33FF66) // Default Matrix green
    val fallbackBackground = Color(0xFF121212) // Dark background
    
    return MatrixUIColorScheme(
        primary = fallbackGreen,
        primaryDim = fallbackGreen.copy(alpha = 0.7f),
        primaryBright = fallbackGreen.copy(alpha = 1.0f),
        
        background = fallbackBackground,
        backgroundSecondary = fallbackBackground.copy(alpha = 0.8f),
        overlayBackground = fallbackBackground.copy(alpha = 0.85f),
        
        border = fallbackGreen,
        borderDim = fallbackGreen.copy(alpha = 0.3f),
        
        textPrimary = Color(0xFFCCCCCC),
        textSecondary = Color(0xFF666666),
        textAccent = fallbackGreen,
        
        buttonConfirm = fallbackGreen.copy(alpha = 0.2f),
        buttonCancel = Color(0xFF330000),
        buttonCancelText = Color(0xFFFF6666),
        
        sliderActive = fallbackGreen,
        sliderInactive = fallbackGreen.copy(alpha = 0.3f),
        
        selectionBackground = fallbackGreen.copy(alpha = 0.2f),
        
        textGlow = fallbackGreen.copy(alpha = 0.3f),
        buttonGlow = fallbackGreen.copy(alpha = 0.2f)
    )
}

/**
 * Check if a background color is light (for text color adaptation)
 */
private fun isLightBackground(backgroundColor: Long): Boolean {
    val color = Color(backgroundColor)
    val rgb = color.toArgb()
    val red = (rgb shr 16) and 0xFF
    val green = (rgb shr 8) and 0xFF
    val blue = rgb and 0xFF
    
    // Calculate luminance
    val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
    return luminance > 0.5
}

/**
 * Get color scheme with error handling
 */
@Composable
fun getSafeUIColorScheme(settings: MatrixSettings?): MatrixUIColorScheme {
    return if (settings != null) {
        getMatrixUIColorScheme(settings)
    } else {
        getFallbackUIColorScheme()
    }
}
