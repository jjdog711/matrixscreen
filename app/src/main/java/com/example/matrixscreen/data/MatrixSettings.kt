package com.example.matrixscreen.data

import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.content.Context
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

/**
 * Custom symbol set created by the user
 */
@Serializable
data class CustomSymbolSet(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val characters: String,
    val fontFileName: String = "matrix_code_nfi.ttf"
)

/**
 * Settings data class for Matrix animation customization
 */
data class MatrixSettings(
    val fallSpeed: Float = 2.0f, // Base speed multiplier (0.5 - 5.0) - Updated to 200%
    val symbolSet: SymbolSet = SymbolSet.MATRIX_AUTHENTIC,
    val colorTint: MatrixColor = MatrixColor.GREEN,
    
    // Animation Configuration
    val fontSize: Float = 14f, // Font size in dp (8 - 24) - Already correct
    val columnCount: Int = 150, // Number of columns (50 - 150) - Updated to 150
    val targetFps: Float = 60f, // Target frame rate (15 - 60) - Updated to 60 FPS
    val rowHeightMultiplier: Float = 0.9f, // Row height multiplier (0.7 - 1.2)
    val maxTrailLength: Int = 100, // Maximum trail length (20 - 100) - Updated to 100 characters
    val maxBrightTrailLength: Int = 15, // Maximum bright trail length (2 - 15) - Updated to 15 characters
    
    // Visual Effects
    val glowIntensity: Float = 2.0f, // Glow effect intensity (0.0 - 2.0) - Updated to 200%
    val jitterAmount: Float = 2.0f, // Jitter amount in pixels (0.0 - 3.0) - Updated to 2 pixels
    val flickerRate: Float = 0.2f, // Flicker rate (0.0 - 0.2) - Updated to 20%
    val mutationRate: Float = 0.08f, // Character mutation rate (0.0 - 0.1) - Updated to 8%
    
    // Timing & Behavior
    val columnStartDelay: Float = 0.01f, // Column start delay in seconds (0 - 10) - Updated to 0.01s
    val columnRestartDelay: Float = 0.01f, // Column restart delay in seconds (0.5 - 5.0) - Updated to 0.01s
    val initialActivePercentage: Float = 0.4f, // Initial active columns (0.1 - 0.8) - Already correct at 40%
    val speedVariationRate: Float = 0.01f, // Speed change rate during runtime (0.0 - 0.01) - Updated to 10/1000
    
    // Background Effects
    val grainDensity: Int = 200, // Grain points count (0 - 500)
    val grainOpacity: Float = 0.03f, // Grain opacity (0.0 - 0.1)
    
    // Custom Symbol Sets
    val savedCustomSets: List<CustomSymbolSet> = emptyList(),
    val activeCustomSetId: String? = null,
    
    // Advanced Color System
    val advancedColorsEnabled: Boolean = false, // Toggle between Basic and Advanced color modes
    
    // Advanced Color Properties (only used when advancedColorsEnabled = true)
    val rainHeadColor: Long = 0xFF00FF00L, // Matrix green default
    val rainBrightTrailColor: Long = 0xFF00CC00L, // Slightly dimmer green
    val rainTrailColor: Long = 0xFF008800L, // Medium green
    val rainDimTrailColor: Long = 0xFF004400L, // Dim green
    val uiColor: Long = 0xFF00CC00L, // UI accent color (slightly dimmer green to avoid conflicts)
    val backgroundColor: Long = 0xFF000000L, // Background color (typically black)
    
    // Theme Preset System
    val selectedThemeName: String? = null, // Name of the currently selected theme preset
    
    // Enhanced Brightness Control System
    val brightnessControlsEnabled: Boolean = false, // Toggle for advanced brightness controls
    
    // Per-brightness-level multipliers (1.0 = default behavior)
    val leadBrightnessMultiplier: Float = 1.0f,        // Brightness 4 (Lead character)
    val brightTrailBrightnessMultiplier: Float = 1.0f, // Brightness 3 (Bright trail)
    val trailBrightnessMultiplier: Float = 1.0f,       // Brightness 2 (Regular trail)
    val dimTrailBrightnessMultiplier: Float = 1.0f,    // Brightness 1 (Dim trail)
    
    // Per-brightness-level alpha controls (1.0 = default behavior)
    val leadAlphaMultiplier: Float = 1.0f,        // Alpha multiplier for lead characters
    val brightTrailAlphaMultiplier: Float = 1.0f, // Alpha multiplier for bright trail
    val trailAlphaMultiplier: Float = 1.0f,       // Alpha multiplier for regular trail
    val dimTrailAlphaMultiplier: Float = 1.0f,    // Alpha multiplier for dim trail
    
    // Enhanced preset system with theme integration
    val brightnessPreset: String = "default", // "default", "enhanced", "subtle", "dramatic", "theme_enhanced"
    val themeBrightnessProfile: String = "balanced" // "balanced", "head_focused", "trail_focused", "uniform"
    
) {
    
    /**
     * Get the effective primary color based on current mode
     * In Basic mode, returns the colorTint color
     * In Advanced mode, returns the rainHeadColor
     */
    fun getEffectivePrimaryColor(): Long {
        return if (advancedColorsEnabled) {
            rainHeadColor
        } else {
            colorTint.colorValue
        }
    }
    
    /**
     * Get the effective background color based on current mode
     * In Basic mode, returns black (0xFF000000L)
     * In Advanced mode, returns the backgroundColor
     */
    fun getEffectiveBackgroundColor(): Long {
        // Always use the backgroundColor property, regardless of mode
        // This allows background color picker to work in both basic and advanced modes
        android.util.Log.d("ColorPicker", "getEffectiveBackgroundColor: advancedColorsEnabled=$advancedColorsEnabled, backgroundColor=${backgroundColor.toString(16)}")
        return backgroundColor
    }
    
    /**
     * Get the effective UI color based on current mode
     * In Basic mode, returns the colorTint color
     * In Advanced mode, returns the uiColor
     */
    fun getEffectiveUiColor(): Long {
        return if (advancedColorsEnabled) {
            uiColor
        } else {
            colorTint.colorValue
        }
    }
    
    /**
     * Check if UI and Background colors are identical (validation rule)
     */
    fun hasColorConflict(): Boolean {
        return advancedColorsEnabled && uiColor == backgroundColor
    }
    
    /**
     * Get color by type string for the color picker dialog
     */
    fun getColorByType(colorType: String): Long {
        return when (colorType) {
            "rainHeadColor" -> rainHeadColor
            "rainBrightTrailColor" -> rainBrightTrailColor
            "rainTrailColor" -> rainTrailColor
            "rainDimTrailColor" -> rainDimTrailColor
            "uiColor" -> uiColor
            "backgroundColor" -> backgroundColor
            else -> rainHeadColor // Default fallback
        }
    }
    
    /**
     * Generate advanced colors from a primary color (for Basic → Advanced transition)
     * Uses the same mathematical brightness calculations as basic mode
     */
    fun generateAdvancedColorsFromPrimary(primaryColor: Long): MatrixSettings {
        // Extract RGB components from the primary color
        val red = android.graphics.Color.red(primaryColor.toInt())
        val green = android.graphics.Color.green(primaryColor.toInt())
        val blue = android.graphics.Color.blue(primaryColor.toInt())
        
        // Apply the same mathematical calculations as basic mode
        // Brightness 4 (Lead) - 50% brightness increase
        val lightRed = (red + (255 - red) * 0.5f).toInt().coerceAtMost(255)
        val lightGreen = (green + (255 - green) * 0.5f).toInt().coerceAtMost(255)
        val lightBlue = (blue + (255 - blue) * 0.5f).toInt().coerceAtMost(255)
        val headColor = android.graphics.Color.argb(255, lightRed, lightGreen, lightBlue).toLong()
        
        // Brightness 3 (Bright Trail) - 15% brightness increase, 220 alpha
        val brightRed = (red + (255 - red) * 0.15f).toInt().coerceAtMost(255)
        val brightGreen = (green + (255 - green) * 0.15f).toInt().coerceAtMost(255)
        val brightBlue = (blue + (255 - blue) * 0.15f).toInt().coerceAtMost(255)
        val brightTrailColor = android.graphics.Color.argb(220, brightRed, brightGreen, brightBlue).toLong()
        
        // Brightness 2 (Trail) - 70% of original, 160 alpha
        val trailColor = android.graphics.Color.argb(160, (red * 0.7f).toInt(), (green * 0.8f).toInt(), (blue * 0.7f).toInt()).toLong()
        
        // Brightness 1 (Dim Trail) - 50% of original, 120 alpha
        val dimTrailColor = android.graphics.Color.argb(120, (red * 0.5f).toInt(), (green * 0.7f).toInt(), (blue * 0.5f).toInt()).toLong()
        
        return copy(
            rainHeadColor = headColor,
            rainBrightTrailColor = brightTrailColor,
            rainTrailColor = trailColor,
            rainDimTrailColor = dimTrailColor,
            uiColor = primaryColor,
            backgroundColor = 0xFF000000L // Keep black background
        )
    }
    
    /**
     * Get primary color for Basic mode from advanced colors (for Advanced → Basic transition)
     * Priority: Head > Bright Trail > Trail > Dim Trail
     */
    fun getPrimaryColorForBasicMode(): Long {
        return rainHeadColor
    }
}

