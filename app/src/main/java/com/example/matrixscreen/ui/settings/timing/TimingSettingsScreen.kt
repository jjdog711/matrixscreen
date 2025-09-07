package com.example.matrixscreen.ui.settings.timing

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
 * Timing settings screen with spawn and respawn delay controls
 */
@Composable
fun TimingSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.SettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSettings by settingsViewModel.settings.collectAsState()
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    SettingsScreenContainer(
        title = "TIMING",
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
                text = "Control the timing and rhythm of the rain effect.",
                style = AppTypography.bodyMedium,
                color = ui.textSecondary,
                settings = optimizedSettings
            )
            
            // Spawn Delay Section
            SettingsSection(
                title = "Spawn Delay",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val spawnDelaySpec = SettingsSpecs.TIMING_SPECS.find { it.key == "columnStartDelay" }!!
                LabeledSlider(
                    spec = spawnDelaySpec,
                    value = currentSettings.columnStartDelay,
                    onValueChange = { settingsViewModel.updateColumnStartDelay(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Respawn Delay Section
            SettingsSection(
                title = "Respawn Delay",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val respawnDelaySpec = SettingsSpecs.TIMING_SPECS.find { it.key == "columnRestartDelay" }!!
                LabeledSlider(
                    spec = respawnDelaySpec,
                    value = currentSettings.columnRestartDelay,
                    onValueChange = { settingsViewModel.updateColumnRestartDelay(it) },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                }
            )
            
            // Future: Flow Direction Section (placeholder)
            SettingsSection(
                title = "Flow Direction",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                ModernTextWithGlow(
                    text = "Coming soon: Center-Out and other flow modes",
                    style = AppTypography.bodySmall,
                    color = ui.textSecondary,
                    settings = optimizedSettings
                )
                }
            )
        }
        },
        modifier = modifier
    )
}