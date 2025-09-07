package com.example.matrixscreen.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.data.MatrixColor
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.data.SymbolSet
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.font.MatrixUIFontManager
import com.example.matrixscreen.ui.CustomColorPickerDialog

/**
 * Reusable three-button action bar for settings screens
 * Provides consistent Confirm, Reset, and Cancel functionality
 */
@Composable
private fun SettingsActionButtons(
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit,
    uiColors: UIColorScheme,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Confirm Button (Check icon)
        Button(
            onClick = onConfirm,
            colors = ButtonDefaults.buttonColors(
                containerColor = uiColors.buttonConfirm,
                contentColor = uiColors.primary
            ),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Confirm changes",
                modifier = Modifier.size(18.dp)
            )
        }
        
        // Reset Button (Refresh icon)
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(
                containerColor = uiColors.buttonCancel,
                contentColor = uiColors.buttonCancelText
            ),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Reset to default",
                modifier = Modifier.size(18.dp)
            )
        }
        
        // Cancel Button (Close icon)
        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(
                containerColor = uiColors.buttonCancel,
                contentColor = uiColors.buttonCancelText
            ),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel changes",
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Helper function to generate UI colors based on the current color scheme
 */
@Composable
private fun getUIColorScheme(colorScheme: MatrixColor): UIColorScheme {
    val baseColor = colorScheme.color
    return UIColorScheme(
        primary = baseColor,
        primaryDim = baseColor.copy(alpha = 0.7f),
        primaryBright = baseColor.copy(alpha = 1.0f),
        background = Color(0xFF0A0A0A),
        backgroundSecondary = Color(0xFF1A1A1A),
        border = baseColor,
        borderDim = Color(0xFF333333),
        textPrimary = Color(0xFFCCCCCC),
        textSecondary = Color(0xFF666666),
        textAccent = baseColor,
        buttonConfirm = baseColor.copy(alpha = 0.2f),
        buttonCancel = Color(0xFF330000),
        buttonCancelText = Color(0xFFFF6666),
        sliderActive = baseColor,
        sliderInactive = Color(0xFF333333),
        selectionBackground = baseColor.copy(alpha = 0.2f)
    )
}

/**
 * Helper function to generate UI colors based on MatrixSettings (supports advanced colors)
 */
@Composable
private fun getUIColorScheme(settings: MatrixSettings): UIColorScheme {
    val baseColor = if (settings.advancedColorsEnabled) {
        androidx.compose.ui.graphics.Color(settings.getEffectiveUiColor())
    } else {
        settings.colorTint.color
    }
    return UIColorScheme(
        primary = baseColor,
        primaryDim = baseColor.copy(alpha = 0.7f),
        primaryBright = baseColor.copy(alpha = 1.0f),
        background = Color(0xFF0A0A0A),
        backgroundSecondary = Color(0xFF1A1A1A),
        border = baseColor,
        borderDim = Color(0xFF333333),
        textPrimary = Color(0xFFCCCCCC),
        textSecondary = Color(0xFF666666),
        textAccent = baseColor,
        buttonConfirm = baseColor.copy(alpha = 0.2f),
        buttonCancel = Color(0xFF330000),
        buttonCancelText = Color(0xFFFF6666),
        sliderActive = baseColor,
        sliderInactive = Color(0xFF333333),
        selectionBackground = baseColor.copy(alpha = 0.2f)
    )
}

/**
 * UI color scheme based on the current Matrix color
 */
private data class UIColorScheme(
    val primary: Color,
    val primaryDim: Color,
    val primaryBright: Color,
    val background: Color,
    val backgroundSecondary: Color,
    val border: Color,
    val borderDim: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textAccent: Color,
    val buttonConfirm: Color,
    val buttonCancel: Color,
    val buttonCancelText: Color,
    val sliderActive: Color,
    val sliderInactive: Color,
    val selectionBackground: Color
)

/**
 * Compact settings overlay that appears at the bottom of the Matrix screen
 * Modern, space-efficient design inspired by mobile game settings
 */
@Composable
fun SettingsOverlay(
    settingsState: SettingsState,
    currentSettings: MatrixSettings,
    livePreviewSettings: MatrixSettings?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateDown: () -> Unit,
    onUpdateLivePreview: (MatrixSettingType, Any) -> Unit,
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {},
    onNavigateToCustomSets: (() -> Unit)? = null,
) {
    // Initialize font manager
    val context = LocalContext.current
    val fontManager = remember { MatrixUIFontManager(context) }
    
    // Initialize fonts
    LaunchedEffect(Unit) {
        fontManager.initializeFonts()
    }
    
    when (settingsState) {
        SettingsState.MatrixScreen -> {
            // No overlay when in matrix screen mode
        }
        SettingsState.SettingsList -> {
            // No overlay when in settings list mode
        }
        is SettingsState.Editing -> {
            // Use modern overlay for editing mode
            ModernSettingsOverlay(
                settingsState = settingsState,
                currentSettings = currentSettings,
                livePreviewSettings = livePreviewSettings,
                onConfirm = onConfirm,
                onCancel = onCancel,
                onBack = onBack,
                onNavigateUp = onNavigateUp,
                onNavigateDown = onNavigateDown,
                onUpdateLivePreview = onUpdateLivePreview,
                onStartEditing = { _ -> /* No-op for legacy overlay */ },
                onPersistSetting = { _, _ -> /* No-op for legacy overlay */ },
                onSwipeUp = onSwipeUp,
                onSwipeDown = onSwipeDown,
                onNavigateToCustomSets = onNavigateToCustomSets ?: {}
            )
        }
    }
}

/**
 * Compact overlay panel for editing individual settings
 * Takes up minimal screen space while maintaining full functionality
 */
