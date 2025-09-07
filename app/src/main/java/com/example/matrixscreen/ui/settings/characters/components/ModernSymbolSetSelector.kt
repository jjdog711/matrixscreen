package com.example.matrixscreen.ui.settings.characters.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.data.SymbolSet
import com.example.matrixscreen.ui.theme.FontUtils
import com.example.matrixscreen.ui.theme.MatrixUIColorScheme
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.settings.components.*

/**
 * Modern symbol set selector with Space Grotesk support and live preview
 * Features: Live font preview, Space Grotesk integration, modern design
 */

/**
 * Modern symbol set selector component
 */
@Composable
fun ModernSymbolSetSelector(
    currentSet: SymbolSet,
    onSetChanged: (SymbolSet) -> Unit,
    colorScheme: MatrixUIColorScheme,
    currentSettings: MatrixSettings?,
    modifier: Modifier = Modifier,
    onNavigateToCustomSets: (() -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section label
        ModernSectionLabel(
            label = "SYMBOL SET",
            colorScheme = colorScheme
        )
        
        // Regular symbol sets (excluding CUSTOM)
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SymbolSet.values().filter { it != SymbolSet.CUSTOM }.forEach { symbolSet ->
                ModernSymbolSetCard(
                    symbolSet = symbolSet,
                    isSelected = symbolSet == currentSet,
                    onClick = { onSetChanged(symbolSet) },
                    colorScheme = colorScheme,
                    currentSettings = currentSettings
                )
            }
        }
        
        // Custom symbol sets section
        if (onNavigateToCustomSets != null) {
            ModernCustomSetToggle(
                currentSet = currentSet,
                onToggle = { checked ->
                    if (checked) {
                        val hasValidCustomSet = currentSettings?.activeCustomSetId != null &&
                            currentSettings.savedCustomSets.any { it.id == currentSettings.activeCustomSetId }
                        
                        if (hasValidCustomSet) {
                            onSetChanged(SymbolSet.CUSTOM)
                        } else {
                            onNavigateToCustomSets()
                        }
                    } else {
                        onSetChanged(SymbolSet.MATRIX_AUTHENTIC)
                    }
                },
                colorScheme = colorScheme,
                currentSettings = currentSettings
            )
        }
    }
}

/**
 * Modern symbol set card with live preview
 */
@Composable
private fun ModernSymbolSetCard(
    symbolSet: SymbolSet,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorScheme: MatrixUIColorScheme,
    currentSettings: MatrixSettings?
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.selectionBackground
        } else {
            colorScheme.backgroundSecondary
        },
        animationSpec = tween(200),
        label = "card_background"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.border
        } else {
            colorScheme.borderDim
        },
        animationSpec = tween(200),
        label = "card_border"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.textAccent
        } else {
            colorScheme.textPrimary
        },
        animationSpec = tween(200),
        label = "card_text"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Symbol set name
            Text(
                text = symbolSet.displayName,
                style = AppTypography.titleSmall,
                color = textColor
            )
            
            // Live preview of characters
            val previewCharacters = symbolSet.effectiveCharacters(currentSettings ?: MatrixSettings())
            ModernSymbolPreview(
                characters = previewCharacters,
                colorScheme = colorScheme,
                maxCharacters = 20
            )
        }
    }
}

/**
 * Modern custom set toggle with navigation
 */
@Composable
private fun ModernCustomSetToggle(
    currentSet: SymbolSet,
    onToggle: (Boolean) -> Unit,
    colorScheme: MatrixUIColorScheme,
    currentSettings: MatrixSettings?
) {
    val isCustomSelected = currentSet == SymbolSet.CUSTOM
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colorScheme.backgroundSecondary)
            .border(1.dp, colorScheme.borderDim, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Custom Symbol Set",
                    style = AppTypography.bodyMedium,
                    color = colorScheme.textPrimary,
                    fontWeight = if (isCustomSelected) FontWeight.SemiBold else FontWeight.Normal
                )
                
                // Show active custom set name if available
                currentSettings?.let { settings ->
                    if (isCustomSelected && settings.activeCustomSetId != null) {
                        val activeSet = settings.savedCustomSets.find { it.id == settings.activeCustomSetId }
                        activeSet?.let { set ->
                            ModernHelperText(
                                text = "Active: ${set.name}",
                                colorScheme = colorScheme
                            )
                        }
                    } else {
                        ModernHelperText(
                            text = "Create custom character sets",
                            colorScheme = colorScheme
                        )
                    }
                }
            }
            
            Switch(
                checked = isCustomSelected,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorScheme.primary,
                    checkedTrackColor = colorScheme.primary.copy(alpha = 0.5f),
                    uncheckedThumbColor = colorScheme.textSecondary,
                    uncheckedTrackColor = colorScheme.borderDim
                )
            )
        }
    }
}

/**
 * Modern font selector for custom symbol sets
 * Shows available fonts including Space Grotesk with live preview
 */
@Composable
fun ModernFontSelector(
    selectedFont: String,
    onFontSelected: (String) -> Unit,
    colorScheme: MatrixUIColorScheme,
    previewCharacters: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section label
        ModernSectionLabel(
            label = "FONT SELECTION",
            colorScheme = colorScheme
        )
        
        // Font preview row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val availableFonts = FontUtils.getAvailableFontFiles()
            
            items(availableFonts) { fontFileName ->
                val displayName = FontUtils.getFontDisplayName(fontFileName)
                ModernFontPreviewCard(
                    displayName = displayName,
                    isSelected = fontFileName == selectedFont,
                    onClick = { onFontSelected(fontFileName) },
                    colorScheme = colorScheme,
                    previewCharacters = previewCharacters
                )
            }
        }
        
        // Helper text
        ModernHelperText(
            text = "Select a font for your custom symbol set. Space Grotesk provides a modern look.",
            colorScheme = colorScheme
        )
    }
}

/**
 * Modern font preview card
 */
@Composable
private fun ModernFontPreviewCard(
    displayName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorScheme: MatrixUIColorScheme,
    previewCharacters: String
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.selectionBackground
        } else {
            colorScheme.backgroundSecondary
        },
        animationSpec = tween(200),
        label = "font_card_background"
    )
    
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.border
        } else {
            colorScheme.borderDim
        },
        animationSpec = tween(200),
        label = "font_card_border"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorScheme.textAccent
        } else {
            colorScheme.textPrimary
        },
        animationSpec = tween(200),
        label = "font_card_text"
    )
    
    Box(
        modifier = Modifier
            .width(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Font name
            Text(
                text = displayName,
                style = AppTypography.bodySmall,
                color = textColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 2
            )
            
            // Character preview
            Text(
                text = previewCharacters.take(8),
                style = AppTypography.bodyMedium,
                color = textColor,
                maxLines = 1
            )
        }
    }
}
