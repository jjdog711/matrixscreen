package com.example.matrixscreen.ui.preview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.theme.MatrixScreenTheme

/**
 * Preview components for the base UI components.
 * 
 * These previews demonstrate how the components look and behave
 * with sample data and different states.
 */

@Preview(showBackground = true)
@Composable
fun LabeledSliderPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            var value by remember { mutableStateOf(2.5f) }
            
            LabeledSlider(
                label = "Fall Speed",
                value = value,
                onValueChange = { value = it },
                range = 0.1f..10.0f,
                step = 0.1f,
                unit = "units/sec",
                affectsPerf = true,
                help = "Controls how fast the rain falls"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LabeledIntSliderPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            var value by remember { mutableStateOf(200) }
            
            LabeledIntSlider(
                label = "Column Count",
                value = value,
                onValueChange = { value = it },
                range = 10..500,
                step = 5,
                unit = "columns",
                affectsPerf = true,
                help = "Number of rain columns"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LabeledSwitchPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            var checked by remember { mutableStateOf(true) }
            
            LabeledSwitch(
                label = "Enable Feature",
                checked = checked,
                onCheckedChange = { checked = it },
                help = "Turn this feature on or off"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ColorControlRowPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            var color by remember { mutableStateOf(0xFF00FF00L) }
            
            ColorControlRow(
                label = "Background Color",
                color = color,
                onColorChange = { color = it },
                help = "Choose the background color"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OptionChipsPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            var selectedFps by remember { mutableStateOf(90) }
            
            OptionChips(
                label = "Target FPS",
                options = FakeFpsOptions,
                selectedOption = selectedFps,
                onOptionSelected = { selectedFps = it },
                toLabel = { "$it FPS" },
                help = "Choose the target frame rate"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetSectionButtonPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            ResetSectionButton(
                onReset = { /* Reset logic */ }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AllComponentsPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var sliderValue by remember { mutableStateOf(2.5f) }
                var intSliderValue by remember { mutableStateOf(200) }
                var switchChecked by remember { mutableStateOf(true) }
                var colorValue by remember { mutableStateOf(0xFF00FF00L) }
                var selectedFps by remember { mutableStateOf(90) }
                
                LabeledSlider(
                    label = "Fall Speed",
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    range = 0.1f..10.0f,
                    step = 0.1f,
                    unit = "units/sec",
                    affectsPerf = true
                )
                
                LabeledIntSlider(
                    label = "Column Count",
                    value = intSliderValue,
                    onValueChange = { intSliderValue = it },
                    range = 10..500,
                    step = 5,
                    unit = "columns",
                    affectsPerf = true
                )
                
                LabeledSwitch(
                    label = "Enable Feature",
                    checked = switchChecked,
                    onCheckedChange = { switchChecked = it }
                )
                
                ColorControlRow(
                    label = "Background Color",
                    color = colorValue,
                    onColorChange = { colorValue = it }
                )
                
                OptionChips(
                    label = "Target FPS",
                    options = FakeFpsOptions,
                    selectedOption = selectedFps,
                    onOptionSelected = { selectedFps = it },
                    toLabel = { "$it FPS" }
                )
                
                ResetSectionButton(
                    onReset = { /* Reset logic */ }
                )
            }
        }
    }
}
