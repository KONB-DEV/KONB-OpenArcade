package off.kys.openarcade.core.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer

/**
 * OPPO ColorOS 17 Living Flow & Luminous Rendering Engine Physics Tokens.
 * Enforces continuous, non-linear, parallel-rendered transitions with organic physical damping.
 */
object LuminousMotionTokens {

    /**
     * One-Take Game Launch Spring:
     * Mass = 1.0, Stiffness = 260f, DampingRatio = 0.88f.
     * Smooth, continuous geometric expansion without jarring cross-fades.
     */
    val OneTakeLaunchSpring: androidx.compose.animation.core.SpringSpec<Float> = spring(
        dampingRatio = 0.88f,
        stiffness = 260f
    )

    val FluidCapsuleSpring: androidx.compose.animation.core.SpringSpec<Float> = spring(
        dampingRatio = 0.83f,
        stiffness = 320f
    )

    val FluidCapsuleSpringDp: androidx.compose.animation.core.SpringSpec<androidx.compose.ui.unit.Dp> = spring(
        dampingRatio = 0.83f,
        stiffness = 320f
    )

    val BallisticSwipeSpring: androidx.compose.animation.core.SpringSpec<Float> = spring(
        dampingRatio = 0.68f,
        stiffness = 220f
    )

    val SoftTouchSpring: androidx.compose.animation.core.SpringSpec<Float> = spring(
        dampingRatio = 0.92f,
        stiffness = 380f
    )
}

/**
 * Modifier that applies ColorOS 17 Living Flow kinetic touch compression.
 * Dynamically scales down to 0.965f on press and rebounds smoothly on release without layout shifts.
 */
fun Modifier.livingFlowTouch(
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    pressedScale: Float = 0.965f,
    reduceAnimations: Boolean = false
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reduceAnimations) pressedScale else 1.0f,
        animationSpec = if (reduceAnimations) spring(stiffness = Spring.StiffnessMediumLow) else LuminousMotionTokens.SoftTouchSpring,
        label = "livingFlowScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else Modifier
        )
}
