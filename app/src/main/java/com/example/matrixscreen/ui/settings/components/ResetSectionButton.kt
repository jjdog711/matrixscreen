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
 * A reset section button for resetting settings to defaults.
 * 
 * This component displays a button that can be used to reset
 * a section of settings to their default values.
 * 
 * @param onReset Callback when the reset button is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun ResetSectionButton(
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignTokens.Spacing.md),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = onReset,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = DesignTokens.Outline.thin,
                color = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = "Reset to Defaults",
                style = MatrixTextStyles.ButtonText
            )
        }
    }
}
