package com.example.matrixscreen.ui.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.data.repo.SettingsRepository
import com.example.matrixscreen.ui.NewSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

private class PreviewSettingsRepository(
    initialSettings: MatrixSettings
) : SettingsRepository {
    private val state = MutableStateFlow(initialSettings)

    override fun observe(): Flow<MatrixSettings> = state

    override suspend fun save(settings: MatrixSettings) {
        state.value = settings
    }

    override suspend fun getCurrent(): MatrixSettings = state.value

    override suspend fun resetToDefaults() {
        state.value = MatrixSettings.DEFAULT
    }
}

@Composable
fun rememberPreviewSettingsViewModel(
    initialSettings: MatrixSettings = MatrixSettings.DEFAULT
): NewSettingsViewModel {
    return remember(initialSettings) {
        NewSettingsViewModel(
            repository = PreviewSettingsRepository(initialSettings),
            dispatcher = Dispatchers.Main.immediate
        )
    }
}
