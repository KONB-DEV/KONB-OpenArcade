package off.kys.openarcade.core.util

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.createBitmap
import androidx.palette.graphics.Palette

class ColorExtractor {
    fun extractPrimaryColor(drawable: Drawable?): Color {
        if (drawable == null) return Color(0xFF2A2A2A)
        
        val bitmap = try {
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 100
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 100
            val bitmap = createBitmap(width, height)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (_: Exception) {
            null
        } ?: return Color(0xFF2A2A2A)

        val palette = Palette.from(bitmap).generate()
        
        val colorInt = palette.getDominantColor(
            palette.getVibrantColor(
                palette.getMutedColor(Color(0xFF2A2A2A).toArgb())
            )
        )
        
        return Color(colorInt)
    }

    fun getAdaptiveColor(color: Color, isDark: Boolean): Color {
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(color.toArgb(), hsl)

        if (isDark) {
            if (hsl[2] < 0.65f) {
                hsl[2] = 0.65f
            }
        } else {
            if (hsl[2] > 0.45f) {
                hsl[2] = 0.45f
            }
        }

        return Color(ColorUtils.HSLToColor(hsl))
    }

    /**
     * Extracts the top 5 prominent colors from a game's icon/artwork.
     * Generates a 5-stop harmonized palette tuned for dynamic oklch-style gradients
     * with a 15% chroma saturation boost as mandated by the One UI 9 specification.
     */
    fun extractTop5ProminentColors(
        drawable: Drawable?,
        fallbackPrimary: Color? = null,
        isDark: Boolean = true
    ): List<Color> {
        val baseSeed = fallbackPrimary ?: Color(0xFF00E5FF)
        val defaultColors = listOf(
            baseSeed,
            Color(0xFF00E676), // vibrant spring green
            Color(0xFF2979FF), // vibrant electric azure
            Color(0xFFD500F9), // vibrant violet
            Color(0xFFFF1744)  // vibrant magenta / rose
        )

        val bitmap = if (drawable != null) {
            try {
                val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 128
                val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 128
                val bmp = createBitmap(width, height)
                val canvas = Canvas(bmp)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bmp
            } catch (_: Exception) {
                null
            }
        } else null

        val palette = if (bitmap != null) {
            try {
                Palette.from(bitmap)
                    .maximumColorCount(32)
                    .generate()
            } catch (_: Exception) {
                null
            }
        } else null

        // Collect distinct swatches prioritizing population and chromance
        val candidateSwatches = mutableListOf<Palette.Swatch>()
        palette?.dominantSwatch?.let { candidateSwatches.add(it) }
        palette?.vibrantSwatch?.let { if (!candidateSwatches.contains(it)) candidateSwatches.add(it) }
        palette?.lightVibrantSwatch?.let { if (!candidateSwatches.contains(it)) candidateSwatches.add(it) }
        palette?.darkVibrantSwatch?.let { if (!candidateSwatches.contains(it)) candidateSwatches.add(it) }
        palette?.mutedSwatch?.let { if (!candidateSwatches.contains(it)) candidateSwatches.add(it) }
        palette?.lightMutedSwatch?.let { if (!candidateSwatches.contains(it)) candidateSwatches.add(it) }
        palette?.darkMutedSwatch?.let { if (!candidateSwatches.contains(it)) candidateSwatches.add(it) }

        palette?.swatches?.sortedByDescending { it.population }?.forEach { swatch ->
            if (candidateSwatches.size < 8 && !candidateSwatches.contains(swatch)) {
                candidateSwatches.add(swatch)
            }
        }

        val rawColors = candidateSwatches.map { Color(it.rgb) }.toMutableList()

        if (rawColors.isEmpty()) {
            if (drawable != null) {
                rawColors.add(extractPrimaryColor(drawable))
            } else if (fallbackPrimary != null) {
                rawColors.add(fallbackPrimary)
            } else {
                rawColors.addAll(defaultColors)
            }
        }

        // Harmonically synthesize up to 5 colors if the logo is monochrome or has few tones
        var angleOffset = 38f
        while (rawColors.size < 5) {
            val baseColor = rawColors.first()
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(baseColor.toArgb(), hsl)
            hsl[0] = (hsl[0] + angleOffset) % 360f
            hsl[1] = (hsl[1] * 1.15f).coerceIn(0.55f, 1.0f) // Boost chroma by 15%
            hsl[2] = if (isDark) 0.58f else 0.72f
            rawColors.add(Color(ColorUtils.HSLToColor(hsl)))
            angleOffset += 42f
        }

        // Tune all 5 stops for balanced perceptual luminescence and +15% chroma boost
        return rawColors.take(5).map { color ->
            val hsl = FloatArray(3)
            ColorUtils.colorToHSL(color.toArgb(), hsl)
            // One UI 9 saturation control matrix: boost underlying chroma by 15%
            hsl[1] = (hsl[1] * 1.15f).coerceIn(0.50f, 1.0f)
            if (isDark) {
                hsl[2] = hsl[2].coerceIn(0.48f, 0.70f)
            } else {
                hsl[2] = hsl[2].coerceIn(0.65f, 0.82f)
            }
            Color(ColorUtils.HSLToColor(hsl))
        }
    }
}
