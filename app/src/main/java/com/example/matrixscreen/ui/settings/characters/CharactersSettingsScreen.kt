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
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.settings.model.get
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow

/**
 * Characters settings screen with symbol sets, fonts, and size controls
 */
@Composable
fun CharactersSettingsScreen(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onBack: () -> Unit,
    onNavigateToCustomSets: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = uiState.draft
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    SettingsScreenContainer(
        title = "CHARACTERS",
        onBack = onBack,
        ui = ui,
        optimizedSettings = optimizedSettings,
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
                SymbolSetSelector(
                    currentSet = SymbolSet.MATRIX_AUTHENTIC, // TODO: Add symbolSet to domain model
                    onSetChanged = { /* TODO: Implement symbol set updates */ },
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
                    onSizeChanged = { settingsViewModel.updateDraft(FontSize, it) },
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
private fun SymbolSetSelector(
    currentSet: SymbolSet,
    onSetChanged: (SymbolSet) -> Unit,
    onNavigateToCustomSets: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Current symbol set display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ModernTextWithGlow(
                text = "Current Set",
                style = AppTypography.titleMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
            
            ModernTextWithGlow(
                text = currentSet.displayName,
                style = AppTypography.bodyMedium,
                color = ui.textSecondary,
                settings = optimizedSettings
            )
        }
        
        // Symbol preview
        SymbolPreview(
            symbolSet = currentSet,
            ui = ui,
            optimizedSettings = optimizedSettings
        )
        
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
    symbolSet: SymbolSet,
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
            
            // Show first 20 characters of the symbol set
            val previewText = symbolSet.characters.take(20)
            ModernTextWithGlow(
                text = previewText,
                style = AppTypography.bodyMedium,
                color = ui.textPrimary,
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
