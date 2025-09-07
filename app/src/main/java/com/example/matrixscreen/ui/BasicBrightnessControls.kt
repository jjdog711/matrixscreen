package com.example.matrixscreen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.MatrixUIColorScheme

/**
 * Basic brightness controls for simple mode
 */
@Composable
fun BasicBrightnessControls(
    currentSettings: MatrixSettings,
    onSettingsUpdate: (MatrixSettings) -> Unit,
    uiColors: MatrixUIColorScheme,
    hapticFeedback: androidx.compose.ui.hapticfeedback.HapticFeedback
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Brightness Preset Selection
        Text(
            text = "Brightness Preset",
            style = AppTypography.bodyMedium,
            color = uiColors.textPrimary,
            fontWeight = FontWeight.Medium
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(BrightnessPresets.allPresets.take(4)) { preset -> // Show only first 4 presets for basic mode
                FilterChip(
                    onClick = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        val updatedSettings = currentSettings.copy(
                            brightnessPreset = preset.name,
                            leadBrightnessMultiplier = preset.leadBrightness,
                            brightTrailBrightnessMultiplier = preset.brightTrailBrightness,
                            trailBrightnessMultiplier = preset.trailBrightness,
                            dimTrailBrightnessMultiplier = preset.dimTrailBrightness,
                            leadAlphaMultiplier = preset.leadAlpha,
                            brightTrailAlphaMultiplier = preset.brightTrailAlpha,
                            trailAlphaMultiplier = preset.trailAlpha,
                            dimTrailAlphaMultiplier = preset.dimTrailAlpha,
                            themeBrightnessProfile = preset.themeBrightnessProfile
                        )
                        onSettingsUpdate(updatedSettings)
                    },
                    label = {
                        Text(
                            text = preset.name,
                            style = AppTypography.labelMedium
                        )
                    },
                    selected = currentSettings.brightnessPreset == preset.name,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = uiColors.primary,
                        selectedLabelColor = uiColors.background,
                        containerColor = uiColors.backgroundSecondary,
                        labelColor = uiColors.textPrimary
                    )
                )
            }
        }
        
        // Overall Brightness Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall Brightness",
                    style = AppTypography.bodyMedium,
                    color = uiColors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${String.format("%.1f", currentSettings.leadBrightnessMultiplier)}x",
                    style = AppTypography.bodyMedium,
                    color = uiColors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = currentSettings.leadBrightnessMultiplier,
                onValueChange = { value ->
                    val updatedSettings = currentSettings.copy(
                        leadBrightnessMultiplier = value,
                        brightTrailBrightnessMultiplier = value * 0.8f,
                        trailBrightnessMultiplier = value * 0.6f,
                        dimTrailBrightnessMultiplier = value * 0.4f
                    )
                    onSettingsUpdate(updatedSettings)
                },
                valueRange = 0.1f..3.0f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = uiColors.primary,
                    activeTrackColor = uiColors.primary,
                    inactiveTrackColor = uiColors.textSecondary.copy(alpha = 0.3f)
                )
            )
        }
    }
}
