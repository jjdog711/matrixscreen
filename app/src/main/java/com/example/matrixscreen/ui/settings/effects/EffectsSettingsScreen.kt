package com.example.matrixscreen.ui.settings.effects

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.settings.model.get
import com.example.matrixscreen.ui.settings.model.specFor
import com.example.matrixscreen.ui.settings.model.Glow
import com.example.matrixscreen.ui.settings.model.Jitter
import com.example.matrixscreen.ui.settings.model.Flicker
import com.example.matrixscreen.ui.settings.model.Mutation
import com.example.matrixscreen.ui.settings.model.MaxTrailLength
import com.example.matrixscreen.ui.settings.model.MaxBrightTrailLength
import com.example.matrixscreen.ui.settings.model.EFFECTS_SPECS
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.preview.rememberPreviewSettingsViewModel
import kotlin.math.min

/**
 * Effects settings screen with spec-driven UI for glow, jitter, flicker, and mutation controls.
 * 
 * This screen uses the EFFECTS_SPECS from SpecsCatalog to render settings dynamically,
 * following the spec-driven UI pattern with proper UDF (draft/confirm/cancel) integration.
 */
@Composable
fun EffectsSettingsScreen(
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
    val glowSpec = remember { EFFECTS_SPECS.specFor(Glow) }
    val jitterSpec = remember { EFFECTS_SPECS.specFor(Jitter) }
    val flickerSpec = remember { EFFECTS_SPECS.specFor(Flicker) }
    val mutationSpec = remember { EFFECTS_SPECS.specFor(Mutation) }
    val maxTrailSpec = remember { EFFECTS_SPECS.specFor(MaxTrailLength) }
    val maxBrightTrailSpec = remember { EFFECTS_SPECS.specFor(MaxBrightTrailLength) }
    val dynamicBrightTrailSpec = remember(currentSettings.maxTrailLength) {
        val minRange = maxBrightTrailSpec.range.first
        val cappedUpper = min(
            maxBrightTrailSpec.range.last,
            currentSettings.maxTrailLength.coerceAtLeast(minRange)
        )
        maxBrightTrailSpec.copy(range = minRange..cappedUpper)
    }
    
    SettingsScreenContainer(
        title = null,
        onBack = onBack,
        ui = ui,
        optimizedSettings = optimizedSettings,
        expanded = true,
        content = {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sectionSpacing)
        ) {
                // Description
                Text(
                    text = "Visual effects and animations for the matrix rain.",
                    style = AppTypography.bodyMedium,
                    color = ui.textSecondary
                )
                
                // Effects Settings Section (per-screen preview tile removed; main matrix rain is the preview)
                SettingsSection(
                    ui = ui,
                    optimizedSettings = optimizedSettings
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Use the simpler base RenderSetting components here for robustness.
                        RenderSetting(
                            spec = glowSpec,
                            value = currentSettings.get(Glow),
                            onValueChange = { value -> settingsViewModel.updateDraft(Glow, value) }
                        )

                        RenderSetting(
                            spec = jitterSpec,
                            value = currentSettings.get(Jitter),
                            onValueChange = { value -> settingsViewModel.updateDraft(Jitter, value) }
                        )

                        RenderSetting(
                            spec = flickerSpec,
                            value = currentSettings.get(Flicker),
                            onValueChange = { value -> settingsViewModel.updateDraft(Flicker, value) }
                        )

                        RenderSetting(
                            spec = mutationSpec,
                            value = currentSettings.get(Mutation),
                            onValueChange = { value -> settingsViewModel.updateDraft(Mutation, value) }
                        )

                        RenderSetting(
                            spec = maxTrailSpec,
                            value = currentSettings.get(MaxTrailLength),
                            onValueChange = { value -> settingsViewModel.updateDraft(MaxTrailLength, value) }
                        )

                        RenderSetting(
                            spec = dynamicBrightTrailSpec,
                            value = currentSettings.get(MaxBrightTrailLength),
                            onValueChange = { value -> settingsViewModel.updateDraft(MaxBrightTrailLength, value) }
                        )
                        
                        // Reset button for effects settings with animation
                        AnimatedResetSectionButton(
                            onReset = {
                                // Reset effects settings to defaults
                                EFFECTS_SPECS.forEach { spec ->
                                    when (spec) {
                                        is SliderSpec -> settingsViewModel.updateDraft(spec.id, spec.default)
                                        is IntSliderSpec -> settingsViewModel.updateDraft(spec.id, spec.default)
                                        is ToggleSpec -> settingsViewModel.updateDraft(spec.id, spec.default)
                                        else -> Unit
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

@Preview(showBackground = true)
@Composable
private fun EffectsSettingsScreenPreview() {
    val previewViewModel = rememberPreviewSettingsViewModel()
    EffectsSettingsScreen(
        settingsViewModel = previewViewModel,
        onBack = {}
    )
}