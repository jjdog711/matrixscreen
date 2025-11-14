package com.example.matrixscreen.data.store

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.example.matrixscreen.data.proto.OverlayStateProto
import java.io.InputStream
import java.io.OutputStream

object OverlayStateSerializer : Serializer<OverlayStateProto> {

    override val defaultValue: OverlayStateProto = OverlayStateProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): OverlayStateProto {
        return try {
            OverlayStateProto.parseFrom(input)
        } catch (exception: Exception) {
            throw CorruptionException("Unable to read OverlayStateProto", exception)
        }
    }

    override suspend fun writeTo(t: OverlayStateProto, output: OutputStream) {
        t.writeTo(output)
    }
}

