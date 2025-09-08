package com.example.matrixscreen.ui

import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.repo.SettingsRepository
import com.example.matrixscreen.ui.settings.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for NewSettingsViewModel.
 * 
 * Tests UDF pattern implementation with draft/confirm/cancel functionality.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NewSettingsViewModelTest {
    
    @Mock
    private lateinit var mockRepository: SettingsRepository
    
    private lateinit var viewModel: NewSettingsViewModel
    private lateinit var testDispatcher: TestDispatcher
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        
        testDispatcher = UnconfinedTestDispatcher()
        
        // Set up test dispatcher
        Dispatchers.setMain(testDispatcher)
        
        // Mock repository to return default settings
        whenever(mockRepository.observe()).thenReturn(flowOf(MatrixSettings.DEFAULT))
        
        viewModel = NewSettingsViewModel(mockRepository, testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state has default settings and no dirty flag`() = runTest {
        // Given initial state
        advanceUntilIdle()
        
        // Then state should have default settings and not be dirty
        val uiState = viewModel.uiState.value
        assertEquals(MatrixSettings.DEFAULT, uiState.saved)
        assertEquals(MatrixSettings.DEFAULT, uiState.draft)
        assertFalse(uiState.dirty)
    }
    
    @Test
    fun `updateDraft sets dirty flag when value changes`() = runTest {
        // Given initial state
        advanceUntilIdle()
        
        // When updating draft with different value
        viewModel.updateDraft(Speed, 3.0f)
        
        // Then draft is updated and dirty flag is set
        val uiState = viewModel.uiState.value
        assertEquals(3.0f, uiState.draft.fallSpeed, 0.01f)
        assertTrue(uiState.dirty)
    }
    
    @Test
    fun `updateDraft does not set dirty flag when value is same`() = runTest {
        // Given initial state
        advanceUntilIdle()
        
        // When updating draft with same value
        viewModel.updateDraft(Speed, MatrixSettings.DEFAULT.fallSpeed)
        
        // Then draft is updated but dirty flag is not set
        val uiState = viewModel.uiState.value
        assertEquals(MatrixSettings.DEFAULT.fallSpeed, uiState.draft.fallSpeed, 0.01f)
        assertFalse(uiState.dirty)
    }
    
    @Test
    fun `commit persists draft settings`() = runTest {
        // Given draft with changes
        advanceUntilIdle()
        viewModel.updateDraft(Speed, 3.0f)
        viewModel.updateDraft(Columns, 100)
        
        // When committing
        viewModel.commit()
        advanceUntilIdle()
        
        // Then repository save is called with draft settings
        verify(mockRepository).save(MatrixSettings.DEFAULT.copy(fallSpeed = 3.0f, columnCount = 100))
    }
    
    @Test
    fun `revert resets draft to saved settings`() = runTest {
        // Given draft with changes
        advanceUntilIdle()
        viewModel.updateDraft(Speed, 3.0f)
        viewModel.updateDraft(Columns, 100)
        
        // When reverting
        viewModel.revert()
        
        // Then draft is reset to saved settings and dirty flag is cleared
        val uiState = viewModel.uiState.value
        assertEquals(MatrixSettings.DEFAULT, uiState.draft)
        assertFalse(uiState.dirty)
    }
    
    @Test
    fun `resetToDefaults calls repository reset`() = runTest {
        // When resetting to defaults
        viewModel.resetToDefaults()
        advanceUntilIdle()
        
        // Then repository reset is called
        verify(mockRepository).resetToDefaults()
    }
    
    @Test
    fun `getCurrentSettings returns saved settings`() = runTest {
        // Given initial state
        advanceUntilIdle()
        
        // When getting current settings
        val currentSettings = viewModel.getCurrentSettings()
        
        // Then it returns saved settings
        assertEquals(MatrixSettings.DEFAULT, currentSettings)
    }
    
    @Test
    fun `getDraftSettings returns draft settings`() = runTest {
        // Given draft with changes
        advanceUntilIdle()
        viewModel.updateDraft(Speed, 3.0f)
        
        // When getting draft settings
        val draftSettings = viewModel.getDraftSettings()
        
        // Then it returns draft settings
        assertEquals(3.0f, draftSettings.fallSpeed, 0.01f)
    }
    
    @Test
    fun `hasUnsavedChanges returns dirty flag`() = runTest {
        // Given initial state
        advanceUntilIdle()
        assertFalse(viewModel.hasUnsavedChanges())
        
        // When making changes
        viewModel.updateDraft(Speed, 3.0f)
        assertTrue(viewModel.hasUnsavedChanges())
        
        // When reverting
        viewModel.revert()
        assertFalse(viewModel.hasUnsavedChanges())
    }
    
    @Test
    fun `multiple draft updates accumulate changes`() = runTest {
        // Given initial state
        advanceUntilIdle()
        
        // When making multiple changes
        viewModel.updateDraft(Speed, 3.0f)
        viewModel.updateDraft(Columns, 100)
        viewModel.updateDraft(Glow, 1.5f)
        
        // Then all changes are in draft
        val uiState = viewModel.uiState.value
        assertEquals(3.0f, uiState.draft.fallSpeed, 0.01f)
        assertEquals(100, uiState.draft.columnCount)
        assertEquals(1.5f, uiState.draft.glowIntensity, 0.01f)
        assertTrue(uiState.dirty)
    }
    
    @Test
    fun `updateDraft works with different SettingId types`() = runTest {
        // Given initial state
        advanceUntilIdle()
        
        // When updating different types of settings
        viewModel.updateDraft(Speed, 2.5f) // Float
        viewModel.updateDraft(Columns, 200) // Int
        viewModel.updateDraft(HeadColor, 0xFF00FFFFL) // Long (color)
        
        // Then all changes are applied correctly
        val uiState = viewModel.uiState.value
        assertEquals(2.5f, uiState.draft.fallSpeed, 0.01f)
        assertEquals(200, uiState.draft.columnCount)
        assertEquals(0xFF00FFFFL, uiState.draft.headColor)
        assertTrue(uiState.dirty)
    }
    
    @Test
    fun `updateDraft maintains type safety`() = runTest {
        // Given initial state
        advanceUntilIdle()
        
        // When updating with correct types
        viewModel.updateDraft(Speed, 1.0f) // Float for Speed
        viewModel.updateDraft(Columns, 50) // Int for Columns
        viewModel.updateDraft(HeadColor, 0xFF000000L) // Long for HeadColor
        
        // Then values are stored correctly
        val uiState = viewModel.uiState.value
        assertEquals(1.0f, uiState.draft.fallSpeed, 0.01f)
        assertEquals(50, uiState.draft.columnCount)
        assertEquals(0xFF000000L, uiState.draft.headColor)
    }
    
    @Test
    fun `updateDraft works with all SettingId categories`() = runTest {
        // Given initial state
        advanceUntilIdle()
        
        // When updating settings from different categories
        viewModel.updateDraft(Speed, 3.0f) // Motion
        viewModel.updateDraft(Glow, 2.5f) // Effects
        viewModel.updateDraft(GrainD, 300) // Background
        viewModel.updateDraft(FontSize, 16) // Characters
        viewModel.updateDraft(BgColor, 0xFF111111L) // Theme
        
        // Then all changes are applied
        val uiState = viewModel.uiState.value
        assertEquals(3.0f, uiState.draft.fallSpeed, 0.01f)
        assertEquals(2.5f, uiState.draft.glowIntensity, 0.01f)
        assertEquals(300, uiState.draft.grainDensity)
        assertEquals(16, uiState.draft.fontSize)
        assertEquals(0xFF111111L, uiState.draft.backgroundColor)
        assertTrue(uiState.dirty)
    }
}
