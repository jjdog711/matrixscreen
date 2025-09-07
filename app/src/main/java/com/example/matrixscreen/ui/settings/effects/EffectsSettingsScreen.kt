package com.example.matrixscreen.ui.settings.effects

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
 * Effects settings screen with glow, jitter, flicker, and mutation controls
 */
@Composable
fun EffectsSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSettings by settingsViewModel.settings.collectAsState()
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    SettingsScreenContainer(
        title = "EFFECTS",
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
            // Glow Section
            SettingsSection(
                title = "Glow",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val glowSpec = SettingsSpecs.EFFECTS_SPECS.find { it.key == "glowIntensity" }!!
                LabeledSlider(
                    spec = glowSpec,
                    value = currentSettings.glowIntensity,
                    onValueChange = { settingsViewModel.updateGlowIntensity(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Jitter Section
            SettingsSection(
                title = "Jitter",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val jitterSpec = SettingsSpecs.EFFECTS_SPECS.find { it.key == "jitterAmount" }!!
                LabeledSlider(
                    spec = jitterSpec,
                    value = currentSettings.jitterAmount,
                    onValueChange = { settingsViewModel.updateJitterAmount(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Flicker Section
            SettingsSection(
                title = "Flicker",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val flickerSpec = SettingsSpecs.EFFECTS_SPECS.find { it.key == "flickerRate" }!!
                LabeledSlider(
                    spec = flickerSpec,
                    value = currentSettings.flickerRate,
                    onValueChange = { settingsViewModel.updateFlickerRate(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Character Mutation Section
            SettingsSection(
                title = "Character Mutation",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val mutationSpec = SettingsSpecs.EFFECTS_SPECS.find { it.key == "mutationRate" }!!
                LabeledSlider(
                    spec = mutationSpec,
                    value = currentSettings.mutationRate,
                    onValueChange = { settingsViewModel.updateMutationRate(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Trail Length Section
            SettingsSection(
                title = "Trail Length",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val maxTrailSpec = SettingsSpecs.EFFECTS_SPECS.find { it.key == "maxTrailLength" }!!
                LabeledSlider(
                    spec = maxTrailSpec,
                    value = currentSettings.maxTrailLength.toFloat(),
                    onValueChange = { settingsViewModel.updateMaxTrailLength(it.toInt()) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                
                val maxBrightTrailSpec = SettingsSpecs.EFFECTS_SPECS.find { it.key == "maxBrightTrailLength" }!!
                LabeledSlider(
                    spec = maxBrightTrailSpec,
                    value = currentSettings.maxBrightTrailLength.toFloat(),
                    onValueChange = { settingsViewModel.updateMaxBrightTrailLength(it.toInt()) },
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