package com.example.matrixscreen.data.model

import com.example.matrixscreen.data.proto.MatrixSettingsProto
import com.example.matrixscreen.data.proto.MatrixSettingsProto.FlowDirectionProto
import com.example.matrixscreen.data.custom.CustomSymbolSet
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlin.math.min

/**
 * Mappers for converting between Proto and Domain models.
 * 
 * These functions handle the conversion between the protobuf representation
 * and the domain model, including validation and clamping of out-of-range values.
 */

/**
 * Convert from Proto to Domain model with validation and clamping.
 * 
 * @param proto The protobuf representation
 * @return Validated and clamped domain model
 */
fun MatrixSettingsProto.toDomain(): MatrixSettings {
    // Renderer logic derives the random minimum as ~30% of max trail length,
    // so we clamp to the spec range while keeping enough headroom for that spread.
    val clampedMaxTrail = this.maxTrailLength.coerceIn(20, 200)
    // Bright trails can never outgrow the actual column trail.
    val clampedBrightTrail = this.maxBrightTrailLength
        .coerceIn(4, min(40, clampedMaxTrail))

    val resolvedSymbolSetId = this.symbolSetId.takeIf { it.isNotBlank() }
        ?: MatrixSettings.DEFAULT.symbolSetId
    val resolvedThemePresetId = this.themePresetId.takeIf { it.isNotBlank() }

    return MatrixSettings(
        schemaVersion = this.schemaVersion.coerceAtLeast(2),
        
        // Motion settings with clamping (FIXED to match MOTION_SPECS)
        fallSpeed = this.fallSpeed.coerceIn(0.5f, 10.0f),  // Fixed: was 5.0f, now matches spec
        columnCount = this.columnCount.coerceIn(50, 500),   // Fixed: was 200, now matches spec
        lineSpacing = this.lineSpacing.coerceIn(0.5f, 2.0f),
        activePercentage = this.activePercentage.coerceIn(0.1f, 1.0f),
        speedVariance = this.speedVariance.coerceIn(0.0f, 0.5f),  // Fixed: was 0.1f, now matches spec
        allowLandscape = if (this.schemaVersion < 2) true else this.allowLandscape,
        
        // Effects settings with clamping (FIXED to match EFFECTS_SPECS)
        glowIntensity = this.glowIntensity.coerceIn(0.0f, 5.0f),  // Fixed: was 3.0f, now matches spec
        jitterAmount = this.jitterAmount.coerceIn(0.0f, 5.0f),
        flickerAmount = this.flickerAmount.coerceIn(0.0f, 1.0f),
        mutationRate = this.mutationRate.coerceIn(0.0f, 0.5f),   // Fixed: was 0.2f, now matches spec
        
        // Background settings with clamping (FIXED to match BACKGROUND_SPECS)
        grainDensity = this.grainDensity.coerceIn(0, 1000),
        grainOpacity = this.grainOpacity.coerceIn(0.0f, 0.2f),   // Fixed: was 1.0f, now matches spec
        targetFps = this.targetFps.coerceIn(5, 120),
        flowDirection = this.flowDirection.toDomainFlowDirection(),
        
        // Color settings with validation
        backgroundColor = this.backgroundColor.coerceIn(0x00000000L, 0xFFFFFFFFL),
        headColor = this.headColor.coerceIn(0x00000000L, 0xFFFFFFFFL),
        brightTrailColor = this.brightTrailColor.coerceIn(0x00000000L, 0xFFFFFFFFL),
        trailColor = this.trailColor.coerceIn(0x00000000L, 0xFFFFFFFFL),
        dimColor = this.dimColor.coerceIn(0x00000000L, 0xFFFFFFFFL),
        
        // UI theme colors with validation
        uiAccent = this.uiAccent.coerceIn(0x00000000L, 0xFFFFFFFFL),
        uiOverlayBg = this.uiOverlayBg.coerceIn(0x00000000L, 0xFFFFFFFFL),
        uiSelectionBg = this.uiSelectionBg.coerceIn(0x00000000L, 0xFFFFFFFFL),
        
        // Character settings with clamping
        fontSize = this.fontSize.coerceIn(8, 32),
        
        // Symbol set settings
        symbolSetId = resolvedSymbolSetId,
        savedCustomSets = decodeCustomSetsFromJson(this.savedCustomSets),
        activeCustomSetId = this.activeCustomSetId.takeIf { it.isNotBlank() },
        
        // Trail length settings with clamping
        maxTrailLength = clampedMaxTrail,
        maxBrightTrailLength = clampedBrightTrail,
        
        // Theme preset settings
        themePresetId = resolvedThemePresetId,
        
        // Timing settings with clamping (MISSING - CRITICAL FIX)
        columnStartDelay = this.columnStartDelay.coerceIn(0.0f, 0.5f),
        columnRestartDelay = this.columnRestartDelay.coerceIn(0.0f, 0.5f),
        
        // Advanced color system (MISSING - CRITICAL FIX)
        advancedColorsEnabled = this.advancedColorsEnabled,
        linkUiAndRainColors = this.linkUiAndRainColors,
        
        // Developer settings
        alwaysShowHints = this.alwaysShowHints
    )
}

