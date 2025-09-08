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
 * UI tests for confirm/cancel functionality in EffectsSettingsScreen.
 * 
 * Tests that the commit and revert operations work correctly
 * with the UDF (Unidirectional Data Flow) pattern.
 */
@RunWith(AndroidJUnit4::class)
class EffectsSettingsConfirmCancelTest {
    
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
    fun `commit_saves_draft_changes_to_repository`() {
        // Given initial state with draft changes
        composeTestRule.waitForIdle()
        
        // Make changes to effects settings
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
        
        // Verify draft changes
        val draftSettings = viewModel.getDraftSettings()
        assert(draftSettings != defaultSettings) {
            "Draft should be different from defaults"
        }
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
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after commit"
        }
    }
    
    @Test
    fun `revert_discards_draft_changes_and_restores_saved_settings`() {
        // Given initial state with draft changes
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // Make changes to effects settings
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
        
        // Verify draft changes
        val draftWithChanges = viewModel.getDraftSettings()
        assert(draftWithChanges != initialSavedSettings) {
            "Draft should be different from saved settings"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes before revert"
        }
        
        // When reverting changes
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        // Then draft should be restored to saved settings
        val draftAfterRevert = viewModel.getDraftSettings()
        assert(draftAfterRevert == initialSavedSettings) {
            "Draft should be reverted to saved settings"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert"
        }
    }
    
    @Test
    fun `commit_then_revert_workflow_works_correctly`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
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
        
        val draftWithChanges = viewModel.getDraftSettings()
        
        // Commit changes
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        // Verify commit
        runTest {
            verify(mockRepository).save(draftWithChanges)
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after commit"
        }
        
        // Make new changes
        composeTestRule.onNodeWithText("Jitter Amount")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        val draftWithNewChanges = viewModel.getDraftSettings()
        assert(draftWithNewChanges != draftWithChanges) {
            "New draft should be different from committed settings"
        }
        assert(viewModel.hasUnsavedChanges()) {
            "Should have unsaved changes after new modifications"
        }
        
        // Revert new changes
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        // Verify revert
        val draftAfterRevert = viewModel.getDraftSettings()
        assert(draftAfterRevert == draftWithChanges) {
            "Draft should be reverted to last committed settings"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert"
        }
    }
    
    @Test
    fun `multiple_commits_work_correctly`() {
        // Given initial state
        composeTestRule.waitForIdle()
        
        // First set of changes
        composeTestRule.onNodeWithText("Glow Intensity")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeRight() }
        
        composeTestRule.waitForIdle()
        
        val firstDraft = viewModel.getDraftSettings()
        
        // Commit first changes
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        runTest {
            verify(mockRepository).save(firstDraft)
        }
        
        // Second set of changes
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
        
        val secondDraft = viewModel.getDraftSettings()
        assert(secondDraft != firstDraft) {
            "Second draft should be different from first"
        }
        
        // Commit second changes
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        runTest {
            verify(mockRepository).save(secondDraft)
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after second commit"
        }
    }
    
    @Test
    fun `revert_without_changes_does_nothing`() {
        // Given initial state with no changes
        composeTestRule.waitForIdle()
        val initialDraft = viewModel.getDraftSettings()
        val initialSaved = viewModel.getCurrentSettings()
        
        // When reverting without changes
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        // Then state should remain unchanged
        val draftAfterRevert = viewModel.getDraftSettings()
        assert(draftAfterRevert == initialDraft) {
            "Draft should remain unchanged after revert with no changes"
        }
        assert(draftAfterRevert == initialSaved) {
            "Draft should equal saved settings after revert with no changes"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert with no changes"
        }
    }
    
    @Test
    fun `commit_without_changes_does_nothing`() {
        // Given initial state with no changes
        composeTestRule.waitForIdle()
        val initialDraft = viewModel.getDraftSettings()
        
        // When committing without changes
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        // Then repository should still be called (for consistency)
        runTest {
            verify(mockRepository).save(initialDraft)
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after commit with no changes"
        }
    }
    
    @Test
    fun `commit_and_revert_maintain_state_consistency`() {
        // Given initial state
        composeTestRule.waitForIdle()
        val initialSavedSettings = viewModel.getCurrentSettings()
        
        // Make changes
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
        
        val draftWithAllChanges = viewModel.getDraftSettings()
        
        // Commit all changes
        viewModel.commit()
        composeTestRule.waitForIdle()
        
        runTest {
            verify(mockRepository).save(draftWithAllChanges)
        }
        
        // Make more changes
        composeTestRule.onNodeWithText("Glow Intensity")
            .onParent()
            .onChild()
            .onChildAt(1)
            .performTouchInput { swipeLeft() } // Move in opposite direction
        
        composeTestRule.waitForIdle()
        
        val draftWithNewChanges = viewModel.getDraftSettings()
        assert(draftWithNewChanges != draftWithAllChanges) {
            "New draft should be different from committed settings"
        }
        
        // Revert to committed state
        viewModel.revert()
        composeTestRule.waitForIdle()
        
        val draftAfterRevert = viewModel.getDraftSettings()
        assert(draftAfterRevert == draftWithAllChanges) {
            "Draft should be reverted to last committed state"
        }
        assert(!viewModel.hasUnsavedChanges()) {
            "Should not have unsaved changes after revert"
        }
    }
}
