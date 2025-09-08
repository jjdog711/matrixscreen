package com.example.matrixscreen.ui.settings.effects

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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.data.model.MatrixSettings
import kotlin.random.Random

/**
 * Live preview tile that demonstrates the current effects settings.
 * 
 * Shows a mini matrix rain effect with the current glow, jitter, flicker, and mutation
 * settings applied in real-time. Provides immediate visual feedback as users adjust
 * the effects controls.
 * 
 * The preview is designed to be compact but informative, giving users a clear sense
 * of how their settings will affect the full matrix rain effect.
 */
@Composable
fun EffectsPreviewTile(
    settings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "effectsPreview")
    
    // Extract effect values
    val glowIntensity = settings.glowIntensity
    val jitterAmount = settings.jitterAmount
    val flickerAmount = settings.flickerAmount
    val mutationRate = settings.mutationRate
    
    // Generate preview characters
    val previewChars = remember { generatePreviewCharacters() }
    
    // Animation states for each effect
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1000 + glowIntensity * 500).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )
    
    val jitterOffset by infiniteTransition.animateFloat(
        initialValue = -jitterAmount,
        targetValue = jitterAmount,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (200 + jitterAmount * 100).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jitterOffset"
    )
    
    val flickerAlpha by infiniteTransition.animateFloat(
        initialValue = 1f - flickerAmount * 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (100 + flickerAmount * 200).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flickerAlpha"
    )
    
    val mutationScale by infiniteTransition.animateFloat(
        initialValue = 1f - mutationRate * 0.2f,
        targetValue = 1f + mutationRate * 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (800 + mutationRate * 400).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mutationScale"
    )
    
    // Preview container
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(DesignTokens.Radius.md),
        elevation = CardDefaults.cardElevation(
            defaultElevation = DesignTokens.Elevation.sm
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(DesignTokens.Spacing.sm)
        ) {
            // Matrix rain preview
            MatrixRainPreview(
                characters = previewChars,
                glowIntensity = glowIntensity,
                glowPulse = glowPulse,
                jitterOffset = jitterOffset,
                flickerAmount = flickerAmount,
                flickerAlpha = flickerAlpha,
                mutationRate = mutationRate,
                mutationScale = mutationScale,
                modifier = Modifier.fillMaxSize()
            )
            
            // Preview label
            Text(
                text = "Live Preview",
                color = Color.Green.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
            )
        }
    }
}

/**
 * Mini matrix rain effect for the preview tile.
 */
@Composable
private fun MatrixRainPreview(
    characters: List<String>,
    glowIntensity: Float,
    glowPulse: Float,
    jitterOffset: Float,
    flickerAmount: Float,
    flickerAlpha: Float,
    mutationRate: Float,
    mutationScale: Float,
    modifier: Modifier = Modifier
) {
    val columns = 8
    val rows = 6
    
    Box(
        modifier = modifier
    ) {
        // Generate matrix columns
        repeat(columns) { col ->
            repeat(rows) { row ->
                val charIndex = (col * rows + row) % characters.size
                val char = characters[charIndex]
                
                // Apply effects to each character
                val charJitter = jitterOffset * (if (row % 2 == 0) 1f else -1f)
                val charFlicker = flickerAlpha * (if (Random.nextFloat() < flickerAmount) 0.3f else 1f)
                val charMutation = mutationScale * (if (Random.nextFloat() < mutationRate) 0.8f else 1f)
                val charGlow = glowPulse * (1f + glowIntensity * 0.1f) // Apply glow pulse with intensity scaling
                
                Text(
                    text = char,
                    color = Color.Green.copy(alpha = charFlicker),
                    fontSize = (12 * charMutation).sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(
                            x = (col * 20 + charJitter).dp,
                            y = (row * 16).dp
                        )
                        .graphicsLayer {
                            // Apply glow effect with pulse animation
                            alpha = charFlicker
                            scaleX = charMutation * charGlow
                            scaleY = charMutation * charGlow
                        }
                )
            }
        }
    }
}

/**
 * Generate preview characters for the matrix effect.
 */
private fun generatePreviewCharacters(): List<String> {
    val chars = mutableListOf<String>()
    
    // Add some matrix-style characters
    chars.addAll(listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))
    chars.addAll(listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J"))
    chars.addAll(listOf("K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"))
    chars.addAll(listOf("U", "V", "W", "X", "Y", "Z"))
    chars.addAll(listOf("!", "@", "#", "$", "%", "^", "&", "*", "(", ")"))
    chars.addAll(listOf("+", "-", "=", "[", "]", "{", "}", "|", "\\", "/"))
    chars.addAll(listOf(":", ";", "\"", "'", ",", ".", "<", ">", "?", "~"))
    
    return chars.shuffled()
}

/**
 * Compact effects preview for smaller spaces.
 */
@Composable
fun CompactEffectsPreview(
    settings: MatrixSettings,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "compactPreview")
    
    // Simplified animations for compact view
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "compactGlow"
    )
    
    val jitterOffset by infiniteTransition.animateFloat(
        initialValue = -settings.jitterAmount * 0.5f,
        targetValue = settings.jitterAmount * 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "compactJitter"
    )
    
    val flickerAlpha by infiniteTransition.animateFloat(
        initialValue = 1f - settings.flickerAmount * 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(150, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "compactFlicker"
    )
    
    Box(
        modifier = modifier
            .size(60.dp)
            .background(
                Color.Black.copy(alpha = 0.8f),
                RoundedCornerShape(DesignTokens.Radius.sm)
            )
            .border(
                width = 1.dp,
                color = Color.Green.copy(alpha = 0.5f),
                shape = RoundedCornerShape(DesignTokens.Radius.sm)
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "MATRIX",
            color = Color.Green.copy(alpha = flickerAlpha),
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .offset(x = jitterOffset.dp)
                .scale(glowPulse)
        )
    }
}
