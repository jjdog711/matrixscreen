package com.example.matrixscreen.data

import com.example.matrixscreen.data.repo.CustomSymbolSetExport
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(JUnit4::class)
class CustomSymbolSetJsonTest {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    @Test
    fun `CustomSymbolSet serializes to JSON correctly`() {
        // Given
        val customSet = CustomSymbolSet(
            id = "test-id-123",
            name = "Test Symbol Set",
            characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
            fontFileName = "matrix_code_nfi.ttf"
        )

        // When
        val jsonString = json.encodeToString(customSet)

        // Then
        assertNotNull(jsonString)
        assert(jsonString.contains("test-id-123"))
        assert(jsonString.contains("Test Symbol Set"))
        assert(jsonString.contains("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"))
        assert(jsonString.contains("matrix_code_nfi.ttf"))
    }

    @Test
    fun `CustomSymbolSet deserializes from JSON correctly`() {
        // Given
        val jsonString = """{
            "id": "test-id-456",
            "name": "Deserialized Set",
            "characters": "!@#$%^&*()_+-=[]{}|;':\",./<>?",
            "fontFileName": "custom_font.ttf"
        }"""

        // When
        val customSet = json.decodeFromString<CustomSymbolSet>(jsonString)

        // Then
        assertEquals("test-id-456", customSet.id)
        assertEquals("Deserialized Set", customSet.name)
        assertEquals("!@#$%^&*()_+-=[]{}|;':\",./<>?", customSet.characters)
        assertEquals("custom_font.ttf", customSet.fontFileName)
    }

    @Test
    fun `CustomSymbolSetExport serializes correctly`() {
        // Given
        val customSets = listOf(
            CustomSymbolSet(
                id = "set-1",
                name = "Set 1",
                characters = "ABC123",
                fontFileName = "font1.ttf"
            ),
            CustomSymbolSet(
                id = "set-2",
                name = "Set 2",
                characters = "XYZ789",
                fontFileName = "font2.ttf"
            )
        )
        val export = CustomSymbolSetExport(
            version = "1.0",
            exportedAt = 1234567890L,
            customSets = customSets
        )

        // When
        val jsonString = json.encodeToString(export)

        // Then
        assertNotNull(jsonString)
        assert(jsonString.contains("1.0"))
        assert(jsonString.contains("1234567890"))
        assert(jsonString.contains("Set 1"))
        assert(jsonString.contains("Set 2"))
    }

    @Test
    fun `CustomSymbolSetExport deserializes correctly`() {
        // Given
        val jsonString = """{
            "version": "1.0",
            "exportedAt": 9876543210,
            "customSets": [
                {
                    "id": "imported-set-1",
                    "name": "Imported Set 1",
                    "characters": "αβγδεζηθικλμνξοπρστυφχψω",
                    "fontFileName": "greek_font.ttf"
                }
            ]
        }"""

        // When
        val export = json.decodeFromString<CustomSymbolSetExport>(jsonString)

        // Then
        assertEquals("1.0", export.version)
        assertEquals(9876543210L, export.exportedAt)
        assertEquals(1, export.customSets.size)
        assertEquals("imported-set-1", export.customSets[0].id)
        assertEquals("Imported Set 1", export.customSets[0].name)
        assertEquals("αβγδεζηθικλμνξοπρστυφχψω", export.customSets[0].characters)
        assertEquals("greek_font.ttf", export.customSets[0].fontFileName)
    }

    @Test
    fun `JSON round-trip preserves data integrity`() {
        // Given
        val originalSet = CustomSymbolSet(
            id = "round-trip-test",
            name = "Round Trip Test Set",
            characters = "🎯🚀💻🔥⚡🎨🌟💎🎭🎪🎨🎯",
            fontFileName = "emoji_font.ttf"
        )

        // When
        val jsonString = json.encodeToString(originalSet)
        val deserializedSet = json.decodeFromString<CustomSymbolSet>(jsonString)

        // Then
        assertEquals(originalSet.id, deserializedSet.id)
        assertEquals(originalSet.name, deserializedSet.name)
        assertEquals(originalSet.characters, deserializedSet.characters)
        assertEquals(originalSet.fontFileName, deserializedSet.fontFileName)
    }

    @Test
    fun `JSON round-trip for export preserves data integrity`() {
        // Given
        val originalExport = CustomSymbolSetExport(
            version = "2.0",
            exportedAt = 1111111111L,
            customSets = listOf(
                CustomSymbolSet(
                    id = "export-test-1",
                    name = "Export Test 1",
                    characters = "一二三四五六七八九十",
                    fontFileName = "chinese_font.ttf"
                ),
                CustomSymbolSet(
                    id = "export-test-2",
                    name = "Export Test 2",
                    characters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ",
                    fontFileName = "cyrillic_font.ttf"
                )
            )
        )

        // When
        val jsonString = json.encodeToString(originalExport)
        val deserializedExport = json.decodeFromString<CustomSymbolSetExport>(jsonString)

        // Then
        assertEquals(originalExport.version, deserializedExport.version)
        assertEquals(originalExport.exportedAt, deserializedExport.exportedAt)
        assertEquals(originalExport.customSets.size, deserializedExport.customSets.size)
        
        originalExport.customSets.forEachIndexed { index, original ->
            val deserialized = deserializedExport.customSets[index]
            assertEquals(original.id, deserialized.id)
            assertEquals(original.name, deserialized.name)
            assertEquals(original.characters, deserialized.characters)
            assertEquals(original.fontFileName, deserialized.fontFileName)
        }
    }

    @Test
    fun `handles empty custom sets list`() {
        // Given
        val export = CustomSymbolSetExport(
            version = "1.0",
            exportedAt = 0L,
            customSets = emptyList()
        )

        // When
        val jsonString = json.encodeToString(export)
        val deserializedExport = json.decodeFromString<CustomSymbolSetExport>(jsonString)

        // Then
        assertEquals("1.0", deserializedExport.version)
        assertEquals(0L, deserializedExport.exportedAt)
        assert(deserializedExport.customSets.isEmpty())
    }

    @Test
    fun `handles special characters in JSON`() {
        // Given
        val customSet = CustomSymbolSet(
            id = "special-chars-test",
            name = "Set with \"quotes\" and 'apostrophes'",
            characters = "\\n\\r\\t\"'`~!@#$%^&*()_+-=[]{}|;':\",./<>?",
            fontFileName = "special_font.ttf"
        )

        // When
        val jsonString = json.encodeToString(customSet)
        val deserializedSet = json.decodeFromString<CustomSymbolSet>(jsonString)

        // Then
        assertEquals(customSet.id, deserializedSet.id)
        assertEquals(customSet.name, deserializedSet.name)
        assertEquals(customSet.characters, deserializedSet.characters)
        assertEquals(customSet.fontFileName, deserializedSet.fontFileName)
    }
}
