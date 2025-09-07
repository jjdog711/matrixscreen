package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.ui.settings.model.SettingSpec
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.ModernTextWithGlow
import com.example.matrixscreen.ui.theme.MatrixUIColorScheme

/**
 * Standard header for all settings screens
 */
@Composable
fun SettingsScreenHeader(
    title: String,
    onBack: () -> Unit,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = ui.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        ModernTextWithGlow(
            text = title,
            style = AppTypography.headlineSmall,
            color = ui.textPrimary,
            settings = optimizedSettings
        )
        
        // Spacer for balance
        Spacer(modifier = Modifier.size(32.dp))
    }
}

/**
 * Standard container for settings screens
 */
@Composable
fun SettingsScreenContainer(
    title: String,
    onBack: () -> Unit,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
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
                SettingsScreenHeader(
                    title = title,
                    onBack = onBack,
                    ui = ui,
                    optimizedSettings = optimizedSettings
                )
                
                content()
            }
        }
    }
}

/**
 * Labeled slider component with help icon and performance indicator
 */
@Composable
fun LabeledSlider(
    spec: SettingSpec,
    value: Float,
    onValueChange: (Float) -> Unit,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Label row with help and performance indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ModernTextWithGlow(
                    text = spec.label,
                    style = AppTypography.titleMedium,
                    color = ui.textPrimary,
                    settings = optimizedSettings
                )
                
                if (spec.performanceImpact) {
                    ModernTextWithGlow(
                        text = "⚡",
                        style = AppTypography.bodySmall,
                        color = ui.textSecondary,
                        settings = optimizedSettings
                    )
                }
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ModernTextWithGlow(
                    text = "${value.toInt()}${spec.unit ?: ""}",
                    style = AppTypography.bodyMedium,
                    color = ui.textSecondary,
                    settings = optimizedSettings
                )
                
                if (spec.help != null) {
                    IconButton(
                        onClick = { /* TODO: Show help tooltip */ },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Help",
                            tint = ui.textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        
        // Slider
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = spec.range ?: 0f..1f,
            steps = if (spec.step != null) {
                ((spec.range?.endInclusive ?: 1f) - (spec.range?.start ?: 0f) / spec.step).toInt() - 1
            } else 0,
            colors = SliderDefaults.colors(
                thumbColor = ui.primary,
                activeTrackColor = ui.primary,
                inactiveTrackColor = ui.selectionBackground
            )
        )
    }
}

/**
 * Section card for grouping related settings
 */
@Composable
fun SettingsSection(
    title: String? = null,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ui.selectionBackground,
            contentColor = ui.textPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (title != null) {
                ModernTextWithGlow(
                    text = title,
                    style = AppTypography.titleMedium,
                    color = ui.textPrimary,
                    settings = optimizedSettings
                )
            }
            
            content()
        }
    }
}

/**
 * Reset button for sections
 */
@Composable
fun ResetSectionButton(
    onReset: () -> Unit,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onReset() },
        colors = CardDefaults.cardColors(
            containerColor = ui.selectionBackground,
            contentColor = ui.textSecondary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        ModernTextWithGlow(
            text = "Reset",
            style = AppTypography.labelMedium,
            color = ui.textSecondary,
            settings = optimizedSettings,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}
