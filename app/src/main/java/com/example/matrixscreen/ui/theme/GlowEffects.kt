package com.example.matrixscreen.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
 * Apply glow effect to text based on MatrixSettings
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
    val effectiveGlowColor = glowColor ?: color
    val glowIntensity = settings.glowIntensity.coerceIn(0f, 2f)
    val glowAlpha = (glowIntensity * 0.3f).coerceIn(0f, 0.6f)
    
    if (glowIntensity > 0.1f) {
        // Apply glow effect
        Text(
            text = text,
            style = style,
            color = color,
            modifier = modifier.drawBehind {
                drawGlowEffect(
                    glowColor = effectiveGlowColor,
                    glowAlpha = glowAlpha,
                    glowRadius = (glowIntensity * 8f).dp.toPx()
                )
            }
        )
    } else {
        // No glow
        Text(
            text = text,
            style = style,
            color = color,
            modifier = modifier
        )
    }
}

/**
 * Apply glow effect to button based on MatrixSettings
 */
@Composable
fun ButtonWithGlow(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
    settings: MatrixSettings,
    glowColor: Color? = null
) {
    val glowIntensity = settings.glowIntensity.coerceIn(0f, 2f)
    val glowAlpha = (glowIntensity * 0.2f).coerceIn(0f, 0.4f)
    
    if (glowIntensity > 0.1f) {
        Button(
            onClick = onClick,
            modifier = modifier.drawBehind {
                drawGlowEffect(
                    glowColor = glowColor ?: Color.White,
                    glowAlpha = glowAlpha,
                    glowRadius = (glowIntensity * 6f).dp.toPx()
                )
            },
            enabled = enabled,
            content = content
        )
    } else {
        Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            content = content
        )
    }
}

/**
 * Apply glow effect to card/container based on MatrixSettings
 */
@Composable
fun CardWithGlow(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: androidx.compose.foundation.BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
    settings: MatrixSettings,
    glowColor: Color? = null
) {
    val glowIntensity = settings.glowIntensity.coerceIn(0f, 2f)
    val glowAlpha = (glowIntensity * 0.15f).coerceIn(0f, 0.3f)
    
    if (glowIntensity > 0.1f) {
        Card(
            modifier = modifier.drawBehind {
                drawGlowEffect(
                    glowColor = glowColor ?: Color.White,
                    glowAlpha = glowAlpha,
                    glowRadius = (glowIntensity * 4f).dp.toPx()
                )
            },
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = colors,
            elevation = elevation,
            border = border,
            content = content
        )
    }
}

/**
 * Draw glow effect behind composable
 */
private fun DrawScope.drawGlowEffect(
    glowColor: Color,
    glowAlpha: Float,
    glowRadius: Float
) {
    // Validate parameters to prevent crashes
    val safeGlowAlpha = glowAlpha.coerceIn(0f, 1f)
    val safeGlowRadius = glowRadius.coerceIn(0f, 50f) // Limit max radius
    
    if (safeGlowAlpha <= 0f || safeGlowRadius <= 0f) {
        return // Skip drawing if parameters are invalid
    }
    
    val glowPaint = Paint().apply {
        color = glowColor.copy(alpha = safeGlowAlpha)
        style = PaintingStyle.Stroke
        strokeWidth = safeGlowRadius
        isAntiAlias = true
        // maskFilter = MaskFilter.blur(BlurStyle.Normal, safeGlowRadius)
    }
    
    // Draw multiple glow layers for better effect
    repeat(3) { layer ->
        val layerAlpha = safeGlowAlpha * (1f - layer * 0.3f).coerceIn(0f, 1f)
        val layerRadius = safeGlowRadius * (1f + layer * 0.5f).coerceIn(0f, 50f)
        
        glowPaint.color = glowColor.copy(alpha = layerAlpha)
        glowPaint.strokeWidth = layerRadius
        
        try {
            drawRoundRect(
                color = glowColor.copy(alpha = layerAlpha),
                topLeft = androidx.compose.ui.geometry.Offset(-layerRadius, -layerRadius),
                size = androidx.compose.ui.geometry.Size(
                    size.width + layerRadius * 2,
                    size.height + layerRadius * 2
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx() + layerRadius)
            )
        } catch (e: Exception) {
            android.util.Log.e("GlowEffect", "Error drawing glow layer: ${e.message}")
            // Skip this layer if drawing fails
        }
    }
}

/**
 * Enhanced text style with glow support
 */
@Composable
fun createGlowTextStyle(
    baseStyle: TextStyle,
    settings: MatrixSettings,
    glowColor: Color? = null
): TextStyle {
    val glowIntensity = settings.glowIntensity.coerceIn(0f, 2f)
    
    return if (glowIntensity > 0.1f) {
        baseStyle.copy(
            shadow = Shadow(
                color = glowColor ?: Color.White,
                offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                blurRadius = (glowIntensity * 8f).sp.value
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
