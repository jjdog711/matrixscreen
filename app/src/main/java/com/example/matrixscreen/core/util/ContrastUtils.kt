package com.example.matrixscreen.core.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Utility functions for color contrast and legibility calculations.
 * 
 * These utilities help ensure text remains readable against various background colors
 * by calculating contrast ratios and suggesting appropriate text colors.
 */

/**
 * Calculate the relative luminance of a color according to WCAG guidelines.
 * 
 * @param color The color to calculate luminance for
 * @return Luminance value between 0.0 (black) and 1.0 (white)
 */
fun Color.calculateLuminance(): Double {
    val r = if (red <= 0.03928) red / 12.92 else ((red + 0.055) / 1.055).pow(2.4)
    val g = if (green <= 0.03928) green / 12.92 else ((green + 0.055) / 1.055).pow(2.4)
    val b = if (blue <= 0.03928) blue / 12.92 else ((blue + 0.055) / 1.055).pow(2.4)
    
    return 0.2126 * r + 0.7152 * g + 0.0722 * b
}

/**
 * Calculate the contrast ratio between two colors according to WCAG guidelines.
 * 
 * @param color1 First color
 * @param color2 Second color
 * @return Contrast ratio between 1.0 (no contrast) and 21.0 (maximum contrast)
 */
fun calculateContrastRatio(color1: Color, color2: Color): Double {
    val luminance1 = color1.calculateLuminance()
    val luminance2 = color2.calculateLuminance()
    
    val lighter = max(luminance1, luminance2)
    val darker = min(luminance1, luminance2)
    
    return (lighter + 0.05) / (darker + 0.05)
}

/**
 * Check if the contrast ratio between two colors meets WCAG AA standards.
 * 
 * @param foreground Foreground color (text)
 * @param background Background color
 * @param isLargeText Whether the text is large (18pt+ or 14pt+ bold)
 * @return true if contrast meets WCAG AA standards
 */
fun meetsWCAGAA(foreground: Color, background: Color, isLargeText: Boolean = false): Boolean {
    val ratio = calculateContrastRatio(foreground, background)
    return if (isLargeText) ratio >= 3.0 else ratio >= 4.5
}

/**
 * Check if the contrast ratio between two colors meets WCAG AAA standards.
 * 
 * @param foreground Foreground color (text)
 * @param background Background color
 * @param isLargeText Whether the text is large (18pt+ or 14pt+ bold)
 * @return true if contrast meets WCAG AAA standards
 */
fun meetsWCAGAAA(foreground: Color, background: Color, isLargeText: Boolean = false): Boolean {
    val ratio = calculateContrastRatio(foreground, background)
    return if (isLargeText) ratio >= 4.5 else ratio >= 7.0
}

/**
 * Get the appropriate text color (black or white) for maximum contrast against a background.
 * 
 * @param backgroundColor The background color
 * @return Black or white color for maximum contrast
 */
fun getContrastingTextColor(backgroundColor: Color): Color {
    val luminance = backgroundColor.calculateLuminance()
    return if (luminance > 0.5) Color.Black else Color.White
}

/**
 * Get a text color that meets WCAG AA standards against a background.
 * 
 * @param backgroundColor The background color
 * @param isLargeText Whether the text is large
 * @return A color that provides sufficient contrast, or null if no suitable color found
 */
fun getAccessibleTextColor(backgroundColor: Color, isLargeText: Boolean = false): Color? {
    val black = Color.Black
    val white = Color.White
    
    return when {
        meetsWCAGAA(black, backgroundColor, isLargeText) -> black
        meetsWCAGAA(white, backgroundColor, isLargeText) -> white
        else -> null
    }
}

/**
 * Adjust a color's brightness to improve contrast against a background.
 * 
 * @param color The color to adjust
 * @param backgroundColor The background color
 * @param targetRatio The desired contrast ratio (default: 4.5 for WCAG AA)
 * @return Adjusted color with improved contrast
 */
fun adjustColorForContrast(color: Color, backgroundColor: Color, targetRatio: Double = 4.5): Color {
    val currentRatio = calculateContrastRatio(color, backgroundColor)
    
    if (currentRatio >= targetRatio) {
        return color
    }
    
    val bgLuminance = backgroundColor.calculateLuminance()
    val colorLuminance = color.calculateLuminance()
    
    // Determine if we need to make the color lighter or darker
    val shouldLighten = colorLuminance < bgLuminance
    
    // Calculate target luminance
    val targetLuminance = if (shouldLighten) {
        (bgLuminance + 0.05) / targetRatio - 0.05
    } else {
        (bgLuminance + 0.05) * targetRatio - 0.05
    }
    
    // Clamp target luminance to valid range
    val clampedTarget = targetLuminance.coerceIn(0.0, 1.0)
    
    // Adjust color components proportionally
    val factor = if (colorLuminance > 0) clampedTarget / colorLuminance else 1.0
    val adjustedFactor = factor.coerceIn(0.1, 10.0) // Prevent extreme adjustments
    
    return Color(
        red = (color.red * adjustedFactor.toFloat()).coerceIn(0f, 1f),
        green = (color.green * adjustedFactor.toFloat()).coerceIn(0f, 1f),
        blue = (color.blue * adjustedFactor.toFloat()).coerceIn(0f, 1f),
        alpha = color.alpha
    )
}

/**
 * Get a safe UI color that maintains good contrast against a background.
 * 
 * @param preferredColor The preferred color
 * @param backgroundColor The background color
 * @param fallbackColor Fallback color if preferred color doesn't work
 * @return A color that provides good contrast
 */
fun getSafeUIColor(
    preferredColor: Color,
    backgroundColor: Color,
    fallbackColor: Color = getContrastingTextColor(backgroundColor)
): Color {
    return if (meetsWCAGAA(preferredColor, backgroundColor)) {
        preferredColor
    } else {
        fallbackColor
    }
}

/**
 * Check if a color combination is safe for UI elements.
 * 
 * @param foreground Foreground color
 * @param background Background color
 * @param minimumRatio Minimum acceptable contrast ratio (default: 3.0)
 * @return true if the combination is safe
 */
fun isColorCombinationSafe(
    foreground: Color,
    background: Color,
    minimumRatio: Double = 3.0
): Boolean {
    return calculateContrastRatio(foreground, background) >= minimumRatio
}

/**
 * Get contrast level description for a color combination.
 * 
 * @param foreground Foreground color
 * @param background Background color
 * @return Description of contrast level
 */
fun getContrastLevel(foreground: Color, background: Color): String {
    val ratio = calculateContrastRatio(foreground, background)
    
    return when {
        ratio >= 7.0 -> "Excellent (AAA)"
        ratio >= 4.5 -> "Good (AA)"
        ratio >= 3.0 -> "Acceptable (Large Text)"
        ratio >= 2.0 -> "Poor"
        else -> "Very Poor"
    }
}
