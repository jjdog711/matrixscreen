package com.example.matrixscreen.ui.quick

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.NewSettingsViewModel
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.settings.model.specFor
import com.example.matrixscreen.ui.settings.model.SettingCategory.*
import com.example.matrixscreen.ui.settings.model.Speed
import com.example.matrixscreen.ui.settings.model.Columns
import com.example.matrixscreen.ui.settings.model.Glow
import com.example.matrixscreen.ui.settings.model.Jitter
import com.example.matrixscreen.ui.settings.model.BgColor
import com.example.matrixscreen.ui.settings.model.Fps
import com.example.matrixscreen.ui.settings.components.RenderSetting

/**
 * Helper function to render a spec setting with proper type handling
 */
@Composable
private fun renderSpecSetting(
    spec: WidgetSpec<*>,
    settings: MatrixSettings,
    viewModel: NewSettingsViewModel
) {
    // Use typed SettingId access based on the spec's id
    when (spec.id) {
        Speed -> {
            val typedSpec = spec as SliderSpec
            RenderSetting(
                spec = typedSpec,
                value = settings.get(Speed),
                onValueChange = { newValue -> viewModel.updateDraft(Speed, newValue) },
                modifier = Modifier.width(120.dp)
            )
        }
        Columns -> {
            val typedSpec = spec as IntSliderSpec
            RenderSetting(
                spec = typedSpec,
                value = settings.get(Columns),
                onValueChange = { newValue -> viewModel.updateDraft(Columns, newValue) },
                modifier = Modifier.width(120.dp)
            )
        }
        Glow -> {
            val typedSpec = spec as SliderSpec
            RenderSetting(
                spec = typedSpec,
                value = settings.get(Glow),
                onValueChange = { newValue -> viewModel.updateDraft(Glow, newValue) },
                modifier = Modifier.width(120.dp)
            )
        }
        Jitter -> {
            val typedSpec = spec as SliderSpec
            RenderSetting(
                spec = typedSpec,
                value = settings.get(Jitter),
                onValueChange = { newValue -> viewModel.updateDraft(Jitter, newValue) },
                modifier = Modifier.width(120.dp)
            )
        }
        BgColor -> {
            val typedSpec = spec as ColorSpec
            RenderSetting(
                spec = typedSpec,
                value = settings.get(BgColor),
                onValueChange = { newValue -> viewModel.updateDraft(BgColor, newValue) },
                modifier = Modifier.width(120.dp)
            )
        }
        Fps -> {
            val typedSpec = spec as SelectSpec<Int>
            RenderSetting(
                spec = typedSpec,
                value = settings.get(Fps),
                onValueChange = { newValue -> viewModel.updateDraft(Fps, newValue) },
                modifier = Modifier.width(120.dp)
            )
        }
        else -> {
            // Handle other SettingId types that might be added to QuickPanelSpecs in the future
            throw IllegalArgumentException("Unsupported SettingId in QuickSettingsPanel: ${spec.id::class.simpleName}")
        }
    }
}

@Composable
fun QuickSettingsPanel(
    viewModel: NewSettingsViewModel,
    specs: List<WidgetSpec<*>> = QuickPanelSpecs.SPECS,
    onOpenAdvanced: (SettingCategory) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings = uiState.draft

    Surface(tonalElevation = 4.dp) {
        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                        items(specs, key = { it.id.key }) { spec ->
                            renderSpecSetting(spec, settings, viewModel)
                        }
            }

            Spacer(Modifier.height(4.dp)); Divider()
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { onOpenAdvanced(MOTION) }) { Text("Motion") }
                TextButton(onClick = { onOpenAdvanced(EFFECTS) }) { Text("Effects") }
                TextButton(onClick = { onOpenAdvanced(THEME) }) { Text("Theme") }
                TextButton(onClick = { onOpenAdvanced(BACKGROUND) }) { Text("Background") }
            }
        }
    }
}

