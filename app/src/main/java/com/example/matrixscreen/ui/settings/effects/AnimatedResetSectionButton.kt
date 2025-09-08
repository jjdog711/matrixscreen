package com.example.matrixscreen.ui.settings.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.ui.settings.components.ResetSectionButton

/**
 * Animated version of ResetSectionButton with tasteful reset animation.
 * 
 * Provides a smooth, satisfying animation when the reset button is pressed:
 * - Press animation: Subtle scale down
 * - Reset animation: Gentle bounce with color flash
 * - Success feedback: Brief glow effect
 * 
 * The animation is designed to feel responsive and provide clear feedback
 * that the reset action has been performed.
 */
@Composable
fun AnimatedResetSectionButton(
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animation states
    var isResetting by remember { mutableStateOf(false) }
    var resetTrigger by remember { mutableStateOf(0) }
    
    // Press animation
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(DesignTokens.Animation.fast),
        label = "pressScale"
    )
    
    // Reset bounce animation
    val resetScale by animateFloatAsState(
        targetValue = if (isResetting) 1f else 1f,
        animationSpec = keyframes {
            durationMillis = 400
            1f at 0 using LinearEasing
            1.15f at 100 using FastOutSlowInEasing
            0.9f at 200 using FastOutSlowInEasing
            1.05f at 300 using FastOutSlowInEasing
            1f at 400 using LinearEasing
        },
        label = "resetBounce"
    )
    
    // Reset glow animation
    val resetGlow by animateFloatAsState(
        targetValue = if (isResetting) 0.4f else 0f,
        animationSpec = tween(300, delayMillis = 100),
        label = "resetGlow"
    )
    
    // Handle reset action with animation
    val handleReset = {
        isResetting = true
        resetTrigger++
        onReset()
    }
    
    // Reset animation state after animation completes
    LaunchedEffect(resetTrigger) {
        if (isResetting) {
            kotlinx.coroutines.delay(500)
            isResetting = false
        }
    }
    
    // Apply animations
    Box(
        modifier = modifier
            .scale(pressScale)
            .scale(resetScale)
            .graphicsLayer {
                // Add subtle glow effect during reset
                alpha = 1f + resetGlow
            }
    ) {
        ResetSectionButton(
            onReset = handleReset,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Alternative animated reset button with more subtle animations.
 * 
 * This version provides a gentler animation experience for users who prefer
 * less prominent feedback animations.
 */
@Composable
fun SubtleAnimatedResetSectionButton(
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animation states
    var isResetting by remember { mutableStateOf(false) }
    var resetTrigger by remember { mutableStateOf(0) }
    
    // Subtle press animation
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(DesignTokens.Animation.fast),
        label = "subtlePressScale"
    )
    
    // Gentle reset animation
    val resetAlpha by animateFloatAsState(
        targetValue = if (isResetting) 0.7f else 1f,
        animationSpec = tween(200),
        label = "resetAlpha"
    )
    
    // Handle reset action with subtle animation
    val handleReset = {
        isResetting = true
        resetTrigger++
        onReset()
    }
    
    // Reset animation state after animation completes
    LaunchedEffect(resetTrigger) {
        if (isResetting) {
            kotlinx.coroutines.delay(300)
            isResetting = false
        }
    }
    
    // Apply subtle animations
    Box(
        modifier = modifier
            .scale(pressScale)
            .alpha(resetAlpha)
    ) {
        ResetSectionButton(
            onReset = handleReset,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Enhanced animated reset button with ripple effect.
 * 
 * This version includes a custom ripple animation that spreads outward
 * from the button when reset is triggered, providing rich visual feedback.
 */
@Composable
fun RippleAnimatedResetSectionButton(
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animation states
    var isResetting by remember { mutableStateOf(false) }
    var resetTrigger by remember { mutableStateOf(0) }
    
    // Press animation
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(DesignTokens.Animation.fast),
        label = "ripplePressScale"
    )
    
    // Ripple animation
    val rippleScale by animateFloatAsState(
        targetValue = if (isResetting) 1.2f else 1f,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "rippleScale"
    )
    
    val rippleAlpha by animateFloatAsState(
        targetValue = if (isResetting) 0f else 0.3f,
        animationSpec = tween(300),
        label = "rippleAlpha"
    )
    
    // Handle reset action with ripple animation
    val handleReset = {
        isResetting = true
        resetTrigger++
        onReset()
    }
    
    // Reset animation state after animation completes
    LaunchedEffect(resetTrigger) {
        if (isResetting) {
            kotlinx.coroutines.delay(400)
            isResetting = false
        }
    }
    
    // Apply ripple animations
    Box(
        modifier = modifier
            .scale(pressScale)
    ) {
        // Ripple effect background
        if (isResetting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(rippleScale)
                    .alpha(rippleAlpha)
                    .graphicsLayer {
                        // Add subtle glow
                        alpha = rippleAlpha * 0.5f
                    }
            )
        }
        
        // Main button
        ResetSectionButton(
            onReset = handleReset,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
