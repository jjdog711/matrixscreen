package com.example.matrixscreen.ui.settings.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matrixscreen.core.IoDispatcher
import com.example.matrixscreen.data.custom.CustomSymbolSet
import com.example.matrixscreen.data.repo.CustomSymbolSetRepository
import com.example.matrixscreen.data.repo.ImportResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for custom symbol set management
 */
data class CustomSymbolSetUiState(
    val customSets: List<CustomSymbolSet> = emptyList(),
    val activeCustomSetId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val importExportState: ImportExportState = ImportExportState.Idle
)

/**
 * State for import/export operations
 */
sealed class ImportExportState {
    object Idle : ImportExportState()
    object Exporting : ImportExportState()
    object Importing : ImportExportState()
    data class ExportSuccess(val jsonData: String) : ImportExportState()
    data class ImportSuccess(val importedCount: Int) : ImportExportState()
    data class Error(val message: String) : ImportExportState()
}

/**
 * ViewModel for managing custom symbol sets with import/export functionality
 */
@HiltViewModel
class CustomSymbolSetViewModel @Inject constructor(
    private val repository: CustomSymbolSetRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CustomSymbolSetUiState())
    val uiState: StateFlow<CustomSymbolSetUiState> = _uiState.asStateFlow()
    
    // Store the ID of the set being edited
    private val _editingSetId = MutableStateFlow<String?>(null)
    val editingSetId: StateFlow<String?> = _editingSetId.asStateFlow()
    
    init {
        // Combine custom sets and active ID streams
        viewModelScope.launch(dispatcher) {
            combine(
                repository.savedSets,
                repository.activeCustomSetId
            ) { customSets, activeId ->
                _uiState.value.copy(
                    customSets = customSets,
                    activeCustomSetId = activeId,
                    isLoading = false,
                    error = null
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
    
    /**
     * Save a custom symbol set
     */
    fun saveCustomSet(customSet: CustomSymbolSet) {
        viewModelScope.launch(dispatcher) {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                repository.upsertCustomSet(customSet)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save custom set: ${e.message}"
                )
            }
        }
    }

    /**
     * Duplicate a custom symbol set
     */
    fun duplicateCustomSet(setId: String) {
        viewModelScope.launch(dispatcher) {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                // Get the current sets and find the one to duplicate
                val currentSets = _uiState.value.customSets
                val originalSet = currentSets.find { customSet -> customSet.id == setId }
                if (originalSet != null) {
                    val duplicatedSet = originalSet.copy(
                        id = java.util.UUID.randomUUID().toString(),
                        name = "${originalSet.name} (copy)"
                    )
                    repository.upsertCustomSet(duplicatedSet)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to duplicate custom set: ${e.message}"
                )
            }
        }
    }

    /**
     * Delete a custom symbol set
     */
    fun deleteCustomSet(setId: String) {
        viewModelScope.launch(dispatcher) {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                repository.deleteCustomSet(setId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to delete custom set: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Set the active custom set
     */
    fun setActiveCustomSet(setId: String?) {
        viewModelScope.launch(dispatcher) {
            try {
                repository.setActiveCustomSetId(setId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to set active custom set: ${e.message}"
                )
            }
        }
    }
    
    /**
     * Export custom symbol sets to JSON
     */
    fun exportCustomSets() {
        viewModelScope.launch(dispatcher) {
            try {
                _uiState.value = _uiState.value.copy(importExportState = ImportExportState.Exporting)
                
                val currentState = _uiState.value
                val jsonData = repository.exportToJson(currentState.customSets)
                
                _uiState.value = currentState.copy(
                    importExportState = ImportExportState.ExportSuccess(jsonData)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    importExportState = ImportExportState.Error("Export failed: ${e.message}")
                )
            }
        }
    }
    
    /**
     * Import custom symbol sets from JSON
     */
    fun importCustomSets(jsonData: String) {
        viewModelScope.launch(dispatcher) {
            try {
                _uiState.value = _uiState.value.copy(importExportState = ImportExportState.Importing)
                
                val result = repository.importAndMerge(jsonData)
                
                when (result) {
                    is ImportResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            importExportState = ImportExportState.ImportSuccess(result.customSets.size)
                        )
                    }
                    is ImportResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            importExportState = ImportExportState.Error(result.message)
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    importExportState = ImportExportState.Error("Import failed: ${e.message}")
                )
            }
        }
    }
    
    /**
     * Clear import/export state
     */
    fun clearImportExportState() {
        _uiState.value = _uiState.value.copy(importExportState = ImportExportState.Idle)
    }
    
    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    /**
     * Get a custom set by ID
     */
    fun getCustomSetById(setId: String): CustomSymbolSet? {
        return _uiState.value.customSets.find { it.id == setId }
    }
    
    /**
     * Set the ID of the custom set being edited
     */
    fun setEditingSetId(setId: String?) {
        _editingSetId.value = setId
    }
    
    /**
     * Get the custom set being edited
     */
    fun getEditingSet(): CustomSymbolSet? {
        val setId = _editingSetId.value
        return if (setId != null) {
            getCustomSetById(setId)
        } else {
            null
        }
    }
    
    /**
     * Check if a name is unique among custom symbol sets
     */
    suspend fun isNameUnique(name: String, excludeId: String? = null): Boolean {
        return repository.isNameUnique(name, excludeId)
    }
}
