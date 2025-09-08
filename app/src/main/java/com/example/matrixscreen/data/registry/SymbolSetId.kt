package com.example.matrixscreen.data.registry

/**
 * Value class for symbol set identifiers.
 * 
 * This provides type safety for symbol set IDs while maintaining
 * efficient storage as strings.
 */
@JvmInline
value class SymbolSetId(val value: String) {
    override fun toString(): String = value
}

/**
 * Registry interface for symbol set resolution.
 * 
 * This allows the system to resolve symbol set IDs to their
 * actual character sets and metadata.
 */
interface SymbolSetRegistry {
    /**
     * Get the characters for a given symbol set ID.
     * 
     * @param id The symbol set ID
     * @return The character string for this symbol set
     */
    fun getCharacters(id: SymbolSetId): String
    
    /**
     * Get the display name for a given symbol set ID.
     * 
     * @param id The symbol set ID
     * @return The display name for this symbol set
     */
    fun getDisplayName(id: SymbolSetId): String
    
    /**
     * Check if a symbol set ID is valid.
     * 
     * @param id The symbol set ID
     * @return true if the ID is valid
     */
    fun isValid(id: SymbolSetId): Boolean
    
    /**
     * Get all available symbol set IDs.
     * 
     * @return List of all available symbol set IDs
     */
    fun getAllIds(): List<SymbolSetId>
}

/**
 * Built-in symbol set IDs.
 */
object BuiltInSymbolSets {
    val MATRIX_AUTHENTIC = SymbolSetId("MATRIX_AUTHENTIC")
    val MATRIX_GLITCH = SymbolSetId("MATRIX_GLITCH")
    val BINARY = SymbolSetId("BINARY")
    val HEX = SymbolSetId("HEX")
    val MIXED = SymbolSetId("MIXED")
    val LATIN = SymbolSetId("LATIN")
    val KATAKANA = SymbolSetId("KATAKANA")
    val NUMBERS = SymbolSetId("NUMBERS")
    val CUSTOM = SymbolSetId("CUSTOM")
    
    val ALL_BUILT_IN = listOf(
        MATRIX_AUTHENTIC,
        MATRIX_GLITCH,
        BINARY,
        HEX,
        MIXED,
        LATIN,
        KATAKANA,
        NUMBERS,
        CUSTOM
    )
}