@Composable
private fun CompactSettingsOverlay(
    setting: MatrixSettingType,
    currentSettings: MatrixSettings,
    livePreviewSettings: MatrixSettings,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateDown: () -> Unit,
    onUpdateLivePreview: (MatrixSettingType, Any) -> Unit,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    onNavigateToCustomSets: (() -> Unit)? = null,
) {
    val uiColors = getUIColorScheme(colorScheme)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f))
            .pointerInput(Unit) {
                var hasNavigated = false
                detectDragGestures(
                    onDragStart = { hasNavigated = false },
                    onDragEnd = { hasNavigated = false },
                    onDrag = { _, dragAmount ->
                        when {
                            // Vertical swipes
                            dragAmount.y < -50f -> {
                                // Swipe up: Do nothing when already in editing mode
                                // The overlay should stay open for editing
                            }
                            dragAmount.y > 100f -> onSwipeDown() // Swipe down: Close overlay (increased threshold)
                            
                            // Horizontal swipes for navigation (only once per gesture)
                            dragAmount.x < -50f && !hasNavigated -> {
                                onNavigateDown() // Swipe left: Next setting
                                hasNavigated = true
                            }
                            dragAmount.x > 50f && !hasNavigated -> {
                                onNavigateUp()    // Swipe right: Previous setting
                                hasNavigated = true
                            }
                        }
                    }
                )
            }
    ) {
        // Compact bottom panel - only 40% of screen height
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(
                    uiColors.background,
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .border(
                    1.dp,
                    uiColors.border,
                    RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .padding(16.dp)
        ) {
            // Compact header with navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                                    IconButton(
                        onClick = onNavigateUp,
                        modifier = Modifier
                        .size(32.dp)
                        .background(
                            uiColors.backgroundSecondary,
                            RoundedCornerShape(6.dp)
                        )
                        .border(1.dp, uiColors.border, RoundedCornerShape(6.dp))
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous",
                        tint = uiColors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                // Setting title and counter
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = getSettingDisplayName(setting),
                        color = uiColors.textAccent,
                        style = AppTypography.bodyMedium
                    )
                    Text(
                        text = "${MatrixSettingType.getPosition(setting)}/${MatrixSettingType.getTotalCount()}",
                        color = uiColors.textSecondary,
                        style = AppTypography.bodySmall
                    )
                    Text(
                        text = "← → swipe to navigate",
                        color = uiColors.textSecondary.copy(alpha = 0.6f),
                        style = AppTypography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                // Next button
                IconButton(
                    onClick = onNavigateDown,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            uiColors.backgroundSecondary,
                            RoundedCornerShape(6.dp)
                        )
                        .border(1.dp, uiColors.border, RoundedCornerShape(6.dp))
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next",
                        tint = uiColors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Compact setting editor
            when (setting) {
                MatrixSettingType.FallSpeed -> {
                    CompactFallSpeedEditor(
                        value = livePreviewSettings.fallSpeed,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.FallSpeed, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.FallSpeed, 2.0f) // Default fall speed
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.SymbolSet -> {
                    CompactSymbolSetEditor(
                        currentSet = livePreviewSettings.symbolSet,
                        onSetChanged = { 
                            onUpdateLivePreview(MatrixSettingType.SymbolSet, it)
                            // Auto-confirm for regular symbol sets, live preview for custom sets
                            if (it != com.example.matrixscreen.data.SymbolSet.CUSTOM) {
                                onConfirm() // Auto-confirm regular symbol sets
                            }
                        },
                        colorScheme = colorScheme,
                        onNavigateToCustomSets = onNavigateToCustomSets,
                        currentSettings = currentSettings,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.SymbolSet, com.example.matrixscreen.data.SymbolSet.MATRIX_AUTHENTIC) // Default symbol set
                        },
                        onCancel = onCancel,
                        onUpdateLivePreview = onUpdateLivePreview
                    )
                }
                MatrixSettingType.ColorTint -> {
                    CompactColorTintEditor(
                        currentColor = livePreviewSettings.colorTint,
                        onColorChanged = { onUpdateLivePreview(MatrixSettingType.ColorTint, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.ColorTint, com.example.matrixscreen.data.MatrixColor.GREEN) // Default color tint
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.FontSize -> {
                    CompactFontSizeEditor(
                        value = livePreviewSettings.fontSize,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.FontSize, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.FontSize, 14f) // Default font size
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.ColumnCount -> {
                    CompactColumnCountEditor(
                        value = livePreviewSettings.columnCount,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.ColumnCount, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.ColumnCount, 150) // Default column count
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.TargetFps -> {
                    CompactTargetFpsEditor(
                        value = livePreviewSettings.targetFps,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.TargetFps, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.TargetFps, 60f) // Default target FPS
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.GlowIntensity -> {
                    CompactGlowIntensityEditor(
                        value = livePreviewSettings.glowIntensity,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.GlowIntensity, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.GlowIntensity, 2.0f) // Default glow intensity
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.MaxTrailLength -> {
                    CompactMaxTrailLengthEditor(
                        value = livePreviewSettings.maxTrailLength,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.MaxTrailLength, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.MaxTrailLength, 100) // Default max trail length
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.MaxBrightTrailLength -> {
                    CompactMaxBrightTrailLengthEditor(
                        value = livePreviewSettings.maxBrightTrailLength,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.MaxBrightTrailLength, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.MaxBrightTrailLength, 15) // Default max bright trail length
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.RowHeightMultiplier -> {
                    CompactRowHeightMultiplierEditor(
                        value = livePreviewSettings.rowHeightMultiplier,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.RowHeightMultiplier, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.RowHeightMultiplier, 0.9f) // Default row height multiplier
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.JitterAmount -> {
                    CompactJitterAmountEditor(
                        value = livePreviewSettings.jitterAmount,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.JitterAmount, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.JitterAmount, 2.0f) // Default jitter amount
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.FlickerRate -> {
                    CompactFlickerRateEditor(
                        value = livePreviewSettings.flickerRate,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.FlickerRate, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.FlickerRate, 0.2f) // Default flicker rate
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.MutationRate -> {
                    CompactMutationRateEditor(
                        value = livePreviewSettings.mutationRate,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.MutationRate, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.MutationRate, 0.08f) // Default mutation rate
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.ColumnStartDelay -> {
                    CompactColumnStartDelayEditor(
                        value = livePreviewSettings.columnStartDelay,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.ColumnStartDelay, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.ColumnStartDelay, 0.01f) // Default column start delay
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.ColumnRestartDelay -> {
                    CompactColumnRestartDelayEditor(
                        value = livePreviewSettings.columnRestartDelay,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.ColumnRestartDelay, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.ColumnRestartDelay, 0.01f) // Default column restart delay
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.InitialActivePercentage -> {
                    CompactInitialActivePercentageEditor(
                        value = livePreviewSettings.initialActivePercentage,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.InitialActivePercentage, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.InitialActivePercentage, 0.4f) // Default initial active percentage
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.SpeedVariationRate -> {
                    CompactSpeedVariationRateEditor(
                        value = livePreviewSettings.speedVariationRate,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.SpeedVariationRate, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.SpeedVariationRate, 0.01f) // Default speed variation rate
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.GrainDensity -> {
                    CompactGrainDensityEditor(
                        value = livePreviewSettings.grainDensity,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.GrainDensity, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.GrainDensity, 200) // Default grain density
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.GrainOpacity -> {
                    CompactGrainOpacityEditor(
                        value = livePreviewSettings.grainOpacity,
                        onValueChange = { onUpdateLivePreview(MatrixSettingType.GrainOpacity, it) },
                        colorScheme = colorScheme,
                        onConfirm = onConfirm,
                        onReset = {
                            onUpdateLivePreview(MatrixSettingType.GrainOpacity, 0.03f) // Default grain opacity
                        },
                        onCancel = onCancel
                    )
                }
                MatrixSettingType.ColorPicker -> {
                    CompactColorPickerEditor(
                        currentSettings = currentSettings,
                        livePreviewSettings = livePreviewSettings,
                        onUpdateLivePreview = onUpdateLivePreview,
                        onConfirm = onConfirm,
                        onCancel = onCancel,
                        onBack = onBack
                    )
                }
                MatrixSettingType.ResetAll -> {
                    CompactResetAllEditor(
                        colorScheme = colorScheme,
                        onConfirm = {
                            // Reset all settings to defaults and confirm, but preserve custom symbol sets
                            val defaultSettings = com.example.matrixscreen.data.MatrixSettings()
                            // Apply all default values to live preview
                            onUpdateLivePreview(MatrixSettingType.FallSpeed, defaultSettings.fallSpeed)
                            onUpdateLivePreview(MatrixSettingType.SymbolSet, defaultSettings.symbolSet) // Reset to default symbol set
                            onUpdateLivePreview(MatrixSettingType.ColorTint, defaultSettings.colorTint)
                            onUpdateLivePreview(MatrixSettingType.FontSize, defaultSettings.fontSize)
                            onUpdateLivePreview(MatrixSettingType.ColumnCount, defaultSettings.columnCount)
                            onUpdateLivePreview(MatrixSettingType.TargetFps, defaultSettings.targetFps)
                            onUpdateLivePreview(MatrixSettingType.RowHeightMultiplier, defaultSettings.rowHeightMultiplier)
                            onUpdateLivePreview(MatrixSettingType.MaxTrailLength, defaultSettings.maxTrailLength)
                            onUpdateLivePreview(MatrixSettingType.MaxBrightTrailLength, defaultSettings.maxBrightTrailLength)
                            onUpdateLivePreview(MatrixSettingType.GlowIntensity, defaultSettings.glowIntensity)
                            onUpdateLivePreview(MatrixSettingType.JitterAmount, defaultSettings.jitterAmount)
                            onUpdateLivePreview(MatrixSettingType.FlickerRate, defaultSettings.flickerRate)
                            onUpdateLivePreview(MatrixSettingType.MutationRate, defaultSettings.mutationRate)
                            onUpdateLivePreview(MatrixSettingType.ColumnStartDelay, defaultSettings.columnStartDelay)
                            onUpdateLivePreview(MatrixSettingType.ColumnRestartDelay, defaultSettings.columnRestartDelay)
                            onUpdateLivePreview(MatrixSettingType.InitialActivePercentage, defaultSettings.initialActivePercentage)
                            onUpdateLivePreview(MatrixSettingType.SpeedVariationRate, defaultSettings.speedVariationRate)
                            onUpdateLivePreview(MatrixSettingType.GrainDensity, defaultSettings.grainDensity)
                            onUpdateLivePreview(MatrixSettingType.GrainOpacity, defaultSettings.grainOpacity)
                            onUpdateLivePreview(MatrixSettingType.ColorPicker, false) // Reset to basic mode
                            onConfirm() // Confirm all changes
                        },
                        onReset = {
                            // Reset all settings to defaults but keep overlay open, preserve custom symbol sets
                            val defaultSettings = com.example.matrixscreen.data.MatrixSettings()
                            onUpdateLivePreview(MatrixSettingType.FallSpeed, defaultSettings.fallSpeed)
                            onUpdateLivePreview(MatrixSettingType.SymbolSet, defaultSettings.symbolSet) // Reset to default symbol set
                            onUpdateLivePreview(MatrixSettingType.ColorTint, defaultSettings.colorTint)
                            onUpdateLivePreview(MatrixSettingType.FontSize, defaultSettings.fontSize)
                            onUpdateLivePreview(MatrixSettingType.ColumnCount, defaultSettings.columnCount)
                            onUpdateLivePreview(MatrixSettingType.TargetFps, defaultSettings.targetFps)
                            onUpdateLivePreview(MatrixSettingType.RowHeightMultiplier, defaultSettings.rowHeightMultiplier)
                            onUpdateLivePreview(MatrixSettingType.MaxTrailLength, defaultSettings.maxTrailLength)
                            onUpdateLivePreview(MatrixSettingType.MaxBrightTrailLength, defaultSettings.maxBrightTrailLength)
                            onUpdateLivePreview(MatrixSettingType.GlowIntensity, defaultSettings.glowIntensity)
                            onUpdateLivePreview(MatrixSettingType.JitterAmount, defaultSettings.jitterAmount)
                            onUpdateLivePreview(MatrixSettingType.FlickerRate, defaultSettings.flickerRate)
                            onUpdateLivePreview(MatrixSettingType.MutationRate, defaultSettings.mutationRate)
                            onUpdateLivePreview(MatrixSettingType.ColumnStartDelay, defaultSettings.columnStartDelay)
                            onUpdateLivePreview(MatrixSettingType.ColumnRestartDelay, defaultSettings.columnRestartDelay)
                            onUpdateLivePreview(MatrixSettingType.InitialActivePercentage, defaultSettings.initialActivePercentage)
                            onUpdateLivePreview(MatrixSettingType.SpeedVariationRate, defaultSettings.speedVariationRate)
                            onUpdateLivePreview(MatrixSettingType.GrainDensity, defaultSettings.grainDensity)
                            onUpdateLivePreview(MatrixSettingType.GrainOpacity, defaultSettings.grainOpacity)
                            onUpdateLivePreview(MatrixSettingType.ColorPicker, false) // Reset to basic mode
                        },
                        onCancel = onCancel
                    )
                }
                
                // New enum cases that need to be handled
                MatrixSettingType.ColorAndBrightness,
                MatrixSettingType.BrightnessControls,
                MatrixSettingType.RainHeadColor,
                MatrixSettingType.RainBrightTrailColor,
                MatrixSettingType.RainTrailColor,
                MatrixSettingType.RainDimTrailColor,
                MatrixSettingType.UiColor,
                MatrixSettingType.LeadBrightnessMultiplier,
                MatrixSettingType.BrightTrailBrightnessMultiplier,
                MatrixSettingType.TrailBrightnessMultiplier,
                MatrixSettingType.DimTrailBrightnessMultiplier -> {
                    // Placeholder for new settings
                    Text(
                        text = "Setting: ${setting.name}",
                        style = AppTypography.bodyMedium,
                        color = uiColors.textSecondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// Compact editor components with 14dp font size and minimal spacing

@Composable
private fun CompactFallSpeedEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Speed:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${(value * 100).toInt()}%",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.5f..5.0f,
            steps = 17,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SLOW",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "FAST",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactSymbolSetEditor(
    currentSet: SymbolSet,
    onSetChanged: (SymbolSet) -> Unit,
    colorScheme: MatrixColor,
    onNavigateToCustomSets: (() -> Unit)? = null,
    currentSettings: MatrixSettings? = null,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit,
    onUpdateLivePreview: (MatrixSettingType, Any) -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    val scrollState = rememberScrollState()
    
    Column {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Fixed height to enable scrolling
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Regular symbol sets (excluding CUSTOM)
            SymbolSet.values().filter { it != SymbolSet.CUSTOM }.forEach { symbolSet ->
                val isSelected = symbolSet == currentSet
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) uiColors.selectionBackground else uiColors.backgroundSecondary,
                    animationSpec = tween(200),
                    label = "background_color"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) uiColors.border else uiColors.borderDim,
                    animationSpec = tween(200),
                    label = "border_color"
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor, RoundedCornerShape(6.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                        .clickable { onSetChanged(symbolSet) }
                        .padding(8.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Symbol set name
                        Text(
                            text = symbolSet.displayName,
                            color = if (isSelected) uiColors.textAccent else uiColors.textPrimary,
                            style = AppTypography.bodyMedium
                        )
                        
                        // Live character preview
                        val previewCharacters = symbolSet.effectiveCharacters(currentSettings ?: MatrixSettings())
                        val previewText = if (previewCharacters.length > 12) {
                            previewCharacters.take(12) + "..."
                        } else {
                            previewCharacters
                        }
                        
                        Text(
                            text = previewText,
                            color = uiColors.textSecondary,
                            style = AppTypography.bodySmall.copy(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        )
                    }
                }
            }
            
            // Custom symbol sets toggle
            if (onNavigateToCustomSets != null) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(uiColors.backgroundSecondary, RoundedCornerShape(6.dp))
                        .border(1.dp, uiColors.borderDim, RoundedCornerShape(6.dp))
                        .clickable { onNavigateToCustomSets() }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Custom Symbol Sets",
                        color = uiColors.textPrimary,
                        style = AppTypography.bodyMedium
                    )
                    
                    androidx.compose.material3.Switch(
                        checked = currentSet == SymbolSet.CUSTOM,
                        onCheckedChange = { checked ->
                            if (checked) {
                                // Check if user already has a valid custom set selected
                                val hasValidCustomSet = currentSettings?.activeCustomSetId != null &&
                                    currentSettings.savedCustomSets.any { it.id == currentSettings.activeCustomSetId }
                                
                                if (hasValidCustomSet) {
                                    // User has a valid custom set, just activate it
                                    onSetChanged(SymbolSet.CUSTOM)
                                } else {
                                    // No valid custom set, navigate to custom sets screen
                                    onNavigateToCustomSets?.invoke()
                                }
                            } else {
                                onSetChanged(SymbolSet.MATRIX_AUTHENTIC)
                            }
                        },
                        colors = androidx.compose.material3.SwitchDefaults.colors(
                            checkedThumbColor = uiColors.primary,
                            checkedTrackColor = uiColors.primary.copy(alpha = 0.5f),
                            uncheckedThumbColor = uiColors.textSecondary,
                            uncheckedTrackColor = uiColors.borderDim
                        )
                    )
                }
            }
        }
        
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactColorTintEditor(
    currentColor: MatrixColor,
    onColorChanged: (MatrixColor) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val colors = MatrixColor.values().toList()
        colors.chunked(3).forEach { rowColors ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowColors.forEach { matrixColor ->
                    CompactColorOption(
                        matrixColor = matrixColor,
                        isSelected = matrixColor == currentColor,
                        onSelected = { onColorChanged(matrixColor) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty space if row is not full
                repeat(3 - rowColors.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactColorOption(
    matrixColor: MatrixColor,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) matrixColor.color.copy(alpha = 0.2f) else Color(0xFF1A1A1A),
        animationSpec = tween(200),
        label = "color_background"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) matrixColor.color else Color(0xFF333333),
        animationSpec = tween(200),
        label = "color_border"
    )
    
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable { onSelected() }
            .padding(6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(matrixColor.color, RoundedCornerShape(2.dp))
            )
            Text(
                text = matrixColor.displayName,
                color = if (isSelected) matrixColor.color else Color(0xFFCCCCCC),
                style = AppTypography.bodyMedium
            )
        }
    }
}

@Composable
private fun CompactFontSizeEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Font Size:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${value.toInt()}dp",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 8f..24f,
            steps = 15,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SMALL",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "LARGE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

// Additional compact editors follow the same pattern...
// (I'll include a few more key ones for brevity)

@Composable
private fun CompactColumnCountEditor(
    value: Int,
    onValueChange: (Int) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Columns:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "$value",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 50f..150f,
            steps = 99,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SPARSE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "DENSE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactTargetFpsEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Frame Rate:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${value.toInt()}fps",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 15f..60f,
            steps = 44,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "LOW",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "HIGH",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactGlowIntensityEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Glow:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${(value * 100).toInt()}%",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.0f..2.0f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NONE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "INTENSE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactMaxTrailLengthEditor(
    value: Int,
    onValueChange: (Int) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trail Length:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "$value chars",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 20f..100f,
            steps = 79,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SHORT",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "LONG",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactMaxBrightTrailLengthEditor(
    value: Int,
    onValueChange: (Int) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bright Trail:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "$value chars",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 2f..15f,
            steps = 12,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SHORT",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "LONG",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactRowHeightMultiplierEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Row Height:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${(value * 100).toInt()}%",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.7f..1.2f,
            steps = 4,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "TIGHT",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "SPACED",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactJitterAmountEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Jitter:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${value.toInt()}px",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.0f..3.0f,
            steps = 29,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NONE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "HIGH",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactFlickerRateEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Flicker:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${(value * 100).toInt()}%",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.0f..0.2f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NONE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "HIGH",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactMutationRateEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mutation:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${(value * 100).toInt()}%",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.0f..0.1f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NONE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "HIGH",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactColumnStartDelayEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Start Delay:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${value.toInt()}s",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..10f,
            steps = 99,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "IMMEDIATE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "DELAYED",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactColumnRestartDelayEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Restart Delay:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${value.toInt()}s",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.5f..5.0f,
            steps = 44,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "FAST",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "SLOW",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactInitialActivePercentageEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Active %:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${(value * 100).toInt()}%",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.1f..0.8f,
            steps = 6,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SPARSE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "DENSE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactSpeedVariationRateEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Speed Variation:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${(value * 1000).toInt()}/1000",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.0f..0.01f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "STABLE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "VARIED",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactGrainDensityEditor(
    value: Int,
    onValueChange: (Int) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Grain Density:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "$value",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..500f,
            steps = 499,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NONE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "HIGH",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

@Composable
private fun CompactGrainOpacityEditor(
    value: Float,
    onValueChange: (Float) -> Unit,
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Grain Opacity:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            Text(
                text = "${(value * 100).toInt()}%",
                color = uiColors.textAccent,
                style = AppTypography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.0f..0.1f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = uiColors.sliderActive,
                activeTrackColor = uiColors.sliderActive,
                inactiveTrackColor = uiColors.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "INVISIBLE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
            Text(
                text = "VISIBLE",
                color = uiColors.textSecondary,
                style = AppTypography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SettingsActionButtons(
            onConfirm = onConfirm,
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
}

/**
 * Compact color picker editor for the settings overlay
 */
@Composable
private fun CompactColorPickerEditor(
    currentSettings: MatrixSettings,
    livePreviewSettings: MatrixSettings,
    onUpdateLivePreview: (MatrixSettingType, Any) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    val uiColors = getUIColorScheme(livePreviewSettings)
    
    // Dialog state management
    var showCustomColorPicker by remember { mutableStateOf(false) }
    var customColorType by remember { mutableStateOf("") }
    var customColorInitialValue by remember { mutableStateOf(0xFF00FF00L) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Color Mode:",
                color = uiColors.textPrimary,
                style = AppTypography.bodyMedium
            )
            
            androidx.compose.material3.Switch(
                checked = livePreviewSettings.advancedColorsEnabled,
                onCheckedChange = { enabled ->
                    // Update the live preview with the new mode
                    onUpdateLivePreview(MatrixSettingType.ColorPicker, enabled)
                },
                colors = androidx.compose.material3.SwitchDefaults.colors(
                    checkedThumbColor = uiColors.primary,
                    checkedTrackColor = uiColors.primary.copy(alpha = 0.5f),
                    uncheckedThumbColor = uiColors.textSecondary,
                    uncheckedTrackColor = uiColors.borderDim
                )
            )
        }
        
        Text(
            text = if (livePreviewSettings.advancedColorsEnabled) "Advanced" else "Basic",
            color = uiColors.textAccent,
            style = AppTypography.bodySmall
        )
        
        // Color picker content based on mode
        if (livePreviewSettings.advancedColorsEnabled) {
            // Advanced mode - complete color picker system
            AdvancedColorPicker(
                livePreviewSettings = livePreviewSettings,
                onUpdateLivePreview = onUpdateLivePreview,
                uiColors = uiColors,
                onConfirm = onConfirm,
                onCancel = onCancel,
                onBack = onBack
            )
        } else {
            // Basic mode - show current primary color with simple color picker
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Primary Color:",
                    color = uiColors.textPrimary,
                    style = AppTypography.bodySmall
                )
                
                // Simple color swatch row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(9) { index ->
                        val color = when (index) {
                            0 -> androidx.compose.ui.graphics.Color(0xFF00FF00) // Matrix Green
                            1 -> androidx.compose.ui.graphics.Color(0xFF0080FF) // Neo Blue
                            2 -> androidx.compose.ui.graphics.Color(0xFFFF0040) // Cyber Red
                            3 -> androidx.compose.ui.graphics.Color(0xFF00FFFF) // Cyan
                            4 -> androidx.compose.ui.graphics.Color(0xFF8000FF) // Purple
                            5 -> androidx.compose.ui.graphics.Color(0xFFFF8000) // Orange
                            6 -> androidx.compose.ui.graphics.Color(0xFFFFFFFF) // White
                            7 -> androidx.compose.ui.graphics.Color(0xFF000000) // Black
                            8 -> null // Custom color picker
                            else -> androidx.compose.ui.graphics.Color(0xFF00FF00)
                        }
                        
                        if (color != null) {
                            // Regular color swatch
                            val isSelected = color.toArgb().toLong() == livePreviewSettings.getEffectivePrimaryColor()
                            
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(color, RoundedCornerShape(6.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color.White else uiColors.border,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        // Update the primary color using raw color value
                                        val colorValue = color.toArgb().toLong()
                                        // For basic mode, find the closest MatrixColor or create a custom one
                                        val matchingMatrixColor = com.example.matrixscreen.data.MatrixColor.values().find { 
                                            it.colorValue == colorValue 
                                        } ?: run {
                                            // If no exact match, find the closest color
                                            com.example.matrixscreen.data.MatrixColor.values().minByOrNull { matrixColor ->
                                                val r1 = android.graphics.Color.red(colorValue.toInt())
                                                val g1 = android.graphics.Color.green(colorValue.toInt())
                                                val b1 = android.graphics.Color.blue(colorValue.toInt())
                                                val r2 = android.graphics.Color.red(matrixColor.colorValue.toInt())
                                                val g2 = android.graphics.Color.green(matrixColor.colorValue.toInt())
                                                val b2 = android.graphics.Color.blue(matrixColor.colorValue.toInt())
                                                val distance = kotlin.math.sqrt(
                                                    ((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2)).toDouble()
                                                )
                                                distance
                                            } ?: com.example.matrixscreen.data.MatrixColor.GREEN
                                        }
                                        onUpdateLivePreview(MatrixSettingType.ColorTint, matchingMatrixColor)
                                    }
                            )
                        } else {
                            // Custom color picker "+" swatch
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(uiColors.background, RoundedCornerShape(6.dp))
                                    .border(1.dp, uiColors.border, RoundedCornerShape(6.dp))
                                    .clickable {
                                        // Open custom color picker for primary color
                                        customColorType = "primary"
                                        customColorInitialValue = livePreviewSettings.getEffectivePrimaryColor()
                                        showCustomColorPicker = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+",
                                    color = uiColors.textPrimary,
                                    style = AppTypography.headlineSmall
                                )
                            }
                        }
                    }
                }
                
                // Background color picker for basic mode
                Text(
                    text = "Background Color:",
                    color = uiColors.textPrimary,
                    style = AppTypography.bodySmall
                )
                
                // Background color swatch row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(9) { index ->
                        val color = when (index) {
                            0 -> androidx.compose.ui.graphics.Color(0xFF000000) // Black
                            1 -> androidx.compose.ui.graphics.Color(0xFFFFFFFF) // White
                            2 -> androidx.compose.ui.graphics.Color(0xFF00FF00) // Matrix Green
                            3 -> androidx.compose.ui.graphics.Color(0xFF0080FF) // Neo Blue
                            4 -> androidx.compose.ui.graphics.Color(0xFFFF0040) // Cyber Red
                            5 -> androidx.compose.ui.graphics.Color(0xFF00FFFF) // Cyan
                            6 -> androidx.compose.ui.graphics.Color(0xFF8000FF) // Purple
                            7 -> androidx.compose.ui.graphics.Color(0xFFFF8000) // Orange
                            8 -> null // Custom color picker
                            else -> androidx.compose.ui.graphics.Color(0xFF000000)
                        }
                        
                        if (color != null) {
                            // Regular color swatch
                            val isSelected = color.toArgb().toLong() == livePreviewSettings.getEffectiveBackgroundColor()
                            
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(color, RoundedCornerShape(6.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color.White else uiColors.border,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        // Update the background color using raw color value
                                        val colorValue = color.toArgb().toLong()
                                        onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "background", "color" to colorValue))
                                    }
                            )
                        } else {
                            // Custom color picker "+" swatch
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(uiColors.background, RoundedCornerShape(6.dp))
                                    .border(1.dp, uiColors.border, RoundedCornerShape(6.dp))
                                    .clickable {
                                        // Open custom color picker for background color
                                        customColorType = "background"
                                        customColorInitialValue = livePreviewSettings.getEffectiveBackgroundColor()
                                        showCustomColorPicker = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+",
                                    color = uiColors.textPrimary,
                                    style = AppTypography.headlineSmall
                                )
                            }
                        }
                    }
                }
                
                // Action buttons for basic mode
                Spacer(modifier = Modifier.height(16.dp))
                SettingsActionButtons(
                    onConfirm = onConfirm,
                    onReset = {
                        // Reset color picker to defaults
                        onUpdateLivePreview(MatrixSettingType.ColorTint, com.example.matrixscreen.data.MatrixColor.GREEN)
                        onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "background", "color" to 0xFF000000L))
                    },
                    onCancel = onCancel,
                    uiColors = uiColors
                )
            }
        }
    }
    
    // Custom Color Picker Dialog
    if (showCustomColorPicker) {
        CustomColorPickerDialog(
            initialColor = androidx.compose.ui.graphics.Color(customColorInitialValue),
            onColorSelected = { selectedColor ->
                val selectedColorLong = selectedColor.toArgb().toLong()
                when (customColorType) {
                    "primary" -> {
                        // For basic mode, find the closest MatrixColor or create a custom one
                        val matchingMatrixColor = com.example.matrixscreen.data.MatrixColor.values().find { 
                            it.colorValue == selectedColorLong 
                        } ?: run {
                            // If no exact match, find the closest color
                            com.example.matrixscreen.data.MatrixColor.values().minByOrNull { matrixColor ->
                                val r1 = android.graphics.Color.red(selectedColorLong.toInt())
                                val g1 = android.graphics.Color.green(selectedColorLong.toInt())
                                val b1 = android.graphics.Color.blue(selectedColorLong.toInt())
                                val r2 = android.graphics.Color.red(matrixColor.colorValue.toInt())
                                val g2 = android.graphics.Color.green(matrixColor.colorValue.toInt())
                                val b2 = android.graphics.Color.blue(matrixColor.colorValue.toInt())
                                val distance = kotlin.math.sqrt(
                                    ((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2)).toDouble()
                                )
                                distance
                            } ?: com.example.matrixscreen.data.MatrixColor.GREEN
                        }
                        onUpdateLivePreview(MatrixSettingType.ColorTint, matchingMatrixColor)
                    }
                    "background" -> {
                        onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "background", "color" to selectedColorLong))
                    }
                }
                showCustomColorPicker = false
            },
            onDismiss = {
                showCustomColorPicker = false
            }
        )
    }
}

/**
 * Complete Advanced Color Picker with individual controls for each color type
 */
@Composable
private fun AdvancedColorPicker(
    livePreviewSettings: MatrixSettings,
    onUpdateLivePreview: (MatrixSettingType, Any) -> Unit,
    uiColors: UIColorScheme,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit
) {
    var showCustomColorDialog by remember { mutableStateOf(false) }
    var customColorTarget by remember { mutableStateOf("") }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        
        // Rain Head Color
        ColorPickerRow(
            title = "Head Color",
            currentColor = livePreviewSettings.rainHeadColor,
            onColorSelected = { colorValue ->
                onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "rainHead", "color" to colorValue))
            },
            onCustomColorClick = {
                customColorTarget = "rainHead"
                showCustomColorDialog = true
            },
            uiColors = uiColors
        )
        
        // Rain Bright Trail Color
        ColorPickerRow(
            title = "Bright Trail",
            currentColor = livePreviewSettings.rainBrightTrailColor,
            onColorSelected = { colorValue ->
                onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "rainBrightTrail", "color" to colorValue))
            },
            onCustomColorClick = {
                customColorTarget = "rainBrightTrail"
                showCustomColorDialog = true
            },
            uiColors = uiColors
        )
        
        // Rain Trail Color
        ColorPickerRow(
            title = "Trail Color",
            currentColor = livePreviewSettings.rainTrailColor,
            onColorSelected = { colorValue ->
                onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "rainTrail", "color" to colorValue))
            },
            onCustomColorClick = {
                customColorTarget = "rainTrail"
                showCustomColorDialog = true
            },
            uiColors = uiColors
        )
        
        // Rain Dim Trail Color
        ColorPickerRow(
            title = "Dim Trail",
            currentColor = livePreviewSettings.rainDimTrailColor,
            onColorSelected = { colorValue ->
                onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "rainDimTrail", "color" to colorValue))
            },
            onCustomColorClick = {
                customColorTarget = "rainDimTrail"
                showCustomColorDialog = true
            },
            uiColors = uiColors
        )
        
        // UI Color
        ColorPickerRow(
            title = "UI Color",
            currentColor = livePreviewSettings.uiColor,
            onColorSelected = { colorValue ->
                onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "ui", "color" to colorValue))
            },
            onCustomColorClick = {
                customColorTarget = "ui"
                showCustomColorDialog = true
            },
            uiColors = uiColors
        )
        
        // Background Color
        ColorPickerRow(
            title = "Background",
            currentColor = livePreviewSettings.backgroundColor,
            onColorSelected = { colorValue ->
                // Debug: Log the color change
                android.util.Log.d("ColorPicker", "Background color changed to: ${colorValue.toString(16)}")
                onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "background", "color" to colorValue))
            },
            onCustomColorClick = {
                customColorTarget = "background"
                showCustomColorDialog = true
            },
            uiColors = uiColors
        )
        
                    // Action buttons for advanced mode
            Spacer(modifier = Modifier.height(16.dp))
            SettingsActionButtons(
                onConfirm = onConfirm,
                onReset = {
                    // Reset advanced colors to defaults
                    onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "rainHead", "color" to 0xFF00FF00L))
                    onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "rainBrightTrail", "color" to 0xFF00FF00L))
                    onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "rainTrail", "color" to 0xFF00FF00L))
                    onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "rainDimTrail", "color" to 0xFF00FF00L))
                    onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "ui", "color" to 0xFF00FF00L))
                    onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to "background", "color" to 0xFF000000L))
                },
                onCancel = onCancel,
                uiColors = uiColors
            )
    }
    
    // Custom Color Picker Dialog
    if (showCustomColorDialog) {
        val initialColor = when (customColorTarget) {
            "rainHead" -> androidx.compose.ui.graphics.Color(livePreviewSettings.rainHeadColor)
            "rainBrightTrail" -> androidx.compose.ui.graphics.Color(livePreviewSettings.rainBrightTrailColor)
            "rainTrail" -> androidx.compose.ui.graphics.Color(livePreviewSettings.rainTrailColor)
            "rainDimTrail" -> androidx.compose.ui.graphics.Color(livePreviewSettings.rainDimTrailColor)
            "ui" -> androidx.compose.ui.graphics.Color(livePreviewSettings.uiColor)
            "background" -> androidx.compose.ui.graphics.Color(livePreviewSettings.backgroundColor)
            else -> androidx.compose.ui.graphics.Color(0xFF00FF00)
        }
        
        CustomColorPickerDialog(
            initialColor = initialColor,
            onColorSelected = { color ->
                val colorValue = color.toArgb().toLong()
                onUpdateLivePreview(MatrixSettingType.ColorPicker, mapOf("type" to customColorTarget, "color" to colorValue))
                showCustomColorDialog = false
            },
            onDismiss = { showCustomColorDialog = false }
        )
    }
}

