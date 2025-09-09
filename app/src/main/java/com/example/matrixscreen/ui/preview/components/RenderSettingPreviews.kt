package com.example.matrixscreen.ui.preview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.theme.MatrixScreenTheme
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings

/**
 * Preview data for RenderSetting demonstrations.
 */

// Sample WidgetSpecs for previews
val SampleSliderSpec = SliderSpec(
    id = Speed,
    label = "Fall Speed",
    range = 0.5f..5.0f,
    step = 0.1f,
    default = 2.0f,
    unit = "units/sec",
    affectsPerf = true,
    help = "Controls how fast the matrix rain falls"
)

val SampleIntSliderSpec = IntSliderSpec(
    id = Columns,
    label = "Column Count",
    range = 50..300,
    step = 10,
    default = 150,
    unit = "columns",
    affectsPerf = true,
    help = "Number of matrix columns to display"
)

val SampleToggleSpec = ToggleSpec(
    id = TestBooleanSetting,
    label = "Enable Effects",
    default = true,
    help = "Toggle visual effects on/off"
)

val SampleColorSpec = ColorSpec(
    id = HeadColor,
    label = "Head Color",
    help = "Color of the leading character in each column"
)

val SampleSelectSpec = SelectSpec(
    id = Fps,
    label = "Target FPS",
    options = listOf(30, 60, 90, 120),
    toLabel = { "$it FPS" },
    default = 60,
    help = "Target frame rate for the matrix animation"
)

/**
 * Preview showing individual RenderSetting components.
 */
@Preview(showBackground = true)
@Composable
fun RenderSettingPreview() {
    MatrixScreenTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Slider example
            RenderSetting(
                spec = SampleSliderSpec,
                value = 2.5f,
                onValueChange = { }
            )
            
            // IntSlider example
            RenderSetting(
                spec = SampleIntSliderSpec,
                value = 200,
                onValueChange = { }
            )
            
            // Toggle example
            RenderSetting(
                spec = SampleToggleSpec,
                value = true,
                onValueChange = { }
            )
            
            // Color example
            RenderSetting(
                spec = SampleColorSpec,
                value = 0xFF00FF00L,
                onValueChange = { }
            )
            
            // Select example
            RenderSetting(
                spec = SampleSelectSpec,
                value = 60,
                onValueChange = { }
            )
        }
    }
}

/**
 * Preview showing SettingsSection wrapper with trailing lambdas.
 */
@Preview(showBackground = true)
@Composable
fun SettingsSectionPreview() {
    MatrixScreenTheme {
        SettingsSection {
            Text(
                text = "Motion Settings",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            RenderSetting(
                spec = SampleSliderSpec,
                value = 2.5f,
                onValueChange = { }
            )
            
            RenderSetting(
                spec = SampleIntSliderSpec,
                value = 200,
                onValueChange = { }
            )
        }
    }
}

/**
 * Preview showing SettingsScreenContainer with multiple sections.
 */
@Preview(showBackground = true)
@Composable
fun SettingsScreenContainerPreview() {
    MatrixScreenTheme {
        val defaultSettings = MatrixSettings.DEFAULT
        val ui = getSafeUIColorScheme(defaultSettings)
        val optimizedSettings = rememberOptimizedSettings(defaultSettings)
        
        SettingsScreenContainer(
            title = "Preview",
            onBack = { },
            ui = ui,
            optimizedSettings = optimizedSettings,
            content = {
            // Motion Section
            SettingsSection(
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                Text(
                    text = "Motion Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                RenderSetting(
                    spec = SampleSliderSpec,
                    value = 2.5f,
                    onValueChange = { }
                )
                
                RenderSetting(
                    spec = SampleIntSliderSpec,
                    value = 200,
                    onValueChange = { }
                )
            }
            
            // Effects Section
            SettingsSection(
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                Text(
                    text = "Effects Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                RenderSetting(
                    spec = SampleToggleSpec,
                    value = true,
                    onValueChange = { }
                )
                
                RenderSetting(
                    spec = SampleColorSpec,
                    value = 0xFF00FF00L,
                    onValueChange = { }
                )
            }
            
            // Performance Section
            SettingsSection(
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                Text(
                    text = "Performance Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                RenderSetting(
                    spec = SampleSelectSpec,
                    value = 60,
                    onValueChange = { }
                )
            }
            }
        )
    }
}

/**
 * Preview showing interactive RenderSetting with state management.
 */
@Preview(showBackground = true)
@Composable
fun InteractiveRenderSettingPreview() {
    MatrixScreenTheme {
        var sliderValue by remember { mutableStateOf(2.5f) }
        var intSliderValue by remember { mutableStateOf(200) }
        var toggleValue by remember { mutableStateOf(true) }
        var colorValue by remember { mutableStateOf(0xFF00FF00L) }
        var selectValue by remember { mutableStateOf(60) }
        
        val defaultSettings = MatrixSettings.DEFAULT
        val ui = getSafeUIColorScheme(defaultSettings)
        val optimizedSettings = rememberOptimizedSettings(defaultSettings)
        
        SettingsScreenContainer(
            title = "Interactive Preview",
            onBack = { },
            ui = ui,
            optimizedSettings = optimizedSettings,
            content = {
            SettingsSection(
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                Text(
                    text = "Interactive Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                RenderSetting(
                    spec = SampleSliderSpec,
                    value = sliderValue,
                    onValueChange = { sliderValue = it }
                )
                
                RenderSetting(
                    spec = SampleIntSliderSpec,
                    value = intSliderValue,
                    onValueChange = { intSliderValue = it }
                )
                
                RenderSetting(
                    spec = SampleToggleSpec,
                    value = toggleValue,
                    onValueChange = { toggleValue = it }
                )
                
                RenderSetting(
                    spec = SampleColorSpec,
                    value = colorValue,
                    onValueChange = { colorValue = it }
                )
                
                RenderSetting(
                    spec = SampleSelectSpec,
                    value = selectValue,
                    onValueChange = { selectValue = it }
                )
            }
            }
        )
    }
}
