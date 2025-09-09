package com.example.matrixscreen

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import com.example.matrixscreen.font.MatrixFontManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.model.*
import com.example.matrixscreen.data.SymbolSet
import com.example.matrixscreen.data.MatrixColor
import com.example.matrixscreen.ui.MatrixSplashScreen
// import com.example.matrixscreen.ui.SettingsListScreen // TODO: Update to use domain model
// import com.example.matrixscreen.ui.SettingsOverlay // Moved to legacy
// import com.example.matrixscreen.ui.ModernSettingsOverlay // TODO: Update to use domain model
// import com.example.matrixscreen.ui.SettingsState // Moved to legacy
// import com.example.matrixscreen.ui.MatrixSettingType // Moved to legacy
// Removed legacy imports - using proper settings navigation now
import com.example.matrixscreen.ui.NewSettingsViewModel
import com.example.matrixscreen.ui.settings.SettingsNavGraph
import com.example.matrixscreen.data.model.MatrixSettings as LegacyMatrixSettings
import com.example.matrixscreen.ui.theme.MatrixScreenTheme
import com.example.matrixscreen.ui.preview.DebugSettingsHarness
import com.example.matrixscreen.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.isActive
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display for modern immersive experience
        enableEdgeToEdge()
        
        // Configure window for fullscreen immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContent {
            MatrixScreenTheme {
                // Setup immersive fullscreen mode
                SetupImmersiveMode()
                
                // Navigation setup
                MatrixApp()
            }
        }
    }
}


/**
 * Main app with splash screen and navigation between Matrix screen and Settings
 */
@Composable
fun MatrixApp() {
    val navController = rememberNavController()
    val settingsViewModel: NewSettingsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    
    
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            MatrixSplashScreen(
                onSplashComplete = { 
                    navController.navigate("matrix") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("matrix") {
            MatrixScreen(
                onSettingsClick = { /* state-driven overlay */ },
                settingsViewModel = settingsViewModel,
                navController = navController
            )
        }
        
        // Debug routes - only available in debug builds
        if (BuildConfig.DEBUG) {
            composable("debug-settings") {
                DebugSettingsHarness(
                    onBackPressed = {
                        navController.navigate("matrix") {
                            popUpTo("matrix") { inclusive = false }
                        }
                    }
                )
            }
            composable("ui-style-preview") {
                com.example.matrixscreen.ui.preview.UIStylePreviewScreen()
            }
        }
        // Custom symbol sets navigation - handled by SettingsOverlayHost pager system
        // These routes redirect to main settings where the full custom sets UI is available
        composable("custom-symbol-sets") {
            LaunchedEffect(Unit) {
                navController.navigate("matrix") {
                    popUpTo("matrix") { inclusive = false }
                }
            }
        }
        composable("create-symbol-set") {
            LaunchedEffect(Unit) {
                navController.navigate("matrix") {
                    popUpTo("matrix") { inclusive = false }
                }
            }
        }
        composable("edit-symbol-set/{setId}") {
            LaunchedEffect(Unit) {
                navController.navigate("matrix") {
                    popUpTo("matrix") { inclusive = false }
                }
            }
        }
    }
}


/**
 * Matrix screen with settings button overlay
 */
@Composable
fun MatrixScreen(
    onSettingsClick: () -> Unit,
    settingsViewModel: NewSettingsViewModel,
    navController: androidx.navigation.NavController
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = uiState.draft
    // val settingsState = SettingsState.MatrixScreen // Default state for matrix screen - moved to legacy
    val livePreviewSettings = null // No live preview in new system
    
    var settingsOpen by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        // Matrix animation background with dynamic color support
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.ui.graphics.Color(currentSettings.backgroundColor).also { color ->
                android.util.Log.d("ColorPicker", "MainActivity: Applying background color: ${color.toArgb().toString(16)}")
            }
        ) {
            // Add subtle grain texture overlay
            Box(modifier = Modifier.fillMaxSize()) {
                // Real matrix rain effect using new domain model
                MatrixDigitalRain(settings = currentSettings)
                
                // Real grain overlay using new domain model
                MatrixGrainOverlay(settings = currentSettings)
            }
        }
        
        // Settings button - tap anywhere on screen or use floating button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { onSettingsClick() },
                        onLongPress = { 
                            // Long press to access debug harness (debug builds only)
                            if (BuildConfig.DEBUG) {
                                navController.navigate("debug-settings")
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    // Only handle swipe gestures when settings overlay is not visible
                        detectDragGestures(
                            onDragEnd = { },
                            onDrag = { _, dragAmount ->
                                // Detect swipe gestures
                                when {
                                    dragAmount.y < -50f -> settingsOpen = true // Swipe up - open settings
                                    dragAmount.y > 50f -> { /* Swipe down - could be used for other actions */ }
                                }
                            }
                        )
                }
        )
        
        // New settings overlay host - keeps matrix visible behind
        com.example.matrixscreen.ui.settings.SettingsOverlayHost(
            isOpen = settingsOpen,
            onOpenChange = { settingsOpen = it },
            settingsViewModel = settingsViewModel
        )
    }
}

