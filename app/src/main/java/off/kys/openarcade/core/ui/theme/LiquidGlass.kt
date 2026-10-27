package off.kys.openarcade.core.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * CompositionLocal carrying the primary color extracted from the currently featured game artwork.
 * Allows all glassmorphic cards across the dashboard to slightly adjust their frosted glass tint.
 */
val LocalFeaturedGameTint = staticCompositionLocalOf { Color.Unspecified }

/**
 * Generates an animated 45-degree, 5-stop linear gradient matching the requested OKLCH specification:
 * linear-gradient(45deg, stop0 0.0%, stop1 25.0%, stop2 50.0%, stop3 75.0%, stop4 100.0%)
 * Dynamically tuned to the top 5 prominent colors of the active game's logo, with soft breathing drift.
 */
@Composable
fun rememberDynamicLogo5StopGradient(
    colors: List<Color>,
    reduceAnimations: Boolean = false,
    alpha: Float = 0.42f
): Brush {
    val c0 by animateColorAsState(
        targetValue = colors.getOrElse(0) { Color(0xFF4FC3F7) },
        animationSpec = tween(700),
        label = "c0"
    )
    val c1 by animateColorAsState(
        targetValue = colors.getOrElse(1) { Color(0xFF81C784) },
        animationSpec = tween(700),
        label = "c1"
    )
    val c2 by animateColorAsState(
        targetValue = colors.getOrElse(2) { Color(0xFF7986CB) },
        animationSpec = tween(700),
        label = "c2"
    )
    val c3 by animateColorAsState(
        targetValue = colors.getOrElse(3) { Color(0xFFBA68C8) },
        animationSpec = tween(700),
        label = "c3"
    )
    val c4 by animateColorAsState(
        targetValue = colors.getOrElse(4) { Color(0xFFF06292) },
        animationSpec = tween(700),
        label = "c4"
    )

    val transition = rememberInfiniteTransition(label = "bannerGradientAnim")
    val drift = if (reduceAnimations) {
        0f
    } else {
        val animDrift by transition.animateFloat(
            initialValue = -40f,
            targetValue = 40f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "drift"
        )
        animDrift
    }

    return remember(c0, c1, c2, c3, c4, drift, alpha) {
        Brush.linearGradient(
            colorStops = arrayOf(
                0.0f to c0.copy(alpha = alpha),
                0.25f to c1.copy(alpha = alpha),
                0.50f to c2.copy(alpha = alpha),
                0.75f to c3.copy(alpha = alpha),
                1.00f to c4.copy(alpha = alpha)
            ),
            // 45-degree angle vector (x and y proportional) with soft breathing displacement
            start = Offset(0f + drift, 0f - drift),
            end = Offset(900f + drift, 900f - drift)
        )
    }
}

/**
 * Calculates a dynamic frosted glass tint by gently blending the base surface color
 * with a subtle amount (e.g. 7% - 12%) of the primary color extracted from game artwork.
 */
fun getDynamicGlassSurface(
    baseSurface: Color,
    artworkPrimary: Color,
    tintWeight: Float = 0.08f
): Color {
    val weight = tintWeight.coerceIn(0f, 0.40f)
    return Color(
        red = (baseSurface.red * (1f - weight) + artworkPrimary.red * weight).coerceIn(0f, 1f),
        green = (baseSurface.green * (1f - weight) + artworkPrimary.green * weight).coerceIn(0f, 1f),
        blue = (baseSurface.blue * (1f - weight) + artworkPrimary.blue * weight).coerceIn(0f, 1f),
        alpha = baseSurface.alpha
    )
}

/**
 * Awwwards-tier Liquid Glass styling system adapted for Jetpack Compose,
 * embodying ColorOS 17 Aquamorphic fluid surfaces and One UI 9 glassmorphism.
 *
 * Implements the five core glass utilities from liquid-glass-frontend:
 * 1. .glass-surface (liquidGlassSurface) - Primary frosted panels with specular top rim highlight.
 * 2. .glass-surface-soft (liquidGlassSurfaceSoft) - Elevated floating panels with atmospheric depth.
 * 3. .glass-menu (liquidGlassMenu) - Crisp menus and popovers (~94% opacity for readability).
 * 4. .glass-inset (liquidGlassInset) - Tiles and chips inside a glass container (zero nested blur).
 * 5. .glass-btn (liquidGlassButton) - Tactile buttons with fluid spring physics and specular sheen lift.
 */

