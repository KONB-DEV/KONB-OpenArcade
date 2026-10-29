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

    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    // Dynamic Tinting System: adjusts frosted glass based on wallpaper / featured game artwork
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

    // Dynamic Cloudy Backdrop: samples wallpaper/artwork color with organic diffusion
    val cloudyBase = if (isDark) {
        Color(0xFF101726).copy(alpha = 0.80f)
    } else {
        Color(0xFFF8FAFC).copy(alpha = 0.86f)
    }

    val dynamicCloudyBase = remember(cloudyBase, effectiveArtworkColor, isDark) {
        off.kys.openarcade.core.ui.theme.getDynamicGlassSurface(
            baseSurface = cloudyBase,
            artworkPrimary = effectiveArtworkColor,
            tintWeight = if (isDark) 0.14f else 0.08f
        )
    }

    val pressedGlass = effectiveArtworkColor.copy(alpha = if (isDark) 0.28f else 0.22f)

    val animatedTint by animateColorAsState(
        targetValue = if (isPressed && onClick != null) pressedGlass else dynamicCloudyBase,
        animationSpec = AnimationUtils.animationSpec(reduceAnimations, 350),
        label = "frostedCardTint"
    )

    // Samsung One UI 9 Directional Specular Border: Top/Left 24% white, Bottom/Right 8% white
    val borderBrush = remember(isDark, effectiveArtworkColor, isPressed) {
        val topAlpha = if (isPressed) 0.45f else (if (isDark) 0.24f else 0.40f)
        val bottomAlpha = if (isPressed) 0.20f else (if (isDark) 0.08f else 0.14f)
        Brush.linearGradient(
            colorStops = arrayOf(
                0.0f to Color.White.copy(alpha = topAlpha),
                0.40f to effectiveArtworkColor.copy(alpha = if (isDark) 0.28f else 0.35f),
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
            // Layer 1: Ambient Wallpaper Color Refraction (Cloudy Backdrop Bloom)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        effectiveArtworkColor.copy(alpha = if (isDark) 0.20f else 0.14f),
                        Color.Transparent
                    ),
                    radius = 500f
                )
            )
            // Layer 2: Cloudy Frosted Substrate
            .background(backgroundGradient)
            // Layer 3: Samsung One UI 9 Cloudy Milky Substrate
            .background(
                if (isDark) Color(0xFFFFFFFF).copy(alpha = 0.04f)
                else Color(0xFFFFFFFF).copy(alpha = 0.35f)
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
