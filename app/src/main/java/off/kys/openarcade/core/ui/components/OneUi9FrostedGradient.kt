package off.kys.openarcade.core.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import off.kys.openarcade.core.ui.theme.BentoTokens
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Checks system low-power / battery saver status and updates reactively.
 * As required by the One UI 9 specification, blur collapses to 0 and transforms into
 * an elegant 92% opaque solid background when low-power mode is active.
 */
@Composable
fun rememberIsBatterySaverActive(): Boolean {
    val context = LocalContext.current
    var isPowerSave by remember {
        val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        mutableStateOf(pm?.isPowerSaveMode == true)
    }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context?, intent: Intent?) {
                val pm = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
                isPowerSave = pm?.isPowerSaveMode == true
            }
        }
        val filter = IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        context.registerReceiver(receiver, filter)
        onDispose {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: Exception) {}
        }
    }

    return isPowerSave
}

/**
 * Directional specular highlight border simulating natural top-down environmental light:
 * - Top and Left edges: 20% opacity white
 * - Bottom and Right edges: 8% opacity white
 * Creates crisp boundary separation without flat borders.
 */
fun getOneUi9SpecularBrush(
    isDark: Boolean = true,
    accentColor: Color? = null
): Brush {
    val topHighlight = Color.White.copy(alpha = if (isDark) 0.22f else 0.35f)
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
 * True 45-degree 5-stop animated linear gradient based on the requested OKLCH specification:
 * linear-gradient(45deg, stop0 0.0%, stop1 25.0%, stop2 50.0%, stop3 75.0%, stop4 100.0%)
 *
 * In standard CSS, 45deg starts at the bottom-left and points to the top-right.
 * Here we calculate the exact (start, end) coordinate offsets for a true 45° angle,
 * applying subtle breathing drift and continuous color cross-fading.
 */
@Composable
fun rememberTrue45Deg5StopGradient(
    colors: List<Color>,
    reduceAnimations: Boolean = false,
    isDark: Boolean = true
): Brush {
    val defaultStops = listOf(
        Color(0xFF2E7D32), // Emerald / green
        Color(0xFF0288D1), // Cyan / light blue
        Color(0xFF3F51B5), // Indigo / cobalt
        Color(0xFF7B1FA2), // Violet
        Color(0xFFC2185B)  // Magenta / rose
    )

    val c0 by animateColorAsState(
        targetValue = colors.getOrElse(0) { defaultStops[0] },
        animationSpec = tween(650),
        label = "c0"
    )
    val c1 by animateColorAsState(
        targetValue = colors.getOrElse(1) { defaultStops[1] },
        animationSpec = tween(650),
        label = "c1"
    )
    val c2 by animateColorAsState(
        targetValue = colors.getOrElse(2) { defaultStops[2] },
        animationSpec = tween(650),
        label = "c2"
    )
    val c3 by animateColorAsState(
        targetValue = colors.getOrElse(3) { defaultStops[3] },
        animationSpec = tween(650),
        label = "c3"
    )
    val c4 by animateColorAsState(
        targetValue = colors.getOrElse(4) { defaultStops[4] },
        animationSpec = tween(650),
        label = "c4"
    )

    val transition = rememberInfiniteTransition(label = "banner45DegDrift")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val driftOffset = if (reduceAnimations) Offset.Zero else Offset(
        x = (cos(phase) * 35f).toFloat(),
        y = (sin(phase) * 35f).toFloat()
    )

    return remember(c0, c1, c2, c3, c4, driftOffset, isDark) {
        // True 45 degree angle: starts at bottom-left, ascends towards top-right
        Brush.linearGradient(
            colorStops = arrayOf(
                0.00f to c0,
                0.25f to c1,
                0.50f to c2,
                0.75f to c3,
                1.00f to c4
            ),
            start = Offset(0f + driftOffset.x, 850f + driftOffset.y), // Bottom-Left
            end = Offset(850f + driftOffset.x, 0f + driftOffset.y)    // Top-Right (45°)
        )
    }
}

/**
 * Samsung One UI 9 Frosted Glass Banner Container.
 * Combines:
 * 1. The vibrant 45-degree 5-stop animated dynamic logo gradient.
 * 2. Samsung One UI 9 Frosted Glass optical stack (Milky tint + fine noise grain).
 * 3. Directional specular highlight border (Top/Left 20% white, Bottom/Right 8% white).
 * 4. Battery Saver fallback to 92% opaque solid finish.
 */
@Composable
fun OneUi9DynamicGradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    shape: Shape = BentoTokens.HeroShape,
    borderWidth: Dp = 1.dp,
    reduceAnimations: Boolean = false,
    isDark: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val isBatterySaver = rememberIsBatterySaverActive()
    val gradientBrush = rememberTrue45Deg5StopGradient(
        colors = colors,
        reduceAnimations = reduceAnimations || isBatterySaver,
        isDark = isDark
    )

    val specularBrush = remember(isDark, colors.firstOrNull()) {
        getOneUi9SpecularBrush(isDark = isDark, accentColor = colors.firstOrNull())
    }

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (isBatterySaver) {
                    // Fallback in Low-Power / Battery Saver mode: 92% solid surface
                    Modifier.background(
                        if (isDark) Color(0xFF141822).copy(alpha = 0.92f)
                        else Color(0xFFF0F4F8).copy(alpha = 0.92f)
                    )
                } else {
                    // Full One UI 9 multi-layered optical stack
                    Modifier
                        // Layer 1: The vibrant 45-degree 5-stop dynamic logo linear gradient
                        .background(gradientBrush)
                        // Layer 2: Deep atmospheric diffusion vignette
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    if (isDark) Color(0xFF0D1117).copy(alpha = 0.55f)
                                    else Color(0xFF0F172A).copy(alpha = 0.18f)
                                ),
                                radius = 900f
                            )
                        )
                        // Layer 3: Samsung One UI 9 Milky Substrate
                        // (#FFFFFF at 12% in light mode, #121212 at 18% in dark mode)
                        .background(
                            if (isDark) Color(0xFF121212).copy(alpha = 0.18f)
                            else Color(0xFFFFFFFF).copy(alpha = 0.12f)
                        )
                }
            )
            // Layer 4: Directional Specular Highlight Border
            .border(borderWidth, specularBrush, shape)
    ) {
        // Fine frosted noise grain texture overlay to mitigate OLED banding
        if (!isBatterySaver) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawOneUi9NoiseGrain(isDark = isDark)
            }
        }

        // Layer 5: High-Contrast Foreground Content
        content()
    }
}

/**
 * Draws fine geometric micro-grain overlay (1.5% opacity) to mitigate color banding on high-refresh OLEDs.
 */
private fun DrawScope.drawOneUi9NoiseGrain(isDark: Boolean) {
    val step = 14f
    val dotColor = if (isDark) Color.White.copy(alpha = 0.02f) else Color.Black.copy(alpha = 0.015f)
    var y = 0f
    while (y < size.height) {
        var x = if ((y / step).toInt() % 2 == 0) 0f else step / 2f
        while (x < size.width) {
            drawCircle(
                color = dotColor,
                radius = 1.0f,
                center = Offset(x, y),
                blendMode = BlendMode.Overlay
            )
            x += step
        }
        y += step
    }
}
