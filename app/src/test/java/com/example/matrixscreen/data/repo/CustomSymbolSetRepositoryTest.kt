package com.example.matrixscreen.data.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.example.matrixscreen.data.custom.CustomSymbolSet
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class CustomSymbolSetRepositoryTest {

    @Mock
    private lateinit var mockContext: android.content.Context

    @Mock
    private lateinit var mockDataStore: DataStore<Preferences>

    @Mock
    private lateinit var mockPreferences: MutablePreferences

    private lateinit var repository: CustomSymbolSetRepository

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
        repository = CustomSymbolSetRepository(mockContext, mockDataStore)
    }

    @Test
    fun `getCustomSets returns empty list when no data`() = runTest {
        // Given
        val emptyPreferences = mock<Preferences> {
            on { get(any()) } doReturn null
        }
        whenever(mockDataStore.data).thenReturn(flowOf(emptyPreferences))

        // When
        val result = repository.getCustomSets().first()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getCustomSets returns parsed custom sets`() = runTest {
        // Given
        val customSetsJson = """[{"id":"test-id-1","name":"Test Set 1","characters":"ABC123","fontFileName":"test.ttf"}]"""
        val preferences = mock<Preferences> {
            on { get(any()) } doReturn customSetsJson
        }
        whenever(mockDataStore.data).thenReturn(flowOf(preferences))

        // When
        val result = repository.getCustomSets().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("test-id-1", result[0].id)
        assertEquals("Test Set 1", result[0].name)
        assertEquals("ABC123", result[0].characters)
    }

    @Test
    fun `getActiveCustomSetId returns null when no active set`() = runTest {
        // Given
        val preferences = mock<Preferences> {
            on { get(any()) } doReturn null
        }
        whenever(mockDataStore.data).thenReturn(flowOf(preferences))

        // When
        val result = repository.getActiveCustomSetId().first()

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `getActiveCustomSetId returns active set ID`() = runTest {
        // Given
        val preferences = mock<Preferences> {
            on { get(any()) } doReturn "test-id-1"
        }
        whenever(mockDataStore.data).thenReturn(flowOf(preferences))

        // When
        val result = repository.getActiveCustomSetId().first()

        // Then
        assertEquals("test-id-1", result)
    }

    @Test
    fun `saveCustomSet adds new custom set`() = runTest {
        // Given
        val preferences = mock<Preferences> {
            on { get(any()) } doReturn "[]"
        }
        whenever(mockDataStore.data).thenReturn(flowOf(preferences))
        whenever(mockDataStore.edit(any())).thenAnswer { invocation ->
            val block = invocation.getArgument<suspend (MutablePreferences) -> Unit>(0)
            block(mockPreferences)
            mockPreferences
        }

        // When
        repository.saveCustomSet(testCustomSet1)

        // Then
        verify(mockDataStore).edit(any())
        verify(mockPreferences).set(any(), any())
    }

    @Test
    fun `saveCustomSet updates existing custom set`() = runTest {
        // Given
        val existingSetsJson = """[{"id":"test-id-1","name":"Test Set 1","characters":"ABC123","fontFileName":"test.ttf"}]"""
        val preferences = mock<Preferences> {
            on { get(any()) } doReturn existingSetsJson
        }
        whenever(mockDataStore.data).thenReturn(flowOf(preferences))
        whenever(mockDataStore.edit(any())).thenAnswer { invocation ->
            val block = invocation.getArgument<suspend (MutablePreferences) -> Unit>(0)
            block(mockPreferences)
            mockPreferences
        }

        val updatedSet = testCustomSet1.copy(name = "Updated Test Set")

        // When
        repository.saveCustomSet(updatedSet)

        // Then
        verify(mockDataStore).edit(any())
        verify(mockPreferences).set(any(), any())
    }

    @Test
    fun `deleteCustomSet removes custom set`() = runTest {
        // Given
        val existingSetsJson = """[{"id":"test-id-1","name":"Test Set 1","characters":"ABC123","fontFileName":"test.ttf"}]"""
        val preferences = mock<Preferences> {
            on { get(any()) } doReturn existingSetsJson
        }
        whenever(mockDataStore.data).thenReturn(flowOf(preferences))
        whenever(mockDataStore.edit(any())).thenAnswer { invocation ->
            val block = invocation.getArgument<suspend (MutablePreferences) -> Unit>(0)
            block(mockPreferences)
            mockPreferences
        }

        // When
        repository.deleteCustomSet("test-id-1")

        // Then
        verify(mockDataStore).edit(any())
        verify(mockPreferences).set(any(), any())
    }

    @Test
    fun `setActiveCustomSetId sets active ID`() = runTest {
        // Given
        whenever(mockDataStore.edit(any())).thenAnswer { invocation ->
            val block = invocation.getArgument<suspend (MutablePreferences) -> Unit>(0)
            block(mockPreferences)
            mockPreferences
        }

        // When
        repository.setActiveCustomSetId("test-id-1")

        // Then
        verify(mockDataStore).edit(any())
        verify(mockPreferences).set(any(), eq("test-id-1"))
    }

    @Test
    fun `setActiveCustomSetId removes active ID when null`() = runTest {
        // Given
        whenever(mockDataStore.edit(any())).thenAnswer { invocation ->
            val block = invocation.getArgument<suspend (MutablePreferences) -> Unit>(0)
            block(mockPreferences)
            mockPreferences
        }

        // When
        repository.setActiveCustomSetId(null)

        // Then
        verify(mockDataStore).edit(any())
        verify(mockPreferences).remove(any())
    }

    @Test
    fun `exportToJson creates valid JSON`() {
        // Given
        val customSets = listOf(testCustomSet1, testCustomSet2)

        // When
        val jsonResult = repository.exportToJson(customSets)

        // Then
        assertTrue(jsonResult.contains("Test Set 1"))
        assertTrue(jsonResult.contains("Test Set 2"))
        assertTrue(jsonResult.contains("version"))
        assertTrue(jsonResult.contains("exportedAt"))
    }

    @Test
    fun `importFromJson parses valid JSON`() {
        // Given
        val jsonData = """{
            "version": "1.0",
            "exportedAt": 1234567890,
            "customSets": [
                {"id":"test-id-1","name":"Test Set 1","characters":"ABC123","fontFileName":"test.ttf"}
            ]
        }"""

        // When
        val result = repository.importFromJson(jsonData)

        // Then
        assertTrue(result is ImportResult.Success)
        val successResult = result as ImportResult.Success
        assertEquals(1, successResult.customSets.size)
        assertEquals("Test Set 1", successResult.customSets[0].name)
    }

    @Test
    fun `importFromJson handles invalid JSON`() {
        // Given
        val invalidJson = "invalid json"

        // When
        val result = repository.importFromJson(invalidJson)

        // Then
        assertTrue(result is ImportResult.Error)
    }

    @Test
    fun `importAndMerge merges custom sets without conflicts`() = runTest {
        // Given
        val existingSetsJson = """[{"id":"test-id-1","name":"Test Set 1","characters":"ABC123","fontFileName":"test.ttf"}]"""
        val preferences = mock<Preferences> {
            on { get(any()) } doReturn existingSetsJson
        }
        whenever(mockDataStore.data).thenReturn(flowOf(preferences))
        whenever(mockDataStore.edit(any())).thenAnswer { invocation ->
            val block = invocation.getArgument<suspend (MutablePreferences) -> Unit>(0)
            block(mockPreferences)
            mockPreferences
        }

        val importJson = """{
            "version": "1.0",
            "exportedAt": 1234567890,
            "customSets": [
                {"id":"test-id-2","name":"Test Set 2","characters":"XYZ789","fontFileName":"test2.ttf"}
            ]
        }"""

        // When
        val result = repository.importAndMerge(importJson)

        // Then
        assertTrue(result is ImportResult.Success)
        val successResult = result as ImportResult.Success
        assertEquals(2, successResult.customSets.size)
    }

    @Test
    fun `importAndMerge handles conflicts by creating new IDs`() = runTest {
        // Given
        val existingSetsJson = """[{"id":"test-id-1","name":"Test Set 1","characters":"ABC123","fontFileName":"test.ttf"}]"""
        val preferences = mock<Preferences> {
            on { get(any()) } doReturn existingSetsJson
        }
        whenever(mockDataStore.data).thenReturn(flowOf(preferences))
        whenever(mockDataStore.edit(any())).thenAnswer { invocation ->
            val block = invocation.getArgument<suspend (MutablePreferences) -> Unit>(0)
            block(mockPreferences)
            mockPreferences
        }

        val importJson = """{
            "version": "1.0",
            "exportedAt": 1234567890,
            "customSets": [
                {"id":"test-id-1","name":"Test Set 1","characters":"ABC123","fontFileName":"test.ttf"}
            ]
        }"""

        // When
        val result = repository.importAndMerge(importJson)

        // Then
        assertTrue(result is ImportResult.Success)
        val successResult = result as ImportResult.Success
        assertEquals(2, successResult.customSets.size)
        // One should have the original name, one should have "(Imported)" suffix
        val names = successResult.customSets.map { it.name }
        assertTrue(names.contains("Test Set 1"))
        assertTrue(names.any { it.contains("Imported") })
    }
}
