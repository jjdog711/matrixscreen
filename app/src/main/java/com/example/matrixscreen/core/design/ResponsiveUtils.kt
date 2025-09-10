package com.example.matrixscreen.core.design

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Responsive utilities for adaptive UI across different screen sizes and orientations.
 * 
 * Follows Google's Material Design responsive guidelines for consistent behavior
 * across phones, tablets, and different orientations.
 */

/**
 * Screen size categories based on Material Design breakpoints
 */
enum class ScreenSizeCategory {
    Small,   // < 600dp width (phones)
    Medium,  // 600-840dp width (small tablets)
    Large    // > 840dp width (large tablets, foldables)
}

/**
 * Determines screen size category based on current configuration
 */
@Composable
fun getScreenSizeCategory(): ScreenSizeCategory {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp
    
    return when {
        widthDp < 600 -> ScreenSizeCategory.Small
        widthDp < 840 -> ScreenSizeCategory.Medium
        else -> ScreenSizeCategory.Large
    }
}

/**
 * Returns adaptive header height based on screen size and orientation
 */
@Composable
fun rememberAdaptiveHeaderHeight(): Dp {
    val configuration = LocalConfiguration.current
    val screenSizeCategory = getScreenSizeCategory()
    
    return when {
        // Landscape mode gets smaller header
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE -> {
            DesignTokens.Sizing.headerHeightLandscape
        }
        // Large screens (tablets) get larger header
        screenSizeCategory == ScreenSizeCategory.Large -> {
            DesignTokens.Sizing.headerHeightTablet
        }
        // Default for phones in portrait
        else -> {
            DesignTokens.Sizing.headerHeight
        }
    }
}

/**
 * Returns adaptive touch target size based on screen size
 */
@Composable
fun rememberAdaptiveTouchTarget(): Dp {
    val screenSizeCategory = getScreenSizeCategory()
    
    return when (screenSizeCategory) {
        ScreenSizeCategory.Small -> DesignTokens.Sizing.minTouchTarget
        ScreenSizeCategory.Medium -> DesignTokens.Sizing.minTouchTarget * 1.125f // 54dp
        ScreenSizeCategory.Large -> DesignTokens.Sizing.minTouchTarget * 1.25f // 60dp
    }
}

/**
 * Returns adaptive spacing based on screen size
 */
@Composable
fun rememberAdaptiveSpacing(): Dp {
    val screenSizeCategory = getScreenSizeCategory()
    
    return when (screenSizeCategory) {
        ScreenSizeCategory.Small -> DesignTokens.Spacing.md
        ScreenSizeCategory.Medium -> DesignTokens.Spacing.lg
        ScreenSizeCategory.Large -> DesignTokens.Spacing.xl
    }
}

/**
 * Returns whether the current screen is in landscape orientation
 */
@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
}

/**
 * Returns whether the current screen is a tablet (medium or large category)
 */
@Composable
fun isTablet(): Boolean {
    val screenSizeCategory = getScreenSizeCategory()
    return screenSizeCategory == ScreenSizeCategory.Medium || screenSizeCategory == ScreenSizeCategory.Large
}
