package com.example.matrixscreen.data

import androidx.compose.ui.graphics.Color

/**
 * Available color themes for the Matrix effect
 */
enum class MatrixColor(val displayName: String, val color: Color, val colorValue: Long) {
    GREEN("Classic", Color(0xFF00FF00), 0xFF00FF00),
    RED("Cyber Red", Color(0xFFFF0040), 0xFFFF0040),
    BLUE("Blue", Color(0xFF0080FF), 0xFF0080FF),
    CYAN("Cyan", Color(0xFF00FFFF), 0xFF00FFFF),
    PURPLE("Purple", Color(0xFF8000FF), 0xFF8000FF),
    ORANGE("Orange", Color(0xFFFF8000), 0xFFFF8000),
    WHITE("White", Color(0xFFFFFFFF), 0xFFFFFFFF),
    BLACK("Shadow", Color(0xFF000000), 0xFF000000)
}
