package com.example.matrixscreen.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.data.model.MatrixSettings

/**
 * Glow effects system for MatrixScreen UI
 * Integrates with MatrixSettings glow configuration for titles and interactive elements
 */

/**
 * Apply glow effect to text using readability-first approach (like real game UI)
 * Text must be fully readable without glow - glow is purely atmospheric
 */
@Composable
fun TextWithGlow(
    text: String,
    style: TextStyle,
    color: Color,
    settings: MatrixSettings,
    modifier: Modifier = Modifier,
    glowColor: Color? = null
) {
    val glowIntensity = settings.glowIntensity.coerceIn(0f, 2f)
    
    // Readability-first: Ensure high contrast base text
    val readableColor = color // Keep original color for readability
    
    val glowStyle = if (glowIntensity > 0.1f) {
        // Professional approach: Minimal atmospheric glow only
        val effectiveGlowColor = glowColor ?: color
        val subtleBlurRadius = (glowIntensity * 2f).coerceIn(0.5f, 4f) // Much more subtle
        val atmosphericAlpha = (glowIntensity * 0.15f).coerceIn(0.05f, 0.3f) // Very low opacity
        
        style.copy(
            shadow = Shadow(
                color = effectiveGlowColor.copy(alpha = atmosphericAlpha),
                offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                blurRadius = subtleBlurRadius
            )
        )
    } else {
        style
    }
    
    Text(
        text = text,
        style = glowStyle,
        color = readableColor, // Always use high-contrast readable color
        modifier = modifier
    )
}

/**
 * Apply glow effect to button based on MatrixSettings
 * Note: Button glow is handled through elevation and border styling
 */
@Composable
fun ButtonWithGlow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
    @Suppress("UNUSED_PARAMETER") settings: MatrixSettings,
    @Suppress("UNUSED_PARAMETER") glowColor: Color? = null
) {
    // For buttons, we rely on elevation and border styling rather than text shadow
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        content = content
    )
}

/**
 * Apply glow effect to card/container based on MatrixSettings
 * Note: Card glow is handled through elevation and border styling
 */
@Composable
fun CardWithGlow(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: androidx.compose.foundation.BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
    @Suppress("UNUSED_PARAMETER") settings: MatrixSettings,
    @Suppress("UNUSED_PARAMETER") glowColor: Color? = null
) {
    // For cards, we rely on elevation and border styling rather than drawing effects
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content
    )
}


/**
 * Enhanced text style with readability-first glow support
 */
@Composable
fun createGlowTextStyle(
    baseStyle: TextStyle,
    settings: MatrixSettings,
    glowColor: Color? = null
): TextStyle {
    val glowIntensity = settings.glowIntensity.coerceIn(0f, 2f)
    
    return if (glowIntensity > 0.1f) {
        // Professional approach: Minimal atmospheric glow that doesn't hurt readability
        val effectiveGlowColor = glowColor ?: Color.White
        val subtleBlurRadius = (glowIntensity * 2f).coerceIn(0.5f, 4f) // Much more subtle
        val atmosphericAlpha = (glowIntensity * 0.15f).coerceIn(0.05f, 0.3f) // Very low opacity
        
        baseStyle.copy(
            shadow = Shadow(
                color = effectiveGlowColor.copy(alpha = atmosphericAlpha),
                offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                blurRadius = subtleBlurRadius
            )
        )
    } else {
        baseStyle
    }
}


/**
 * Apply glow to modern UI components
 */
@Composable
fun ModernTextWithGlow(
    text: String,
    style: TextStyle,
    color: Color,
    settings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    TextWithGlow(
        text = text,
        style = style,
        color = color,
        settings = settings,
        modifier = modifier,
        glowColor = color
    )
}

/**
 * Apply glow to modern buttons
 */
@Composable
fun ModernButtonWithGlow(
    text: String,
    onClick: () -> Unit,
    colorScheme: MatrixUIColorScheme,
    settings: MatrixSettings,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true
) {
    val backgroundColor = if (isPrimary) {
        colorScheme.buttonConfirm
    } else {
        colorScheme.backgroundSecondary
    }
    
    val borderColor = if (isPrimary) {
        colorScheme.border
    } else {
        colorScheme.borderDim
    }
    
    val textColor = if (isPrimary) {
        colorScheme.textAccent
    } else {
        colorScheme.textPrimary
    }
    
    CardWithGlow(
        modifier = modifier
            .height(48.dp)
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        settings = settings,
        glowColor = if (isPrimary) colorScheme.primary else null,
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                ModernTextWithGlow(
                    text = text.uppercase(),
                    style = com.example.matrixscreen.ui.theme.MatrixTextStyles.ButtonText,
                    color = textColor,
                    settings = settings
                )
            }
        }
    )
}
