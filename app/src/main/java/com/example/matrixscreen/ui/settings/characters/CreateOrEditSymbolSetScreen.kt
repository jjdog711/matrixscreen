package com.example.matrixscreen.ui.settings.characters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.theme.FontUtils
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.MatrixTextStyles
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.core.design.DesignTokens
import kotlin.random.Random
import com.example.matrixscreen.ui.components.*

/**
 * Live preview of character pools
 */
@Composable
private fun PoolsPreview(
    characters: String,
    selectedFont: String,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    val pools = parsePoolsForPreview(characters)

    Column(
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm),
        modifier = Modifier.padding(top = DesignTokens.Spacing.sm)
    ) {
        com.example.matrixscreen.ui.theme.ModernTextWithGlow(
            text = "POOL PREVIEW",
            style = AppTypography.labelMedium,
            color = ui.textAccent,
            settings = optimizedSettings
        )

        pools.forEachIndexed { index, pool ->
            Column(
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.xs)
            ) {
                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                    text = "Pool ${index + 1}:",
                    style = AppTypography.labelSmall,
                    color = ui.textPrimary,
                    settings = optimizedSettings
                )

                // Show sample characters from this pool
                val sample = pool.take(8)
                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                    text = sample,
                    style = if (selectedFont == "matrix_code_nfi.ttf") {
                        MatrixTextStyles.MatrixSymbolPreview
                    } else {
                        AppTypography.bodyLarge
                    },
                    color = ui.textAccent,
                    settings = optimizedSettings
                )
            }
        }

        if (pools.size > 1) {
            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                text = "Columns will cycle through these pools",
                style = AppTypography.labelSmall,
                color = ui.textSecondary,
                settings = optimizedSettings,
                modifier = Modifier.padding(top = DesignTokens.Spacing.xs)
            )
        }
    }
}

/**
 * Parse character pools for preview, similar to renderer's logic
 */
private fun parsePoolsForPreview(characters: String): List<String> {
    if (characters.isEmpty()) return emptyList()

    val pools = characters.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    return if (pools.isEmpty()) {
        listOf(characters)
    } else {
        pools
    }
}


