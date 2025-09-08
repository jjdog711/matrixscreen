package com.example.matrixscreen.ui.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.theme.MatrixUIColorScheme

/**
 * Generic function that maps WidgetSpec<T> to appropriate base components.
 * 
 * This is the heart of the spec-driven UI system, enabling UI generation
 * from data specifications. Each WidgetSpec type is mapped to its corresponding
 * base UI component.
 * 
 * @param spec The WidgetSpec that describes the setting
 * @param value The current value of the setting
 * @param onValueChange Callback when the value changes
 * @param modifier Modifier for the component
 */
@Composable
fun <T : Any> RenderSetting(
    spec: WidgetSpec<T>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    when (spec) {
        is SliderSpec -> {
            LabeledSlider(
                label = spec.label,
                value = value as Float,
                onValueChange = onValueChange as (Float) -> Unit,
                range = spec.range,
                step = spec.step,
                unit = spec.unit,
                affectsPerf = spec.affectsPerf,
                help = spec.help,
                modifier = modifier
            )
        }
        
        is IntSliderSpec -> {
            LabeledIntSlider(
                label = spec.label,
                value = value as Int,
                onValueChange = onValueChange as (Int) -> Unit,
                range = spec.range,
                step = spec.step,
                unit = spec.unit,
                affectsPerf = spec.affectsPerf,
                help = spec.help,
                modifier = modifier
            )
        }
        
        is ToggleSpec -> {
            LabeledSwitch(
                label = spec.label,
                checked = value as Boolean,
                onCheckedChange = onValueChange as (Boolean) -> Unit,
                help = spec.help,
                modifier = modifier
            )
        }
        
        is BooleanSpec -> {
            LabeledSwitch(
                label = spec.label,
                checked = value as Boolean,
                onCheckedChange = onValueChange as (Boolean) -> Unit,
                help = spec.help,
                modifier = modifier
            )
        }
        
        is ColorSpec -> {
            ColorControlRow(
                label = spec.label,
                color = value as Long,
                onColorChange = onValueChange as (Long) -> Unit,
                help = spec.help,
                modifier = modifier
            )
        }
        
        is SelectSpec<T> -> {
            OptionChips(
                label = spec.label,
                options = spec.options,
                selectedOption = value,
                onOptionSelected = onValueChange,
                toLabel = spec.toLabel,
                help = spec.help,
                modifier = modifier
            )
        }
    }
}
