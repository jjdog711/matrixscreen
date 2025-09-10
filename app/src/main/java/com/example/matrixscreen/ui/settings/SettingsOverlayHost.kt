package com.example.matrixscreen.ui.settings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import kotlin.math.abs
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalConfiguration
import com.example.matrixscreen.core.design.rememberAdaptiveHeaderHeight
import com.example.matrixscreen.core.design.DesignTokens

/**
 * Helper function to determine if a horizontal swipe should trigger page navigation
 */
fun shouldTriggerHorizontalSwipe(dx: Float, dy: Float, density: androidx.compose.ui.unit.Density): Boolean {
    val minDx = with(density) { 20.dp.toPx() } // 16–24dp window
    val ratioOk = kotlin.math.abs(dx) > 2f * kotlin.math.abs(dy)
    return kotlin.math.abs(dx) > minDx && ratioOk
}

/**
 * Google Maps-style bottom sheet with continuous finger tracking
 * - Sheet offset from bottom: 0f = fully open (header at top), screenHeightPx = fully closed
 * - Tracks finger exactly with no snapping during drag
 * - Stays where user leaves it (even halfway up)
 * - Light spring settle only on release
 * - Phase 2: content scroll only when header is pinned to top
 * - Self-contained: handles initial swipe detection internally
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsOverlayHost(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onFirstGesture: (() -> Unit)? = null,
    onOffsetChange: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Always compose; it "lives" under the rain when closed
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() } // Full screen height
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() } // Full screen width
    val scope = rememberCoroutineScope()
    
    // Adaptive header height based on screen size and orientation
    val headerHeightPx = with(density) { rememberAdaptiveHeaderHeight().toPx() }
    
    val totalPages = 10 // 7 main settings + 3 custom symbol sets screens
    val lastActivePage = rememberSaveable { mutableStateOf(0) }
    val pagerState = rememberPagerState(
        initialPage = lastActivePage.value + (Int.MAX_VALUE / 2 / totalPages) * totalPages, // Start in middle for infinite scroll
        pageCount = { Int.MAX_VALUE } // Infinite pages
    )
    
    // Update lastActivePage whenever it changes (using modulo for infinite scroll)
    LaunchedEffect(pagerState.currentPage) {
        lastActivePage.value = pagerState.currentPage % totalPages
    }

    // Sheet offset from bottom; 0f = fully open (header at top), screenHeightPx + headerHeightPx = fully closed
    val offsetY = remember { Animatable(screenHeightPx + headerHeightPx) }

    // Self-contained state management - no external isOpen dependency
    // The sheet manages its own visibility based on user interaction

    // Gesture handling is now done directly in detectDragGestures for better control

    // Nested scroll: Phase 1 = move sheet until header hits top; Phase 2 = content scroll
    val nested = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // Phase 1: Upward drag moves sheet until header hits top (offsetY == 0)
                if (available.y < 0 && offsetY.value > 0f) {
                    val currentValue = offsetY.value
                    val new = (currentValue + available.y).coerceIn(0f, screenHeightPx + headerHeightPx)
                    val consumed = new - currentValue
                    
                    // Only update if there's a meaningful change for smooth performance
                    if (kotlin.math.abs(consumed) > 0.5f) {
                        scope.launch { offsetY.snapTo(new) }
                        return Offset(0f, consumed)
                    }
                }
                return Offset.Zero
            }
            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                // Phase 2: Downward scroll - only when header is pinned to top
                if (available.y > 0 && offsetY.value < screenHeightPx + headerHeightPx) {
                    val currentValue = offsetY.value
                    val new = (currentValue + available.y).coerceIn(0f, screenHeightPx + headerHeightPx)
                    val consumedY = new - currentValue
                    
                    // Only update if there's a meaningful change for smooth performance
                    if (kotlin.math.abs(consumedY) > 0.5f) {
                        scope.launch { offsetY.snapTo(new) }
                        return Offset(0f, consumedY)
                    }
                }
                return Offset.Zero
            }
        }
    }

    Box(modifier.fillMaxSize()) {
        // Full-screen invisible touch detection for gesture recognition
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            // inform the hint system
                            onFirstGesture?.invoke()
                        },
                        onDrag = { _, drag -> 
                            // Handle gesture directly - move the sheet with finger
                            val currentValue = offsetY.value
                            val new = (currentValue + drag.y).coerceIn(0f, screenHeightPx + headerHeightPx)
                            
                            // Only update if there's a meaningful change
                            if (kotlin.math.abs(new - currentValue) > 0.5f) {
                                scope.launch { 
                                    offsetY.snapTo(new)
                                    onOffsetChange(new)
                                }
                            }
                        },
                        onDragEnd = { 
                            // Handle drag end with spring animation
                            val currentOffset = offsetY.value
                            val openThreshold = screenHeightPx * 0.25f // 25% threshold for more responsive UX
                            
                            scope.launch {
                                val target = when {
                                    currentOffset < openThreshold -> screenHeightPx // Snap to closed
                                    else -> currentOffset // Stay where dragged (no snapping)
                                }
                                
                                offsetY.animateTo(target, spring(dampingRatio = 0.85f, stiffness = 400f))
                                onOffsetChange(target)
                            }
                        }
                    )
                }
        )
        
        // Sheet container - completely invisible when closed, full-screen when open
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset { 
                    // When offsetY = screenHeightPx (closed): sheet completely off-screen
                    // When offsetY = 0f (open): header at very top of screen
                    // Calculate offset to ensure complete invisibility when closed, top alignment when open
                    val clampedOffset = offsetY.value.coerceIn(0f, screenHeightPx + headerHeightPx)
                    IntOffset(0, kotlin.math.round(clampedOffset).toInt()) 
                }
                .size(
                    width = screenWidthPx.dp,
                    height = (screenHeightPx + headerHeightPx).dp // Extra height for complete hiding
                )
                .nestedScroll(nested)
        ) {
            // Phase 2: Content scroll only when header is pinned to top
            SettingsPager(
                pagerState = pagerState,
                isExpanded = offsetY.value <= headerHeightPx, // Only expanded when header is at top
            onBack = {
                    if (offsetY.value <= headerHeightPx) {
                        // collapse first; if already collapsed, close
                        scope.launch { offsetY.animateTo(screenHeightPx + headerHeightPx) }
                        // Sheet is self-contained, no external state management needed
                    }
                },
                onNavigateToPage = { index ->
                    scope.launch {
                        // Navigate to the closest page with the target index
                        val currentPage = pagerState.currentPage
                        val currentActualPage = currentPage % totalPages
                        val targetPage = currentPage - currentActualPage + index
                        pagerState.animateScrollToPage(targetPage)
                    }
                },
            settingsViewModel = settingsViewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SettingsPager(
    pagerState: androidx.compose.foundation.pager.PagerState,
    isExpanded: Boolean,
    onBack: () -> Unit,
    onNavigateToPage: (Int) -> Unit,
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    
    HorizontalPager(
        state = pagerState, 
        modifier = modifier.pointerInput(isExpanded) {
            if (isExpanded) {
                detectDragGestures(
                    onDragStart = { },
                    onDragEnd = { },
                    onDrag = { _, dragAmount ->
                        // Only handle horizontal swipes when sheet is fully expanded
                        if (shouldTriggerHorizontalSwipe(dragAmount.x, dragAmount.y, density)) {
                            val currentPage = pagerState.currentPage
                            val targetPage = when {
                                dragAmount.x > 0 -> currentPage - 1 // Swipe right -> previous (infinite)
                                dragAmount.x < 0 -> currentPage + 1 // Swipe left -> next (infinite)
                                else -> currentPage
                            }
                            if (targetPage != currentPage) {
                                scope.launch {
                                    pagerState.animateScrollToPage(targetPage)
                                }
                            }
                        }
                    }
                )
            }
        }
    ) { page ->
        val actualPage = page % 10 // Map infinite page to actual content (10 total pages)
        when (actualPage) {
            0 -> SettingsHomeScreen(
                settingsViewModel = settingsViewModel,
                onNavigateToTheme = { onNavigateToPage(1) },
                onNavigateToCharacters = { onNavigateToPage(2) },
                onNavigateToMotion = { onNavigateToPage(3) },
                onNavigateToEffects = { onNavigateToPage(4) },
                onNavigateToTiming = { onNavigateToPage(5) },
                onNavigateToBackground = { onNavigateToPage(6) },
                onNavigateToUIPreview = { /* handled elsewhere */ },
                onBack = onBack
            )
            1 -> com.example.matrixscreen.ui.settings.theme.ThemeSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
                onNavigateToCustomSets = { onNavigateToPage(7) },
            )
            2 -> com.example.matrixscreen.ui.settings.characters.CharactersSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
                onNavigateToCustomSets = { onNavigateToPage(7) },
            )
            3 -> com.example.matrixscreen.ui.settings.motion.MotionSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
            )
            4 -> com.example.matrixscreen.ui.settings.effects.EffectsSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
            )
            5 -> com.example.matrixscreen.ui.settings.timing.TimingSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
            )
            6 -> com.example.matrixscreen.ui.settings.background.BackgroundSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
            )
            // Custom Symbol Sets screens
            7 -> {
                val customSymbolSetViewModel: com.example.matrixscreen.ui.settings.characters.CustomSymbolSetViewModel = hiltViewModel()
                com.example.matrixscreen.ui.settings.characters.CustomSymbolSetsScreen(
                    viewModel = customSymbolSetViewModel,
                    settingsViewModel = settingsViewModel,
                    onBackPressed = onBack,
                    onNavigateToCreate = { onNavigateToPage(8) },
                    onNavigateToEdit = { setId -> 
                        // Store the set ID in the ViewModel for the edit screen to retrieve
                        customSymbolSetViewModel.setEditingSetId(setId)
                        onNavigateToPage(9) 
                    }
                )
            }
            8 -> {
                val customSymbolSetViewModel: com.example.matrixscreen.ui.settings.characters.CustomSymbolSetViewModel = hiltViewModel()
                com.example.matrixscreen.ui.settings.characters.CreateOrEditSymbolSetScreen(
                    viewModel = customSymbolSetViewModel,
                    onBackPressed = { onNavigateToPage(7) },
                    onDelete = null,
                    existingSet = null,
                    settingsViewModel = settingsViewModel
                )
            }
            9 -> {
                val customSymbolSetViewModel: com.example.matrixscreen.ui.settings.characters.CustomSymbolSetViewModel = hiltViewModel()
                val editingSet = customSymbolSetViewModel.getEditingSet()
                com.example.matrixscreen.ui.settings.characters.CreateOrEditSymbolSetScreen(
                    viewModel = customSymbolSetViewModel,
                    onBackPressed = { 
                        customSymbolSetViewModel.setEditingSetId(null) // Clear editing state
                        onNavigateToPage(7) 
                    },
                    onDelete = { 
                        editingSet?.let { customSymbolSetViewModel.deleteCustomSet(it.id) }
                        customSymbolSetViewModel.setEditingSetId(null) // Clear editing state
                        onNavigateToPage(7) 
                    },
                    existingSet = editingSet,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}


