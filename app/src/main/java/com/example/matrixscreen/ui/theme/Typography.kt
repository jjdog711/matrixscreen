package com.example.matrixscreen.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Modern typography system for MatrixScreen UI
 * Follows the design spec with Space Grotesk for UI elements and JetBrains Mono for technical content
 */

// Font families - using actual custom fonts from Type.kt
val SpaceGroteskFamily = SpaceGrotesk
val JetBrainsMonoFamily = JetBrainsMono

/**
 * Centralized typography definitions following the design spec
 */
val MatrixTypography = Typography(
    // Headers and titles - Space Grotesk
    headlineLarge = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    
    // Body text - JetBrains Mono for technical content
    bodyLarge = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    bodySmall = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 14.sp
    ),
    
    // Labels and technical fields - JetBrains Mono
    labelLarge = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 10.sp,
        lineHeight = 12.sp
    ),
    
    // Buttons - Space Grotesk
    titleLarge = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 12.sp
    )
)

/**
 * Custom typography styles specific to MatrixScreen components
 */
object MatrixTextStyles {
    
    // Header styles
    val HeaderTitle = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    )
    
    val SectionLabel = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    
    // Button styles
    val ButtonText = TextStyle(
        fontFamily = SpaceGroteskFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
    
    // Technical content styles
    val SliderLabel = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
    
    val SliderValue = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    
    val HelperText = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
    
    val SymbolPreview = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    
    val SliderRangeLabel = TextStyle(
        fontFamily = JetBrainsMonoFamily,
        fontWeight = FontWeight.Light,
        fontSize = 10.sp,
        lineHeight = 12.sp
    )
    
    // Matrix-specific styles
    val MatrixRain = TextStyle(
        fontFamily = com.example.matrixscreen.ui.theme.MatrixCodeNFI,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    
    val MatrixSymbolPreview = TextStyle(
        fontFamily = com.example.matrixscreen.ui.theme.MatrixCodeNFI,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 22.sp
    )
    
    val MatrixFontPreview = TextStyle(
        fontFamily = com.example.matrixscreen.ui.theme.MatrixCodeNFI,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
}

/**
 * Composable function to get typography with fallback handling
 */
@Composable
fun getMatrixTypography(): Typography {
    return try {
        MatrixTypography
    } catch (e: Exception) {
        // Fallback to system typography if custom fonts fail to load
        Typography()
    }
}
