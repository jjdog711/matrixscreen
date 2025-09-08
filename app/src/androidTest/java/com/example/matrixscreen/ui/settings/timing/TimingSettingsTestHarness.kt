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
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.kotlin.whenever

/**
 * Test harness for TimingSettingsScreen UI tests.
 * 
 * Provides common setup and utility methods for testing timing settings functionality
 * including draft updates, reset operations, and confirm/cancel workflows.
 */
@RunWith(AndroidJUnit4::class)
abstract class TimingSettingsTestHarness {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Mock
    protected lateinit var mockRepository: SettingsRepository
    
    protected lateinit var viewModel: NewSettingsViewModel
    protected lateinit var testDispatcher: TestDispatcher
    
    protected val defaultSettings = MatrixSettings.DEFAULT
    protected val modifiedSettings = defaultSettings.copy(
        columnStartDelay = 0.1f,
        columnRestartDelay = 0.3f
    )
    
    /**
     * Setup common test environment.
     */
    protected fun setupTestEnvironment() {
        // Mock repository to return default settings
        whenever(mockRepository.observe()).thenReturn(flowOf(defaultSettings))
        
        viewModel = NewSettingsViewModel(mockRepository, testDispatcher)
    }
    
    /**
     * Setup test environment with custom settings.
     */
    protected fun setupTestEnvironmentWithSettings(settings: MatrixSettings) {
        // Mock repository to return custom settings
        whenever(mockRepository.observe()).thenReturn(flowOf(settings))
        
        viewModel = NewSettingsViewModel(mockRepository, testDispatcher)
    }
    
    /**
     * Compose the TimingSettingsScreen with the test view model.
     */
    protected fun composeTimingSettingsScreen() {
        composeTestRule.setContent {
            TimingSettingsScreen(
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
            .onChildAt(1) // Slider
    }
    
    /**
     * Find the reset button for timing settings.
     */
    protected fun findResetButton(): SemanticsNodeInteraction {
        return composeTestRule.onNodeWithText("Reset")
    }
    
    /**
     * Verify that a slider value is displayed correctly.
     */
    protected fun verifySliderValueDisplayed(label: String, expectedValue: String) {
        composeTestRule.onNodeWithText(label)
            .onParent()
            .assert(hasAnyDescendant(hasText(expectedValue)))
    }
    
    /**
     * Verify that timing settings section is displayed.
     */
    protected fun verifyTimingSettingsSectionDisplayed() {
        composeTestRule.onNodeWithText("Timing Controls")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Spawn Delay")
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText("Respawn Delay")
            .assertIsDisplayed()
    }
    
    /**
     * Verify that reset section is displayed.
     */
    protected fun verifyResetSectionDisplayed() {
        composeTestRule.onNodeWithText("Reset")
            .assertIsDisplayed()
    }
}
