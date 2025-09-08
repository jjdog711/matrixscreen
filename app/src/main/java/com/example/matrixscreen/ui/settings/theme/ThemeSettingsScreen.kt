package com.example.matrixscreen.ui.settings.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.registry.BuiltInThemes
import com.example.matrixscreen.data.registry.ThemePresetId
import com.example.matrixscreen.data.registry.ThemePresetRegistryImpl
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.settings.theme.components.*
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow

/**
 * Theme settings screen with presets, color mode toggle, and advanced color controls
 */
@Composable
fun ThemeSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onBack: () -> Unit,
    onNavigateToCustomSets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = uiState.draft
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    // Color picker state
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var selectedColorType by remember { mutableStateOf("") }
    
    SettingsScreenContainer(
        title = "THEME",
        onBack = onBack,
        ui = ui,
        optimizedSettings = optimizedSettings,
        content = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sectionSpacing)
        ) {
            // Presets Section
            SettingsSection(
                title = "Presets",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                PresetsRow(
                    settingsViewModel = settingsViewModel,
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Advanced Colors Toggle Section
            SettingsSection(
                title = "Color Mode",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                AdvancedColorsToggle(
                    settingsViewModel = settingsViewModel,
                    currentSettings = currentSettings,
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Color Controls Section
            SettingsSection(
                title = if (currentSettings.advancedColorsEnabled) "Advanced Colors" else "Basic Colors",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                ColorControls(
                    settingsViewModel = settingsViewModel,
                    currentSettings = currentSettings,
                    ui = ui,
                    optimizedSettings = optimizedSettings,
                    onColorClick = { type ->
                        selectedColorType = type
                        showColorPickerDialog = true
                    }
                )
                }
            )
        }
        },
        modifier = modifier
    )
    
    // Color picker dialog
    if (showColorPickerDialog) {
        ColorPickerDialog(
            colorType = selectedColorType,
            currentColor = getCurrentColor(currentSettings, selectedColorType),
            onColorSelected = { color ->
                updateColor(settingsViewModel, selectedColorType, color)
                showColorPickerDialog = false
            },
            onDismiss = { showColorPickerDialog = false }
        )
    }
}

/**
 * Row of preset buttons using registry system
 */
@Composable
private fun PresetsRow(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    val themeRegistry = remember { ThemePresetRegistryImpl() }
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sm)
    ) {
        BuiltInThemes.ALL_BUILT_IN.forEach { themeId ->
            PresetButton(
                name = themeRegistry.getDisplayName(themeId),
                onClick = { applyThemePreset(settingsViewModel, themeId, themeRegistry) },
                ui = ui,
                optimizedSettings = optimizedSettings
            )
        }
    }
}

/**
 * Advanced colors toggle section
 */
@Composable
private fun AdvancedColorsToggle(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    currentSettings: MatrixSettings,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.md)
    ) {
        // Advanced Colors Toggle
        val advancedColorsSpec = THEME_SPECS.find { it.id == AdvancedColorsEnabled } as BooleanSpec
        LabeledSwitch(
            spec = advancedColorsSpec,
            value = currentSettings.advancedColorsEnabled,
            onValueChange = { settingsViewModel.updateDraft(AdvancedColorsEnabled, it) },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
        // Link UI & Rain Colors Toggle (only show when advanced colors are enabled)
        if (currentSettings.advancedColorsEnabled) {
            val linkColorsSpec = THEME_SPECS.find { it.id == LinkUiAndRainColors } as BooleanSpec
            LabeledSwitch(
                spec = linkColorsSpec,
                value = currentSettings.linkUiAndRainColors,
                onValueChange = { settingsViewModel.updateDraft(LinkUiAndRainColors, it) },
                ui = ui,
                optimizedSettings = optimizedSettings
            )
        }
    }
}

/**
 * Color controls using THEME_SPECS
 */
@Composable
private fun ColorControls(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    currentSettings: MatrixSettings,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    onColorClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.md)
    ) {
        // Render color specs from THEME_SPECS (excluding boolean specs)
        THEME_SPECS.filterIsInstance<ColorSpec>().forEach { spec ->
            ColorControlRow(
                spec = spec,
                currentColor = getCurrentColor(currentSettings, spec.id.key),
                onColorClick = { onColorClick(spec.id.key) },
                ui = ui,
                optimizedSettings = optimizedSettings
            )
        }
    }
}

/**
 * Color control row using ColorSpec
 */
@Composable
private fun ColorControlRow(
    spec: ColorSpec,
    currentColor: Long,
    onColorClick: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ModernTextWithGlow(
                text = spec.label,
                style = AppTypography.titleMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
            if (spec.help != null) {
                ModernTextWithGlow(
                    text = spec.help,
                    style = AppTypography.bodySmall,
                    color = ui.textSecondary,
                    settings = optimizedSettings
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sm)
        ) {
            // Color swatch
            Box(
                modifier = Modifier
                    .size(com.example.matrixscreen.core.design.DesignTokens.Sizing.colorSwatchSize)
                    .clip(CircleShape)
                    .background(Color(currentColor))
                    .border(
                        width = 2.dp,
                        color = ui.selectionBackground,
                        shape = CircleShape
                    )
                    .clickable { onColorClick() }
            )
            
            // Picker button
            ModernTextWithGlow(
                text = "•••",
                style = AppTypography.bodyMedium,
                color = ui.textSecondary,
                settings = optimizedSettings,
                modifier = Modifier.clickable { onColorClick() }
            )
        }
    }
}

/**
 * Color picker dialog
 */
