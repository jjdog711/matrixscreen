package com.example.matrixscreen.font

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manages Matrix font loading and provides fallback font strategies
 * for authentic Matrix digital rain rendering
 */
class MatrixFontManager(private val context: Context) {
    
    companion object {
        private const val TAG = "MatrixFontManager"
        
        // Matrix Code NFI font file name (if available)
        private const val MATRIX_FONT_FILE = "matrix_code_nfi.ttf"
        
        // Fallback fonts for different character types
        private const val KATAKANA_FONT = "NotoSansJP-Regular.ttf"
        private const val MONOSPACE_FONT = "monospace"
    }
    
    private var matrixTypeface: Typeface? = null
    private var katakanaTypeface: Typeface? = null
    private var monospaceTypeface: Typeface? = null
    
    // Cache for custom fonts
    private val customFontCache = mutableMapOf<String, Typeface?>()
    
    /**
     * Initialize font loading - attempts to load Matrix font first,
     * then falls back to hybrid approach
     */
    fun initializeFonts() {
        try {
            // Try to load Matrix Code NFI font from assets
            matrixTypeface = loadFontFromAssets(MATRIX_FONT_FILE)
            if (matrixTypeface != null) {
                Log.i(TAG, "Matrix Code NFI font loaded successfully")
                return
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load Matrix Code NFI font: ${e.message}")
        }
        
        // Fallback: Load hybrid font combination
        loadHybridFonts()
    }
    
    /**
     * Load hybrid font combination for authentic Matrix look
     */
    private fun loadHybridFonts() {
        try {
            // Try to load Noto Sans JP for Katakana characters
            katakanaTypeface = loadFontFromAssets(KATAKANA_FONT)
            Log.i(TAG, "Katakana font loaded: ${katakanaTypeface != null}")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load Katakana font: ${e.message}")
        }
        
        // Load system monospace for Latin characters
        monospaceTypeface = Typeface.MONOSPACE
        Log.i(TAG, "Hybrid font system initialized")
    }
    
    /**
     * Load font from assets directory
     */
    private fun loadFontFromAssets(fontFileName: String): Typeface? {
        return try {
            context.assets.open("fonts/$fontFileName").use { inputStream ->
                // Create temporary file for Typeface creation
                val tempFile = File.createTempFile("font_", ".ttf", context.cacheDir)
                tempFile.deleteOnExit()
                
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                
                Typeface.createFromFile(tempFile)
            }
        } catch (e: IOException) {
            Log.w(TAG, "Font file not found in assets: $fontFileName")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error loading font: $fontFileName", e)
            null
        }
    }
    
    /**
     * Get appropriate Typeface for a character
     * Uses smart character-based selection for proper font rendering
     */
    fun getTypefaceForCharacter(char: Char): Typeface {
        // Smart character-based selection (no forced Matrix font)
        return when {
            isKatakana(char) -> katakanaTypeface ?: monospaceTypeface ?: Typeface.MONOSPACE
            isLatin(char) -> monospaceTypeface ?: Typeface.MONOSPACE
            else -> monospaceTypeface ?: Typeface.MONOSPACE
        }
    }
    
    /**
     * Get appropriate Typeface for a character with symbol set context
     * Matrix Authentic and Matrix Glitch use Matrix font for authentic look
     */
    fun getTypefaceForCharacter(char: Char, symbolSet: com.example.matrixscreen.data.SymbolSet?): Typeface {
        return when {
            // Matrix sets use Matrix font for authentic look
            (symbolSet == com.example.matrixscreen.data.SymbolSet.MATRIX_AUTHENTIC || 
             symbolSet == com.example.matrixscreen.data.SymbolSet.MATRIX_GLITCH) && matrixTypeface != null -> 
                matrixTypeface!!
            
            // Character-based selection for all other cases
            isKatakana(char) -> katakanaTypeface ?: monospaceTypeface ?: Typeface.MONOSPACE
            isLatin(char) -> monospaceTypeface ?: Typeface.MONOSPACE
            else -> monospaceTypeface ?: Typeface.MONOSPACE
        }
    }
    
    /**
     * Get appropriate Typeface for a character with symbol set ID string
     * Matrix Authentic and Matrix Glitch use Matrix font for authentic look
     */
    fun getTypefaceForCharacter(char: Char, symbolSetId: String): Typeface {
        return when {
            // Matrix sets use Matrix font for authentic look
            (symbolSetId == "MATRIX_AUTHENTIC" || symbolSetId == "MATRIX_GLITCH") && matrixTypeface != null -> 
                matrixTypeface!!
            
            // Character-based selection for all other cases
            isKatakana(char) -> katakanaTypeface ?: monospaceTypeface ?: Typeface.MONOSPACE
            isLatin(char) -> monospaceTypeface ?: Typeface.MONOSPACE
            else -> monospaceTypeface ?: Typeface.MONOSPACE
        }
    }
    
    /**
     * Check if character is Katakana (including half-width)
     */
    private fun isKatakana(char: Char): Boolean {
        val code = char.code
        return (code in 0x30A0..0x30FF) || // Full-width Katakana
               (code in 0xFF65..0xFF9F)    // Half-width Katakana
    }
    
    /**
     * Check if character is Latin (A-Z, a-z, 0-9)
     */
    private fun isLatin(char: Char): Boolean {
        val code = char.code
        return (code in 0x0041..0x005A) || // A-Z
               (code in 0x0061..0x007A) || // a-z
               (code in 0x0030..0x0039)    // 0-9
    }
    
    /**
     * Get the primary Matrix Typeface (for consistent sizing)
     */
    fun getPrimaryTypeface(): Typeface {
        return matrixTypeface ?: monospaceTypeface ?: Typeface.MONOSPACE
    }
    
    /**
     * Check if Matrix font is available
     */
    fun isMatrixFontAvailable(): Boolean {
        return matrixTypeface != null
    }
    
    /**
     * Get font information for debugging
     */
    fun getFontInfo(): String {
        return buildString {
            append("Matrix Font Manager Status:\n")
            append("- Matrix Code NFI: ${if (matrixTypeface != null) "Loaded" else "Not Available"}\n")
            append("- Katakana Font: ${if (katakanaTypeface != null) "Loaded" else "Not Available"}\n")
            append("- Monospace Font: ${if (monospaceTypeface != null) "Loaded" else "Not Available"}\n")
            append("- Using: ${if (matrixTypeface != null) "Matrix Code NFI" else "Hybrid System"}")
        }
    }
    
    /**
     * Get list of available font files in assets/fonts/
     */
    suspend fun getAvailableFontFiles(): List<String> = withContext(Dispatchers.IO) {
        try {
            val fontFiles = mutableListOf<String>()
            val fontDir = "fonts"
            
            // List all files in the fonts directory
            context.assets.list(fontDir)?.forEach { fileName ->
                if (fileName.endsWith(".ttf", ignoreCase = true)) {
                    fontFiles.add(fileName)
                }
            }
            
            // Add default system fonts as options
            fontFiles.addAll(listOf(
                "system_monospace.ttf", // Placeholder for system monospace
                "system_default.ttf"    // Placeholder for system default
            ))
            
            fontFiles.sorted()
        } catch (e: Exception) {
            Log.e(TAG, "Error listing font files", e)
            listOf("matrix_code_nfi.ttf", "system_monospace.ttf", "system_default.ttf")
        }
    }
    
    /**
     * Load a custom font by filename
     */
    fun loadCustomFont(fontFileName: String): Typeface? {
        // Check cache first
        customFontCache[fontFileName]?.let { return it }
        
        val typeface = when {
            fontFileName == "system_monospace.ttf" -> Typeface.MONOSPACE
            fontFileName == "system_default.ttf" -> Typeface.DEFAULT
            fontFileName.endsWith(".ttf", ignoreCase = true) -> {
                loadFontFromAssets(fontFileName)
            }
            else -> {
                Log.w(TAG, "Unknown font file: $fontFileName")
                null
            }
        }
        
        // Cache the result (including null for failed loads)
        customFontCache[fontFileName] = typeface
        
        return typeface
    }
    
    /**
     * Get typeface for custom symbol set
     */
    fun getTypefaceForCustomSet(fontFileName: String): Typeface {
        return loadCustomFont(fontFileName) 
            ?: matrixTypeface 
            ?: monospaceTypeface 
            ?: Typeface.MONOSPACE
    }
    
    /**
     * Get display name for font file
     */
    fun getFontDisplayName(fontFileName: String): String {
        return when (fontFileName) {
            "matrix_code_nfi.ttf" -> "Matrix Code NFI"
            "space_grotesk_regular.ttf" -> "Space Grotesk"
            "space_grotesk_bold.ttf" -> "Space Grotesk Bold"
            "cascadia_mono.ttf" -> "Cascadia Mono"
            "digital_7.ttf" -> "Digital-7"
            "orbitron.ttf" -> "Orbitron"
            "system_monospace.ttf" -> "System Monospace"
            "system_default.ttf" -> "System Default"
            else -> fontFileName.removeSuffix(".ttf").replace("_", " ").split(" ").joinToString(" ") { 
                it.replaceFirstChar { char -> char.uppercaseChar() }
            }
        }
    }
}
