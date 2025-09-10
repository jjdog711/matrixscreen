package com.example.matrixscreen.ui.settings.characters

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.core.design.scrollableContent
import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.registry.BuiltInSymbolSets
import com.example.matrixscreen.data.registry.SymbolSetId as RegistrySymbolSetId
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.settings.model.SymbolSetId
import com.example.matrixscreen.ui.settings.model.*
import com.example.matrixscreen.ui.components.*
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings

/**
 * Screen for managing custom symbol sets
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSymbolSetsScreen(
    viewModel: CustomSymbolSetViewModel,
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onBackPressed: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val fileManager = remember { SymbolSetFileManager(context) }

    // Import handler
    val importHandler = rememberImportHandler(
        fileManager = fileManager,
        onImportComplete = { jsonData ->
            viewModel.importCustomSets(jsonData)
        }
    )

    // Auto-clear import/export state after showing feedback
    LaunchedEffect(uiState.importExportState) {
        when (uiState.importExportState) {
            is ImportExportState.ExportSuccess,
            is ImportExportState.ImportSuccess,
            is ImportExportState.Error -> {
                kotlinx.coroutines.delay(3000) // Show for 3 seconds
                viewModel.clearImportExportState()
            }
            else -> {
                // No action needed
            }
        }
    }
    
    
    // Get UI state for theming
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = settingsUiState.saved
    val ui = getSafeUIColorScheme(currentSettings)
    val optimizedSettings = rememberOptimizedSettings(currentSettings)
    
    SettingsScreenContainer(
        title = "CUSTOM SYMBOL SETS",
        onBack = onBackPressed,
        ui = ui,
        optimizedSettings = optimizedSettings,
        expanded = true, // Always expanded for custom sets
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(com.example.matrixscreen.core.design.DesignTokens.Spacing.sectionSpacing)
            ) {
                // Import/Export section
                SettingsSection(
                    title = "Import / Export",
                    ui = ui,
                    optimizedSettings = optimizedSettings
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.exportCustomSets() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ui.selectionBackground,
                                contentColor = ui.textPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.CloudDownload,
                                contentDescription = "Export",
                                modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                            )
                            Spacer(modifier = Modifier.width(DesignTokens.Spacing.xs))
                            Text(
                                text = "Export",
                                style = com.example.matrixscreen.ui.theme.AppTypography.labelMedium,
                                color = ui.textPrimary
                            )
                        }
                        
                        Button(
                            onClick = { importHandler.importFromFile() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ui.selectionBackground,
                                contentColor = ui.textPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.CloudUpload,
                                contentDescription = "Import",
                                modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                            )
                            Spacer(modifier = Modifier.width(DesignTokens.Spacing.xs))
                            Text(
                                text = "Import",
                                style = com.example.matrixscreen.ui.theme.AppTypography.labelMedium,
                                color = ui.textPrimary
                            )
                        }
                    }

                    // Import/Export status feedback
                    when (uiState.importExportState) {
                        is ImportExportState.Exporting -> {
                            Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                text = "EXPORTING...",
                                style = com.example.matrixscreen.ui.theme.AppTypography.labelMedium,
                                color = ui.textAccent,
                                settings = optimizedSettings
                            )
                        }
                        is ImportExportState.Importing -> {
                            Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                text = "IMPORTING...",
                                style = com.example.matrixscreen.ui.theme.AppTypography.labelMedium,
                                color = ui.textAccent,
                                settings = optimizedSettings
                            )
                        }
                        is ImportExportState.ExportSuccess -> {
                            Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                    text = "EXPORT SUCCESSFUL",
                                    style = com.example.matrixscreen.ui.theme.AppTypography.labelMedium,
                                    color = ui.textAccent,
                                    settings = optimizedSettings,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearImportExportState() },
                                    modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Dismiss",
                                        tint = ui.textAccent,
                                        modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                                    )
                                }
                            }
                        }
                        is ImportExportState.ImportSuccess -> {
                            val count = (uiState.importExportState as ImportExportState.ImportSuccess).importedCount
                            Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                    text = "IMPORTED $count SET${if (count != 1) "S" else ""}",
                                    style = com.example.matrixscreen.ui.theme.AppTypography.labelMedium,
                                    color = ui.textAccent,
                                    settings = optimizedSettings,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearImportExportState() },
                                    modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Dismiss",
                                        tint = ui.textAccent,
                                        modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                                    )
                                }
                            }
                        }
                        is ImportExportState.Error -> {
                            val error = (uiState.importExportState as ImportExportState.Error).message
                            Spacer(modifier = Modifier.height(DesignTokens.Spacing.sm))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                    text = "ERROR: $error",
                                    style = com.example.matrixscreen.ui.theme.AppTypography.labelSmall,
                                    color = ui.buttonCancelText,
                                    settings = optimizedSettings,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearImportExportState() },
                                    modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Dismiss error",
                                        tint = ui.buttonCancelText,
                                        modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                                    )
                                }
                            }
                        }
                        ImportExportState.Idle -> {
                            // No feedback needed
                        }
                    }
                }
                
                // Custom sets list
                if (uiState.customSets.isEmpty()) {
                    // Empty state
                    SettingsSection(
                        title = "No Custom Sets",
                        ui = ui,
                        optimizedSettings = optimizedSettings
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.lg)
                        ) {
                            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                text = "Create your first custom symbol set",
                                style = com.example.matrixscreen.ui.theme.AppTypography.bodyMedium,
                                color = ui.textPrimary,
                                settings = optimizedSettings,
                            )
                            
                            com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                                text = "Use any characters you want for your Matrix rain effect",
                                style = com.example.matrixscreen.ui.theme.AppTypography.bodySmall,
                                color = ui.textSecondary,
                                settings = optimizedSettings,
                            )
                        }
                    }
                } else {
                    SettingsSection(
                        title = "Saved Sets",
                        ui = ui,
                        optimizedSettings = optimizedSettings
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .scrollableContent(),
                            verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
                        ) {
                            uiState.customSets.forEach { customSet ->
                                CustomSetItem(
                                    customSet = customSet,
                                    isSelected = customSet.id == uiState.activeCustomSetId,
                                    onSelect = {
                                        viewModel.setActiveCustomSet(customSet.id)
                                        settingsViewModel.updateDraft(SymbolSetId, BuiltInSymbolSets.CUSTOM.value)
                                        settingsViewModel.commit() // Immediate commit for symbol sets
                                    },
                                    onEdit = { onNavigateToEdit(customSet.id) },
                                    onDuplicate = {
                                        viewModel.duplicateCustomSet(customSet.id)
                                    },
                                    onDelete = { viewModel.deleteCustomSet(customSet.id) },
                                    ui = ui,
                                    optimizedSettings = optimizedSettings
                                )
                            }
                        }
                    }
                }
                
                // Create new set button
                Button(
                    onClick = onNavigateToCreate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ui.primary,
                        contentColor = ui.textPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(DesignTokens.Sizing.touchTarget)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Create New Set",
                        modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                    )
                    Spacer(modifier = Modifier.width(DesignTokens.Spacing.sm))
                    com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                        text = "CREATE NEW SET",
                        style = com.example.matrixscreen.ui.theme.AppTypography.labelMedium,
                        color = ui.textPrimary,
                        settings = optimizedSettings
                    )
                }
            }
        },
        modifier = Modifier
    )
}

/**
 * Individual custom set item
 */
