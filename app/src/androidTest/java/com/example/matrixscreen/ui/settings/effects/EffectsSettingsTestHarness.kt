package com.example.matrixscreen.ui.settings.effects

import androidx.compose.ui.test.*
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.repo.SettingsRepository
import com.example.matrixscreen.ui.NewSettingsViewModel
import com.example.matrixscreen.ui.settings.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Test harness for EffectsSettingsScreen UI tests.
 * 
 * Provides common setup, test data, and helper functions for testing
 * effects settings functionality including draft updates, resets, and confirm/cancel.
 */
@RunWith(AndroidJUnit4::class)
abstract class EffectsSettingsTestHarness {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Mock
    protected lateinit var mockRepository: SettingsRepository
    
    protected lateinit var viewModel: NewSettingsViewModel
    protected lateinit var testDispatcher: TestDispatcher
    
    protected val defaultSettings = MatrixSettings.DEFAULT
    protected val modifiedSettings = defaultSettings.copy(
        glowIntensity = 3.0f,
        jitterAmount = 1.5f,
        flickerAmount = 0.4f,
        mutationRate = 0.12f
    )
    
    /**
     * Setup common test environment.
     */
    protected fun setupTestEnvironment() {
        MockitoAnnotations.openMocks(this)
        testDispatcher = UnconfinedTestDispatcher()
        
        // Mock repository to return default settings
        whenever(mockRepository.observe()).thenReturn(flowOf(defaultSettings))
        
        viewModel = NewSettingsViewModel(mockRepository, testDispatcher)
    }
    
    /**
     * Setup test environment with custom settings.
     */
    protected fun setupTestEnvironmentWithSettings(settings: MatrixSettings) {
        MockitoAnnotations.openMocks(this)
        testDispatcher = UnconfinedTestDispatcher()
        
        // Mock repository to return custom settings
        whenever(mockRepository.observe()).thenReturn(flowOf(settings))
        
        viewModel = NewSettingsViewModel(mockRepository, testDispatcher)
    }
    
    /**
     * Compose the EffectsSettingsScreen with the test view model.
     */
    protected fun composeEffectsSettingsScreen() {
        composeTestRule.setContent {
            EffectsSettingsScreen(
                settingsViewModel = viewModel,
                onBack = { /* Test back navigation */ }
            )
        }
    }
    
    /**
     * Find and interact with a slider by its label.
     */
    protected fun findSliderByLabel(label: String): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithText(label)
            .onParent()
            .onChild()
            .onChildAt(1) // Slider is typically the second child
    }
    
    /**
     * Find and interact with the reset button.
     */
    protected fun findResetButton(): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithText("Reset to Defaults")
    }
    
    /**
     * Find and interact with the live preview tile.
     */
    protected fun findPreviewTile(): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithText("Live Preview")
            .onParent()
            .onChild()
            .onChildAt(1) // Preview tile is typically the second child
    }
    
    /**
     * Verify that a setting value is displayed correctly.
     */
    protected fun verifySettingValue(label: String, expectedValue: String) {
        composeTestRule.onNodeWithText(label)
            .onParent()
            .assert(hasAnyDescendant(hasText(expectedValue)))
    }
    
    /**
     * Verify that the preview tile is visible and interactive.
     */
    protected fun verifyPreviewTileVisible() {
        findPreviewTile()
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    /**
     * Verify that the reset button is visible and interactive.
     */
    protected fun verifyResetButtonVisible() {
        findResetButton()
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    /**
     * Verify that all effects settings are displayed.
     */
    protected fun verifyAllEffectsSettingsVisible() {
        // Verify all effects setting labels are visible
        composeTestRule.onNodeWithText("Glow Intensity").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jitter Amount").assertIsDisplayed()
        composeTestRule.onNodeWithText("Flicker Amount").assertIsDisplayed()
        composeTestRule.onNodeWithText("Mutation Rate").assertIsDisplayed()
    }
    
    /**
     * Verify that the screen title is displayed.
     */
    protected fun verifyScreenTitle() {
        composeTestRule.onNodeWithText("EFFECTS").assertIsDisplayed()
    }
    
    /**
     * Verify that the description is displayed.
     */
    protected fun verifyDescription() {
        composeTestRule.onNodeWithText("Visual effects and animations for the matrix rain.").assertIsDisplayed()
    }
    
    /**
     * Get the current draft settings from the view model.
     */
    protected fun getCurrentDraftSettings(): MatrixSettings {
        return viewModel.getDraftSettings()
    }
    
    /**
     * Get the current saved settings from the view model.
     */
    protected fun getCurrentSavedSettings(): MatrixSettings {
        return viewModel.getCurrentSettings()
    }
    
    /**
     * Check if there are unsaved changes.
     */
    protected fun hasUnsavedChanges(): Boolean {
        return viewModel.hasUnsavedChanges()
    }
    
    /**
     * Wait for compose to be idle.
     */
    protected fun waitForIdle() {
        composeTestRule.waitForIdle()
    }
    
    /**
     * Test data for effects settings.
     */
    object TestData {
        val glowTestValues = listOf(0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f)
        val jitterTestValues = listOf(0.0f, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f)
        val flickerTestValues = listOf(0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
        val mutationTestValues = listOf(0.0f, 0.05f, 0.1f, 0.15f, 0.2f, 0.5f)
        
        val defaultEffectsSettings = MatrixSettings.DEFAULT
        val modifiedEffectsSettings = MatrixSettings.DEFAULT.copy(
            glowIntensity = 3.0f,
            jitterAmount = 1.5f,
            flickerAmount = 0.4f,
            mutationRate = 0.12f
        )
    }
}
