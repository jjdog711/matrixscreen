package com.example.matrixscreen.data.registry

import com.example.matrixscreen.data.SymbolSet
import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.model.MatrixSettings
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for SymbolEngineConfig and its integration with SymbolSet
 */
class SymbolEngineConfigTest {
    
    @Test
    fun `toSymbolEngineConfig creates correct config from MatrixSettings`() {
        val customSets = listOf(
            CustomSymbolSet(id = "1", name = "Test Set", characters = "ABC")
        )
        val settings = MatrixSettings(
            symbolSetId = "CUSTOM",
            savedCustomSets = customSets,
            activeCustomSetId = "1"
        )
        
        val config = settings.toSymbolEngineConfig()
        
        assertEquals("CUSTOM", config.symbolSetId)
        assertEquals(1, config.savedCustomSets.size)
        assertEquals("1", config.activeCustomSetId)
        assertEquals("Test Set", config.savedCustomSets[0].name)
    }
    
    @Test
    fun `SymbolSet effectiveCharacters works with SymbolEngineConfig`() {
        val customSets = listOf(
            CustomSymbolSet(id = "1", name = "Test Set", characters = "ABC")
        )
        val config = SymbolEngineConfig(
            symbolSetId = "CUSTOM",
            savedCustomSets = customSets,
            activeCustomSetId = "1"
        )
        
        // Test built-in symbol sets
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", 
            SymbolSet.MATRIX_AUTHENTIC.effectiveCharacters(config))
        assertEquals("01", 
            SymbolSet.BINARY.effectiveCharacters(config))
        
        // Test custom symbol set
        assertEquals("ABC", 
            SymbolSet.CUSTOM.effectiveCharacters(config))
    }
    
    @Test
    fun `SymbolSet effectiveCharacters handles missing custom set gracefully`() {
        val config = SymbolEngineConfig(
            symbolSetId = "CUSTOM",
            savedCustomSets = emptyList(),
            activeCustomSetId = "nonexistent"
        )
        
        assertEquals("01", SymbolSet.CUSTOM.effectiveCharacters(config))
    }
    
    @Test
    fun `SymbolSet effectiveCharacters handles null active custom set`() {
        val customSets = listOf(
            CustomSymbolSet(id = "1", name = "Test Set", characters = "ABC")
        )
        val config = SymbolEngineConfig(
            symbolSetId = "CUSTOM",
            savedCustomSets = customSets,
            activeCustomSetId = null
        )
        
        assertEquals("01", SymbolSet.CUSTOM.effectiveCharacters(config))
    }
    
    @Test
    fun `SymbolEngineConfig preserves all custom symbol set data`() {
        val customSets = listOf(
            CustomSymbolSet(id = "1", name = "Set 1", characters = "ABC"),
            CustomSymbolSet(id = "2", name = "Set 2", characters = "XYZ")
        )
        val config = SymbolEngineConfig(
            symbolSetId = "CUSTOM",
            savedCustomSets = customSets,
            activeCustomSetId = "2"
        )
        
        assertEquals(2, config.savedCustomSets.size)
        assertEquals("Set 1", config.savedCustomSets[0].name)
        assertEquals("Set 2", config.savedCustomSets[1].name)
        assertEquals("XYZ", SymbolSet.CUSTOM.effectiveCharacters(config))
    }
}
