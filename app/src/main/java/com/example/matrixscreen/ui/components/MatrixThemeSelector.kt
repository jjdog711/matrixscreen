package com.example.matrixscreen.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.ui.theme.MatrixColorTheme
import com.example.matrixscreen.ui.theme.MatrixColorThemePresets

/**
 * Matrix Theme Preset Selector UI component
 * Displays all 24 theme presets in a horizontal scrollable row
 */
@Composable
fun MatrixThemeSelector(
    currentThemeName: String?,
    onThemeSelected: (MatrixColorTheme) -> Unit
) {
    val listState = rememberLazyListState()
    
    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(MatrixColorThemePresets) { theme ->
            ThemePreviewBox(
                theme = theme,
                isSelected = currentThemeName == theme.name,
                onClick = { onThemeSelected(theme) }
            )
        }
    }
}
