package com.example.matrixscreen.core.util

import androidx.compose.ui.graphics.Color
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(JUnit4::class)
class ContrastUtilsTest {

    @Test
    fun `calculateLuminance returns correct values for basic colors`() {
        // Test black
        val blackLuminance = Color.Black.calculateLuminance()
        assertEquals(0.0, blackLuminance, 0.001)
        
        // Test white
        val whiteLuminance = Color.White.calculateLuminance()
        assertEquals(1.0, whiteLuminance, 0.001)
        
        // Test gray
        val grayLuminance = Color.Gray.calculateLuminance()
        assertTrue(grayLuminance > 0.0)
        assertTrue(grayLuminance < 1.0)
    }

    @Test
    fun `calculateContrastRatio returns correct values for high contrast pairs`() {
        // Black on white should have maximum contrast
        val blackOnWhite = calculateContrastRatio(Color.Black, Color.White)
        assertEquals(21.0, blackOnWhite, 0.1)
        
        // White on black should have maximum contrast
        val whiteOnBlack = calculateContrastRatio(Color.White, Color.Black)
        assertEquals(21.0, whiteOnBlack, 0.1)
        
        // Same color should have minimum contrast
        val sameColor = calculateContrastRatio(Color.Red, Color.Red)
        assertEquals(1.0, sameColor, 0.001)
    }

    @Test
    fun `meetsWCAGAA returns correct results for standard color pairs`() {
        // Black on white should meet AA standards
        assertTrue(meetsWCAGAA(Color.Black, Color.White))
        assertTrue(meetsWCAGAA(Color.Black, Color.White, isLargeText = true))
        
        // White on black should meet AA standards
        assertTrue(meetsWCAGAA(Color.White, Color.Black))
        assertTrue(meetsWCAGAA(Color.White, Color.Black, isLargeText = true))
        
        // Similar colors should not meet AA standards
        assertFalse(meetsWCAGAA(Color.Red, Color.DarkRed))
        assertFalse(meetsWCAGAA(Color.Blue, Color.DarkBlue))
    }

    @Test
    fun `meetsWCAGAAA returns correct results for high contrast pairs`() {
        // Black on white should meet AAA standards
        assertTrue(meetsWCAGAAA(Color.Black, Color.White))
        assertTrue(meetsWCAGAAA(Color.Black, Color.White, isLargeText = true))
        
        // White on black should meet AAA standards
        assertTrue(meetsWCAGAAA(Color.White, Color.Black))
        assertTrue(meetsWCAGAAA(Color.White, Color.Black, isLargeText = true))
        
        // Medium contrast colors should not meet AAA standards
        assertFalse(meetsWCAGAAA(Color.Gray, Color.White))
        assertFalse(meetsWCAGAAA(Color.Gray, Color.Black))
    }

    @Test
    fun `getContrastingTextColor returns correct colors`() {
        // Light background should return black text
        assertEquals(Color.Black, getContrastingTextColor(Color.White))
        assertEquals(Color.Black, getContrastingTextColor(Color.LightGray))
        assertEquals(Color.Black, getContrastingTextColor(Color.Yellow))
        
        // Dark background should return white text
        assertEquals(Color.White, getContrastingTextColor(Color.Black))
        assertEquals(Color.White, getContrastingTextColor(Color.DarkGray))
        assertEquals(Color.White, getContrastingTextColor(Color.DarkBlue))
    }

    @Test
    fun `getAccessibleTextColor returns suitable colors`() {
        // White background should return black text
        val whiteBgText = getAccessibleTextColor(Color.White)
        assertEquals(Color.Black, whiteBgText)
        
        // Black background should return white text
        val blackBgText = getAccessibleTextColor(Color.Black)
        assertEquals(Color.White, blackBgText)
        
        // Gray background should return appropriate text color
        val grayBgText = getAccessibleTextColor(Color.Gray)
        assertNotNull(grayBgText)
        assertTrue(grayBgText == Color.Black || grayBgText == Color.White)
    }

    @Test
    fun `adjustColorForContrast improves contrast when needed`() {
        val backgroundColor = Color.White
        val lowContrastColor = Color.LightGray
        
        val adjustedColor = adjustColorForContrast(lowContrastColor, backgroundColor, 4.5)
        
        // Adjusted color should have better contrast
        val originalRatio = calculateContrastRatio(lowContrastColor, backgroundColor)
        val adjustedRatio = calculateContrastRatio(adjustedColor, backgroundColor)
        
        assertTrue(adjustedRatio >= originalRatio)
    }

    @Test
    fun `adjustColorForContrast returns original color when contrast is sufficient`() {
        val backgroundColor = Color.White
        val highContrastColor = Color.Black
        
        val adjustedColor = adjustColorForContrast(highContrastColor, backgroundColor, 4.5)
        
        // Should return original color since contrast is already sufficient
        assertEquals(highContrastColor, adjustedColor)
    }

    @Test
    fun `getSafeUIColor returns preferred color when contrast is good`() {
        val backgroundColor = Color.White
        val preferredColor = Color.Black
        val fallbackColor = Color.Red
        
        val safeColor = getSafeUIColor(preferredColor, backgroundColor, fallbackColor)
        
        // Should return preferred color since it has good contrast
        assertEquals(preferredColor, safeColor)
    }

    @Test
    fun `getSafeUIColor returns fallback color when contrast is poor`() {
        val backgroundColor = Color.White
        val preferredColor = Color.LightGray // Poor contrast
        val fallbackColor = Color.Black
        
        val safeColor = getSafeUIColor(preferredColor, backgroundColor, fallbackColor)
        
        // Should return fallback color since preferred color has poor contrast
        assertEquals(fallbackColor, safeColor)
    }

    @Test
    fun `isColorCombinationSafe returns correct results`() {
        // High contrast combinations should be safe
        assertTrue(isColorCombinationSafe(Color.Black, Color.White, 3.0))
        assertTrue(isColorCombinationSafe(Color.White, Color.Black, 3.0))
        
        // Low contrast combinations should not be safe
        assertFalse(isColorCombinationSafe(Color.LightGray, Color.White, 3.0))
        assertFalse(isColorCombinationSafe(Color.DarkGray, Color.Black, 3.0))
    }

    @Test
    fun `getContrastLevel returns appropriate descriptions`() {
        // High contrast should be "Excellent"
        val excellentLevel = getContrastLevel(Color.Black, Color.White)
        assertTrue(excellentLevel.contains("Excellent"))
        
        // Medium contrast should be "Good"
        val goodLevel = getContrastLevel(Color.DarkBlue, Color.White)
        assertTrue(goodLevel.contains("Good") || goodLevel.contains("Excellent"))
        
        // Low contrast should be "Poor"
        val poorLevel = getContrastLevel(Color.LightGray, Color.White)
        assertTrue(poorLevel.contains("Poor") || poorLevel.contains("Very Poor"))
    }

    @Test
    fun `luminance calculation is consistent with Compose Color luminance`() {
        val testColors = listOf(
            Color.Red,
            Color.Green,
            Color.Blue,
            Color.Yellow,
            Color.Cyan,
            Color.Magenta,
            Color.Gray
        )
        
        testColors.forEach { color ->
            val ourLuminance = color.calculateLuminance()
            val composeLuminance = color.luminance()
            
            // Our calculation should be close to Compose's calculation
            assertEquals(composeLuminance, ourLuminance, 0.01)
        }
    }
}
