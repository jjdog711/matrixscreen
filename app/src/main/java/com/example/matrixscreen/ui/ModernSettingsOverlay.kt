package com.example.matrixscreen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectDragGestures
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow
import com.example.matrixscreen.ui.theme.ModernButtonWithGlow

/**
 * Reusable labeled slider component with live preview and persistence
 * Follows the design spec: label left, value right, thin track, dynamic colors
 */
@Composable
private fun LabeledSlider(
    label: String,
    valueText: String,
    value: Float,
    onValueChangePreview: (Float) -> Unit,          // live preview while dragging
    onValueChangePersist: (Float) -> Unit,          // persist on release
    valueRange: ClosedFloatingPointRange<Float>,
    step: Float,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme
) {
    var last by remember(value) { mutableStateOf(value) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = AppTypography.bodyMedium, color = ui.textPrimary)
            Text(valueText, style = AppTypography.bodySmall, color = ui.textSecondary)
        }
        Slider(
            value = value,
            onValueChange = {
                val v = it.coerceIn(valueRange)
                last = v
                onValueChangePreview(v)            // live preview
            },
            onValueChangeFinished = { onValueChangePersist(last) }, // ✅ persist final value
            valueRange = valueRange,
            steps = ((valueRange.endInclusive - valueRange.start) / step)
                .toInt().coerceAtLeast(1) - 1,    // ✅ guard against negatives
            colors = SliderDefaults.colors(
                thumbColor = ui.primary,
                activeTrackColor = ui.primary,
                inactiveTrackColor = ui.borderDim
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("LOW",  style = AppTypography.bodySmall, color = ui.textSecondary)
            Text("HIGH", style = AppTypography.bodySmall, color = ui.textSecondary)
        }
    }
}

/**
 * Modern compact settings overlay - bottom-anchored, semi-transparent, max-width 340dp
 */
@Composable
fun ModernSettingsOverlay(
    settingsState: SettingsState,
    currentSettings: MatrixSettings,
    livePreviewSettings: MatrixSettings?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateDown: () -> Unit,
    onUpdateLivePreview: (MatrixSettingType, Any) -> Unit,
    onStartEditing: (MatrixSettingType) -> Unit,
    onPersistSetting: (MatrixSettingType, Any) -> Unit,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    onNavigateToCustomSets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    // Use live preview settings if available, otherwise use regular settings
    val displaySettings = livePreviewSettings ?: currentSettings
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(settingsState) {
                // Handle swipe gestures for all states
                detectDragGestures(
                    onDragEnd = { },
                    onDrag = { _, dragAmount ->
                        when {
                            dragAmount.y < -50f -> onSwipeUp() // Swipe up to show overlay
                            dragAmount.y > 50f -> onSwipeDown() // Swipe down to hide overlay
                        }
                    }
                )
            }
    ) {
        // Compact bottom-anchored overlay
        when (settingsState) {
            is SettingsState.MatrixScreen -> {
                // Show minimal overlay or nothing
                CompactOverlayTrigger(
                    onTap = onSwipeUp,
                    ui = ui
                )
            }
            is SettingsState.SettingsList -> {
                CompactSettingsList(
                    currentSettings = displaySettings,
                    onSettingSelected = { setting ->
                        // Start editing the setting
                        onStartEditing(setting)
                    },
                    onBack = onBack,
                    onNavigateToCustomSets = onNavigateToCustomSets,
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
            }
            is SettingsState.Editing -> {
                CompactSettingEditor(
                    setting = settingsState.setting,
                    currentSettings = displaySettings,
                    onUpdateLivePreview = onUpdateLivePreview,
                    onPersistSetting = onPersistSetting,
                    onConfirm = onConfirm,
                    onCancel = onCancel,
                    onNavigateUp = onNavigateUp,
                    onNavigateDown = onNavigateDown,
                    ui = ui
                )
            }
        }
    }
}

/**
 * Minimal overlay trigger when on matrix screen
 */
