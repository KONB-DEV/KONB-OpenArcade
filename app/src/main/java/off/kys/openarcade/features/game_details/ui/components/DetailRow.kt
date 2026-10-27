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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color,
    onClick: (() -> Unit)?
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val squircleShape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp)

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.97f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "detailRowScale"
    )

    val containerColor = if (isPressed && onClick != null) {
        primary.copy(alpha = 0.20f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    }

    val animatedContainer by animateColorAsState(
        targetValue = containerColor,
        animationSpec = tween(200),
        label = "detailRowContainer"
    )

    val borderBrush = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = if (isPressed && onClick != null) 0.85f else 0.50f),
            if (isPressed && onClick != null) primary.copy(alpha = 0.65f) else tertiary.copy(alpha = 0.30f),
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
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
    ) {
        // Specular top highlight
        androidx.compose.foundation.Canvas(modifier = Modifier.matchParentSize()) {
            drawLine(
                brush = Brush.horizontalGradient(
                    0.0f to Color.Transparent,
                    0.20f to Color.White.copy(alpha = if (isPressed) 0.80f else 0.45f),
                    0.50f to primary.copy(alpha = if (isPressed) 0.70f else 0.35f),
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = valueColor,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 200.dp)
                )
                if (onClick != null) {
                    Icon(
                        painter = painterResource(R.drawable.round_chevron_left_24),
                        contentDescription = null,
                        tint = primary.copy(alpha = 0.45f),
                        modifier = Modifier
                            .size(15.dp)
                            .graphicsLayer { rotationZ = 180f }
                    )
                }
            }
        }
    }
}