/**
 * Available symbol sets for the Matrix effect
 */
enum class SymbolSet(val displayName: String, val characters: String) {
    LATIN(
        displayName = "Latin",
        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    ),
    KATAKANA(
        displayName = "Katakana", 
        characters = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ"
    ),
    MATRIX_AUTHENTIC(
        displayName = "Matrix Authentic",
        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヲン・ーｧｨｩｪｫｬｭｮｯｰﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝ"
    ),
    MATRIX_GLITCH(
        displayName = "Matrix Glitch",
        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヲン・ーｧｨｩｪｫｬｭｮｯｰﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝx̶R̸S̷T̸U̶V̷W̸X̶Y̷Z̸◊◈◆◇▲△▼▽●○■□▲△▼▽"
    ),
    NUMBERS(
        displayName = "Numbers",
        characters = "0123456789"
    ),
    MIXED(
        displayName = "Mixed",
        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ"
    ),
    BINARY(
        displayName = "Binary",
        characters = "01"
    ),
    HEX(
        displayName = "Hexadecimal",
        characters = "0123456789ABCDEF"
    ),
    CUSTOM(
        displayName = "Custom",
        characters = "" // Will be determined by activeCustomSetId
    );
    
    /**
     * Get the effective characters for this symbol set, considering custom sets
     */
    fun effectiveCharacters(settings: MatrixSettings): String {
        return when (this) {
            CUSTOM -> {
                settings.savedCustomSets.find { it.id == settings.activeCustomSetId }?.characters
                    ?: "01" // Fallback to binary if no active custom set
            }
            else -> characters
        }
    }
}