@Composable
private fun CompactOverlayTrigger(
    onTap: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                // Double tap to open settings
            }
    ) {
        // Small floating indicator in bottom right
        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(48.dp),
            color = ui.overlayBackground,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 4.dp
        ) {
            IconButton(
                onClick = onTap,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = ui.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Compact settings list - bottom-anchored card
 */
@Composable
private fun CompactSettingsList(
    currentSettings: MatrixSettings,
    onSettingSelected: (MatrixSettingType) -> Unit,
    onBack: () -> Unit,
    onNavigateToCustomSets: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .widthIn(max = 340.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .wrapContentHeight()
                .navigationBarsPadding(),
            color = ui.overlayBackground,
            contentColor = ui.textPrimary,
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
                    ModernTextWithGlow(
                        text = "SETTINGS",
                        style = AppTypography.headlineSmall,
                        color = ui.textPrimary,
                        settings = optimizedSettings
                    )
                    
                    IconButton(
            onClick = onBack,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ui.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Settings grid
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Basic Settings
                    CompactSettingItem(
                        title = "Fall Speed",
                        value = "${(currentSettings.fallSpeed * 100).toInt()}%",
                        onClick = { onSettingSelected(MatrixSettingType.FallSpeed) },
                        ui = ui
                    )
                    
                    CompactSettingItem(
                        title = "Symbol Set",
                        value = currentSettings.symbolSet.displayName,
                        onClick = { onSettingSelected(MatrixSettingType.SymbolSet) },
                        ui = ui
                    )
                    
                    CompactSettingItem(
                        title = "Color Theme",
                        value = if (currentSettings.advancedColorsEnabled) "Advanced" else currentSettings.colorTint.displayName,
                        onClick = { onSettingSelected(MatrixSettingType.ColorPicker) },
                        ui = ui
                    )
                    
                    CompactSettingItem(
                        title = "Font Size",
                        value = "${currentSettings.fontSize.toInt()}dp",
                        onClick = { onSettingSelected(MatrixSettingType.FontSize) },
                        ui = ui
                    )
                    
                    CompactSettingItem(
                        title = "Columns",
                        value = "${currentSettings.columnCount}",
                        onClick = { onSettingSelected(MatrixSettingType.ColumnCount) },
                        ui = ui
                    )
                    
                    CompactSettingItem(
                        title = "Custom Sets",
                        value = "${currentSettings.savedCustomSets.size} sets",
                        onClick = onNavigateToCustomSets,
                        ui = ui
                    )
                }
            }
        }
    }
}

/**
 * Compact setting editor - bottom-anchored card
 */
