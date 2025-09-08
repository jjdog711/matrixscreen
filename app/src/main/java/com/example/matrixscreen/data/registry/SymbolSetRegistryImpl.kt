package com.example.matrixscreen.data.registry

import com.example.matrixscreen.data.SymbolSet
import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.repo.CustomSymbolSetRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/**
 * Implementation of SymbolSetRegistry that bridges the legacy SymbolSet enum
 * with the new registry system and provides dynamic custom symbol set support.
 */
@Singleton
class SymbolSetRegistryImpl @Inject constructor(
    private val customSymbolSetRepository: CustomSymbolSetRepository
) : SymbolSetRegistry {
    
    override fun getCharacters(id: SymbolSetId): String {
        return when (id.value) {
            "MATRIX_AUTHENTIC" -> SymbolSet.MATRIX_AUTHENTIC.characters
            "MATRIX_GLITCH" -> SymbolSet.MATRIX_GLITCH.characters
            "BINARY" -> SymbolSet.BINARY.characters
            "HEX" -> SymbolSet.HEX.characters
            "MIXED" -> SymbolSet.MIXED.characters
            "LATIN" -> SymbolSet.LATIN.characters
            "KATAKANA" -> SymbolSet.KATAKANA.characters
            "NUMBERS" -> SymbolSet.NUMBERS.characters
            "CUSTOM" -> getCustomCharactersSync() // Dynamic lookup for custom sets
            else -> SymbolSet.MATRIX_AUTHENTIC.characters // Default fallback
        }
    }
    
    /**
     * Get characters for custom symbol sets synchronously.
     * This method provides a fallback for cases where async access isn't available.
     * 
     * @return The characters for the active custom set, or fallback if not found
     */
    private fun getCustomCharactersSync(): String {
        return try {
            // Since Flow properties can't be accessed synchronously,
            // return a reasonable fallback and log the limitation
            android.util.Log.w("SymbolSetRegistryImpl", 
                "getCharacters() called for CUSTOM set - use getEffectiveCharacters() for dynamic lookup")
            "01" // Fallback to binary
        } catch (e: Exception) {
            android.util.Log.e("SymbolSetRegistryImpl", "Error getting custom characters", e)
            "01" // Fallback to binary
        }
    }
    
    override fun getDisplayName(id: SymbolSetId): String {
        return when (id.value) {
            "MATRIX_AUTHENTIC" -> SymbolSet.MATRIX_AUTHENTIC.displayName
            "MATRIX_GLITCH" -> SymbolSet.MATRIX_GLITCH.displayName
            "BINARY" -> SymbolSet.BINARY.displayName
            "HEX" -> SymbolSet.HEX.displayName
            "MIXED" -> SymbolSet.MIXED.displayName
            "LATIN" -> SymbolSet.LATIN.displayName
            "KATAKANA" -> SymbolSet.KATAKANA.displayName
            "NUMBERS" -> SymbolSet.NUMBERS.displayName
            "CUSTOM" -> "Custom"
            else -> "Unknown"
        }
    }
    
    override fun isValid(id: SymbolSetId): Boolean {
        return when (id.value) {
            "MATRIX_AUTHENTIC", "MATRIX_GLITCH", "BINARY", 
            "HEX", "MIXED", "LATIN", "KATAKANA", "NUMBERS", "CUSTOM" -> true
            else -> false
        }
    }
    
    override fun getAllIds(): List<SymbolSetId> {
        return BuiltInSymbolSets.ALL_BUILT_IN
    }
    
    /**
     * Get characters for a custom symbol set.
     * 
     * @param customSets List of custom symbol sets
     * @param activeCustomSetId The active custom set ID
     * @return The characters for the active custom set, or fallback if not found
     */
    fun getCustomCharacters(customSets: List<CustomSymbolSet>, activeCustomSetId: String?): String {
        if (activeCustomSetId == null) return "01"
        return customSets.find { it.id == activeCustomSetId }?.characters ?: "01"
    }
    
    /**
     * Get effective characters for a symbol set, considering custom sets.
     * 
     * @param id The symbol set ID
     * @param customSets List of custom symbol sets
     * @param activeCustomSetId The active custom set ID
     * @return The effective characters for this symbol set
     */
    fun getEffectiveCharacters(
        id: SymbolSetId, 
        customSets: List<CustomSymbolSet>, 
        activeCustomSetId: String?
    ): String {
        return if (id == BuiltInSymbolSets.CUSTOM) {
            getCustomCharacters(customSets, activeCustomSetId)
        } else {
            getCharacters(id)
        }
    }
    
    /**
     * Get effective characters for a symbol set using the repository.
     * This is the preferred method for dynamic custom symbol set lookup.
     * 
     * @param id The symbol set ID
     * @return Flow of effective characters for this symbol set
     */
    fun getEffectiveCharactersFlow(id: SymbolSetId): kotlinx.coroutines.flow.Flow<String> {
        return if (id == BuiltInSymbolSets.CUSTOM) {
            // For custom sets, combine the saved sets and active ID flows
            kotlinx.coroutines.flow.combine(
                customSymbolSetRepository.savedSets,
                customSymbolSetRepository.activeCustomSetId
            ) { customSets, activeId ->
                getCustomCharacters(customSets, activeId)
            }
        } else {
            // For built-in sets, return a static flow
            kotlinx.coroutines.flow.flowOf(getCharacters(id))
        }
    }
}
