package com.example.matrixscreen.ui.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.matrixscreen.data.proto.OverlayStateProto
import com.example.matrixscreen.data.store.OverlayStateSerializer
import kotlinx.coroutines.flow.Flow

private const val OVERLAY_STATE_FILE_NAME = "overlay_state.pb"

val Context.overlayStateDataStore: DataStore<OverlayStateProto> by dataStore(
    fileName = OVERLAY_STATE_FILE_NAME,
    serializer = OverlayStateSerializer
)

object OverlayStateStore {
    fun state(context: Context): Flow<OverlayStateProto> = context.overlayStateDataStore.data

    suspend fun update(
        context: Context,
        transform: (OverlayStateProto) -> OverlayStateProto
    ) {
        context.overlayStateDataStore.updateData(transform)
    }
}

