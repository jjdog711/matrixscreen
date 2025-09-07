package com.example.matrixscreen.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.ui.theme.MatrixUIColorScheme
import com.example.matrixscreen.ui.theme.AppTypography

/**
 * Modern UI components following the MatrixScreen design spec
 * Features: 8dp radius, 48dp touch targets, 340dp max width, dynamic colors
 */

/**
 * Modern header title component
 * Space Grotesk, 20sp, left-aligned, dynamic color
 */
@Composable
fun ModernHeaderTitle(
    title: String,
    colorScheme: MatrixUIColorScheme,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = AppTypography.headlineSmall,
        color = colorScheme.textAccent,
        modifier = modifier
    )
}

/**
 * Modern section label component
 * Space Grotesk, 16sp, SemiBold, left-aligned
 */
@Composable
fun ModernSectionLabel(
    label: String,
    colorScheme: MatrixUIColorScheme,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        style = AppTypography.titleSmall,
        color = colorScheme.textAccent,
        modifier = modifier
    )
}

/**
 * Modern button component
 * Space Grotesk, 14sp, 48dp height, 8dp radius, dynamic colors
 */
@Composable
fun ModernButton(
    text: String,
    onClick: () -> Unit,
    colorScheme: MatrixUIColorScheme,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    enabled: Boolean = true
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isPrimary) {
            colorScheme.buttonConfirm
        } else {
            colorScheme.backgroundSecondary
        },
        animationSpec = tween(200),
        label = "button_background"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isPrimary) {
            colorScheme.border
        } else {
            colorScheme.borderDim
        },
        animationSpec = tween(200),
        label = "button_border"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isPrimary) {
            colorScheme.textAccent
        } else {
            colorScheme.textPrimary
        },
        animationSpec = tween(200),
        label = "button_text"
    )
    
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            style = AppTypography.labelLarge,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Modern slider component
 * JetBrains Mono labels, dynamic colors, 8dp radius
 */
@Composable
fun ModernSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    label: String,
    colorScheme: MatrixUIColorScheme,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Label and value row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = AppTypography.bodyMedium,
                color = colorScheme.textPrimary
            )
            Text(
                text = String.format("%.1f", value),
                style = AppTypography.bodyMedium,
                color = colorScheme.textSecondary
            )
        }
        
        // Slider
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = colorScheme.sliderActive,
                activeTrackColor = colorScheme.sliderActive,
                inactiveTrackColor = colorScheme.sliderInactive
            ),
            modifier = Modifier.height(48.dp) // Ensure 48dp touch target
        )
        
        // Range labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SMALL",
                style = AppTypography.bodySmall,
                color = colorScheme.textSecondary
            )
            Text(
                text = "LARGE",
                style = AppTypography.bodySmall,
                color = colorScheme.textSecondary
            )
        }
    }
}

/**
 * Modern selection card component
 * 8dp radius, dynamic colors, 48dp touch target
 */
@Composable
fun ModernSelectionCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorScheme: MatrixUIColorScheme,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.selectionBackground
        } else {
            colorScheme.backgroundSecondary
        },
        animationSpec = tween(200),
        label = "card_background"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.border
        } else {
            colorScheme.borderDim
        },
        animationSpec = tween(200),
        label = "card_border"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.textAccent
        } else {
            colorScheme.textPrimary
        },
        animationSpec = tween(200),
        label = "card_text"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = AppTypography.bodyMedium,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

/**
 * Modern helper text component
 * JetBrains Mono, 12sp, Light, muted color
 */
@Composable
fun ModernHelperText(
    text: String,
    colorScheme: MatrixUIColorScheme,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = AppTypography.bodySmall,
        color = colorScheme.textSecondary,
        modifier = modifier
    )
}

/**
 * Modern symbol preview component
 * Enhanced with Space Grotesk styling and better visual presentation
 */
@Composable
fun ModernSymbolPreview(
    characters: String,
    colorScheme: MatrixUIColorScheme,
    modifier: Modifier = Modifier,
    maxCharacters: Int = 16
) {
    val previewText = if (characters.length > maxCharacters) {
        characters.take(maxCharacters) + "..."
    } else {
        characters
    }
    
    // Use a monospace style for character preview to maintain alignment
    Text(
        text = previewText,
        style = AppTypography.bodyMedium.copy(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        ),
        color = colorScheme.textPrimary,
        modifier = modifier
    )
}

/**
 * Modern overlay container
 * 85% opacity background, 8dp radius, 340dp max width
 */
@Composable
fun ModernOverlayContainer(
    colorScheme: MatrixUIColorScheme,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val maxWidth = minOf(340.dp, maxWidth * 0.9f)
        
        Column(
            modifier = Modifier
                .width(maxWidth)
                .clip(RoundedCornerShape(8.dp))
                .background(colorScheme.overlayBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
        }
    }
}

/**
 * Modern action buttons row
 * Three buttons: Confirm, Reset, Cancel
 */
@Composable
fun ModernActionButtons(
    onConfirm: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit,
    colorScheme: MatrixUIColorScheme,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Confirm Button
        ModernButton(
            text = "CONFIRM",
            onClick = onConfirm,
            colorScheme = colorScheme,
            modifier = Modifier.weight(1f),
            isPrimary = true,
            enabled = enabled
        )
        
        // Reset Button
        ModernButton(
            text = "RESET",
            onClick = onReset,
            colorScheme = colorScheme,
            modifier = Modifier.weight(1f),
            isPrimary = false,
            enabled = enabled
        )
        
        // Cancel Button
        ModernButton(
            text = "CANCEL",
            onClick = onCancel,
            colorScheme = colorScheme,
            modifier = Modifier.weight(1f),
            isPrimary = false,
            enabled = enabled
        )
    }
}
