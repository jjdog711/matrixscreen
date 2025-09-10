package com.example.matrixscreen.core.design

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * Scrollable content utilities for MatrixScreen UI components.
 * 
 * Provides consistent scrollable behavior for dynamic content areas
 * that may exceed available screen space.
 */

/**
 * Makes a content area scrollable when it exceeds the maximum height.
 * 
 * This modifier combines height constraints, scrolling, and consistent padding
 * to provide a standardized scrollable content experience across the app.
 * 
 * @param maxHeight Maximum height before scrolling kicks in (default: DesignTokens.Scrolling.maxContentHeight)
 * @param minHeight Minimum height for the content area (default: DesignTokens.Scrolling.minContentHeight)
 * @param padding Padding to apply to the scrollable content (default: DesignTokens.Scrolling.scrollablePadding)
 * 
 * @return Modifier with height constraints, scrolling, and padding applied
 */
@Composable
fun Modifier.scrollableContent(
    maxHeight: Dp = DesignTokens.Scrolling.maxContentHeight,
    minHeight: Dp = DesignTokens.Scrolling.minContentHeight,
    padding: Dp = DesignTokens.Scrolling.scrollablePadding
): Modifier {
    return this
        .heightIn(min = minHeight, max = maxHeight)
        .verticalScroll(rememberScrollState())
        .padding(padding)
}

/**
 * Makes a content area scrollable with custom scroll state.
 * 
 * Use this variant when you need to control the scroll state externally
 * (e.g., for programmatic scrolling or scroll position persistence).
 * 
 * @param scrollState The scroll state to use for this content area
 * @param maxHeight Maximum height before scrolling kicks in (default: DesignTokens.Scrolling.maxContentHeight)
 * @param minHeight Minimum height for the content area (default: DesignTokens.Scrolling.minContentHeight)
 * @param padding Padding to apply to the scrollable content (default: DesignTokens.Scrolling.scrollablePadding)
 * 
 * @return Modifier with height constraints, scrolling, and padding applied
 */
fun Modifier.scrollableContentWithState(
    scrollState: androidx.compose.foundation.ScrollState,
    maxHeight: Dp = DesignTokens.Scrolling.maxContentHeight,
    minHeight: Dp = DesignTokens.Scrolling.minContentHeight,
    padding: Dp = DesignTokens.Scrolling.scrollablePadding
): Modifier {
    return this
        .heightIn(min = minHeight, max = maxHeight)
        .verticalScroll(scrollState)
        .padding(padding)
}
