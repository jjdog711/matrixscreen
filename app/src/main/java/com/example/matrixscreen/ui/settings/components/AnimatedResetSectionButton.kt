package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.*

/**
 * Animated reset section button with subtle micro-interactions.
 * 
 * Provides the same functionality as ResetSectionButton but with
 * tasteful animations that enhance user experience.
 */

/**
 * An animated reset button for settings sections.
 * 
 * @param onReset Callback when the reset button is clicked
 * @param modifier Modifier for the component
 */
@Composable
fun AnimatedResetSectionButton(
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    var hasReset by remember { mutableStateOf(false) }
    
    // Track reset action for success animation
    LaunchedEffect(Unit) {
        if (hasReset) {
            kotlinx.coroutines.delay(300)
            hasReset = false
        }
    }
    
    OutlinedButton(
        onClick = {
            onReset()
            hasReset = true
        },
        modifier = modifier
            .fillMaxWidth()
            .interactiveScale(isPressed)
            .successBounce(if (hasReset) Unit else null),
        interactionSource = interactionSource
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Reset to Defaults")
        }
    }
}
