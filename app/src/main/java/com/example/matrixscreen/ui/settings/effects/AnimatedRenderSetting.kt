package com.example.matrixscreen.ui.settings.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.ui.settings.components.RenderSetting
import com.example.matrixscreen.ui.settings.model.WidgetSpec
import com.example.matrixscreen.ui.settings.model.Glow
import com.example.matrixscreen.ui.settings.model.Jitter
import com.example.matrixscreen.ui.settings.model.Flicker
import com.example.matrixscreen.ui.settings.model.Mutation

/**
 * Animated version of RenderSetting with micro-animations for effects settings.
 * 
 * Provides tasteful, minimal animations that communicate the function of each effect:
 * - Glow: Gentle pulsing animation when value changes
 * - Jitter: Subtle shake animation when value changes  
 * - Flicker: Quick flash animation when value changes
 * - Mutation: Smooth morphing animation when value changes
 * 
 * All animations are performant and designed to enhance user experience without causing jank.
 */
@Composable
fun <T : Any> AnimatedRenderSetting(
    spec: WidgetSpec<T>,
    value: T,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Track value changes for effect-specific animations
    val previousValue = remember { mutableStateOf(value) }
    val hasValueChanged = remember { mutableStateOf(false) }
    
    // Update value change tracking
    LaunchedEffect(value) {
        if (value != previousValue.value) {
            hasValueChanged.value = true
            previousValue.value = value
            // Reset animation trigger after a short delay
            kotlinx.coroutines.delay(600)
            hasValueChanged.value = false
        }
    }
    
    // Effect-specific animation modifiers
    val animationModifier = when (spec.id) {
        is Glow -> Modifier.glowPulseAnimation(hasValueChanged.value, value as Float)
        is Jitter -> Modifier.jitterShakeAnimation(hasValueChanged.value, value as Float)
        is Flicker -> Modifier.flickerFlashAnimation(hasValueChanged.value, value as Float)
        is Mutation -> Modifier.mutationMorphAnimation(hasValueChanged.value, value as Float)
        else -> Modifier.interactiveScale(isPressed)
    }
    
    // Render the base setting with animations
    Box(
        modifier = modifier
            .then(animationModifier)
    ) {
        RenderSetting(
            spec = spec,
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Glow effect animation - gentle pulsing that intensifies with higher glow values.
 */
@Composable
private fun Modifier.glowPulseAnimation(
    isTriggered: Boolean,
    glowValue: Float
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "glowPulse")
    val baseScale = 1f
    val maxScale = 1f + (glowValue * 0.02f) // Scale increases with glow intensity
    
    val scale by infiniteTransition.animateFloat(
        initialValue = baseScale,
        targetValue = if (isTriggered) maxScale else baseScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (800 + glowValue * 200).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )
    
    return this.scale(scale)
}

/**
 * Jitter effect animation - subtle shake that increases with jitter amount.
 */
@Composable
private fun Modifier.jitterShakeAnimation(
    isTriggered: Boolean,
    jitterValue: Float
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "jitterShake")
    val shakeIntensity = jitterValue * 0.5f // Convert jitter value to shake intensity
    
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isTriggered) shakeIntensity else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 100,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jitterOffsetX"
    )
    
    return this.graphicsLayer {
        translationX = offsetX
    }
}

/**
 * Flicker effect animation - quick flash that increases with flicker amount.
 */
@Composable
private fun Modifier.flickerFlashAnimation(
    isTriggered: Boolean,
    flickerValue: Float
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "flickerFlash")
    val flashIntensity = flickerValue * 0.3f // Convert flicker value to flash intensity
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isTriggered) 1f - flashIntensity else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (150 + flickerValue * 100).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flickerAlpha"
    )
    
    return this.graphicsLayer {
        this.alpha = alpha
    }
}

/**
 * Mutation effect animation - smooth morphing that increases with mutation rate.
 */
@Composable
private fun Modifier.mutationMorphAnimation(
    isTriggered: Boolean,
    mutationValue: Float
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "mutationMorph")
    val morphIntensity = mutationValue * 0.1f // Convert mutation value to morph intensity
    
    val scaleX by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isTriggered) 1f + morphIntensity else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (600 + mutationValue * 400).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mutationScaleX"
    )
    
    val scaleY by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isTriggered) 1f - morphIntensity * 0.5f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (600 + mutationValue * 400).toInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mutationScaleY"
    )
    
    return this.graphicsLayer {
        this.scaleX = scaleX
        this.scaleY = scaleY
    }
}

/**
 * Interactive scale animation for general use.
 */
@Composable
private fun Modifier.interactiveScale(isPressed: Boolean): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(DesignTokens.Animation.fast),
        label = "interactiveScale"
    )
    
    return this.scale(scale)
}