@Composable
fun SetupImmersiveMode() {
    val view = LocalView.current
    
    LaunchedEffect(Unit) {
        val window = (view.context as ComponentActivity).window
        val windowInsetsController = WindowCompat.getInsetsController(window, view)
        
        // Configure immersive sticky mode
        windowInsetsController.apply {
            // Hide system bars (status bar and navigation bar)
            hide(WindowInsetsCompat.Type.systemBars())
            
            // Set behavior for when user swipes to reveal system bars
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        
        // Additional fullscreen flags for maximum immersion (Android 11+ compatible)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Use new API for Android 11+
            window.setDecorFitsSystemWindows(false)
        } else {
            // Use deprecated API for older versions
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
        }
    }
}

/**
 * Efficient tiled grain overlay with subtle animated drift
 * Uses pre-allocated ALPHA_8 bitmap tile to avoid per-frame Paint allocation
 */
@Composable
private fun MatrixGrainOverlay(
    settings: com.example.matrixscreen.data.model.MatrixSettings,
    modifier: Modifier = Modifier
) {
    if (settings.grainOpacity <= 0f || settings.grainDensity <= 0) return

    val density = LocalDensity.current
    val tileSizePx = with(density) { 192.dp.toPx().toInt().coerceAtLeast(64) }

    // Build an ALPHA_8 noise tile once per size/density
    val noiseTile = remember(tileSizePx, settings.grainDensity) {
        val bmp = android.graphics.Bitmap.createBitmap(
            tileSizePx, tileSizePx, android.graphics.Bitmap.Config.ALPHA_8
        )
        val canvas = android.graphics.Canvas(bmp)
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE // alpha-only mask
            isAntiAlias = false
            strokeWidth = 1f
        }
        val rng = kotlin.random.Random(42)
        val dots = settings.grainDensity.coerceAtMost(tileSizePx * tileSizePx / 4)
        repeat(dots) {
            val x = rng.nextInt(tileSizePx)
            val y = rng.nextInt(tileSizePx)
            canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
        }
        bmp.asImageBitmap()
    }

    // Subtle endless drift
    val drift = rememberInfiniteTransition(label = "grain")
    val offsetX by drift.animateFloat(
        initialValue = 0f,
        targetValue = tileSizePx.toFloat(),
        animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing)),
        label = "grainX"
    )
    val offsetY by drift.animateFloat(
        initialValue = 0f,
        targetValue = tileSizePx.toFloat(),
        animationSpec = infiniteRepeatable(tween(6100, easing = LinearEasing)),
        label = "grainY"
    )

    Canvas(modifier = modifier) {
        val cols = (size.width / tileSizePx).toInt() + 2
        val rows = (size.height / tileSizePx).toInt() + 2
        val alpha = settings.grainOpacity.coerceIn(0f, 1f)
        val tint = Color(0xFF00FF00)

        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val x = col * tileSizePx - (offsetX % tileSizePx)
                val y = row * tileSizePx - (offsetY % tileSizePx)
                drawImage(
                    image = noiseTile,
                    topLeft = Offset(x, y),
                    alpha = alpha,
                    colorFilter = ColorFilter.tint(tint, BlendMode.SrcIn)
                )
            }
        }
    }
}

/**
 * Data class representing a single character in the authentic Matrix digital rain
 * @param char The character to display
 * @param rowPosition Discrete row position (not smooth Y coordinate)
 * @param brightness Stepped brightness level (0=invisible, 1=dim, 2=medium, 3=bright, 4=head)
 * @param age How many frames this character has existed
 * @param isHead Whether this is the printing head character
 * @param jitterX Horizontal jitter offset for authentic Matrix effect
 * @param flickerAlpha Flicker effect alpha value
 * @param glowIntensity Glow intensity for enhanced visual effects
 */
data class MatrixGlyph(
    var char: Char,
    var rowPosition: Int,
    var brightness: Int,
    var age: Int,
    var isHead: Boolean = false,
    var jitterX: Float = 0f,
    var flickerAlpha: Float = 1f,
    var glowIntensity: Float = 1f
)

/**
 * Data class representing a column of Matrix digital rain
 * @param columnIndex Which column on screen (0 to columnCount-1)
 * @param glyphs List of characters currently visible in this column
 * @param printSpeed Characters per second to print
 * @param lastPrintTime When we last printed a character (nanoseconds)
 * @param headRow Current row position of the printing head
 * @param isActive Whether this column is currently printing
 * @param restartDelay Time until next restart (nanoseconds)
 * @param trailLength Length of the character trail
 * @param charPool Character pool assigned to this column (for per-column character pools)
 */
