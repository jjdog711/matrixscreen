package com.example.matrixscreen.ui.settings.motion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.settings.model.get
import com.example.matrixscreen.ui.settings.model.specFor
import com.example.matrixscreen.ui.settings.model.Speed
import com.example.matrixscreen.ui.settings.model.Columns
import com.example.matrixscreen.ui.settings.model.LineSpace
import com.example.matrixscreen.ui.settings.model.ActivePct
import com.example.matrixscreen.ui.settings.model.SpeedVar
import com.example.matrixscreen.ui.settings.model.AllowLandscape
import com.example.matrixscreen.ui.settings.model.MOTION_SPECS
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.core.design.DesignTokens

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
    modifier: Modifier = Modifier,
) {
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val currentSettings by remember {
        derivedStateOf { uiState.draft }
    }
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    // Memoize spec lookups at composable level (best practice - avoids recomposition issues)
    val allowLandscapeSpec = remember { MOTION_SPECS.specFor(AllowLandscape) }
    val flowDirectionSpec = remember { MOTION_SPECS.specFor(FlowDirectionSetting) }
    val speedSpec = remember { MOTION_SPECS.specFor(Speed) }
    val columnsSpec = remember { MOTION_SPECS.specFor(Columns) }
    val lineSpaceSpec = remember { MOTION_SPECS.specFor(LineSpace) }
    val activePctSpec = remember { MOTION_SPECS.specFor(ActivePct) }
    val speedVarSpec = remember { MOTION_SPECS.specFor(SpeedVar) }
    
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
            Text(
                text = "Controls flow density and pacing.",
                style = AppTypography.bodyMedium,
                color = ui.textSecondary
            )

            SettingsSection(
                title = "Motion Controls",
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    RenderSetting(
                        spec = allowLandscapeSpec,
                        value = currentSettings.get(AllowLandscape),
                        onValueChange = { value -> settingsViewModel.updateDraft(AllowLandscape, value) }
                    )

                    RenderSetting(
                        spec = flowDirectionSpec,
                        value = currentSettings.get(FlowDirectionSetting),
                        onValueChange = { value -> settingsViewModel.updateDraft(FlowDirectionSetting, value) }
                    )

                    RenderSetting(
                        spec = speedSpec,
                        value = currentSettings.get(Speed),
                        onValueChange = { value -> settingsViewModel.updateDraft(Speed, value) }
                    )

                    RenderSetting(
                        spec = columnsSpec,
                        value = currentSettings.get(Columns),
                        onValueChange = { value -> settingsViewModel.updateDraft(Columns, value) }
                    )

                    RenderSetting(
                        spec = lineSpaceSpec,
                        value = currentSettings.get(LineSpace),
                        onValueChange = { value -> settingsViewModel.updateDraft(LineSpace, value) }
                    )

                    RenderSetting(
                        spec = activePctSpec,
                        value = currentSettings.get(ActivePct),
                        onValueChange = { value -> settingsViewModel.updateDraft(ActivePct, value) }
                    )

                    RenderSetting(
                        spec = speedVarSpec,
                        value = currentSettings.get(SpeedVar),
                        onValueChange = { value -> settingsViewModel.updateDraft(SpeedVar, value) }
                    )

                    AnimatedResetSectionButton(
                        onReset = {
                            MOTION_SPECS.forEach { spec ->
                                when (spec) {
                                    is SliderSpec -> settingsViewModel.updateDraft(spec.id, spec.default)
                                    is IntSliderSpec -> settingsViewModel.updateDraft(spec.id, spec.default)
                                    is ToggleSpec -> settingsViewModel.updateDraft(spec.id, spec.default)
                                    is SelectSpec<*> -> {
                                        @Suppress("UNCHECKED_CAST")
                                        val id = spec.id as SettingId<Any>
                                        @Suppress("UNCHECKED_CAST")
                                        val defaultValue = spec.default as Any
                                        settingsViewModel.updateDraft(id, defaultValue)
                                    }
                                    else -> throw IllegalArgumentException("Unsupported spec type for reset: ${spec::class.simpleName}")
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}