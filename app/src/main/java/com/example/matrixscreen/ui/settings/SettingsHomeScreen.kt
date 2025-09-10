package com.example.matrixscreen.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.model.*
import com.example.matrixscreen.ui.settings.model.SettingCategory
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow
import com.example.matrixscreen.ui.NewSettingsViewModel
import com.example.matrixscreen.ui.settings.components.SettingsSection
import com.example.matrixscreen.ui.settings.components.LabeledSwitch
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
    onNavigateToUIPreview: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Get current settings for UI theming
    val uiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = uiState.draft
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    SettingsScreenContainer(
        title = "SETTINGS",
        onBack = onBack,
        ui = ui,
        optimizedSettings = optimizedSettings,
        expanded = true, // Always expanded in overlay context
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sectionSpacing)
            ) {
                // Presets Section (wrapped for preview-style card)
                SettingsSection(
                    title = "Presets",
                    ui = ui,
                    optimizedSettings = optimizedSettings
                ) {
                    PresetsSection(
                        settingsViewModel = settingsViewModel,
                        ui = ui,
                        optimizedSettings = optimizedSettings
                    )
                }
                
                // Categories Grid (wrapped for preview-style card)
                SettingsSection(
                    title = "Categories",
                    ui = ui,
                    optimizedSettings = optimizedSettings
                ) {
                    Column(
                        modifier = Modifier.height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.md)
                    ) {
                        // Create rows of category cards
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
                                // Fill remaining space if row is not full
                                repeat(itemsPerRow - rowCategories.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                
                // Developer Tools Section (wrapped for preview-style card)
                SettingsSection(
                    title = "Developer Tools",
                    ui = ui,
                    optimizedSettings = optimizedSettings
                ) {
                    DeveloperToolsSection(
                        settingsViewModel = settingsViewModel,
                        onNavigateToUIPreview = onNavigateToUIPreview,
                        ui = ui,
                        optimizedSettings = optimizedSettings
                    )
                }
            }
        }
    )
}

/**
 * Presets section with quick configuration buttons
 */
@Composable
private fun PresetsSection(
    settingsViewModel: NewSettingsViewModel,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ModernTextWithGlow(
            text = "PRESETS",
            style = AppTypography.labelMedium,
            color = ui.textSecondary,
            settings = optimizedSettings
        )
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Film-Accurate", "Performance", "Showcase").forEach { preset ->
                PresetButton(
                    name = preset,
                    onClick = { 
                        // TODO: Implement preset functionality in NewSettingsViewModel
                        // settingsViewModel.applyPreset(preset) 
                    },
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
            }
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
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
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
        ModernTextWithGlow(
            text = name,
            style = AppTypography.labelSmall,
            color = ui.textPrimary,
            settings = optimizedSettings,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
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

/**
 * Developer Tools section with UI Preview access and developer settings
 */
@Composable
private fun DeveloperToolsSection(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onNavigateToUIPreview: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sm)
    ) {
        // UI Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToUIPreview() },
            colors = CardDefaults.cardColors(
                containerColor = ui.selectionBackground,
                contentColor = ui.textPrimary
            ),
            shape = RoundedCornerShape(com.example.matrixscreen.core.design.DesignTokens.Radius.card)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(com.example.matrixscreen.core.design.DesignTokens.Spacing.md),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UI Style Preview",
                    style = AppTypography.bodyMedium,
                    color = ui.textPrimary
                )
            }
        }
        
        // Developer Settings
        DeveloperSettingsCard(
            settingsViewModel = settingsViewModel,
            ui = ui,
            optimizedSettings = optimizedSettings
        )
    }
}

/**
 * Developer settings card with toggle for always showing hints
 */
@Composable
private fun DeveloperSettingsCard(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    val currentSettings by settingsViewModel.uiState.collectAsState()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ui.selectionBackground,
            contentColor = ui.textPrimary
        ),
        shape = RoundedCornerShape(com.example.matrixscreen.core.design.DesignTokens.Radius.card)
    ) {
        Column(
            modifier = Modifier.padding(com.example.matrixscreen.core.design.DesignTokens.Spacing.md),
            verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sm)
        ) {
            ModernTextWithGlow(
                text = "Developer Settings",
                style = AppTypography.titleMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
            
            // Always Show Hints Toggle
            val alwaysShowHintsSpec = com.example.matrixscreen.ui.settings.model.DEVELOPER_SPECS.first()
            LabeledSwitch(
                label = alwaysShowHintsSpec.label,
                checked = currentSettings.draft.alwaysShowHints,
                onCheckedChange = { settingsViewModel.updateDraft(com.example.matrixscreen.ui.settings.model.AlwaysShowHints, it) },
                help = alwaysShowHintsSpec.help
            )
        }
    }
}
