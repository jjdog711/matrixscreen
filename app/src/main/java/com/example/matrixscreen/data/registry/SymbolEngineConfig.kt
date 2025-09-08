package com.example.matrixscreen.data.registry

import com.example.matrixscreen.data.custom.CustomSymbolSet

/**
 * Domain-level configuration for symbol engine operations.
 * 
 * This DTO contains only the fields needed for symbol set resolution,
 * allowing the SymbolSet.effectiveCharacters() method to work with
 * the new domain model without requiring legacy settings.
 */
data class SymbolEngineConfig(
    val symbolSetId: String,
    val savedCustomSets: List<CustomSymbolSet>,
    val activeCustomSetId: String?
)

/**
 * Extension function to create SymbolEngineConfig from MatrixSettings.
 */
fun com.example.matrixscreen.data.model.MatrixSettings.toSymbolEngineConfig(): SymbolEngineConfig {
    return SymbolEngineConfig(
        symbolSetId = this.symbolSetId,
        savedCustomSets = this.savedCustomSets,
        activeCustomSetId = this.activeCustomSetId
    )
}
