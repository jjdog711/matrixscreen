package com.example.matrixscreen.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.example.matrixscreen.data.model.MatrixSettings
// Font management now handled by Typography system
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Performance optimization utilities for MatrixScreen UI
 * Implements font caching, color calculation caching, and recomposition optimization
 */

/**
 * Font manager cache to prevent repeated initialization
 */
// Font manager functions removed - now using Typography system
// @Composable
// fun rememberFontManager(): MatrixUIFontManager {
//     val context = LocalContext.current
//     return remember { MatrixUIFontManager(context) }
// }

/**
 * Cached color scheme calculation
 */
@Composable
fun rememberColorScheme(settings: MatrixSettings?): MatrixUIColorScheme {
    val uiAccent = settings?.uiAccent
    val backgroundColor = settings?.backgroundColor
    val glowIntensity = settings?.glowIntensity
    
    return remember(uiAccent, backgroundColor, glowIntensity) {
        if (settings != null) {
            // Create color scheme without composable calls
            val baseColor = Color(settings.uiAccent)
            val bgColor = Color(settings.backgroundColor)
            val effectiveGlowIntensity = settings.glowIntensity.coerceIn(0f, 2f)
            
            MatrixUIColorScheme(
                // Text colors
                textPrimary = Color(0xFFCCCCCC),
                textSecondary = Color(0xFF666666),
                textAccent = baseColor,
                
                // Background colors
                backgroundPrimary = bgColor,
                backgroundSecondary = bgColor.copy(alpha = 0.8f),
                overlayBackground = Color(settings.uiOverlayBg).copy(alpha = 0.85f),
                
                // Slider colors
                sliderActive = baseColor,
                sliderInactive = baseColor.copy(alpha = 0.3f),
                
                // Primary colors
                primary = baseColor,
                borderDim = baseColor.copy(alpha = 0.3f),
                buttonCancelText = Color(0xFFFF6666),
                
                // Legacy compatibility
                primaryDim = baseColor.copy(alpha = 0.7f),
                primaryBright = baseColor.copy(alpha = 1.0f),
                background = bgColor,
                border = baseColor,
                buttonConfirm = baseColor.copy(alpha = 0.2f),
                buttonCancel = Color(0xFF330000),
                selectionBackground = Color(settings.uiSelectionBg),
                textGlow = baseColor.copy(alpha = (effectiveGlowIntensity * 0.3f).coerceIn(0f, 0.6f)),
                buttonGlow = baseColor.copy(alpha = (effectiveGlowIntensity * 0.2f).coerceIn(0f, 0.4f))
            )
        } else {
            // Create fallback color scheme without composable calls
            val fallbackGreen = Color(0xFF33FF66)
            val fallbackBackground = Color(0xFF121212)
            
            MatrixUIColorScheme(
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
    }
}

/**
 * Optimized font loading with caching
 */
// Font manager functions removed - now using Typography system
// @Composable
// fun rememberInitializedFontManager(): MatrixUIFontManager {
//     val fontManager = rememberFontManager()
//     var isInitialized by remember { mutableStateOf(false) }
//     
//     LaunchedEffect(Unit) {
//         if (!isInitialized) {
//             withContext(Dispatchers.IO) {
//                 fontManager.initializeFonts()
//             }
//             isInitialized = true
//         }
//     }
//     
//     return fontManager
// }

/**
 * Debounced state for live preview updates
 * Prevents excessive recomposition during rapid changes
 */
@Composable
fun <T> rememberDebouncedState(
    initialValue: T,
    delayMs: Long = 100L
): MutableState<T> {
    val state = remember { mutableStateOf(initialValue) }
    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }
    
    return object : MutableState<T> {
        override var value: T
            get() = state.value
            set(newValue) {
                debounceJob?.cancel()
                debounceJob = coroutineScope.launch {
                    delay(delayMs)
                    state.value = newValue
                }
            }
        
        override fun component1(): T = value
        override fun component2(): (T) -> Unit = { value = it }
    }
}

/**
 * Memoized color calculations
 */
@Composable
fun rememberMemoizedColors(
    baseColor: Long,
    backgroundColor: Long,
    glowIntensity: Float
): MatrixUIColorScheme {
    return remember(baseColor, backgroundColor, glowIntensity) {
        val baseColorCompose = androidx.compose.ui.graphics.Color(baseColor)
        val backgroundColorCompose = androidx.compose.ui.graphics.Color(backgroundColor)
        
        MatrixUIColorScheme(
            // Text colors
            textPrimary = if (isLightBackground(backgroundColor)) {
                androidx.compose.ui.graphics.Color(0xFF1A1A1A)
            } else {
                androidx.compose.ui.graphics.Color(0xFFCCCCCC)
            },
            textSecondary = androidx.compose.ui.graphics.Color(0xFF666666),
            textAccent = baseColorCompose,
            
            // Background colors
            backgroundPrimary = backgroundColorCompose,
            backgroundSecondary = backgroundColorCompose.copy(alpha = 0.8f),
            overlayBackground = backgroundColorCompose.copy(alpha = 0.85f),
            
            // Slider colors
            sliderActive = baseColorCompose,
            sliderInactive = baseColorCompose.copy(alpha = 0.3f),
            
            // Primary colors
            primary = baseColorCompose,
            borderDim = baseColorCompose.copy(alpha = 0.3f),
            buttonCancelText = androidx.compose.ui.graphics.Color(0xFFFF6666),
            
            // Legacy compatibility
            primaryDim = baseColorCompose.copy(alpha = 0.7f),
            primaryBright = baseColorCompose.copy(alpha = 1.0f),
            background = backgroundColorCompose,
            border = baseColorCompose,
            buttonConfirm = baseColorCompose.copy(alpha = 0.2f),
            buttonCancel = androidx.compose.ui.graphics.Color(0xFF330000),
            selectionBackground = baseColorCompose.copy(alpha = 0.2f),
            textGlow = baseColorCompose.copy(alpha = (glowIntensity * 0.3f).coerceIn(0f, 0.6f)),
            buttonGlow = baseColorCompose.copy(alpha = (glowIntensity * 0.2f).coerceIn(0f, 0.4f))
        )
    }
}

/**
 * Check if background is light (memoized)
 */
private fun isLightBackground(backgroundColor: Long): Boolean {
    val color = Color(backgroundColor)
    val rgb = color.toArgb()
    val red = (rgb shr 16) and 0xFF
    val green = (rgb shr 8) and 0xFF
    val blue = rgb and 0xFF
    
    val luminance = (0.299 * red.toDouble() + 0.587 * green.toDouble() + 0.114 * blue.toDouble()) / 255.0
    return luminance > 0.5
}

/**
 * Optimized recomposition for settings updates
 */
@Composable
fun rememberOptimizedSettings(
    settings: MatrixSettings
): MatrixSettings {
    return remember(
        settings.fallSpeed,
        settings.columnCount,
        settings.lineSpacing,
        settings.activePercentage,
        settings.speedVariance,
        settings.glowIntensity,
        settings.jitterAmount,
        settings.flickerAmount,
        settings.mutationRate,
        settings.grainDensity,
        settings.grainOpacity,
        settings.targetFps,
        settings.backgroundColor,
        settings.headColor,
        settings.brightTrailColor,
        settings.trailColor,
        settings.dimColor,
        settings.uiAccent,
        settings.uiOverlayBg,
        settings.uiSelectionBg,
        settings.fontSize
    ) {
        settings
    }
}

/**
 * Lazy initialization for expensive operations
 */
@Composable
fun <T> rememberLazyInitialized(
    key: Any?,
    initializer: suspend () -> T
): T? {
    var result by remember(key) { mutableStateOf<T?>(null) }
    var isInitialized by remember(key) { mutableStateOf(false) }
    
    LaunchedEffect(key) {
        if (!isInitialized) {
            result = withContext(Dispatchers.IO) {
                initializer()
            }
            isInitialized = true
        }
    }
    
    return result
}

/**
 * Performance monitoring for UI updates
 */
@Composable
fun rememberPerformanceMonitor(): (String) -> Unit {
    return remember {
        { operation: String ->
            val startTime = System.currentTimeMillis()
            // Log performance metrics in debug builds
            // if (BuildConfig.DEBUG) {
            android.util.Log.d("Performance", "Operation: $operation took ${System.currentTimeMillis() - startTime}ms")
            // }
        }
    }
}

/**
 * Optimized font loading with error handling
 */
// Font manager functions removed - now using Typography system
// suspend fun loadFontsSafely(fontManager: MatrixUIFontManager): Boolean {
//     return try {
//         withContext(Dispatchers.IO) {
//             fontManager.initializeFonts()
//             true
//         }
//     } catch (e: Exception) {
//         android.util.Log.e("FontLoading", "Failed to load fonts: ${e.message}")
//         false
//     }
// }

/**
 * Memory-efficient color scheme updates
 */
@Composable
fun rememberEfficientColorScheme(
    settings: MatrixSettings?
): MatrixUIColorScheme {
    val colorScheme = rememberColorScheme(settings)
    
    // Only recalculate if essential properties change
    return remember(
        settings?.uiAccent,
        settings?.backgroundColor,
        settings?.glowIntensity
    ) {
        colorScheme
    }
}