@Composable
private fun CustomSetItem(
    customSet: CustomSymbolSet,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    optimizedSettings: MatrixSettings
) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) ui.selectionBackground else ui.overlayBackground,
        animationSpec = tween(200),
        label = "background_color"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) ui.textAccent else ui.borderDim,
        animationSpec = tween(200),
        label = "border_color"
    )
    
    Card(
        onClick = onSelect,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(DesignTokens.Outline.thin, borderColor, RoundedCornerShape(DesignTokens.Radius.sm))
    ) {
        Column(
            modifier = Modifier.padding(DesignTokens.Spacing.lg)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                        text = customSet.name,
                        style = com.example.matrixscreen.ui.theme.AppTypography.titleSmall,
                        color = if (isSelected) ui.textAccent else ui.textPrimary,
                        settings = optimizedSettings
                    )
                    
                    com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                        text = "${customSet.characters.length} characters",
                        style = com.example.matrixscreen.ui.theme.AppTypography.bodySmall,
                        color = ui.textSecondary,
                        settings = optimizedSettings,
                        modifier = Modifier.padding(top = DesignTokens.Spacing.xs)
                    )
                    
                    // Character preview
                    com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                        text = customSet.characters.take(20) + if (customSet.characters.length > 20) "..." else "",
                        style = com.example.matrixscreen.ui.theme.AppTypography.labelSmall,
                        color = ui.textSecondary,
                        settings = optimizedSettings,
                        modifier = Modifier.padding(top = DesignTokens.Spacing.xs)
                    )
                }
                
                if (isSelected) {
                    com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                        text = "ACTIVE",
                        style = com.example.matrixscreen.ui.theme.AppTypography.labelSmall,
                        color = ui.textAccent,
                        settings = optimizedSettings
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(DesignTokens.Spacing.md))
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
            ) {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ui.selectionBackground,
                        contentColor = ui.textPrimary
                    ),
                    modifier = Modifier.height(DesignTokens.Sizing.buttonHeight)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                    )
                }

                Button(
                    onClick = onDuplicate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ui.selectionBackground,
                        contentColor = ui.textPrimary
                    ),
                    modifier = Modifier.height(DesignTokens.Sizing.buttonHeight)
                ) {
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = "Duplicate",
                        modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                    )
                }

                Button(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ui.selectionBackground,
                        contentColor = ui.textPrimary
                    ),
                    modifier = Modifier.height(DesignTokens.Sizing.buttonHeight)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(DesignTokens.Sizing.smallIconSize)
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                    text = "Delete Custom Set",
                    style = com.example.matrixscreen.ui.theme.AppTypography.titleMedium,
                    color = ui.textPrimary,
                    settings = optimizedSettings
                )
            },
            text = {
                com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                    text = "Are you sure you want to delete \"${customSet.name}\"? This action cannot be undone.",
                    style = com.example.matrixscreen.ui.theme.AppTypography.bodyMedium,
                    color = ui.textSecondary,
                    settings = optimizedSettings
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ui.buttonCancelText,
                        contentColor = ui.textPrimary
                    )
                ) {
                    com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                        text = "DELETE",
                        style = com.example.matrixscreen.ui.theme.AppTypography.labelMedium,
                        color = ui.textPrimary,
                        settings = optimizedSettings
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ui.selectionBackground,
                        contentColor = ui.textPrimary
                    )
                ) {
                    com.example.matrixscreen.ui.theme.ModernTextWithGlow(
                        text = "CANCEL",
                        style = com.example.matrixscreen.ui.theme.AppTypography.labelMedium,
                        color = ui.textPrimary,
                        settings = optimizedSettings
                    )
                }
            },
            containerColor = ui.overlayBackground,
            titleContentColor = ui.textPrimary,
            textContentColor = ui.textSecondary
        )
    }
}

