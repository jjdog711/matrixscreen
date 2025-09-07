package com.example.matrixscreen.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

/**
 * Reusable composable that dismisses the keyboard when tapping outside of text fields.
 * 
 * Usage:
 * ```kotlin
 * TapOutsideToDismissKeyboard {
 *     // Your screen content here
 *     Column {
 *         OutlinedTextField(...)
 *         Button(...)
 *     }
 * }
 * ```
 */
@Composable
fun TapOutsideToDismissKeyboard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus() // dismisses keyboard
                })
            }
    ) {
        content()
    }
}
