package com.example.matrixscreen.ui.settings.components

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.ui.theme.MatrixTextStyles

/**
 * A color control row component for color selection.
 * 
 * This component displays a label and color swatch that can be clicked
 * to open a color picker. It's stateless and uses hoisted callbacks.
 * 
 * @param label The label text for the color control
 * @param color The current color value (as Long ARGB)
 * @param onColorChange Callback when the color changes
 * @param help Optional help text to display
 * @param modifier Modifier for the component
 */
@Composable
fun ColorControlRow(
    label: String,
    color: Long,
    onColorChange: (Long) -> Unit,
    help: String? = null,
    modifier: Modifier = Modifier
) {
    var showColorPicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignTokens.Spacing.md)
    ) {
        // Main row with label and color swatch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MatrixTextStyles.SliderLabel,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            
            // Color swatch
            Box(
                modifier = Modifier
                    .size(DesignTokens.Sizing.colorSwatchSize)
                    .clip(RoundedCornerShape(DesignTokens.Radius.sm))
                    .background(Color(color))
                    .border(
                        width = DesignTokens.Outline.thin,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(DesignTokens.Radius.sm)
                    )
                    .clickable {
                        showColorPicker = true
                    }
            )
        }
        
        // Help text
        help?.let { helpText ->
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
            Text(
                text = helpText,
                style = MatrixTextStyles.HelperText,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    // Color picker dialog
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
