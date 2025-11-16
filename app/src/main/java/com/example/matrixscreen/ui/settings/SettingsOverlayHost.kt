package com.example.matrixscreen.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.core.design.rememberAdaptiveHeaderHeight
import com.example.matrixscreen.ui.theme.MatrixUIColorScheme
import com.example.matrixscreen.ui.theme.getSafeUIColorScheme
import com.example.matrixscreen.ui.theme.rememberOptimizedSettings
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.collections.ArrayDeque
private enum class SheetValue { Hidden, Expanded }

private enum class SettingsTab(val label: String) {
    Overview("Overview"),
    Theme("Theme"),
    Characters("Characters"),
    Motion("Motion"),
    Effects("Effects"),
    Timing("Timing"),
    Background("Background")
}

private enum class CharactersPane { Overview, CustomSets, Create, Edit }

private const val FLING_VELOCITY_THRESHOLD = 2000f

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SettingsOverlayHost(
    settingsViewModel: com.example.matrixscreen.ui.NewSettingsViewModel,
    onFirstGesture: (() -> Unit)? = null,
    onOffsetChange: (Float) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val uiColors = getSafeUIColorScheme(settingsUiState.draft)
    val optimizedSettings = rememberOptimizedSettings(settingsUiState.draft)

    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val headerHeightPx = with(density) { rememberAdaptiveHeaderHeight().toPx() }
    val hiddenOffset = screenHeightPx + headerHeightPx

    val offsetY = remember { Animatable(hiddenOffset) }

    var sheetValue by rememberSaveable { mutableStateOf(SheetValue.Hidden) }

    fun offsetFor(value: SheetValue): Float = when (value) {
        SheetValue.Hidden -> hiddenOffset
        SheetValue.Expanded -> 0f
    }

    fun closestAnchor(offset: Float): SheetValue {
        if (hiddenOffset == 0f) return SheetValue.Hidden
        val ratio = (offset / hiddenOffset).coerceIn(0f, 1f)
        return if (ratio <= 0.5f) SheetValue.Expanded else SheetValue.Hidden
    }

    suspend fun snapTo(value: SheetValue) {
        offsetY.stop()
        offsetY.snapTo(offsetFor(value))
        sheetValue = value
    }

    suspend fun animateTo(value: SheetValue) {
        offsetY.stop()
        offsetY.animateTo(
            targetValue = offsetFor(value),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        val previousValue = sheetValue
        sheetValue = value
        if (previousValue != value) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(offsetY) {
        snapshotFlow { offsetY.value }
            .collectLatest { value ->
                onOffsetChange(value)
            }
    }

    var hasTriggeredFirstGesture by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(sheetValue) {
        if (sheetValue == SheetValue.Hidden) {
            hasTriggeredFirstGesture = false
        }
    }

    LaunchedEffect(sheetValue, settingsUiState.dirty) {
        if (sheetValue == SheetValue.Hidden && settingsUiState.dirty) {
            settingsViewModel.commit()
        }
    }

    val tabSwipeThresholdPx = remember(density) { with(density) { DesignTokens.Sizing.tabSwipeThreshold.toPx() } }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < 0f && offsetY.value > 0f) {
                    if (!hasTriggeredFirstGesture) {
                        onFirstGesture?.invoke()
                        hasTriggeredFirstGesture = true
                    }
                    val newOffset = (offsetY.value + available.y).coerceIn(0f, hiddenOffset)
                    val consumed = newOffset - offsetY.value
                    if (abs(consumed) > 0.1f) {
                        scope.launch {
                            offsetY.stop()
                            offsetY.snapTo(newOffset)
                        }
                        return Offset(0f, consumed)
                    }
                }
                return Offset.Zero
            }

            override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
                if (available.y > 0f && offsetY.value < hiddenOffset) {
                    val newOffset = (offsetY.value + available.y).coerceIn(0f, hiddenOffset)
                    val consumedY = newOffset - offsetY.value
                    if (abs(consumedY) > 0.1f) {
                        scope.launch {
                            offsetY.stop()
                            offsetY.snapTo(newOffset)
                        }
                        return Offset(0f, consumedY)
                    }
                }
                return Offset.Zero
            }
        }
    }

    var selectedTab by rememberSaveable { mutableStateOf(SettingsTab.Overview) }
    var charactersPane by rememberSaveable { mutableStateOf(CharactersPane.Overview) }
    var editingCustomSetId by rememberSaveable { mutableStateOf<String?>(null) }
    var preferencesRestored by remember { mutableStateOf(false) }

    LaunchedEffect(context) {
        val savedState = OverlayStateStore.state(context).firstOrNull()
        val savedTabName = savedState?.lastTab
        val savedPaneName = savedState?.lastCharactersPane
        savedTabName?.takeIf { it.isNotBlank() }?.let { runCatching { SettingsTab.valueOf(it) }.getOrNull() }?.let { selectedTab = it }
        savedPaneName?.takeIf { it.isNotBlank() }?.let { runCatching { CharactersPane.valueOf(it) }.getOrNull() }?.let { charactersPane = it }
        preferencesRestored = true
    }

    LaunchedEffect(context, selectedTab, preferencesRestored) {
        if (preferencesRestored) {
            OverlayStateStore.update(context) { state ->
                val builder = state.toBuilder()
                    .setLastTab(selectedTab.name)
                if (selectedTab != SettingsTab.Characters) {
                    builder.setLastCharactersPane(CharactersPane.Overview.name)
                }
                builder.build()
            }
        }
    }

    LaunchedEffect(context, selectedTab, charactersPane, preferencesRestored) {
        if (preferencesRestored && selectedTab == SettingsTab.Characters) {
            OverlayStateStore.update(context) { state ->
                state.toBuilder()
                    .setLastCharactersPane(charactersPane.name)
                    .build()
            }
        }
    }

    val openExpanded: () -> Unit = { scope.launch { animateTo(SheetValue.Expanded) } }
    val collapseToClosed: () -> Unit = { scope.launch { animateTo(SheetValue.Hidden) } }
    val hideSheet: () -> Unit = { scope.launch { animateTo(SheetValue.Hidden) } }

    fun targetFor(offset: Float, velocity: Float): SheetValue {
        return when {
            velocity > FLING_VELOCITY_THRESHOLD -> SheetValue.Hidden
            velocity < -FLING_VELOCITY_THRESHOLD -> SheetValue.Expanded
            else -> closestAnchor(offset)
        }
    }

    fun settleWithVelocity(velocity: Float = 0f) {
        val target = targetFor(offsetY.value, velocity)
        scope.launch { animateTo(target) }
    }

    val tabs = SettingsTab.values()

    fun navigateRelativeTab(offset: Int): Boolean {
        val currentIndex = tabs.indexOf(selectedTab)
        val targetIndex = (currentIndex + offset).coerceIn(0, tabs.lastIndex)
        if (targetIndex != currentIndex) {
            selectedTab = tabs[targetIndex]
            if (selectedTab != SettingsTab.Characters) {
                charactersPane = CharactersPane.Overview
                editingCustomSetId = null
            }
            if (sheetValue == SheetValue.Hidden) {
                openExpanded()
            }
            return true
        }
        return false
    }

    val handleBack: () -> Unit = {
        when {
            charactersPane != CharactersPane.Overview -> {
                charactersPane = CharactersPane.Overview
                editingCustomSetId = null
            }
            selectedTab != SettingsTab.Overview -> {
                selectedTab = SettingsTab.Overview
            }
            sheetValue == SheetValue.Expanded -> collapseToClosed()
            else -> hideSheet()
        }
    }

    val sheetOffset = offsetY.value
    val sheetHeightDp = with(density) { (screenHeightPx + headerHeightPx).toDp() }
    val expansionFraction = remember(sheetOffset) {
        if (hiddenOffset == 0f) 0f
        else 1f - ((sheetOffset - 0f) / (hiddenOffset - 0f))
    }.coerceIn(0f, 1f)

    val customSymbolSetViewModel: com.example.matrixscreen.ui.settings.characters.CustomSymbolSetViewModel = hiltViewModel()

    BackHandler(
        enabled = sheetValue != SheetValue.Hidden ||
            selectedTab != SettingsTab.Overview ||
            (selectedTab == SettingsTab.Characters && charactersPane != CharactersPane.Overview)
    ) {
        handleBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .pointerInput(selectedTab, sheetValue) {
                val velocitySamples = ArrayDeque<Float>()
                var lastDragTime: Long? = null
                var horizontalAccum = 0f
                var horizontalConsumed = false
                detectDragGestures(
                    onDragStart = {
                        horizontalAccum = 0f
                        horizontalConsumed = false
                        velocitySamples.clear()
                        lastDragTime = null
                        if (!hasTriggeredFirstGesture) {
                            onFirstGesture?.invoke()
                            hasTriggeredFirstGesture = true
                        }
                    },
                    onDrag = { change, dragAmount ->
                        val currentTime = change.uptimeMillis
                        val previousTime = lastDragTime
                        if (previousTime != null) {
                            val deltaTimeMillis = (currentTime - previousTime).coerceAtLeast(1L)
                            val instantaneousVelocity = (dragAmount.y / deltaTimeMillis) * 1000f
                            velocitySamples.addLast(instantaneousVelocity)
                            if (velocitySamples.size > 6) {
                                velocitySamples.removeFirst()
                            }
                        }
                        lastDragTime = currentTime
                        val canSwipeTabs = sheetValue != SheetValue.Hidden &&
                                (sheetValue == SheetValue.Expanded || offsetY.value <= hiddenOffset * 0.4f)
                        if (!horizontalConsumed && canSwipeTabs && abs(dragAmount.x) > abs(dragAmount.y)) {
                            horizontalAccum += dragAmount.x
                            if (abs(horizontalAccum) >= tabSwipeThresholdPx) {
                                val direction = if (horizontalAccum < 0f) 1 else -1
                                if (navigateRelativeTab(direction)) {
                                    horizontalConsumed = true
                                }
                                horizontalAccum = 0f
                            }
                        } else {
                            horizontalAccum = 0f
                            val newOffset = (offsetY.value + dragAmount.y).coerceIn(0f, hiddenOffset)
                            scope.launch {
                                offsetY.stop()
                                offsetY.snapTo(newOffset)
                            }
                        }
                    },
                    onDragEnd = {
                        val velocity = if (velocitySamples.isEmpty()) 0f else velocitySamples.average().toFloat()
                        velocitySamples.clear()
                        lastDragTime = null
                        if (!horizontalConsumed) {
                            settleWithVelocity(velocity)
                        } else {
                            if (sheetValue == SheetValue.Hidden) {
                                openExpanded()
                            } else {
                                settleWithVelocity(velocity)
                            }
                        }
                    },
                    onDragCancel = {
                        velocitySamples.clear()
                        lastDragTime = null
                        if (!horizontalConsumed) {
                            settleWithVelocity(velocity = 0f)
                        }
                    }
                )
            }
    ) {
        val scrimAlpha = 0.15f * expansionFraction

        if (scrimAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(uiColors.backgroundPrimary.copy(alpha = scrimAlpha))
                    .pointerInput(sheetValue) {
                        detectTapGestures { hideSheet() }
                    }
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset { IntOffset(x = 0, y = sheetOffset.roundToInt()) }
                .fillMaxWidth()
                .height(sheetHeightDp),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            tonalElevation = 0.dp,
            color = uiColors.overlayBackground,
            contentColor = uiColors.textPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SheetHandle(uiColors = uiColors)
                AnimatedVisibility(visible = expansionFraction > 0.05f) {
                    SettingsTabRow(
                        uiColors = uiColors,
                        selectedTab = selectedTab,
                        onTabSelected = { tab ->
                            selectedTab = tab
                            if (tab != SettingsTab.Characters) {
                                charactersPane = CharactersPane.Overview
                                editingCustomSetId = null
                            }
                            if (sheetValue == SheetValue.Hidden) {
                                openExpanded()
                            }
                        }
                    )
                }

                HorizontalDivider(color = uiColors.borderDim.copy(alpha = 0.4f))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                ) {
                    Crossfade(
                        targetState = selectedTab,
                        label = "settings_tab_content"
                    ) { tab ->
                        when (tab) {
                            SettingsTab.Overview -> SettingsHomeScreen(
                                settingsViewModel = settingsViewModel,
                                onNavigateToTheme = {
                                    selectedTab = SettingsTab.Theme
                                    if (sheetValue == SheetValue.Hidden) {
                                        openExpanded()
                                    }
                                },
                                onNavigateToCharacters = {
                                    selectedTab = SettingsTab.Characters
                                    charactersPane = CharactersPane.Overview
                                    if (sheetValue == SheetValue.Hidden) {
                                        openExpanded()
                                    }
                                },
                                onNavigateToMotion = {
                                    selectedTab = SettingsTab.Motion
                                    if (sheetValue == SheetValue.Hidden) {
                                        openExpanded()
                                    }
                                },
                                onNavigateToEffects = {
                                    selectedTab = SettingsTab.Effects
                                    if (sheetValue == SheetValue.Hidden) {
                                        openExpanded()
                                    }
                                },
                                onNavigateToTiming = {
                                    selectedTab = SettingsTab.Timing
                                    if (sheetValue == SheetValue.Hidden) {
                                        openExpanded()
                                    }
                                },
                                onNavigateToBackground = {
                                    selectedTab = SettingsTab.Background
                                    if (sheetValue == SheetValue.Hidden) {
                                        openExpanded()
                                    }
                                },
                                onBack = handleBack
                            )

                            SettingsTab.Theme -> com.example.matrixscreen.ui.settings.theme.ThemeSettingsScreen(
                                settingsViewModel = settingsViewModel,
                                onBack = handleBack
                            )

                            SettingsTab.Characters -> when (charactersPane) {
                                CharactersPane.Overview -> com.example.matrixscreen.ui.settings.characters.CharactersSettingsScreen(
                                    settingsViewModel = settingsViewModel,
                                    onBack = handleBack,
                                    onOpenCustomSets = {
                                        charactersPane = CharactersPane.CustomSets
                                        if (sheetValue == SheetValue.Hidden) {
                                            openExpanded()
                                        }
                                    }
                                )

                                CharactersPane.CustomSets -> com.example.matrixscreen.ui.settings.characters.CustomSymbolSetsScreen(
                                    viewModel = customSymbolSetViewModel,
                                    settingsViewModel = settingsViewModel,
                                    onBack = {
                                        charactersPane = CharactersPane.Overview
                                        editingCustomSetId = null
                                        customSymbolSetViewModel.setEditingSetId(null)
                                    },
                                    onCreateNew = {
                                        charactersPane = CharactersPane.Create
                                        editingCustomSetId = null
                                        customSymbolSetViewModel.setEditingSetId(null)
                                    },
                                    onEdit = { setId ->
                                        editingCustomSetId = setId
                                        customSymbolSetViewModel.setEditingSetId(setId)
                                        charactersPane = CharactersPane.Edit
                                    }
                                )

                                CharactersPane.Create -> com.example.matrixscreen.ui.settings.characters.CreateOrEditSymbolSetScreen(
                                    viewModel = customSymbolSetViewModel,
                                    onBackPressed = {
                                        charactersPane = CharactersPane.CustomSets
                                    },
                                    onDelete = null,
                                    existingSet = null,
                                    settingsViewModel = settingsViewModel
                                )

                                CharactersPane.Edit -> {
                                    val editingId = editingCustomSetId
                                    val editingSet = remember(editingId) {
                                        editingId?.let { customSymbolSetViewModel.getCustomSetById(it) }
                                    }

                                    if (editingSet == null) {
                                        LaunchedEffect(Unit) {
                                            charactersPane = CharactersPane.CustomSets
                                            editingCustomSetId = null
                                            customSymbolSetViewModel.setEditingSetId(null)
                                        }
                                    } else {
                                        com.example.matrixscreen.ui.settings.characters.CreateOrEditSymbolSetScreen(
                                            viewModel = customSymbolSetViewModel,
                                            onBackPressed = {
                                                charactersPane = CharactersPane.CustomSets
                                                editingCustomSetId = null
                                                customSymbolSetViewModel.setEditingSetId(null)
                                            },
                                            onDelete = {
                                                customSymbolSetViewModel.deleteCustomSet(editingSet.id)
                                                charactersPane = CharactersPane.CustomSets
                                                editingCustomSetId = null
                                                customSymbolSetViewModel.setEditingSetId(null)
                                            },
                                            existingSet = editingSet,
                                            settingsViewModel = settingsViewModel
                                        )
                                    }
                                }
                            }

                            SettingsTab.Motion -> com.example.matrixscreen.ui.settings.motion.MotionSettingsScreen(
                                settingsViewModel = settingsViewModel,
                                onBack = handleBack
                            )

                            SettingsTab.Effects -> com.example.matrixscreen.ui.settings.effects.EffectsSettingsScreen(
                                settingsViewModel = settingsViewModel,
                                onBack = handleBack
                            )

                            SettingsTab.Timing -> com.example.matrixscreen.ui.settings.timing.TimingSettingsScreen(
                                settingsViewModel = settingsViewModel,
                                onBack = handleBack
                            )

                            SettingsTab.Background -> com.example.matrixscreen.ui.settings.background.BackgroundSettingsScreen(
                                settingsViewModel = settingsViewModel,
                                onBack = handleBack
                            )
                        }
                    }
                }

            }
        }
    }
}


@Composable
private fun SheetHandle(uiColors: MatrixUIColorScheme, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 72.dp, height = 6.dp)
                .background(
                    color = uiColors.textPrimary.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(50)
                )
        )
    }
}

@Composable
private fun SettingsTabRow(
    uiColors: MatrixUIColorScheme,
    selectedTab: SettingsTab,
    onTabSelected: (SettingsTab) -> Unit
) {
    val tabs = SettingsTab.values()
    val selectedIndex = tabs.indexOf(selectedTab)
    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        containerColor = uiColors.overlayBackground.copy(alpha = 0.85f),
        contentColor = uiColors.textPrimary,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedIndex])
                    .padding(horizontal = 32.dp),
                color = uiColors.primary
            )
        }
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onTabSelected(tab) },
                selectedContentColor = uiColors.textPrimary,
                unselectedContentColor = uiColors.textSecondary,
                text = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (index == selectedIndex) uiColors.textPrimary else uiColors.textSecondary
                    )
                }
            )
        }
    }
}