@Composable
private fun CompactSettingEditor(
    setting: MatrixSettingType,
    currentSettings: MatrixSettings,
    onUpdateLivePreview: (MatrixSettingType, Any) -> Unit,
    onPersistSetting: (MatrixSettingType, Any) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onNavigateUp: () -> Unit,
    onNavigateDown: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val swipeThresholdPx = with(density) { 48.dp.toPx() }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Surface(
            modifier = modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .widthIn(max = 340.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .wrapContentHeight()
                .navigationBarsPadding()
                .pointerInput(Unit) {
                    var dragX = 0f
                    var dragY = 0f
                    
                    detectDragGestures(
                        onDragEnd = {
                            val horizontal = kotlin.math.abs(dragX) > swipeThresholdPx &&
                                             kotlin.math.abs(dragX) > kotlin.math.abs(dragY) * 1.5f
                            if (horizontal) {
                                if (dragX < 0f) onNavigateDown() else onNavigateUp()
                            }
                            dragX = 0f; dragY = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume() // don't propagate to background
                        dragX += dragAmount.x
                        dragY += dragAmount.y
                    }
                },
            color = ui.overlayBackground,
            contentColor = ui.textPrimary,
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        ) {
    Column(
                modifier = Modifier
                    .padding(12.dp)
                    .imePadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
                // Header with navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateUp,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous",
                            tint = ui.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = setting.name.uppercase(),
                        style = AppTypography.headlineSmall,
                        color = ui.textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    
                    IconButton(
                        onClick = onNavigateDown,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next",
                            tint = ui.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Setting editor content
                when (setting) {
                    // Motion / Performance
                    MatrixSettingType.FallSpeed -> {
                        LabeledSlider(
                            label = "FALL SPEED",
                            valueText = "%.1f×".format(currentSettings.fallSpeed),
                            value = currentSettings.fallSpeed,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0.5f..5.0f,
                            step = 0.1f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.TargetFps -> {
                        LabeledSlider(
                            label = "TARGET FPS",
                            valueText = "${currentSettings.targetFps} fps",
                            value = currentSettings.targetFps.toFloat(),
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 15f..60f,
                            step = 5f,
                            ui = ui
                        )
                    }
                    
                    // Appearance / Geometry
                    MatrixSettingType.FontSize -> {
                        LabeledSlider(
                            label = "FONT SIZE",
                            valueText = "%.0fdp".format(currentSettings.fontSize),
                            value = currentSettings.fontSize,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 8f..24f,
                            step = 0.5f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.ColumnCount -> {
                        LabeledSlider(
                            label = "COLUMN COUNT",
                            valueText = "${currentSettings.columnCount}",
                            value = currentSettings.columnCount.toFloat(),
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v.toInt()) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v.toInt()) },
                            valueRange = 50f..150f,
                            step = 1f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.RowHeightMultiplier -> {
                        LabeledSlider(
                            label = "ROW HEIGHT MULTIPLIER",
                            valueText = "%.2f×".format(currentSettings.rowHeightMultiplier),
                            value = currentSettings.rowHeightMultiplier,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0.7f..1.2f,
                            step = 0.05f,
                            ui = ui
                        )
                    }
                    
                    // Trails / Glow
                    MatrixSettingType.MaxTrailLength -> {
                        LabeledSlider(
                            label = "MAX TRAIL LENGTH",
                            valueText = "${currentSettings.maxTrailLength} px",
                            value = currentSettings.maxTrailLength.toFloat(),
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v.toInt()) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v.toInt()) },
                            valueRange = 20f..100f,
                            step = 1f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.MaxBrightTrailLength -> {
                        LabeledSlider(
                            label = "BRIGHT TRAIL LENGTH",
                            valueText = "${currentSettings.maxBrightTrailLength} px",
                            value = currentSettings.maxBrightTrailLength.toFloat(),
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v.toInt()) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v.toInt()) },
                            valueRange = 2f..15f,
                            step = 1f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.GlowIntensity -> {
                        LabeledSlider(
                            label = "GLOW INTENSITY",
                            valueText = "%.2f".format(currentSettings.glowIntensity),
                            value = currentSettings.glowIntensity,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0.0f..2.0f,
                            step = 0.05f,
                            ui = ui
                        )
                    }
                    
                    // Dynamics
                    MatrixSettingType.JitterAmount -> {
                        LabeledSlider(
                            label = "JITTER AMOUNT",
                            valueText = "%.1f".format(currentSettings.jitterAmount),
                            value = currentSettings.jitterAmount,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0.0f..3.0f,
                            step = 0.1f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.FlickerRate -> {
                        LabeledSlider(
                            label = "FLICKER RATE",
                            valueText = "%.2f".format(currentSettings.flickerRate),
                            value = currentSettings.flickerRate,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0.0f..0.2f,
                            step = 0.01f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.MutationRate -> {
                        LabeledSlider(
                            label = "MUTATION RATE",
                            valueText = "%.3f".format(currentSettings.mutationRate),
                            value = currentSettings.mutationRate,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0.0f..0.1f,
                            step = 0.005f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.SpeedVariationRate -> {
                        LabeledSlider(
                            label = "SPEED VARIATION RATE",
                            valueText = "%.3f".format(currentSettings.speedVariationRate),
                            value = currentSettings.speedVariationRate,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0.0f..0.01f,
                            step = 0.001f,
                            ui = ui
                        )
                    }
                    
                    // Timing
                    MatrixSettingType.ColumnStartDelay -> {
                        LabeledSlider(
                            label = "COLUMN START DELAY",
                            valueText = "%.2fs".format(currentSettings.columnStartDelay),
                            value = currentSettings.columnStartDelay,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0f..10f,
                            step = 0.05f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.ColumnRestartDelay -> {
                        LabeledSlider(
                            label = "COLUMN RESTART DELAY",
                            valueText = "%.2fs".format(currentSettings.columnRestartDelay),
                            value = currentSettings.columnRestartDelay,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0.5f..5.0f,
                            step = 0.05f,
                            ui = ui
                        )
                    }
                    
                    // Population / Grain
                    MatrixSettingType.InitialActivePercentage -> {
                        LabeledSlider(
                            label = "ACTIVE %",
                            valueText = "${(currentSettings.initialActivePercentage * 100).toInt()}%",
                            value = currentSettings.initialActivePercentage,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0.1f..0.8f,
                            step = 0.05f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.GrainDensity -> {
                        LabeledSlider(
                            label = "GRAIN DENSITY",
                            valueText = "${currentSettings.grainDensity}",
                            value = currentSettings.grainDensity.toFloat(),
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v.toInt()) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v.toInt()) },
                            valueRange = 0f..500f,
                            step = 10f,
                            ui = ui
                        )
                    }
                    MatrixSettingType.GrainOpacity -> {
                        LabeledSlider(
                            label = "GRAIN OPACITY",
                            valueText = "%.3f".format(currentSettings.grainOpacity),
                            value = currentSettings.grainOpacity,
                            onValueChangePreview = { v -> onUpdateLivePreview(setting, v) },
                            onValueChangePersist = { v -> onPersistSetting(setting, v) },
                            valueRange = 0.0f..0.1f,
                            step = 0.005f,
                            ui = ui
                        )
                    }
                    
                    // Non-slider settings
                    MatrixSettingType.SymbolSet, MatrixSettingType.ColorTint, MatrixSettingType.ColorPicker, MatrixSettingType.ResetAll -> {
                        // Placeholder for non-slider settings
                        Text(
                            text = "Setting: ${setting.name}",
                            style = AppTypography.bodyMedium,
                            color = ui.textSecondary
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
                            color = ui.textSecondary
                        )
                    }
                }
                
                // Bottom action bar with icon buttons
                BottomActionBarIcons(
                    onConfirm = onConfirm,
                    onCancel = onCancel,
                    onReset = { 
                        // Reset current setting to its default value
                        val defaultSettings = MatrixSettings()
                        val defaultValue: Any = when (setting) {
                            MatrixSettingType.FallSpeed -> defaultSettings.fallSpeed
                            MatrixSettingType.SymbolSet -> defaultSettings.symbolSet
                            MatrixSettingType.ColorTint -> defaultSettings.colorTint
                            MatrixSettingType.FontSize -> defaultSettings.fontSize
                            MatrixSettingType.ColumnCount -> defaultSettings.columnCount
                            MatrixSettingType.TargetFps -> defaultSettings.targetFps
                            MatrixSettingType.RowHeightMultiplier -> defaultSettings.rowHeightMultiplier
                            MatrixSettingType.MaxTrailLength -> defaultSettings.maxTrailLength
                            MatrixSettingType.MaxBrightTrailLength -> defaultSettings.maxBrightTrailLength
                            MatrixSettingType.GlowIntensity -> defaultSettings.glowIntensity
                            MatrixSettingType.JitterAmount -> defaultSettings.jitterAmount
                            MatrixSettingType.FlickerRate -> defaultSettings.flickerRate
                            MatrixSettingType.MutationRate -> defaultSettings.mutationRate
                            MatrixSettingType.ColumnStartDelay -> defaultSettings.columnStartDelay
                            MatrixSettingType.ColumnRestartDelay -> defaultSettings.columnRestartDelay
                            MatrixSettingType.InitialActivePercentage -> defaultSettings.initialActivePercentage
                            MatrixSettingType.SpeedVariationRate -> defaultSettings.speedVariationRate
                            MatrixSettingType.GrainDensity -> defaultSettings.grainDensity
                            MatrixSettingType.GrainOpacity -> defaultSettings.grainOpacity
                            MatrixSettingType.ColorPicker -> defaultSettings.advancedColorsEnabled
                            MatrixSettingType.ResetAll -> Unit // No-op for ResetAll
                            
                            // New enum cases
                            MatrixSettingType.ColorAndBrightness -> defaultSettings.colorTint
                            MatrixSettingType.BrightnessControls -> defaultSettings.brightnessControlsEnabled
                            MatrixSettingType.RainHeadColor -> defaultSettings.rainHeadColor
                            MatrixSettingType.RainBrightTrailColor -> defaultSettings.rainBrightTrailColor
                            MatrixSettingType.RainTrailColor -> defaultSettings.rainTrailColor
                            MatrixSettingType.RainDimTrailColor -> defaultSettings.rainDimTrailColor
                            MatrixSettingType.UiColor -> defaultSettings.uiColor
                            MatrixSettingType.LeadBrightnessMultiplier -> defaultSettings.leadBrightnessMultiplier
                            MatrixSettingType.BrightTrailBrightnessMultiplier -> defaultSettings.brightTrailBrightnessMultiplier
                            MatrixSettingType.TrailBrightnessMultiplier -> defaultSettings.trailBrightnessMultiplier
                            MatrixSettingType.DimTrailBrightnessMultiplier -> defaultSettings.dimTrailBrightnessMultiplier
                        }
                        onUpdateLivePreview(setting, defaultValue)
                    },
                    ui = ui
                )
            }
        }
    }
}

/**
 * Compact setting item for the settings list
 */
@Composable
private fun CompactSettingItem(
    title: String,
    value: String,
    onClick: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        color = ui.backgroundSecondary,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = AppTypography.bodyMedium,
                color = ui.textPrimary
            )
            
            Text(
                text = value,
                style = AppTypography.bodyMedium,
                color = ui.textAccent
            )
        }
    }
}

/**
 * Compact slider editor
 */
@Composable
private fun CompactSliderEditor(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    formatValue: (Float) -> String,
    onValueChange: (Float) -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = AppTypography.bodyMedium,
                color = ui.textPrimary
            )
            
            Text(
                text = formatValue(value),
                style = AppTypography.bodyMedium,
                color = ui.textAccent
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = ui.primary,
                activeTrackColor = ui.sliderActive,
                inactiveTrackColor = ui.sliderInactive
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SMALL",
                style = AppTypography.bodySmall,
                color = ui.textSecondary
            )
            Text(
                text = "LARGE",
                style = AppTypography.bodySmall,
                color = ui.textSecondary
            )
        }
    }
}

/**
 * Bottom action bar with icon buttons (≥48dp tap targets)
 */
@Composable
private fun BottomActionBarIcons(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onReset: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        IconButton(
            onClick = onCancel,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Cancel",
                tint = ui.buttonCancelText,
                modifier = Modifier.size(24.dp)
            )
        }
        
        IconButton(
            onClick = onReset,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Reset",
                tint = ui.buttonCancelText,
                modifier = Modifier.size(24.dp)
            )
        }
        
        IconButton(
            onClick = onConfirm,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Confirm",
                tint = ui.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Helper function to get current value for a setting
 */
private fun getCurrentValue(settings: MatrixSettings, setting: MatrixSettingType): Any {
    return when (setting) {
        MatrixSettingType.FallSpeed -> settings.fallSpeed
        MatrixSettingType.SymbolSet -> settings.symbolSet
        MatrixSettingType.ColorTint -> settings.colorTint
        MatrixSettingType.FontSize -> settings.fontSize
        MatrixSettingType.ColumnCount -> settings.columnCount
        MatrixSettingType.TargetFps -> settings.targetFps
        MatrixSettingType.RowHeightMultiplier -> settings.rowHeightMultiplier
        MatrixSettingType.MaxTrailLength -> settings.maxTrailLength
        MatrixSettingType.MaxBrightTrailLength -> settings.maxBrightTrailLength
        MatrixSettingType.GlowIntensity -> settings.glowIntensity
        MatrixSettingType.JitterAmount -> settings.jitterAmount
        MatrixSettingType.FlickerRate -> settings.flickerRate
        MatrixSettingType.MutationRate -> settings.mutationRate
        MatrixSettingType.ColumnStartDelay -> settings.columnStartDelay
        MatrixSettingType.ColumnRestartDelay -> settings.columnRestartDelay
        MatrixSettingType.InitialActivePercentage -> settings.initialActivePercentage
        MatrixSettingType.SpeedVariationRate -> settings.speedVariationRate
        MatrixSettingType.GrainDensity -> settings.grainDensity
        MatrixSettingType.GrainOpacity -> settings.grainOpacity
        MatrixSettingType.ColorPicker -> settings.advancedColorsEnabled
        MatrixSettingType.ResetAll -> Unit
        
        // New enum cases
        MatrixSettingType.ColorAndBrightness -> settings.colorTint
        MatrixSettingType.BrightnessControls -> settings.brightnessControlsEnabled
        MatrixSettingType.RainHeadColor -> settings.rainHeadColor
        MatrixSettingType.RainBrightTrailColor -> settings.rainBrightTrailColor
        MatrixSettingType.RainTrailColor -> settings.rainTrailColor
        MatrixSettingType.RainDimTrailColor -> settings.rainDimTrailColor
        MatrixSettingType.UiColor -> settings.uiColor
        MatrixSettingType.LeadBrightnessMultiplier -> settings.leadBrightnessMultiplier
        MatrixSettingType.BrightTrailBrightnessMultiplier -> settings.brightTrailBrightnessMultiplier
        MatrixSettingType.TrailBrightnessMultiplier -> settings.trailBrightnessMultiplier
        MatrixSettingType.DimTrailBrightnessMultiplier -> settings.dimTrailBrightnessMultiplier
    }
}