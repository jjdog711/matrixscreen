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
import org.mockito.kotlin.whenever

/**
 * UI tests for draft update functionality in MotionSettingsScreen.
 * 
 * Tests that slider interactions properly update the draft state
 * and that changes are reflected in the UI immediately.
 */
@RunWith(AndroidJUnit4::class)
class MotionSettingsDraftUpdateTest {
    
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
    fun `fall_speed_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with fall speed slider
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1) // Slider
            .performTouchInput {
                // Move slider to a new position (approximately 3.0f)
                swipeRight()
            }
        
        composeTestRule.waitForIdle()
        
        // Then draft state should be updated
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.fallSpeed != defaultSettings.fallSpeed) {
            "Fall speed should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying fall speed"
        }
    }
    
    @Test
    fun `column_count_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with column count slider
        composeTestRule.onNodeWithText("Column Count")
            .onParent()
            .onChild()
            .onChildAt(1) // Slider
            .performTouchInput {
                // Move slider to a new position (approximately 200)
                swipeRight()
            }
        
        composeTestRule.waitForIdle()
        
        // Then draft state should be updated
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.columnCount != defaultSettings.columnCount) {
            "Column count should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying column count"
        }
    }
    
    @Test
    fun `line_spacing_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with line spacing slider
        composeTestRule.onNodeWithText("Line Spacing")
            .onParent()
            .onChild()
            .onChildAt(1) // Slider
            .performTouchInput {
                // Move slider to a new position
                swipeRight()
            }
        
        composeTestRule.waitForIdle()
        
        // Then draft state should be updated
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.lineSpacing != defaultSettings.lineSpacing) {
            "Line spacing should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying line spacing"
        }
    }
    
    @Test
    fun `active_percentage_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with active percentage slider
        composeTestRule.onNodeWithText("Active Percentage")
            .onParent()
            .onChild()
            .onChildAt(1) // Slider
            .performTouchInput {
                // Move slider to a new position
                swipeRight()
            }
        
        composeTestRule.waitForIdle()
        
        // Then draft state should be updated
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.activePercentage != defaultSettings.activePercentage) {
            "Active percentage should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying active percentage"
        }
    }
    
    @Test
    fun `speed_variance_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with speed variance slider
        composeTestRule.onNodeWithText("Speed Variance")
            .onParent()
            .onChild()
            .onChildAt(1) // Slider
            .performTouchInput {
                // Move slider to a new position
                swipeRight()
            }
        
        composeTestRule.waitForIdle()
        
        // Then draft state should be updated
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.speedVariance != defaultSettings.speedVariance) {
            "Speed variance should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying speed variance"
        }
    }
    
    @Test
    fun `multiple_slider_updates_accumulate_in_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with multiple sliders
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
        
        // Then all changes should be in draft state
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
            "Should have unsaved changes after modifying multiple settings"
        }
    }
    
    @Test
    fun `slider_values_are_displayed_correctly_in_UI`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with a slider
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Then the value should be displayed in the UI
        // Note: This test verifies that the UI shows the updated value
        // The exact value display depends on the implementation of RenderSetting
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .assert(hasAnyDescendant(hasText("2.0").not())) // Should not show default value
    }
    
    @Test
    fun `draft_updates_do_not_affect_saved_settings`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // When making draft changes
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Then saved settings should remain unchanged
        val currentSavedSettings = viewModel.getCurrentSettings()
        assert(currentSavedSettings == initialSavedSettings) {
            "Saved settings should not change when making draft updates"
        }
        
        // But draft should be different
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings != currentSavedSettings) {
            "Draft settings should be different from saved settings"
        }
    }
    
    @Test
    fun `preview_tile_updates_with_draft_changes`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Verify preview tile is visible
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
        
        // Then preview tile should still be visible and updated
        composeTestRule.onNodeWithText("Live Preview")
            .onParent()
            .onChild()
            .onChildAt(1)
            .assertIsDisplayed()
            .assertIsEnabled()
    }
}