data class MatrixColumn(
    val columnIndex: Int,
    val glyphs: MutableList<MatrixGlyph>,
    var printSpeed: Float,
    var lastPrintTime: Long,
    var headRow: Int,
    var isActive: Boolean,
    var restartDelay: Long,
    var trailLength: Int,
    var brightTrailLength: Int, // Random length for bright green trail behind head
    var charPool: List<Char> // Per-column character pool for comma-separated input support
)

/*
 * PERFORMANCE OPTIMIZATION STRATEGY:
 * 
 * 1. FRAME SYNCHRONIZATION:
 *    - Use withFrameNanos for precise 60fps timing
 *    - Implement frame time delta calculations for consistent animation speed
 *    - Add frame rate limiting to prevent excessive CPU usage
 * 
 * 2. RECOMPOSITION MINIMIZATION:
 *    - Isolate Canvas in separate composable to prevent parent recomposition
 *    - Use remember for all static data (character sets, paint objects)
 *    - Minimize state changes that trigger recomposition
 * 
 * 3. LIFECYCLE MANAGEMENT:
 *    - Pause animation when app goes to background (STARTED state)
 *    - Resume animation when app returns to foreground
 *    - Properly cancel coroutines on disposal
 * 
 * 4. MEMORY OPTIMIZATION:
 *    - Pre-allocate Paint objects to avoid GC pressure
 *    - Reuse character arrays instead of creating new strings
 *    - Implement object pooling for frequently created objects
 * 
 * 5. RENDERING OPTIMIZATION:
 *    - Viewport culling: only draw visible characters
 *    - Batch similar drawing operations
 *    - Use native canvas for text rendering performance
 */

@Composable
fun MatrixDigitalRain(
    settings: com.example.matrixscreen.data.model.MatrixSettings = com.example.matrixscreen.data.model.MatrixSettings()
) {
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    
    // Initialize Matrix font manager with error handling
    val fontManager = remember { MatrixFontManager(context) }
    LaunchedEffect(Unit) {
        try {
            fontManager.initializeFonts()
        } catch (e: Exception) {
            // Log error but don't crash the app
            android.util.Log.e("MatrixDigitalRain", "Font initialization failed: ${e.message}")
        }
    }
    
    // AUTHENTIC MATRIX: Configuration for discrete terminal-style printing
    val configuration = LocalConfiguration.current
    val reactiveAnimationConfig = remember(settings) {
        val fontSize = with(density) { settings.fontSize.dp.toPx() }
        val rowHeight = fontSize * settings.getRowHeightMultiplier()
        val screenHeight = with(density) { configuration.screenHeightDp.dp.toPx() }
        MatrixAnimationConfig(
            fontSize = fontSize,
            columnCount = settings.columnCount,
            rowHeight = rowHeight,
            screenRows = (screenHeight / rowHeight).toInt() + 50,
            targetFps = settings.targetFps.toFloat(),
            printSpeedMultiplier = settings.fallSpeed,
            matrixColor = settings.getColorTint(),
            maxTrailLength = settings.maxTrailLength,
            maxBrightTrailLength = settings.maxBrightTrailLength,
            glowIntensity = settings.glowIntensity,
            jitterAmount = settings.jitterAmount,
            flickerRate = settings.flickerAmount,
            mutationRate = settings.mutationRate,
            columnStartDelay = settings.columnStartDelay,
            columnRestartDelay = settings.columnRestartDelay,
            initialActivePercentage = settings.activePercentage,
            speedVariationRate = settings.speedVariance,
            grainDensity = settings.grainDensity,
            grainOpacity = settings.grainOpacity
        )
    }
    
    // OPTIMIZATION: Update character set based on settings (use passed-in settings for live preview)
    val matrixChars = remember(settings.getSymbolSet(), settings.getSavedCustomSets(), settings.getActiveCustomSetId()) {
        settings.getSymbolSet().effectiveCharacters(settings).toCharArray()
    }
    
    // PER-COLUMN CHARACTER POOLS: Parse comma-separated character groups (use passed-in settings for live preview)
    val characterPools = remember(settings.getSymbolSet(), settings.getSavedCustomSets(), settings.getActiveCustomSetId()) {
        val characters = settings.getSymbolSet().effectiveCharacters(settings)
        parseCharacterPools(characters)
    }
    
    
    // OPTIMIZATION: Pre-allocate Paint objects to avoid GC pressure during animation
    val paintCache = remember(reactiveAnimationConfig.fontSize, fontManager) { 
        MatrixPaintCache(reactiveAnimationConfig.fontSize, fontManager) 
    }
    
    // AUTHENTIC MATRIX: Initialize columns for terminal-style printing
    // Rebuild only when structure truly changes
    val columns = remember(
        reactiveAnimationConfig.fontSize,
        reactiveAnimationConfig.rowHeight,
        reactiveAnimationConfig.columnCount,
        matrixChars,
        characterPools,
        settings.getSymbolSet(),
        settings.getActiveCustomSetId() // Include activeCustomSetId to force column rebuild when switching custom sets
    ) {
        createMatrixColumns(reactiveAnimationConfig, matrixChars, characterPools)
    }
    
    // OPTIMIZATION: Use single state for animation trigger to minimize recomposition
    var animationTrigger by remember { mutableIntStateOf(0) }
    
    // Keep loop mounted; read freshest values every frame
    val currentConfig by rememberUpdatedState(reactiveAnimationConfig)
    val currentColumns by rememberUpdatedState(columns)
    val currentMatrixChars by rememberUpdatedState(matrixChars)
    val currentCharacterPools by rememberUpdatedState(characterPools)
    
    // OPTIMIZATION: Frame-perfect animation with lifecycle awareness and frame rate limiting
    // CRITICAL FIX: Minimal dependencies to prevent freeze issues
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            var lastFrameTime = 0L
            
            while (isActive) {
                withFrameNanos { frameTimeNanos ->
                    // Use live target FPS; coerce to avoid divide-by-zero
                    val fps = currentConfig.targetFps.coerceAtLeast(1f)
                    val targetFrameInterval = (1_000_000_000.0 / fps.toDouble()).toLong()
                    
                    if (frameTimeNanos - lastFrameTime >= targetFrameInterval) {
                        // AUTHENTIC MATRIX: Update columns with discrete printing behavior
                        updateMatrixColumns(
                            currentColumns,
                            frameTimeNanos,
                            currentConfig,
                            currentMatrixChars,
                            currentCharacterPools
                        )
                        
                        // Trigger single recomposition for Canvas
                        animationTrigger++
                        lastFrameTime = frameTimeNanos
                    }
                }
            }
        }
    }
    
    // OPTIMIZATION: Isolate Canvas to prevent parent recomposition
    AuthenticMatrixCanvas(
        columns = columns,
        animationConfig = reactiveAnimationConfig,
        paintCache = paintCache,
        animationTrigger = animationTrigger,
        settings = settings
    )
}

