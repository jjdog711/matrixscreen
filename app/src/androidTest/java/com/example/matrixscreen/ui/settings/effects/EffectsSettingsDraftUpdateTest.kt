package com.example.matrixscreen.ui.settings.effects

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
 * UI tests for draft update functionality in EffectsSettingsScreen.
 * 
 * Tests that slider interactions properly update the draft state
 * and that changes are reflected in the UI immediately.
 */
@RunWith(AndroidJUnit4::class)
class EffectsSettingsDraftUpdateTest {
    
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
    fun `glow_intensity_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with glow intensity slider
        composeTestRule.onNodeWithText("Glow Intensity")
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
        assert(draftSettings.glowIntensity != defaultSettings.glowIntensity) {
            "Glow intensity should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying glow intensity"
        }
    }
    
    @Test
    fun `jitter_amount_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with jitter amount slider
        composeTestRule.onNodeWithText("Jitter Amount")
            .onParent()
            .onChild()
            .onChildAt(1) // Slider
            .performTouchInput {
                // Move slider to a new position (approximately 1.5f)
                swipeRight()
            }
        
        composeTestRule.waitForIdle()
        
        // Then draft state should be updated
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.jitterAmount != defaultSettings.jitterAmount) {
            "Jitter amount should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying jitter amount"
        }
    }
    
    @Test
    fun `flicker_amount_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with flicker amount slider
        composeTestRule.onNodeWithText("Flicker Amount")
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
        assert(draftSettings.flickerAmount != defaultSettings.flickerAmount) {
            "Flicker amount should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying flicker amount"
        }
    }
    
    @Test
    fun `mutation_rate_slider_updates_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with mutation rate slider
        composeTestRule.onNodeWithText("Mutation Rate")
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
        assert(draftSettings.mutationRate != defaultSettings.mutationRate) {
            "Mutation rate should be different from default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after modifying mutation rate"
        }
    }
    
    @Test
    fun `multiple_slider_updates_accumulate_in_draft_state`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When interacting with multiple sliders
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
        
        composeTestRule.waitForIdle()
        
        // Then all changes should be in draft state
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.glowIntensity != defaultSettings.glowIntensity) {
            "Glow intensity should be different from default"
        }
        assert(draftSettings.jitterAmount != defaultSettings.jitterAmount) {
            "Jitter amount should be different from default"
        }
        assert(draftSettings.flickerAmount != defaultSettings.flickerAmount) {
            "Flicker amount should be different from default"
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
        composeTestRule.onNodeWithText("Glow Intensity")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Then the value should be displayed in the UI
        // Note: This test verifies that the UI shows the updated value
        // The exact value display depends on the implementation of RenderSetting
        composeTestRule.onNodeWithText("Glow Intensity")
            .onParent()
            .assert(hasAnyDescendant(hasText("2.0").not())) // Should not show default value
    }
    
    @Test
    fun `draft_updates_do_not_affect_saved_settings`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // When making draft changes
        composeTestRule.onNodeWithText("Glow Intensity")
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
        composeTestRule.onNodeWithText("Glow Intensity")
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