/**
 * Convert from Domain to Proto model.
 * 
 * @param domain The domain model
 * @return Protobuf representation
 */
fun MatrixSettings.toProto(): MatrixSettingsProto {
    return MatrixSettingsProto.newBuilder()
        .setSchemaVersion(this.schemaVersion)
        
        // Motion settings
        .setFallSpeed(this.fallSpeed)
        .setColumnCount(this.columnCount)
        .setLineSpacing(this.lineSpacing)
        .setActivePercentage(this.activePercentage)
        .setSpeedVariance(this.speedVariance)
        .setAllowLandscape(this.allowLandscape)
        
        // Effects settings
        .setGlowIntensity(this.glowIntensity)
        .setJitterAmount(this.jitterAmount)
        .setFlickerAmount(this.flickerAmount)
        .setMutationRate(this.mutationRate)
        
        // Background settings
        .setGrainDensity(this.grainDensity)
        .setGrainOpacity(this.grainOpacity)
        .setTargetFps(this.targetFps)
        .setFlowDirection(this.flowDirection.toProto())
        
        // Color settings
        .setBackgroundColor(this.backgroundColor)
        .setHeadColor(this.headColor)
        .setBrightTrailColor(this.brightTrailColor)
        .setTrailColor(this.trailColor)
        .setDimColor(this.dimColor)
        
        // UI theme colors
        .setUiAccent(this.uiAccent)
        .setUiOverlayBg(this.uiOverlayBg)
        .setUiSelectionBg(this.uiSelectionBg)
        
        // Character settings
        .setFontSize(this.fontSize)
        
        // Symbol set settings
        .setSymbolSetId(this.symbolSetId.ifBlank { MatrixSettings.DEFAULT.symbolSetId })
        .setSavedCustomSets(encodeCustomSetsToJson(this.savedCustomSets))
        .setActiveCustomSetId(this.activeCustomSetId ?: "")
        
        // Trail length settings
        .setMaxTrailLength(this.maxTrailLength)
        .setMaxBrightTrailLength(this.maxBrightTrailLength)
        
        // Theme preset settings
        .setThemePresetId(this.themePresetId ?: "")
        
        // Timing settings (MISSING - CRITICAL FIX)
        .setColumnStartDelay(this.columnStartDelay)
        .setColumnRestartDelay(this.columnRestartDelay)
        
        // Advanced color system (MISSING - CRITICAL FIX)
        .setAdvancedColorsEnabled(this.advancedColorsEnabled)
        .setLinkUiAndRainColors(this.linkUiAndRainColors)
        
        // Developer settings
        .setAlwaysShowHints(this.alwaysShowHints)
        .build()
}

/**
 * Create a default Proto instance with current schema version.
 * 
 * @return Default protobuf instance
 */
fun createDefaultProto(): MatrixSettingsProto {
    return MatrixSettings.DEFAULT.toProto()
}

private fun FlowDirectionProto.toDomainFlowDirection(): FlowDirection {
    return when (this) {
        FlowDirectionProto.FLOW_DIRECTION_BOTTOM_TO_TOP -> FlowDirection.BOTTOM_TO_TOP
        FlowDirectionProto.FLOW_DIRECTION_LEFT_TO_RIGHT -> FlowDirection.LEFT_TO_RIGHT
        FlowDirectionProto.FLOW_DIRECTION_RIGHT_TO_LEFT -> FlowDirection.RIGHT_TO_LEFT
        FlowDirectionProto.UNRECOGNIZED,
        FlowDirectionProto.FLOW_DIRECTION_UNSPECIFIED,
        FlowDirectionProto.FLOW_DIRECTION_TOP_TO_BOTTOM -> FlowDirection.TOP_TO_BOTTOM
    }
}

