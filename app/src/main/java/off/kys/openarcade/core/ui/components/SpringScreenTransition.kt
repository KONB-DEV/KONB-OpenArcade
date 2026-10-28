package off.kys.openarcade.core.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransition
import off.kys.openarcade.core.util.HapticFeedbackService
import off.kys.openarcade.core.util.LocalHapticFeedbackService

/**
 * Samsung One UI 9 Fluid Kinetic Motion Navigation Transition.
 * Uses the signature non-linear organic deceleration curve Cubic-Bezier(0.19, 1.0, 0.22, 1.0)
 * over 360ms with subtle scale and proportional parallax offset for silky-smooth 60/120fps transitions.
 */
@Composable
fun SpringScreenTransition(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    reduceAnimations: Boolean = false,
    hapticService: HapticFeedbackService = LocalHapticFeedbackService.current
) {
    LaunchedEffect(navigator.lastItem) {
        hapticService.navigationTransition()
    }

    ScreenTransition(
        navigator = navigator,
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E1A)),
        transition = {
            if (reduceAnimations) {
                fadeIn(tween(200, easing = LinearOutSlowInEasing)) togetherWith
                        fadeOut(tween(180, easing = LinearOutSlowInEasing))
            } else {
                val isPush = navigator.lastEvent != StackEvent.Pop

                // Samsung One UI 9 Primary Interface Deceleration Curve
                val oneUi9Decel = CubicBezierEasing(0.19f, 1.0f, 0.22f, 1.0f)
                val duration = 360
                val offsetSpec = tween<IntOffset>(duration, easing = oneUi9Decel)
                val scaleSpec = tween<Float>(duration, easing = oneUi9Decel)
                val fadeSpec = tween<Float>((duration * 0.75f).toInt(), easing = LinearOutSlowInEasing)

                val enterTransition = if (isPush) {
                    slideInHorizontally(
                        animationSpec = offsetSpec,
                        initialOffsetX = { fullWidth -> (fullWidth * 0.22f).toInt() }
                    ) + fadeIn(
                        animationSpec = fadeSpec
                    ) + scaleIn(
                        initialScale = 0.98f,
                        animationSpec = scaleSpec
                    )
                } else {
                    slideInHorizontally(
                        animationSpec = offsetSpec,
                        initialOffsetX = { fullWidth -> (-fullWidth * 0.12f).toInt() }
                    ) + fadeIn(
                        animationSpec = fadeSpec
                    ) + scaleIn(
                        initialScale = 0.985f,
                        animationSpec = scaleSpec
                    )
                }

                val exitTransition = if (isPush) {
                    slideOutHorizontally(
                        animationSpec = offsetSpec,
                        targetOffsetX = { fullWidth -> (-fullWidth * 0.12f).toInt() }
                    ) + fadeOut(
                        animationSpec = fadeSpec
                    ) + scaleOut(
                        targetScale = 0.985f,
                        animationSpec = scaleSpec
                    )
                } else {
                    slideOutHorizontally(
                        animationSpec = offsetSpec,
                        targetOffsetX = { fullWidth -> (fullWidth * 0.22f).toInt() }
                    ) + fadeOut(
                        animationSpec = fadeSpec
                    ) + scaleOut(
                        targetScale = 0.98f,
                        animationSpec = scaleSpec
                    )
                }

                enterTransition togetherWith exitTransition
            }
        }
    )
}

