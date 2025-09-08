package com.example.matrixscreen.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.ModernTextWithGlow
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings

/**
 * Visual test harness for the new UI system
 * Shows all typography styles, colors, and component layouts
 * Serves as the visual contract for all UI in the app
 */
@Composable
fun UIStylePreviewScreen(
    settings: MatrixSettings = MatrixSettings()
) {
    val ui = getSafeUIColorScheme(settings)
    val optimizedSettings = rememberOptimizedSettings(settings)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ui.backgroundPrimary)
            .padding(DesignTokens.Spacing.screenPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sectionSpacing)
    ) {
        // Page Title with Glow
        ModernTextWithGlow(
            text = "UI STYLE PREVIEW",
            style = AppTypography.headlineLarge,
            color = ui.textAccent,
            settings = optimizedSettings
        )
        
        // Typography Section
        PreviewSectionCard(
            title = "Typography",
            ui = ui,
            optimizedSettings = optimizedSettings
        ) {
            TypographyPreview(ui, optimizedSettings)
        }
        
        // Color Swatches Section
        PreviewSectionCard(
            title = "Color Swatches",
            ui = ui,
            optimizedSettings = optimizedSettings
        ) {
            ColorSwatchesPreview(ui)
        }
        
        // Render Controls Section (with mock controls)
        PreviewSectionCard(
            title = "Render Controls",
            ui = ui,
            optimizedSettings = optimizedSettings
        ) {
            RenderControlsPreview(ui, optimizedSettings)
        }
        
        // Glow Comparison Section
        PreviewSectionCard(
            title = "Glow Comparison",
            ui = ui,
            optimizedSettings = optimizedSettings
        ) {
            GlowComparisonPreview(ui, optimizedSettings)
        }
    }
}

/**
 * Reusable section card with glow border for preview sections
 * Provides consistent styling across all preview sections
 */
@Composable
private fun PreviewSectionCard(
    title: String,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .let { modifier ->
                val glowIntensity = optimizedSettings.glowIntensity.coerceIn(0f, 2f)
                val cardShape = RoundedCornerShape(DesignTokens.Radius.previewCard)
                
                if (glowIntensity > 0.1f) {
                    // Industry standard neon values (based on Cyberpunk 2077, Unity/Unreal)
                    val innerGlowElevation = (glowIntensity * 1.5f + 1f).dp // 1-4dp inner glow
                    val outerGlowElevation = (glowIntensity * 4f + 2f).dp // 2-10dp outer glow
                    
                    val innerGlowAlpha = (glowIntensity * 0.2f + 0.3f).coerceIn(0.3f, 0.5f) // 30-50% inner
                    val outerGlowAlpha = (glowIntensity * 0.075f + 0.1f).coerceIn(0.1f, 0.25f) // 10-25% outer
                    
                    modifier
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
                    modifier
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = ui.overlayBackground,
            contentColor = ui.textPrimary
        ),
        shape = RoundedCornerShape(DesignTokens.Radius.previewCard),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignTokens.Elevation.previewCard
        )
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.Spacing.sectionPadding),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.elementSpacing)
        ) {
            // Section Header with Glow
            ModernTextWithGlow(
                text = title.uppercase(),
                style = AppTypography.headlineMedium,
                color = ui.textAccent,
                settings = optimizedSettings
            )
            
            // Section Content
            content()
        }
    }
}

@Composable
private fun TypographyPreview(
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    @Suppress("UNUSED_PARAMETER") optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.elementSpacing)
    ) {
        // Headlines
        Text(
            text = "Headline Large - Space Grotesk",
            style = AppTypography.headlineLarge,
            color = ui.textPrimary
        )
        Text(
            text = "Headline Medium - Space Grotesk",
            style = AppTypography.headlineMedium,
            color = ui.textPrimary
        )
        Text(
            text = "Headline Small - Space Grotesk",
            style = AppTypography.headlineSmall,
            color = ui.textPrimary
        )
        
        // Body Text
        Text(
            text = "Body Large - JetBrains Mono",
            style = AppTypography.bodyLarge,
            color = ui.textSecondary
        )
        Text(
            text = "Body Medium - JetBrains Mono",
            style = AppTypography.bodyMedium,
            color = ui.textSecondary
        )
        Text(
            text = "Body Small - JetBrains Mono",
            style = AppTypography.bodySmall,
            color = ui.textSecondary
        )
        
        // Labels
        Text(
            text = "Label Large - Space Grotesk (Buttons)",
            style = AppTypography.labelLarge,
            color = ui.textPrimary
        )
        Text(
            text = "Label Small - JetBrains Mono (Values)",
            style = AppTypography.labelSmall,
            color = ui.textSecondary
        )
    }
}

