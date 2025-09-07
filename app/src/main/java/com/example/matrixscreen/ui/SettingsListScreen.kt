package com.example.matrixscreen.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import com.example.matrixscreen.ui.theme.ModernTextWithGlow
import com.example.matrixscreen.ui.MatrixSettingType

/**
 * Settings list screen that shows all available settings
 * Each setting can be tapped to start editing in overlay mode
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsListScreen(
    onBackPressed: () -> Unit,
    onSettingSelected: (MatrixSettingType) -> Unit,
    settings: MatrixSettings,
    onResetSettings: () -> Unit
) {
    val scrollState = rememberScrollState()
    val ui = getSafeUIColorScheme(settings)
    val optimizedSettings = rememberOptimizedSettings(settings)
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ui.background,
        contentColor = ui.textPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Header with back button
            ModernHeader(
                title = "MATRIX SETTINGS",
                onBackPressed = onBackPressed,
                onResetPressed = onResetSettings,
                ui = ui,
                settings = optimizedSettings
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Basic Settings Section
            ModernSection(title = "BASIC SETTINGS", ui = ui) {
                ModernSettingItem(
                    title = "Fall Speed",
                    description = "Speed of character falling animation",
                    currentValue = "${(settings.fallSpeed * 100).toInt()}%",
                    onClick = { onSettingSelected(MatrixSettingType.FallSpeed) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Symbol Set",
                    description = "Character set for Matrix rain",
                    currentValue = settings.symbolSet.displayName,
                    onClick = { onSettingSelected(MatrixSettingType.SymbolSet) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Color & Brightness",
                    description = "Unified color customization and brightness controls",
                    currentValue = if (settings.advancedColorsEnabled) "Advanced Mode" else "Basic Mode",
                    onClick = { onSettingSelected(MatrixSettingType.ColorAndBrightness) },
                    ui = ui
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Animation Configuration Section
            ModernSection(title = "ANIMATION CONFIGURATION", ui = ui) {
                ModernSettingItem(
                    title = "Font Size",
                    description = "Size of Matrix characters",
                    currentValue = "${settings.fontSize.toInt()}dp",
                    onClick = { onSettingSelected(MatrixSettingType.FontSize) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Column Count",
                    description = "Number of Matrix columns",
                    currentValue = "${settings.columnCount}",
                    onClick = { onSettingSelected(MatrixSettingType.ColumnCount) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Frame Rate",
                    description = "Animation frame rate",
                    currentValue = "${settings.targetFps.toInt()}fps",
                    onClick = { onSettingSelected(MatrixSettingType.TargetFps) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Trail Length",
                    description = "Maximum characters in trail",
                    currentValue = "${settings.maxTrailLength} chars",
                    onClick = { onSettingSelected(MatrixSettingType.MaxTrailLength) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Bright Trail Length",
                    description = "Maximum bright green characters behind head",
                    currentValue = "${settings.maxBrightTrailLength} chars",
                    onClick = { onSettingSelected(MatrixSettingType.MaxBrightTrailLength) },
                    ui = ui
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Visual Effects Section
            ModernSection(title = "VISUAL EFFECTS", ui = ui) {
                ModernSettingItem(
                    title = "Glow Intensity",
                    description = "Intensity of character glow effects",
                    currentValue = "${(settings.glowIntensity * 100).toInt()}%",
                    onClick = { onSettingSelected(MatrixSettingType.GlowIntensity) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Jitter Amount",
                    description = "Horizontal jitter movement",
                    currentValue = "${settings.jitterAmount.toInt()}px",
                    onClick = { onSettingSelected(MatrixSettingType.JitterAmount) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Flicker Rate",
                    description = "Character flicker frequency",
                    currentValue = "${(settings.flickerRate * 100).toInt()}%",
                    onClick = { onSettingSelected(MatrixSettingType.FlickerRate) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Mutation Rate",
                    description = "Character change frequency",
                    currentValue = "${(settings.mutationRate * 100).toInt()}%",
                    onClick = { onSettingSelected(MatrixSettingType.MutationRate) },
                    ui = ui
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Timing & Behavior Section
            ModernSection(title = "TIMING & BEHAVIOR", ui = ui) {
                ModernSettingItem(
                    title = "Start Delay",
                    description = "Delay before columns start",
                    currentValue = "${settings.columnStartDelay.toInt()}s",
                    onClick = { onSettingSelected(MatrixSettingType.ColumnStartDelay) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Restart Delay",
                    description = "Delay before columns restart",
                    currentValue = "${settings.columnRestartDelay.toInt()}s",
                    onClick = { onSettingSelected(MatrixSettingType.ColumnRestartDelay) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Active Percentage",
                    description = "Percentage of columns starting active",
                    currentValue = "${(settings.initialActivePercentage * 100).toInt()}%",
                    onClick = { onSettingSelected(MatrixSettingType.InitialActivePercentage) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Speed Variation",
                    description = "How often columns change speed",
                    currentValue = "${(settings.speedVariationRate * 1000).toInt()}/1000",
                    onClick = { onSettingSelected(MatrixSettingType.SpeedVariationRate) },
                    ui = ui
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Background Effects Section
            ModernSection(title = "BACKGROUND EFFECTS", ui = ui) {
                ModernSettingItem(
                    title = "Grain Density",
                    description = "Number of grain texture points",
                    currentValue = "${settings.grainDensity}",
                    onClick = { onSettingSelected(MatrixSettingType.GrainDensity) },
                    ui = ui
                )
                
                ModernSettingItem(
                    title = "Grain Opacity",
                    description = "Opacity of grain texture",
                    currentValue = "${(settings.grainOpacity * 100).toInt()}%",
                    onClick = { onSettingSelected(MatrixSettingType.GrainOpacity) },
                    ui = ui
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // System Actions Section
            ModernSection(title = "SYSTEM ACTIONS", ui = ui) {
                ModernSettingItem(
                    title = "Reset All",
                    description = "Restore defaults for all settings",
                    currentValue = "Restore defaults",
                    onClick = onResetSettings,
                    ui = ui
                )
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
        }
    }
}

/**
 * Future feature item that shows coming soon message
 */
