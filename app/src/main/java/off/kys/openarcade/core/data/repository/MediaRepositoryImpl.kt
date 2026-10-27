package off.kys.openarcade.core.data.repository

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.ui.graphics.Color
import off.kys.openarcade.core.domain.repository.MediaRepository
import off.kys.openarcade.core.util.ColorExtractor
import off.kys.openarcade.core.util.IconManager

class MediaRepositoryImpl(
    private val iconManager: IconManager,
    private val colorExtractor: ColorExtractor
) : MediaRepository {
    override suspend fun saveCustomIcon(packageName: String, uri: Uri): String? {
        return iconManager.saveCustomIcon(packageName, uri)
    }

    override suspend fun saveExtractedIcon(packageName: String, drawable: Drawable): String? {
        return iconManager.saveExtractedIcon(packageName, drawable)
    }

    override fun extractPrimaryColor(drawable: Drawable?): Color {
        return colorExtractor.extractPrimaryColor(drawable)
    }

    override fun getAdaptiveColor(color: Color, isDark: Boolean): Color {
        return colorExtractor.getAdaptiveColor(color, isDark)
    }

    override fun extractTop5ProminentColors(
        drawable: Drawable?,
        fallbackPrimary: Color?,
        isDark: Boolean
    ): List<Color> {
        return colorExtractor.extractTop5ProminentColors(drawable, fallbackPrimary, isDark)
    }
}
