package com.example.matrixscreen.data.registry

import com.example.matrixscreen.data.custom.CustomSymbolSet
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SymbolSetRegistryImpl
 */
class SymbolSetRegistryImplTest {
    
    private val registry = SymbolSetRegistryImpl()
    
    @Test
    fun `getCharacters returns correct characters for built-in symbol sets`() {
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 
            registry.getCharacters(BuiltInSymbolSets.MATRIX_AUTHENTIC))
        assertEquals("01", 
            registry.getCharacters(BuiltInSymbolSets.MATRIX_BINARY))
        assertEquals("0123456789ABCDEF", 
            registry.getCharacters(BuiltInSymbolSets.MATRIX_HEX))
    }
    
    @Test
    fun `getDisplayName returns correct display names`() {
        assertEquals("Matrix Authentic", 
            registry.getDisplayName(BuiltInSymbolSets.MATRIX_AUTHENTIC))
        assertEquals("Binary", 
            registry.getDisplayName(BuiltInSymbolSets.MATRIX_BINARY))
        assertEquals("Custom", 
            registry.getDisplayName(BuiltInSymbolSets.CUSTOM))
    }
    
    @Test
    fun `isValid returns true for valid symbol set IDs`() {
        assertTrue(registry.isValid(BuiltInSymbolSets.MATRIX_AUTHENTIC))
        assertTrue(registry.isValid(BuiltInSymbolSets.MATRIX_BINARY))
        assertTrue(registry.isValid(BuiltInSymbolSets.CUSTOM))
    }
    
    @Test
    fun `isValid returns false for invalid symbol set IDs`() {
        assertFalse(registry.isValid(SymbolSetId("INVALID")))
        assertFalse(registry.isValid(SymbolSetId("")))
    }
    
    @Test
    fun `getAllIds returns all built-in symbol sets`() {
        val allIds = registry.getAllIds()
        assertEquals(6, allIds.size)
        assertTrue(allIds.contains(BuiltInSymbolSets.MATRIX_AUTHENTIC))
        assertTrue(allIds.contains(BuiltInSymbolSets.MATRIX_BINARY))
        assertTrue(allIds.contains(BuiltInSymbolSets.CUSTOM))
    }
    
    @Test
    fun `getCustomCharacters returns correct characters for active custom set`() {
        val customSets = listOf(
            CustomSymbolSet(id = "1", name = "Test Set 1", characters = "ABC"),
            CustomSymbolSet(id = "2", name = "Test Set 2", characters = "XYZ")
        )
        
        assertEquals("ABC", registry.getCustomCharacters(customSets, "1"))
        assertEquals("XYZ", registry.getCustomCharacters(customSets, "2"))
        assertEquals("01", registry.getCustomCharacters(customSets, "3")) // Non-existent ID
        assertEquals("01", registry.getCustomCharacters(customSets, null)) // Null ID
    }
    
    @Test
    fun `getEffectiveCharacters returns custom characters for CUSTOM symbol set`() {
        val customSets = listOf(
            CustomSymbolSet(id = "1", name = "Test Set", characters = "ABC")
        )
        
        assertEquals("ABC", registry.getEffectiveCharacters(
            BuiltInSymbolSets.CUSTOM, customSets, "1"))
        assertEquals("01", registry.getEffectiveCharacters(
            BuiltInSymbolSets.CUSTOM, customSets, null))
    }
    
    @Test
    fun `getEffectiveCharacters returns built-in characters for non-custom symbol sets`() {
        val customSets = emptyList<CustomSymbolSet>()
        
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 
            registry.getEffectiveCharacters(BuiltInSymbolSets.MATRIX_AUTHENTIC, customSets, null))
        assertEquals("01", 
            registry.getEffectiveCharacters(BuiltInSymbolSets.MATRIX_BINARY, customSets, null))
    }
}
