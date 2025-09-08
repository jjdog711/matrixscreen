package com.example.matrixscreen.ui.settings.effects

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
 * Effects settings screen with spec-driven UI for glow, jitter, flicker, and mutation controls.
 * 
 * This screen uses the EFFECTS_SPECS from SpecsCatalog to render settings dynamically,
 * following the spec-driven UI pattern with proper UDF (draft/confirm/cancel) integration.
 */
@Composable
fun EffectsSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = uiState.draft
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
                // Description
                ModernTextWithGlow(
                    text = "Visual effects and animations for the matrix rain.",
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
                        
                        EffectsPreviewTile(
                            settings = currentSettings,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                // Effects Settings Section
                SettingsSection {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Render all effects specs using typed SettingId access
                        val glowSpec = EFFECTS_SPECS.specFor(Glow)
                        AnimatedRenderSetting(
                            spec = glowSpec,
                            value = currentSettings.get(Glow),
                            onValueChange = { value -> settingsViewModel.updateDraft(Glow, value) },
                            showPreview = true
                        )

                        val jitterSpec = EFFECTS_SPECS.specFor(Jitter)
                        AnimatedRenderSetting(
                            spec = jitterSpec,
                            value = currentSettings.get(Jitter),
                            onValueChange = { value -> settingsViewModel.updateDraft(Jitter, value) },
                            showPreview = true
                        )

                        val flickerSpec = EFFECTS_SPECS.specFor(Flicker)
                        AnimatedRenderSetting(
                            spec = flickerSpec,
                            value = currentSettings.get(Flicker),
                            onValueChange = { value -> settingsViewModel.updateDraft(Flicker, value) },
                            showPreview = true
                        )

                        val mutationSpec = EFFECTS_SPECS.specFor(Mutation)
                        AnimatedRenderSetting(
                            spec = mutationSpec,
                            value = currentSettings.get(Mutation),
                            onValueChange = { value -> settingsViewModel.updateDraft(Mutation, value) },
                            showPreview = true
                        )
                        
                        // Reset button for effects settings with animation
                        AnimatedResetSectionButton(
                            onReset = {
                                // Reset effects settings to defaults
                                EFFECTS_SPECS.forEach { spec ->
                                    settingsViewModel.updateDraft(spec.id, spec.default)
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