@Composable
private fun FutureFeatureItem(
    title: String,
    description: String,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme
) {
    val backgroundColor by animateColorAsState(
        targetValue = ui.backgroundSecondary,
        animationSpec = tween(200),
        label = "background"
    )
    
    val borderColor by animateColorAsState(
        targetValue = ui.borderDim,
        animationSpec = tween(200),
        label = "border"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = AppTypography.bodyMedium,
                    color = ui.textPrimary
                )
                
                Text(
                    text = "🚀 Future",
                    style = AppTypography.bodySmall,
                    color = ui.textSecondary
                )
            }
            
            Text(
                text = description,
                style = AppTypography.bodyMedium,
                color = ui.textSecondary
            )
        }
    }
}

/**
 * Modern setting item that can be tapped to start editing
 */
@Composable
private fun ModernSettingItem(
    title: String,
    description: String,
    currentValue: String,
    onClick: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme
) {
    val backgroundColor by animateColorAsState(
        targetValue = ui.backgroundSecondary,
        animationSpec = tween(200),
        label = "background_color"
    )
    val borderColor by animateColorAsState(
        targetValue = ui.borderDim,
        animationSpec = tween(200),
        label = "border_color"
    )
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = AppTypography.bodyMedium,
                    color = ui.textPrimary
                )
                
                Text(
                    text = description,
                    style = AppTypography.bodySmall,
                    color = ui.textSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = currentValue,
                        style = AppTypography.bodyMedium,
                        color = ui.textAccent
                    )
                    
                    Text(
                        text = "TAP TO EDIT",
                        style = AppTypography.bodySmall,
                        color = ui.textSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = ui.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Modern header component
 */
@Composable
private fun ModernHeader(
    title: String,
    onBackPressed: () -> Unit,
    onResetPressed: () -> Unit,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    settings: MatrixSettings
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
                    ui.backgroundSecondary,
                    RoundedCornerShape(8.dp)
                )
                .border(1.dp, ui.border, RoundedCornerShape(8.dp))
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = ui.primary
            )
        }
        
        ModernTextWithGlow(
            text = title,
            style = AppTypography.headlineSmall,
            color = ui.textPrimary,
            settings = settings
        )
        
        IconButton(
            onClick = onResetPressed,
            modifier = Modifier
                .background(
                    ui.backgroundSecondary,
                    RoundedCornerShape(8.dp)
                )
                .border(1.dp, ui.border, RoundedCornerShape(8.dp))
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Reset",
                tint = ui.primary
            )
        }
    }
}

/**
 * Modern section component
 */
@Composable
private fun ModernSection(
    title: String,
    ui: com.example.matrixscreen.ui.theme.MatrixUIColorScheme,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = AppTypography.titleSmall,
            color = ui.textAccent,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    ui.backgroundSecondary,
                    RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    ui.borderDim,
                    RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                content()
            }
        }
    }
}
