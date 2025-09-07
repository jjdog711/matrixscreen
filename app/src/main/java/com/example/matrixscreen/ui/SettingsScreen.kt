package com.example.matrixscreen.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matrixscreen.data.MatrixColor
import com.example.matrixscreen.data.MatrixSettings
import com.example.matrixscreen.data.SymbolSet
import com.example.matrixscreen.ui.components.MatrixThemeSelector

/**
 * Cyberpunk-themed settings screen with Matrix customization options
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val scrollState = rememberScrollState()
    
    // Cyberpunk color scheme
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
                title = "MATRIX SETTINGS",
                onBackPressed = onBackPressed,
                onResetPressed = { viewModel.resetSettings() }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Fall Speed Setting
            CyberpunkSection(title = "FALL SPEED") {
                FallSpeedSlider(
                    value = settings.fallSpeed,
                    onValueChange = { viewModel.updateFallSpeed(it) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Symbol Set Setting
            CyberpunkSection(title = "SYMBOL SET") {
                SymbolSetSelector(
                    currentSet = settings.symbolSet,
                    onSetChanged = { viewModel.updateSymbolSet(it) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Matrix Theme Presets
            CyberpunkSection(title = "MATRIX THEMES") {
                MatrixThemeSelector(
                    currentThemeName = settings.selectedThemeName,
                    onThemeSelected = { theme -> viewModel.applyMatrixTheme(theme) }
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Advanced Settings Toggle
            CyberpunkSection(title = "ADVANCED SETTINGS") {
                AdvancedSettingsToggle(
                    isExpanded = remember { mutableStateOf(false) },
                    settings = settings,
                    viewModel = viewModel
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Matrix Effects Info
            if (settings.symbolSet == SymbolSet.MATRIX_AUTHENTIC || settings.symbolSet == SymbolSet.MATRIX_GLITCH) {
                CyberpunkSection(title = "MATRIX EFFECTS") {
                    MatrixEffectsInfo()
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Preview section
            CyberpunkSection(title = "PREVIEW") {
                MatrixPreview(settings = settings)
            }
            
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
        }
    }
}

@Composable
private fun CyberpunkHeader(
    title: String,
    onBackPressed: () -> Unit,
    onResetPressed: () -> Unit
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
        
        IconButton(
            onClick = onResetPressed,
            modifier = Modifier
                .background(
                    Color(0xFF1A1A1A),
                    RoundedCornerShape(8.dp)
                )
                .border(1.dp, Color(0xFF00FF00), RoundedCornerShape(8.dp))
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Reset",
                tint = Color(0xFF00FF00)
            )
        }
    }
}

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
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun FallSpeedSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Speed:",
                color = Color(0xFFCCCCCC),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "${(value * 100).toInt()}%",
                color = Color(0xFF00FF00),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0.5f..5.0f,
            steps = 17, // 0.5, 0.75, 1.0, 1.25, ... 5.0
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF00FF00),
                activeTrackColor = Color(0xFF00FF00),
                inactiveTrackColor = Color(0xFF333333)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SLOW",
                color = Color(0xFF666666),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "FAST",
                color = Color(0xFF666666),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun SymbolSetSelector(
    currentSet: SymbolSet,
    onSetChanged: (SymbolSet) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SymbolSet.values().forEach { symbolSet ->
            val isSelected = symbolSet == currentSet
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
            
            // Special styling for Matrix character sets
            val isMatrixSet = symbolSet == SymbolSet.MATRIX_AUTHENTIC || symbolSet == SymbolSet.MATRIX_GLITCH
            val matrixBorderColor = if (isSelected) Color(0xFF00FF00) else Color(0xFF006600)
            val finalBorderColor = if (isMatrixSet) matrixBorderColor else borderColor
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor, RoundedCornerShape(8.dp))
                    .border(
                        2.dp, 
                        if (isMatrixSet) finalBorderColor else Color(0xFF333333), 
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onSetChanged(symbolSet) }
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = symbolSet.displayName,
                            color = if (isSelected) Color(0xFF00FF00) else Color(0xFFCCCCCC),
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        
                        // Special indicator for Matrix sets
                        if (isMatrixSet) {
                            Text(
                                text = "★",
                                color = Color(0xFF00FF00),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    
                    Text(
                        text = when (symbolSet) {
                            SymbolSet.MATRIX_AUTHENTIC -> "Authentic Matrix movie characters with half-width Katakana"
                            SymbolSet.MATRIX_GLITCH -> "Matrix characters + glitch symbols and custom glyphs"
                            else -> symbolSet.characters.take(20) + if (symbolSet.characters.length > 20) "..." else ""
                        },
                        color = if (isMatrixSet) Color(0xFF00CC00) else Color(0xFF666666),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    
                    // Character count for Matrix sets
                    if (isMatrixSet) {
                        Text(
                            text = "${symbolSet.characters.length} characters",
                            color = Color(0xFF888888),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun AdvancedSettingsToggle(
    isExpanded: MutableState<Boolean>,
    settings: MatrixSettings,
    viewModel: SettingsViewModel
) {
    Column {
        // Toggle button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isExpanded.value) Color(0xFF003300) else Color(0xFF1A1A1A),
                    RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    if (isExpanded.value) Color(0xFF00FF00) else Color(0xFF333333),
                    RoundedCornerShape(8.dp)
                )
                .clickable { isExpanded.value = !isExpanded.value }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isExpanded.value) "Hide Advanced Settings" else "Show Advanced Settings",
                color = Color(0xFF00FF00),
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = if (isExpanded.value) "▲" else "▼",
                color = Color(0xFF00FF00),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        
        // Advanced settings content
        if (isExpanded.value) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Animation Configuration
            AdvancedSettingsGroup(
                title = "ANIMATION CONFIGURATION",
                icon = "⚙️"
            ) {
                AnimationConfigSettings(settings, viewModel)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Visual Effects
            AdvancedSettingsGroup(
                title = "VISUAL EFFECTS",
                icon = "✨"
            ) {
                VisualEffectsSettings(settings, viewModel)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Timing & Behavior
            AdvancedSettingsGroup(
                title = "TIMING & BEHAVIOR",
                icon = "⏱️"
            ) {
                TimingBehaviorSettings(settings, viewModel)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Background Effects
            AdvancedSettingsGroup(
                title = "BACKGROUND EFFECTS",
                icon = "🎨"
            ) {
                BackgroundEffectsSettings(settings, viewModel)
            }
        }
    }
}

@Composable
private fun AdvancedSettingsGroup(
    title: String,
    icon: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = "$icon $title",
            color = Color(0xFF00FF00),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF0F0F0F),
                    RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    Color(0xFF333333),
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun AnimationConfigSettings(
    settings: MatrixSettings,
    viewModel: SettingsViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Font Size
        SettingSlider(
            label = "Font Size",
            value = settings.fontSize,
            valueRange = 8f..24f,
            onValueChange = viewModel::updateFontSize,
            unit = "dp",
            description = "Size of Matrix characters"
        )
        
        // Column Count
        SettingSlider(
            label = "Column Count",
            value = settings.columnCount.toFloat(),
            valueRange = 50f..150f,
            onValueChange = { viewModel.updateColumnCount(it.toInt()) },
            unit = "columns",
            description = "Number of Matrix columns"
        )
        
        // Target FPS
        SettingSlider(
            label = "Target FPS",
            value = settings.targetFps,
            valueRange = 15f..60f,
            onValueChange = viewModel::updateTargetFps,
            unit = "fps",
            description = "Animation frame rate"
        )
        
        // Row Height Multiplier
        SettingSlider(
            label = "Row Height",
            value = settings.rowHeightMultiplier,
            valueRange = 0.7f..1.2f,
            onValueChange = viewModel::updateRowHeightMultiplier,
            unit = "x",
            description = "Vertical spacing between rows"
        )
        
        // Max Trail Length
        SettingSlider(
            label = "Max Trail Length",
            value = settings.maxTrailLength.toFloat(),
            valueRange = 20f..100f,
            onValueChange = { viewModel.updateMaxTrailLength(it.toInt()) },
            unit = "chars",
            description = "Maximum characters in trail (Matrix movie has long trails)"
        )
        
        // Max Bright Trail Length
        SettingSlider(
            label = "Max Bright Trail Length",
            value = settings.maxBrightTrailLength.toFloat(),
            valueRange = 2f..15f,
            onValueChange = { viewModel.updateMaxBrightTrailLength(it.toInt()) },
            unit = "chars",
            description = "Maximum bright green characters behind head (varies per column)"
        )
    }
}

@Composable
private fun VisualEffectsSettings(
    settings: MatrixSettings,
    viewModel: SettingsViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Glow Intensity
        SettingSlider(
            label = "Glow Intensity",
            value = settings.glowIntensity,
            valueRange = 0.0f..2.0f,
            onValueChange = viewModel::updateGlowIntensity,
            unit = "x",
            description = "Intensity of character glow effects"
        )
        
        // Jitter Amount
        SettingSlider(
            label = "Jitter Amount",
            value = settings.jitterAmount,
            valueRange = 0.0f..3.0f,
            onValueChange = viewModel::updateJitterAmount,
            unit = "px",
            description = "Horizontal jitter movement"
        )
        
        // Flicker Rate
        SettingSlider(
            label = "Flicker Rate",
            value = settings.flickerRate,
            valueRange = 0.0f..0.2f,
            onValueChange = viewModel::updateFlickerRate,
            unit = "%",
            description = "Character flicker frequency",
            formatValue = { "${(it * 100).toInt()}" }
        )
        
        // Mutation Rate
        SettingSlider(
            label = "Mutation Rate",
            value = settings.mutationRate,
            valueRange = 0.0f..0.1f,
            onValueChange = viewModel::updateMutationRate,
            unit = "%",
            description = "Character change frequency",
            formatValue = { "${(it * 100).toInt()}" }
        )
    }
}

@Composable
private fun TimingBehaviorSettings(
    settings: MatrixSettings,
    viewModel: SettingsViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Column Start Delay
        SettingSlider(
            label = "Start Delay",
            value = settings.columnStartDelay,
            valueRange = 0f..10f,
            onValueChange = viewModel::updateColumnStartDelay,
            unit = "s",
            description = "Delay before columns start"
        )
        
        // Column Restart Delay
        SettingSlider(
            label = "Restart Delay",
            value = settings.columnRestartDelay,
            valueRange = 0.5f..5.0f,
            onValueChange = viewModel::updateColumnRestartDelay,
            unit = "s",
            description = "Delay before columns restart"
        )
        
        // Initial Active Percentage
        SettingSlider(
            label = "Initial Active %",
            value = settings.initialActivePercentage,
            valueRange = 0.1f..0.8f,
            onValueChange = viewModel::updateInitialActivePercentage,
            unit = "%",
            description = "Percentage of columns starting active",
            formatValue = { "${(it * 100).toInt()}" }
        )
        
        // Speed Variation Rate
        SettingSlider(
            label = "Speed Variation Rate",
            value = settings.speedVariationRate,
            valueRange = 0.0f..0.01f,
            onValueChange = viewModel::updateSpeedVariationRate,
            unit = "/frame",
            formatValue = { "${(it * 1000).toInt()}/1000" },
            description = "How often columns change speed during runtime"
        )
    }
}

@Composable
private fun BackgroundEffectsSettings(
    settings: MatrixSettings,
    viewModel: SettingsViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Grain Density
        SettingSlider(
            label = "Grain Density",
            value = settings.grainDensity.toFloat(),
            valueRange = 0f..500f,
            onValueChange = { viewModel.updateGrainDensity(it.toInt()) },
            unit = "points",
            description = "Number of grain texture points"
        )
        
        // Grain Opacity
        SettingSlider(
            label = "Grain Opacity",
            value = settings.grainOpacity,
            valueRange = 0.0f..0.1f,
            onValueChange = viewModel::updateGrainOpacity,
            unit = "%",
            description = "Opacity of grain texture",
            formatValue = { "${(it * 100).toInt()}" }
        )
    }
}

@Composable
private fun SettingSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    unit: String,
    description: String,
    formatValue: (Float) -> String = { it.toString() }
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color(0xFFCCCCCC),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "${formatValue(value)}$unit",
                color = Color(0xFF00FF00),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = ((valueRange.endInclusive - valueRange.start) / 0.1f).toInt() - 1,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF00FF00),
                activeTrackColor = Color(0xFF00FF00),
                inactiveTrackColor = Color(0xFF333333)
            ),
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = description,
            color = Color(0xFF666666),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun MatrixEffectsInfo() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Font System Info
        EffectInfoItem(
            title = "AUTHENTIC FONT SYSTEM",
            description = "Matrix Code NFI font with hybrid fallback for optimal character rendering",
            features = listOf("Half-width Katakana", "Authentic Matrix glyphs", "Character-specific typefaces")
        )
        
        // Visual Effects Info
        EffectInfoItem(
            title = "ENHANCED VISUAL EFFECTS",
            description = "Movie-accurate visual enhancements for authentic Matrix experience",
            features = listOf("Multi-level glow effects", "Jitter & drift simulation", "Flicker interference", "Fade trail gradients")
        )
        
        // Density & Timing Info
        EffectInfoItem(
            title = "AUTHENTIC DENSITY & TIMING",
            description = "Optimized for movie-accurate screen coverage and organic patterns",
            features = listOf("100 columns (increased density)", "Organic timing variations", "Staggered column starts", "Shorter authentic trails")
        )
        
        // Background Enhancement
        EffectInfoItem(
            title = "ATMOSPHERIC BACKGROUND",
            description = "Deep green-black background with subtle film grain texture",
            features = listOf("Matrix green-black (#001a00)", "Subtle grain overlay", "Enhanced visual depth")
        )
    }
}

@Composable
private fun EffectInfoItem(
    title: String,
    description: String,
    features: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            color = Color(0xFF00FF00),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = description,
            color = Color(0xFFCCCCCC),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 12.sp
        )
        
        features.forEach { feature ->
            Text(
                text = "• $feature",
                color = Color(0xFF888888),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun MatrixPreview(settings: MatrixSettings) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color(0xFF001a00), RoundedCornerShape(8.dp)) // Matrix green-black background
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(8.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Main character preview
            Text(
                text = when (settings.symbolSet) {
                    SymbolSet.MATRIX_AUTHENTIC -> "ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヲン"
                    SymbolSet.MATRIX_GLITCH -> "ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヲン◊◈◆◇▲△▼▽"
                    else -> settings.symbolSet.characters.take(15)
                },
                color = settings.colorTint.color,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            
            // Settings summary
            Text(
                text = "${settings.symbolSet.displayName}",
                color = settings.colorTint.color,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "${settings.colorTint.displayName} • Speed: ${(settings.fallSpeed * 100).toInt()}%",
                color = Color(0xFF666666),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
            
            // Special Matrix features indicator
            if (settings.symbolSet == SymbolSet.MATRIX_AUTHENTIC || settings.symbolSet == SymbolSet.MATRIX_GLITCH) {
                Text(
                    text = "★ Enhanced Matrix Effects: Glow • Jitter • Flicker • Authentic Font",
                    color = Color(0xFF00CC00),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
