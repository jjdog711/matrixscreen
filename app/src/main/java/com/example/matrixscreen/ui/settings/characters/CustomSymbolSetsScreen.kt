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
import com.example.matrixscreen.data.custom.CustomSymbolSet
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
    
    // Cyberpunk color scheme
    val cyberpunkGreen = Color(0xFF00FF00)
    val cyberpunkDark = Color(0xFF0A0A0A)
    
    // Get UI state for theming
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val currentSettings = settingsUiState.draft
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
                CyberpunkSection(title = "IMPORT / EXPORT") {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.exportCustomSets() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.CloudDownload,
                                contentDescription = "Export",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Export",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                        
                        Button(
                            onClick = { importHandler.importFromFile() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1A1A1A),
                                contentColor = Color(0xFF00FF00)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.CloudUpload,
                                contentDescription = "Import",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Import",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Import/Export status feedback
                    when (uiState.importExportState) {
                        is ImportExportState.Exporting -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "EXPORTING...",
                                color = cyberpunkGreen,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        is ImportExportState.Importing -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "IMPORTING...",
                                color = cyberpunkGreen,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        is ImportExportState.ExportSuccess -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "EXPORT SUCCESSFUL",
                                    color = Color(0xFF00FF00),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearImportExportState() },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Dismiss",
                                        tint = Color(0xFF00FF00),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        is ImportExportState.ImportSuccess -> {
                            val count = (uiState.importExportState as ImportExportState.ImportSuccess).importedCount
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "IMPORTED $count SET${if (count != 1) "S" else ""}",
                                    color = Color(0xFF00FF00),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearImportExportState() },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Dismiss",
                                        tint = Color(0xFF00FF00),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                        is ImportExportState.Error -> {
                            val error = (uiState.importExportState as ImportExportState.Error).message
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ERROR: $error",
                                    color = Color(0xFFFF6666),
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearImportExportState() },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Dismiss error",
                                        tint = Color(0xFFFF6666),
                                        modifier = Modifier.size(12.dp)
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
                    CyberpunkSection(title = "NO CUSTOM SETS") {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Create your first custom symbol set",
                                color = Color(0xFFCCCCCC),
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            
                            Text(
                                text = "Use any characters you want for your Matrix rain effect",
                                color = Color(0xFF666666),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    CyberpunkSection(title = "SAVED SETS") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(com.example.matrixscreen.core.design.DesignTokens.Scrolling.customSetsListHeight),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.customSets.forEach { customSet ->
                                CustomSetItem(
                                    customSet = customSet,
                                    isSelected = customSet.id == uiState.activeCustomSetId,
                                    onSelect = {
                                        viewModel.setActiveCustomSet(customSet.id)
                                        settingsViewModel.updateDraft(SymbolSetId, BuiltInSymbolSets.CUSTOM.value)
                                    },
                                    onEdit = { onNavigateToEdit(customSet.id) },
                                    onDuplicate = {
                                        viewModel.duplicateCustomSet(customSet.id)
                                    },
                                    onDelete = { viewModel.deleteCustomSet(customSet.id) }
                                )
                            }
                        }
                    }
                }
                
                // Create new set button
                Button(
                    onClick = onNavigateToCreate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = cyberpunkGreen,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Create New Set",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CREATE NEW SET",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
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
    onDelete: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF003300) else Color(0xFF1A1A1A),
        animationSpec = tween(200),
        label = "background_color"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF00FF00) else Color(0xFF333333),
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
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = customSet.name,
                        color = if (isSelected) Color(0xFF00FF00) else Color(0xFFCCCCCC),
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "${customSet.characters.length} characters",
                        color = Color(0xFF666666),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    // Character preview
                    Text(
                        text = customSet.characters.take(20) + if (customSet.characters.length > 20) "..." else "",
                        color = Color(0xFF888888),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                if (isSelected) {
                    Text(
                        text = "ACTIVE",
                        color = Color(0xFF00FF00),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A1A1A),
                        contentColor = Color(0xFF00FF00)
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Edit",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = onDuplicate,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1A4D1A),
                        contentColor = Color(0xFF00FF00)
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Duplicate",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Duplicate",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF330000),
                        contentColor = Color(0xFFFF6666)
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Delete",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

/**
 * Cyberpunk header component
 */
@Composable
private fun CyberpunkHeader(
    title: String,
    onBackPressed: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackPressed,
            modifier = Modifier
                .background(
                    Color(0xFF1A1A1A),
                    RoundedCornerShape(8.dp)
                )
                .border(1.dp, Color(0xFF00FF00), RoundedCornerShape(8.dp))
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color(0xFF00FF00)
            )
        }
        
        Text(
            text = title,
            color = Color(0xFF00FF00),
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        
        // Empty space for balance
        Spacer(modifier = Modifier.size(48.dp))
    }
}

/**
 * Cyberpunk section component
 */
@Composable
private fun CyberpunkSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            color = Color(0xFF00FF00),
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF0F0F0F),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    1.dp,
                    Color(0xFF333333),
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            content()
        }
    }
}
