package com.example.matrixscreen.ui.settings.background

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.SettingsSpecs
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow

/**
 * Background settings screen with film grain and performance controls
 */
@Composable
fun BackgroundSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSettings by settingsViewModel.settings.collectAsState()
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    SettingsScreenContainer(
        title = "BACKGROUND",
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
            // Film Grain Section
            SettingsSection(
                title = "Film Grain",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val grainDensitySpec = SettingsSpecs.BACKGROUND_SPECS.find { it.key == "grainDensity" }!!
                LabeledSlider(
                    spec = grainDensitySpec,
                    value = currentSettings.grainDensity.toFloat(),
                    onValueChange = { settingsViewModel.updateGrainDensity(it.toInt()) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                
                val grainOpacitySpec = SettingsSpecs.BACKGROUND_SPECS.find { it.key == "grainOpacity" }!!
                LabeledSlider(
                    spec = grainOpacitySpec,
                    value = currentSettings.grainOpacity,
                    onValueChange = { settingsViewModel.updateGrainOpacity(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Performance Section
            SettingsSection(
                title = "Performance",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val targetFpsSpec = SettingsSpecs.BACKGROUND_SPECS.find { it.key == "targetFps" }!!
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModernTextWithGlow(
                        text = targetFpsSpec.label,
                        style = AppTypography.titleMedium,
                        color = ui.textPrimary,
                        settings = optimizedSettings
                    )
                    
                    ModernTextWithGlow(
                        text = "${currentSettings.targetFps.toInt()} FPS",
                        style = AppTypography.bodyMedium,
                        color = ui.textSecondary,
                        settings = optimizedSettings
                    )
                }
                
                // Frame rate buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(15f, 30f, 45f, 60f).forEach { fps ->
                        FrameRateButton(
                            fps = fps,
                            isSelected = currentSettings.targetFps == fps,
                            onClick = { settingsViewModel.updateTargetFps(fps) },
                            ui = ui,
                            optimizedSettings = optimizedSettings
                        )
                    }
                }
                }
            )
        }
        },
        modifier = modifier
    )
}

/**
 * Frame rate selection button
 */
@Composable
private fun FrameRateButton(
    fps: Float,
    isSelected: Boolean,
    onClick: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Card(
        modifier = Modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ui.primary else ui.selectionBackground,
            contentColor = if (isSelected) ui.textPrimary else ui.textSecondary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        ModernTextWithGlow(
            text = "${fps.toInt()}",
            style = AppTypography.labelMedium,
            color = if (isSelected) ui.textPrimary else ui.textSecondary,
            settings = optimizedSettings,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}