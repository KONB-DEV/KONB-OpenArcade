package off.kys.openarcade.features.app_picker.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import off.kys.openarcade.R
import off.kys.openarcade.core.domain.model.AppInfo

@Composable
fun AppItem(
    app: AppInfo,
    isSelected: Boolean,
    onToggle: () -> Unit,
    hapticFeedbackEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val squircleShape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)

    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "appItemScale"
    )

    // Container Background Color Animation with frosted liquid glass
    val containerColor by animateColorAsState(
        targetValue = if (isSelected)
            primary.copy(alpha = 0.22f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f),
        animationSpec = tween(200),
        label = "appItemContainer"
    )

    // Smooth progress for animating gradients
    val animationProgress by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(200),
        label = "selectionProgress"
    )

    // Animated colors for row border gradient with specular rim
    val rowStartColor = lerp(Color.White.copy(alpha = 0.40f), Color.White.copy(alpha = 0.85f), animationProgress)
    val rowAccentColor = lerp(tertiary.copy(alpha = 0.25f), primary.copy(alpha = 0.70f), animationProgress)

    val borderBrush = Brush.linearGradient(
        listOf(rowStartColor, rowAccentColor, Color.Transparent)
    )

    val context = LocalContext.current
    val iconModel = remember(app.packageName) {
        try {
            context.packageManager.getApplicationIcon(app.packageName)
        } catch (_: Exception) {
            null
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .scale(scale)
            .clip(squircleShape)
            .background(containerColor, squircleShape)
            .border(1.1.dp, borderBrush, squircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (hapticFeedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onToggle()
                }
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon in a squircle
        Card(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.80f)
            ),
            modifier = Modifier.border(
                0.8.dp,
                Color.White.copy(alpha = 0.40f),
                androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            )
        ) {
            AsyncImage(
                model = iconModel,
                contentDescription = null,
                modifier = Modifier
                    .size(44.dp)
                    .padding(4.dp),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.label,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = app.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(12.dp))

        // Animated colors for checkbox border gradient
        val checkStartColor = lerp(tertiary.copy(alpha = 0.35f), primary.copy(alpha = 0.85f), animationProgress)
        val checkEndColor = lerp(Color.Transparent, tertiary.copy(alpha = 0.40f), animationProgress)

        val checkBorderBrush = Brush.linearGradient(
            listOf(checkStartColor, checkEndColor)
        )

        val checkFill by animateColorAsState(
            targetValue = if (isSelected) primary else Color.Transparent,
            animationSpec = tween(200),
            label = "checkFill"
        )

        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(checkFill, MaterialTheme.shapes.extraSmall)
                .border(1.dp, checkBorderBrush, MaterialTheme.shapes.extraSmall),
            contentAlignment = Alignment.Center
        ) {
            // Smoothly scales and fades the check icon in/out
            this@Row.AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn(tween(150)) + scaleIn(transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center, initialScale = 0.6f),
                exit = fadeOut(tween(150)) + scaleOut(transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center, targetScale = 0.6f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_check_24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}