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
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.settings.model.get
import com.example.matrixscreen.ui.settings.model.specFor
import com.example.matrixscreen.ui.settings.model.ColumnStartDelay
import com.example.matrixscreen.ui.settings.model.ColumnRestartDelay
import com.example.matrixscreen.ui.settings.model.TIMING_SPECS
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow

/**
 * Timing settings screen with spawn and respawn delay controls
 */
@Composable
fun TimingSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = uiState.draft
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    SettingsScreenContainer(
        title = "TIMING",
        onBack = onBack,
        ui = ui,
        optimizedSettings = optimizedSettings,
        expanded = isExpanded,
        content = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sectionSpacing)
        ) {
            // Description
            Text(
                text = "Control the timing and rhythm of the rain effect.",
                style = AppTypography.bodyMedium,
                color = ui.textSecondary
            )
            
            // Timing Controls Section
            SettingsSection(
                title = "Timing Controls",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.lg)
                ) {
                    // Spawn Delay
                    val spawnDelaySpec = TIMING_SPECS.specFor(ColumnStartDelay)
                    RenderSetting(
                        spec = spawnDelaySpec,
                        value = currentSettings.get(ColumnStartDelay),
                        onValueChange = { v: Float -> settingsViewModel.updateDraft(ColumnStartDelay, v) }
                    )
                    
                    // Respawn Delay
                    val respawnDelaySpec = TIMING_SPECS.specFor(ColumnRestartDelay)
                    RenderSetting(
                        spec = respawnDelaySpec,
                        value = currentSettings.get(ColumnRestartDelay),
                        onValueChange = { v: Float -> settingsViewModel.updateDraft(ColumnRestartDelay, v) }
                    )
                }
                }
            )
            
            // Reset Section
            SettingsSection(
                title = "Reset",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                ResetSectionButton(
                    onReset = {
                        settingsViewModel.revert()
                    },
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
                Text(
                    text = "Coming soon: Center-Out and other flow modes",
                    style = AppTypography.bodySmall,
                    color = ui.textSecondary
                )
                }
            )
        }
        },
        modifier = modifier
    )
}