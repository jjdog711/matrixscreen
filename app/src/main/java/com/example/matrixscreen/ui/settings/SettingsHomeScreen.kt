package com.example.matrixscreen.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.ui.settings.model.SettingCategory
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow

/**
 * Home screen for the new settings architecture
 * Displays 6 category cards in a grid layout with presets at the top
 */
@Composable
fun SettingsHomeScreen(
    settingsViewModel: com.example.matrixscreen.ui.SettingsViewModel,
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
    val currentSettings by settingsViewModel.settings.collectAsState()
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .widthIn(max = 400.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .wrapContentHeight()
                .navigationBarsPadding(),
            colors = CardDefaults.cardColors(
                containerColor = ui.overlayBackground,
                contentColor = ui.textPrimary
            ),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ModernTextWithGlow(
                        text = "SETTINGS",
                        style = AppTypography.headlineSmall,
                        color = ui.textPrimary,
                        settings = optimizedSettings
                    )
                    
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ui.textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Presets Section
                PresetsSection(
                    settingsViewModel = settingsViewModel,
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                
                // Categories Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
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
        }
    }
}

/**
 * Presets section with quick configuration buttons
 */
@Composable
private fun PresetsSection(
    settingsViewModel: com.example.matrixscreen.ui.SettingsViewModel,
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
                    onClick = { settingsViewModel.applyPreset(preset) },
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = ui.selectionBackground,
            contentColor = ui.textPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Icon/Emoji
            ModernTextWithGlow(
                text = category.icon,
                style = AppTypography.headlineMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
            
            // Title
            ModernTextWithGlow(
                text = category.displayName,
                style = AppTypography.titleMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
            
            // Description
            ModernTextWithGlow(
                text = category.description,
                style = AppTypography.bodySmall,
                color = ui.textSecondary,
                settings = optimizedSettings
            )
        }
    }
}