/**
 * Individual color picker row with swatches and custom color option
 */
@Composable
private fun ColorPickerRow(
    title: String,
    currentColor: Long,
    onColorSelected: (Long) -> Unit,
    onCustomColorClick: () -> Unit,
    uiColors: UIColorScheme
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Title
        Text(
            text = title,
            color = uiColors.textPrimary,
            style = AppTypography.bodySmall
        )
        
        // Color swatches row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Preset color swatches
            items(8) { index ->
                val color = when (index) {
                    0 -> androidx.compose.ui.graphics.Color(0xFF00FF00) // Matrix Green
                    1 -> androidx.compose.ui.graphics.Color(0xFF0080FF) // Neo Blue
                    2 -> androidx.compose.ui.graphics.Color(0xFFFF0040) // Cyber Red
                    3 -> androidx.compose.ui.graphics.Color(0xFF00FFFF) // Cyan
                    4 -> androidx.compose.ui.graphics.Color(0xFF8000FF) // Purple
                    5 -> androidx.compose.ui.graphics.Color(0xFFFF8000) // Orange
                    6 -> androidx.compose.ui.graphics.Color(0xFFFFFFFF) // White
                    7 -> androidx.compose.ui.graphics.Color(0xFF000000) // Black
                    else -> androidx.compose.ui.graphics.Color(0xFF00FF00)
                }
                
                val colorValue = color.toArgb().toLong()
                val isSelected = colorValue == currentColor
                
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(color, RoundedCornerShape(6.dp))
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) Color.White else uiColors.border,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable { onColorSelected(colorValue) }
                )
            }
            
            // Custom color swatch
            item {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFF1A1A1A), RoundedCornerShape(6.dp))
                        .border(1.dp, uiColors.border, RoundedCornerShape(6.dp))
                        .clickable { onCustomColorClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = uiColors.primary,
                        style = AppTypography.headlineSmall
                    )
                }
            }
        }
    }
}