/*
 * AUTHENTIC MATRIX: Canvas for terminal-style digital printing
 * Renders characters at discrete row positions, not smooth coordinates
 */
@Composable
private fun AuthenticMatrixCanvas(
    columns: List<MatrixColumn>,
    animationConfig: MatrixAnimationConfig,
    paintCache: MatrixPaintCache,
    animationTrigger: Int, // Trigger recomposition without exposing internal state
    settings: MatrixSettings
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        // val canvasHeight = size.height // Currently unused
        
        // Calculate column width
        val columnWidth = canvasWidth / animationConfig.columnCount
        
        // Draw dynamic background color
        drawRect(androidx.compose.ui.graphics.Color(settings.getEffectiveBackgroundColor()), size = size)
        
        // Draw each column independently
        columns.forEachIndexed { columnIndex, column ->
            val xPosition = columnIndex * columnWidth + columnWidth / 2
            
            // Draw all characters in this column
            drawMatrixColumn(
                column = column,
                xPosition = xPosition,
                animationConfig = animationConfig,
                paintCache = paintCache,
                settings = settings
            )
        }
    }
}

/*
 * OPTIMIZATION: Configuration data class to avoid parameter passing overhead
 */
data class MatrixAnimationConfig(
    val fontSize: Float,
    val columnCount: Int,
    val rowHeight: Float,
    val screenRows: Int,
    val targetFps: Float,
    val printSpeedMultiplier: Float = 2.5f,
    val matrixColor: com.example.matrixscreen.data.MatrixColor = com.example.matrixscreen.data.MatrixColor.GREEN,
    val maxTrailLength: Int = 60,
    val maxBrightTrailLength: Int = 8,
    
    // Visual Effects
    val glowIntensity: Float = 1.0f,
    val jitterAmount: Float = 1.0f,
    val flickerRate: Float = 0.05f,
    val mutationRate: Float = 0.04f,
    
    // Timing & Behavior
    val columnStartDelay: Float = 4.0f,
    val columnRestartDelay: Float = 2.0f,
    val initialActivePercentage: Float = 0.4f,
    val speedVariationRate: Float = 0.001f,
    
    // Background Effects
    val grainDensity: Int = 200,
    val grainOpacity: Float = 0.03f
)

/*
 * OPTIMIZATION: Pre-allocated Paint cache to eliminate GC pressure
 * Enhanced with Matrix font support and improved visual effects
 */
class MatrixPaintCache(fontSize: Float, fontManager: MatrixFontManager) {
    // Pre-create all paint objects with different alpha values
    val glowPaint = android.graphics.Paint().apply {
        textSize = fontSize
        typeface = fontManager.getPrimaryTypeface()
        isAntiAlias = true
        maskFilter = BlurMaskFilter(12f, BlurMaskFilter.Blur.NORMAL) // Increased glow
    }
    
