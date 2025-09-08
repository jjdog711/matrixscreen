package com.example.matrixscreen.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.data.custom.CustomSymbolSet

/**
 * Screen for managing custom symbol sets
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSymbolSetsScreen(
    onBackPressed: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    savedCustomSets: List<CustomSymbolSet>,
    activeCustomSetId: String?,
    onSelectCustomSet: (String) -> Unit,
    onDeleteCustomSet: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    
    // Cyberpunk color scheme
    val cyberpunkGreen = Color(0xFF00FF00)
    val cyberpunkDark = Color(0xFF0A0A0A)
    
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
                title = "CUSTOM SYMBOL SETS",
                onBackPressed = onBackPressed
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Custom sets list
            if (savedCustomSets.isEmpty()) {
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
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(400.dp)
                    ) {
                        items(savedCustomSets) { customSet ->
                            CustomSetItem(
                                customSet = customSet,
                                isSelected = customSet.id == activeCustomSetId,
                                onSelect = { onSelectCustomSet(customSet.id) },
                                onEdit = { onNavigateToEdit(customSet.id) },
                                onDelete = { onDeleteCustomSet(customSet.id) }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
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
            
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
        }
    }
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
