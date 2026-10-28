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

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "meshPulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "meshPulse2"
    )

    val crestBreathing by infiniteTransition.animateFloat(
        initialValue = 0.045f,
        targetValue = 0.085f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crestBreathing"
    )

    val p1 = if (reduceAnimations) 0.5f else pulse1
    val p2 = if (reduceAnimations) 0.5f else pulse2
    val crestAlpha = if (reduceAnimations) 0.05f else crestBreathing

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // GPU-cached living bioluminescent canvas with watermark crest
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val width = size.width
                    val height = size.height

                    // Point 1: Deep Aquamorphic Teal Void
                    val p1Center = Offset(
                        x = width * (0.25f + 0.12f * p1),
                        y = height * (0.20f + 0.08f * p2)
                    )
                    val p1Radius = width * (0.85f + 0.08f * p1)
                    val brush1 = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0D3B4C).copy(alpha = 0.45f),
                            Color(0xFF121E36).copy(alpha = 0.28f),
                            Color.Transparent
                        ),
                        center = p1Center,
                        radius = p1Radius
                    )

                    // Point 2: Abyssal Navy Core
                    val p2Center = Offset(
                        x = width * (0.80f - 0.15f * p2),
                        y = height * (0.45f + 0.10f * p1)
                    )
                    val p2Radius = width * (0.90f + 0.10f * p2)
                    val brush2 = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF121E36).copy(alpha = 0.60f),
                            Color(0xFF0A0E1A).copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        center = p2Center,
                        radius = p2Radius
                    )

                    // Point 3: Reactive Game Tint Apex Glow (interpolates with primaryGlow)
                    val p3Center = Offset(
                        x = width * (0.45f + 0.18f * p2),
                        y = height * (0.75f - 0.14f * p1)
                    )
                    val p3Radius = width * (0.80f + 0.12f * p1)
                    val brush3 = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0D3B4C).copy(alpha = 0.40f),
                            primaryGlow.copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        center = p3Center,
                        radius = p3Radius
                    )

                    // Point 4: Sapphire & Aurora dynamic accent
                    val orbAccentCenter = Offset(
                        x = width * (0.60f + 0.08f * p1),
                        y = height * (0.30f + 0.10f * p2)
                    )
                    val orbAccentRadius = width * 0.70f
                    val brush4 = Brush.radialGradient(
                        colors = listOf(
                            secondaryGlow.copy(alpha = 0.24f),
                            tertiaryGlow.copy(alpha = 0.14f),
                            Color.Transparent
                        ),
                        center = orbAccentCenter,
                        radius = orbAccentRadius
                    )

                    // Ambient OpenArcade Crest Geometry
                    val crestPath = Path().apply {
                        val centerX = width * 0.5f
                        val centerY = height * 0.22f
                        val crestW = width * 0.32f
                        val crestH = crestW * 0.65f

                        // Rounded controller body outline
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

                    onDrawBehind {
                        // Layer 1: Ambient mesh gradients
                        drawCircle(brush = brush1, radius = p1Radius, center = p1Center)
                        drawCircle(brush = brush2, radius = p2Radius, center = p2Center)
                        drawCircle(brush = brush3, radius = p3Radius, center = p3Center)
                        drawCircle(brush = brush4, radius = orbAccentRadius, center = orbAccentCenter)

                        // Layer 2: Luminous Arcade Logo Crest Watermark
                        val crestColor = primaryGlow.copy(alpha = crestAlpha)
                        drawPath(
                            path = crestPath,
                            color = crestColor,
                            style = Stroke(width = 2.5f)
                        )

                        // D-Pad cross watermark
                        val cX = width * 0.5f
                        val cY = height * 0.22f
                        val dpadX = cX - width * 0.08f
                        val dpadSize = width * 0.045f
                        val dpadThick = width * 0.015f

                        drawRect(
                            color = crestColor,
                            topLeft = Offset(dpadX - dpadSize * 0.5f, cY - dpadThick * 0.5f),
                            size = Size(dpadSize, dpadThick)
                        )
                        drawRect(
                            color = crestColor,
                            topLeft = Offset(dpadX - dpadThick * 0.5f, cY - dpadSize * 0.5f),
                            size = Size(dpadThick, dpadSize)
                        )

                        // Dual Action Buttons watermark
                        val btnX = cX + width * 0.07f
                        val btnRadius = width * 0.016f
                        drawCircle(
                            color = crestColor,
                            radius = btnRadius,
                            center = Offset(btnX, cY - btnRadius * 1.1f),
                            style = Stroke(width = 1.8f)
                        )
                        drawCircle(
                            color = crestColor,
                            radius = btnRadius,
                            center = Offset(btnX + btnRadius * 2.2f, cY + btnRadius * 0.8f),
                            style = Stroke(width = 1.8f)
                        )
                    }
                }
        )

        content()
    }
}
