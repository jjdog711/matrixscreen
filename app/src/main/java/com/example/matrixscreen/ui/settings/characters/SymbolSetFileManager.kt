package com.example.matrixscreen.ui.settings.characters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * File manager for handling import/export of custom symbol sets using SAF
 */
class SymbolSetFileManager(val context: Context) {
    
    /**
     * Create a temporary file for export
     */
    fun createExportFile(jsonData: String): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "matrix_symbols_$timestamp.ms-symbols.json"
        val file = File(context.cacheDir, fileName)
        
        file.writeText(jsonData)
        return file
    }
    
    /**
     * Get URI for file sharing
     */
    fun getFileUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    
    /**
     * Read JSON data from URI
     */
    suspend fun readJsonFromUri(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.bufferedReader().readText()
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    /**
     * Clean up temporary files
     */
    fun cleanupTempFiles() {
        try {
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("matrix_symbols_") && file.name.endsWith(".ms-symbols.json")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }
}

/**
 * Composable for handling file export
 */
@Composable
fun rememberExportLauncher(
    onExportReady: (Uri) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result: androidx.activity.result.ActivityResult ->
    if (result.resultCode == Activity.RESULT_OK) {
        result.data?.data?.let { uri: Uri ->
            onExportReady(uri)
        }
    }
}

/**
 * Composable for handling file import
 */
@Composable
fun rememberImportLauncher(
    onImportReady: (Uri) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri: Uri? ->
    uri?.let { onImportReady(it) }
}

/**
 * Composable for handling file sharing
 */
@Composable
fun rememberShareLauncher(
    onShareReady: (Uri) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result: androidx.activity.result.ActivityResult ->
    if (result.resultCode == Activity.RESULT_OK) {
        result.data?.data?.let { uri: Uri ->
            onShareReady(uri)
        }
    }
}

/**
 * Composable for export functionality
 */
@Composable
fun rememberExportHandler(
    fileManager: SymbolSetFileManager,
    jsonData: String,
    onExportComplete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val exportLauncher = rememberExportLauncher { uri ->
        coroutineScope.launch {
            try {
                // Copy file to selected location
                fileManager.context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    jsonData.byteInputStream().use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                onExportComplete()
            } catch (e: Exception) {
                // Handle export error
            }
        }
    }
    
    // Function to trigger export
    fun exportToFile() {
        coroutineScope.launch {
            try {
                val tempFile = fileManager.createExportFile(jsonData)
                val fileUri = fileManager.getFileUri(tempFile)
                
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                    putExtra(Intent.EXTRA_TITLE, tempFile.name)
                }
                
                exportLauncher.launch(intent)
            } catch (e: Exception) {
                // Handle export error
            }
        }
    }
}

/**
 * Composable for import functionality
 */
@Composable
fun rememberImportHandler(
    fileManager: SymbolSetFileManager,
    onImportComplete: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val importLauncher = rememberImportLauncher { uri ->
        coroutineScope.launch {
            try {
                val jsonData = fileManager.readJsonFromUri(uri)
                if (jsonData != null) {
                    onImportComplete(jsonData)
                }
            } catch (e: Exception) {
                // Handle import error
            }
        }
    }
    
    // Function to trigger import
    fun importFromFile() {
        importLauncher.launch("application/json")
    }
}
