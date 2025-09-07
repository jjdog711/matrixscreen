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
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow

/**
 * Theme settings screen with presets, color mode toggle, and advanced color controls
 */
@Composable
fun ThemeSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.SettingsViewModel,
    onBack: () -> Unit,
    onNavigateToCustomSets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSettings by settingsViewModel.settings.collectAsState()
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
            
            // Color Mode Section
            SettingsSection(
                title = "Color Mode",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                ColorModeToggle(
                    isAdvanced = currentSettings.advancedColorsEnabled,
                    onModeChanged = { settingsViewModel.updateAdvancedColorsEnabled(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Advanced Colors Section (only show if advanced mode is enabled)
            if (currentSettings.advancedColorsEnabled) {
                SettingsSection(
                    title = "Advanced Colors",
                    ui = ui,
                    optimizedSettings = optimizedSettings,
                    content = {
                    AdvancedColorControls(
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
        }
        },
        modifier = modifier
    )
}

/**
 * Row of preset buttons
 */
@Composable
private fun PresetsRow(
    settingsViewModel: com.example.matrixscreen.ui.SettingsViewModel,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("Film-Accurate", "Neon", "Emerald", "Monochrome").forEach { preset ->
            PresetButton(
                name = preset,
                onClick = { settingsViewModel.applyPreset(preset) },
                ui = ui,
                optimizedSettings = optimizedSettings
            )
        }
    }
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
    settingsViewModel: com.example.matrixscreen.ui.SettingsViewModel,
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
            color = currentSettings.rainHeadColor,
            onColorClick = { onColorClick("rainHeadColor") },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
        // Bright Trail Color
        ColorControlRow(
            label = "Bright Trail",
            color = currentSettings.rainBrightTrailColor,
            onColorClick = { onColorClick("rainBrightTrailColor") },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
        // Trail Color
        ColorControlRow(
            label = "Trail",
            color = currentSettings.rainTrailColor,
            onColorClick = { onColorClick("rainTrailColor") },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
        // Dim Trail Color
        ColorControlRow(
            label = "Dim Trail",
            color = currentSettings.rainDimTrailColor,
            onColorClick = { onColorClick("rainDimTrailColor") },
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
        // UI Color
        ColorControlRow(
            label = "UI",
            color = currentSettings.uiColor,
            onColorClick = { onColorClick("uiColor") },
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