@Composable
private fun ColorPickerDialog(
    colorType: String,
    currentColor: Long,
    onColorSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(Color(currentColor)) }
    
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(com.example.matrixscreen.core.design.DesignTokens.Spacing.lg),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1A1A1A)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(com.example.matrixscreen.core.design.DesignTokens.Radius.card)
        ) {
            Column(
                modifier = Modifier.padding(com.example.matrixscreen.core.design.DesignTokens.Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.lg)
            ) {
                // Title
                Text(
                    text = "Select Color",
                    color = Color(0xFF00FF00),
                    fontSize = 18.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                
                // Color picker
                ColorPickerRow(
                    title = "Color",
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it },
                    showCustomColor = true
                )
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.md)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF330000),
                            contentColor = Color(0xFFFF6666)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Cancel",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                    
                    Button(
                        onClick = {
                            onColorSelected(selectedColor.toLongColor())
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00FF00),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Select",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Get current color value for a color type
 */
private fun getCurrentColor(settings: MatrixSettings, colorType: String): Long {
    return when (colorType) {
        "backgroundColor" -> settings.backgroundColor
        "headColor" -> settings.headColor
        "brightTrailColor" -> settings.brightTrailColor
        "trailColor" -> settings.trailColor
        "dimColor" -> settings.dimColor
        "uiAccent" -> settings.uiAccent
        "uiOverlayBg" -> settings.uiOverlayBg
        "uiSelectionBg" -> settings.uiSelectionBg
        else -> 0xFF000000L
    }
}

/**
 * Update color value for a color type
 */
private fun updateColor(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    colorType: String,
    color: Long
) {
    when (colorType) {
        "backgroundColor" -> settingsViewModel.updateDraft(BgColor, color)
        "headColor" -> settingsViewModel.updateDraft(HeadColor, color)
        "brightTrailColor" -> settingsViewModel.updateDraft(BrightColor, color)
        "trailColor" -> settingsViewModel.updateDraft(TrailColor, color)
        "dimColor" -> settingsViewModel.updateDraft(DimColor, color)
        "uiAccent" -> settingsViewModel.updateDraft(UiAccent, color)
        "uiOverlayBg" -> settingsViewModel.updateDraft(UiOverlay, color)
        "uiSelectionBg" -> settingsViewModel.updateDraft(UiSelectBg, color)
    }
}

/**
 * Apply theme preset using registry system
 */
private fun applyThemePreset(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    themeId: ThemePresetId,
    themeRegistry: ThemePresetRegistryImpl
) {
    val colors = themeRegistry.getColors(themeId)
    
    // Update all colors from the theme preset
    settingsViewModel.updateDraft(BgColor, colors.backgroundColor)
    settingsViewModel.updateDraft(HeadColor, colors.headColor)
    settingsViewModel.updateDraft(BrightColor, colors.brightTrailColor)
    settingsViewModel.updateDraft(TrailColor, colors.trailColor)
    settingsViewModel.updateDraft(DimColor, colors.dimColor)
    settingsViewModel.updateDraft(UiAccent, colors.uiAccent)
    settingsViewModel.updateDraft(UiOverlay, colors.uiOverlayBg)
    settingsViewModel.updateDraft(UiSelectBg, colors.uiSelectionBg)
    
    // Set the theme preset ID
    settingsViewModel.updateDraft(ThemePresetId, themeId.value)
}

/**
 * Color mode toggle between Simple and Advanced
 */
@Composable
private fun ColorModeToggle(
    isAdvanced: Boolean,
    onModeChanged: (Boolean) -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModernTextWithGlow(
            text = "Advanced Colors",
            style = AppTypography.titleMedium,
            color = ui.textPrimary,
            settings = optimizedSettings
        )
        
        Switch(
            checked = isAdvanced,
            onCheckedChange = onModeChanged,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ui.primary,
                checkedTrackColor = ui.primary.copy(alpha = 0.5f),
                uncheckedThumbColor = ui.textSecondary,
                uncheckedTrackColor = ui.selectionBackground
            )
        )
    }
}

/**
 * Advanced color controls with color swatches and picker buttons
 */
@Composable
private fun AdvancedColorControls(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    currentSettings: MatrixSettings,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    onColorClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Head Color
        ColorControlRow(
            label = "Head",
            color = currentSettings.headColor,
            onColorClick = { onColorClick("headColor") },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
        // Bright Trail Color
        ColorControlRow(
            label = "Bright Trail",
            color = currentSettings.brightTrailColor,
            onColorClick = { onColorClick("brightTrailColor") },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
        // Trail Color
        ColorControlRow(
            label = "Trail",
            color = currentSettings.trailColor,
            onColorClick = { onColorClick("trailColor") },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
        // Dim Trail Color
        ColorControlRow(
            label = "Dim Trail",
            color = currentSettings.dimColor,
            onColorClick = { onColorClick("dimColor") },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
        // UI Color
        ColorControlRow(
            label = "UI",
            color = currentSettings.uiAccent,
            onColorClick = { onColorClick("uiAccent") },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
        // Background Color
        ColorControlRow(
            label = "Background",
            color = currentSettings.backgroundColor,
            onColorClick = { onColorClick("backgroundColor") },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
    }
}

/**
 * Individual color control row with swatch and picker button
 */
@Composable
private fun ColorControlRow(
    label: String,
    color: Long,
    onColorClick: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModernTextWithGlow(
            text = label,
            style = AppTypography.titleMedium,
            color = ui.textPrimary,
            settings = optimizedSettings
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Color swatch
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(color))
                    .border(
                        width = 2.dp,
                        color = ui.selectionBackground,
                        shape = CircleShape
                    )
                    .clickable { onColorClick() }
            )
            
            // Picker button
            ModernTextWithGlow(
                text = "•••",
                style = AppTypography.bodyMedium,
                color = ui.textSecondary,
                settings = optimizedSettings,
                modifier = Modifier.clickable { onColorClick() }
            )
        }
    }
}
