package com.example.matrixscreen.ui.settings.motion

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
import org.mockito.kotlin.whenever

/**
 * Test harness for MotionSettingsScreen UI tests.
 * 
 * Provides common setup, test data, and helper functions for testing
 * motion settings functionality including draft updates, resets, and confirm/cancel.
 */
@RunWith(AndroidJUnit4::class)
abstract class MotionSettingsTestHarness {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Mock
    protected lateinit var mockRepository: SettingsRepository
    
    protected lateinit var viewModel: NewSettingsViewModel
    protected lateinit var testDispatcher: TestDispatcher
    
    protected val defaultSettings = MatrixSettings.DEFAULT
    protected val modifiedSettings = defaultSettings.copy(
        fallSpeed = 3.0f,
        columnCount = 200,
        lineSpacing = 1.1f,
        activePercentage = 0.6f,
        speedVariance = 0.02f
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
     * Compose the MotionSettingsScreen with the test view model.
     */
    protected fun composeMotionSettingsScreen() {
        composeTestRule.setContent {
            MotionSettingsScreen(
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
     * Verify that all motion settings are displayed.
     */
    protected fun verifyAllMotionSettingsVisible() {
        // Verify all motion setting labels are visible
        composeTestRule.onNodeWithText("Fall Speed").assertIsDisplayed()
        composeTestRule.onNodeWithText("Column Count").assertIsDisplayed()
        composeTestRule.onNodeWithText("Line Spacing").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active Percentage").assertIsDisplayed()
        composeTestRule.onNodeWithText("Speed Variance").assertIsDisplayed()
    }
    
    /**
     * Verify that the screen title is displayed.
     */
    protected fun verifyScreenTitle() {
        composeTestRule.onNodeWithText("MOTION").assertIsDisplayed()
    }
    
    /**
     * Verify that the description is displayed.
     */
    protected fun verifyDescription() {
        composeTestRule.onNodeWithText("Controls flow density and pacing.").assertIsDisplayed()
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
     * Test data for motion settings.
     */
    object TestData {
        val speedTestValues = listOf(1.0f, 2.5f, 5.0f, 8.0f, 10.0f)
        val columnTestValues = listOf(50, 100, 150, 200, 300, 500)
        val lineSpacingTestValues = listOf(0.5f, 0.8f, 1.0f, 1.2f, 1.5f, 2.0f)
        val activePercentageTestValues = listOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f, 1.0f)
        val speedVarianceTestValues = listOf(0.0f, 0.01f, 0.05f, 0.1f, 0.15f, 0.2f)
        
        val defaultMotionSettings = MatrixSettings.DEFAULT
        val modifiedMotionSettings = MatrixSettings.DEFAULT.copy(
            fallSpeed = 3.0f,
            columnCount = 200,
            lineSpacing = 1.1f,
            activePercentage = 0.6f,
            speedVariance = 0.02f
        )
    }
}
