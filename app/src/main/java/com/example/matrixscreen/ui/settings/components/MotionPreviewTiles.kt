package com.example.matrixscreen.ui.settings.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.data.model.MatrixSettings
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Preview tiles for motion settings that show live previews of the matrix rain effect.
 * 
 * These components provide visual feedback for motion-related settings changes,
 * allowing users to see the impact of their adjustments in real-time.
 */

/**
 * A mini preview tile that shows a simplified matrix rain effect.
 * 
 * @param settings The current matrix settings to preview
 * @param modifier Modifier for the component
 */
@Composable
fun MotionPreviewTile(
    settings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "motionPreview")
    
    // Animate the falling speed based on settings
    val fallSpeed by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1000 / settings.fallSpeed).toInt().coerceAtLeast(100),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "fallSpeed"
    )
    
    // Animate column activity based on active percentage
    val columnActivity by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "columnActivity"
    )
    
    Box(
        modifier = modifier
            .size(120.dp, 80.dp)
            .clip(RoundedCornerShape(DesignTokens.Radius.md))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = DesignTokens.Outline.thin,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(DesignTokens.Radius.md)
            )
    ) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(settings.backgroundColor))
        )
        
        // Matrix rain preview
        MatrixRainPreview(
            fallSpeed = fallSpeed,
            columnCount = settings.columnCount,
            activePercentage = settings.activePercentage,
            columnActivity = columnActivity,
            lineSpacing = settings.lineSpacing,
            speedVariance = settings.speedVariance
        )
        
        // Overlay label
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(DesignTokens.Spacing.xs)
        ) {
            Text(
                text = "Preview",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

/**
 * A compact preview tile for individual motion settings.
 * 
 * @param label The setting label
 * @param value The current value
 * @param unit The value unit
 * @param previewContent The preview content to show
 * @param modifier Modifier for the component
 */
@Composable
fun CompactMotionPreview(
    label: String,
    value: Any,
    unit: String? = null,
    previewContent: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(100.dp)
            .clip(RoundedCornerShape(DesignTokens.Radius.sm))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(DesignTokens.Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.xs)
    ) {
        // Preview content
        Box(
            modifier = Modifier
                .size(60.dp, 40.dp)
                .clip(RoundedCornerShape(DesignTokens.Radius.xs))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            previewContent()
        }
        
        // Label and value
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 9.sp
        )
        
        Text(
            text = "$value${unit?.let { " $it" } ?: ""}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * A simplified matrix rain preview animation.
 * 
 * @param fallSpeed The current fall speed animation value
 * @param columnCount The number of columns to show
 * @param activePercentage The percentage of active columns
 * @param columnActivity The column activity animation value
 * @param lineSpacing The line spacing setting
 * @param speedVariance The speed variance setting
 */
@Composable
private fun MatrixRainPreview(
    fallSpeed: Float,
    columnCount: Int,
    activePercentage: Float,
    columnActivity: Float,
    lineSpacing: Float,
    speedVariance: Float
) {
    val columnsToShow = (columnCount / 10).coerceAtMost(20) // Scale down for preview
    val activeColumns = (columnsToShow * activePercentage).toInt()
    
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(columnsToShow) { columnIndex ->
            val isActive = columnIndex < activeColumns
            val columnSpeed = fallSpeed * (1f + Random.nextFloat() * speedVariance)
            
            if (isActive) {
                Column(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                        .graphicsLayer {
                            translationY = (fallSpeed % 100f) - 100f
                        }
                ) {
                    repeat(8) { lineIndex ->
                        val alpha = (1f - (lineIndex * 0.15f)).coerceAtLeast(0.1f)
                        val char = if (Random.nextFloat() < 0.1f) {
                            Random.nextInt(0x30A0, 0x30FF).toChar() // Katakana range
                        } else {
                            Random.nextInt(0x0030, 0x0039).toChar() // Numbers
                        }
                        
                        Text(
                            text = char.toString(),
                            color = Color.Green.copy(alpha = alpha),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = (lineSpacing * 2).dp)
                        )
                    }
                }
            } else {
                // Inactive column - show dimmed characters
                Column(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                ) {
                    repeat(3) { lineIndex ->
                        val char = Random.nextInt(0x0030, 0x0039).toChar()
                        Text(
                            text = char.toString(),
                            color = Color.Green.copy(alpha = 0.2f),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * A preview tile specifically for speed settings.
 */
@Composable
fun SpeedPreviewTile(
    speed: Float,
    modifier: Modifier = Modifier
) {
    CompactMotionPreview(
        label = "Speed",
        value = String.format("%.1f", speed),
        unit = "x",
        modifier = modifier,
        previewContent = {
            val infiniteTransition = rememberInfiniteTransition(label = "speedPreview")
            val animationSpeed by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (1000 / speed).toInt().coerceAtLeast(100),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "speedAnimation"
            )
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Animated dots showing speed
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) { index ->
                        val dotAlpha = (1f - (index * 0.3f)).coerceAtLeast(0.1f)
                        val dotOffset = (animationSpeed * 20f - (index * 7f)).coerceAtLeast(0f)
                        
                        Box(
                            modifier = Modifier
                                .size(3.dp)
                                .background(
                                    Color.Green.copy(alpha = dotAlpha),
                                    RoundedCornerShape(50)
                                )
                                .graphicsLayer {
                                    translationY = dotOffset
                                }
                        )
                    }
                }
            }
        }
    )
}

/**
 * A preview tile specifically for column count settings.
 */
@Composable
fun ColumnCountPreviewTile(
    columnCount: Int,
    modifier: Modifier = Modifier
) {
    CompactMotionPreview(
        label = "Columns",
        value = columnCount,
        modifier = modifier,
        previewContent = {
            val columnsToShow = (columnCount / 20).coerceAtMost(8)
            
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(columnsToShow) { index ->
                    val height = (20 + Random.nextInt(0, 20)).dp
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(height)
                            .background(
                                Color.Green.copy(alpha = 0.6f),
                                RoundedCornerShape(1.dp)
                            )
                    )
                }
            }
        }
    )
}

/**
 * A preview tile specifically for active percentage settings.
 */
@Composable
fun ActivePercentagePreviewTile(
    activePercentage: Float,
    modifier: Modifier = Modifier
) {
    CompactMotionPreview(
        label = "Active",
        value = "${(activePercentage * 100).toInt()}%",
        modifier = modifier,
        previewContent = {
            val activeColumns = (8 * activePercentage).toInt()
            
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(8) { index ->
                    val isActive = index < activeColumns
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                if (isActive) Color.Green else Color.Gray.copy(alpha = 0.3f),
                                RoundedCornerShape(50)
                            )
                    )
                }
            }
        }
    )
}