/**
 * Reset All Settings Editor with confirmation dialog
 */
@Composable
private fun CompactResetAllEditor(
    colorScheme: MatrixColor,
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit
) {
    val uiColors = getUIColorScheme(colorScheme)
    var showConfirmationDialog by remember { mutableStateOf(false) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simple warning message
        Text(
            text = "⚠️ This will reset all settings to defaults",
            color = uiColors.buttonCancelText,
            style = AppTypography.bodyMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Text(
            text = "Custom symbol sets will be preserved.",
            color = uiColors.textPrimary,
style = AppTypography.bodySmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        SettingsActionButtons(
            onConfirm = {
                showConfirmationDialog = true
            },
            onReset = onReset,
            onCancel = onCancel,
            uiColors = uiColors
        )
    }
    
    // Confirmation Dialog
    if (showConfirmationDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = {
                Text(
                    text = "Reset All Settings",
                    color = uiColors.textPrimary,
                    style = AppTypography.bodyMedium
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to reset all settings to their default values? Custom symbol sets will be preserved.",
                    color = uiColors.textPrimary,
                    style = AppTypography.bodyMedium
                )
            },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showConfirmationDialog = false
                        onConfirm()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = uiColors.buttonCancelText
                    )
                ) {
                    Text(
                        text = "RESET ALL",
                        style = AppTypography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showConfirmationDialog = false },
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = uiColors.textSecondary
                    )
                ) {
                    Text(
                        text = "Cancel",
                        style = AppTypography.bodyMedium
                    )
                }
            },
            containerColor = uiColors.background,
            titleContentColor = uiColors.textPrimary,
            textContentColor = uiColors.textPrimary
        )
    }
}

