package com.example.matrixscreen.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Data class representing a complete Matrix color theme
 * Contains all color properties needed for a cohesive visual theme
 */
data class MatrixColorTheme(
    val name: String,
    val headColor: Color,
    val brightTrailColor: Color,
    val trailColor: Color,
    val dimTrailColor: Color,
    val backgroundColor: Color
)

/**
 * Predefined list of 24 curated Matrix color themes
 * Each theme provides a complete color palette for the Matrix rain effect
 */
val MatrixColorThemePresets = listOf(
    MatrixColorTheme("Matrix Classic", Color(0xFF00FF41), Color(0xFF80FF80), Color(0xFF40FF40), Color(0xFF20FF20), Color(0xFF000000)),
    MatrixColorTheme("Tangerine Dream", Color(0xFFFF6B00), Color(0xFFFF9966), Color(0xFFFF8840), Color(0xFFFF7740), Color(0xFF111111)),
    MatrixColorTheme("Electric Cyan", Color(0xFF00FFFF), Color(0xFF66FFFF), Color(0xFF33CCCC), Color(0xFF229999), Color(0xFF000000)),
    MatrixColorTheme("Radiant Magenta", Color(0xFFFF00AA), Color(0xFFFF66CC), Color(0xFFFF3399), Color(0xFFCC3399), Color(0xFF0A0A0A)),
    MatrixColorTheme("Cyber Blue", Color(0xFF0099FF), Color(0xFF66CCFF), Color(0xFF3399CC), Color(0xFF2277AA), Color(0xFF030F2E)),
    MatrixColorTheme("Gamma Lime", Color(0xFFADFF2F), Color(0xFFCCFF66), Color(0xFF99CC33), Color(0xFF779933), Color(0xFF090909)),
    MatrixColorTheme("Digital Flame", Color(0xFFFF3300), Color(0xFFFF6633), Color(0xFFCC3300), Color(0xFF992200), Color(0xFF1B1B1B)),
    MatrixColorTheme("Violet Burst", Color(0xFF9B30FF), Color(0xFFB266FF), Color(0xFF9147FF), Color(0xFF7722AA), Color(0xFF080019)),
    MatrixColorTheme("Alien Mint", Color(0xFF00FFCC), Color(0xFF66FFE5), Color(0xFF33CCAA), Color(0xFF229988), Color(0xFF121212)),
    MatrixColorTheme("Voltage White", Color(0xFFFFFFFF), Color(0xFFE0E0FF), Color(0xFFCCCCFF), Color(0xFFAAAAFF), Color(0xFF000000)),
    MatrixColorTheme("Terminal White", Color(0xFF000000), Color(0xFF333333), Color(0xFF666666), Color(0xFF999999), Color(0xFFFFFFFF)),
    MatrixColorTheme("Sunburn", Color(0xFFFF4500), Color(0xFFFF7256), Color(0xFFCC3910), Color(0xFF993312), Color(0xFFFFF5E1)),
    MatrixColorTheme("Classic Print", Color(0xFF222266), Color(0xFF333377), Color(0xFF444488), Color(0xFF555599), Color(0xFFF9F9F9)),
    MatrixColorTheme("Snow Code", Color(0xFF333333), Color(0xFF666666), Color(0xFF999999), Color(0xFFBBBBBB), Color(0xFFFFFFFF)),
    MatrixColorTheme("Abyss Inverse", Color(0xFF00FF41), Color(0xFF80FF80), Color(0xFF40FF40), Color(0xFF20FF20), Color(0xFFE6E6E6)),
    MatrixColorTheme("Synth Grid", Color(0xFFFF00FF), Color(0xFFFF66FF), Color(0xFFCC33CC), Color(0xFF992299), Color(0xFF0D0D40)),
    MatrixColorTheme("Laser Lemon", Color(0xFFFFFF33), Color(0xFFFFFF66), Color(0xFFCCCC33), Color(0xFF999900), Color(0xFF010824)),
    MatrixColorTheme("Neon Jungle", Color(0xFF7FFF00), Color(0xFFADFF2F), Color(0xFF6B8E23), Color(0xFF556B2F), Color(0xFF002F2F)),
    MatrixColorTheme("Ultraviolet", Color(0xFF9400D3), Color(0xFFBA55D3), Color(0xFF9932CC), Color(0xFF7B68EE), Color(0xFF000000)),
    MatrixColorTheme("Nightdrive Teal", Color(0xFF00CED1), Color(0xFF40E0D0), Color(0xFF20B2AA), Color(0xFF008B8B), Color(0xFF002222)),
    MatrixColorTheme("Cotton Candy", Color(0xFFFF66CC), Color(0xFFFFB6C1), Color(0xFFFF99AA), Color(0xFFEE82EE), Color(0xFF120017)),
    MatrixColorTheme("Aurora Green", Color(0xFF66FFB2), Color(0xFFB2FFF0), Color(0xFF80FFAA), Color(0xFF66CCAA), Color(0xFF202020)),
    MatrixColorTheme("Redshift", Color(0xFFFF0033), Color(0xFFFF6666), Color(0xFFCC3333), Color(0xFF992222), Color(0xFF1C1C1C)),
    MatrixColorTheme("Solar Flare", Color(0xFFFFD700), Color(0xFFFFFF99), Color(0xFFFFCC00), Color(0xFFCC9900), Color(0xFF1A1A1A))
)
