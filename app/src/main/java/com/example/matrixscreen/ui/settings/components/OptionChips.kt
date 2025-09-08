package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.ui.theme.MatrixTextStyles

/**
 * A horizontal row of option chips for selection with accessibility support and dropdown fallback.
 * 
 * This component displays a list of selectable chips in a horizontal row for short lists,
 * or falls back to a dropdown for long lists. It includes proper accessibility support
 * and is stateless with hoisted callbacks for state management.
 * 
 * @param label The label text for the option group
 * @param options The list of available options
 * @param selectedOption The currently selected option
 * @param onOptionSelected Callback when an option is selected
 * @param toLabel Function to convert option to display label
 * @param help Optional help text to display
 * @param maxChipsForHorizontal Maximum number of options to show as chips (default: 6)
 * @param modifier Modifier for the component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> OptionChips(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    toLabel: (T) -> String,
    help: String? = null,
    maxChipsForHorizontal: Int = 6,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignTokens.Spacing.md)
            .semantics {
                contentDescription = "$label: ${toLabel(selectedOption)} selected from ${options.size} options"
            }
    ) {
        // Label
        Text(
            text = label,
            style = MatrixTextStyles.SliderLabel,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics {
                contentDescription = label
            }
        )
        
        Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
        
        // Choose between chips and dropdown based on list length
        if (options.size <= maxChipsForHorizontal) {
            // Horizontal chips for short lists
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm),
                contentPadding = PaddingValues(horizontal = DesignTokens.Spacing.xs)
            ) {
                items(options) { option ->
                    val isSelected = option == selectedOption
                    
                    FilterChip(
                        onClick = { onOptionSelected(option) },
                        label = {
                            Text(
                                text = toLabel(option),
                                style = MatrixTextStyles.SliderValue
                            )
                        },
                        selected = isSelected,
                        modifier = Modifier
                            .selectable(
                                selected = isSelected,
                                onClick = { onOptionSelected(option) }
                            )
                            .semantics {
                                contentDescription = "${toLabel(option)}${if (isSelected) ", selected" else ""}"
                            },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        } else {
            // Dropdown for long lists
            var expanded by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = toLabel(selectedOption),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Select option") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .semantics {
                            contentDescription = "Dropdown for $label, currently ${toLabel(selectedOption)}"
                        }
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        val isSelected = option == selectedOption
                        
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = toLabel(option),
                                    style = MatrixTextStyles.SliderValue
                                )
                            },
                            onClick = {
                                onOptionSelected(option)
                                expanded = false
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "${toLabel(option)}${if (isSelected) ", currently selected" else ""}"
                            }
                        )
                    }
                }
            }
        }
        
        // Help text
        help?.let { helpText ->
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.xs))
            Text(
                text = helpText,
                style = MatrixTextStyles.HelperText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.semantics {
                    contentDescription = helpText
                }
            )
        }
    }
}