    val mainPaint = android.graphics.Paint().apply {
        textSize = fontSize
        typeface = fontManager.getPrimaryTypeface()
        isAntiAlias = true
    }
    
    val brightPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = fontSize
        typeface = fontManager.getPrimaryTypeface()
        isAntiAlias = true
    }
    
    // Enhanced glow paint for lead characters
    val leadGlowPaint = android.graphics.Paint().apply {
        textSize = fontSize
        typeface = fontManager.getPrimaryTypeface()
        isAntiAlias = true
        maskFilter = BlurMaskFilter(16f, BlurMaskFilter.Blur.NORMAL) // Stronger glow for lead
    }
    
    // Font manager for character-specific typefaces
    val fontManager = fontManager
}


/*
 * AUTHENTIC MATRIX: Update all columns with discrete terminal-style printing
 * Each column prints characters at its own rate with irregular timing
 * Enhanced with per-column character pool support
 */
private fun updateMatrixColumns(
    columns: List<MatrixColumn>,
    currentTimeNanos: Long,
    config: MatrixAnimationConfig,
    matrixChars: CharArray,
    characterPools: List<List<Char>>
) {
    columns.forEach { column ->
        updateMatrixColumn(column, currentTimeNanos, config, matrixChars, characterPools)
    }
}

/*
 * AUTHENTIC MATRIX: Update a single column with discrete terminal-style printing
 * Characters are printed row by row, not smoothly moved
 * Enhanced with per-column character pool support
 */
private fun updateMatrixColumn(
    column: MatrixColumn,
    currentTimeNanos: Long,
    config: MatrixAnimationConfig,
    matrixChars: CharArray,
    characterPools: List<List<Char>>
) {
    // Check if column should be active
    if (!column.isActive) {
        if (currentTimeNanos >= column.restartDelay) {
            // Restart the column
            column.isActive = true
            column.headRow = -1 // Start above screen
            column.glyphs.clear()
            // Authentic Matrix movie speed distribution - mostly slow with occasional fast bursts
            column.printSpeed = generateMatrixSpeed() * config.printSpeedMultiplier
            // More random trail lengths like Matrix movie - some very long, some shorter
            val minTrail = (config.maxTrailLength * 0.3f).toInt() // 30% of max as minimum
            val maxTrail = config.maxTrailLength
            
            // Occasionally create very long trails like in Matrix movie (10% chance)
            val isLongTrail = Random.nextFloat() < 0.1f
            val actualMaxTrail = if (isLongTrail) {
                (maxTrail * 1.5f).toInt() // 50% longer for dramatic effect
            } else {
                maxTrail
            }
            
            column.trailLength = Random.nextInt(minTrail, actualMaxTrail + 1)
            
            // Random bright trail length (configurable range for authentic Matrix look)
            column.brightTrailLength = Random.nextInt(2, config.maxBrightTrailLength + 1)
            
            // PER-COLUMN CHARACTER POOLS: Reassign random character pool on restart for visual variety
            if (characterPools.isNotEmpty()) {
                column.charPool = characterPools.random()
            } else {
                // Fallback to global character array if no pools available
                column.charPool = matrixChars.toList()
            }
        }
        return
    }
    
    // Calculate print interval based on speed (characters per second)
    val printIntervalNanos = (1_000_000_000L / column.printSpeed).toLong()
    
    // Dynamic speed variation - occasionally change speed during runtime (like in the movie)
    if (Random.nextFloat() < config.speedVariationRate) { // Configurable chance per frame for speed change
        column.printSpeed = generateMatrixSpeed() * config.printSpeedMultiplier
    }
    
    // Check if it's time to print the next character
    if (currentTimeNanos - column.lastPrintTime >= printIntervalNanos) {
        // Print new character at head position
        column.headRow++
        
        // Add new head character if within screen bounds
        if (column.headRow >= 0 && column.headRow < config.screenRows) {
            // PER-COLUMN CHARACTER POOLS: Use column's assigned character pool
            val newChar = if (column.charPool.isNotEmpty()) {
                column.charPool[Random.nextInt(column.charPool.size)]
            } else {
                // Fallback to global character array
                matrixChars[Random.nextInt(matrixChars.size)]
            }
            
            column.glyphs.add(0, MatrixGlyph(
                char = newChar,
                rowPosition = column.headRow,
                brightness = 4, // Brightest level for head
                age = 0,
                isHead = true
            ))
        }
        
        // Update existing characters
        val iterator = column.glyphs.iterator()
        while (iterator.hasNext()) {
            val glyph = iterator.next()
            glyph.age++
            
            // Update brightness based on distance from head with random bright trail length
            val distanceFromHead = glyph.age
            val trailLength = column.trailLength
            val brightTrailLength = column.brightTrailLength
            val fadePoint = (trailLength * 0.8f).toInt() // Start fading at 80% of trail length
            
            glyph.brightness = when {
                glyph.isHead -> 4  // Bright white-green head
                distanceFromHead <= brightTrailLength -> 3  // Random bright green trail
                distanceFromHead <= (brightTrailLength + 3) -> 2  // Medium green transition
                distanceFromHead <= fadePoint -> 1 // Dim green (configurable trail length)
                else -> 0  // Invisible/remove
            }
            
            // Enhanced visual effects for authentic Matrix look
            if (!glyph.isHead) {
                // Jitter effect - slight horizontal movement
                if (Random.nextFloat() < 0.1f) { // 10% chance per frame
                    glyph.jitterX = (Random.nextFloat() - 0.5f) * config.jitterAmount // Configurable jitter amount
                }
                
                // Flicker effect - occasional opacity changes
                if (Random.nextFloat() < config.flickerRate) { // Configurable flicker rate
                    glyph.flickerAlpha = Random.nextFloat() * 0.5f + 0.5f // 0.5 to 1.0 alpha
                }
                
                // Glow intensity variation
                if (Random.nextFloat() < 0.08f) { // 8% chance per frame
                    glyph.glowIntensity = Random.nextFloat() * 0.3f + 0.7f // 0.7 to 1.0 intensity
                }
            } else {
                // Lead character effects
                glyph.jitterX = (Random.nextFloat() - 0.5f) * (config.jitterAmount * 0.5f) // Subtle jitter for lead
                glyph.flickerAlpha = 1f // Always full alpha for lead
                glyph.glowIntensity = 1.2f * config.glowIntensity // Enhanced glow for lead
            }
            
            // Sparse character mutation (authentic Matrix behavior)
            if (Random.nextFloat() < config.mutationRate && !glyph.isHead) { // Configurable mutation rate
                // PER-COLUMN CHARACTER POOLS: Use column's assigned character pool for mutation
                glyph.char = column.charPool[Random.nextInt(column.charPool.size)]
            }
            
            // Smart character removal: only remove when truly invisible or far off-screen
            val visibleScreenRows = config.screenRows - 50 // Actual visible screen height
            val offScreenBuffer = 25 // Additional buffer beyond visible area
            val maxOffScreenPosition = visibleScreenRows + offScreenBuffer
            
            if (glyph.brightness == 0 || glyph.rowPosition > maxOffScreenPosition) {
                iterator.remove()
            } else if (glyph.isHead) {
                // Convert old head to regular character
                if (distanceFromHead > 0) {
                    glyph.isHead = false
                }
            }
        }
        
        column.lastPrintTime = currentTimeNanos
        
        // Smart column reset: stop when head is far enough off-screen that trail will complete
        val visibleScreenRows = config.screenRows - 50 // Actual visible screen height
        val trailCompletionBuffer = column.trailLength + 10 // Extra buffer for trail to fully fade
        val resetThreshold = visibleScreenRows + trailCompletionBuffer
        
        if (column.headRow > resetThreshold) {
            column.isActive = false
            val restartDelayNanos = (config.columnRestartDelay * 1_000_000_000L).toLong()
            column.restartDelay = if (restartDelayNanos > 0) {
                currentTimeNanos + Random.nextLong(restartDelayNanos / 2, restartDelayNanos) // Configurable restart delay
            } else {
                currentTimeNanos // No delay if columnRestartDelay is 0
            }
        }
    }
}

