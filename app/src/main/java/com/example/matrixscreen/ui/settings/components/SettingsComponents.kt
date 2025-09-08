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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.settings.model.SettingSpec
import com.example.matrixscreen.ui.settings.model.BooleanSpec
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.ModernTextWithGlow
import com.example.matrixscreen.ui.theme.MatrixUIColorScheme
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings

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
        Spacer(modifier = Modifier.size(32.dp))
        
        ModernTextWithGlow(
            text = title,
            style = AppTypography.headlineMedium,
            color = androidx.compose.ui.graphics.Color.White,
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
    modifier: Modifier = Modifier,
    expanded: Boolean = false
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .let { base ->
                    if (expanded) {
                        base
                            .padding(horizontal = 12.dp, vertical = 12.dp)
                            .fillMaxSize()
                    } else {
                        base
                            .widthIn(max = 400.dp)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(com.example.matrixscreen.core.design.DesignTokens.Sizing.overlayMaxHeight)
                    }
                }
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
 * Section card for grouping related settings - matches PreviewSectionCard styling
 */
@Composable
fun SettingsSection(
    title: String? = null,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .let { cardModifier ->
                val glowIntensity = optimizedSettings.glowIntensity.coerceIn(0f, 2f)
                val cardShape = RoundedCornerShape(com.example.matrixscreen.core.design.DesignTokens.Radius.previewCard)

                if (glowIntensity > 0.1f) {
                    // Industry standard neon values (based on Cyberpunk 2077, Unity/Unreal)
                    val innerGlowElevation = (glowIntensity * 1.5f + 1f).dp // 1-4dp inner glow
                    val outerGlowElevation = (glowIntensity * 4f + 2f).dp // 2-10dp outer glow

                    val innerGlowAlpha = (glowIntensity * 0.2f + 0.3f).coerceIn(0.3f, 0.5f) // 30-50% inner
                    val outerGlowAlpha = (glowIntensity * 0.075f + 0.1f).coerceIn(0.1f, 0.25f) // 10-25% outer

                    cardModifier
                        // Outer glow (subtle, wide spread)
                        .shadow(
                            elevation = outerGlowElevation,
                            shape = cardShape,
                            ambientColor = ui.textAccent.copy(alpha = outerGlowAlpha),
                            spotColor = ui.textAccent.copy(alpha = outerGlowAlpha * 0.8f)
                        )
                        // Inner glow (brighter, tighter)
                        .shadow(
                            elevation = innerGlowElevation,
                            shape = cardShape,
                            ambientColor = ui.textAccent.copy(alpha = innerGlowAlpha),
                            spotColor = ui.textAccent.copy(alpha = innerGlowAlpha * 0.9f)
                        )
                        // Clean border on top
                        .border(
                            width = 1.dp,
                            color = ui.textAccent,
                            shape = cardShape
                        )
                } else {
                    cardModifier
                }
                },
            colors = CardDefaults.cardColors(
                containerColor = ui.overlayBackground,
                contentColor = ui.textPrimary
            ),
            shape = RoundedCornerShape(com.example.matrixscreen.core.design.DesignTokens.Radius.previewCard),
            elevation = CardDefaults.cardElevation(
                defaultElevation = com.example.matrixscreen.core.design.DesignTokens.Elevation.previewCard
            )
        ) {
            Column(
                modifier = Modifier.padding(com.example.matrixscreen.core.design.DesignTokens.Spacing.sectionPadding),
                verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.elementSpacing)
            ) {
                if (title != null) {
                    ModernTextWithGlow(
                        text = title.uppercase(),
                        style = AppTypography.headlineMedium,
                        color = ui.textAccent,
                        settings = optimizedSettings
                    )
                }
                
                content()
            }
        }
    }
}

/**
 * Preview-friendly overload that supplies default UI/optimized settings.
 */
@Composable
fun SettingsSection(
    title: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val defaults = MatrixSettings.DEFAULT
    val ui = getSafeUIColorScheme(defaults)
    val optimized = rememberOptimizedSettings(defaults)
    SettingsSection(
        title = title,
        ui = ui,
        optimizedSettings = optimized,
        modifier = modifier,
        content = content
    )
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

/**
 * Labeled switch component for boolean settings
 */
@Composable
fun LabeledSwitch(
    spec: BooleanSpec,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            ModernTextWithGlow(
                text = spec.label,
                style = AppTypography.titleMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
            if (spec.help != null) {
                ModernTextWithGlow(
                    text = spec.help,
                    style = AppTypography.bodySmall,
                    color = ui.textSecondary,
                    settings = optimizedSettings
                )
            }
        }
        
        Switch(
            checked = value,
            onCheckedChange = onValueChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ui.primary,
                checkedTrackColor = ui.primary.copy(alpha = 0.5f),
                uncheckedThumbColor = ui.textSecondary,
                uncheckedTrackColor = ui.textSecondary.copy(alpha = 0.3f)
            )
        )
    }
}
