package com.example.matrixscreen.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.matrixscreen.core.design.DesignTokens
import com.example.matrixscreen.data.model.FlowDirection
import com.example.matrixscreen.ui.theme.MatrixTextStyles

@Composable
fun FlowDirectionSelector(
    label: String,
    value: FlowDirection,
    onValueChange: (FlowDirection) -> Unit,
    help: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(DesignTokens.Spacing.md)
    ) {
        Text(
            text = label,
            style = MatrixTextStyles.SliderLabel,
            color = MaterialTheme.colorScheme.onSurface
        )

        FlowDirectionGrid(
            selected = value,
            onSelected = onValueChange,
            modifier = Modifier.padding(top = DesignTokens.Spacing.sm)
        )

        help?.let {
            Text(
                text = it,
                style = MatrixTextStyles.HelperText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = DesignTokens.Spacing.xs)
            )
        }
    }
}

@Composable
private fun FlowDirectionGrid(
    selected: FlowDirection,
    onSelected: (FlowDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        DirectionOption(
            FlowDirection.TOP_TO_BOTTOM,
            Icons.Rounded.ArrowDownward,
            "Down",
            "Flow down"
        ),
        DirectionOption(
            FlowDirection.BOTTOM_TO_TOP,
            Icons.Rounded.ArrowUpward,
            "Up",
            "Flow up"
        ),
        DirectionOption(
            FlowDirection.LEFT_TO_RIGHT,
            Icons.Rounded.ArrowForward,
            "Right",
            "Flow right"
        ),
        DirectionOption(
            FlowDirection.RIGHT_TO_LEFT,
            Icons.Rounded.ArrowBack,
            "Left",
            "Flow left"
        )
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm)
    ) {
        options.chunked(2).forEach { rowOptions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(DesignTokens.Spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowOptions.forEach { option ->
                    FlowDirectionTile(
                        option = option,
                        selected = selected == option.direction,
                        onClick = { onSelected(option.direction) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

private data class DirectionOption(
    val direction: FlowDirection,
    val icon: ImageVector,
    val label: String,
    val description: String
)

@Composable
private fun FlowDirectionTile(
    option: DirectionOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val accent = MaterialTheme.colorScheme.primary
    val borderColor = if (selected) accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }

    Surface(
        modifier = modifier
            .height(84.dp)
            .shadow(
                elevation = if (selected) 12.dp else 0.dp,
                shape = shape,
                ambientColor = accent.copy(alpha = 0.4f),
                spotColor = accent.copy(alpha = 0.4f)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .background(Color.Transparent, shape)
            .clickable(
                onClick = onClick,
                role = Role.RadioButton
            )
            .semantics {
                contentDescription = "${option.description}${if (selected) ", selected" else ""}"
                role = Role.RadioButton
            },
        color = Color.Transparent,
        shape = shape
    ) {
        Box(
            modifier = Modifier
                .background(containerColor, shape)
                .padding(PaddingValues(vertical = 8.dp))
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = option.label,
                    style = MatrixTextStyles.SliderValue.copy(fontWeight = FontWeight.Bold),
                    color = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