/**
 * Screen for creating or editing a custom symbol set
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditSymbolSetScreen(
    viewModel: CustomSymbolSetViewModel,
    onBackPressed: () -> Unit,
    onDelete: (() -> Unit)? = null,
    existingSet: CustomSymbolSet? = null,
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel? = null
) {
    val scrollState = rememberScrollState()
    val isEditing = existingSet != null
    
    // Form state
    var name by remember { mutableStateOf(existingSet?.name ?: "") }
    var characters by remember { mutableStateOf(existingSet?.characters ?: "") }
    var selectedFont by remember { mutableStateOf(existingSet?.fontFileName ?: "matrix_code_nfi.ttf") }
    var nameError by remember { mutableStateOf("") }
    var charactersError by remember { mutableStateOf("") }
    
    // Get UI state for theming
    val settingsUiState by (settingsViewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(com.example.matrixscreen.ui.SettingsUiState(
        saved = com.example.matrixscreen.data.model.MatrixSettings.DEFAULT,
        draft = com.example.matrixscreen.data.model.MatrixSettings.DEFAULT,
        dirty = false
    )) })
    val currentSettings = settingsUiState.saved
    val ui = com.example.matrixscreen.ui.theme.getSafeUIColorScheme(currentSettings)
    val optimizedSettings = com.example.matrixscreen.ui.theme.rememberOptimizedSettings(currentSettings)
    
    // Clear preview override and editing state when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            settingsViewModel?.clearPreviewOverride()
            viewModel.setEditingSetId(null)
        }
    }
    
    // Coroutine scope for async operations
    val coroutineScope = rememberCoroutineScope()
    
    // Focus manager for keyboard dismissal
    val focusManager = LocalFocusManager.current
    
    // Bundled fonts only
    val bundledFonts = listOf("matrix_code_nfi.ttf", "space_grotesk.ttf", "jetbrains_mono.ttf")
    
    // Character sanitization (preserving repetition for weighting)
    val sanitizedCharacters = remember(characters) {
        characters
            .trim()
            .filter { it.isDefined() && !it.isISOControl() }
    }
    
    val characterCount = sanitizedCharacters.length
    val isValid = name.isNotBlank() && characterCount > 0 && characterCount <= 2000
    
    SettingsScreenContainer(
        title = if (isEditing) "EDIT SYMBOL SET" else "CREATE SYMBOL SET",
        onBack = onBackPressed,
        ui = ui,
        optimizedSettings = optimizedSettings,
        expanded = true, // Always expanded for editing
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .clickable { focusManager.clearFocus() },
                verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sectionSpacing)
            ) {
            
            // Name input
            SettingsSection(
                title = "Set Name",
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        nameError = ""
                    },
                    label = { 
                        com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                            text = "Name",
                            style = AppTypography.labelMedium,
                            color = ui.textSecondary,
                            settings = optimizedSettings
                        )
                    },
                    isError = nameError.isNotEmpty(),
                    supportingText = if (nameError.isNotEmpty()) {
                        { 
                            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                text = nameError,
                                style = AppTypography.labelSmall,
                                color = ui.buttonCancelText,
                                settings = optimizedSettings
                            )
                        }
                    } else null,
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ui.textAccent,
                        unfocusedBorderColor = ui.borderDim,
                        focusedTextColor = ui.textPrimary,
                        unfocusedTextColor = ui.textPrimary,
                        focusedLabelColor = ui.textAccent,
                        unfocusedLabelColor = ui.textSecondary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Characters input
            SettingsSection(
                title = "Characters",
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
                ) {
                    OutlinedTextField(
                        value = characters,
                        onValueChange = { 
                            characters = it
                            charactersError = ""
                        },
                        label = { 
                            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                text = "Characters",
                                style = AppTypography.labelMedium,
                                color = ui.textSecondary,
                                settings = optimizedSettings
                            )
                        },
                        isError = charactersError.isNotEmpty(),
                        supportingText = if (charactersError.isNotEmpty()) {
                            { 
                                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                    text = charactersError,
                                    style = AppTypography.labelSmall,
                                    color = ui.buttonCancelText,
                                    settings = optimizedSettings
                                )
                            }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ui.textAccent,
                            unfocusedBorderColor = ui.borderDim,
                            focusedTextColor = ui.textPrimary,
                            unfocusedTextColor = ui.textPrimary,
                            focusedLabelColor = ui.textAccent,
                            unfocusedLabelColor = ui.textSecondary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                    
                    // Character count and validation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                            text = "$characterCount characters",
                            style = AppTypography.labelMedium,
                            color = if (characterCount > 0 && characterCount <= 2000)
                                ui.textAccent else ui.buttonCancelText,
                            settings = optimizedSettings
                        )

                        if (characterCount > 2000) {
                            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                text = "MAX 2000",
                                style = AppTypography.labelSmall,
                                color = ui.buttonCancelText,
                                settings = optimizedSettings
                            )
                        }
                    }
                    
                    // Live pools preview
                    if (sanitizedCharacters.isNotEmpty()) {
                        PoolsPreview(
                            characters = sanitizedCharacters,
                            selectedFont = selectedFont,
                            ui = ui,
                            optimizedSettings = optimizedSettings
                        )
                    }

                    // Character weighting tip
                    com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                        text = "💡 Tip: Repeat characters to make them appear more often in the Matrix rain",
                        style = AppTypography.labelSmall,
                        color = ui.textSecondary,
                        settings = optimizedSettings,
                        modifier = Modifier.padding(top = DesignTokens.Spacing.xs)
                    )

                    // Per-column character pools tip
                    com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                        text = "🎯 Pro Tip: Separate words with commas to make each rain column use a different word or character group (e.g. lol,wtf,smh)",
                        style = AppTypography.labelSmall,
                        color = ui.textSecondary,
                        settings = optimizedSettings,
                        modifier = Modifier.padding(top = DesignTokens.Spacing.xs)
                    )
                }
            }
            
            
            // Character preview with dual samples
            SettingsSection(
                title = "Preview",
                ui = ui,
                optimizedSettings = optimizedSettings
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
                ) {
                    // Fixed sample row (stable preview)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.xs)
                    ) {
                        com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                            text = "Fixed Sample",
                            style = AppTypography.labelMedium,
                            color = ui.textAccent,
                            settings = optimizedSettings
                        )
                        
                        val fixedSample = "A B C 1 2 3 $ @ # ? ! % * { }"
                        com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                            text = fixedSample,
                            style = AppTypography.bodyLarge,
                            color = ui.textAccent,
                            settings = optimizedSettings
                        )
                    }
                    
                    // User sample row (dynamic preview)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.xs)
                    ) {
                        com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                            text = "Your Characters",
                            style = AppTypography.labelMedium,
                            color = ui.textAccent,
                            settings = optimizedSettings
                        )
                        
                        if (sanitizedCharacters.isNotEmpty()) {
                            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                text = sanitizedCharacters.take(64) + if (sanitizedCharacters.length > 64) "..." else "",
                                style = AppTypography.bodyLarge,
                                color = ui.textAccent,
                                settings = optimizedSettings
                            )
                            
                            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                text = "These characters will be used in your Matrix rain effect",
                                style = AppTypography.labelSmall,
                                color = ui.textSecondary,
                                settings = optimizedSettings
                            )
                        } else {
                            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                text = "Enter characters above to see preview",
                                style = AppTypography.bodyMedium,
                                color = ui.textSecondary,
                                settings = optimizedSettings
                            )
                        }
                    }
                }
            }
            
            // Preview button
            Button(
                onClick = {
                    if (sanitizedCharacters.isNotEmpty()) {
                        val previewSet = CustomSymbolSet(
                            id = "preview-${System.currentTimeMillis()}",
                            name = "Preview",
                            characters = sanitizedCharacters,
                            fontFileName = selectedFont
                        )
                        settingsViewModel?.applyPreviewOverride(previewSet)
                    }
                },
                enabled = sanitizedCharacters.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ui.textSecondary,
                    contentColor = ui.textPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignTokens.Sizing.touchTarget)
            ) {
                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                    text = "PREVIEW IN MATRIX",
                    style = AppTypography.labelMedium,
                    color = ui.textPrimary,
                    settings = optimizedSettings
                )
            }
            
            // Cancel button
            Button(
                onClick = {
                    settingsViewModel?.clearPreviewOverride()
                    viewModel.setEditingSetId(null)
                    onBackPressed()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ui.selectionBackground,
                    contentColor = ui.buttonCancelText
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignTokens.Sizing.touchTarget)
            ) {
                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                    text = "CANCEL",
                    style = AppTypography.labelMedium,
                    color = ui.buttonCancelText,
                    settings = optimizedSettings
                )
            }
            
            // Save button
            Button(
                onClick = {
                    // Validate inputs
                    var hasError = false
                    
                    if (name.isBlank()) {
                        nameError = "Name is required"
                        hasError = true
                    }
                    
                    if (characterCount == 0) {
                        charactersError = "At least one character is required"
                        hasError = true
                    } else if (characterCount > 2000) {
                        charactersError = "Maximum 2000 characters allowed"
                        hasError = true
                    }
                    
                    if (!hasError) {
                        // Check name uniqueness asynchronously
                        coroutineScope.launch {
                            val isUnique = viewModel.isNameUnique(name.trim(), existingSet?.id)
                            if (!isUnique) {
                                nameError = "Name already exists"
                            } else {
                                val customSet = CustomSymbolSet(
                                    id = existingSet?.id ?: java.util.UUID.randomUUID().toString(),
                                    name = name.trim(),
                                    characters = sanitizedCharacters,
                                    fontFileName = selectedFont
                                )
                                viewModel.saveCustomSet(customSet)
                                onBackPressed()
                            }
                        }
                    }
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isValid) ui.primary else ui.selectionBackground,
                    contentColor = if (isValid) ui.textPrimary else ui.textSecondary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(DesignTokens.Sizing.touchTarget)
            ) {
                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                    text = if (isEditing) "SAVE CHANGES" else "CREATE SET",
                    style = AppTypography.labelMedium,
                    color = if (isValid) ui.textPrimary else ui.textSecondary,
                    settings = optimizedSettings
                )
            }
            }
        }
    )
}