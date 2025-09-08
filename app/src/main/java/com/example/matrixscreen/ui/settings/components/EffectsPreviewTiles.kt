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
 * Preview tiles for effects settings that show live previews of visual effects.
 * 
 * These components provide visual feedback for effects-related settings changes,
 * allowing users to see the impact of their adjustments in real-time.
 */

/**
 * A mini preview tile that shows a simplified matrix rain effect with effects applied.
 * 
 * @param settings The current matrix settings to preview
 * @param modifier Modifier for the component
 */
@Composable
fun EffectsPreviewTile(
    settings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "effectsPreview")
    
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
    
    // Animate glow effect
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )
    
    // Animate jitter effect
    val jitterOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jitterOffset"
    )
    
    // Animate flicker effect
    val flickerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 100,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flickerAlpha"
    )
    
    Box(
        modifier = modifier
            .size(120.dp, 80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Color.Black,
                RoundedCornerShape(8.dp)
            )
            .border(
                1.dp,
                Color.Green.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
    ) {
        // Render simplified matrix rain with effects
        MatrixRainWithEffects(
            fallSpeed = fallSpeed,
            columnCount = settings.columnCount,
            activePercentage = settings.activePercentage,
            lineSpacing = settings.lineSpacing,
            speedVariance = settings.speedVariance,
            glowIntensity = settings.glowIntensity,
            jitterAmount = settings.jitterAmount,
            flickerAmount = settings.flickerAmount,
            mutationRate = settings.mutationRate,
            glowPulse = glowPulse,
            jitterOffset = jitterOffset,
            flickerAlpha = flickerAlpha
        )
    }
}

/**
 * Simplified matrix rain with effects applied for preview.
 */
@Composable
private fun MatrixRainWithEffects(
    fallSpeed: Float,
    columnCount: Int,
    activePercentage: Float,
    lineSpacing: Float,
    speedVariance: Float,
    glowIntensity: Float,
    jitterAmount: Float,
    flickerAmount: Float,
    mutationRate: Float,
    glowPulse: Float,
    jitterOffset: Float,
    flickerAlpha: Float
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
                            // Apply jitter effect
                            translationX = jitterOffset * jitterAmount
                        }
                ) {
                    repeat(8) { lineIndex ->
                        val baseAlpha = (1f - (lineIndex * 0.15f)).coerceAtLeast(0.1f)
                        val alpha = baseAlpha * flickerAlpha * (1f - flickerAmount * 0.5f)
                        
                        // Apply glow effect
                        val glowAlpha = (glowIntensity * glowPulse * 0.3f).coerceAtMost(0.5f)
                        
                        val char = if (Random.nextFloat() < mutationRate) {
                            Random.nextInt(0x30A0, 0x30FF).toChar() // Katakana range
                        } else {
                            Random.nextInt(0x0030, 0x0039).toChar() // Numbers
                        }
                        
                        // Main character
                        Text(
                            text = char.toString(),
                            color = Color.Green.copy(alpha = alpha),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = (lineSpacing * 2).dp)
                        )
                        
                        // Glow effect
                        if (glowIntensity > 0) {
                            Text(
                                text = char.toString(),
                                color = Color.Green.copy(alpha = glowAlpha),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .padding(vertical = (lineSpacing * 2).dp)
                                    .graphicsLayer {
                                        scaleX = 1.2f
                                        scaleY = 1.2f
                                    }
                            )
                        }
                    }
                }
            } else {
                // Inactive column - show dimmed characters
                Column(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight()
                ) {
                    repeat(8) { lineIndex ->
                        val alpha = 0.1f
                        val char = Random.nextInt(0x0030, 0x0039).toChar()
                        
                        Text(
                            text = char.toString(),
                            color = Color.Green.copy(alpha = alpha),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(vertical = (lineSpacing * 2).dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Glow intensity preview tile.
 */
@Composable
fun GlowPreviewTile(
    glowIntensity: Float,
    modifier: Modifier = Modifier
) {
    CompactMotionPreview(
        label = "Glow",
        value = String.format("%.1f", glowIntensity),
        unit = "x",
        modifier = modifier,
        previewContent = {
            val infiniteTransition = rememberInfiniteTransition(label = "glowPreview")
            val glowPulse by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 1500,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glowPulse"
            )
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Glow effect visualization
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            Color.Green.copy(alpha = glowIntensity * glowPulse * 0.5f),
                            RoundedCornerShape(50)
                        )
                        .graphicsLayer {
                            scaleX = 1f + (glowIntensity * glowPulse * 0.3f)
                            scaleY = 1f + (glowIntensity * glowPulse * 0.3f)
                        }
                )
            }
        }
    )
}

/**
 * Jitter amount preview tile.
 */
@Composable
fun JitterPreviewTile(
    jitterAmount: Float,
    modifier: Modifier = Modifier
) {
    CompactMotionPreview(
        label = "Jitter",
        value = String.format("%.1f", jitterAmount),
        unit = "px",
        modifier = modifier,
        previewContent = {
            val infiniteTransition = rememberInfiniteTransition(label = "jitterPreview")
            val jitterOffset by infiniteTransition.animateFloat(
                initialValue = -1f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 200,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "jitterOffset"
            )
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Jitter effect visualization
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(
                                    Color.Green,
                                    RoundedCornerShape(50)
                                )
                                .graphicsLayer {
                                    translationX = jitterOffset * jitterAmount * (index + 1)
                                }
                        )
                    }
                }
            }
        }
    )
}

/**
 * Flicker amount preview tile.
 */
@Composable
fun FlickerPreviewTile(
    flickerAmount: Float,
    modifier: Modifier = Modifier
) {
    CompactMotionPreview(
        label = "Flicker",
        value = String.format("%.2f", flickerAmount),
        unit = "x",
        modifier = modifier,
        previewContent = {
            val infiniteTransition = rememberInfiniteTransition(label = "flickerPreview")
            val flickerAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 100,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "flickerAlpha"
            )
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Flicker effect visualization
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(
                            Color.Green.copy(alpha = flickerAlpha * (1f - flickerAmount * 0.5f)),
                            RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    )
}

/**
 * Mutation rate preview tile.
 */
@Composable
fun MutationPreviewTile(
    mutationRate: Float,
    modifier: Modifier = Modifier
) {
    CompactMotionPreview(
        label = "Mutation",
        value = String.format("%.2f", mutationRate),
        unit = "x",
        modifier = modifier,
        previewContent = {
            val infiniteTransition = rememberInfiniteTransition(label = "mutationPreview")
            val mutationCycle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = (1000 / mutationRate.coerceAtLeast(0.01f)).toInt().coerceAtLeast(100),
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                ),
                label = "mutationCycle"
            )
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Mutation effect visualization
                Row(
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    repeat(5) { index ->
                        val char = if (mutationCycle > (index * 0.2f)) {
                            Random.nextInt(0x30A0, 0x30FF).toChar() // Katakana
                        } else {
                            Random.nextInt(0x0030, 0x0039).toChar() // Numbers
                        }
                        
                        Text(
                            text = char.toString(),
                            color = Color.Green,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    )
}
