package com.example.matrixscreen.core.design

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

/**
 * Animation utilities for MatrixScreen UI components.
 * 
 * Provides consistent, tasteful micro-interactions and animations
 * that enhance user experience without causing jank or distraction.
 */

/**
 * Creates a subtle scale animation for interactive elements.
 * 
 * @param isPressed Whether the element is currently being pressed
 * @param scaleDown The scale factor when pressed (default: 0.95f)
 * @param duration The animation duration in milliseconds (default: 150ms)
 */
@Composable
fun Modifier.interactiveScale(
    isPressed: Boolean,
    scaleDown: Float = 0.95f,
    duration: Int = DesignTokens.Animation.fast
): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = tween(duration),
        label = "interactiveScale"
    )
    
    return this.scale(scale)
}

/**
 * Creates a subtle alpha animation for hover/focus states.
 * 
 * @param isHovered Whether the element is currently hovered
 * @param alphaTarget The target alpha when hovered (default: 0.8f)
 * @param duration The animation duration in milliseconds (default: 200ms)
 */
@Composable
fun Modifier.interactiveAlpha(
    isHovered: Boolean,
    alphaTarget: Float = 0.8f,
    duration: Int = DesignTokens.Animation.normal
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (isHovered) alphaTarget else 1f,
        animationSpec = tween(duration),
        label = "interactiveAlpha"
    )
    
    return this.alpha(alpha)
}

/**
 * Creates a gentle bounce animation for successful actions.
 * 
 * @param trigger The trigger value that causes the bounce
 * @param duration The animation duration in milliseconds (default: 300ms)
 */
@Composable
fun Modifier.successBounce(
    trigger: Any?,
    duration: Int = DesignTokens.Animation.normal
): Modifier {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = keyframes {
            durationMillis = duration
            1f at 0 with LinearEasing
            1.1f at duration / 3 with FastOutSlowInEasing
            0.95f at duration * 2 / 3 with FastOutSlowInEasing
            1f at duration with LinearEasing
        },
        label = "successBounce"
    )
    
    return this.scale(scale)
}

/**
 * Creates a subtle glow animation for focus states.
 * 
 * @param isFocused Whether the element is currently focused
 * @param glowIntensity The glow intensity when focused (default: 0.3f)
 * @param duration The animation duration in milliseconds (default: 200ms)
 */
@Composable
fun Modifier.focusGlow(
    isFocused: Boolean,
    glowIntensity: Float = DesignTokens.FocusGlow.intensity,
    duration: Int = DesignTokens.FocusGlow.duration
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (isFocused) glowIntensity else 0f,
        animationSpec = tween(duration),
        label = "focusGlow"
    )
    
    return this.graphicsLayer {
        this.alpha = alpha
    }
}

/**
 * Creates a smooth slide-in animation for appearing content.
 * 
 * @param isVisible Whether the content should be visible
 * @param offset The slide offset in dp (default: 16dp)
 * @param duration The animation duration in milliseconds (default: 300ms)
 */
@Composable
fun Modifier.slideInFromBottom(
    isVisible: Boolean,
    offset: androidx.compose.ui.unit.Dp = 16.dp,
    duration: Int = DesignTokens.Animation.normal
): Modifier {
    val slideOffset by animateDpAsState(
        targetValue = if (isVisible) 0.dp else offset,
        animationSpec = tween(duration, easing = FastOutSlowInEasing),
        label = "slideInFromBottom"
    )
    
    return this.graphicsLayer {
        translationY = slideOffset.toPx()
        alpha = if (isVisible) 1f else 0f
    }
}

/**
 * Creates a gentle pulse animation for attention-grabbing elements.
 * 
 * @param isPulsing Whether the element should pulse
 * @param minScale The minimum scale during pulse (default: 0.98f)
 * @param maxScale The maximum scale during pulse (default: 1.02f)
 * @param duration The pulse cycle duration in milliseconds (default: 1000ms)
 */
@Composable
fun Modifier.gentlePulse(
    isPulsing: Boolean,
    minScale: Float = 0.98f,
    maxScale: Float = 1.02f,
    duration: Int = 1000
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "gentlePulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration / 2, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    
    return if (isPulsing) {
        this.scale(scale)
    } else {
        this
    }
}

/**
 * Creates a smooth rotation animation for loading or state changes.
 * 
 * @param isRotating Whether the element should rotate
 * @param duration The rotation cycle duration in milliseconds (default: 1000ms)
 */
@Composable
fun Modifier.smoothRotation(
    isRotating: Boolean,
    duration: Int = 1000
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "smoothRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    return if (isRotating) {
        this.graphicsLayer {
            rotationZ = rotation
        }
    } else {
        this
    }
}

/**
 * Animation state for tracking user interactions.
 */
@Stable
class InteractionState {
    var isPressed by mutableStateOf(false)
        private set
    var isHovered by mutableStateOf(false)
        private set
    var isFocused by mutableStateOf(false)
        private set
    
    fun onPressStart() { isPressed = true }
    fun onPressEnd() { isPressed = false }
    fun onHoverStart() { isHovered = true }
    fun onHoverEnd() { isHovered = false }
    fun onFocusStart() { isFocused = true }
    fun onFocusEnd() { isFocused = false }
}

/**
 * Creates and remembers an interaction state for tracking user interactions.
 */
@Composable
fun rememberInteractionState(): InteractionState {
    return remember { InteractionState() }
}
