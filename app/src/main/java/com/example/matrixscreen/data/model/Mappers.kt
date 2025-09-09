package com.example.matrixscreen.data.model

import com.example.matrixscreen.data.proto.MatrixSettingsProto

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
    return MatrixSettings(
        schemaVersion = this.schemaVersion.coerceAtLeast(1),
        
        // Motion settings with clamping (FIXED to match MOTION_SPECS)
        fallSpeed = this.fallSpeed.coerceIn(0.5f, 10.0f),  // Fixed: was 5.0f, now matches spec
        columnCount = this.columnCount.coerceIn(50, 500),   // Fixed: was 200, now matches spec
        lineSpacing = this.lineSpacing.coerceIn(0.5f, 2.0f),
        activePercentage = this.activePercentage.coerceIn(0.1f, 1.0f),
        speedVariance = this.speedVariance.coerceIn(0.0f, 0.5f),  // Fixed: was 0.1f, now matches spec
        
        // Effects settings with clamping (FIXED to match EFFECTS_SPECS)
        glowIntensity = this.glowIntensity.coerceIn(0.0f, 5.0f),  // Fixed: was 3.0f, now matches spec
        jitterAmount = this.jitterAmount.coerceIn(0.0f, 5.0f),
        flickerAmount = this.flickerAmount.coerceIn(0.0f, 1.0f),
        mutationRate = this.mutationRate.coerceIn(0.0f, 0.5f),   // Fixed: was 0.2f, now matches spec
        
        // Background settings with clamping (FIXED to match BACKGROUND_SPECS)
        grainDensity = this.grainDensity.coerceIn(0, 1000),
        grainOpacity = this.grainOpacity.coerceIn(0.0f, 0.2f),   // Fixed: was 1.0f, now matches spec
        targetFps = this.targetFps.coerceIn(5, 120),
        
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
        
        // Timing settings with clamping (MISSING - CRITICAL FIX)
        columnStartDelay = this.columnStartDelay.coerceIn(0.0f, 0.5f),
        columnRestartDelay = this.columnRestartDelay.coerceIn(0.0f, 0.5f),
        
        // Advanced color system (MISSING - CRITICAL FIX)
        advancedColorsEnabled = this.advancedColorsEnabled,
        linkUiAndRainColors = this.linkUiAndRainColors
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
        
        // Effects settings
        .setGlowIntensity(this.glowIntensity)
        .setJitterAmount(this.jitterAmount)
        .setFlickerAmount(this.flickerAmount)
        .setMutationRate(this.mutationRate)
        
        // Background settings
        .setGrainDensity(this.grainDensity)
        .setGrainOpacity(this.grainOpacity)
        .setTargetFps(this.targetFps)
        
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
        
        // Timing settings (MISSING - CRITICAL FIX)
        .setColumnStartDelay(this.columnStartDelay)
        .setColumnRestartDelay(this.columnRestartDelay)
        
        // Advanced color system (MISSING - CRITICAL FIX)
        .setAdvancedColorsEnabled(this.advancedColorsEnabled)
        .setLinkUiAndRainColors(this.linkUiAndRainColors)
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

/**
 * Validate and clamp a single setting value.
 * 
 * @param key The setting key
 * @param value The value to validate
 * @return Clamped value within valid range
 */
fun clampSettingValue(key: String, value: Any): Any {
    return when (key) {
        "fallSpeed" -> (value as Float).coerceIn(0.5f, 5.0f)
        "columnCount" -> (value as Int).coerceIn(50, 200)
        "lineSpacing" -> (value as Float).coerceIn(0.5f, 2.0f)
        "activePercentage" -> (value as Float).coerceIn(0.1f, 1.0f)
        "speedVariance" -> (value as Float).coerceIn(0.0f, 0.1f)
        "glowIntensity" -> (value as Float).coerceIn(0.0f, 3.0f)
        "jitterAmount" -> (value as Float).coerceIn(0.0f, 5.0f)
        "flickerAmount" -> (value as Float).coerceIn(0.0f, 1.0f)
        "mutationRate" -> (value as Float).coerceIn(0.0f, 0.2f)
        "grainDensity" -> (value as Int).coerceIn(0, 1000)
        "grainOpacity" -> (value as Float).coerceIn(0.0f, 1.0f)
        "targetFps" -> (value as Int).coerceIn(5, 120)
        "fontSize" -> (value as Int).coerceIn(8, 32)
        else -> value
    }
}