/**
 * Available color themes for the Matrix effect
 */
enum class MatrixColor(val displayName: String, val color: Color, val colorValue: Long) {
    GREEN("Classic", Color(0xFF00FF00), 0xFF00FF00),
    RED("Cyber Red", Color(0xFFFF0040), 0xFFFF0040),
    BLUE("Blue", Color(0xFF0080FF), 0xFF0080FF),
    CYAN("Cyan", Color(0xFF00FFFF), 0xFF00FFFF),
    PURPLE("Purple", Color(0xFF8000FF), 0xFF8000FF),
    ORANGE("Orange", Color(0xFFFF8000), 0xFFFF8000),
    WHITE("White", Color(0xFFFFFFFF), 0xFFFFFFFF),
    BLACK("Shadow", Color(0xFF000000), 0xFF000000)
}

/**
 * DataStore extension for Context to create the preferences DataStore
 */
val Context.matrixSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "matrix_settings"
)

/**
 * Repository for managing Matrix settings persistence using DataStore
 */
class MatrixSettingsRepository(private val dataStore: DataStore<Preferences>) {
    
    companion object {
        // Basic Settings
        private val FALL_SPEED_KEY = floatPreferencesKey("fall_speed")
        private val SYMBOL_SET_KEY = stringPreferencesKey("symbol_set")
        private val COLOR_TINT_KEY = stringPreferencesKey("color_tint")
        
        // Animation Configuration
        private val FONT_SIZE_KEY = floatPreferencesKey("font_size")
        private val COLUMN_COUNT_KEY = intPreferencesKey("column_count")
        private val TARGET_FPS_KEY = floatPreferencesKey("target_fps")
        private val ROW_HEIGHT_MULTIPLIER_KEY = floatPreferencesKey("row_height_multiplier")
        private val MAX_TRAIL_LENGTH_KEY = intPreferencesKey("max_trail_length")
        private val MAX_BRIGHT_TRAIL_LENGTH_KEY = intPreferencesKey("max_bright_trail_length")
        
        // Visual Effects
        private val GLOW_INTENSITY_KEY = floatPreferencesKey("glow_intensity")
        private val JITTER_AMOUNT_KEY = floatPreferencesKey("jitter_amount")
        private val FLICKER_RATE_KEY = floatPreferencesKey("flicker_rate")
        private val MUTATION_RATE_KEY = floatPreferencesKey("mutation_rate")
        
        // Timing & Behavior
        private val COLUMN_START_DELAY_KEY = floatPreferencesKey("column_start_delay")
        private val COLUMN_RESTART_DELAY_KEY = floatPreferencesKey("column_restart_delay")
        private val INITIAL_ACTIVE_PERCENTAGE_KEY = floatPreferencesKey("initial_active_percentage")
        private val SPEED_VARIATION_RATE_KEY = floatPreferencesKey("speed_variation_rate")
        
        // Background Effects
        private val GRAIN_DENSITY_KEY = intPreferencesKey("grain_density")
        private val GRAIN_OPACITY_KEY = floatPreferencesKey("grain_opacity")
        
        // Custom Symbol Sets
        private val SAVED_CUSTOM_SETS_KEY = stringPreferencesKey("saved_custom_sets")
        private val ACTIVE_CUSTOM_SET_ID_KEY = stringPreferencesKey("active_custom_set_id")
        
        // Advanced Color System
        private val ADVANCED_COLORS_ENABLED_KEY = booleanPreferencesKey("advanced_colors_enabled")
        private val RAIN_HEAD_COLOR_KEY = longPreferencesKey("rain_head_color")
        private val RAIN_BRIGHT_TRAIL_COLOR_KEY = longPreferencesKey("rain_bright_trail_color")
        private val RAIN_TRAIL_COLOR_KEY = longPreferencesKey("rain_trail_color")
        private val RAIN_DIM_TRAIL_COLOR_KEY = longPreferencesKey("rain_dim_trail_color")
        private val UI_COLOR_KEY = longPreferencesKey("ui_color")
        private val BACKGROUND_COLOR_KEY = longPreferencesKey("background_color")
        
        // Theme Preset System
        private val SELECTED_THEME_NAME_KEY = stringPreferencesKey("selected_theme_name")
        
        // Enhanced Brightness Control System
        private val BRIGHTNESS_CONTROLS_ENABLED_KEY = booleanPreferencesKey("brightness_controls_enabled")
        private val LEAD_BRIGHTNESS_MULTIPLIER_KEY = floatPreferencesKey("lead_brightness_multiplier")
        private val BRIGHT_TRAIL_BRIGHTNESS_MULTIPLIER_KEY = floatPreferencesKey("bright_trail_brightness_multiplier")
        private val TRAIL_BRIGHTNESS_MULTIPLIER_KEY = floatPreferencesKey("trail_brightness_multiplier")
        private val DIM_TRAIL_BRIGHTNESS_MULTIPLIER_KEY = floatPreferencesKey("dim_trail_brightness_multiplier")
        private val LEAD_ALPHA_MULTIPLIER_KEY = floatPreferencesKey("lead_alpha_multiplier")
        private val BRIGHT_TRAIL_ALPHA_MULTIPLIER_KEY = floatPreferencesKey("bright_trail_alpha_multiplier")
        private val TRAIL_ALPHA_MULTIPLIER_KEY = floatPreferencesKey("trail_alpha_multiplier")
        private val DIM_TRAIL_ALPHA_MULTIPLIER_KEY = floatPreferencesKey("dim_trail_alpha_multiplier")
        private val BRIGHTNESS_PRESET_KEY = stringPreferencesKey("brightness_preset")
        private val THEME_BRIGHTNESS_PROFILE_KEY = stringPreferencesKey("theme_brightness_profile")
        
    }
    
