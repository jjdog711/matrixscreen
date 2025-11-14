package com.example.matrixscreen.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.derivedStateOf
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.model.*
import com.example.matrixscreen.ui.settings.model.SettingCategory
import com.example.matrixscreen.ui.settings.model.SettingId
import com.example.matrixscreen.ui.settings.model.SettingPreset
import com.example.matrixscreen.ui.settings.model.SettingPresets
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow
import com.example.matrixscreen.ui.NewSettingsViewModel
import com.example.matrixscreen.ui.settings.components.SettingsSection
import com.example.matrixscreen.ui.settings.components.SettingsScreenContainer


/**
 * Home screen for the new settings architecture
 * Displays 6 category cards in a grid layout with presets at the top
 */
@Composable
fun SettingsHomeScreen(
    settingsViewModel: NewSettingsViewModel,
    onNavigateToTheme: () -> Unit,
    onNavigateToCharacters: () -> Unit,
    onNavigateToMotion: () -> Unit,
    onNavigateToEffects: () -> Unit,
    onNavigateToTiming: () -> Unit,
    onNavigateToBackground: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Get current settings for UI theming
    val uiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val currentSettings by remember {
        derivedStateOf { uiState.draft }
    }
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    val presets = remember { SettingPresets.ALL_PRESETS }

    val onPresetSelected: (SettingPreset) -> Unit = { preset ->
        applySettingPreset(settingsViewModel, preset)
    }
    
    SettingsScreenContainer(
        title = "SETTINGS",
        onBack = onBack,
        ui = ui,
        optimizedSettings = optimizedSettings,
        expanded = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sectionSpacing)
        ) {
            SettingsSection(
                title = "Presets",
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                PresetsSection(
                    presets = presets,
                    ui = ui,
                    optimizedSettings = optimizedSettings,
                    onPresetSelected = onPresetSelected
                )
            }

            SettingsSection(
                title = "Categories",
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.md)
                ) {
                    val categories = SettingCategory.values().toList()
                    val itemsPerRow = 2

                    categories.chunked(itemsPerRow).forEach { rowCategories: List<SettingCategory> ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.md)
                        ) {
                            rowCategories.forEach { category: SettingCategory ->
                                CategoryCard(
                                    category = category,
                                    onClick = {
                                        when (category) {
                                            SettingCategory.THEME -> onNavigateToTheme()
                                            SettingCategory.CHARACTERS -> onNavigateToCharacters()
                                            SettingCategory.MOTION -> onNavigateToMotion()
                                            SettingCategory.EFFECTS -> onNavigateToEffects()
                                            SettingCategory.TIMING -> onNavigateToTiming()
                                            SettingCategory.BACKGROUND -> onNavigateToBackground()
                                        }
                                    },
                                    ui = ui,
                                    optimizedSettings = optimizedSettings,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(itemsPerRow - rowCategories.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Presets section with quick configuration buttons
 */
@Composable
private fun PresetsSection(
    presets: List<SettingPreset>,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    onPresetSelected: (SettingPreset) -> Unit,
    feedbackMessage: String? = null,
    feedbackIsError: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            presets.forEach { preset ->
                PresetButton(
                    name = preset.name,
                    onClick = { onPresetSelected(preset) },
                    ui = ui
                )
            }
        }

        feedbackMessage?.let { message ->
            Text(
                text = message,
                style = AppTypography.bodySmall,
                color = if (feedbackIsError) ui.buttonCancelText else ui.textSecondary
            )
        }
    }
}

/**
 * Individual preset button
 */
@Composable
private fun PresetButton(
    name: String,
    onClick: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme
) {
    Card(
        modifier = Modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = ui.selectionBackground,
            contentColor = ui.textPrimary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = name,
            style = AppTypography.labelSmall,
            color = ui.textPrimary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

private fun applySettingPreset(
    settingsViewModel: NewSettingsViewModel,
    preset: SettingPreset
) {
    preset.updates.forEach { (id, value) ->
        applyPresetValue(settingsViewModel, id, value)
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T> applyPresetValue(
    settingsViewModel: NewSettingsViewModel,
    id: SettingId<T>,
    value: Any
) {
    settingsViewModel.updateDraft(id, value as T)
}

/**
 * Category card for each settings section
 */
@Composable
private fun CategoryCard(
    category: SettingCategory,
    onClick: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(com.example.matrixscreen.core.design.DesignTokens.Radius.previewCard)
    val glowIntensity = optimizedSettings.glowIntensity.coerceIn(0f, 2f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .let { base ->
                if (glowIntensity > 0.1f) {
                    val innerGlowElevation = (glowIntensity * 1.5f + 1f).dp
                    val outerGlowElevation = (glowIntensity * 4f + 2f).dp
                    val innerGlowAlpha = (glowIntensity * 0.2f + 0.3f).coerceIn(0.3f, 0.5f)
                    val outerGlowAlpha = (glowIntensity * 0.075f + 0.1f).coerceIn(0.1f, 0.25f)

                    base
                        .shadow(
                            elevation = outerGlowElevation,
                            shape = cardShape,
                            ambientColor = ui.textAccent.copy(alpha = outerGlowAlpha),
                            spotColor = ui.textAccent.copy(alpha = outerGlowAlpha * 0.8f)
                        )
                        .shadow(
                            elevation = innerGlowElevation,
                            shape = cardShape,
                            ambientColor = ui.textAccent.copy(alpha = innerGlowAlpha),
                            spotColor = ui.textAccent.copy(alpha = innerGlowAlpha * 0.9f)
                        )
                        .border(1.dp, ui.textAccent, cardShape)
                        .clickable { onClick() }
                } else {
                    base.clickable { onClick() }
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = ui.overlayBackground,
            contentColor = ui.textPrimary
        ),
        shape = cardShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = com.example.matrixscreen.core.design.DesignTokens.Elevation.previewCard
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title only - clean and minimal
            ModernTextWithGlow(
                text = category.displayName,
                style = AppTypography.titleMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
        }
    }
}

