package com.example.matrixscreen.core.util

import android.content.Context
import android.view.Display
import android.view.WindowManager
import kotlin.math.abs

/**
 * Utility for handling device FPS coercion and validation.
 * 
 * This utility provides functions to:
 * - Detect device-supported refresh rates
 * - Coerce user-selected FPS to device-supported values
 * - Validate FPS ranges for different device capabilities
 */
object FpsCoercionUtil {
    
    /**
     * Get the device's supported refresh rates.
     * 
     * @param context Android context to access display information
     * @return List of supported refresh rates in Hz
     */
    fun getSupportedRefreshRates(context: Context): List<Int> {
        return try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            
            // Get supported refresh rates
            val supportedModes = display.supportedModes
            val refreshRates = mutableSetOf<Int>()
            
            supportedModes.forEach { mode ->
                refreshRates.add(mode.refreshRate.toInt())
            }
            
            // Sort and return as list, ensuring we have common rates
            val sortedRates = refreshRates.sorted()
            
            // Ensure we have at least 60Hz as fallback
            if (sortedRates.isEmpty() || !sortedRates.contains(60)) {
                listOf(60)
            } else {
                sortedRates
            }
        } catch (e: Exception) {
            // Fallback to common refresh rates if detection fails
            listOf(60, 90, 120)
        }
    }
    
    /**
     * Coerce a target FPS to the nearest device-supported rate.
     * 
     * @param targetFps The desired FPS value
     * @param supportedRates List of device-supported refresh rates
     * @param minFps Minimum allowed FPS (default: 15)
     * @param maxFps Maximum allowed FPS (default: 120)
     * @return The coerced FPS value
     */
    fun coerceFps(
        targetFps: Int,
        supportedRates: List<Int>,
        minFps: Int = 15,
        maxFps: Int = 120
    ): Int {
        // First clamp to valid range
        val clampedFps = targetFps.coerceIn(minFps, maxFps)
        
        // Find the nearest supported rate
        return supportedRates.minByOrNull { abs(it - clampedFps) } ?: 60
    }
    
    /**
     * Get the effective FPS for a given target, considering device capabilities.
     * 
     * @param targetFps The desired FPS value
     * @param context Android context for device detection
     * @return The effective FPS that will actually be used
     */
    fun getEffectiveFps(targetFps: Int, context: Context): Int {
        val supportedRates = getSupportedRefreshRates(context)
        return coerceFps(targetFps, supportedRates)
    }
    
    /**
     * Check if a given FPS is supported by the device.
     * 
     * @param fps The FPS to check
     * @param supportedRates List of device-supported refresh rates
     * @return true if the FPS is supported, false otherwise
     */
    fun isFpsSupported(fps: Int, supportedRates: List<Int>): Boolean {
        return supportedRates.contains(fps)
    }
    
    /**
     * Get a list of recommended FPS options based on device capabilities.
     * 
     * @param context Android context for device detection
     * @return List of recommended FPS values
     */
    fun getRecommendedFpsOptions(context: Context): List<Int> {
        val supportedRates = getSupportedRefreshRates(context)
        val commonRates = listOf(30, 60, 90, 120)
        
        // Return common rates that are supported by the device
        return commonRates.filter { isFpsSupported(it, supportedRates) }
    }
    
    /**
     * Get the maximum supported FPS for the device.
     * 
     * @param context Android context for device detection
     * @return The maximum supported FPS
     */
    fun getMaxSupportedFps(context: Context): Int {
        val supportedRates = getSupportedRefreshRates(context)
        return supportedRates.maxOrNull() ?: 60
    }
    
    /**
     * Get the minimum supported FPS for the device.
     * 
     * @param context Android context for device detection
     * @return The minimum supported FPS
     */
    fun getMinSupportedFps(context: Context): Int {
        val supportedRates = getSupportedRefreshRates(context)
        return supportedRates.minOrNull() ?: 30
    }
}