    /**
     * Flow of current settings that automatically updates UI when changed
     */
    val settingsFlow: Flow<MatrixSettings> = dataStore.data.map { preferences ->
        MatrixSettings(
            // Basic Settings
            fallSpeed = preferences[FALL_SPEED_KEY] ?: 2.0f, // 200%
            symbolSet = try {
                SymbolSet.valueOf(preferences[SYMBOL_SET_KEY] ?: SymbolSet.MATRIX_AUTHENTIC.name)
            } catch (e: IllegalArgumentException) {
                SymbolSet.MATRIX_AUTHENTIC
            },
            colorTint = try {
                MatrixColor.valueOf(preferences[COLOR_TINT_KEY] ?: MatrixColor.GREEN.name)
            } catch (e: IllegalArgumentException) {
                MatrixColor.GREEN
            },
            
            // Animation Configuration
            fontSize = preferences[FONT_SIZE_KEY] ?: 14f, // 14dp
            columnCount = preferences[COLUMN_COUNT_KEY] ?: 150, // 150 columns
            targetFps = preferences[TARGET_FPS_KEY] ?: 60f, // 60 FPS
            rowHeightMultiplier = preferences[ROW_HEIGHT_MULTIPLIER_KEY] ?: 0.9f,
            maxTrailLength = preferences[MAX_TRAIL_LENGTH_KEY] ?: 100, // 100 characters
            maxBrightTrailLength = preferences[MAX_BRIGHT_TRAIL_LENGTH_KEY] ?: 15, // 15 characters
            
            // Visual Effects
            glowIntensity = preferences[GLOW_INTENSITY_KEY] ?: 2.0f, // 200%
            jitterAmount = preferences[JITTER_AMOUNT_KEY] ?: 2.0f, // 2 pixels
            flickerRate = preferences[FLICKER_RATE_KEY] ?: 0.2f, // 20%
            mutationRate = preferences[MUTATION_RATE_KEY] ?: 0.08f, // 8%
            
            // Timing & Behavior
            columnStartDelay = preferences[COLUMN_START_DELAY_KEY] ?: 0.01f, // 0.01s
            columnRestartDelay = preferences[COLUMN_RESTART_DELAY_KEY] ?: 0.01f, // 0.01s
            initialActivePercentage = preferences[INITIAL_ACTIVE_PERCENTAGE_KEY] ?: 0.4f, // 40%
            speedVariationRate = preferences[SPEED_VARIATION_RATE_KEY] ?: 0.01f, // 10/1000
            
            // Background Effects
            grainDensity = preferences[GRAIN_DENSITY_KEY] ?: 200,
            grainOpacity = preferences[GRAIN_OPACITY_KEY] ?: 0.03f,
            
            // Custom Symbol Sets
            savedCustomSets = try {
                val jsonString = preferences[SAVED_CUSTOM_SETS_KEY] ?: "[]"
                Json.decodeFromString<List<CustomSymbolSet>>(jsonString)
            } catch (e: Exception) {
                emptyList()
            },
            activeCustomSetId = preferences[ACTIVE_CUSTOM_SET_ID_KEY],
            
            // Advanced Color System
            advancedColorsEnabled = preferences[ADVANCED_COLORS_ENABLED_KEY] ?: false,
            rainHeadColor = preferences[RAIN_HEAD_COLOR_KEY] ?: 0xFF00FF00L,
            rainBrightTrailColor = preferences[RAIN_BRIGHT_TRAIL_COLOR_KEY] ?: 0xFF00CC00L,
            rainTrailColor = preferences[RAIN_TRAIL_COLOR_KEY] ?: 0xFF008800L,
            rainDimTrailColor = preferences[RAIN_DIM_TRAIL_COLOR_KEY] ?: 0xFF004400L,
            uiColor = preferences[UI_COLOR_KEY] ?: 0xFF00FF00L,
            backgroundColor = preferences[BACKGROUND_COLOR_KEY] ?: 0xFF000000L,
            
            // Theme Preset System
            selectedThemeName = preferences[SELECTED_THEME_NAME_KEY],
            
            // Enhanced Brightness Control System
            brightnessControlsEnabled = preferences[BRIGHTNESS_CONTROLS_ENABLED_KEY] ?: false,
            leadBrightnessMultiplier = preferences[LEAD_BRIGHTNESS_MULTIPLIER_KEY] ?: 1.0f,
            brightTrailBrightnessMultiplier = preferences[BRIGHT_TRAIL_BRIGHTNESS_MULTIPLIER_KEY] ?: 1.0f,
            trailBrightnessMultiplier = preferences[TRAIL_BRIGHTNESS_MULTIPLIER_KEY] ?: 1.0f,
            dimTrailBrightnessMultiplier = preferences[DIM_TRAIL_BRIGHTNESS_MULTIPLIER_KEY] ?: 1.0f,
            leadAlphaMultiplier = preferences[LEAD_ALPHA_MULTIPLIER_KEY] ?: 1.0f,
            brightTrailAlphaMultiplier = preferences[BRIGHT_TRAIL_ALPHA_MULTIPLIER_KEY] ?: 1.0f,
            trailAlphaMultiplier = preferences[TRAIL_ALPHA_MULTIPLIER_KEY] ?: 1.0f,
            dimTrailAlphaMultiplier = preferences[DIM_TRAIL_ALPHA_MULTIPLIER_KEY] ?: 1.0f,
            brightnessPreset = preferences[BRIGHTNESS_PRESET_KEY] ?: "default",
            themeBrightnessProfile = preferences[THEME_BRIGHTNESS_PROFILE_KEY] ?: "balanced",
            
        )
    }
    
