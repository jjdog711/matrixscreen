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
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

/**
 * UI tests for reset functionality in TimingSettingsScreen.
 * 
 * Tests that the reset operations work correctly for timing settings
 * and restore default values for the timing section.
 */
@RunWith(AndroidJUnit4::class)
class TimingSettingsResetTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Mock
    private lateinit var mockRepository: SettingsRepository
    
    private lateinit var viewModel: NewSettingsViewModel
    private lateinit var testDispatcher: TestDispatcher
    
    private val defaultSettings = MatrixSettings.DEFAULT
    private val modifiedSettings = defaultSettings.copy(
        columnStartDelay = 0.1f,
        columnRestartDelay = 0.3f
    )
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        testDispatcher = UnconfinedTestDispatcher()
        
        // Mock repository to return modified settings
        whenever(mockRepository.observe()).thenReturn(flowOf(modifiedSettings))
        
        viewModel = NewSettingsViewModel(mockRepository, testDispatcher)
        
        composeTestRule.setContent {
            TimingSettingsScreen(
                settingsViewModel = viewModel,
                onBack = { /* Test back navigation */ }
            )
        }
    }
    
    @Test
    fun `reset_section_restores_timing_defaults`() {
        // Given initial state with modified settings
        composeTestRule.waitForIdle()
        
        // Verify initial state has modified values
        val initialDraft = viewModel.getDraftSettings()
        assert(initialDraft.columnStartDelay == modifiedSettings.columnStartDelay) {
            "Initial draft should have modified spawn delay"
        }
        assert(initialDraft.columnRestartDelay == modifiedSettings.columnRestartDelay) {
            "Initial draft should have modified respawn delay"
        }
        
        // When resetting timing section
        viewModel.resetSection(TIMING_SPECS)
        composeTestRule.waitForIdle()
        
        // Then timing settings should be restored to defaults
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset.columnStartDelay == defaultSettings.columnStartDelay) {
            "Spawn delay should be reset to default"
        }
        assert(draftAfterReset.columnRestartDelay == defaultSettings.columnRestartDelay) {
            "Respawn delay should be reset to default"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after reset"
        }
    }
    
    @Test
    fun `reset_section_only_affects_timing_settings`() {
        // Given initial state with modified settings
        composeTestRule.waitForIdle()
        
        // Make additional changes to non-timing settings
        viewModel.updateSetting(Glow, 3.0f)
        viewModel.updateSetting(Jitter, 1.5f)
        
        composeTestRule.waitForIdle()
        
        val draftBeforeReset = viewModel.getDraftSettings()
        
        // When resetting only timing section
        viewModel.resetSection(TIMING_SPECS)
        composeTestRule.waitForIdle()
        
        // Then only timing settings should be reset
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset.columnStartDelay == defaultSettings.columnStartDelay) {
            "Spawn delay should be reset to default"
        }
        assert(draftAfterReset.columnRestartDelay == defaultSettings.columnRestartDelay) {
            "Respawn delay should be reset to default"
        }
        assert(draftAfterReset.glowIntensity == draftBeforeReset.glowIntensity) {
            "Glow intensity should not be affected by timing reset"
        }
        assert(draftAfterReset.jitterAmount == draftBeforeReset.jitterAmount) {
            "Jitter amount should not be affected by timing reset"
        }
    }
    
    @Test
    fun `reset_section_works_with_draft_changes`() {
        // Given initial state with modified settings
        composeTestRule.waitForIdle()
        
        // Make additional draft changes to timing settings
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
        
        // When resetting timing section
        viewModel.resetSection(TIMING_SPECS)
        composeTestRule.waitForIdle()
        
        // Then timing settings should be reset to defaults (not the modified saved values)
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset.columnStartDelay == defaultSettings.columnStartDelay) {
            "Spawn delay should be reset to default, not modified saved value"
        }
        assert(draftAfterReset.columnRestartDelay == defaultSettings.columnRestartDelay) {
            "Respawn delay should be reset to default, not modified saved value"
        }
        assert(draftAfterReset != draftWithChanges) {
            "Draft should be different after reset"
        }
    }
    
    @Test
    fun `reset_section_button_is_displayed`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Then reset button should be visible
        composeTestRule.onNodeWithText("Reset")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
    
    @Test
    fun `reset_section_works_multiple_times`() {
        // Given initial state with modified settings
        composeTestRule.waitForIdle()
        
        // First reset
        viewModel.resetSection(TIMING_SPECS)
        composeTestRule.waitForIdle()
        
        val draftAfterFirstReset = viewModel.getDraftSettings()
        assert(draftAfterFirstReset.columnStartDelay == defaultSettings.columnStartDelay) {
            "First reset should work"
        }
        
        // Make changes again
        viewModel.updateSetting(ColumnStartDelay, 0.2f)
        viewModel.updateSetting(ColumnRestartDelay, 0.4f)
        
        composeTestRule.waitForIdle()
        
        val draftWithNewChanges = viewModel.getDraftSettings()
        assert(draftWithNewChanges.columnStartDelay == 0.2f) {
            "New changes should be applied"
        }
        
        // Second reset
        viewModel.resetSection(TIMING_SPECS)
        composeTestRule.waitForIdle()
        
        val draftAfterSecondReset = viewModel.getDraftSettings()
        assert(draftAfterSecondReset.columnStartDelay == defaultSettings.columnStartDelay) {
            "Second reset should work"
        }
        assert(draftAfterSecondReset.columnRestartDelay == defaultSettings.columnRestartDelay) {
            "Second reset should work for respawn delay too"
        }
    }
    
    @Test
    fun `reset_section_does_not_affect_other_sections`() {
        // Given initial state with modified settings
        composeTestRule.waitForIdle()
        
        // Make changes to other sections
        viewModel.updateSetting(Glow, 3.0f)
        viewModel.updateSetting(Speed, 3.0f)
        viewModel.updateSetting(Columns, 200)
        
        composeTestRule.waitForIdle()
        
        val draftBeforeReset = viewModel.getDraftSettings()
        
        // When resetting timing section
        viewModel.resetSection(TIMING_SPECS)
        composeTestRule.waitForIdle()
        
        // Then other sections should remain unchanged
        val draftAfterReset = viewModel.getDraftSettings()
        assert(draftAfterReset.glowIntensity == draftBeforeReset.glowIntensity) {
            "Glow intensity should not be affected"
        }
        assert(draftAfterReset.fallSpeed == draftBeforeReset.fallSpeed) {
            "Fall speed should not be affected"
        }
        assert(draftAfterReset.columnCount == draftBeforeReset.columnCount) {
            "Column count should not be affected"
        }
        
        // But timing settings should be reset
        assert(draftAfterReset.columnStartDelay == defaultSettings.columnStartDelay) {
            "Spawn delay should be reset"
        }
        assert(draftAfterReset.columnRestartDelay == defaultSettings.columnRestartDelay) {
            "Respawn delay should be reset"
        }
    }
}
