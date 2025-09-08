package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.matrixscreen.core.design.DesignTokens

/**
 * A Material 3 surface wrapper for settings screens.
 * 
 * This component provides a consistent container for entire settings screens,
 * with proper scrolling behavior and padding. It uses trailing lambdas for
 * content composition and follows Material 3 design guidelines.
 * 
 * @param modifier Modifier for the component
 * @param content The content to display within the screen container
 */
@Composable
fun SettingsScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(DesignTokens.Spacing.screenPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg),
            content = content
        )
    }
}
