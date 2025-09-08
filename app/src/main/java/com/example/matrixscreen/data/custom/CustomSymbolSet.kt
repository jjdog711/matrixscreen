package com.example.matrixscreen.data.custom

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Custom symbol set created by the user
 * 
 * This data class represents a user-created symbol set with custom characters.
 * It's stored in the domain model and persisted as JSON in DataStore.
 */
@Serializable
data class CustomSymbolSet(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val characters: String,
    val fontFileName: String = "matrix_code_nfi.ttf"
)