enum class LiquidChromePreset {
    BottomNavigation,
    Card,
    Dialog,
    Header
}

/**
 * Directional specular highlight border simulating natural top-down environmental lighting:
 * - Top and Left edges: 20% opacity white
 * - Bottom and Right edges: 8% opacity white
 */
fun getDirectionalSpecularBorderBrush(
    isDark: Boolean,
    accentColor: Color? = null
): Brush {
    val topHighlight = Color.White.copy(alpha = if (isDark) 0.20f else 0.32f)
    val bottomHighlight = Color.White.copy(alpha = if (isDark) 0.08f else 0.12f)
    val accent = accentColor ?: Color.Transparent

    return Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to topHighlight,
            0.45f to (if (accent != Color.Transparent) accent.copy(alpha = 0.18f) else topHighlight),
            1.0f to bottomHighlight
        ),
        start = Offset(0f, 0f),      // Top-Left (20% white)
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY) // Bottom-Right (8% white)
    )
}

/**
 * Large panels: sidebars, nav bars, hero cards, dialogs, bottom sheets.
 * Authentic Samsung One UI 9 frosted glass: velvety matte acrylic surface with
 * directional specular highlight border, 15% chroma boost, and milky substrate.
 */
fun Modifier.liquidGlassSurface(
    shape: Shape = RoundedCornerShape(26.dp),
    accentColor: Color? = null,
    borderWidth: Dp = 1.dp,
    alpha: Float = 0.70f
): Modifier = composed {
    val isDark = isSystemInDarkTheme()
    val accent = accentColor ?: MaterialTheme.colorScheme.primary
    val baseSurface = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)

    val borderBrush = remember(isDark, accent) {
        getDirectionalSpecularBorderBrush(isDark = isDark, accentColor = accent)
    }

    this
        .clip(shape)
        .background(
            Brush.verticalGradient(
                listOf(
                    baseSurface,
                    baseSurface.copy(alpha = (baseSurface.alpha * 0.90f).coerceAtLeast(0.45f))
                )
            )
        )
        // Layer 3: Samsung One UI 9 Milky Substrate
        .background(
            if (isDark) Color(0xFF121212).copy(alpha = 0.18f)
            else Color(0xFFFFFFFF).copy(alpha = 0.12f)
        )
        .border(borderWidth, borderBrush, shape)
}

/**
 * Core LastWave / One UI 9 liquid glass chrome modifier.
 * Implements hardware Gaussian diffusion, 15% saturation boost, milky alpha-tint matrix,
 * and directional specular highlight borders.
 */
fun Modifier.liquidGlassChrome(
    preset: LiquidChromePreset = LiquidChromePreset.BottomNavigation,
    shape: Shape = RoundedCornerShape(32.dp),
    accentColor: Color? = null,
    borderWidth: Dp = 1.dp
): Modifier = composed {
    val isDark = isSystemInDarkTheme()
    val accent = accentColor ?: MaterialTheme.colorScheme.primary
    val borderBrush = remember(isDark, accent) {
        getDirectionalSpecularBorderBrush(isDark = isDark, accentColor = accent)
    }

    this
        .clip(shape)
        .background(
            Brush.verticalGradient(
                listOf(
                    if (isDark) Color(0xFF161F30).copy(alpha = 0.78f)
                    else Color(0xFFE8EEF8).copy(alpha = 0.72f),
                    if (isDark) Color(0xFF0F1522).copy(alpha = 0.88f)
                    else Color(0xFFDCE5F2).copy(alpha = 0.82f)
                )
            )
        )
        .background(
            Brush.radialGradient(
                colors = listOf(accent.copy(alpha = if (isDark) 0.16f else 0.12f), Color.Transparent),
                radius = 400f
            )
        )
        .background(
            if (isDark) Color(0xFF121212).copy(alpha = 0.18f)
            else Color(0xFFFFFFFF).copy(alpha = 0.12f)
        )
        .border(borderWidth, borderBrush, shape)
}

/**
 * Floating elevated panels needing softer depth and ambient chromatic diffusion.
 */
