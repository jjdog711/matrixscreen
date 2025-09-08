package com.example.matrixscreen.ui.settings.timing

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
 * Comprehensive UI test suite for TimingSettingsScreen.
 * 
 * This test suite combines draft update, reset, and confirm/cancel functionality
 * to ensure the complete UDF (Unidirectional Data Flow) pattern works correctly
 * in the timing settings screen.
 */
@RunWith(AndroidJUnit4::class)
class TimingSettingsUITestSuite {
    
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
            TimingSettingsScreen(
                settingsViewModel = viewModel,
                onBack = { /* Test back navigation */ }
            )
        }
    }
    
    @Test
    fun `complete_timing_settings_workflow_draft_reset_commit_revert`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // Step 1: Make draft changes
        composeTestRule.onNodeWithText("Spawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Respawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        val draftWithChanges = viewModel.getDraftSettings()
        assert(draftWithChanges != initialSavedSettings) {
            "Draft should be different from initial settings"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifications"
        }
        
        // Step 2: Reset timing section
        viewModel.resetSection(TIMING_SPECS)
        composeTestRule.waitForIdle()
        
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset.columnStartDelay == defaultSettings.columnStartDelay) {
            "Spawn delay should be reset to default"
        }
        assert(draftAfterReset.columnRestartDelay == defaultSettings.columnRestartDelay) {
            "Respawn delay should be reset to default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should still have unsaved changes after reset"
        }
        
        // Step 3: Make new changes
        composeTestRule.onNodeWithText("Spawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        val draftWithNewChanges = viewModel.getDraftSettings()
        assert(draftWithNewChanges.columnStartDelay != defaultSettings.columnStartDelay) {
            "New changes should be applied"
        }
        
        // Step 4: Commit changes
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        runTest {
            verify(mockRepository).save(draftWithNewChanges)
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after commit"
        }
        
        // Step 5: Make more changes and revert
        composeTestRule.onNodeWithText("Respawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        val draftWithMoreChanges = viewModel.getDraftSettings()
        assert(draftWithMoreChanges != draftWithNewChanges) {
            "More changes should be applied"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before revert"
        }
        
        // Step 6: Revert to committed state
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        val draftAfterRevert = viewModel.getDraftSettings()
        assert(draftAfterRevert == draftWithNewChanges) {
            "Draft should be reverted to last committed state"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert"
        }
    }
    
    @Test
    fun `timing_settings_ui_elements_are_displayed_correctly`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Then all UI elements should be visible
        composeTestRule.onNodeWithText("TIMING")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Control the timing and rhythm of the rain effect.")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Timing Controls")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Spawn Delay")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Respawn Delay")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Reset")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Flow Direction")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Coming soon: Center-Out and other flow modes")
            .assertIsDisplayed()
    }
    
    @Test
    fun `slider_interactions_update_ui_immediately`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with spawn delay slider
        composeTestRule.onNodeWithText("Spawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Then UI should reflect the change immediately
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.columnStartDelay != defaultSettings.columnStartDelay) {
            "Slider interaction should update draft state"
        }
        
        // When interacting with respawn delay slider
        composeTestRule.onNodeWithText("Respawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Then both sliders should have updated values
        val finalDraftSettings = viewModel.getDraftSettings()
        assert(finalDraftSettings.columnStartDelay != defaultSettings.columnStartDelay) {
            "Spawn delay should remain changed"
        }
        assert(finalDraftSettings.columnRestartDelay != defaultSettings.columnRestartDelay) {
            "Respawn delay should be changed"
        }
    }
    
    @Test
    fun `reset_button_functionality_works_correctly`() {
        // Given initial state with changes
        composeTestRule.waitForIdle()
        
        // Make changes
        composeTestRule.onNodeWithText("Spawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Respawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        val draftBeforeReset = viewModel.getDraftSettings()
        assert(draftBeforeReset.columnStartDelay != defaultSettings.columnStartDelay) {
            "Should have changes before reset"
        }
        
        // When clicking reset button (this would be triggered by the ResetSectionButton)
        viewModel.resetSection(TIMING_SPECS)
        composeTestRule.waitForIdle()
        
        // Then timing settings should be reset
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset.columnStartDelay == defaultSettings.columnStartDelay) {
            "Spawn delay should be reset"
        }
        assert(draftAfterReset.columnRestartDelay == defaultSettings.columnRestartDelay) {
            "Respawn delay should be reset"
        }
    }
    
    @Test
    fun `timing_settings_workflow_with_multiple_commits_and_reverts`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // First cycle: make changes, commit, make more changes, revert
        composeTestRule.onNodeWithText("Spawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        val firstDraft = viewModel.getDraftSettings()
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        runTest {
            verify(mockRepository).save(firstDraft)
        }
        
        // Make more changes
        composeTestRule.onNodeWithText("Respawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        val secondDraft = viewModel.getDraftSettings()
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        val draftAfterRevert = viewModel.getDraftSettings()
        assert(draftAfterRevert == firstDraft) {
            "Should revert to first committed state"
        }
        
        // Second cycle: make different changes, commit
        composeTestRule.onNodeWithText("Respawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeLeft() }
        
        composeTestRule.waitForIdle()
        
        val thirdDraft = viewModel.getDraftSettings()
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        runTest {
            verify(mockRepository).save(thirdDraft)
        }
        
        // Verify final state
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after final commit"
        }
    }
}
