package com.example.matrixscreen.ui.settings.motion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.settings.model.get
import com.example.matrixscreen.ui.settings.model.specFor
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow

/**
 * Motion settings screen with spec-driven UI for rain speed, columns, and flow controls.
 * 
 * This screen uses the MOTION_SPECS from SpecsCatalog to render settings dynamically,
 * following the spec-driven UI pattern with proper UDF (draft/confirm/cancel) integration.
 */
@Composable
fun MotionSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = uiState.draft
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
                
                // Live Preview Section
                SettingsSection {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ModernTextWithGlow(
                            text = "Live Preview",
                            style = AppTypography.titleSmall,
                            color = ui.textPrimary,
                            settings = optimizedSettings
                        )
                        
                        MotionPreviewTile(
                            settings = currentSettings,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // Motion Settings Section
                SettingsSection {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Render all motion specs using typed SettingId access
                        val speedSpec = MOTION_SPECS.specFor(Speed)
                        AnimatedRenderSetting(
                            spec = speedSpec,
                            value = currentSettings.get(Speed),
                            onValueChange = { value -> settingsViewModel.updateDraft(Speed, value) },
                            showPreview = true
                        )

                        val columnsSpec = MOTION_SPECS.specFor(Columns)
                        AnimatedRenderSetting(
                            spec = columnsSpec,
                            value = currentSettings.get(Columns),
                            onValueChange = { value -> settingsViewModel.updateDraft(Columns, value) },
                            showPreview = true
                        )

                        val lineSpaceSpec = MOTION_SPECS.specFor(LineSpace)
                        AnimatedRenderSetting(
                            spec = lineSpaceSpec,
                            value = currentSettings.get(LineSpace),
                            onValueChange = { value -> settingsViewModel.updateDraft(LineSpace, value) },
                            showPreview = true
                        )

                        val activePctSpec = MOTION_SPECS.specFor(ActivePct)
                        AnimatedRenderSetting(
                            spec = activePctSpec,
                            value = currentSettings.get(ActivePct),
                            onValueChange = { value -> settingsViewModel.updateDraft(ActivePct, value) },
                            showPreview = true
                        )

                        val speedVarSpec = MOTION_SPECS.specFor(SpeedVar)
                        AnimatedRenderSetting(
                            spec = speedVarSpec,
                            value = currentSettings.get(SpeedVar),
                            onValueChange = { value -> settingsViewModel.updateDraft(SpeedVar, value) },
                            showPreview = true
                        )
                        
                        // Reset button for motion settings with animation
                        AnimatedResetSectionButton(
                            onReset = {
                                // Reset motion settings to defaults
                                MOTION_SPECS.forEach { spec ->
                                    when (spec) {
                                        is SliderSpec -> {
                                            settingsViewModel.updateDraft(spec.id, spec.default)
                                        }
                                        is IntSliderSpec -> {
                                            settingsViewModel.updateDraft(spec.id, spec.default)
                                        }
                                        is ToggleSpec -> {
                                            settingsViewModel.updateDraft(spec.id, spec.default)
                                        }
                                        else -> {
                                            // Handle other spec types if needed
                                            throw IllegalArgumentException("Unsupported spec type for reset: ${spec::class.simpleName}")
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        modifier = modifier
    )
}