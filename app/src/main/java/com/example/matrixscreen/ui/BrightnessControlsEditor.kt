package com.example.matrixscreen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.ui.theme.MatrixUIColorScheme
import com.example.matrixscreen.ui.theme.AppTypography

@Composable
fun BrightnessControlsEditor(
    currentSettings: MatrixSettings,
    onUpdateLivePreview: (MatrixSettingType, Any) -> Unit,
    onUpdateBrightnessMultiplier: (Int, Float) -> Unit,
    onUpdateAlphaMultiplier: (Int, Float) -> Unit,
    onApplyPreset: (String) -> Unit,
    ui: MatrixUIColorScheme
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Enable/Disable Toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ui.backgroundSecondary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Enable Advanced Brightness Controls",
                        style = AppTypography.titleMedium,
                        color = ui.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Fine-tune brightness levels for each Matrix rain character type",
                        style = AppTypography.bodySmall,
                        color = ui.textSecondary
                    )
                }
                Switch(
                    checked = currentSettings.brightnessControlsEnabled,
                    onCheckedChange = { onUpdateLivePreview(MatrixSettingType.BrightnessControls, it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ui.primary,
                        checkedTrackColor = ui.primary.copy(alpha = 0.5f),
                        uncheckedThumbColor = ui.textSecondary,
                        uncheckedTrackColor = ui.textSecondary.copy(alpha = 0.3f)
                    )
                )
            }
        }
        
        if (currentSettings.brightnessControlsEnabled) {
            // Preset Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ui.backgroundSecondary)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Brightness Preset",
                        style = AppTypography.titleMedium,
                        color = ui.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choose a preset or customize individual levels below",
                        style = AppTypography.bodySmall,
                        color = ui.textSecondary
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(getAvailablePresets(currentSettings)) { preset ->
                            FilterChip(
                                onClick = { onApplyPreset(preset.name) },
                                label = { 
                                    Text(
                                        text = preset.name,
                                        style = AppTypography.bodyMedium,
                                        color = if (currentSettings.brightnessPreset == preset.name) 
                                            ui.primary else ui.textPrimary
                                    )
                                },
                                selected = currentSettings.brightnessPreset == preset.name,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ui.primary.copy(alpha = 0.2f),
                                    selectedLabelColor = ui.primary,
                                    containerColor = ui.backgroundSecondary,
                                    labelColor = ui.textPrimary
                                )
                            )
                        }
                    }
                }
            }
            
            // Individual Brightness Controls
            BrightnessLevelControl(
                title = "Lead Characters (Brightest)",
                description = "The head character that leads each column",
                brightnessMultiplier = currentSettings.leadBrightnessMultiplier,
                alphaMultiplier = currentSettings.leadAlphaMultiplier,
                onBrightnessChange = { onUpdateBrightnessMultiplier(4, it) },
                onAlphaChange = { onUpdateAlphaMultiplier(4, it) },
                ui = ui
            )
            
            BrightnessLevelControl(
                title = "Bright Trail Characters",
                description = "Characters immediately following the lead",
                brightnessMultiplier = currentSettings.brightTrailBrightnessMultiplier,
                alphaMultiplier = currentSettings.brightTrailAlphaMultiplier,
                onBrightnessChange = { onUpdateBrightnessMultiplier(3, it) },
                onAlphaChange = { onUpdateAlphaMultiplier(3, it) },
                ui = ui
            )
            
            BrightnessLevelControl(
                title = "Regular Trail Characters",
                description = "Standard trail characters",
                brightnessMultiplier = currentSettings.trailBrightnessMultiplier,
                alphaMultiplier = currentSettings.trailAlphaMultiplier,
                onBrightnessChange = { onUpdateBrightnessMultiplier(2, it) },
                onAlphaChange = { onUpdateAlphaMultiplier(2, it) },
                ui = ui
            )
            
            BrightnessLevelControl(
                title = "Dim Trail Characters",
                description = "Fading characters at the end of trails",
                brightnessMultiplier = currentSettings.dimTrailBrightnessMultiplier,
                alphaMultiplier = currentSettings.dimTrailAlphaMultiplier,
                onBrightnessChange = { onUpdateBrightnessMultiplier(1, it) },
                onAlphaChange = { onUpdateAlphaMultiplier(1, it) },
                ui = ui
            )
            
            // Theme Integration Info
            if (currentSettings.advancedColorsEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ui.primary.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Theme Integration",
                            style = AppTypography.titleSmall,
                            color = ui.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Brightness controls work with your selected theme colors. " +
                                    "Adjustments are applied to the theme's color palette.",
                            style = AppTypography.bodySmall,
                            color = ui.textSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BrightnessLevelControl(
    title: String,
    description: String,
    brightnessMultiplier: Float,
    alphaMultiplier: Float,
    onBrightnessChange: (Float) -> Unit,
    onAlphaChange: (Float) -> Unit,
    ui: MatrixUIColorScheme
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ui.backgroundSecondary)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = AppTypography.titleMedium,
                    color = ui.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = AppTypography.bodySmall,
                    color = ui.textSecondary
                )
            }
            
            // Brightness Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Brightness",
                        style = AppTypography.bodyMedium,
                        color = ui.textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${String.format("%.1f", brightnessMultiplier)}x",
                        style = AppTypography.bodyMedium,
                        color = ui.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = brightnessMultiplier,
                    onValueChange = onBrightnessChange,
                    valueRange = 0.1f..3.0f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = ui.primary,
                        activeTrackColor = ui.primary,
                        inactiveTrackColor = ui.textSecondary.copy(alpha = 0.3f)
                    )
                )
            }
            
            // Alpha Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Alpha",
                        style = AppTypography.bodyMedium,
                        color = ui.textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${String.format("%.1f", alphaMultiplier)}x",
                        style = AppTypography.bodyMedium,
                        color = ui.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = alphaMultiplier,
                    onValueChange = onAlphaChange,
                    valueRange = 0.1f..2.0f,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = ui.primary,
                        activeTrackColor = ui.primary,
                        inactiveTrackColor = ui.textSecondary.copy(alpha = 0.3f)
                    )
                )
            }
        }
    }
}

/**
 * Get available presets based on current settings
 */
private fun getAvailablePresets(settings: MatrixSettings): List<BrightnessPreset> {
    return if (settings.advancedColorsEnabled) {
        // Show all presets including theme-specific ones
        BrightnessPresets.allPresets
    } else {
        // Show only core presets for basic color tint mode
        BrightnessPresets.corePresets
    }
}