/*
 * OPTIMIZATION: Optimized stream reset with pre-allocated character array
 */
/*
 * PER-COLUMN CHARACTER POOLS: Parse comma-separated character groups
 * Splits input string on commas and creates individual character pools
 * Falls back to single pool if no commas are found
 */
private fun parseCharacterPools(characters: String): List<List<Char>> {
    val pools = characters.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.toList() }
    
    // Fallback: if no commas found, treat entire string as one pool
    return if (pools.isEmpty()) {
        listOf(characters.toList())
    } else {
        pools
    }
}

/*
 * AUTHENTIC MATRIX: Generate speed distribution like the movie
 * Most columns are slow and contemplative, with occasional fast bursts
 */
private fun generateMatrixSpeed(): Float {
    val random = Random.nextFloat()
    
    return when {
        // 5% chance for very fast bursts (like when Neo sees the code)
        random < 0.05f -> Random.nextFloat() * 8f + 12f // 12-20 chars/sec
        
        // 10% chance for fast activity (dramatic moments)
        random < 0.15f -> Random.nextFloat() * 4f + 6f // 6-10 chars/sec
        
        // 20% chance for medium speed (normal activity)
        random < 0.35f -> Random.nextFloat() * 2f + 3f // 3-5 chars/sec
        
        // 40% chance for slow speed (contemplative, most common)
        random < 0.75f -> Random.nextFloat() * 1.5f + 1f // 1-2.5 chars/sec
        
        // 25% chance for very slow (almost meditative)
        else -> Random.nextFloat() * 0.8f + 0.2f // 0.2-1 chars/sec
    }
}

/*
 * AUTHENTIC MATRIX: Create columns for terminal-style digital printing
 * Enhanced with per-column character pool support for comma-separated input
 */
