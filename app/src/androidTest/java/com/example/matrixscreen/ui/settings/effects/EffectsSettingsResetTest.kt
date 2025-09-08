package com.example.matrixscreen.ui.settings.effects

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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * UI tests for reset functionality in EffectsSettingsScreen.
 * 
 * Tests that the reset button properly resets all effects settings
 * to their default values and clears unsaved changes.
 */
@RunWith(AndroidJUnit4::class)
class EffectsSettingsResetTest {
    
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
            EffectsSettingsScreen(
                settingsViewModel = viewModel,
                onBack = { /* Test back navigation */ }
            )
        }
    }
    
    @Test
    fun `reset_button_resets_all_effects_settings_to_defaults`() {
        // Given initial state with some changes
        composeTestRule.waitForIdle()
        
        // Make changes to all effects settings
        composeTestRule.onNodeWithText("Glow Intensity")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Jitter Amount")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Flicker Amount")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Mutation Rate")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Verify changes were made
        val draftBeforeReset = viewModel.getDraftSettings()
        assert(draftBeforeReset != defaultSettings) {
            "Draft should be different from defaults before reset"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before reset"
        }
        
        // When clicking reset button
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Then all settings should be reset to defaults
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset == defaultSettings) {
            "All effects settings should be reset to defaults"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after reset"
        }
    }
    
    @Test
    fun `reset_button_is_visible_and_interactive`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Then reset button should be visible and interactive
        composeTestRule.onNodeWithText("Reset to Defaults")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun `reset_works_when_no_changes_have_been_made`() {
        // Given initial state with no changes
        composeTestRule.waitForIdle()
        
        // Verify initial state
        val initialDraft = viewModel.getDraftSettings()
        assert(initialDraft == defaultSettings) {
            "Initial draft should equal defaults"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes initially"
        }
        
        // When clicking reset button
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Then state should remain unchanged
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset == defaultSettings) {
            "Draft should still equal defaults after reset"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should still not have unsaved changes after reset"
        }
    }
    
    @Test
    fun `reset_works_after_partial_changes`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Make changes to only some settings
        composeTestRule.onNodeWithText("Glow Intensity")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Jitter Amount")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Verify partial changes
        val draftBeforeReset = viewModel.getDraftSettings()
        assert(draftBeforeReset.glowIntensity != defaultSettings.glowIntensity) {
            "Glow intensity should be different from default"
        }
        assert(draftBeforeReset.jitterAmount != defaultSettings.jitterAmount) {
            "Jitter amount should be different from default"
        }
        assert(draftBeforeReset.flickerAmount == defaultSettings.flickerAmount) {
            "Flicker amount should still equal default"
        }
        assert(draftBeforeReset.mutationRate == defaultSettings.mutationRate) {
            "Mutation rate should still equal default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before reset"
        }
        
        // When clicking reset button
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Then all settings should be reset to defaults
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset == defaultSettings) {
            "All settings should be reset to defaults"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after reset"
        }
    }
    
    @Test
    fun `reset_works_multiple_times_in_sequence`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Make changes and reset multiple times
        repeat(3) { iteration ->
            // Make changes
            composeTestRule.onNodeWithText("Glow Intensity")
                .onParent()
                .onChild()
                .onChildAt(1)
                .performTouchInput { swipeRight() }
            
            composeTestRule.onNodeWithText("Flicker Amount")
                .onParent()
                .onChild()
                .onChildAt(1)
                .performTouchInput { swipeRight() }
            
            composeTestRule.waitForIdle()
            
            // Verify changes
            val draftWithChanges = viewModel.getDraftSettings()
            assert(draftWithChanges != defaultSettings) {
                "Draft should be different from defaults in iteration $iteration"
            }
            assert(viewModel.hasUnsavedChanges()) {
                "Should have unsaved changes in iteration $iteration"
            }
            
            // Reset
            composeTestRule.onNodeWithText("Reset to Defaults")
                .performClick()
            
            composeTestRule.waitForIdle()
            
            // Verify reset
            val draftAfterReset = viewModel.getDraftSettings()
            assert(draftAfterReset == defaultSettings) {
                "Draft should be reset to defaults in iteration $iteration"
            }
            assert(!viewModel.hasUnsavedChanges()) {
                "Should not have unsaved changes after reset in iteration $iteration"
            }
        }
    }
    
    @Test
    fun `reset_clears_unsaved_changes_flag`() {
        // Given initial state with changes
        composeTestRule.waitForIdle()
        
        // Make changes
        composeTestRule.onNodeWithText("Glow Intensity")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.onNodeWithText("Mutation Rate")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Verify unsaved changes flag is set
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before reset"
        }
        
        // When clicking reset
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Then unsaved changes flag should be cleared
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after reset"
        }
    }
    
    @Test
    fun `reset_does_not_affect_saved_settings`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // Make changes
        composeTestRule.onNodeWithText("Glow Intensity")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // When clicking reset
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Then saved settings should remain unchanged
        val currentSavedSettings = viewModel.getCurrentSettings()
        assert(currentSavedSettings == initialSavedSettings) {
            "Saved settings should not be affected by reset"
        }
        
        // But draft should be reset to defaults
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset == defaultSettings) {
            "Draft should be reset to defaults"
        }
    }
}
