package com.example.matrixscreen.ui.preview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.matrixscreen.BuildConfig
import com.example.matrixscreen.ui.NewSettingsViewModel
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.theme.MatrixScreenTheme

/**
 * Debug harness for testing persistence and ViewModel end-to-end.
 * 
 * This composable provides a simple UI to test the draft/confirm/cancel pattern
 * and verify that settings are properly persisted and loaded.
 * 
 * Only available in DEBUG builds and should not be included in production navigation.
 */
@Composable
fun DebugSettingsHarness(
    modifier: Modifier = Modifier,
    viewModel: NewSettingsViewModel = hiltViewModel(),
    onBackPressed: () -> Unit = {}
) {
    // Only show in debug builds
    if (!BuildConfig.DEBUG) {
        return
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Matrix Screen"
                )
            }
            Text(
                text = "Debug Settings Harness",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
        }
        
        // State display
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Current State",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("Dirty: ${uiState.dirty}")
                Text("Fall Speed: ${uiState.draft.fallSpeed}")
                Text("Column Count: ${uiState.draft.columnCount}")
                Text("Glow Intensity: ${uiState.draft.glowIntensity}")
            }
        }
        
        // Test controls
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Test Controls",
                    style = MaterialTheme.typography.titleMedium
                )
                
                // Fall Speed slider
                Text("Fall Speed: ${uiState.draft.fallSpeed}")
                Slider(
                    value = uiState.draft.fallSpeed,
                    onValueChange = { viewModel.updateDraft(Speed, it) },
                    valueRange = 0.1f..10.0f,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Column Count slider
                Text("Column Count: ${uiState.draft.columnCount}")
                Slider(
                    value = uiState.draft.columnCount.toFloat(),
                    onValueChange = { viewModel.updateDraft(Columns, it.toInt()) },
                    valueRange = 10f..300f,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Glow Intensity slider
                Text("Glow Intensity: ${uiState.draft.glowIntensity}")
                Slider(
                    value = uiState.draft.glowIntensity,
                    onValueChange = { viewModel.updateDraft(Glow, it) },
                    valueRange = 0f..5f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { viewModel.commit() },
                enabled = uiState.dirty,
                modifier = Modifier.weight(1f)
            ) {
                Text("Confirm")
            }
            
            Button(
                onClick = { viewModel.revert() },
                enabled = uiState.dirty,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
        
        Button(
            onClick = { viewModel.resetToDefaults() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset to Defaults")
        }
        
        // Instructions
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("1. Adjust sliders to change draft values")
                Text("2. Notice 'Dirty' flag becomes true")
                Text("3. Click 'Confirm' to persist changes")
                Text("4. Click 'Cancel' to revert to saved state")
                Text("5. Click 'Reset' to restore defaults")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DebugSettingsHarnessPreview() {
    MatrixScreenTheme {
        // Note: This preview won't work with the real ViewModel due to Hilt
        // but shows the UI structure
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Debug Harness Preview\n(Requires Hilt context)")
        }
    }
}
