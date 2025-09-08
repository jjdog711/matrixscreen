package com.example.matrixscreen.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {
    // Get current settings for UI theming
    val uiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = uiState.draft
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .let { base ->
                    if (isExpanded) {
                        base
                            .padding(horizontal = com.example.matrixscreen.core.design.DesignTokens.Spacing.md, vertical = com.example.matrixscreen.core.design.DesignTokens.Spacing.md)
                            .fillMaxSize()
                    } else {
                        base
                            .widthIn(max = 400.dp)
                            .padding(horizontal = com.example.matrixscreen.core.design.DesignTokens.Spacing.lg, vertical = com.example.matrixscreen.core.design.DesignTokens.Spacing.md)
                            .wrapContentHeight()
                    }
                }
                .navigationBarsPadding(),
            colors = CardDefaults.cardColors(
                containerColor = ui.overlayBackground,
                contentColor = ui.textPrimary
            ),
            shape = RoundedCornerShape(topStart = com.example.matrixscreen.core.design.DesignTokens.Radius.card, topEnd = com.example.matrixscreen.core.design.DesignTokens.Radius.card)
        ) {
            Column(
                modifier = Modifier
                    .padding(com.example.matrixscreen.core.design.DesignTokens.Spacing.lg)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sectionSpacing)
            ) {
                // Header
                com.example.matrixscreen.ui.settings.components.SettingsScreenHeader(
                    title = "SETTINGS",
                    onBack = onBack,
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                
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
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.md),
                        verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.md),
                        modifier = Modifier.height(300.dp)
                    ) {
                        items(SettingCategory.values()) { category ->
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
                                optimizedSettings = optimizedSettings
                            )
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
                        onNavigateToUIPreview = onNavigateToUIPreview,
                        ui = ui,
                        optimizedSettings = optimizedSettings
                    )
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
    optimizedSettings: MatrixSettings
) {
    val cardShape = RoundedCornerShape(com.example.matrixscreen.core.design.DesignTokens.Radius.previewCard)
    val glowIntensity = optimizedSettings.glowIntensity.coerceIn(0f, 2f)

    Card(
        modifier = Modifier
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
 * Developer Tools section with UI Preview access
 */
@Composable
private fun DeveloperToolsSection(
    onNavigateToUIPreview: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sm)
    ) {
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
    }
}