/**
 * Get display name for setting type
 */
private fun getSettingDisplayName(setting: MatrixSettingType): String {
    return when (setting) {
        MatrixSettingType.FallSpeed -> "Fall Speed"
        MatrixSettingType.SymbolSet -> "Symbol Set"
        MatrixSettingType.ColorTint -> "Color Theme"
        MatrixSettingType.ColorPicker -> "Color Picker"
        MatrixSettingType.FontSize -> "Font Size"
        MatrixSettingType.ColumnCount -> "Column Count"
        MatrixSettingType.TargetFps -> "Frame Rate"
        MatrixSettingType.RowHeightMultiplier -> "Row Height"
        MatrixSettingType.MaxTrailLength -> "Trail Length"
        MatrixSettingType.MaxBrightTrailLength -> "Bright Trail Length"
        MatrixSettingType.GlowIntensity -> "Glow Intensity"
        MatrixSettingType.JitterAmount -> "Jitter Amount"
        MatrixSettingType.FlickerRate -> "Flicker Rate"
        MatrixSettingType.MutationRate -> "Mutation Rate"
        MatrixSettingType.ColumnStartDelay -> "Start Delay"
        MatrixSettingType.ColumnRestartDelay -> "Restart Delay"
        MatrixSettingType.InitialActivePercentage -> "Active Percentage"
        MatrixSettingType.SpeedVariationRate -> "Speed Variation"
        MatrixSettingType.GrainDensity -> "Grain Density"
        MatrixSettingType.GrainOpacity -> "Grain Opacity"
        MatrixSettingType.ResetAll -> "Reset All Settings"
        
        // New enum cases
        MatrixSettingType.ColorAndBrightness -> "Color & Brightness"
        MatrixSettingType.BrightnessControls -> "Brightness Controls"
        MatrixSettingType.RainHeadColor -> "Rain Head Color"
        MatrixSettingType.RainBrightTrailColor -> "Rain Bright Trail Color"
        MatrixSettingType.RainTrailColor -> "Rain Trail Color"
        MatrixSettingType.RainDimTrailColor -> "Rain Dim Trail Color"
        MatrixSettingType.UiColor -> "UI Color"
        MatrixSettingType.LeadBrightnessMultiplier -> "Lead Brightness Multiplier"
        MatrixSettingType.BrightTrailBrightnessMultiplier -> "Bright Trail Brightness Multiplier"
        MatrixSettingType.TrailBrightnessMultiplier -> "Trail Brightness Multiplier"
        MatrixSettingType.DimTrailBrightnessMultiplier -> "Dim Trail Brightness Multiplier"
    }
}

/**
 * Compact Future Feature editor for the overlay
 */
@Composable
private fun CompactFutureFeatureEditor(
    featureName: String,
    description: String,
    uiColors: UIColorScheme
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(uiColors.backgroundSecondary, RoundedCornerShape(8.dp))
            .border(1.dp, uiColors.border, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        Text(
            text = featureName,
            color = uiColors.textAccent,
            style = AppTypography.headlineSmall
        )
        
        // Future feature message
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(uiColors.backgroundSecondary, RoundedCornerShape(6.dp))
                .border(1.dp, uiColors.borderDim, RoundedCornerShape(6.dp))
                .padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "🚀 Future Feature",
                    color = uiColors.textAccent,
                    style = AppTypography.bodyMedium
                )
                
                Text(
                    text = description,
                    color = uiColors.textSecondary,
                    style = AppTypography.bodySmall
                )
            }
        }
    }
}
