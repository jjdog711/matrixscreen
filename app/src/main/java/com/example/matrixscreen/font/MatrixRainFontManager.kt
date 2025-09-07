package com.example.matrixscreen.font

import android.content.Context
import android.graphics.Typeface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Simplified font manager specifically for Matrix rain effect
 * Handles only the essential font loading needed for Canvas rendering
 */
class MatrixRainFontManager(private val context: Context) {
    
    private var matrixTypeface: Typeface? = null
    private var isInitialized = false
    
    /**
     * Initialize fonts for Matrix rain effect
     */
    suspend fun initializeFonts() = withContext(Dispatchers.IO) {
        try {
            // Load Matrix NFI font for custom symbol sets
            matrixTypeface = Typeface.createFromAsset(context.assets, "fonts/matrix_code_nfi.ttf")
            isInitialized = true
        } catch (e: Exception) {
            // Fallback to default monospace font
            matrixTypeface = Typeface.MONOSPACE
            isInitialized = true
        }
    }
    
    /**
     * Get primary typeface for Matrix rain
     */
    fun getPrimaryTypeface(): Typeface {
        return matrixTypeface ?: Typeface.MONOSPACE
    }
    
    /**
     * Get typeface for custom symbol set
     */
    fun getTypefaceForCustomSet(fontFileName: String): Typeface {
        return when (fontFileName) {
            "matrix_code_nfi.ttf" -> matrixTypeface ?: Typeface.MONOSPACE
            else -> Typeface.MONOSPACE
        }
    }
    
    /**
     * Get typeface for character (simplified)
     */
    fun getTypefaceForCharacter(@Suppress("UNUSED_PARAMETER") char: Char): Typeface {
        return getPrimaryTypeface()
    }
}
