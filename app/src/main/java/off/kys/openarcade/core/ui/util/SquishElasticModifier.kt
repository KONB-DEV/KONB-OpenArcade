package off.kys.openarcade.core.ui.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * ColorOS 17 & One UI 9 Tactile Squish-and-Stretch Modifier.
 *
 * Simulates physical kinetic elasticity:
 * - On pointer down (press): instantaneously compresses vertically while expanding horizontally
 *   (scaleX: ~1.03f, scaleY: ~0.94f) to convey tactile displacement.
 * - On pointer up (release): snaps back with an elastic bounce (overshoot) using damped spring physics
 *   before settling back to rest (1.0f, 1.0f).
 *
 * Utilizes [Modifier.graphicsLayer] for 100% GPU-accelerated transforms without triggering layout passes.
 */
fun Modifier.squishElasticPress(
    interactionSource: MutableInteractionSource? = null,
    reduceAnimations: Boolean = false,
    squishX: Float = 1.032f,
    squishY: Float = 0.942f
): Modifier = composed {
    if (reduceAnimations) return@composed this

    val source = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by source.collectIsPressedAsState()

    val scaleXAnim = remember { Animatable(1f) }
    val scaleYAnim = remember { Animatable(1f) }

    val pressSpring = remember {
        spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        )
    }
    val releaseSpring = remember {
        spring<Float>(
            dampingRatio = 0.62f,
            stiffness = 240f
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            launch {
                scaleXAnim.animateTo(targetValue = squishX, animationSpec = pressSpring)
            }
            launch {
                scaleYAnim.animateTo(targetValue = squishY, animationSpec = pressSpring)
            }
        } else {
            launch {
                scaleXAnim.animateTo(targetValue = 1f, animationSpec = releaseSpring)
            }
            launch {
                scaleYAnim.animateTo(targetValue = 1f, animationSpec = releaseSpring)
            }
        }
    }

    this.graphicsLayer {
        scaleX = scaleXAnim.value
        scaleY = scaleYAnim.value
    }
}