    /**
     * Update fall speed setting
     */
    suspend fun updateFallSpeed(speed: Float) {
        dataStore.edit { preferences ->
            preferences[FALL_SPEED_KEY] = speed.coerceIn(0.5f, 5.0f)
        }
    }
    
    /**
     * Update symbol set setting
     */
    suspend fun updateSymbolSet(symbolSet: SymbolSet) {
        dataStore.edit { preferences ->
            preferences[SYMBOL_SET_KEY] = symbolSet.name
        }
    }
    
    /**
     * Update color tint setting
     */
    suspend fun updateColorTint(color: MatrixColor) {
        dataStore.edit { preferences ->
            preferences[COLOR_TINT_KEY] = color.name
        }
    }
    
    /**
     * Update animation configuration settings
     */
    suspend fun updateFontSize(size: Float) {
        dataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = size.coerceIn(8f, 24f)
        }
    }
    
    suspend fun updateColumnCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[COLUMN_COUNT_KEY] = count.coerceIn(50, 150)
        }
    }
    
    suspend fun updateTargetFps(fps: Float) {
        dataStore.edit { preferences ->
            preferences[TARGET_FPS_KEY] = fps.coerceIn(15f, 60f)
        }
    }
    
    suspend fun updateRowHeightMultiplier(multiplier: Float) {
        dataStore.edit { preferences ->
            preferences[ROW_HEIGHT_MULTIPLIER_KEY] = multiplier.coerceIn(0.7f, 1.2f)
        }
    }
    
    suspend fun updateMaxTrailLength(length: Int) {
        dataStore.edit { preferences ->
            preferences[MAX_TRAIL_LENGTH_KEY] = length.coerceIn(20, 100)
        }
    }
    
    suspend fun updateMaxBrightTrailLength(length: Int) {
        dataStore.edit { preferences ->
            preferences[MAX_BRIGHT_TRAIL_LENGTH_KEY] = length.coerceIn(2, 15)
        }
    }
    
    /**
     * Update visual effects settings
     */
    suspend fun updateGlowIntensity(intensity: Float) {
        dataStore.edit { preferences ->
            preferences[GLOW_INTENSITY_KEY] = intensity.coerceIn(0.0f, 2.0f)
        }
    }
    
    suspend fun updateJitterAmount(amount: Float) {
        dataStore.edit { preferences ->
            preferences[JITTER_AMOUNT_KEY] = amount.coerceIn(0.0f, 3.0f)
        }
    }
    
    suspend fun updateFlickerRate(rate: Float) {
        dataStore.edit { preferences ->
            preferences[FLICKER_RATE_KEY] = rate.coerceIn(0.0f, 0.2f)
        }
    }
    
    suspend fun updateMutationRate(rate: Float) {
        dataStore.edit { preferences ->
            preferences[MUTATION_RATE_KEY] = rate.coerceIn(0.0f, 0.1f)
        }
    }
    
    /**
     * Update timing & behavior settings
     */
    suspend fun updateColumnStartDelay(delay: Float) {
        dataStore.edit { preferences ->
            preferences[COLUMN_START_DELAY_KEY] = delay.coerceIn(0f, 10f)
        }
    }
    
    suspend fun updateColumnRestartDelay(delay: Float) {
        dataStore.edit { preferences ->
            preferences[COLUMN_RESTART_DELAY_KEY] = delay.coerceIn(0.5f, 5.0f)
        }
    }
    
    suspend fun updateInitialActivePercentage(percentage: Float) {
        dataStore.edit { preferences ->
            preferences[INITIAL_ACTIVE_PERCENTAGE_KEY] = percentage.coerceIn(0.1f, 0.8f)
        }
    }
    
    suspend fun updateSpeedVariationRate(rate: Float) {
        dataStore.edit { preferences ->
            preferences[SPEED_VARIATION_RATE_KEY] = rate.coerceIn(0.0f, 0.01f)
        }
    }
    
    /**
     * Update background effects settings
     */
    suspend fun updateGrainDensity(density: Int) {
        dataStore.edit { preferences ->
            preferences[GRAIN_DENSITY_KEY] = density.coerceIn(0, 500)
        }
    }
    
    suspend fun updateGrainOpacity(opacity: Float) {
        dataStore.edit { preferences ->
            preferences[GRAIN_OPACITY_KEY] = opacity.coerceIn(0.0f, 0.1f)
        }
    }
    
    /**
     * Update custom symbol sets
     */
    suspend fun updateSavedCustomSets(customSets: List<CustomSymbolSet>) {
        dataStore.edit { preferences ->
            val jsonString = Json.encodeToString(customSets)
            preferences[SAVED_CUSTOM_SETS_KEY] = jsonString
        }
    }
    
    suspend fun updateActiveCustomSetId(setId: String?) {
        dataStore.edit { preferences ->
            if (setId != null) {
                preferences[ACTIVE_CUSTOM_SET_ID_KEY] = setId
            } else {
                preferences.remove(ACTIVE_CUSTOM_SET_ID_KEY)
            }
        }
    }
    
    /**
     * Update advanced color system settings
     */
    suspend fun updateAdvancedColorsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ADVANCED_COLORS_ENABLED_KEY] = enabled
        }
    }
    
    suspend fun updateRainHeadColor(color: Long) {
        dataStore.edit { preferences ->
            preferences[RAIN_HEAD_COLOR_KEY] = color
        }
    }
    
    suspend fun updateRainBrightTrailColor(color: Long) {
        dataStore.edit { preferences ->
            preferences[RAIN_BRIGHT_TRAIL_COLOR_KEY] = color
        }
    }
    
    suspend fun updateRainTrailColor(color: Long) {
        dataStore.edit { preferences ->
            preferences[RAIN_TRAIL_COLOR_KEY] = color
        }
    }
    
    suspend fun updateRainDimTrailColor(color: Long) {
        dataStore.edit { preferences ->
            preferences[RAIN_DIM_TRAIL_COLOR_KEY] = color
        }
    }
    
    suspend fun updateUiColor(color: Long) {
        dataStore.edit { preferences ->
            preferences[UI_COLOR_KEY] = color
        }
    }
    
    suspend fun updateBackgroundColor(color: Long) {
        dataStore.edit { preferences ->
            preferences[BACKGROUND_COLOR_KEY] = color
        }
    }
    
    /**
     * Update multiple advanced colors at once (for mode transitions)
     */
    suspend fun updateAdvancedColors(
        rainHeadColor: Long,
        rainBrightTrailColor: Long,
        rainTrailColor: Long,
        rainDimTrailColor: Long,
        uiColor: Long,
        backgroundColor: Long
    ) {
        dataStore.edit { preferences ->
            preferences[RAIN_HEAD_COLOR_KEY] = rainHeadColor
            preferences[RAIN_BRIGHT_TRAIL_COLOR_KEY] = rainBrightTrailColor
            preferences[RAIN_TRAIL_COLOR_KEY] = rainTrailColor
            preferences[RAIN_DIM_TRAIL_COLOR_KEY] = rainDimTrailColor
            preferences[UI_COLOR_KEY] = uiColor
            preferences[BACKGROUND_COLOR_KEY] = backgroundColor
        }
    }
    
    /**
     * Update selected theme name
     */
    suspend fun updateSelectedThemeName(themeName: String?) {
        dataStore.edit { preferences ->
            if (themeName != null) {
                preferences[SELECTED_THEME_NAME_KEY] = themeName
            } else {
                preferences.remove(SELECTED_THEME_NAME_KEY)
            }
        }
    }
    
    /**
     * Update enhanced brightness control settings
     */
    suspend fun updateBrightnessControlsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BRIGHTNESS_CONTROLS_ENABLED_KEY] = enabled
        }
    }
    
    suspend fun updateLeadBrightnessMultiplier(multiplier: Float) {
        dataStore.edit { preferences ->
            preferences[LEAD_BRIGHTNESS_MULTIPLIER_KEY] = multiplier.coerceIn(0.1f, 3.0f)
        }
    }
    
    suspend fun updateBrightTrailBrightnessMultiplier(multiplier: Float) {
        dataStore.edit { preferences ->
            preferences[BRIGHT_TRAIL_BRIGHTNESS_MULTIPLIER_KEY] = multiplier.coerceIn(0.1f, 3.0f)
        }
    }
    
    suspend fun updateTrailBrightnessMultiplier(multiplier: Float) {
        dataStore.edit { preferences ->
            preferences[TRAIL_BRIGHTNESS_MULTIPLIER_KEY] = multiplier.coerceIn(0.1f, 3.0f)
        }
    }
    
    suspend fun updateDimTrailBrightnessMultiplier(multiplier: Float) {
        dataStore.edit { preferences ->
            preferences[DIM_TRAIL_BRIGHTNESS_MULTIPLIER_KEY] = multiplier.coerceIn(0.1f, 3.0f)
        }
    }
    
    suspend fun updateLeadAlphaMultiplier(multiplier: Float) {
        dataStore.edit { preferences ->
            preferences[LEAD_ALPHA_MULTIPLIER_KEY] = multiplier.coerceIn(0.1f, 2.0f)
        }
    }
    
    suspend fun updateBrightTrailAlphaMultiplier(multiplier: Float) {
        dataStore.edit { preferences ->
            preferences[BRIGHT_TRAIL_ALPHA_MULTIPLIER_KEY] = multiplier.coerceIn(0.1f, 2.0f)
        }
    }
    
    suspend fun updateTrailAlphaMultiplier(multiplier: Float) {
        dataStore.edit { preferences ->
            preferences[TRAIL_ALPHA_MULTIPLIER_KEY] = multiplier.coerceIn(0.1f, 2.0f)
        }
    }
    
    suspend fun updateDimTrailAlphaMultiplier(multiplier: Float) {
        dataStore.edit { preferences ->
            preferences[DIM_TRAIL_ALPHA_MULTIPLIER_KEY] = multiplier.coerceIn(0.1f, 2.0f)
        }
    }
    
    suspend fun updateBrightnessPreset(preset: String) {
        dataStore.edit { preferences ->
            preferences[BRIGHTNESS_PRESET_KEY] = preset
        }
    }
    
    suspend fun updateThemeBrightnessProfile(profile: String) {
        dataStore.edit { preferences ->
            preferences[THEME_BRIGHTNESS_PROFILE_KEY] = profile
        }
    }
    
    
    /**
     * Reset all settings to defaults
     */
    suspend fun resetToDefaults() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