private fun createMatrixColumns(
    config: MatrixAnimationConfig,
    matrixChars: CharArray,
    characterPools: List<List<Char>>
): List<MatrixColumn> {
    return List(config.columnCount) { columnIndex ->
        // Authentic Matrix movie speed distribution - mostly slow with occasional fast bursts
        val finalSpeed = generateMatrixSpeed() * config.printSpeedMultiplier
        
        // ENHANCED STAGGERED SPAWNING: More organic column initialization
        // Create a more natural distribution of start times
        // val baseDelay = if (config.columnStartDelay > 0f) {
        //     (config.columnStartDelay * 1_000_000_000L).toLong()
        // } else {
        //     1_000_000_000L // Default 1 second base delay
        // }
        
        // Create organic staggered timing - some columns start immediately, others much later
        val startDelay = when {
            // 20% start immediately (0-0.5 seconds)
            Random.nextFloat() < 0.2f -> Random.nextLong(0L, 500_000_000L)
            // 30% start early (0.5-2 seconds)
            Random.nextFloat() < 0.5f -> Random.nextLong(500_000_000L, 2_000_000_000L)
            // 30% start medium (2-5 seconds)
            Random.nextFloat() < 0.8f -> Random.nextLong(2_000_000_000L, 5_000_000_000L)
            // 20% start late (5-10 seconds) for dramatic effect
            else -> Random.nextLong(5_000_000_000L, 10_000_000_000L)
        }
        
        // More organic initial activation - fewer columns start active initially
        val isInitiallyActive = Random.nextFloat() < (config.initialActivePercentage * 0.3f) // Reduce initial active percentage
        
        // More random trail lengths like Matrix movie - some very long, some shorter
        val minTrail = (config.maxTrailLength * 0.3f).toInt() // 30% of max as minimum
        val maxTrail = config.maxTrailLength
        
        // Occasionally create very long trails like in Matrix movie (10% chance)
        val isLongTrail = Random.nextFloat() < 0.1f
        val actualMaxTrail = if (isLongTrail) {
            (maxTrail * 1.5f).toInt() // 50% longer for dramatic effect
        } else {
            maxTrail
        }
        
        // PER-COLUMN CHARACTER POOLS: Assign random character pool to this column
        val chosenPool = if (characterPools.isNotEmpty()) {
            characterPools.random()
        } else {
            // Fallback to global character array if no pools available
            matrixChars.toList()
        }
        
        MatrixColumn(
            columnIndex = columnIndex,
            glyphs = mutableListOf(),
            printSpeed = finalSpeed,
            lastPrintTime = 0L,
            headRow = -Random.nextInt(5, 50), // More varied staggered start positions for organic feel
            isActive = isInitiallyActive,
            restartDelay = System.nanoTime() + startDelay,
            trailLength = Random.nextInt(minTrail, actualMaxTrail + 1),
            brightTrailLength = Random.nextInt(2, config.maxBrightTrailLength + 1), // Random bright trail length (configurable)
            charPool = chosenPool // Assign per-column character pool
        )
    }
}

/*
 * AUTHENTIC MATRIX: Draw a single column with discrete row positioning
 * Uses stepped brightness levels instead of smooth alpha gradients
 */