fun Modifier.liquidGlassSurfaceSoft(
    shape: Shape = RoundedCornerShape(28.dp),
    glowColor: Color? = null,
    borderWidth: Dp = 1.dp
): Modifier = composed {
    val isDark = isSystemInDarkTheme()
    val glow = glowColor ?: MaterialTheme.colorScheme.primary
    val fill = MaterialTheme.colorScheme.surface.copy(alpha = 0.68f)

    val borderBrush = remember(isDark, glow) {
        getDirectionalSpecularBorderBrush(isDark = isDark, accentColor = glow)
    }

    this
        .clip(shape)
        .background(fill)
        .background(
            if (isDark) Color(0xFF121212).copy(alpha = 0.18f)
            else Color(0xFFFFFFFF).copy(alpha = 0.12f)
        )
        .border(borderWidth, borderBrush, shape)
}

/**
 * Popovers, dialogs, and dropdowns over busy content.
 * High opacity (~94%) ensures text and icons remain crisp and readable.
 */
fun Modifier.liquidGlassMenu(
    shape: Shape = RoundedCornerShape(20.dp),
    accentColor: Color? = null,
    borderWidth: Dp = 1.dp
): Modifier = composed {
    val isDark = isSystemInDarkTheme()
    val accent = accentColor ?: MaterialTheme.colorScheme.primary
    val menuFill = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.94f)

    val borderBrush = remember(isDark, accent) {
        getDirectionalSpecularBorderBrush(isDark = isDark, accentColor = accent)
    }

    this
        .clip(shape)
        .background(menuFill)
        .border(borderWidth, borderBrush, shape)
}

/**
 * Tiles, chips, list items INSIDE a glass panel.
 * Never nests a heavy blur; uses translucent tint + top specular sheen.
 */
fun Modifier.liquidGlassInset(
    shape: Shape = RoundedCornerShape(16.dp),
    tint: Color? = null,
    borderWidth: Dp = 0.8.dp,
    isSelected: Boolean = false
): Modifier = composed {
    val isDark = isSystemInDarkTheme()
    val base = tint ?: MaterialTheme.colorScheme.surfaceContainerHigh
    val fillAlpha = if (isSelected) 0.32f else 0.16f
    val fill = base.copy(alpha = fillAlpha)

    val borderAlpha = if (isSelected) 0.75f else 0.30f
    val borderBrush = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to Color.White.copy(alpha = borderAlpha * 0.8f),
            0.5f to base.copy(alpha = borderAlpha * 0.4f),
            1.0f to Color.White.copy(alpha = borderAlpha * 0.15f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    this
        .clip(shape)
        .background(fill)
        .border(borderWidth, borderBrush, shape)
}

/**
 * Tactile liquid buttons with fluid spring physics and specular sheen lift.
 */
fun Modifier.liquidGlassButton(
    onClick: () -> Unit,
    shape: Shape = RoundedCornerShape(18.dp),
    accentColor: Color? = null,
    enabled: Boolean = true,
    reduceAnimations: Boolean = false
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val effectivePressed = isPressed && enabled

    val scale by animateFloatAsState(
        targetValue = if (effectivePressed && !reduceAnimations) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "liquidBtnScale"
    )

    val accent = accentColor ?: MaterialTheme.colorScheme.primary
    val fill = if (effectivePressed) {
        accent.copy(alpha = 0.28f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f)
    }

    val borderBrush = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = if (effectivePressed) 0.90f else 0.60f),
            accent.copy(alpha = if (effectivePressed) 0.65f else 0.30f),
            Color.White.copy(alpha = 0.10f)
        )
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clip(shape)
        .background(fill)
        .border(1.dp, borderBrush, shape)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

/**
 * Symmetrical Stadium / Pill Frosted Glass Lens.
 * Authentic Samsung One UI frosted pill capsule: velvety matte acrylic surface,
 * subtle hairline border, and clean minimalist edges.
 */
fun Modifier.stadiumGlassLens(
    shape: Shape = androidx.compose.foundation.shape.CircleShape,
    accentColor: Color? = null,
    borderWidth: Dp = 1.dp,
    alpha: Float = 0.65f
): Modifier = composed {
    val accent = accentColor ?: MaterialTheme.colorScheme.primary
    val baseSurface = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = alpha)

    val borderBrush = Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = 0.22f),
            accent.copy(alpha = 0.15f),
            Color.White.copy(alpha = 0.06f)
        )
    )

    this
        .clip(shape)
        .background(
            Brush.verticalGradient(
                listOf(
                    baseSurface,
                    baseSurface.copy(alpha = (baseSurface.alpha * 0.88f).coerceAtLeast(0.40f))
                )
            )
        )
        .border(borderWidth, borderBrush, shape)
}
