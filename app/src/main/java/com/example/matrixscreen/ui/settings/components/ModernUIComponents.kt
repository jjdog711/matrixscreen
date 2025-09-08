package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.data.model.MatrixSettings
import com.example.matrixscreen.ui.theme.AppTypography
import com.example.matrixscreen.ui.theme.MatrixUIColorScheme
import com.example.matrixscreen.ui.theme.ModernTextWithGlow

@Composable
fun ModernSectionLabel(
    label: String,
    colorScheme: MatrixUIColorScheme
) {
    Text(
        text = label,
        style = AppTypography.labelMedium,
        color = colorScheme.textSecondary,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ModernHelperText(
    text: String,
    colorScheme: MatrixUIColorScheme
) {
    Text(
        text = text,
        style = AppTypography.bodySmall,
        color = colorScheme.textSecondary
    )
}

@Composable
fun ModernSymbolPreview(
    characters: String,
    colorScheme: MatrixUIColorScheme,
    maxCharacters: Int = 20
) {
    Text(
        text = characters.take(maxCharacters),
        style = AppTypography.bodySmall,
        color = colorScheme.textPrimary,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
    )
}

@Composable
fun PresetButton(
    name: String,
    onClick: () -> Unit,
    ui: MatrixUIColorScheme,
    optimizedSettings: MatrixSettings,
    modifier: Modifier = Modifier,
    swatches: List<Long>? = null
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = ui.selectionBackground,
            contentColor = ui.textPrimary
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModernTextWithGlow(
                text = name,
                style = AppTypography.labelMedium,
                color = ui.textPrimary,
                settings = optimizedSettings
            )
            if (!swatches.isNullOrEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    swatches.take(6).forEach { c ->
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color(c))
                                .border(1.dp, ui.textSecondary.copy(alpha = 0.25f), CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColorSwatch(
    color: Long,
    onClick: () -> Unit,
    ui: MatrixUIColorScheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(32.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(color)
        ),
        shape = CircleShape
    ) {
        // Empty content - the card itself is the color swatch
    }
}