package off.kys.openarcade.features.game_details.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.features.games_launcher.ui.components.ArcadeFilterChip

/**
 * Custom Detail Row built specifically to house responsive tags inside FlowRow.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryDetailRow(
    label: String,
    categories: List<String>,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val squircleShape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp)

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "catRowScale"
    )

    val containerColor = if (isPressed) {
        accentColor.copy(alpha = 0.20f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    val animatedContainer by animateColorAsState(
        targetValue = containerColor,
        animationSpec = tween(200),
        label = "categoryDetailRowContainer"
    )

    val borderBrush = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = if (isPressed) 0.85f else 0.50f),
            if (isPressed) accentColor.copy(alpha = 0.65f) else accentColor.copy(alpha = 0.30f),
            Color.White.copy(alpha = 0.10f),
            Color.Transparent
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(squircleShape)
            .background(animatedContainer)
            .border(1.2.dp, borderBrush, squircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        // Specular top highlight
        androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
            drawLine(
                brush = Brush.horizontalGradient(
                    0.0f to Color.Transparent,
                    0.20f to Color.White.copy(alpha = if (isPressed) 0.80f else 0.45f),
                    0.50f to accentColor.copy(alpha = if (isPressed) 0.70f else 0.35f),
                    0.80f to Color.White.copy(alpha = 0.30f),
                    1.0f to Color.Transparent
                ),
                start = Offset(0f, 1f),
                end = Offset(size.width, 1f),
                strokeWidth = 1.3.dp.toPx()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                FlowRow(
                    modifier = Modifier.widthIn(max = 220.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { category ->
                        ArcadeFilterChip(
                            label = category,
                            selected = false,
                            onClick = onClick // Just open dialog
                        )
                    }
                }

                Spacer(Modifier.width(6.dp))

                Icon(
                    painter = painterResource(R.drawable.round_chevron_left_24),
                    contentDescription = null,
                    tint = accentColor.copy(alpha = 0.45f),
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(15.dp)
                        .graphicsLayer { rotationZ = 180f }
                )
            }
        }
    }
}
