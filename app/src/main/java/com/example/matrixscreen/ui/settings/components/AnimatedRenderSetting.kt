package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.*
import com.example.matrixscreen.ui.settings.model.*

/**
 * Enhanced RenderSetting component with subtle animations and micro-interactions.
 * 
 * This component provides the same functionality as RenderSetting but with
 * tasteful animations that enhance user experience without causing jank.
 */

/**
 * Renders a setting with animations based on its WidgetSpec.
 * 
 * @param spec The WidgetSpec defining the setting
 * @param value The current value
 * @param onValueChange Callback when the value changes
 * @param showPreview Whether to show a preview tile (default: true)
 * @param modifier Modifier for the component
 */
@Composable
fun <T> AnimatedRenderSetting(
    spec: WidgetSpec<T>,
    value: T,
    onValueChange: (T) -> Unit,
    showPreview: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    var hasChanged by remember { mutableStateOf(false) }
    var lastValue by remember { mutableStateOf(value) }
    
    // Track value changes for success animation
    LaunchedEffect(value) {
        if (value != lastValue) {
            hasChanged = true
            lastValue = value
            // Reset the change flag after animation
            kotlinx.coroutines.delay(300)
            hasChanged = false
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .interactiveScale(isPressed)
            .interactiveAlpha(isHovered)
            .focusGlow(isFocused)
            .successBounce(if (hasChanged) value else null)
    ) {
        when (spec) {
            is SliderSpec -> {
                AnimatedSliderSetting(
                    spec = spec,
                    value = value as Float,
                    onValueChange = onValueChange as (Float) -> Unit,
                    interactionSource = interactionSource,
                    showPreview = showPreview
                )
            }
            is IntSliderSpec -> {
                AnimatedIntSliderSetting(
                    spec = spec,
                    value = value as Int,
                    onValueChange = onValueChange as (Int) -> Unit,
                    interactionSource = interactionSource,
                    showPreview = showPreview
                )
            }
            is ToggleSpec -> {
        }
        is BooleanSpec -> {
                AnimatedBooleanSetting(
                    spec = spec,
                    value = value as Boolean,
                    onValueChange = onValueChange as (Boolean) -> Unit,
                    interactionSource = interactionSource
                )
            }
            is SelectSpec -> {
                AnimatedSelectSetting(
                    spec = spec as SelectSpec<Any>,
                    value = value as Any,
                    onValueChange = onValueChange as (Any) -> Unit,
                    interactionSource = interactionSource,
                    showPreview = showPreview
                )
            }
            is ColorSpec -> {
                AnimatedColorSetting(
                    spec = spec,
                    value = value as Long,
                    onValueChange = onValueChange as (Long) -> Unit,
                    interactionSource = interactionSource
                )
            }
        }
    }
}

/**
 * Animated slider setting with preview.
 */
@Composable
private fun AnimatedSliderSetting(
    spec: SliderSpec,
    value: Float,
    onValueChange: (Float) -> Unit,
    interactionSource: MutableInteractionSource,
    showPreview: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
    ) {
        // Main slider
        Column(
            modifier = Modifier.weight(1f)
        ) {
            RenderSetting(
                spec = spec,
                value = value,
                onValueChange = onValueChange
            )
        }
        
        // Preview tile
        if (showPreview) {
            when (spec.id) {
                is Speed -> {
                    SpeedPreviewTile(
                        speed = value,
                        modifier = Modifier.slideInFromBottom(true)
                    )
                }
                is LineSpace -> {
                    CompactMotionPreview(
                        label = "Spacing",
                        value = String.format("%.1f", value),
                        modifier = Modifier.slideInFromBottom(true),
                        previewContent = {
                            // Show spacing visualization
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy((value * 2).dp)
                            ) {
                                repeat(3) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primary,
                                                androidx.compose.foundation.shape.RoundedCornerShape(1.dp)
                                            )
                                    )
                                }
                            }
                        }
                    )
                }
                is ActivePct -> {
                    ActivePercentagePreviewTile(
                        activePercentage = value,
                        modifier = Modifier.slideInFromBottom(true)
                    )
                }
                is SpeedVar -> {
                    CompactMotionPreview(
                        label = "Variance",
                        value = String.format("%.3f", value),
                        modifier = Modifier.slideInFromBottom(true),
                        previewContent = {
                            // Show variance visualization
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(3) { index ->
                                    val height = (10 + (value * 50)).dp
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(height)
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                                androidx.compose.foundation.shape.RoundedCornerShape(1.dp)
                                            )
                                    )
                                }
                            }
                        }
                    )
                }
                // Effects preview tiles
                is Glow -> {
                    GlowPreviewTile(
                        glowIntensity = value,
                        modifier = Modifier.slideInFromBottom(true)
                    )
                }
                is Jitter -> {
                    JitterPreviewTile(
                        jitterAmount = value,
                        modifier = Modifier.slideInFromBottom(true)
                    )
                }
                is Flicker -> {
                    FlickerPreviewTile(
                        flickerAmount = value,
                        modifier = Modifier.slideInFromBottom(true)
                    )
                }
                is Mutation -> {
                    MutationPreviewTile(
                        mutationRate = value,
                        modifier = Modifier.slideInFromBottom(true)
                    )
                }
                else -> {
                    // Generic preview for other slider specs
                    CompactMotionPreview(
                        label = spec.label,
                        value = value.toString(),
                        unit = spec.unit,
                        modifier = Modifier.slideInFromBottom(true),
                        previewContent = {
                            // Generic slider visualization
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * Animated integer slider setting with preview.
 */
@Composable
private fun AnimatedIntSliderSetting(
    spec: IntSliderSpec,
    value: Int,
    onValueChange: (Int) -> Unit,
    interactionSource: MutableInteractionSource,
    showPreview: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
    ) {
        // Main slider
        Column(
            modifier = Modifier.weight(1f)
        ) {
            RenderSetting(
                spec = spec,
                value = value,
                onValueChange = onValueChange
            )
        }
        
        // Preview tile
        if (showPreview) {
            when (spec.id) {
                is Columns -> {
                    ColumnCountPreviewTile(
                        columnCount = value,
                        modifier = Modifier.slideInFromBottom(true)
                    )
                }
                else -> {
                    // Generic preview for other int slider specs
                    CompactMotionPreview(
                        label = spec.label,
                        value = value.toString(),
                        unit = spec.unit,
                        modifier = Modifier.slideInFromBottom(true),
                        previewContent = {
                            // Generic int slider visualization
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                        androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * Animated toggle setting.
 */
@Composable
private fun AnimatedToggleSetting(
    spec: ToggleSpec,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    interactionSource: MutableInteractionSource
) {
    RenderSetting(
        spec = spec,
        value = value,
        onValueChange = onValueChange
    )
}

/**
 * Animated select setting with preview.
 */
@Composable
private fun AnimatedSelectSetting(
    spec: SelectSpec<Any>,
    value: Any,
    onValueChange: (Any) -> Unit,
    interactionSource: MutableInteractionSource,
    showPreview: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.md)
    ) {
        // Main select
        Column(
            modifier = Modifier.weight(1f)
        ) {
            RenderSetting(
                spec = spec,
                value = value,
                onValueChange = onValueChange
            )
        }
        
        // Preview tile
        if (showPreview) {
            CompactMotionPreview(
                label = spec.label,
                value = value.toString(),
                modifier = Modifier.slideInFromBottom(true),
                previewContent = {
                    // Generic select visualization
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                androidx.compose.foundation.shape.RoundedCornerShape(2.dp)
                            )
                    )
                }
            )
        }
    }
}

/**
 * Animated color setting.
 */
@Composable
private fun AnimatedColorSetting(
    spec: ColorSpec,
    value: Long,
    onValueChange: (Long) -> Unit,
    interactionSource: MutableInteractionSource
) {
    RenderSetting(
        spec = spec,
        value = value,
        onValueChange = onValueChange
    )
}

/**
 * Animated boolean setting component for BooleanSpec.
 */
@Composable
private fun AnimatedBooleanSetting(
    spec: BooleanSpec,
    value: Boolean,
    onValueChange: (Boolean) -> Unit,
    interactionSource: MutableInteractionSource
) {
    // Use the same implementation as ToggleSpec since they're functionally identical
    AnimatedToggleSetting(
        spec = ToggleSpec(
            id = spec.id,
            label = spec.label,
            default = spec.default,
            help = spec.help
        ),
        value = value,
        onValueChange = onValueChange,
        interactionSource = interactionSource
    )
}