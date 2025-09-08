package com.example.matrixscreen.ui.settings.motion

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
 * UI tests for confirm/cancel functionality in MotionSettingsScreen.
 * 
 * Tests that the UDF (Unidirectional Data Flow) pattern works correctly
 * with draft updates, confirm (commit), and cancel (revert) operations.
 * 
 * Note: These tests focus on the ViewModel's confirm/cancel functionality
 * since the MotionSettingsScreen itself doesn't have explicit confirm/cancel buttons.
 * The confirm/cancel operations are typically handled at a higher level (e.g., in the overlay).
 */
@RunWith(AndroidJUnit4::class)
class MotionSettingsConfirmCancelTest {
    
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
    fun `draft_updates_do_not_affect_saved_settings_until_commit`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // When making draft changes
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
        
        // Then saved settings should remain unchanged
        val currentSavedSettings = viewModel.getCurrentSettings()
        assert(currentSavedSettings == initialSavedSettings) {
            "Saved settings should not change until commit"
        }
        
        // But draft should be different
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings != currentSavedSettings) {
            "Draft settings should be different from saved settings"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after draft updates"
        }
    }
    
    @Test
    fun `commit_persists_draft_settings_to_repository`() {
        // Given modified draft state
        composeTestRule.waitForIdle()
        
        // Make draft changes
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
        
        val draftSettings = viewModel.getDraftSettings()
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before commit"
        }
        
        // When committing changes
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        // Then repository should be called with draft settings
        runTest {
            verify(mockRepository).save(draftSettings)
        }
    }
    
    @Test
    fun `revert_resets_draft_to_saved_settings`() {
        // Given modified draft state
        composeTestRule.waitForIdle()
        
        // Make draft changes
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
        
        val savedSettings = viewModel.getCurrentSettings()
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before revert"
        }
        
        // When reverting changes
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        // Then draft should be reset to saved settings
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings == savedSettings) {
            "Draft settings should be reset to saved settings after revert"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert"
        }
    }
    
    @Test
    fun `commit_clears_dirty_flag`() {
        // Given modified draft state
        composeTestRule.waitForIdle()
        
        // Make draft changes
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before commit"
        }
        
        // When committing changes
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        // Then dirty flag should be cleared
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after commit"
        }
    }
    
    @Test
    fun `revert_clears_dirty_flag`() {
        // Given modified draft state
        composeTestRule.waitForIdle()
        
        // Make draft changes
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before revert"
        }
        
        // When reverting changes
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        // Then dirty flag should be cleared
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert"
        }
    }
    
    @Test
    fun `multiple_draft_updates_accumulate_before_commit`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // When making multiple draft changes
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
        
        // Then all changes should be in draft
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
            "Should have unsaved changes after multiple draft updates"
        }
        
        // When committing
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        // Then all changes should be persisted
        runTest {
            verify(mockRepository).save(draftSettings)
        }
    }
    
    @Test
    fun `revert_after_multiple_draft_updates_resets_all_changes`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // When making multiple draft changes
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
        
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before revert"
        }
        
        // When reverting
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        // Then all changes should be reverted
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings == initialSavedSettings) {
            "All draft changes should be reverted"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert"
        }
    }
    
    @Test
    fun `commit_and_revert_cycle_works_correctly`() {
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
        
        val draftSettings = viewModel.getDraftSettings()
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before commit"
        }
        
        // When committing
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        runTest {
            verify(mockRepository).save(draftSettings)
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after commit"
        }
        
        // Make new draft changes
        composeTestRule.onNodeWithText("Column Count")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before revert"
        }
        
        // When reverting
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        // Then draft should be reset to the committed settings
        val revertedDraftSettings = viewModel.getDraftSettings()
        assert(revertedDraftSettings == draftSettings) {
            "Draft should be reset to the last committed settings"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert"
        }
    }
    
    @Test
    fun `draft_updates_work_after_commit`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Make and commit changes
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after commit"
        }
        
        // When making new draft changes
        composeTestRule.onNodeWithText("Column Count")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Then new changes should be in draft
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after new draft updates"
        }
        
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.columnCount != defaultSettings.columnCount) {
            "New draft changes should be present"
        }
    }
    
    @Test
    fun `draft_updates_work_after_revert`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // Make and revert changes
        composeTestRule.onNodeWithText("Fall Speed")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert"
        }
        
        // When making new draft changes
        composeTestRule.onNodeWithText("Column Count")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        // Then new changes should be in draft
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after new draft updates"
        }
        
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings.columnCount != defaultSettings.columnCount) {
            "New draft changes should be present"
        }
    }
}