@Composable
private fun ColorSwatchesPreview(
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.elementSpacing)
    ) {
        // Primary color row
        Row(
            horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
        ) {
            ColorSwatch("Primary", ui.primary)
            ColorSwatch("Text Accent", ui.textAccent)
            ColorSwatch("Overlay BG", ui.overlayBackground)
            ColorSwatch("Slider Active", ui.sliderActive)
        }
        
        // Secondary color row
        Row(
            horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
        ) {
            ColorSwatch("Text Primary", ui.textPrimary)
            ColorSwatch("Text Secondary", ui.textSecondary)
            ColorSwatch("Slider Inactive", ui.sliderInactive)
            ColorSwatch("Border Dim", ui.borderDim)
        }
    }
}

@Composable
private fun ColorSwatch(
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.xs)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color, RoundedCornerShape(DesignTokens.Radius.card))
        )
        Text(
            text = label,
            style = AppTypography.bodySmall,
            color = Color.White
        )
    }
}

/**
 * Preview section with mock controls (Switch, Slider, Color Chip)
 * Shows what real interactive controls will look like
 */
@Composable
private fun RenderControlsPreview(
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    @Suppress("UNUSED_PARAMETER") optimizedSettings: MatrixSettings
) {
    var sliderValue by remember { mutableFloatStateOf(0.6f) }
    var switchValue by remember { mutableStateOf(true) }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
    ) {
        // Slider Row - Glow Intensity
        SliderRow(
            label = "Glow Intensity",
            value = sliderValue,
            onValueChange = { sliderValue = it },
            ui = ui
        )
        
        // Switch Row - Enable Rain Flicker
        SwitchRow(
            label = "Enable Rain Flicker",
            checked = switchValue,
            onCheckedChange = { switchValue = it },
            ui = ui
        )
        
        // Color Chip Row - Accent Color
        ColorChipRow(
            label = "Accent Color",
            color = ui.textAccent,
            ui = ui
        )
    }
}

/**
 * Mock slider control with label and percentage display
 */
@Composable
private fun SliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = AppTypography.bodyMedium,
                color = ui.textPrimary
            )
            Text(
                text = "${(value * 100).toInt()}%",
                style = AppTypography.labelSmall,
                color = ui.textSecondary
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = ui.sliderActive,
                activeTrackColor = ui.sliderActive,
                inactiveTrackColor = ui.sliderInactive
            )
        )
    }
}

/**
 * Mock switch control with label
 */
@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = ui.textPrimary
        )
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ui.textAccent,
                checkedTrackColor = ui.textAccent.copy(alpha = 0.5f),
                uncheckedThumbColor = ui.textSecondary,
                uncheckedTrackColor = ui.borderDim
            )
        )
    }
}

/**
 * Mock color chip control with label
 */
@Composable
private fun ColorChipRow(
    label: String,
    color: Color,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = ui.textPrimary
        )
        
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color, CircleShape)
        )
    }
}


@Composable
private fun GlowComparisonPreview(
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
    ) {
        // With Glow Examples
        Column(
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
        ) {
            Text(
                text = "✨ WITH GLOW (Use for titles & headers)",
                style = AppTypography.bodySmall,
                color = ui.textSecondary
            )
            
            ModernTextWithGlow(
                text = "SECTION HEADER",
                style = AppTypography.headlineMedium,
                color = ui.textAccent,
                settings = optimizedSettings
            )
            
            ModernTextWithGlow(
                text = "Page Title",
                style = AppTypography.headlineLarge,
                color = ui.textAccent,
                settings = optimizedSettings
            )
        }
        
        // Without Glow Examples
        Column(
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
        ) {
            Text(
                text = "🔘 WITHOUT GLOW (Use for content & values)",
                style = AppTypography.bodySmall,
                color = ui.textSecondary
            )
            
            Text(
                text = "Setting Label",
                style = AppTypography.bodyMedium,
                color = ui.textPrimary
            )
            
            Text(
                text = "Value: 60%",
                style = AppTypography.labelSmall,
                color = ui.textSecondary
            )
            
            Text(
                text = "Description text for explaining settings",
                style = AppTypography.bodySmall,
                color = ui.textSecondary
            )
        }
    }
}
