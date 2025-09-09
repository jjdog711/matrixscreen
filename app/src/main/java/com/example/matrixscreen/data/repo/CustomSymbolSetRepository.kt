package com.example.matrixscreen.data.repo

import androidx.datastore.core.DataStore
import com.example.matrixscreen.data.proto.MatrixSettingsProto
import com.example.matrixscreen.data.custom.CustomSymbolSet
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Singleton
class CustomSymbolSetRepository @Inject constructor(
    private val settingsStore: DataStore<MatrixSettingsProto>,
    private val json: Json
) {

    private val listSerializer = ListSerializer(CustomSymbolSet.serializer())
    
    val savedSets: Flow<List<CustomSymbolSet>> =
        settingsStore.data.map { proto ->
            val raw = proto.savedCustomSets
            if (raw.isNullOrBlank()) emptyList()
            else runCatching { json.decodeFromString(listSerializer, raw) }.getOrElse { emptyList() }
        }

    val activeCustomSetId: Flow<String?> =
        settingsStore.data.map { it.activeCustomSetId.takeIf { id -> id.isNotBlank() } }

    val selectedSymbolSetId: Flow<String> =
        settingsStore.data.map { it.symbolSetId } // String ID for built-in or "custom:<id>"
    
    suspend fun setSelectedSymbolSetId(id: String) {
        settingsStore.updateData { cur ->
            cur.toBuilder()
                .setSymbolSetId(id)
                .build()
        }
    }

    suspend fun setActiveCustomSetId(id: String?) {
        settingsStore.updateData { cur ->
            cur.toBuilder()
                .setActiveCustomSetId(id ?: "")
                .build()
        }
    }

    suspend fun upsertCustomSet(set: CustomSymbolSet) {
        settingsStore.updateData { cur ->
            val current = decodeList(cur.savedCustomSets)
            val updated = current
                .filterNot { it.id == set.id } // replace if same id
                .plus(set)
            cur.toBuilder()
                .setSavedCustomSets(encodeList(updated))
                .build()
        }
    }

    suspend fun deleteCustomSet(id: String) {
        settingsStore.updateData { cur ->
            val current = decodeList(cur.savedCustomSets)
            val updated = current.filterNot { it.id == id }
            val newActive = cur.activeCustomSetId.takeIf { it != id } ?: ""
            cur.toBuilder()
                .setSavedCustomSets(encodeList(updated))
                .setActiveCustomSetId(newActive)
                .build()
        }
    }
    
    private fun decodeList(raw: String?): List<CustomSymbolSet> =
        if (raw.isNullOrBlank()) emptyList()
        else runCatching { json.decodeFromString(listSerializer, raw) }.getOrElse { emptyList() }

    private fun encodeList(list: List<CustomSymbolSet>): String =
        json.encodeToString(listSerializer, list)
    
    /**
     * Public method to decode custom sets from JSON string
     */
    fun decodeCustomSets(raw: String?): List<CustomSymbolSet> = decodeList(raw)
    
    /**
     * Public method to encode custom sets to JSON string
     */
    fun encodeCustomSets(list: List<CustomSymbolSet>): String = encodeList(list)
    
    /**
     * Export custom symbol sets to JSON format
     */
    fun exportToJson(customSets: List<CustomSymbolSet>): String {
        val exportData = CustomSymbolSetExport(
            version = "1.0",
            exportedAt = System.currentTimeMillis(),
            customSets = customSets
        )
        return json.encodeToString(CustomSymbolSetExport.serializer(), exportData)
    }
    
    /**
     * Import custom symbol sets from JSON format
     */
    fun importFromJson(jsonString: String): ImportResult {
        return try {
            val exportData = json.decodeFromString(CustomSymbolSetExport.serializer(), jsonString)
            ImportResult.Success(exportData.customSets)
        } catch (e: Exception) {
            ImportResult.Error(e.message ?: "Unknown import error")
        }
    }
    
    /**
     * Import and merge custom symbol sets from JSON
     */
    suspend fun importAndMerge(jsonString: String): ImportResult {
        val importResult = importFromJson(jsonString)
        return when (importResult) {
            is ImportResult.Success -> {
                try {
                    val currentSets = settingsStore.data.map { proto -> decodeList(proto.savedCustomSets) }.first()
                    val mergedSets = mergeCustomSets(currentSets, importResult.customSets)
                    
                    settingsStore.updateData { cur ->
                        cur.toBuilder()
                            .setSavedCustomSets(encodeList(mergedSets))
                            .build()
                    }
                    
                    ImportResult.Success(mergedSets)
                } catch (e: Exception) {
                    ImportResult.Error("Failed to merge sets: ${e.message}")
                }
            }
            is ImportResult.Error -> importResult
        }
    }
    
    /**
     * Merge two lists of custom symbol sets, handling conflicts by ID
     */
    private fun mergeCustomSets(
        current: List<CustomSymbolSet>,
        imported: List<CustomSymbolSet>
    ): List<CustomSymbolSet> {
        val currentMap = current.associateBy { it.id }
        
        val merged = mutableListOf<CustomSymbolSet>()
        
        // Add all current sets
        merged.addAll(current)
        
        // Add imported sets, handling conflicts
        imported.forEach { importedSet ->
            val existing = currentMap[importedSet.id]
            if (existing == null) {
                // New set, add it
                merged.add(importedSet)
            } else {
                // Conflict - create new ID and add with suffix
                val newId = java.util.UUID.randomUUID().toString()
                val newName = "${importedSet.name} (Imported)"
                merged.add(importedSet.copy(id = newId, name = newName))
            }
        }
        
        return merged
    }
}

/**
 * Export data structure for JSON serialization
 */
@kotlinx.serialization.Serializable
data class CustomSymbolSetExport(
    val version: String,
    val exportedAt: Long,
    val customSets: List<CustomSymbolSet>
)

/**
 * Result of import operation
 */
sealed class ImportResult {
    data class Success(val customSets: List<CustomSymbolSet>) : ImportResult()
    data class Error(val message: String) : ImportResult()
}

