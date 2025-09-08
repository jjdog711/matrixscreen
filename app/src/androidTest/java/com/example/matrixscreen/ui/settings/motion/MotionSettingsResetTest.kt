package com.example.matrixscreen.ui.settings.motion

import androidx.compose.ui.test.*
import androidx.compose.ui.test.hasAnyDescendant
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
 * UI tests for reset functionality in MotionSettingsScreen.
 * 
 * Tests that the reset button properly resets motion settings to their defaults
 * and that the UI reflects the reset state correctly.
 */
@RunWith(AndroidJUnit4::class)
class MotionSettingsResetTest {
    
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
    fun `reset_button_is_visible_and_clickable`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Then reset button should be visible and clickable
        composeTestRule.onNodeWithText("Reset to Defaults")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun `reset_button_resets_all_motion_settings_to_defaults`() {
        // Given modified draft state
        composeTestRule.waitForIdle()
        
        // Make some changes to draft
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
        
        // Verify changes were made
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before reset"
        }
        
        // When clicking reset button
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Then all motion settings should be reset to defaults
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.fallSpeed == defaultSettings.fallSpeed) {
            "Fall speed should be reset to default"
        }
        assert(draftSettings.columnCount == defaultSettings.columnCount) {
            "Column count should be reset to default"
        }
        assert(draftSettings.lineSpacing == defaultSettings.lineSpacing) {
            "Line spacing should be reset to default"
        }
        assert(draftSettings.activePercentage == defaultSettings.activePercentage) {
            "Active percentage should be reset to default"
        }
        assert(draftSettings.speedVariance == defaultSettings.speedVariance) {
            "Speed variance should be reset to default"
        }
        
        // And dirty flag should be cleared
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after reset"
        }
    }
    
    @Test
    fun `reset_button_works_when_no_changes_have_been_made`() {
        // Given initial state with no changes
        composeTestRule.waitForIdle()
        
        // Verify no unsaved changes initially
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes initially"
        }
        
        // When clicking reset button
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Then settings should remain at defaults
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings == defaultSettings) {
            "Settings should remain at defaults after reset with no changes"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after reset with no changes"
        }
    }
    
    @Test
    fun `reset_button_resets_individual_motion_settings_correctly`() {
        // Given modified individual settings
        composeTestRule.waitForIdle()
        
        // Test resetting fall speed
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        assert(viewModel.getDraftSettings().fallSpeed == defaultSettings.fallSpeed) {
            "Fall speed should be reset to default"
        }
        
        // Test resetting column count
        composeTestRule.onNodeWithText("Column Count")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        assert(viewModel.getDraftSettings().columnCount == defaultSettings.columnCount) {
            "Column count should be reset to default"
        }
        
        // Test resetting line spacing
        composeTestRule.onNodeWithText("Line Spacing")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        assert(viewModel.getDraftSettings().lineSpacing == defaultSettings.lineSpacing) {
            "Line spacing should be reset to default"
        }
        
        // Test resetting active percentage
        composeTestRule.onNodeWithText("Active Percentage")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        assert(viewModel.getDraftSettings().activePercentage == defaultSettings.activePercentage) {
            "Active percentage should be reset to default"
        }
        
        // Test resetting speed variance
        composeTestRule.onNodeWithText("Speed Variance")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        assert(viewModel.getDraftSettings().speedVariance == defaultSettings.speedVariance) {
            "Speed variance should be reset to default"
        }
    }
    
    @Test
    fun `reset_button_does_not_affect_saved_settings`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // Make draft changes
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // When clicking reset button
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Then saved settings should remain unchanged
        val currentSavedSettings = viewModel.getCurrentSettings()
        assert(currentSavedSettings == initialSavedSettings) {
            "Saved settings should not be affected by reset button"
        }
    }
    
    @Test
    fun `reset_button_updates_UI_to_show_default_values`() {
        // Given modified settings
        composeTestRule.waitForIdle()
        
        // Make changes
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // When clicking reset button
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Then UI should show default values
        // Note: This test verifies that the UI reflects the reset state
        // The exact value display depends on the implementation of RenderSetting
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .assert(hasAnyDescendant(hasText("2.0"))) // Should show default value
    }
    
    @Test
    fun `reset_button_works_multiple_times_in_sequence`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Make changes and reset multiple times
        repeat(3) {
            // Make changes
            composeTestRule.onNodeWithText("Fall Speed")
                .onParent()
                .onChild()
                .onChildAt(1)
                .performTouchInput { swipeRight() }
            
            composeTestRule.waitForIdle()
            
            // Reset
            composeTestRule.onNodeWithText("Reset to Defaults")
                .performClick()
            
            composeTestRule.waitForIdle()
            
            // Verify reset worked
            assert(viewModel.getDraftSettings().fallSpeed == defaultSettings.fallSpeed) {
                "Fall speed should be reset to default on iteration $it"
            }
            assert(!viewModel.hasUnsavedChanges()) {
                "Should not have unsaved changes after reset on iteration $it"
            }
        }
    }
    
    @Test
    fun `reset_button_clears_dirty_flag_after_reset`() {
        // Given modified draft state
        composeTestRule.waitForIdle()
        
        // Make changes
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
        
        // Verify dirty flag is set
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before reset"
        }
        
        // When clicking reset button
        composeTestRule.onNodeWithText("Reset to Defaults")
            .performClick()
        
        composeTestRule.waitForIdle()
        
        // Then dirty flag should be cleared
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after reset"
        }
    }
}
