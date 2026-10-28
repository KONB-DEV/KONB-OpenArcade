package off.kys.openarcade.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import off.kys.openarcade.core.ui.theme.GlassMeshCyan
import off.kys.openarcade.core.ui.theme.GlassMeshSapphire
import off.kys.openarcade.core.ui.theme.GlassMeshViolet

/**
 * ColorOS 17 & One UI 9 Aquamorphic Ambient Mesh Canvas with Glowing Arcade Crest.
 *
 * Generates GPU-accelerated slow-moving luminous color orbs and aquatic refractions behind screen content,
 * reactive to the currently active/featured game's color palette with an ambient OpenArcade console watermark.
 */
@Composable
fun AmbientMeshBackground(
    modifier: Modifier = Modifier,
    primaryGlow: Color = GlassMeshCyan,
    secondaryGlow: Color = GlassMeshSapphire,
    tertiaryGlow: Color = GlassMeshViolet,
    reduceAnimations: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "meshAtmosphere")

    val pulse1State = if (reduceAnimations) {
        null
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 14000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "meshPulse1"
        )
    }

    val pulse2State = if (reduceAnimations) {
        null
    } else {
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 18000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "meshPulse2"
        )
    }

    val crestBreathingState = if (reduceAnimations) {
        null
    } else {
        infiniteTransition.animateFloat(
            initialValue = 0.045f,
            targetValue = 0.085f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 6000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "crestBreathing"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hardware-accelerated GPU draw phase canvas with zero Composable tree invalidations
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val width = size.width
                    val height = size.height

                    // Pre-calculate cached geometry once per layout size
                    val centerX = width * 0.5f
                    val centerY = height * 0.22f
                    val crestW = width * 0.32f
                    val crestH = crestW * 0.65f

                    val crestPath = Path().apply {
                        addRoundRect(
                            RoundRect(
                                left = centerX - crestW * 0.5f,
                                top = centerY - crestH * 0.5f,
                                right = centerX + crestW * 0.5f,
                                bottom = centerY + crestH * 0.5f,
                                cornerRadius = CornerRadius(crestH * 0.38f, crestH * 0.38f)
                            )
                        )
                    }

                    val dpadX = centerX - width * 0.08f
                    val dpadSize = width * 0.045f
                    val dpadThick = width * 0.015f
                    val dpadTopLeft1 = Offset(dpadX - dpadSize * 0.5f, centerY - dpadThick * 0.5f)
                    val dpadSize1 = Size(dpadSize, dpadThick)
                    val dpadTopLeft2 = Offset(dpadX - dpadThick * 0.5f, centerY - dpadSize * 0.5f)
                    val dpadSize2 = Size(dpadThick, dpadSize)

                    val btnX = centerX + width * 0.07f
                    val btnRadius = width * 0.016f
                    val btnCenter1 = Offset(btnX, centerY - btnRadius * 1.1f)
                    val btnCenter2 = Offset(btnX + btnRadius * 2.2f, centerY + btnRadius * 0.8f)

                    val strokeCrest = Stroke(width = 2.5f)
                    val strokeBtn = Stroke(width = 1.8f)

                    onDrawBehind {
                        val p1 = pulse1State?.value ?: 0.5f
                        val p2 = pulse2State?.value ?: 0.5f
                        val crestAlpha = crestBreathingState?.value ?: 0.05f

                        // Layer 1: Point 1 - Deep Aquamorphic Teal Void
                        val p1Center = Offset(
                            x = width * (0.25f + 0.12f * p1),
                            y = height * (0.20f + 0.08f * p2)
                        )
                        val p1Radius = width * (0.85f + 0.08f * p1)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF0D3B4C).copy(alpha = 0.45f),
                                    Color(0xFF121E36).copy(alpha = 0.28f),
                                    Color.Transparent
                                ),
                                center = p1Center,
                                radius = p1Radius
                            ),
                            radius = p1Radius,
                            center = p1Center
                        )

                        // Point 2: Abyssal Navy Core
                        val p2Center = Offset(
                            x = width * (0.80f - 0.15f * p2),
                            y = height * (0.45f + 0.10f * p1)
                        )
                        val p2Radius = width * (0.90f + 0.10f * p2)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF121E36).copy(alpha = 0.60f),
                                    Color(0xFF0A0E1A).copy(alpha = 0.35f),
                                    Color.Transparent
                                ),
                                center = p2Center,
                                radius = p2Radius
                            ),
                            radius = p2Radius,
                            center = p2Center
                        )

                        // Point 3: Reactive Game Tint Apex Glow (interpolates with primaryGlow)
                        val p3Center = Offset(
                            x = width * (0.45f + 0.18f * p2),
                            y = height * (0.75f - 0.14f * p1)
                        )
                        val p3Radius = width * (0.80f + 0.12f * p1)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF0D3B4C).copy(alpha = 0.40f),
                                    primaryGlow.copy(alpha = 0.22f),
                                    Color.Transparent
                                ),
                                center = p3Center,
                                radius = p3Radius
                            ),
                            radius = p3Radius,
                            center = p3Center
                        )

                        // Point 4: Sapphire & Aurora dynamic accent
                        val orbAccentCenter = Offset(
                            x = width * (0.60f + 0.08f * p1),
                            y = height * (0.30f + 0.10f * p2)
                        )
                        val orbAccentRadius = width * 0.70f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    secondaryGlow.copy(alpha = 0.24f),
                                    tertiaryGlow.copy(alpha = 0.14f),
                                    Color.Transparent
                                ),
                                center = orbAccentCenter,
                                radius = orbAccentRadius
                            ),
                            radius = orbAccentRadius,
                            center = orbAccentCenter
                        )

                        // Layer 2: Luminous Arcade Logo Crest Watermark
                        val crestColor = primaryGlow.copy(alpha = crestAlpha)
                        drawPath(path = crestPath, color = crestColor, style = strokeCrest)
                        drawRect(color = crestColor, topLeft = dpadTopLeft1, size = dpadSize1)
                        drawRect(color = crestColor, topLeft = dpadTopLeft2, size = dpadSize2)
                        drawCircle(color = crestColor, radius = btnRadius, center = btnCenter1, style = strokeBtn)
                        drawCircle(color = crestColor, radius = btnRadius, center = btnCenter2, style = strokeBtn)
                    }
                }
        )

        content()
    }
}
