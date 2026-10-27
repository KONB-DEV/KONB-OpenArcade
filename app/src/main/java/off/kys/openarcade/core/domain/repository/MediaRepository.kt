package off.kys.openarcade.core.domain.repository

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.ui.graphics.Color

interface MediaRepository {
    suspend fun saveCustomIcon(packageName: String, uri: Uri): String?
    suspend fun saveExtractedIcon(packageName: String, drawable: Drawable): String?
    fun extractPrimaryColor(drawable: Drawable?): Color
    fun getAdaptiveColor(color: Color, isDark: Boolean): Color
    fun extractTop5ProminentColors(
        drawable: Drawable?,
        fallbackPrimary: Color? = null,
        isDark: Boolean = true
    ): List<Color>
}
