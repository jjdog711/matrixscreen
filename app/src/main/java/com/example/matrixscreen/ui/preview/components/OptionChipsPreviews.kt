package com.example.matrixscreen.ui.preview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.ui.settings.components.OptionChips
import com.example.matrixscreen.ui.theme.MatrixScreenTheme

/**
 * Preview for the OptionChips component with accessibility and dropdown refinements.
 */

// FPS options as specified in requirements
val FpsOptions = listOf(15, 30, 45, 60, 90, 120)

// Short list for chips demonstration
val ShortOptions = listOf("Low", "Medium", "High")

// Long list for dropdown demonstration
val LongOptions = listOf(
    "Option 1", "Option 2", "Option 3", "Option 4", "Option 5",
    "Option 6", "Option 7", "Option 8", "Option 9", "Option 10",
    "Option 11", "Option 12", "Option 13", "Option 14", "Option 15"
)

/**
 * Preview for FPS selection with chips (short list).
 */
@Preview(showBackground = true)
@Composable
fun FpsSelectionChipsPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(DesignTokens.Spacing.md),
            color = MaterialTheme.colorScheme.background
        ) {
            var selectedFps by remember { mutableStateOf(60) }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
            ) {
                Text(
                    text = "FPS Selection (Chips)",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Short list displays as horizontal chips with accessibility support",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OptionChips(
                    label = "Target FPS",
                    options = FpsOptions,
                    selectedOption = selectedFps,
                    onOptionSelected = { selectedFps = it },
                    toLabel = { "$it FPS" },
                    help = "Select the target frame rate for the Matrix rain animation"
                )
                
                // Display current selection
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
                            text = "Current Selection:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                        Text(
                            text = "$selectedFps FPS",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Preview for short list with chips.
 */
@Preview(showBackground = true)
@Composable
fun ShortListChipsPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(DesignTokens.Spacing.md),
            color = MaterialTheme.colorScheme.background
        ) {
            var selectedOption by remember { mutableStateOf("Medium") }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
            ) {
                Text(
                    text = "Short List (Chips)",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                OptionChips(
                    label = "Quality Level",
                    options = ShortOptions,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it },
                    toLabel = { it },
                    help = "Choose the quality level for the effect"
                )
            }
        }
    }
}

/**
 * Preview for long list with dropdown fallback.
 */
@Preview(showBackground = true)
@Composable
fun LongListDropdownPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(DesignTokens.Spacing.md),
            color = MaterialTheme.colorScheme.background
        ) {
            var selectedOption by remember { mutableStateOf("Option 5") }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
            ) {
                Text(
                    text = "Long List (Dropdown)",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Long lists automatically use dropdown for better UX",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OptionChips(
                    label = "Select Option",
                    options = LongOptions,
                    selectedOption = selectedOption,
                    onOptionSelected = { selectedOption = it },
                    toLabel = { it },
                    help = "Choose from a long list of options",
                    maxChipsForHorizontal = 6
                )
                
                // Display current selection
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
                            text = "Current Selection:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                        Text(
                            text = selectedOption,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

/**
 * Preview demonstrating both chips and dropdown in the same screen.
 */
@Preview(showBackground = true)
@Composable
fun MixedOptionChipsPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(DesignTokens.Spacing.md),
            color = MaterialTheme.colorScheme.background
        ) {
            var selectedFps by remember { mutableStateOf(60) }
            var selectedQuality by remember { mutableStateOf("Medium") }
            var selectedLongOption by remember { mutableStateOf("Option 3") }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
            ) {
                Text(
                    text = "Mixed Option Types",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Demonstrates both chips and dropdown based on list length",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // FPS selection (chips)
                OptionChips(
                    label = "Target FPS",
                    options = FpsOptions,
                    selectedOption = selectedFps,
                    onOptionSelected = { selectedFps = it },
                    toLabel = { "$it FPS" },
                    help = "Frame rate for Matrix rain animation"
                )
                
                // Quality selection (chips)
                OptionChips(
                    label = "Quality Level",
                    options = ShortOptions,
                    selectedOption = selectedQuality,
                    onOptionSelected = { selectedQuality = it },
                    toLabel = { it },
                    help = "Visual quality setting"
                )
                
                // Long list (dropdown)
                OptionChips(
                    label = "Advanced Option",
                    options = LongOptions,
                    selectedOption = selectedLongOption,
                    onOptionSelected = { selectedLongOption = it },
                    toLabel = { it },
                    help = "Choose from many advanced options",
                    maxChipsForHorizontal = 6
                )
                
                // Summary card
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
                            text = "Current Selections:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                        Text(
                            text = "FPS: $selectedFps",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Quality: $selectedQuality",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Advanced: $selectedLongOption",
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
 * Preview for accessibility testing with different states.
 */
@Preview(showBackground = true)
@Composable
fun AccessibilityStatesPreview() {
    MatrixScreenTheme {
        Surface(
            modifier = Modifier.padding(DesignTokens.Spacing.md),
            color = MaterialTheme.colorScheme.background
        ) {
            var selectedFps by remember { mutableStateOf(60) }
            
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
            ) {
                Text(
                    text = "Accessibility States",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Text(
                    text = "Test accessibility with screen readers and keyboard navigation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OptionChips(
                    label = "Target FPS",
                    options = FpsOptions,
                    selectedOption = selectedFps,
                    onOptionSelected = { selectedFps = it },
                    toLabel = { "$it FPS" },
                    help = "Select frame rate - accessible with screen readers and keyboard navigation"
                )
                
                // Instructions for accessibility testing
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(DesignTokens.Spacing.md)
                    ) {
                        Text(
                            text = "Accessibility Features:",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                        Text(
                            text = "• Content descriptions for screen readers",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "• Selection state announcements",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "• Keyboard navigation support",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "• Focus indicators",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}
