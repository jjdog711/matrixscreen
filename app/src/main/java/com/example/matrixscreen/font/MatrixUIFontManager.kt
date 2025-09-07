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
 * Enhanced font manager for MatrixScreen UI typography system
 * Handles both UI fonts (Space Grotesk, JetBrains Mono) and symbol fonts
 */
class MatrixUIFontManager(private val context: Context) {
    
    companion object {
        private const val TAG = "MatrixUIFontManager"
        
        // UI Font files
        private const val SPACE_GROTESK_REGULAR = "space_grotesk_regular.ttf"
        private const val SPACE_GROTESK_MEDIUM = "space_grotesk_medium.ttf"
        private const val SPACE_GROTESK_SEMIBOLD = "space_grotesk_semibold.ttf"
        private const val JETBRAINS_MONO_REGULAR = "jetbrains_mono_regular.ttf"
        private const val JETBRAINS_MONO_LIGHT = "jetbrains_mono_light.ttf"
        
        // Symbol font files (existing)
        private const val MATRIX_FONT_FILE = "matrix_code_nfi.ttf"
        private const val KATAKANA_FONT = "NotoSansJP-Regular.ttf"
        private const val MONOSPACE_FONT = "monospace"
    }
    
    // UI Font cache
    private var spaceGroteskRegular: Typeface? = null
    private var spaceGroteskMedium: Typeface? = null
    private var spaceGroteskSemibold: Typeface? = null
    private var jetbrainsMonoRegular: Typeface? = null
    private var jetbrainsMonoLight: Typeface? = null
    
    // Symbol font cache (from existing MatrixFontManager)
    private var matrixTypeface: Typeface? = null
    private var katakanaTypeface: Typeface? = null
    private var monospaceTypeface: Typeface? = null
    
    // Custom font cache
    private val customFontCache = mutableMapOf<String, Typeface?>()
    
    /**
     * Initialize all fonts - both UI and symbol fonts
     */
    suspend fun initializeFonts() = withContext(Dispatchers.IO) {
        try {
            // Load UI fonts
            loadUIFonts()
            
            // Load symbol fonts
            loadSymbolFonts()
            
            Log.i(TAG, "All fonts initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Font initialization failed: ${e.message}", e)
        }
    }
    
