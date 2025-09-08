package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.ui.theme.MatrixTextStyles

/**
 * A labeled switch component for boolean values.
 * 
 * This component displays a label and switch control.
 * It's stateless and uses hoisted callbacks for state management.
 * 
 * @param label The label text for the switch
 * @param checked The current state of the switch
 * @param onCheckedChange Callback when the state changes
 * @param help Optional help text to display
 * @param modifier Modifier for the component
 */
@Composable
fun LabeledSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    help: String? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignTokens.Spacing.md)
    ) {
        // Main row with label and switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MatrixTextStyles.SliderLabel,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
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
