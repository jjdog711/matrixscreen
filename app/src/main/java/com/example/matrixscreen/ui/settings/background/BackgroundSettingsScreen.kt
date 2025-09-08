package com.example.matrixscreen.ui.settings.background

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.util.FpsCoercionUtil
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.settings.model.get
import com.example.matrixscreen.ui.settings.model.specFor
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow

/**
 * Background settings screen with film grain and performance controls
 */
@Composable
fun BackgroundSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = uiState.draft
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    val context = LocalContext.current
    
    // Get device capabilities for FPS coercion
    val supportedRates = remember { FpsCoercionUtil.getSupportedRefreshRates(context) }
    val effectiveFps = remember(currentSettings.targetFps, supportedRates) {
        FpsCoercionUtil.coerceFps(currentSettings.targetFps, supportedRates)
    }
    val recommendedFpsOptions = remember { FpsCoercionUtil.getRecommendedFpsOptions(context) }
    
    SettingsScreenContainer(
        title = "BACKGROUND",
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
            // Film Grain Section
            SettingsSection(
                title = "Film Grain",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                // Render all background specs using typed SettingId access
                val grainDensitySpec = BACKGROUND_SPECS.specFor(GrainD)
                RenderSetting(
                    spec = grainDensitySpec,
                    value = currentSettings.get(GrainD),
                    onValueChange = { v: Int -> settingsViewModel.updateDraft(GrainD, v) }
                )

                val grainOpacitySpec = BACKGROUND_SPECS.specFor(GrainO)
                RenderSetting(
                    spec = grainOpacitySpec,
                    value = currentSettings.get(GrainO),
                    onValueChange = { v: Float -> settingsViewModel.updateDraft(GrainO, v) }
                )
                }
            )
            
            // Performance Section
            SettingsSection(
                title = "Performance",
                ui = ui,
                optimizedSettings = optimizedSettings,
                content = {
                val targetFpsSpec = BACKGROUND_SPECS.specFor(Fps)
                RenderSetting(
                    spec = targetFpsSpec,
                    value = currentSettings.get(Fps),
                    onValueChange = { v: Int -> settingsViewModel.updateDraft(Fps, v) }
                )
                
                // FPS Selection with effective FPS display
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ModernTextWithGlow(
                            text = targetFpsSpec.label,
                            style = AppTypography.titleMedium,
                            color = ui.textPrimary,
                            settings = optimizedSettings
                        )
                        
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            ModernTextWithGlow(
                                text = "${currentSettings.targetFps.toInt()} FPS",
                                style = AppTypography.bodyMedium,
                                color = ui.textSecondary,
                                settings = optimizedSettings
                            )
                            
                            // Show effective FPS if different from target
                            if (effectiveFps != currentSettings.targetFps) {
                                ModernTextWithGlow(
                                    text = "→ ${effectiveFps} FPS (effective)",
                                    style = AppTypography.bodySmall,
                                    color = ui.textAccent,
                                    settings = optimizedSettings
                                )
                            }
                        }
                    }
                    
                    // Frame rate buttons using device-recommended options
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recommendedFpsOptions.forEach { fps ->
                            FrameRateButton(
                                fps = fps.toFloat(),
                                isSelected = currentSettings.targetFps == fps,
                                onClick = { settingsViewModel.updateDraft(Fps, fps) },
                                ui = ui,
                                optimizedSettings = optimizedSettings
                            )
                        }
                    }
                    
                    // Show device capabilities info
                    if (supportedRates.size > 1) {
                        ModernTextWithGlow(
                            text = "Device supports: ${supportedRates.joinToString(", ")} Hz",
                            style = AppTypography.bodySmall,
                            color = ui.textSecondary,
                            settings = optimizedSettings
                        )
                    }
                }
                }
            )
        }
        },
        modifier = modifier
    )
}

/**
 * Frame rate selection button
 */
@Composable
private fun FrameRateButton(
    fps: Float,
    isSelected: Boolean,
    onClick: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Card(
        modifier = Modifier
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ui.primary else ui.selectionBackground,
            contentColor = if (isSelected) ui.textPrimary else ui.textSecondary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        ModernTextWithGlow(
            text = "${fps.toInt()}",
            style = AppTypography.labelMedium,
            color = if (isSelected) ui.textPrimary else ui.textSecondary,
            settings = optimizedSettings,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}