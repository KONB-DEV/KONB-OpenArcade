package off.kys.openarcade.core.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Continuous Curvature Squircle Shape (Apple / One UI continuous curvature superellipse).
 * Eliminates the abrupt tangent angle transition found in standard rounded rectangles,
 * producing an ultra-smooth, organic squircle contour for modern Bento grid systems.
 */
class SquircleShape(
    val radius: Dp = 24.dp,
    val smoothing: Float = 0.65f
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val maxRadius = minOf(size.width, size.height) / 2f
        if (maxRadius <= 0f) {
            return Outline.Rectangle(Rect(Offset.Zero, size))
        }

        val r = with(density) { radius.toPx() }.coerceIn(0f, maxRadius)
        if (r <= 0f) {
            return Outline.Rectangle(Rect(Offset.Zero, size))
        }

        val w = size.width
        val h = size.height

        // Continuous curvature extension length (Apple squircle factor: 1.32 - 1.45)
        val s = (r * (1f + smoothing * 0.40f)).coerceAtMost(maxRadius)
        // Control offset constant for smooth cubic bezier curvature
        val k = s * 0.52f

        val path = Path().apply {
            // Start at top straight edge before top-right corner
            moveTo(s, 0f)
            lineTo(w - s, 0f)

            // Top-right continuous squircle corner
            cubicTo(
                w - s + k, 0f,
                w, s - k,
                w, s
            )
            lineTo(w, h - s)

            // Bottom-right continuous squircle corner
            cubicTo(
                w, h - s + k,
                w - s + k, h,
                w - s, h
            )
            lineTo(s, h)

            // Bottom-left continuous squircle corner
            cubicTo(
                s - k, h,
                0f, h - s + k,
                0f, h - s
            )
            lineTo(0f, s)

            // Top-left continuous squircle corner
            cubicTo(
                0f, s - k,
                s - k, 0f,
                s, 0f
            )
            close()
        }

        return Outline.Generic(path)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SquircleShape) return false
        return radius == other.radius && smoothing == other.smoothing
    }

    override fun hashCode(): Int {
        var result = radius.hashCode()
        result = 31 * result + smoothing.hashCode()
        return result
    }
}

/**
 * Unified Bento Design Tokens for consistent squircle radii and padding across the arcade dashboard.
 */
object BentoTokens {
    // Squircle Shapes
    val HeroShape = SquircleShape(radius = 32.dp, smoothing = 0.70f)
    val LargeTileShape = SquircleShape(radius = 26.dp, smoothing = 0.65f)
    val MediumTileShape = SquircleShape(radius = 22.dp, smoothing = 0.65f)
    val SmallBadgeShape = SquircleShape(radius = 14.dp, smoothing = 0.60f)
    val PillShape = CircleShape

    // Consistent Bento Spacing & Padding
    val GridOuterPadding = 16.dp
    val GridGutter = 12.dp
    val CardInnerPadding = 16.dp
    val CompactInnerPadding = 12.dp
    val BentoSectionSpacing = 16.dp
}
