package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.core.util.ColorUtils
import com.example.matrixscreen.ui.theme.MatrixTextStyles

/**
 * A color picker dialog that allows users to select colors using RGB sliders and hex input.
 * 
 * This dialog provides a comprehensive color selection interface with:
 * - RGB sliders for fine-tuning color components
 * - Hex input field for direct color entry
 * - Live preview of the selected color
 * - Material 3 design system integration
 * 
 * @param isOpen Whether the dialog is currently open
 * @param initialColor The initial color value (as Long ARGB)
 * @param onColorSelected Callback when a color is selected
 * @param onDismiss Callback when the dialog is dismissed
 */
@Composable
fun ColorPickerDialog(
    isOpen: Boolean,
    initialColor: Long,
    onColorSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    if (isOpen) {
        var currentColor by remember { mutableStateOf(initialColor) }
        var hexInput by remember { mutableStateOf(ColorUtils.longToHex(currentColor, includeAlpha = false)) }
        var hexError by remember { mutableStateOf(false) }
        
        // Update hex input when color changes from sliders
        LaunchedEffect(currentColor) {
            hexInput = ColorUtils.longToHex(currentColor, includeAlpha = false)
            hexError = false
        }
        
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = "Select Color",
                    style = MatrixTextStyles.SectionLabel
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
                ) {
                    // Color preview
                    ColorPreview(
                        color = currentColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                    
                    // RGB sliders
                    RgbSliders(
                        color = currentColor,
                        onColorChange = { newColor ->
                            currentColor = newColor
                        }
                    )
                    
                    // Hex input
                    HexInput(
                        hexValue = hexInput,
                        onHexChange = { newHex ->
                            hexInput = newHex
                            val parsedColor = ColorUtils.hexToLong(newHex)
                            if (parsedColor != null) {
                                currentColor = parsedColor
                                hexError = false
                            } else {
                                hexError = true
                            }
                        },
                        hasError = hexError
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onColorSelected(currentColor)
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * A color preview box that shows the currently selected color.
 */
@Composable
private fun ColorPreview(
    color: Long,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(DesignTokens.Radius.md))
            .background(Color(color))
            .border(
                width = DesignTokens.Outline.medium,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(DesignTokens.Radius.md)
            )
    )
}

/**
 * RGB sliders for fine-tuning color components.
 */
@Composable
private fun RgbSliders(
    color: Long,
    onColorChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val (red, green, blue) = ColorUtils.longToRgb(color)
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
    ) {
        // Red slider
        ColorSlider(
            label = "Red",
            value = red,
            onValueChange = { newRed ->
                val newColor = ColorUtils.rgbToLong(
                    ColorUtils.clampRgb(newRed),
                    green,
                    blue
                )
                onColorChange(newColor)
            },
            color = Color.Red
        )
        
        // Green slider
        ColorSlider(
            label = "Green",
            value = green,
            onValueChange = { newGreen ->
                val newColor = ColorUtils.rgbToLong(
                    red,
                    ColorUtils.clampRgb(newGreen),
                    blue
                )
                onColorChange(newColor)
            },
            color = Color.Green
        )
        
        // Blue slider
        ColorSlider(
            label = "Blue",
            value = blue,
            onValueChange = { newBlue ->
                val newColor = ColorUtils.rgbToLong(
                    red,
                    green,
                    ColorUtils.clampRgb(newBlue)
                )
                onColorChange(newColor)
            },
            color = Color.Blue
        )
    }
}

/**
 * A single color slider with label and value display.
 */
@Composable
private fun ColorSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MatrixTextStyles.SliderLabel,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(60.dp)
        )
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color
            )
        )
        
        Text(
            text = (value * 255).toInt().toString(),
            style = MatrixTextStyles.SliderValue,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(40.dp)
        )
    }
}

/**
 * Hex input field for direct color entry.
 */
@Composable
private fun HexInput(
    hexValue: String,
    onHexChange: (String) -> Unit,
    hasError: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.xs)
    ) {
        Text(
            text = "Hex Color",
            style = MatrixTextStyles.SliderLabel,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        OutlinedTextField(
            value = hexValue,
            onValueChange = onHexChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("#00FF00") },
            isError = hasError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = KeyboardType.Text
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (hasError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                unfocusedBorderColor = if (hasError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        )
        
        if (hasError) {
            Text(
                text = "Invalid hex color format",
                style = MatrixTextStyles.HelperText,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
