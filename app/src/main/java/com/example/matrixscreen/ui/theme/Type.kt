package com.example.matrixscreen.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.matrixscreen.R

// Display: Space Grotesk - using actual font files
val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_semibold, FontWeight.SemiBold)
)

// Technical / Labels: JetBrains Mono - using actual font files
val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_light, FontWeight.Light),
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal)
)

// Matrix Code NFI - for custom symbol sets and Matrix rain effect
val MatrixCodeNFI = FontFamily(
    Font(R.font.matrix_code_nfi, FontWeight.Normal)
)

// Cascadia Mono - alternative monospace font
val CascadiaMono = FontFamily(
    Font(R.font.cascadia_mono, FontWeight.Normal)
)

// Digital 7 - digital display font
val Digital7 = FontFamily(
    Font(R.font.digital_7, FontWeight.Normal)
)

// Orbitron - futuristic font
val Orbitron = FontFamily(
    Font(R.font.orbitron, FontWeight.Normal)
)

// Modern HUD typography scale — tighter, more tactile feel
val AppTypography = Typography(
    // Titles & Section Headers - Space Grotesk
    headlineLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    headlineMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),
    headlineSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    
    // Label text (sliders, inputs) - JetBrains Mono
    bodyLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    bodySmall = androidx.compose.ui.text.TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Light,
        fontSize = 12.sp,
        lineHeight = 14.sp
    ),
    
    // Value or numeric readouts - JetBrains Mono
    labelSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    
    // Button text - Space Grotesk
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),
    
    // Section headers - Space Grotesk
    titleSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)