private fun FlowDirection.toProto(): FlowDirectionProto {
    return when (this) {
        FlowDirection.TOP_TO_BOTTOM -> FlowDirectionProto.FLOW_DIRECTION_TOP_TO_BOTTOM
        FlowDirection.BOTTOM_TO_TOP -> FlowDirectionProto.FLOW_DIRECTION_BOTTOM_TO_TOP
        FlowDirection.LEFT_TO_RIGHT -> FlowDirectionProto.FLOW_DIRECTION_LEFT_TO_RIGHT
        FlowDirection.RIGHT_TO_LEFT -> FlowDirectionProto.FLOW_DIRECTION_RIGHT_TO_LEFT
    }
}

/**
 * Decode custom symbol sets from JSON string.
 * 
 * @param jsonString The JSON string containing custom sets
 * @return List of CustomSymbolSet objects, or empty list if invalid
 */
private fun decodeCustomSetsFromJson(jsonString: String?): List<CustomSymbolSet> {
    if (jsonString.isNullOrBlank()) return emptyList()
    
    return try {
        val json = Json { ignoreUnknownKeys = true }
        val listSerializer = ListSerializer(CustomSymbolSet.serializer())
        json.decodeFromString(listSerializer, jsonString)
    } catch (e: Exception) {
        // Log error and return empty list
        android.util.Log.w("Mappers", "Failed to decode custom sets: ${e.message}")
        emptyList()
    }
}

/**
 * Encode custom symbol sets to JSON string.
 * 
 * @param customSets The list of CustomSymbolSet objects
 * @return JSON string representation, or empty string if list is empty
 */
private fun encodeCustomSetsToJson(customSets: List<CustomSymbolSet>): String {
    if (customSets.isEmpty()) return ""
    
    return try {
        val json = Json { ignoreUnknownKeys = true }
        val listSerializer = ListSerializer(CustomSymbolSet.serializer())
        json.encodeToString(listSerializer, customSets)
    } catch (e: Exception) {
        // Log error and return empty string
        android.util.Log.w("Mappers", "Failed to encode custom sets: ${e.message}")
        ""
    }
}

/**
 * Validate and clamp a single setting value.
 * 
 * @param key The setting key
 * @param value The value to validate
 * @return Clamped value within valid range
 */
fun clampSettingValue(key: String, value: Any): Any {
    return when (key) {
        "fallSpeed" -> (value as Float).coerceIn(0.5f, 10.0f)
        "columnCount" -> (value as Int).coerceIn(50, 500)
        "lineSpacing" -> (value as Float).coerceIn(0.5f, 2.0f)
        "activePercentage" -> (value as Float).coerceIn(0.1f, 1.0f)
        "speedVariance" -> (value as Float).coerceIn(0.0f, 0.5f)
        "allowLandscape" -> value as Boolean
        "glowIntensity" -> (value as Float).coerceIn(0.0f, 5.0f)
        "jitterAmount" -> (value as Float).coerceIn(0.0f, 5.0f)
        "flickerAmount" -> (value as Float).coerceIn(0.0f, 1.0f)
        "mutationRate" -> (value as Float).coerceIn(0.0f, 0.5f)
        "grainDensity" -> (value as Int).coerceIn(0, 1000)
        "grainOpacity" -> (value as Float).coerceIn(0.0f, 0.2f)
        "targetFps" -> (value as Int).coerceIn(5, 120)
        "fontSize" -> (value as Int).coerceIn(8, 32)
        "maxTrailLength" -> (value as Int).coerceIn(20, 200)
        "maxBrightTrailLength" -> (value as Int).coerceIn(4, 40)
        "columnStartDelay" -> (value as Float).coerceIn(0.0f, 0.5f)
        "columnRestartDelay" -> (value as Float).coerceIn(0.0f, 0.5f)
        else -> value
    }
}
