package off.kys.openarcade.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutExpo
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import off.kys.openarcade.core.ui.theme.LuminousMotionTokens
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import off.kys.openarcade.core.domain.model.GameEntry
import kotlin.time.Duration.Companion.milliseconds

private val IconSize = 120.dp
private val InnerIconSize = 80.dp
private val HaloBaseSize = 160.dp
private val RingGap = 48.dp
private val ScanBarWidth = 140.dp
private val ScanBarHeight = 2.dp
private val ProgressBarWidth = 180.dp
private val ProgressBarHeight = 3.dp

@Composable
fun LaunchOverlay(
    game: GameEntry?,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    reduceAnimations: Boolean = false,
) {
    val primary = game?.getPrimaryColor() ?: MaterialTheme.colorScheme.primary
    val tertiary = game?.getTertiaryColor() ?: MaterialTheme.colorScheme.tertiary

    var phase by remember { mutableIntStateOf(0) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            phase = 1
            delay(300.milliseconds)
            phase = 2
            delay(200.milliseconds)
            phase = 3
        } else {
            phase = 0
        }
    }

    val iconScale by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0.55f,
        animationSpec = if (reduceAnimations) tween(300) else LuminousMotionTokens.OneTakeLaunchSpring,
        label = "iconScale"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (phase >= 1) 1f else 0f,
        animationSpec = tween(400),
        label = "iconAlpha"
    )

    val titleAlpha by animateFloatAsState(
        targetValue = if (phase >= 2) 1f else 0f,
        animationSpec = tween(350, delayMillis = 80),
        label = "titleAlpha"
    )
    val titleSlide by animateFloatAsState(
        targetValue = if (phase >= 2) 0f else 18f,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "titleSlide"
    )

    val progressAnim = remember { Animatable(0f) }
    LaunchedEffect(phase) {
        if (phase >= 2) {
            progressAnim.snapTo(0f)
            progressAnim.animateTo(1f, tween(1400, easing = EaseInOutExpo))
        } else {
            progressAnim.snapTo(0f)
        }
    }

    val infinite = rememberInfiniteTransition(label = "launchLoops")

    val haloAlpha by if (reduceAnimations) {
        animateFloatAsState(0.40f, label = "haloAlphaStatic")
    } else {
        infinite.animateFloat(
            initialValue = 0.30f,
            targetValue = 0.65f,
            animationSpec = infiniteRepeatable(
                tween(1800, easing = EaseInOutSine),
                RepeatMode.Reverse
            ),
            label = "haloAlpha"
        )
    }

    val haloScale by if (reduceAnimations) {
        animateFloatAsState(1.00f, label = "haloScaleStatic")
    } else {
        infinite.animateFloat(
            initialValue = 1.00f,
            targetValue = 1.12f,
            animationSpec = infiniteRepeatable(
                tween(1800, easing = EaseInOutSine),
                RepeatMode.Reverse
            ),
            label = "haloScale"
        )
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            initialScale = 0.88f,
            animationSpec = if (reduceAnimations) tween(200) else LuminousMotionTokens.OneTakeLaunchSpring
        ) + fadeIn(tween(250)),
        exit = scaleOut(
            targetScale = 1.06f,
            animationSpec = if (reduceAnimations) tween(200) else LuminousMotionTokens.OneTakeLaunchSpring
        ) + fadeOut(tween(350)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF070B14))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primary.copy(alpha = 0.35f),
                            primary.copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        radius = 900f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .size(HaloBaseSize * 3.5f)
                    .align(Alignment.Center)
                    .scale(haloScale)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                primary.copy(alpha = if (phase >= 3) haloAlpha * 0.25f else 0f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .size(HaloBaseSize)
                    .align(Alignment.Center)
                    .scale(if (phase >= 3) haloScale else 1f)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                primary.copy(alpha = if (phase >= 1) haloAlpha * 0.45f else 0f),
                                primary.copy(alpha = if (phase >= 1) haloAlpha * 0.12f else 0f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .scale(iconScale)
                        .alpha(iconAlpha)
                ) {
                    ArcadeGameIcon(
                        icon = game?.customIconPath
                            ?: game?.cachedIconPath
                            ?: game?.packageName ?: "",
                        contentDescription = null,
                        modifier = Modifier.size(IconSize),
                        primaryColor = primary,
                        tertiaryColor = tertiary,
                        iconSize = InnerIconSize,
                        showContainerBorder = false,
                        showBackgroundBloom = true
                    )
                }

                Spacer(Modifier.height(28.dp))

                Text(
                    text = game?.displayName ?: "",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .alpha(titleAlpha)
                        .padding(bottom = titleSlide.dp)
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = game?.packageName ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = primary.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alpha(titleAlpha)
                )

                Spacer(Modifier.height(36.dp))

                LaunchProgressBar(
                    progress = progressAnim.value,
                    primary = primary,
                    tertiary = tertiary,
                    modifier = Modifier
                        .width(ProgressBarWidth)
                        .alpha(if (phase >= 2) 1f else 0f)
                )

                Spacer(Modifier.height(14.dp))

                LaunchStatusLabel(
                    progress = progressAnim.value,
                    primary = primary,
                    modifier = Modifier.alpha(if (phase >= 2) titleAlpha else 0f)
                )
            }
        }
    }
}

@Composable
private fun LaunchProgressBar(
    progress: Float,
    primary: Color,
    tertiary: Color,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(ProgressBarHeight)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(primary.copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(ProgressBarHeight)
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            primary.copy(alpha = 0.95f),
                            tertiary.copy(alpha = 0.70f)
                        )
                    )
                )
        )
        if (progress > 0.02f && progress < 0.99f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(ProgressBarHeight),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                )
            }
        }
    }
}

@Composable
private fun LaunchStatusLabel(
    progress: Float,
    primary: Color,
    modifier: Modifier = Modifier
) {
    val label = when {
        progress < 0.65f -> "LOADING"
        progress < 0.90f -> "STARTING GAME"
        else -> "READY"
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 3.sp
        ),
        color = primary.copy(alpha = 0.85f),
        modifier = modifier
    )
}