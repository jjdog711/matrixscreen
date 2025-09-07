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

// Slimmer modern scale — smaller than default, but readable
val AppTypography = Typography(
    // Screen title / overlay title
    headlineSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize   = 16.sp,   // was 20sp → slimmer
        letterSpacing = 0.sp
    ),
    // Section headers
    titleSmall = androidx.compose.ui.text.TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 14.sp
    ),
    // Field labels
    bodyMedium = androidx.compose.ui.text.TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Normal,
        fontSize   = 13.sp
    ),
    // Helper / hint text
    bodySmall = androidx.compose.ui.text.TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = FontWeight.Light,
        fontSize   = 12.sp
    ),
    // Button text
    labelLarge = androidx.compose.ui.text.TextStyle(
        fontFamily = SpaceGrotesk,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp
    )
)