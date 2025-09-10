package com.example.matrixscreen.ui.settings.characters

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.SymbolSet
import com.example.matrixscreen.data.registry.BuiltInSymbolSets
import com.example.matrixscreen.data.registry.SymbolSetId as RegistrySymbolSetId
import androidx.compose.foundation.clickable
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.settings.model.CHARACTERS_SPECS
import com.example.matrixscreen.ui.settings.model.get
import com.example.matrixscreen.ui.settings.model.FontSize
import com.example.matrixscreen.ui.settings.model.SymbolSetId
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.core.design.scrollableContent

/**
 * Characters settings screen with symbol sets, fonts, and size controls
 */
@Composable
fun CharactersSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onBack: () -> Unit,
    onNavigateToCustomSets: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = uiState.saved
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    SettingsScreenContainer(
        title = "CHARACTERS",
        onBack = onBack,
        ui = ui,
        optimizedSettings = optimizedSettings,
        expanded = true,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sectionSpacing)
            ) {
                // Symbol Set Section
                SettingsSection(
                    title = "Symbol Set",
                    ui = ui,
                    optimizedSettings = optimizedSettings,
                    content = {
                        SymbolSetGrid(
                            currentSettings = currentSettings,
                            settingsViewModel = settingsViewModel,
                            onNavigateToCustomSets = onNavigateToCustomSets,
                            ui = ui,
                            optimizedSettings = optimizedSettings
                        )
                    }
                )
                
                // Font Section
                SettingsSection(
                    title = "Font & Size",
                    ui = ui,
                    optimizedSettings = optimizedSettings,
                    content = {
                        FontSizeControl(
                            currentSize = currentSettings.get(FontSize),
                            onSizeChanged = { 
                                settingsViewModel.updateDraft(FontSize, it)
                                settingsViewModel.commit()
                            },
                            ui = ui,
                            optimizedSettings = optimizedSettings
                        )
                    }
                )
            }
        },
        modifier = modifier
    )
}

/**
 * Symbol set selector with preview and custom sets management
 */
@Composable
private fun SymbolSetGrid(
    currentSettings: MatrixSettings,
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onNavigateToCustomSets: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Built-in sets grid using Column/Row for better performance
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .scrollableContent(),
            verticalArrangement = Arrangement.spacedBy(DesignTokens.Scrolling.gridLineSpacing)
        ) {
            // Create rows of symbol set tiles
            val symbolSets = BuiltInSymbolSets.ALL_BUILT_IN
            val itemsPerRow = 2 // Design token: 2 columns per row
            
            symbolSets.chunked(itemsPerRow).forEach { rowSets ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Scrolling.gridItemSpacing)
                ) {
                    rowSets.forEach { id ->
                        SymbolSetTile(
                            id = id,
                            isSelected = currentSettings.symbolSetId == id.value,
                            onClick = { 
                                settingsViewModel.updateDraft(SymbolSetId, id.value)
                                settingsViewModel.commit() // Immediate commit for symbol sets
                            },
                            ui = ui,
                            optimizedSettings = optimizedSettings,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space if row is not full
                    repeat(itemsPerRow - rowSets.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Custom sets button
        androidx.compose.material3.Button(
            onClick = onNavigateToCustomSets,
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = ui.primary,
                contentColor = ui.textPrimary
            )
        ) {
            ModernTextWithGlow(
                text = "Manage Custom Sets",
                style = AppTypography.labelMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
        }
    }
}

/**
 * Symbol preview showing sample characters
 */
@Composable
private fun SymbolPreview(
    characters: String,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = ui.selectionBackground
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ModernTextWithGlow(
                text = "Preview:",
                style = AppTypography.labelSmall,
                color = ui.textSecondary,
                settings = optimizedSettings
            )
            
            val previewText = characters.take(24)
            ModernTextWithGlow(
                text = previewText,
                style = AppTypography.bodyMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
        }
    }
}

@Composable
private fun SymbolSetTile(
    id: com.example.matrixscreen.data.registry.SymbolSetId,
    isSelected: Boolean,
    onClick: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    val display = when (id.value) {
        "MATRIX_AUTHENTIC" -> SymbolSet.MATRIX_AUTHENTIC.displayName
        "MATRIX_GLITCH" -> SymbolSet.MATRIX_GLITCH.displayName
        "BINARY" -> SymbolSet.BINARY.displayName
        "HEX" -> SymbolSet.HEX.displayName
        "MIXED" -> SymbolSet.MIXED.displayName
        "LATIN" -> SymbolSet.LATIN.displayName
        "KATAKANA" -> SymbolSet.KATAKANA.displayName
        "NUMBERS" -> SymbolSet.NUMBERS.displayName
        "CUSTOM" -> "Custom"
        else -> "Unknown"
    }
    val preview = when (id.value) {
        "MATRIX_AUTHENTIC" -> SymbolSet.MATRIX_AUTHENTIC.characters
        "MATRIX_GLITCH" -> SymbolSet.MATRIX_GLITCH.characters
        "BINARY" -> SymbolSet.BINARY.characters
        "HEX" -> SymbolSet.HEX.characters
        "MIXED" -> SymbolSet.MIXED.characters
        "LATIN" -> SymbolSet.LATIN.characters
        "KATAKANA" -> SymbolSet.KATAKANA.characters
        "NUMBERS" -> SymbolSet.NUMBERS.characters
        "CUSTOM" -> "01"
        else -> SymbolSet.MATRIX_AUTHENTIC.characters
    }.take(18)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) ui.selectionBackground else ui.overlayBackground,
            contentColor = ui.textPrimary
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ModernTextWithGlow(
                text = display,
                style = AppTypography.titleSmall,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
            ModernTextWithGlow(
                text = preview,
                style = AppTypography.bodySmall,
                color = ui.textSecondary,
                settings = optimizedSettings
            )
        }
    }
}

/**
 * Font size control with slider
 */
@Composable
private fun FontSizeControl(
    currentSize: Int,
    onSizeChanged: (Int) -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    val fontSizeSpec = CHARACTERS_SPECS.find { it.id == FontSize }!!
    
    RenderSetting(
        spec = fontSizeSpec,
        value = currentSize,
        onValueChange = onSizeChanged
    )
}
