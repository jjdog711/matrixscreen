package com.example.matrixscreen.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.matrixscreen.data.model.MatrixSettings

/**
 * Modern color scheme system for MatrixScreen UI
 * Dynamically pulls colors from MatrixSettings with proper fallbacks
 */

/**
 * UI Color scheme based on MatrixSettings
 * All colors are dynamically derived from user preferences
 */
data class MatrixUIColorScheme(
    // Text colors
    val textPrimary: Color,
    val textSecondary: Color,
    val textAccent: Color,
    
    // Background colors
    val backgroundPrimary: Color,
    val backgroundSecondary: Color,
    val overlayBackground: Color, // 85% opacity overlay
    
    // Slider colors
    val sliderActive: Color,
    val sliderInactive: Color,
    
    // Primary colors from MatrixSettings
    val primary: Color,
    val borderDim: Color,
    val buttonCancelText: Color,
    
    // Legacy compatibility (keep for existing code)
    val primaryDim: Color,
    val primaryBright: Color,
    val background: Color,
    val border: Color,
    val buttonConfirm: Color,
    val buttonCancel: Color,
    val selectionBackground: Color,
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
        Color(settings.uiAccent)
    } catch (e: Exception) {
        android.util.Log.e("ColorScheme", "Error getting base color: ${e.message}")
        Color(0xFF00CC00) // Fallback to Matrix green
    }
    
    // Calculate glow colors based on textGlowIntensity
    val glowIntensity = settings.glowIntensity.coerceIn(0f, 2f)
    val textGlow = baseColor.copy(alpha = (glowIntensity * 0.3f).coerceIn(0f, 0.6f))
    val buttonGlow = baseColor.copy(alpha = (glowIntensity * 0.2f).coerceIn(0f, 0.4f))
    
    return MatrixUIColorScheme(
        // Text colors - adapt to background with error handling
        textPrimary = try {
            if (isLightBackground(settings.backgroundColor)) {
                Color(0xFF1A1A1A) // Dark text for light backgrounds
            } else {
                Color(0xFFCCCCCC) // Light text for dark backgrounds
            }
        } catch (e: Exception) {
            android.util.Log.e("ColorScheme", "Error calculating text color: ${e.message}")
            Color(0xFFCCCCCC) // Fallback to light text
        },
        textSecondary = try {
            if (isLightBackground(settings.backgroundColor)) {
                Color(0xFF666666)
            } else {
                Color(0xFF666666)
            }
        } catch (e: Exception) {
            Color(0xFF666666) // Fallback
        },
        textAccent = baseColor,
        
        // Background colors - use dynamic background from settings with error handling
        backgroundPrimary = try {
            Color(settings.backgroundColor)
        } catch (e: Exception) {
            android.util.Log.e("ColorScheme", "Error getting background color: ${e.message}")
            Color(0xFF000000) // Fallback to black
        },
        backgroundSecondary = try {
            Color(settings.backgroundColor).copy(alpha = 0.8f)
        } catch (e: Exception) {
            Color(0xFF000000).copy(alpha = 0.8f)
        },
        overlayBackground = Color(settings.uiOverlayBg).copy(alpha = 0.85f),
        
        // Slider colors
        sliderActive = baseColor,
        sliderInactive = baseColor.copy(alpha = 0.3f),
        
        // Primary colors from MatrixSettings
        primary = baseColor,
        borderDim = baseColor.copy(alpha = 0.3f),
        buttonCancelText = Color(0xFFFF6666),
        
        // Legacy compatibility (keep for existing code)
        primaryDim = baseColor.copy(alpha = 0.7f),
        primaryBright = baseColor.copy(alpha = 1.0f),
        background = try {
            Color(settings.backgroundColor)
        } catch (e: Exception) {
            Color(0xFF000000)
        },
        border = baseColor,
        buttonConfirm = baseColor.copy(alpha = 0.2f),
        buttonCancel = Color(0xFF330000),
        selectionBackground = Color(settings.uiSelectionBg),
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
        // Text colors
        textPrimary = Color(0xFFCCCCCC),
        textSecondary = Color(0xFF666666),
        textAccent = fallbackGreen,
        
        // Background colors
        backgroundPrimary = fallbackBackground,
        backgroundSecondary = fallbackBackground.copy(alpha = 0.8f),
        overlayBackground = fallbackBackground.copy(alpha = 0.85f),
        
        // Slider colors
        sliderActive = fallbackGreen,
        sliderInactive = fallbackGreen.copy(alpha = 0.3f),
        
        // Primary colors
        primary = fallbackGreen,
        borderDim = fallbackGreen.copy(alpha = 0.3f),
        buttonCancelText = Color(0xFFFF6666),
        
        // Legacy compatibility
        primaryDim = fallbackGreen.copy(alpha = 0.7f),
        primaryBright = fallbackGreen.copy(alpha = 1.0f),
        background = fallbackBackground,
        border = fallbackGreen,
        buttonConfirm = fallbackGreen.copy(alpha = 0.2f),
        buttonCancel = Color(0xFF330000),
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
