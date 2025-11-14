package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
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
    title: String?,
    onBack: () -> Unit,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    if (title.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        return
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
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
 * Standard container for settings screens with proper nested scroll support
 */
@Composable
fun SettingsScreenContainer(
    title: String? = null,
    onBack: () -> Unit,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val contentScrollState = rememberScrollState()

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

                // Content area with proper scrolling support
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(contentScrollState)
                ) {
                    content()
                }
            }
        }
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
        val glowIntensity = optimizedSettings.glowIntensity.coerceIn(0f, 5f)
        val cardShape = RoundedCornerShape(com.example.matrixscreen.core.design.DesignTokens.Radius.previewCard)
        val cardModifier = Modifier
            .fillMaxWidth()
            .widthIn(max = com.example.matrixscreen.core.design.DesignTokens.Sizing.overlayContentMaxWidth)
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
                        .border(
                            width = 1.dp,
                            color = ui.textAccent,
                            shape = cardShape
                        )
                } else {
                    base
                }
            }

        Card(
            modifier = cardModifier,
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
        Text(
            text = "Reset",
            style = AppTypography.labelMedium,
            color = ui.textSecondary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

/**
 * Preset button for theme selection with optional color swatches
 */
@Composable
fun PresetButton(
    name: String,
    onClick: () -> Unit,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier,
    swatches: List<Long>? = null
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = ui.selectionBackground,
            contentColor = ui.textPrimary
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModernTextWithGlow(
                text = name,
                style = AppTypography.labelMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
            if (!swatches.isNullOrEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    swatches.take(6).forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(c))
                                .border(1.dp, ui.textSecondary.copy(alpha = 0.25f), CircleShape)
                        )
                    }
                }
            }
        }
    }
}
