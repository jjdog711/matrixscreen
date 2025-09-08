# Custom Symbol Set Integration

## Overview

This document describes how custom symbol sets are integrated into the Matrix Rain rendering engine, providing dynamic character resolution at runtime.

## Architecture

### Core Components

1. **CustomSymbolSetRepository** - Manages persistence and retrieval of custom symbol sets
2. **SymbolSetRegistryImpl** - Provides dynamic character resolution for both built-in and custom sets
3. **SymbolSet.effectiveCharacters()** - Main method used by the rendering engine for character resolution
4. **MainActivity.kt** - Uses the resolved characters for actual matrix rain rendering

### Data Flow

```
User Creates Custom Set → CustomSymbolSetRepository → MatrixSettings → SymbolSet.effectiveCharacters() → Matrix Rain Rendering
```

## Key Implementation Details

### 1. Dynamic Character Resolution

The main rendering logic in `MainActivity.kt` uses:
```kotlin
val matrixChars = remember(settings.getSymbolSet(), settings.getSavedCustomSets(), settings.getActiveCustomSetId()) {
    settings.getSymbolSet().effectiveCharacters(settings).toCharArray()
}
```

### 2. SymbolSet.effectiveCharacters() Method

This method handles both built-in and custom symbol sets:
```kotlin
fun effectiveCharacters(settings: MatrixSettings): String {
    return when (this) {
        CUSTOM -> {
            val activeId = settings.activeCustomSetId
            val customSet = settings.savedCustomSets.find { it.id == activeId }
            customSet?.characters ?: "01" // Fallback to binary
        }
        else -> characters
    }
}
```

### 3. Registry Integration

The `SymbolSetRegistryImpl` now supports dynamic custom set lookup:
- `getCharacters()` - Returns fallback for CUSTOM sets (synchronous limitation)
- `getEffectiveCharacters()` - Full dynamic resolution with custom set data
- `getEffectiveCharactersFlow()` - Reactive Flow-based resolution

### 4. Custom Font Support

The rendering engine already supports custom fonts for custom symbol sets:
```kotlin
val charTypeface = if (settings.getSymbolSet() == SymbolSet.CUSTOM && 
    settings.getActiveCustomSetId() != null) {
    val customSet = settings.getSavedCustomSets().find { it.id == settings.getActiveCustomSetId()?.toString() }
    customSet?.let { 
        paintCache.fontManager.getTypefaceForCustomSet(it.fontFileName)
    } ?: paintCache.fontManager.getTypefaceForCharacter(glyph.char)
} else {
    paintCache.fontManager.getTypefaceForCharacter(glyph.char)
}
```

## Features

### ✅ Fully Functional
- **Dynamic Character Resolution**: Custom sets are resolved at runtime
- **Live Preview**: Changes apply immediately without restart
- **Custom Fonts**: Each custom set can use its own font
- **Persistence**: Custom sets are saved and restored automatically
- **Fallback Handling**: Graceful degradation when custom sets are missing

### ✅ Performance Optimized
- **Reactive Updates**: Only recomposes when custom set data changes
- **Memory Efficient**: Characters are cached and reused
- **Flow-based**: Non-blocking data access

### ✅ Developer Friendly
- **Logging**: Debug information for custom set resolution
- **Type Safety**: Full compile-time type safety
- **Clean Architecture**: Separation of concerns

## Usage

### Creating a Custom Symbol Set
1. User navigates to Characters settings
2. Creates a new custom symbol set with desired characters and font
3. Sets it as active
4. Matrix rain immediately reflects the new characters

### Technical Integration
The system automatically:
1. Saves the custom set to DataStore via CustomSymbolSetRepository
2. Updates MatrixSettings with the new activeCustomSetId
3. Triggers recomposition in MainActivity
4. Resolves characters via SymbolSet.effectiveCharacters()
5. Renders the new characters in the matrix rain

## Testing

The integration can be tested by:
1. Creating a custom symbol set with unique characters (e.g., "ABC123")
2. Setting it as active
3. Observing that the matrix rain displays only those characters
4. Switching to a different custom set and verifying the change
5. Switching back to built-in sets and verifying normal operation

## Future Enhancements

- **Multi-set Support**: Support for multiple active custom sets
- **Character Weighting**: More sophisticated character frequency control
- **Performance Caching**: In-memory caching of resolved character sets
- **Validation**: Character set validation and sanitization
