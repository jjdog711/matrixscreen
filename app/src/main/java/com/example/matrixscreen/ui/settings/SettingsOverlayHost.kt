package com.example.matrixscreen.ui.settings

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

/**
 * Overlay host that sits above the Matrix screen and renders the settings UI
 * as a bottom panel with gestures:
 * - Swipe up from matrix to open (collapsed)
 * - Swipe up again or drag up to expand
 * - Swipe down to collapse; swipe down again to close
 * - Horizontal swipe to change categories (pager)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsOverlayHost(
    isOpen: Boolean,
    onOpenChange: (Boolean) -> Unit,
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    // initialPage: 0 = Home, 1..6 = categories
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val totalPages = 10 // 7 main settings + 3 custom symbol sets screens
    val pagerState = rememberPagerState(initialPage = totalPages * 1000 / 2, pageCount = { totalPages * 1000 })
    var isExpanded by remember { mutableStateOf(false) }

    // Map index -> screen
    val scope = rememberCoroutineScope()
    val navigateToPage: (Int) -> Unit = { index ->
        scope.launch {
            pagerState.animateScrollToPage(index)
        }
    }

    // Gesture: vertical drag to expand/collapse/close
    var totalDrag by remember { mutableFloatStateOf(0f) }
    val dragThreshold = with(LocalDensity.current) { 48.dp.toPx() }

    Box(
        modifier = modifier
            .fillMaxSize()
            // Transparent area to capture downward swipe to close when collapsed
            .pointerInput(isExpanded) {
                detectDragGestures(
                    onDragEnd = {
                        if (!isExpanded && totalDrag > dragThreshold) {
                            onOpenChange(false)
                        }
                        totalDrag = 0f
                    }
                ) { _, dragAmount ->
                    if (dragAmount.y < -dragThreshold && !isExpanded) {
                        isExpanded = true
                        totalDrag = 0f
                    } else if (dragAmount.y > dragThreshold) {
                        if (isExpanded) {
                            isExpanded = false
                        } else {
                            totalDrag += dragAmount.y
                        }
                    }
                }
            }
    ) {
        // Sheet content with pager
        SettingsPager(
            pagerState = pagerState,
            isExpanded = isExpanded,
            onBack = {
                if (isExpanded) {
                    isExpanded = false
                } else {
                    onOpenChange(false)
                }
            },
            onNavigateToPage = navigateToPage,
            settingsViewModel = settingsViewModel,
            modifier = Modifier.fillMaxSize()
        )
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
    HorizontalPager(state = pagerState, modifier = modifier) { page ->
        val mapped = page % 10
        when (mapped) {
            0 -> SettingsHomeScreen(
                settingsViewModel = settingsViewModel,
                onNavigateToTheme = { onNavigateToPage(1) },
                onNavigateToCharacters = { onNavigateToPage(2) },
                onNavigateToMotion = { onNavigateToPage(3) },
                onNavigateToEffects = { onNavigateToPage(4) },
                onNavigateToTiming = { onNavigateToPage(5) },
                onNavigateToBackground = { onNavigateToPage(6) },
                onNavigateToUIPreview = { /* handled elsewhere */ },
                onBack = onBack,
                isExpanded = isExpanded
            )
            1 -> com.example.matrixscreen.ui.settings.theme.ThemeSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
                onNavigateToCustomSets = { onNavigateToPage(7) },
                isExpanded = isExpanded
            )
            2 -> com.example.matrixscreen.ui.settings.characters.CharactersSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
                onNavigateToCustomSets = { onNavigateToPage(7) },
                isExpanded = isExpanded
            )
            3 -> com.example.matrixscreen.ui.settings.motion.MotionSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
                isExpanded = isExpanded
            )
            4 -> com.example.matrixscreen.ui.settings.effects.EffectsSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
                isExpanded = isExpanded
            )
            5 -> com.example.matrixscreen.ui.settings.timing.TimingSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
                isExpanded = isExpanded
            )
            6 -> com.example.matrixscreen.ui.settings.background.BackgroundSettingsScreen(
                settingsViewModel = settingsViewModel,
                onBack = onBack,
                isExpanded = isExpanded
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


