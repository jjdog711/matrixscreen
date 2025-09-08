package com.example.matrixscreen.data


/**
 * Available symbol sets for the Matrix effect
 */
enum class SymbolSet(val displayName: String, val characters: String) {
    LATIN(
        displayName = "Latin",
        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    ),
    KATAKANA(
        displayName = "Katakana", 
        characters = "アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ"
    ),
    MATRIX_AUTHENTIC(
        displayName = "Matrix Authentic",
        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヲン・ーｧｨｩｪｫｬｭｮｯｰﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝ"
    ),
    MATRIX_GLITCH(
        displayName = "Matrix Glitch",
        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789ァアィイゥウェエォオカガキギクグケゲコゴサザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヲン・ーｧｨｩｪｫｬｭｮｯｰﾀﾁﾂﾃﾄﾅﾆﾇﾈﾉﾊﾋﾌﾍﾎﾏﾐﾑﾒﾓﾔﾕﾖﾗﾘﾙﾚﾛﾜﾝx̶R̸S̷T̸U̶V̷W̸X̶Y̷Z̸◊◈◆◇▲△▼▽●○■□▲△▼▽"
    ),
    NUMBERS(
        displayName = "Numbers",
        characters = "0123456789"
    ),
    MIXED(
        displayName = "Mixed",
        characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンガギグゲゴザジズゼゾダヂヅデドバビブベボパピプペポ"
    ),
    BINARY(
        displayName = "Binary",
        characters = "01"
    ),
    HEX(
        displayName = "Hexadecimal",
        characters = "0123456789ABCDEF"
    ),
    CUSTOM(
        displayName = "Custom",
        characters = "" // Will be determined by activeCustomSetId
    );
    
    // Legacy method removed - use effectiveCharacters(MatrixSettings) or effectiveCharacters(SymbolEngineConfig) instead
    
    /**
     * Get effective characters for domain MatrixSettings using registry system
     */
    fun effectiveCharacters(settings: com.example.matrixscreen.data.model.MatrixSettings): String {
        return when (this) {
            CUSTOM -> {
                val activeId = settings.activeCustomSetId
                val customSet = settings.savedCustomSets.find { it.id == activeId }
                val result = customSet?.characters ?: "01"
                
                // Log for debugging custom symbol set resolution
                if (activeId != null && customSet == null) {
                    android.util.Log.w("SymbolSet", 
                        "Custom symbol set with ID '$activeId' not found in saved sets. Available IDs: ${settings.savedCustomSets.map { it.id }}")
                } else if (activeId != null && customSet != null) {
                    android.util.Log.d("SymbolSet", 
                        "Using custom symbol set '${customSet.name}' with ${customSet.characters.length} characters")
                } else {
                    android.util.Log.d("SymbolSet", 
                        "No active custom set ID, using binary fallback")
                }
                
                result
            }
            else -> characters
        }
    }
    
    /**
     * Get effective characters using domain-level SymbolEngineConfig.
     * 
     * This overload allows symbol set resolution without requiring the full
     * MatrixSettings object, making it more efficient and decoupled.
     */
    fun effectiveCharacters(config: com.example.matrixscreen.data.registry.SymbolEngineConfig): String {
        return when (this) {
            CUSTOM -> {
                config.savedCustomSets.find { it.id == config.activeCustomSetId }?.characters
                    ?: "01" // Fallback to binary if no active custom set
            }
            else -> characters
        }
    }
}