private fun DrawScope.drawMatrixColumn(
    column: MatrixColumn,
    xPosition: Float,
    animationConfig: MatrixAnimationConfig,
    paintCache: MatrixPaintCache,
    settings: MatrixSettings
) {
    val fontSize = animationConfig.fontSize
    val adjustedXPos = xPosition - fontSize * 0.4f
    
    drawIntoCanvas { canvas ->
        fun lightenColor(argb: Int, factor: Float): Int {
            val a = android.graphics.Color.alpha(argb)
            val r = android.graphics.Color.red(argb)
            val g = android.graphics.Color.green(argb)
            val b = android.graphics.Color.blue(argb)
            val lr = (r + (255 - r) * factor).toInt().coerceAtMost(255)
            val lg = (g + (255 - g) * factor).toInt().coerceAtMost(255)
            val lb = (b + (255 - b) * factor).toInt().coerceAtMost(255)
            return android.graphics.Color.argb(a, lr, lg, lb)
        }
        
        // Draw each character at its discrete row position
        for (glyph in column.glyphs) {
            val yPosition = glyph.rowPosition * animationConfig.rowHeight + fontSize * 0.6f
            val charString = glyph.char.toString()
            
            // Skip if outside visible screen bounds (with small buffer for smooth rendering)
            val visibleHeight = size.height
            val renderBuffer = fontSize * 2 // Small buffer for smooth character rendering
            if (yPosition < -renderBuffer || yPosition > visibleHeight + renderBuffer) continue
            
            // Apply jitter offset
            val finalXPos = adjustedXPos + glyph.jitterX
            
            // Get character-specific typeface
            val charTypeface = if (settings.getSymbolSet() == com.example.matrixscreen.data.SymbolSet.CUSTOM && 
                settings.getActiveCustomSetId() != null) {
                // Use custom font for custom symbol sets
                val customSet = settings.getSavedCustomSets().find { it.id == settings.getActiveCustomSetId()?.toString() }
                customSet?.let { 
                    paintCache.fontManager.getTypefaceForCustomSet(it.fontFileName)
                } ?: paintCache.fontManager.getTypefaceForCharacter(glyph.char)
            } else {
                // Use default font selection for regular symbol sets
                paintCache.fontManager.getTypefaceForCharacter(glyph.char)
            }
            
            // AUTHENTIC MATRIX: Enhanced stepped brightness levels with advanced color support
            when (glyph.brightness) {
                4 -> { // Lead character - unified color with cinematic lift
                    val headBase = settings.getRainHeadColor().toInt()
                    val headColor = lightenColor(headBase, 0.5f) // ~50% toward white for punch
                    
                    // Draw enhanced glow first
                    paintCache.leadGlowPaint.color = android.graphics.Color.argb(
                        (200 * glyph.glowIntensity * animationConfig.glowIntensity).toInt().coerceAtMost(255), 
                        android.graphics.Color.red(headColor),
                        android.graphics.Color.green(headColor),
                        android.graphics.Color.blue(headColor)
                    )
                    paintCache.leadGlowPaint.typeface = charTypeface
                    canvas.nativeCanvas.drawText(charString, finalXPos, yPosition, paintCache.leadGlowPaint)
                    
                    // Draw very bright head character with flicker
                    paintCache.brightPaint.color = android.graphics.Color.argb(
                        (255 * glyph.flickerAlpha).toInt(), 
                        android.graphics.Color.red(headColor),
                        android.graphics.Color.green(headColor),
                        android.graphics.Color.blue(headColor)
                    )
                    paintCache.brightPaint.typeface = charTypeface
                    canvas.nativeCanvas.drawText(charString, finalXPos, yPosition, paintCache.brightPaint)
                }
                3 -> { // Bright trail - unified color with slight cinematic lift
                    val brightBase = settings.getRainBrightTrailColor().toInt()
                    val brightTrailColor = lightenColor(brightBase, 0.15f) // ~15% toward white
                    
                    // Draw subtle glow
                    paintCache.glowPaint.color = android.graphics.Color.argb(
                        (60 * glyph.glowIntensity * animationConfig.glowIntensity).toInt(), 
                        android.graphics.Color.red(brightTrailColor),
                        android.graphics.Color.green(brightTrailColor),
                        android.graphics.Color.blue(brightTrailColor)
                    )
                    paintCache.glowPaint.typeface = charTypeface
                    canvas.nativeCanvas.drawText(charString, finalXPos, yPosition, paintCache.glowPaint)
                    
                    // Draw bright trail character
                    paintCache.mainPaint.color = android.graphics.Color.argb(
                        (android.graphics.Color.alpha(brightTrailColor) * glyph.flickerAlpha).toInt(), 
                        android.graphics.Color.red(brightTrailColor),
                        android.graphics.Color.green(brightTrailColor),
                        android.graphics.Color.blue(brightTrailColor)
                    )
                    paintCache.mainPaint.typeface = charTypeface
                    canvas.nativeCanvas.drawText(charString, finalXPos, yPosition, paintCache.mainPaint)
                }
                2 -> { // Regular trail - use rain trail color (unified)
                    val trailColor = settings.getRainTrailColor().toInt()
                    
                    paintCache.mainPaint.color = android.graphics.Color.argb(
                        (android.graphics.Color.alpha(trailColor) * glyph.flickerAlpha).toInt(), 
                        android.graphics.Color.red(trailColor),
                        android.graphics.Color.green(trailColor),
                        android.graphics.Color.blue(trailColor)
                    )
                    paintCache.mainPaint.typeface = charTypeface
                    canvas.nativeCanvas.drawText(charString, finalXPos, yPosition, paintCache.mainPaint)
                }
                1 -> { // Dim trail - use rain dim trail color (unified)
                    val dimTrailColor = settings.getRainDimTrailColor().toInt()
                    
                    paintCache.mainPaint.color = android.graphics.Color.argb(
                        (android.graphics.Color.alpha(dimTrailColor) * glyph.flickerAlpha).toInt(), 
                        android.graphics.Color.red(dimTrailColor),
                        android.graphics.Color.green(dimTrailColor),
                        android.graphics.Color.blue(dimTrailColor)
                    )
                    paintCache.mainPaint.typeface = charTypeface
                    canvas.nativeCanvas.drawText(charString, finalXPos, yPosition, paintCache.mainPaint)
                }
                // brightness 0 = invisible, don't draw
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MatrixDigitalRainPreview() {
    MatrixScreenTheme {
        MatrixDigitalRain()
    }
}

