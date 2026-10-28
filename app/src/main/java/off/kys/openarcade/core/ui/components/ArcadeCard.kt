package off.kys.openarcade.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import off.kys.openarcade.core.ui.theme.BentoTokens
import off.kys.openarcade.core.ui.util.AnimationUtils

/**
 * Flagship One UI 9 & ColorOS 17 Liquid Glass Card.
 * Combines translucent frosted surfaces, dynamic game-tinted ambient backdrops,
 * refractive specular rim highlights, and fluid organic spring physics.
 */
@Composable
fun ArcadeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = BentoTokens.LargeTileShape,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    tertiaryColor: Color = MaterialTheme.colorScheme.tertiary,
    borderWidth: Dp = 1.3.dp,
    reduceAnimations: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // ColorOS 17 Tactile Squeeze (0.94x over 80ms) & Elastic Bounce Release (101.5% overshoot)
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null && !reduceAnimations) 0.94f else 1f,
        animationSpec = if (isPressed) {
            tween(durationMillis = 80, easing = CubicBezierEasing(0.2f, 0f, 0f, 1f))
        } else {
            spring(
                dampingRatio = 0.55f, // Elastic bounce overshoot
                stiffness = 320f
            )
        },
        label = "liquidCardScale"
    )

    // Dynamic Tinting System: adjusts frosted glass slightly based on featured game artwork primary color
    val featuredArtworkTint = off.kys.openarcade.core.ui.theme.LocalFeaturedGameTint.current
    val effectiveArtworkColor = remember(featuredArtworkTint, accentColor) {
        if (featuredArtworkTint.isSpecified &&
            featuredArtworkTint != Color.Transparent &&
            featuredArtworkTint != Color.Unspecified
        ) {
            featuredArtworkTint
        } else {
            accentColor
        }
    }

    val baseSurface = MaterialTheme.colorScheme.surfaceVariant
    // Blends base frosted acrylic with 8% of the extracted artwork primary color
    val dynamicallyTintedBase = remember(baseSurface, effectiveArtworkColor) {
        off.kys.openarcade.core.ui.theme.getDynamicGlassSurface(
            baseSurface = baseSurface,
            artworkPrimary = effectiveArtworkColor,
            tintWeight = 0.08f
        ).copy(alpha = 0.72f)
    }

    val pressedGlass = effectiveArtworkColor.copy(alpha = 0.22f)

    val animatedTint by animateColorAsState(
        targetValue = if (isPressed && onClick != null) pressedGlass else dynamicallyTintedBase,
        animationSpec = AnimationUtils.animationSpec(reduceAnimations, 350),
        label = "frostedCardTint"
    )

    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    // Samsung One UI 9 Directional Specular Border: Top/Left 20% white, Bottom/Right 8% white
    val borderBrush = remember(isDark, effectiveArtworkColor, isPressed) {
        val topAlpha = if (isPressed) 0.38f else (if (isDark) 0.20f else 0.32f)
        val bottomAlpha = if (isPressed) 0.16f else (if (isDark) 0.08f else 0.12f)
        Brush.linearGradient(
            colorStops = arrayOf(
                0.0f to Color.White.copy(alpha = topAlpha),
                0.45f to effectiveArtworkColor.copy(alpha = topAlpha * 0.70f),
                1.0f to Color.White.copy(alpha = bottomAlpha)
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    val backgroundGradient = remember(animatedTint) {
        Brush.verticalGradient(
            listOf(
                animatedTint,
                animatedTint.copy(alpha = (animatedTint.alpha * 0.88f).coerceAtLeast(0.40f))
            )
        )
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(shape)
            .background(backgroundGradient)
            // Layer 3: Samsung One UI 9 Milky Substrate
            .background(
                if (isDark) Color(0xFF121212).copy(alpha = 0.18f)
                else Color(0xFFFFFFFF).copy(alpha = 0.12f)
            )
            .border(borderWidth, borderBrush, shape)
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
        Column {
            content()
        }
    }
}
