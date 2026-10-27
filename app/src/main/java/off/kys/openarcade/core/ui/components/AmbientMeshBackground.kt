package off.kys.openarcade.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import off.kys.openarcade.core.ui.theme.GlassMeshCyan
import off.kys.openarcade.core.ui.theme.GlassMeshSapphire
import off.kys.openarcade.core.ui.theme.GlassMeshViolet

/**
 * ColorOS 17 & One UI 9 Aquamorphic Ambient Mesh Canvas.
 * Generates slow-moving luminous color orbs and aquatic refractions behind screen content.
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

    val p1 = if (reduceAnimations) 0.5f else pulse1
    val p2 = if (reduceAnimations) 0.5f else pulse2

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Orb 1: Upper-left Cyan glow
            val orb1Center = Offset(
                x = width * (0.2f + 0.15f * p1),
                y = height * (0.15f + 0.10f * p2)
            )
            val orb1Radius = width * (0.65f + 0.10f * p1)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryGlow.copy(alpha = 0.35f),
                        primaryGlow.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = orb1Center,
                    radius = orb1Radius
                ),
                radius = orb1Radius,
                center = orb1Center
            )

            // Orb 2: Mid-right Sapphire/Cobalt glow
            val orb2Center = Offset(
                x = width * (0.85f - 0.20f * p2),
                y = height * (0.45f + 0.15f * p1)
            )
            val orb2Radius = width * (0.75f + 0.12f * p2)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryGlow.copy(alpha = 0.32f),
                        secondaryGlow.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = orb2Center,
                    radius = orb2Radius
                ),
                radius = orb2Radius,
                center = orb2Center
            )

            // Orb 3: Lower-left Violet/Aurora glow
            val orb3Center = Offset(
                x = width * (0.35f + 0.20f * p2),
                y = height * (0.80f - 0.12f * p1)
            )
            val orb3Radius = width * (0.70f + 0.15f * p1)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        tertiaryGlow.copy(alpha = 0.32f),
                        tertiaryGlow.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = orb3Center,
                    radius = orb3Radius
                ),
                radius = orb3Radius,
                center = orb3Center
            )

            // Orb 4: Deep Midnight Indigo / Electric Purple Center Depth Glow
            val orb4Center = Offset(
                x = width * (0.50f + 0.10f * p1),
                y = height * (0.35f + 0.12f * p2)
            )
            val orb4Radius = width * 0.85f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x3B6366F1), // Deep Electric Indigo
                        Color(0x1F3B0764), // Rich Dark Purple
                        Color.Transparent
                    ),
                    center = orb4Center,
                    radius = orb4Radius
                ),
                radius = orb4Radius,
                center = orb4Center
            )
        }

        content()
    }
}
