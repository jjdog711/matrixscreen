package com.example.matrixscreen.ui.settings.motion

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.repo.SettingsRepository
import com.example.matrixscreen.ui.NewSettingsViewModel
import com.example.matrixscreen.ui.settings.model.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Comprehensive UI test suite for MotionSettingsScreen.
 * 
 * This test suite combines draft update, reset, and confirm/cancel functionality
 * to ensure the complete UDF (Unidirectional Data Flow) pattern works correctly
 * in the motion settings screen.
 */
@RunWith(AndroidJUnit4::class)
class MotionSettingsUITestSuite {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Mock
    private lateinit var mockRepository: SettingsRepository
    
    private lateinit var viewModel: NewSettingsViewModel
    private lateinit var testDispatcher: TestDispatcher
    
    private val defaultSettings = MatrixSettings.DEFAULT
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        testDispatcher = UnconfinedTestDispatcher()
        
        // Mock repository to return default settings
        whenever(mockRepository.observe()).thenReturn(flowOf(defaultSettings))
        
        viewModel = NewSettingsViewModel(mockRepository, testDispatcher)
        
        composeTestRule.setContent {
            MotionSettingsScreen(
                settingsViewModel = viewModel,
                onBack = { /* Test back navigation */ }
            )
        }
    }
    
    @Test
    fun `complete_motion_settings_workflow_draft_reset_commit_revert`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // Verify initial state
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes initially"
        }
        assert(viewModel.getDraftSettings() == initialSavedSettings) {
            "Draft should equal saved settings initially"
        }
        
        // Step 1: Make draft changes
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Column Count")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Verify draft changes
        val draftAfterChanges = viewModel.getDraftSettings()
        assert(draftAfterChanges != initialSavedSettings) {
            "Draft should be different from saved settings after changes"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after draft updates"
        }
        assert(viewModel.getCurrentSettings() == initialSavedSettings) {
            "Saved settings should not change after draft updates"
        }
        
        // Step 2: Reset to defaults
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Verify reset
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset == defaultSettings) {
            "Draft should be reset to defaults"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after reset"
        }
        
        // Step 3: Make new draft changes
        composeTestRule.onNodeWithText("Line Spacing")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Active Percentage")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Verify new draft changes
        val draftAfterNewChanges = viewModel.getDraftSettings()
        assert(draftAfterNewChanges != defaultSettings) {
            "Draft should be different from defaults after new changes"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after new draft updates"
        }
        
        // Step 4: Commit changes
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        // Verify commit
        runTest {
            verify(mockRepository).save(draftAfterNewChanges)
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after commit"
        }
        
        // Step 5: Make more draft changes
        composeTestRule.onNodeWithText("Speed Variance")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Verify new draft changes
        val draftAfterCommit = viewModel.getDraftSettings()
        assert(draftAfterCommit != draftAfterNewChanges) {
            "Draft should be different from committed settings"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after new draft updates"
        }
        
        // Step 6: Revert changes
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        // Verify revert
        val draftAfterRevert = viewModel.getDraftSettings()
        assert(draftAfterRevert == draftAfterNewChanges) {
            "Draft should be reverted to last committed settings"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert"
        }
    }
    
    @Test
    fun `motion_settings_UI_elements_are_properly_displayed`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Verify screen title
        composeTestRule.onNodeWithText("MOTION").assertIsDisplayed()
        
        // Verify description
        composeTestRule.onNodeWithText("Controls flow density and pacing.").assertIsDisplayed()
        
        // Verify live preview section
        composeTestRule.onNodeWithText("Live Preview").assertIsDisplayed()
        
        // Verify all motion settings are displayed
        composeTestRule.onNodeWithText("Fall Speed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Column Count").assertIsDisplayed()
        composeTestRule.onNodeWithText("Line Spacing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active Percentage").assertIsDisplayed()
        composeTestRule.onNodeWithText("Speed Variance").assertIsDisplayed()
        
        // Verify reset button
        composeTestRule.onNodeWithText("Reset to Defaults").assertIsDisplayed()
        
        // Verify preview tile is visible
        composeTestRule.onNodeWithText("Live Preview")
            .onParent()
            .onChild()
            .onChildAt(1) // Preview tile
            .assertIsDisplayed()
    }
    
    @Test
    fun `motion_settings_sliders_are_interactive`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Verify all sliders are interactive
        val sliderLabels = listOf(
            "Fall Speed",
            "Column Count", 
            "Line Spacing",
            "Active Percentage",
            "Speed Variance"
        )
        
        sliderLabels.forEach { label ->
            composeTestRule.onNodeWithText(label)
                .onParent()
                .onChild()
                .onChildAt(1) // Slider
                .assertIsEnabled()
                .assertIsDisplayed()
        }
    }
    
    @Test
    fun `reset_button_is_interactive`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Verify reset button is interactive
        composeTestRule.onNodeWithText("Reset to Defaults")
            .assertIsEnabled()
            .assertIsDisplayed()
    }
    
    @Test
    fun `preview_tile_updates_with_draft_changes`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Verify preview tile is visible initially
        composeTestRule.onNodeWithText("Live Preview")
            .onParent()
            .onChild()
            .onChildAt(1) // Preview tile
            .assertIsDisplayed()
        
        // When making draft changes
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Then preview tile should still be visible
        composeTestRule.onNodeWithText("Live Preview")
            .onParent()
            .onChild()
            .onChildAt(1) // Preview tile
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun `motion_settings_maintain_state_consistency`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Make changes to multiple settings
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Column Count")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Line Spacing")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Verify all changes are in draft
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.fallSpeed != defaultSettings.fallSpeed) {
            "Fall speed should be different from default"
        }
        assert(draftSettings.columnCount != defaultSettings.columnCount) {
            "Column count should be different from default"
        }
        assert(draftSettings.lineSpacing != defaultSettings.lineSpacing) {
            "Line spacing should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes"
        }
        
        // Reset and verify all changes are cleared
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset == defaultSettings) {
            "All settings should be reset to defaults"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after reset"
        }
    }
    
    @Test
    fun `motion_settings_handle_rapid_interactions_correctly`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Make rapid changes to different sliders
        repeat(5) {
            composeTestRule.onNodeWithText("Fall Speed")
                .onParent()
                .onChild()
                .onChildAt(1)
                .performTouchInput { swipeRight() }
            
            composeTestRule.onNodeWithText("Column Count")
                .onParent()
                .onChild()
                .onChildAt(1)
                .performTouchInput { swipeRight() }
            
            composeTestRule.waitForIdle()
        }
        
        // Verify final state is consistent
        val finalDraftSettings = viewModel.getDraftSettings()
        assert(finalDraftSettings != defaultSettings) {
            "Final draft should be different from defaults"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after rapid interactions"
        }
        
        // Reset should work correctly
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset == defaultSettings) {
            "Settings should be reset to defaults after rapid interactions"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after reset"
        }
    }
}
