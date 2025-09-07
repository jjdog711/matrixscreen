package com.example.matrixscreen.ui.settings.motion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.SettingsSpecs
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow

/**
 * Motion settings screen with rain speed, columns, and flow controls
 */
@Composable
fun MotionSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSettings by settingsViewModel.settings.collectAsState()
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    SettingsScreenContainer(
        title = "MOTION",
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
            // Description
            ModernTextWithGlow(
                text = "Controls flow density and pacing.",
                style = AppTypography.bodyMedium,
                color = ui.textSecondary,
                settings = optimizedSettings
            )
            
            // Rain Speed Section
            SettingsSection(
                title = "Rain Speed",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val fallSpeedSpec = SettingsSpecs.MOTION_SPECS.find { it.key == "fallSpeed" }!!
                LabeledSlider(
                    spec = fallSpeedSpec,
                    value = currentSettings.fallSpeed,
                    onValueChange = { settingsViewModel.updateFallSpeed(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Columns Section
            SettingsSection(
                title = "Columns",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val columnCountSpec = SettingsSpecs.MOTION_SPECS.find { it.key == "columnCount" }!!
                LabeledSlider(
                    spec = columnCountSpec,
                    value = currentSettings.columnCount.toFloat(),
                    onValueChange = { settingsViewModel.updateColumnCount(it.toInt()) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Line Spacing Section
            SettingsSection(
                title = "Line Spacing",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val rowHeightSpec = SettingsSpecs.MOTION_SPECS.find { it.key == "rowHeightMultiplier" }!!
                LabeledSlider(
                    spec = rowHeightSpec,
                    value = currentSettings.rowHeightMultiplier,
                    onValueChange = { settingsViewModel.updateRowHeightMultiplier(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Active Columns Section
            SettingsSection(
                title = "Active Columns",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val activePercentageSpec = SettingsSpecs.MOTION_SPECS.find { it.key == "initialActivePercentage" }!!
                LabeledSlider(
                    spec = activePercentageSpec,
                    value = currentSettings.initialActivePercentage,
                    onValueChange = { settingsViewModel.updateInitialActivePercentage(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Speed Variance Section
            SettingsSection(
                title = "Speed Variance",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val speedVariationSpec = SettingsSpecs.MOTION_SPECS.find { it.key == "speedVariationRate" }!!
                LabeledSlider(
                    spec = speedVariationSpec,
                    value = currentSettings.speedVariationRate,
                    onValueChange = { settingsViewModel.updateSpeedVariationRate(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
        }
        },
        modifier = modifier
    )
}