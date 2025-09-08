package com.example.matrixscreen.ui.preview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.ui.settings.components.ColorControlRow
import com.example.matrixscreen.ui.settings.components.ColorPickerDialog
import com.example.matrixscreen.ui.theme.MatrixScreenTheme

/**
 * Preview for the ColorControlRow component with color picker integration.
 */
@Preview(showBackground = true)
@Composable
fun ColorControlRowWithPickerPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(DesignTokens.Spacing.md),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
            ) {
                // Matrix green color control
                ColorControlRow(
                    label = "Matrix Green",
                    color = 0xFF00FF00L,
                    onColorChange = { /* Preview only */ },
                    help = "The primary color for Matrix rain characters"
                )
                
                // Background color control
                ColorControlRow(
                    label = "Background",
                    color = 0xFF000000L,
                    onColorChange = { /* Preview only */ },
                    help = "The background color behind the Matrix rain"
                )
                
                // Trail color control
                ColorControlRow(
                    label = "Trail Color",
                    color = 0xFF008800L,
                    onColorChange = { /* Preview only */ },
                    help = "The color of the trailing characters"
                )
            }
        }
    }
}

/**
 * Interactive preview for the ColorControlRow component.
 * This preview demonstrates the color picker dialog functionality.
 */
@Preview(showBackground = true)
@Composable
fun InteractiveColorControlRowPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(DesignTokens.Spacing.md),
            color = MaterialTheme.colorScheme.background
        ) {
            var selectedColor by remember { mutableStateOf(0xFF00FF00L) }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
            ) {
                Text(
                    text = "Interactive Color Picker Preview",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Click the color swatch to open the color picker dialog",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Interactive color control
                ColorControlRow(
                    label = "Selected Color",
                    color = selectedColor,
                    onColorChange = { newColor ->
                        selectedColor = newColor
                    },
                    help = "Click the color swatch to open the color picker"
                )
                
                // Display current color info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(DesignTokens.Spacing.md)
                    ) {
                        Text(
                            text = "Current Color Information:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                        Text(
                            text = "ARGB: ${String.format("0x%08X", selectedColor)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Hex: #${String.format("%06X", selectedColor and 0xFFFFFF)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Preview for the ColorPickerDialog component.
 * This preview shows the dialog in isolation for testing purposes.
 */
@Preview(showBackground = true)
@Composable
fun ColorPickerDialogPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(DesignTokens.Spacing.md),
            color = MaterialTheme.colorScheme.background
        ) {
            var showDialog by remember { mutableStateOf(true) }
            var selectedColor by remember { mutableStateOf(0xFF00FF00L) }
            
            Column {
                Text(
                    text = "Color Picker Dialog Preview",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
                
                Button(
                    onClick = { showDialog = true }
                ) {
                    Text("Open Color Picker")
                }
                
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
                
                // Display selected color
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(DesignTokens.Spacing.md)
                    ) {
                        Text(
                            text = "Selected Color:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                        Text(
                            text = "ARGB: ${String.format("0x%08X", selectedColor)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Hex: #${String.format("%06X", selectedColor and 0xFFFFFF)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Color picker dialog
            ColorPickerDialog(
                isOpen = showDialog,
                initialColor = selectedColor,
                onColorSelected = { newColor ->
                    selectedColor = newColor
                },
                onDismiss = {
                    showDialog = false
                }
            )
        }
    }
}

/**
 * Preview demonstrating multiple color controls for theme settings.
 */
@Preview(showBackground = true)
@Composable
fun ThemeColorControlsPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(DesignTokens.Spacing.md),
            color = MaterialTheme.colorScheme.background
        ) {
            var headColor by remember { mutableStateOf(0xFF00FF00L) }
            var trailColor by remember { mutableStateOf(0xFF008800L) }
            var dimColor by remember { mutableStateOf(0xFF004400L) }
            var bgColor by remember { mutableStateOf(0xFF000000L) }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
            ) {
                Text(
                    text = "Theme Color Controls",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Configure the Matrix rain color scheme",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                
                // Head color
                ColorControlRow(
                    label = "Head Color",
                    color = headColor,
                    onColorChange = { headColor = it },
                    help = "The brightest color for the leading characters"
                )
                
                // Trail color
                ColorControlRow(
                    label = "Trail Color",
                    color = trailColor,
                    onColorChange = { trailColor = it },
                    help = "The color for trailing characters"
                )
                
                // Dim color
                ColorControlRow(
                    label = "Dim Color",
                    color = dimColor,
                    onColorChange = { dimColor = it },
                    help = "The dimmest color for fading characters"
                )
                
                // Background color
                ColorControlRow(
                    label = "Background",
                    color = bgColor,
                    onColorChange = { bgColor = it },
                    help = "The background color behind the rain"
                )
            }
        }
    }
}
