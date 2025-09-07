package com.example.matrixscreen.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Matrix-inspired color palette with 20 carefully selected colors
 */
object MatrixColorPalette {
    val colors = listOf(
        // Classic Matrix Greens
        Color(0xFF00FF00), // Classic Matrix Green
        Color(0xFF00CC00), // Bright Green
        Color(0xFF008800), // Medium Green
        Color(0xFF004400), // Dim Green
        Color(0xFF80FF80), // Light Green
        
        // Cyberpunk Blues
        Color(0xFF0080FF), // Neo Blue
        Color(0xFF0066CC), // Bright Blue
        Color(0xFF004499), // Medium Blue
        Color(0xFF002266), // Dark Blue
        Color(0xFF80BFFF), // Light Blue
        
        // Electric Colors
        Color(0xFF00FFFF), // Cyan
        Color(0xFFFF0040), // Cyber Red
        Color(0xFF8000FF), // Purple
        Color(0xFFFF8000), // Orange
        Color(0xFFFFFF00), // Yellow
        
        // Neutrals & Special
        Color(0xFFFFFFFF), // Ghost White
        Color(0xFFCCCCCC), // Light Gray
        Color(0xFF666666), // Medium Gray
        Color(0xFF333333), // Dark Gray
        Color(0xFF000000)  // Pure Black
    )
    
    val colorNames = listOf(
        "Matrix Green", "Bright Green", "Medium Green", "Dim Green", "Light Green",
        "Neo Blue", "Bright Blue", "Medium Blue", "Dark Blue", "Light Blue",
        "Cyan", "Cyber Red", "Purple", "Orange", "Yellow",
        "Ghost White", "Light Gray", "Medium Gray", "Dark Gray", "Pure Black"
    )
}

/**
 * Color swatch component for the color picker
 */
@Composable
fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showName: Boolean = false,
    colorName: String = ""
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color(0xFF333333),
        animationSpec = tween(200),
        label = "border_color"
    )
    
    val borderWidth = if (isSelected) 3.dp else 1.dp
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color)
                .border(borderWidth, borderColor, CircleShape)
                .clickable { onClick() }
        )
        
        if (showName && colorName.isNotEmpty()) {
            Text(
                text = colorName,
                color = Color(0xFFCCCCCC),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/**
 * Custom color swatch with "+" icon
 */
@Composable
fun CustomColorSwatch(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color(0xFF1A1A1A))
            .border(1.dp, Color(0xFF333333), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Add,
            contentDescription = "Add Custom Color",
            tint = Color(0xFF00FF00),
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Color picker row with swatches
 */
@Composable
fun ColorPickerRow(
    title: String,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
    showCustomColor: Boolean = true
) {
    var showCustomColorDialog by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
    ) {
        // Title
        Text(
            text = title,
            color = Color(0xFF00FF00),
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Color swatches
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(MatrixColorPalette.colors.size) { index ->
                val color = MatrixColorPalette.colors[index]
                val colorName = MatrixColorPalette.colorNames[index]
                val isSelected = color == selectedColor
                
                ColorSwatch(
                    color = color,
                    isSelected = isSelected,
                    onClick = { onColorSelected(color) },
                    colorName = colorName
                )
            }
            
            // Custom color swatch
            if (showCustomColor) {
                item {
                    CustomColorSwatch(
                        onClick = { showCustomColorDialog = true }
                    )
                }
            }
        }
    }
    
    // Custom color picker dialog
    if (showCustomColorDialog) {
        CustomColorPickerDialog(
            initialColor = selectedColor,
            onColorSelected = { color ->
                onColorSelected(color)
                showCustomColorDialog = false
            },
            onDismiss = { showCustomColorDialog = false }
        )
    }
}

/**
 * Basic mode color picker (Primary + Background)
 */
@Composable
fun BasicColorPicker(
    primaryColor: Color,
    backgroundColor: Color,
    onPrimaryColorSelected: (Color) -> Unit,
    onBackgroundColorSelected: (Color) -> Unit,
    onAdvancedModeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Primary Color (affects both rain and UI)
        ColorPickerRow(
            title = "Primary Color",
            selectedColor = primaryColor,
            onColorSelected = onPrimaryColorSelected,
            showCustomColor = true
        )
        
        // Background Color
        ColorPickerRow(
            title = "Background Color",
            selectedColor = backgroundColor,
            onColorSelected = onBackgroundColorSelected,
            showCustomColor = true
        )
        
        // Advanced Mode Toggle
        Button(
            onClick = onAdvancedModeToggle,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color(0xFF00FF00)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Advanced Color Settings",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Advanced mode color picker (all individual colors)
 */
@Composable
fun AdvancedColorPicker(
    rainHeadColor: Color,
    rainBrightTrailColor: Color,
    rainTrailColor: Color,
    rainDimTrailColor: Color,
    uiColor: Color,
    backgroundColor: Color,
    onRainHeadColorSelected: (Color) -> Unit,
    onRainBrightTrailColorSelected: (Color) -> Unit,
    onRainTrailColorSelected: (Color) -> Unit,
    onRainDimTrailColorSelected: (Color) -> Unit,
    onUiColorSelected: (Color) -> Unit,
    onBackgroundColorSelected: (Color) -> Unit,
    onBasicModeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    hasColorConflict: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Color conflict warning
        if (hasColorConflict) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF330000)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚠️ UI and Background colors cannot be identical",
                    color = Color(0xFFFF6666),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
        
        // Rain Colors Section
        Text(
            text = "RAIN COLORS",
            color = Color(0xFF00FF00),
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        ColorPickerRow(
            title = "Head Color",
            selectedColor = rainHeadColor,
            onColorSelected = onRainHeadColorSelected
        )
        
        ColorPickerRow(
            title = "Bright Trail Color",
            selectedColor = rainBrightTrailColor,
            onColorSelected = onRainBrightTrailColorSelected
        )
        
        ColorPickerRow(
            title = "Trail Color",
            selectedColor = rainTrailColor,
            onColorSelected = onRainTrailColorSelected
        )
        
        ColorPickerRow(
            title = "Dim Trail Color",
            selectedColor = rainDimTrailColor,
            onColorSelected = onRainDimTrailColorSelected
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // UI & Background Section
        Text(
            text = "UI & BACKGROUND",
            color = Color(0xFF00FF00),
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        ColorPickerRow(
            title = "UI Color",
            selectedColor = uiColor,
            onColorSelected = onUiColorSelected
        )
        
        ColorPickerRow(
            title = "Background Color",
            selectedColor = backgroundColor,
            onColorSelected = onBackgroundColorSelected
        )
        
        // Basic Mode Toggle
        Button(
            onClick = onBasicModeToggle,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A1A1A),
                contentColor = Color(0xFF00FF00)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Basic Color Settings",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Helper function to convert Long color value to Compose Color
 */
fun Long.toColor(): Color {
    return Color(this)
}

/**
 * Helper function to convert Compose Color to Long color value
 */
fun Color.toLongColor(): Long {
    return this.toArgb().toLong()
}
