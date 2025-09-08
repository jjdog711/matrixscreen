package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.matrixscreen.core.design.DesignTokens

/**
 * A Material 3 surface wrapper for settings sections.
 * 
 * This component provides a consistent card-based container for grouping
 * related settings. It uses trailing lambdas for content composition
 * and follows Material 3 design guidelines.
 * 
 * @param modifier Modifier for the component
 * @param content The content to display within the section
 */
@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignTokens.Elevation.card
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(DesignTokens.Radius.card)
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.Spacing.sectionPadding),
            content = content
        )
    }
}
