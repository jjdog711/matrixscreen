package com.example.matrixscreen.ui.settings.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.registry.BuiltInThemes
import com.example.matrixscreen.data.registry.ThemePresetRegistryImpl
import com.example.matrixscreen.data.registry.ThemePresetId as RegistryThemePresetId
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.settings.model.BooleanSpec
import com.example.matrixscreen.ui.settings.model.ColorSpec
import com.example.matrixscreen.ui.settings.model.THEME_SPECS
import com.example.matrixscreen.ui.settings.model.specFor
import com.example.matrixscreen.ui.settings.model.LinkUiAndRainColors
import com.example.matrixscreen.ui.settings.model.AdvancedColorsEnabled
import com.example.matrixscreen.ui.settings.model.BgColor
import com.example.matrixscreen.ui.settings.model.HeadColor
import com.example.matrixscreen.ui.settings.model.BrightColor
import com.example.matrixscreen.ui.settings.model.TrailColor
import com.example.matrixscreen.ui.settings.model.DimColor
import com.example.matrixscreen.ui.settings.model.UiAccent
import com.example.matrixscreen.ui.settings.model.UiOverlay
import com.example.matrixscreen.ui.settings.model.UiSelectBg
import com.example.matrixscreen.ui.settings.model.ThemePresetId
import com.example.matrixscreen.ui.components.MatrixThemeSelector
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.MatrixColorTheme
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.core.util.applyColorLinking

/**
 * Theme settings screen with presets, color mode toggle, and advanced color controls
 */
@Composable
fun ThemeSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val currentSettings by remember {
        derivedStateOf { uiState.draft }
    }
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    val themeRegistry = remember { ThemePresetRegistryImpl() }
    val displaySettings by remember {
        derivedStateOf {
            if (currentSettings.linkUiAndRainColors) {
                applyColorLinking(currentSettings)
            } else {
                currentSettings
            }
        }
    }
    
    // Color picker state
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var selectedColorType by remember { mutableStateOf("") }
    
    SettingsScreenContainer(
        title = null,
        onBack = onBack,
        ui = ui,
        optimizedSettings = optimizedSettings,
        expanded = true,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sectionSpacing)
        ) {
            SettingsSection(
                title = "Theme Presets",
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                ThemePresetSelector(
                    currentSettings = currentSettings,
                    settingsViewModel = settingsViewModel,
                    themeRegistry = themeRegistry
                )
            }

            SettingsSection(
                title = "Advanced Colors",
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                AdvancedColorsToggle(
                    settingsViewModel = settingsViewModel,
                    currentSettings = currentSettings,
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
            }

            SettingsSection(
                title = null,
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                ColorControls(
                    settingsViewModel = settingsViewModel,
                    currentSettings = currentSettings,
                    displaySettings = displaySettings,
                    ui = ui,
                    optimizedSettings = optimizedSettings,
                    onColorClick = { type ->
                        selectedColorType = type
                        showColorPickerDialog = true
                    }
                )
            }
        }
    }
    
    // Color picker dialog
    if (showColorPickerDialog) {
        ColorPickerDialog(
            isOpen = showColorPickerDialog,
            initialColor = getCurrentColor(currentSettings, selectedColorType),
            onColorSelected = { color ->
                updateColor(settingsViewModel, selectedColorType, color)
                showColorPickerDialog = false
            },
            onDismiss = { showColorPickerDialog = false }
        )
    }
}

@Composable
private fun ThemePresetSelector(
    currentSettings: MatrixSettings,
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    themeRegistry: ThemePresetRegistryImpl
) {
    val presetThemes = remember(themeRegistry) {
        BuiltInThemes.ALL_BUILT_IN.map { id ->
            val colors = themeRegistry.getColors(id)
            MatrixColorTheme(
                name = themeRegistry.getDisplayName(id),
                headColor = Color(colors.headColor),
                brightTrailColor = Color(colors.brightTrailColor),
                trailColor = Color(colors.trailColor),
                dimTrailColor = Color(colors.dimColor),
                backgroundColor = Color(colors.backgroundColor)
            ) to id
        }
    }

    val currentPresetName = currentSettings.themePresetId?.let { presetId ->
        runCatching { themeRegistry.getDisplayName(RegistryThemePresetId(presetId)) }.getOrNull()
    }

    MatrixThemeSelector(
        currentThemeName = currentPresetName,
        onThemeSelected = { theme ->
            presetThemes.firstOrNull { it.first.name == theme.name }?.second?.let { themeId ->
                applyThemePreset(settingsViewModel, themeId, themeRegistry)
            }
        },
        themes = presetThemes.map { it.first }
    )
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
        val advancedColorsSpec = THEME_SPECS.specFor(AdvancedColorsEnabled)
        LabeledSwitch(
            label = advancedColorsSpec.label,
            checked = currentSettings.advancedColorsEnabled,
            onCheckedChange = { settingsViewModel.updateDraft(AdvancedColorsEnabled, it) },
            help = advancedColorsSpec.help
        )
        
        // Link UI & Rain Colors Toggle (only show when advanced colors are enabled)
        if (currentSettings.advancedColorsEnabled) {
            val linkColorsSpec = THEME_SPECS.specFor(LinkUiAndRainColors)
            LabeledSwitch(
                label = linkColorsSpec.label,
                checked = currentSettings.linkUiAndRainColors,
                onCheckedChange = { settingsViewModel.updateDraft(LinkUiAndRainColors, it) },
                help = linkColorsSpec.help
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
    displaySettings: MatrixSettings,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    onColorClick: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.md)
    ) {
        val linkedUiSpecs = setOf(UiAccent, UiOverlay, UiSelectBg)
        val linkingActive = currentSettings.linkUiAndRainColors

        // Render color specs from THEME_SPECS (excluding boolean specs)
        THEME_SPECS.filterIsInstance<ColorSpec>().forEach { spec ->
            val isLinkedSpec = linkingActive && linkedUiSpecs.contains(spec.id)
            ColorControlRow(
                spec = spec,
                currentColor = getCurrentColor(displaySettings, spec.id.key),
                onColorClick = {
                    if (!isLinkedSpec) {
                        onColorClick(spec.id.key)
                    }
                },
                enabled = !isLinkedSpec,
                helperText = spec.help,
                statusText = if (isLinkedSpec) "Linked to rain colors" else null,
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
    enabled: Boolean = true,
    helperText: String? = null,
    statusText: String? = null,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    val contentAlpha = if (enabled) 1f else 0.6f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = spec.label,
                style = AppTypography.titleMedium,
                color = ui.textPrimary
            )
            helperText?.let {
                Text(
                    text = it,
                    style = AppTypography.bodySmall,
                    color = ui.textSecondary
                )
            }
            statusText?.let {
                Text(
                    text = it,
                    style = AppTypography.bodySmall,
                    color = ui.textAccent
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
                    .alpha(contentAlpha)
                    .let { base ->
                        if (enabled) {
                            base.clickable { onColorClick() }
                        } else {
                            base
                        }
                    }
            )
            
            // Picker button
            Text(
                text = "•••",
                style = AppTypography.bodyMedium,
                color = ui.textSecondary,
                modifier = Modifier
                    .alpha(contentAlpha)
                    .let { base ->
                        if (enabled) {
                            base.clickable { onColorClick() }
                        } else {
                            base
                        }
                    }
            )
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
    themeId: RegistryThemePresetId,
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
    // Ensure renderer uses per-setting rain colors instead of legacy tint
    settingsViewModel.updateDraft(AdvancedColorsEnabled, true)
    
    // Set the theme preset ID
    settingsViewModel.updateDraft(ThemePresetId, themeId.value)
}

