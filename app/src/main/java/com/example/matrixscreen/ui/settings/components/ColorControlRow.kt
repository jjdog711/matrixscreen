package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.ui.theme.MatrixTextStyles
import com.example.matrixscreen.ui.theme.MatrixUIColorScheme

/**
 * A color control row component for color selection.
 *
 * This component displays a label, optional helper/status text, and a color swatch
 * that can open either the built-in ColorPickerDialog or a caller-provided dialog.
 */
@Composable
fun ColorControlRow(
    label: String,
    color: Long,
    onColorChange: (Long) -> Unit,
    help: String? = null,
    statusText: String? = null,
    enabled: Boolean = true,
    uiColors: MatrixUIColorScheme? = null,
    onColorClick: (() -> Unit)? = null,
    showPickerButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shouldUseInternalPicker = onColorClick == null
    var showColorPicker by remember(onColorClick) { mutableStateOf(false) }
    val contentAlpha = if (enabled) 1f else 0.6f
    val textColor = uiColors?.textPrimary ?: MaterialTheme.colorScheme.onSurface
    val helperColor = uiColors?.textSecondary ?: MaterialTheme.colorScheme.onSurfaceVariant
    val statusColor = uiColors?.textAccent ?: MaterialTheme.colorScheme.primary
    val swatchBorderColor = uiColors?.selectionBackground ?: MaterialTheme.colorScheme.outline
    val buttonColor = uiColors?.textSecondary ?: MaterialTheme.colorScheme.onSurfaceVariant

    val handleClick = {
        if (!enabled) return@let
        if (shouldUseInternalPicker) {
            showColorPicker = true
        } else {
            onColorClick?.invoke()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignTokens.Spacing.md)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MatrixTextStyles.SliderLabel,
                    color = textColor
                )
                help?.let {
                    Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
                    Text(
                        text = it,
                        style = MatrixTextStyles.HelperText,
                        color = helperColor
                    )
                }
                statusText?.let {
                    Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
                    Text(
                        text = it,
                        style = MatrixTextStyles.HelperText,
                        color = statusColor
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(DesignTokens.Sizing.colorSwatchSize)
                        .clip(RoundedCornerShape(DesignTokens.Radius.sm))
                        .background(Color(color))
                        .border(
                            width = DesignTokens.Outline.thin,
                            color = swatchBorderColor,
                            shape = RoundedCornerShape(DesignTokens.Radius.sm)
                        )
                        .alpha(contentAlpha)
                        .clickable(enabled = enabled) { handleClick() }
                )

                if (showPickerButton) {
                    Text(
                        text = "•••",
                        style = MatrixTextStyles.SliderLabel,
                        color = buttonColor,
                        modifier = Modifier
                            .alpha(contentAlpha)
                            .clickable(enabled = enabled) { handleClick() }
                    )
                }
            }
        }
    }

    if (shouldUseInternalPicker) {
        ColorPickerDialog(
            isOpen = showColorPicker,
            initialColor = color,
            onColorSelected = { selectedColor ->
                onColorChange(selectedColor)
            },
            onDismiss = {
                showColorPicker = false
            }
        )
    }
}
