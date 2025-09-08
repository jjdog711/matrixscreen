package com.example.matrixscreen.ui.settings.characters

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.ui.theme.FontUtils
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.MatrixTextStyles
import com.example.matrixscreen.ui.settings.components.*
import com.example.matrixscreen.ui.components.*

/**
 * Screen for creating or editing a custom symbol set
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrEditSymbolSetScreen(
    viewModel: CustomSymbolSetViewModel,
    onBackPressed: () -> Unit,
    onDelete: (() -> Unit)? = null,
    existingSet: CustomSymbolSet? = null
) {
    val scrollState = rememberScrollState()
    val isEditing = existingSet != null
    
    // Form state
    var name by remember { mutableStateOf(existingSet?.name ?: "") }
    var characters by remember { mutableStateOf(existingSet?.characters ?: "") }
    var selectedFont by remember { mutableStateOf(existingSet?.fontFileName ?: "matrix_code_nfi.ttf") }
    var nameError by remember { mutableStateOf("") }
    var charactersError by remember { mutableStateOf("") }
    
    // Available fonts
    var availableFonts by remember { mutableStateOf<List<String>>(emptyList()) }
    
    // Load available fonts using FontUtils
    LaunchedEffect(Unit) {
        try {
            availableFonts = FontUtils.getAvailableFontFiles()
        } catch (e: Exception) {
            availableFonts = listOf("matrix_code_nfi.ttf")
        }
    }
    
    // Character sanitization (preserving repetition for weighting)
    val sanitizedCharacters = remember(characters) {
        characters
            .trim()
            .filter { it.isDefined() && !it.isISOControl() }
    }
    
    val characterCount = sanitizedCharacters.length
    val isValid = name.isNotBlank() && characterCount > 0 && characterCount <= 512
    
    // Cyberpunk color scheme
    val cyberpunkGreen = Color(0xFF00FF00)
    val cyberpunkDark = Color(0xFF0A0A0A)
    val cyberpunkBorder = Color(0xFF333333)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(cyberpunkDark, Color.Black),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header with back button
            CyberpunkHeader(
                title = if (isEditing) "EDIT SYMBOL SET" else "CREATE SYMBOL SET",
                onBackPressed = onBackPressed,
                onDelete = if (isEditing) onDelete else null
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Name input
            CyberpunkSection(title = "SET NAME") {
                OutlinedTextField(
                    value = name,
                    onValueChange = { 
                        name = it
                        nameError = ""
                    },
                    label = { 
                        Text(
                            "Name",
                            color = Color(0xFFCCCCCC),
                            fontFamily = FontFamily.Monospace
                        )
                    },
                    isError = nameError.isNotEmpty(),
                    supportingText = if (nameError.isNotEmpty()) {
                        { Text(nameError, color = Color(0xFFFF6666)) }
                    } else null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = cyberpunkGreen,
                        unfocusedBorderColor = cyberpunkBorder,
                        focusedTextColor = Color(0xFFCCCCCC),
                        unfocusedTextColor = Color(0xFFCCCCCC),
                        focusedLabelColor = cyberpunkGreen,
                        unfocusedLabelColor = Color(0xFF666666)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Characters input
            CyberpunkSection(title = "CHARACTERS") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = characters,
                        onValueChange = { 
                            characters = it
                            charactersError = ""
                        },
                        label = { 
                            Text(
                                "Characters",
                                color = Color(0xFFCCCCCC),
                                fontFamily = FontFamily.Monospace
                            )
                            },
                        isError = charactersError.isNotEmpty(),
                        supportingText = if (charactersError.isNotEmpty()) {
                            { Text(charactersError, color = Color(0xFFFF6666)) }
                        } else null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = cyberpunkGreen,
                            unfocusedBorderColor = cyberpunkBorder,
                            focusedTextColor = Color(0xFFCCCCCC),
                            unfocusedTextColor = Color(0xFFCCCCCC),
                            focusedLabelColor = cyberpunkGreen,
                            unfocusedLabelColor = Color(0xFF666666)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 6,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Text
                        )
                    )
                    
                    // Character count and validation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$characterCount characters",
                            color = if (characterCount > 0 && characterCount <= 512) 
                                Color(0xFF00FF00) else Color(0xFFFF6666),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        
                        if (characterCount > 512) {
                            Text(
                                text = "MAX 512",
                                color = Color(0xFFFF6666),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Character weighting tip
                    Text(
                        text = "💡 Tip: Repeat characters to make them appear more often in the Matrix rain",
                        color = Color(0xFF888888),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    // Per-column character pools tip
                    Text(
                        text = "🎯 Pro Tip: Separate words with commas to make each rain column use a different word or character group (e.g. lol,wtf,smh)",
                        color = Color(0xFF888888),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Font picker
            CyberpunkSection(title = "FONT SELECTION") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Choose Font",
                        color = Color(0xFFCCCCCC),
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (availableFonts.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(80.dp)
                        ) {
                            items(availableFonts) { fontFileName ->
                                val isSelected = fontFileName == selectedFont
                                val displayName = FontUtils.getFontDisplayName(fontFileName)
                                val isSpaceGrotesk = fontFileName.contains("space", ignoreCase = true) || 
                                                    fontFileName.contains("grotesk", ignoreCase = true)
                                
                                Card(
                                    onClick = { selectedFont = fontFileName },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) Color(0xFF003300) else Color(0xFF1A1A1A)
                                    ),
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(70.dp)
                                        .border(
                                            1.dp, 
                                            if (isSelected) cyberpunkGreen else Color(0xFF333333), 
                                            RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = if (isSpaceGrotesk) "Space Grotesk (Modern)" else displayName,
                                            color = if (isSelected) cyberpunkGreen else Color(0xFFCCCCCC),
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            maxLines = 2,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                        )
                                        
                                        // Inline preview using Typography system
                                        Text(
                                            text = "A9$@",
                                            style = if (fontFileName == "matrix_code_nfi.ttf") {
                                                MatrixTextStyles.MatrixFontPreview
                                            } else {
                                                AppTypography.bodyMedium
                                            },
                                            color = if (isSelected) cyberpunkGreen else Color(0xFF888888),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Loading fonts...",
                            color = Color(0xFF666666),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Character preview with dual samples
            CyberpunkSection(title = "PREVIEW") {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Fixed sample row (stable preview)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Fixed Sample (Space Grotesk)",
                            color = Color(0xFF00FF00),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        
                        val fixedSample = "A B C 1 2 3 $ @ # ? ! % * { }"
                        Text(
                            text = fixedSample,
                            style = if (selectedFont == "matrix_code_nfi.ttf") {
                                MatrixTextStyles.MatrixSymbolPreview
                            } else {
                                AppTypography.bodyLarge
                            },
                            color = cyberpunkGreen
                        )
                    }
                    
                    // User sample row (dynamic preview)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Your Characters",
                            color = Color(0xFF00FF00),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (sanitizedCharacters.isNotEmpty()) {
                            Text(
                                text = sanitizedCharacters.take(64) + if (sanitizedCharacters.length > 64) "..." else "",
                                style = if (selectedFont == "matrix_code_nfi.ttf") {
                                    MatrixTextStyles.MatrixSymbolPreview
                                } else {
                                    AppTypography.bodyLarge
                                },
                                color = cyberpunkGreen
                            )
                            
                            Text(
                                text = "These characters will be used in your Matrix rain effect",
                                color = Color(0xFF666666),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        } else {
                            Text(
                                text = "Enter characters above to see preview",
                                color = Color(0xFF666666),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
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
                    } else if (characterCount > 512) {
                        charactersError = "Maximum 512 characters allowed"
                        hasError = true
                    }
                    
                    if (!hasError) {
                        val customSet = CustomSymbolSet(
                            id = existingSet?.id ?: java.util.UUID.randomUUID().toString(),
                            name = name.trim(),
                            characters = sanitizedCharacters,
                            fontFileName = selectedFont
                        )
                        viewModel.saveCustomSet(customSet)
                        onBackPressed()
                    }
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isValid) cyberpunkGreen else Color(0xFF333333),
                    contentColor = if (isValid) Color.Black else Color(0xFF666666)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (isEditing) "SAVE CHANGES" else "CREATE SET",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
        }
    }
}

/**
 * Cyberpunk header component with optional delete button
 */
@Composable
private fun CyberpunkHeader(
    title: String,
    onBackPressed: () -> Unit,
    onDelete: (() -> Unit)? = null
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
        
        if (onDelete != null) {
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .background(
                        Color(0xFF330000),
                        RoundedCornerShape(8.dp)
                    )
                    .border(1.dp, Color(0xFFFF6666), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFFF6666)
                )
            }
        } else {
            // Empty space for balance
            Spacer(modifier = Modifier.size(48.dp))
        }
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