    /**
     * Load UI fonts (Space Grotesk and JetBrains Mono)
     */
    private suspend fun loadUIFonts() = withContext(Dispatchers.IO) {
        try {
            // Try to load custom fonts, but don't fail if they're not available
            spaceGroteskRegular = loadFontFromAssets(SPACE_GROTESK_REGULAR)
            spaceGroteskMedium = loadFontFromAssets(SPACE_GROTESK_MEDIUM)
            spaceGroteskSemibold = loadFontFromAssets(SPACE_GROTESK_SEMIBOLD)
            jetbrainsMonoRegular = loadFontFromAssets(JETBRAINS_MONO_REGULAR)
            jetbrainsMonoLight = loadFontFromAssets(JETBRAINS_MONO_LIGHT)
            
            val spaceGroteskLoaded = spaceGroteskRegular != null
            val jetbrainsMonoLoaded = jetbrainsMonoRegular != null
            
            Log.i(TAG, "UI fonts loaded: Space Grotesk=$spaceGroteskLoaded, JetBrains Mono=$jetbrainsMonoLoaded")
            
            if (!spaceGroteskLoaded) {
                Log.w(TAG, "Space Grotesk fonts not available, using system fallbacks")
            }
            if (!jetbrainsMonoLoaded) {
                Log.w(TAG, "JetBrains Mono fonts not available, using system fallbacks")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load UI fonts: ${e.message}")
        }
        Unit // Explicit return for withContext
    }
    
    /**
     * Load symbol fonts (existing functionality)
     */
    private suspend fun loadSymbolFonts() = withContext(Dispatchers.IO) {
        try {
            // Try to load Matrix Code NFI font from assets
            matrixTypeface = loadFontFromAssets(MATRIX_FONT_FILE)
            if (matrixTypeface != null) {
                Log.i(TAG, "Matrix Code NFI font loaded successfully")
                return@withContext
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load Matrix Code NFI font: ${e.message}")
        }
        
        // Fallback: Load hybrid font combination
        try {
            katakanaTypeface = loadFontFromAssets(KATAKANA_FONT)
            Log.i(TAG, "Katakana font loaded: ${katakanaTypeface != null}")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load Katakana font: ${e.message}")
        }
        
        // Load system monospace for Latin characters
        monospaceTypeface = Typeface.MONOSPACE
        Log.i(TAG, "Hybrid font system initialized")
        Unit // Explicit return for withContext
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
    
    // UI Font getters with fallbacks
    
    /**
     * Get Space Grotesk Regular with fallback
     */
    fun getSpaceGroteskRegular(): Typeface {
        return spaceGroteskRegular ?: Typeface.SANS_SERIF
    }
    
    /**
     * Get Space Grotesk Medium with fallback
     */
    fun getSpaceGroteskMedium(): Typeface {
        return spaceGroteskMedium ?: getSpaceGroteskRegular()
    }
    
    /**
     * Get Space Grotesk SemiBold with fallback
     */
    fun getSpaceGroteskSemibold(): Typeface {
        return spaceGroteskSemibold ?: getSpaceGroteskMedium()
    }
    
    /**
     * Get JetBrains Mono Regular with fallback
     */
    fun getJetBrainsMonoRegular(): Typeface {
        return jetbrainsMonoRegular ?: Typeface.MONOSPACE
    }
    
    /**
     * Get JetBrains Mono Light with fallback
     */
    fun getJetBrainsMonoLight(): Typeface {
        return jetbrainsMonoLight ?: getJetBrainsMonoRegular()
    }
    
    /**
     * Check if custom UI fonts are available
     */
    fun areCustomUIFontsAvailable(): Boolean {
        return spaceGroteskRegular != null && jetbrainsMonoRegular != null
    }
    
    /**
     * Get font status for debugging
     */
    fun getUIFontStatus(): String {
        return buildString {
            append("UI Font Status:\n")
            append("- Space Grotesk Regular: ${if (spaceGroteskRegular != null) "Loaded" else "Using System Fallback"}\n")
            append("- Space Grotesk Medium: ${if (spaceGroteskMedium != null) "Loaded" else "Using System Fallback"}\n")
            append("- Space Grotesk SemiBold: ${if (spaceGroteskSemibold != null) "Loaded" else "Using System Fallback"}\n")
            append("- JetBrains Mono Regular: ${if (jetbrainsMonoRegular != null) "Loaded" else "Using System Fallback"}\n")
            append("- JetBrains Mono Light: ${if (jetbrainsMonoLight != null) "Loaded" else "Using System Fallback"}\n")
        }
    }
    
    // Symbol font getters (existing functionality)
    
    /**
     * Get appropriate Typeface for a character (existing functionality)
     */
    fun getTypefaceForCharacter(char: Char): Typeface {
        return when {
            matrixTypeface != null -> matrixTypeface!!
            char.isKatakana() -> katakanaTypeface ?: monospaceTypeface ?: Typeface.MONOSPACE
            else -> monospaceTypeface ?: Typeface.MONOSPACE
        }
    }
    
    /**
     * Load custom font (existing functionality)
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
     * Get typeface for custom symbol set (existing functionality)
     */
    fun getTypefaceForCustomSet(fontFileName: String): Typeface {
        return loadCustomFont(fontFileName) 
            ?: matrixTypeface 
            ?: monospaceTypeface 
            ?: Typeface.MONOSPACE
    }
    
    /**
     * Get display name for font file (enhanced with UI fonts)
     */
    fun getFontDisplayName(fontFileName: String): String {
        return when (fontFileName) {
            // UI Fonts
            SPACE_GROTESK_REGULAR -> "Space Grotesk Regular"
            SPACE_GROTESK_MEDIUM -> "Space Grotesk Medium"
            SPACE_GROTESK_SEMIBOLD -> "Space Grotesk SemiBold"
            JETBRAINS_MONO_REGULAR -> "JetBrains Mono Regular"
            JETBRAINS_MONO_LIGHT -> "JetBrains Mono Light"
            
            // Symbol Fonts
            MATRIX_FONT_FILE -> "Matrix Code NFI"
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
    
    /**
     * Check if a character is Katakana (existing functionality)
     */
    private fun Char.isKatakana(): Boolean {
        return this in '\u30A0'..'\u30FF'
    }
    
    /**
     * Get available UI fonts for symbol set selector
     */
    fun getAvailableUIFonts(): List<Pair<String, String>> {
        val fonts = mutableListOf<Pair<String, String>>()
        
        // Add Space Grotesk if available
        if (spaceGroteskRegular != null) {
            fonts.add("space_grotesk_regular.ttf" to "Space Grotesk (Modern)")
        }
        
        // Add JetBrains Mono if available
        if (jetbrainsMonoRegular != null) {
            fonts.add("jetbrains_mono_regular.ttf" to "JetBrains Mono")
        }
        
        // Add existing symbol fonts
        fonts.add("matrix_code_nfi.ttf" to "Matrix Code NFI")
        fonts.add("cascadia_mono.ttf" to "Cascadia Mono")
        fonts.add("digital_7.ttf" to "Digital-7")
        fonts.add("orbitron.ttf" to "Orbitron")
        fonts.add("system_monospace.ttf" to "System Monospace")
        
        return fonts
    }
}
