package com.example.matrixscreen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matrixscreen.core.IoDispatcher
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.repo.SettingsRepository
import com.example.matrixscreen.ui.settings.model.SettingId
import com.example.matrixscreen.ui.settings.model.with
import com.example.matrixscreen.data.custom.CustomSymbolSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for settings management following UDF pattern.
 * 
 * Contains saved settings (persisted), draft settings (temporary edits),
 * and dirty flag indicating if draft differs from saved.
 */
data class SettingsUiState(
    val saved: MatrixSettings,
    val draft: MatrixSettings,
    val dirty: Boolean
)

/**
 * ViewModel for managing MatrixScreen settings with UDF pattern.
 * 
 * Implements draft/confirm/cancel pattern where UI edits update a draft
 * immediately, Confirm persists the draft, and Cancel reverts to saved state.
 * No composables directly touch storage - all persistence goes through this ViewModel.
 */
@HiltViewModel
class NewSettingsViewModel @Inject constructor(
    private val repository: SettingsRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(
        SettingsUiState(
            saved = MatrixSettings.DEFAULT,
            draft = MatrixSettings.DEFAULT,
            dirty = false
        )
    )
    
    /**
     * Current UI state exposed to composables.
     */
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    /**
     * Preview override for custom symbol sets - transient field not in saved state.
     * Used to temporarily override the active symbol set for preview purposes.
     */
    private val _previewOverrideSymbolSet = MutableStateFlow<CustomSymbolSet?>(null)
    val previewOverrideSymbolSet: StateFlow<CustomSymbolSet?> = _previewOverrideSymbolSet.asStateFlow()
    
    init {
        // Load initial settings from repository
        viewModelScope.launch(dispatcher) {
            repository.observe().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    saved = settings,
                    draft = settings,
                    dirty = false
                )
            }
        }
    }
    
    /**
     * Update a draft setting value using SettingId.
     * 
     * This method updates the draft immediately but does not persist.
     * Use commit() to persist changes or revert() to discard them.
     * 
     * @param id The SettingId for the setting to update
     * @param value The new value (must match the SettingId type)
     */
    fun <T> updateDraft(id: SettingId<T>, value: T) {
        val currentState = _uiState.value
        val updatedDraft = currentState.draft.with(id, value)
        val isDirty = updatedDraft != currentState.saved
        
        _uiState.value = currentState.copy(
            draft = updatedDraft,
            dirty = isDirty
        )
    }
    
    /**
     * Commit the current draft settings to persistent storage.
     * 
     * This persists the draft settings and updates the saved state.
     * After commit, dirty flag is reset to false.
     */
    fun commit() {
        val currentState = _uiState.value
        if (currentState.dirty) {
            viewModelScope.launch(dispatcher) {
                repository.save(currentState.draft)
                // The saved state will be updated via the repository.observe() flow
            }
        }
    }
    
    /**
     * Revert the draft settings to match the saved settings.
     * 
     * This discards any unsaved changes and resets the dirty flag to false.
     */
    fun revert() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            draft = currentState.saved,
            dirty = false
        )
    }
    
    /**
     * Reset all settings to default values.
     * 
     * This immediately updates both saved and draft to defaults.
     */
    fun resetToDefaults() {
        viewModelScope.launch(dispatcher) {
            repository.resetToDefaults()
            // The state will be updated via the repository.observe() flow
        }
    }
    
    /**
     * Get the current saved settings.
     * 
     * @return The currently saved settings
     */
    fun getCurrentSettings(): MatrixSettings {
        return _uiState.value.saved
    }
    
    /**
     * Get the current draft settings.
     * 
     * @return The current draft settings
     */
    fun getDraftSettings(): MatrixSettings {
        return _uiState.value.draft
    }
    
    /**
     * Check if there are unsaved changes.
     * 
     * @return true if draft differs from saved settings
     */
    fun hasUnsavedChanges(): Boolean {
        return _uiState.value.dirty
    }
    
    /**
     * Apply a preview override for custom symbol sets.
     * This temporarily overrides the active symbol set for preview purposes.
     * 
     * @param customSet The custom symbol set to preview, or null to clear preview
     */
    fun applyPreviewOverride(customSet: CustomSymbolSet?) {
        _previewOverrideSymbolSet.value = customSet
    }
    
    /**
     * Clear the current preview override.
     * This restores the normal symbol set behavior.
     */
    fun clearPreviewOverride() {
        _previewOverrideSymbolSet.value = null
    }
}
