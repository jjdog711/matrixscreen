package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.ui.theme.MatrixTextStyles

/**
 * A labeled slider component for integer values.
 * 
 * This component displays a label, slider control, and current value.
 * It's stateless and uses hoisted callbacks for state management.
 * 
 * @param label The label text for the slider
 * @param value The current value of the slider
 * @param onValueChange Callback when the value changes
 * @param range The range of valid values
 * @param step The step size for the slider
 * @param unit Optional unit string to display with the value
 * @param affectsPerf Whether this setting affects performance (shows warning)
 * @param help Optional help text to display
 * @param modifier Modifier for the component
 */
@Composable
fun LabeledIntSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    step: Int = 1,
    unit: String? = null,
    affectsPerf: Boolean = false,
    help: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignTokens.Spacing.md)
    ) {
        // Label row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MatrixTextStyles.SliderLabel,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Value display
            Text(
                text = buildString {
                    append(value.toString())
                    unit?.let { append(" $it") }
                },
                style = MatrixTextStyles.SliderValue,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End
            )
        }
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        
        // Slider
        Slider(
            value = value.toFloat(),
            onValueChange = { newValue ->
                val steppedValue = ((newValue - range.first) / step) * step + range.first
                onValueChange(steppedValue.toInt().coerceIn(range))
            },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = (range.last - range.first) / step - 1,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.outline
            )
        )
        
        // Range labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = range.first.toString(),
                style = MatrixTextStyles.SliderRangeLabel,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = range.last.toString(),
                style = MatrixTextStyles.SliderRangeLabel,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Performance warning
        if (affectsPerf) {
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
            Text(
                text = "⚠️ May affect performance",
                style = MatrixTextStyles.HelperText,
                color = MaterialTheme.colorScheme.error
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
}
