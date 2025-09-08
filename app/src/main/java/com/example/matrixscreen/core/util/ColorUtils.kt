package com.example.matrixscreen.core.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.roundToInt

/**
 * Utility functions for color conversion and manipulation.
 * 
 * This object provides functions to convert between different color representations
 * used in the MatrixScreen app, including Long ARGB values, Compose Color objects,
 * and hex strings.
 */
object ColorUtils {
    
    /**
     * Converts a Long ARGB color value to a Compose Color object.
     * 
     * @param colorLong The color as a Long ARGB value
     * @return The color as a Compose Color object
     */
    fun longToColor(colorLong: Long): Color {
        return Color(colorLong)
    }
    
    /**
     * Converts a Compose Color object to a Long ARGB value.
     * 
     * @param color The color as a Compose Color object
     * @return The color as a Long ARGB value
     */
    fun colorToLong(color: Color): Long {
        return color.toArgb().toLong()
    }
    
    /**
     * Converts a Long ARGB color value to a hex string.
     * 
     * @param colorLong The color as a Long ARGB value
     * @param includeAlpha Whether to include the alpha channel in the hex string
     * @return The color as a hex string (e.g., "#FF00FF00" or "#00FF00")
     */
    fun longToHex(colorLong: Long, includeAlpha: Boolean = true): String {
        val color = Color(colorLong)
        val alpha = (color.alpha * 255).roundToInt()
        val red = (color.red * 255).roundToInt()
        val green = (color.green * 255).roundToInt()
        val blue = (color.blue * 255).roundToInt()
        
        return if (includeAlpha) {
            String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
        } else {
            String.format("#%02X%02X%02X", red, green, blue)
        }
    }
    
    /**
     * Converts a hex string to a Long ARGB color value.
     * 
     * @param hexString The color as a hex string (e.g., "#FF00FF00" or "#00FF00")
     * @return The color as a Long ARGB value, or null if the hex string is invalid
     */
    fun hexToLong(hexString: String): Long? {
        return try {
            val cleanHex = hexString.removePrefix("#").uppercase()
            
            when (cleanHex.length) {
                6 -> {
                    // RGB format - add full alpha
                    val red = cleanHex.substring(0, 2).toInt(16)
                    val green = cleanHex.substring(2, 4).toInt(16)
                    val blue = cleanHex.substring(4, 6).toInt(16)
                    Color(red, green, blue, 255).toArgb().toLong()
                }
                8 -> {
                    // ARGB format
                    val alpha = cleanHex.substring(0, 2).toInt(16)
                    val red = cleanHex.substring(2, 4).toInt(16)
                    val green = cleanHex.substring(4, 6).toInt(16)
                    val blue = cleanHex.substring(6, 8).toInt(16)
                    Color(red, green, blue, alpha).toArgb().toLong()
                }
                else -> null
            }
        } catch (e: NumberFormatException) {
            null
        }
    }
    
    /**
     * Extracts RGB components from a Long ARGB color value.
     * 
     * @param colorLong The color as a Long ARGB value
     * @return Triple of (red, green, blue) as floats in range [0.0, 1.0]
     */
    fun longToRgb(colorLong: Long): Triple<Float, Float, Float> {
        val color = Color(colorLong)
        return Triple(color.red, color.green, color.blue)
    }
    
    /**
     * Creates a Long ARGB color value from RGB components.
     * 
     * @param red Red component in range [0.0, 1.0]
     * @param green Green component in range [0.0, 1.0]
     * @param blue Blue component in range [0.0, 1.0]
     * @param alpha Alpha component in range [0.0, 1.0], defaults to 1.0
     * @return The color as a Long ARGB value
     */
    fun rgbToLong(red: Float, green: Float, blue: Float, alpha: Float = 1.0f): Long {
        return Color(red, green, blue, alpha).toArgb().toLong()
    }
    
    /**
     * Validates if a hex string is a valid color representation.
     * 
     * @param hexString The hex string to validate
     * @return True if the hex string is valid, false otherwise
     */
    fun isValidHex(hexString: String): Boolean {
        return hexToLong(hexString) != null
    }
    
    /**
     * Clamps RGB values to the valid range [0.0, 1.0].
     * 
     * @param value The value to clamp
     * @return The clamped value
     */
    fun clampRgb(value: Float): Float {
        return value.coerceIn(0.0f, 1.0f)
    }
    
    /**
     * Clamps alpha values to the valid range [0.0, 1.0].
     * 
     * @param value The value to clamp
     * @return The clamped value
     */
    fun clampAlpha(value: Float): Float {
        return value.coerceIn(0.0f, 1.0f)
    }
}
