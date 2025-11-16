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
import java.util.Locale
import kotlin.math.roundToInt

/**
 * A labeled slider component for floating-point values.
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
fun LabeledSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
    step: Float,
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
                text = formatSliderValue(
                    value = value,
                    step = step,
                    unit = unit,
                    range = range,
                    includeUnit = true
                ),
                style = MatrixTextStyles.SliderValue,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End
            )
        }
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        
        // Slider
        val sliderSteps = remember(range.start, range.endInclusive, step) {
            calculateSliderSteps(range, step)
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = sliderSteps,
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
                text = formatSliderValue(
                    value = range.start,
                    step = step,
                    unit = unit,
                    range = range,
                    includeUnit = unit == "%"
                ),
                style = MatrixTextStyles.SliderRangeLabel,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatSliderValue(
                    value = range.endInclusive,
                    step = step,
                    unit = unit,
                    range = range,
                    includeUnit = unit == "%"
                ),
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

private fun calculateSliderSteps(
    range: ClosedFloatingPointRange<Float>,
    step: Float
): Int {
    if (step <= 0f || range.endInclusive <= range.start) return 0
    val intervals = ((range.endInclusive - range.start) / step).roundToInt().coerceAtLeast(0)
    return (intervals - 1).coerceAtLeast(0)
}

private fun formatSliderValue(
    value: Float,
    step: Float,
    unit: String?,
    range: ClosedFloatingPointRange<Float>,
    includeUnit: Boolean
): String {
    val isPercentRange = unit == "%" && range.start >= 0f && range.endInclusive <= 1f
    return if (isPercentRange) {
        val percentValue = (value * 100f).roundToInt()
        if (includeUnit) "$percentValue%" else percentValue.toString()
    } else {
        val decimals = when {
            step < 0.01f -> 3
            step < 0.1f -> 2
            step < 1f -> 1
            else -> 0
        }
        val formatted = if (decimals == 0) {
            value.roundToInt().toString()
        } else {
            String.format(Locale.US, "%.${decimals}f", value)
        }
        if (includeUnit && !unit.isNullOrBlank()) "$formatted ${unit.trim()}" else formatted
    }
}
