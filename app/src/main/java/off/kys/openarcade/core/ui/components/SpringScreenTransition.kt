package off.kys.openarcade.core.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransition
import off.kys.openarcade.core.util.HapticFeedbackService
import off.kys.openarcade.core.util.LocalHapticFeedbackService

/**
 * Samsung One UI 9 Global Fluid Kinetic Motion Navigation Transition.
 * Employs custom physics-based spring dynamics (F = -kx - cv) tuned with
 * an organic deceleration curve (stiffness = 320f, dampingRatio = 0.88f).
 * Eliminates mechanical lag and rigid linear easing through parallel layer
 * scaling (0.975f -> 1.0f) and natural 28% proportional parallax displacement.
 */
@Composable
fun SpringScreenTransition(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    reduceAnimations: Boolean = false,
    hapticService: HapticFeedbackService = LocalHapticFeedbackService.current
) {
    LaunchedEffect(navigator.lastItem) {
        try {
            hapticService.navigationTransition()
        } catch (_: Throwable) {}
    }

    val kineticOffsetSpring = androidx.compose.runtime.remember {
        spring<IntOffset>(dampingRatio = 0.88f, stiffness = 320f)
    }
    val kineticScaleSpring = androidx.compose.runtime.remember {
        spring<Float>(dampingRatio = 0.88f, stiffness = 340f)
    }
    val kineticAlphaSpring = androidx.compose.runtime.remember {
        spring<Float>(dampingRatio = 1.0f, stiffness = 400f)
    }

    ScreenTransition(
        navigator = navigator,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E1A)),
        transition = {
            if (reduceAnimations) {
                (fadeIn(spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)) togetherWith
                        fadeOut(spring(stiffness = Spring.StiffnessMediumLow, dampingRatio = Spring.DampingRatioNoBouncy)))
            } else {
                val isPush = navigator.lastEvent != StackEvent.Pop

                val enterTransition = if (isPush) {
                    slideInHorizontally(
                        animationSpec = kineticOffsetSpring,
                        initialOffsetX = { fullWidth -> (fullWidth * 0.28f).toInt() }
                    ) + fadeIn(
                        animationSpec = kineticAlphaSpring
                    ) + scaleIn(
                        initialScale = 0.975f,
                        animationSpec = kineticScaleSpring
                    )
                } else {
                    slideInHorizontally(
                        animationSpec = kineticOffsetSpring,
                        initialOffsetX = { fullWidth -> (-fullWidth * 0.10f).toInt() }
                    ) + fadeIn(
                        animationSpec = kineticAlphaSpring
                    ) + scaleIn(
                        initialScale = 0.985f,
                        animationSpec = kineticScaleSpring
                    )
                }

                val exitTransition = if (isPush) {
                    slideOutHorizontally(
                        animationSpec = kineticOffsetSpring,
                        targetOffsetX = { fullWidth -> (-fullWidth * 0.10f).toInt() }
                    ) + fadeOut(
                        animationSpec = kineticAlphaSpring
                    ) + scaleOut(
                        targetScale = 0.985f,
                        animationSpec = kineticScaleSpring
                    )
                } else {
                    slideOutHorizontally(
                        animationSpec = kineticOffsetSpring,
                        targetOffsetX = { fullWidth -> (fullWidth * 0.28f).toInt() }
                    ) + fadeOut(
                        animationSpec = kineticAlphaSpring
                    ) + scaleOut(
                        targetScale = 0.975f,
                        animationSpec = kineticScaleSpring
                    )
                }

                enterTransition togetherWith exitTransition
            }
        }
    )
}


