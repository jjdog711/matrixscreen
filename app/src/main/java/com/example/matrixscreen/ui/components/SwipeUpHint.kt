package com.example.matrixscreen.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.IntOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Educational affordance that teaches users the swipe-up gesture
 * - Shows animated arrow and text hint
 * - Auto-dismisses after 5 seconds or on first interaction
 * - Uses Matrix-themed styling with neon green accent
 */
@Composable
fun SwipeUpHint(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    autoDismissDelay: Long = 5000L // 5 seconds
) {
    var visible by remember { mutableStateOf(true) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800),
        label = "hint_alpha"
    )

    // Looping arrow animation - gentle up/down movement
    val offsetY = remember { Animatable(0f) }
    
    LaunchedEffect(visible) {
        if (visible) {
            // Start the looping arrow animation
            launch {
                while (visible) {
                    offsetY.animateTo(
                        targetValue = -12f,
                        animationSpec = tween(1000, easing = LinearEasing)
                    )
                    offsetY.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(1000, easing = LinearEasing)
                    )
                }
            }
            
            // Auto-dismiss after delay
            delay(autoDismissDelay)
            visible = false
            onDismiss()
        }
    }

    if (alpha > 0f) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .alpha(alpha)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp), // Position above bottom edge
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Animated arrow icon
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint = Color(0xFF00FF00), // Matrix neon green
                    modifier = Modifier
                        .offset(y = offsetY.value.dp)
                        .padding(4.dp),
                    // Make it slightly larger for better visibility
                )
                
                // Hint text
                Text(
                    text = "Swipe up to open settings",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Enhanced swipe-up hint with pulsing animation, sheet-aware positioning, and one-time display logic
 */
@Composable
fun MatrixSwipeUpHint(
    sheetOffsetY: Float,           // current sheet offset in px
    screenHeightPx: Float,         // total screen height in px
    headerHeightPx: Float,         // height of header in px
    alwaysShowHints: Boolean = false, // developer setting to always show hints
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    var hasSeenSwipeHint by rememberSaveable { mutableStateOf(false) }
    var dismissed by remember { mutableStateOf(false) }

    // Pulsing alpha animation (breathing)
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Fade-out once header is visible
    val headerVisible = sheetOffsetY <= screenHeightPx - headerHeightPx
    val shouldShow = if (alwaysShowHints) {
        !headerVisible // Always show unless header is visible
    } else {
        !dismissed && !hasSeenSwipeHint && !headerVisible // One-time display logic
    }
    
    val fadeAlpha by animateFloatAsState(
        targetValue = if (shouldShow) pulseAlpha else 0f,
        animationSpec = tween(500),
        label = "fadeAlpha"
    )

    // Once header is visible, mark dismissed permanently (unless alwaysShowHints is enabled)
    LaunchedEffect(headerVisible) {
        if (headerVisible && !dismissed && !alwaysShowHints) {
            dismissed = true
            hasSeenSwipeHint = true
            onDismiss()
        }
    }

    // Only render if should show and has alpha
    if (shouldShow && fadeAlpha > 0f) {
        // Move hint upward with the sheet
        val hintOffsetY = sheetOffsetY - headerHeightPx

        Column(
            modifier = modifier
                .fillMaxSize()
                .offset { IntOffset(0, hintOffsetY.roundToInt()) }
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = Color.White.copy(alpha = fadeAlpha),
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Swipe up to open settings",
                color = Color.White.copy(alpha = fadeAlpha),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
