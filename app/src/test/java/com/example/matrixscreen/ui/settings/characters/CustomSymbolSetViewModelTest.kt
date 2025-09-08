package com.example.matrixscreen.ui.settings.characters

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.repo.CustomSymbolSetRepository
import com.example.matrixscreen.data.repo.ImportResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class CustomSymbolSetViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockRepository: CustomSymbolSetRepository

    private lateinit var viewModel: CustomSymbolSetViewModel
    private lateinit var testDispatcher: TestDispatcher

    private val testCustomSet1 = CustomSymbolSet(
        id = "test-id-1",
        name = "Test Set 1",
        characters = "ABC123",
        fontFileName = "test.ttf"
    )

    private val testCustomSet2 = CustomSymbolSet(
        id = "test-id-2",
        name = "Test Set 2",
        characters = "XYZ789",
        fontFileName = "test2.ttf"
    )

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        
        // Mock repository responses
        whenever(mockRepository.getCustomSets()).thenReturn(flowOf(listOf(testCustomSet1, testCustomSet2)))
        whenever(mockRepository.getActiveCustomSetId()).thenReturn(flowOf("test-id-1"))
        
        viewModel = CustomSymbolSetViewModel(mockRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads custom sets and active ID`() = runTest {
        // When
        testDispatcher.scheduler.advanceUntilIdle()
        val uiState = viewModel.uiState.value

        // Then
        assertEquals(2, uiState.customSets.size)
        assertEquals("test-id-1", uiState.activeCustomSetId)
        assertFalse(uiState.isLoading)
        assertEquals(null, uiState.error)
        assertTrue(uiState.importExportState is ImportExportState.Idle)
    }

    @Test
    fun `saveCustomSet calls repository and updates state`() = runTest {
        // Given
        val newCustomSet = CustomSymbolSet(
            id = "new-id",
            name = "New Set",
            characters = "NEW123",
            fontFileName = "new.ttf"
        )

        // When
        viewModel.saveCustomSet(newCustomSet)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockRepository).saveCustomSet(newCustomSet)
    }

    @Test
    fun `saveCustomSet handles repository error`() = runTest {
        // Given
        val newCustomSet = CustomSymbolSet(
            id = "new-id",
            name = "New Set",
            characters = "NEW123",
            fontFileName = "new.ttf"
        )
        whenever(mockRepository.saveCustomSet(any())).thenThrow(RuntimeException("Save failed"))

        // When
        viewModel.saveCustomSet(newCustomSet)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.error?.contains("Save failed") == true)
    }

    @Test
    fun `deleteCustomSet calls repository`() = runTest {
        // When
        viewModel.deleteCustomSet("test-id-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockRepository).deleteCustomSet("test-id-1")
    }

    @Test
    fun `deleteCustomSet handles repository error`() = runTest {
        // Given
        whenever(mockRepository.deleteCustomSet(any())).thenThrow(RuntimeException("Delete failed"))

        // When
        viewModel.deleteCustomSet("test-id-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.error?.contains("Delete failed") == true)
    }

    @Test
    fun `setActiveCustomSet calls repository`() = runTest {
        // When
        viewModel.setActiveCustomSet("test-id-2")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(mockRepository).setActiveCustomSetId("test-id-2")
    }

    @Test
    fun `setActiveCustomSet handles repository error`() = runTest {
        // Given
        whenever(mockRepository.setActiveCustomSetId(any())).thenThrow(RuntimeException("Set active failed"))

        // When
        viewModel.setActiveCustomSet("test-id-2")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.error?.contains("Set active failed") == true)
    }

    @Test
    fun `exportCustomSets exports successfully`() = runTest {
        // Given
        val exportJson = """{"version":"1.0","exportedAt":1234567890,"customSets":[]}"""
        whenever(mockRepository.exportToJson(any())).thenReturn(exportJson)

        // When
        viewModel.exportCustomSets()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.importExportState is ImportExportState.ExportSuccess)
        val exportState = uiState.importExportState as ImportExportState.ExportSuccess
        assertEquals(exportJson, exportState.jsonData)
    }

    @Test
    fun `exportCustomSets handles export error`() = runTest {
        // Given
        whenever(mockRepository.exportToJson(any())).thenThrow(RuntimeException("Export failed"))

        // When
        viewModel.exportCustomSets()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.importExportState is ImportExportState.Error)
        val errorState = uiState.importExportState as ImportExportState.Error
        assertTrue(errorState.message.contains("Export failed"))
    }

    @Test
    fun `importCustomSets imports successfully`() = runTest {
        // Given
        val importJson = """{"version":"1.0","exportedAt":1234567890,"customSets":[]}"""
        val importResult = ImportResult.Success(listOf(testCustomSet1))
        whenever(mockRepository.importAndMerge(importJson)).thenReturn(importResult)

        // When
        viewModel.importCustomSets(importJson)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.importExportState is ImportExportState.ImportSuccess)
        val importState = uiState.importExportState as ImportExportState.ImportSuccess
        assertEquals(1, importState.importedCount)
    }

    @Test
    fun `importCustomSets handles import error`() = runTest {
        // Given
        val importJson = """invalid json"""
        val importResult = ImportResult.Error("Invalid JSON format")
        whenever(mockRepository.importAndMerge(importJson)).thenReturn(importResult)

        // When
        viewModel.importCustomSets(importJson)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.importExportState is ImportExportState.Error)
        val errorState = uiState.importExportState as ImportExportState.Error
        assertEquals("Invalid JSON format", errorState.message)
    }

    @Test
    fun `importCustomSets handles repository exception`() = runTest {
        // Given
        val importJson = """{"version":"1.0","exportedAt":1234567890,"customSets":[]}"""
        whenever(mockRepository.importAndMerge(importJson)).thenThrow(RuntimeException("Import failed"))

        // When
        viewModel.importCustomSets(importJson)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.importExportState is ImportExportState.Error)
        val errorState = uiState.importExportState as ImportExportState.Error
        assertTrue(errorState.message.contains("Import failed"))
    }

    @Test
    fun `clearImportExportState resets to idle`() = runTest {
        // Given
        viewModel.exportCustomSets()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.importExportState is ImportExportState.ExportSuccess)

        // When
        viewModel.clearImportExportState()

        // Then
        assertTrue(viewModel.uiState.value.importExportState is ImportExportState.Idle)
    }

    @Test
    fun `clearError removes error state`() = runTest {
        // Given
        whenever(mockRepository.saveCustomSet(any())).thenThrow(RuntimeException("Save failed"))
        viewModel.saveCustomSet(testCustomSet1)
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.uiState.value.error != null)

        // When
        viewModel.clearError()

        // Then
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `getCustomSetById returns correct set`() = runTest {
        // When
        val result = viewModel.getCustomSetById("test-id-1")

        // Then
        assertEquals(testCustomSet1, result)
    }

    @Test
    fun `getCustomSetById returns null for non-existent ID`() = runTest {
        // When
        val result = viewModel.getCustomSetById("non-existent-id")

        // Then
        assertEquals(null, result)
    }
}
