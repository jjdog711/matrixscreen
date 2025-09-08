package com.example.matrixscreen.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * Only available in debug builds
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
        TypographyPreview(ui, optimizedSettings)
        
        // Color Swatches Section
        ColorSwatchesPreview(ui)
        
        // Component Layout Section
        ComponentLayoutPreview(ui, optimizedSettings)
        
        // Glow vs Non-Glow Comparison
        GlowComparisonPreview(ui, optimizedSettings)
    }
}

@Composable
private fun TypographyPreview(
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.elementSpacing)
    ) {
        // Section Header with Glow
        ModernTextWithGlow(
            text = "TYPOGRAPHY",
            style = AppTypography.headlineSmall,
            color = ui.textAccent,
            settings = optimizedSettings
        )
        
        // Headlines
        Text(
            text = "Headline Large",
            style = AppTypography.headlineLarge,
            color = ui.textPrimary
        )
        Text(
            text = "Headline Medium",
            style = AppTypography.headlineMedium,
            color = ui.textPrimary
        )
        Text(
            text = "Headline Small",
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
            text = "Label Small - JetBrains Mono",
            style = AppTypography.labelSmall,
            color = ui.textSecondary
        )
        
        // Button Text
        Text(
            text = "Button Text - Space Grotesk",
            style = AppTypography.labelLarge,
            color = ui.textPrimary
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
        Text(
            text = "COLOR SWATCHES",
            style = AppTypography.headlineSmall,
            color = ui.textPrimary
        )
        
        // Color swatch grid
        Row(
            horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
        ) {
            ColorSwatch("Primary", ui.primary)
            ColorSwatch("Text Accent", ui.textAccent)
            ColorSwatch("Overlay BG", ui.overlayBackground)
            ColorSwatch("Slider Active", ui.sliderActive)
        }
        
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

@Composable
private fun ComponentLayoutPreview(
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.elementSpacing)
    ) {
        Text(
            text = "COMPONENT LAYOUT",
            style = AppTypography.headlineSmall,
            color = ui.textPrimary
        )
        
        // Sample button
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = ui.primary,
                contentColor = ui.textPrimary
            ),
            shape = RoundedCornerShape(DesignTokens.Radius.button)
        ) {
            Text(
                text = "Confirm",
                style = AppTypography.bodyMedium
            )
        }
        
        // Sample card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = ui.overlayBackground
            ),
            shape = RoundedCornerShape(DesignTokens.Radius.card)
        ) {
            Column(
                modifier = Modifier.padding(DesignTokens.Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
            ) {
                Text(
                    text = "Sample Card",
                    style = AppTypography.titleSmall,
                    color = ui.textPrimary
                )
                Text(
                    text = "This is a sample card with proper spacing and colors.",
                    style = AppTypography.bodySmall,
                    color = ui.textSecondary
                )
            }
        }
    }
}

@Composable
private fun GlowComparisonPreview(
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.elementSpacing)
    ) {
        Text(
            text = "GLOW COMPARISON",
            style = AppTypography.headlineSmall,
            color = ui.textPrimary
        )
        
        // With Glow (should be used for headers)
        ModernTextWithGlow(
            text = "HEADER WITH GLOW",
            style = AppTypography.headlineSmall,
            color = ui.textAccent,
            settings = optimizedSettings
        )
        
        // Without Glow (should be used for labels/values)
        Text(
            text = "Label Without Glow",
            style = AppTypography.bodyMedium,
            color = ui.textSecondary
        )
        
        Text(
            text = "Value: 42",
            style = AppTypography.labelSmall,
            color = ui.textSecondary
        )
    }
}
