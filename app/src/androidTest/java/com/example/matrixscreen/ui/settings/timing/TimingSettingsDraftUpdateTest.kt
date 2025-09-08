package com.example.matrixscreen.ui.settings.timing

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
 * UI tests for draft update functionality in TimingSettingsScreen.
 * 
 * Tests that slider interactions properly update the draft state
 * and that changes are reflected in the UI immediately.
 */
@RunWith(AndroidJUnit4::class)
class TimingSettingsDraftUpdateTest {
    
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
    fun `spawn_delay_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with spawn delay slider
        composeTestRule.onNodeWithText("Spawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1) // Slider
            .performTouchInput {
                // Move slider to a new position (approximately 0.1f)
                swipeRight()
            }
        
        composeTestRule.waitForIdle()
        
        // Then draft state should be updated
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.columnStartDelay != defaultSettings.columnStartDelay) {
            "Column start delay should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying spawn delay"
        }
    }
    
    @Test
    fun `respawn_delay_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with respawn delay slider
        composeTestRule.onNodeWithText("Respawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1) // Slider
            .performTouchInput {
                // Move slider to a new position (approximately 0.3f)
                swipeRight()
            }
        
        composeTestRule.waitForIdle()
        
        // Then draft state should be updated
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.columnRestartDelay != defaultSettings.columnRestartDelay) {
            "Column restart delay should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying respawn delay"
        }
    }
    
    @Test
    fun `multiple_slider_updates_accumulate_in_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with multiple sliders
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
        
        // Then all changes should be in draft state
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.columnStartDelay != defaultSettings.columnStartDelay) {
            "Column start delay should be different from default"
        }
        assert(draftSettings.columnRestartDelay != defaultSettings.columnRestartDelay) {
            "Column restart delay should be different from default"
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
        composeTestRule.onNodeWithText("Spawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Then the value should be displayed in the UI
        // Note: This test verifies that the UI shows the updated value
        // The exact value display depends on the implementation of LabeledSlider
        composeTestRule.onNodeWithText("Spawn Delay")
            .onParent()
            .assert(hasAnyDescendant(hasText("0.01").not())) // Should not show default value
    }
    
    @Test
    fun `draft_updates_do_not_affect_saved_settings`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // When making draft changes
        composeTestRule.onNodeWithText("Spawn Delay")
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
    fun `timing_controls_section_displays_correctly`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Then timing controls section should be visible
        composeTestRule.onNodeWithText("Timing Controls")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Spawn Delay")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Respawn Delay")
            .assertIsDisplayed()
    }
    
    @Test
    fun `slider_interactions_work_in_both_directions`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When moving slider right
        composeTestRule.onNodeWithText("Spawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        val draftAfterRight = viewModel.getDraftSettings()
        
        // When moving slider left
        composeTestRule.onNodeWithText("Spawn Delay")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeLeft() }
        
        composeTestRule.waitForIdle()
        
        val draftAfterLeft = viewModel.getDraftSettings()
        
        // Then both directions should work
        assert(draftAfterRight.columnStartDelay != defaultSettings.columnStartDelay) {
            "Right swipe should change the value"
        }
        assert(draftAfterLeft.columnStartDelay != draftAfterRight.columnStartDelay) {
            "Left swipe should change the value in opposite direction"
        }
    }
}
