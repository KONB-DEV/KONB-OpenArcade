package off.kys.openarcade.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AquaLightColorScheme = lightColorScheme(
    primary = AquaPrimaryLight,
    onPrimary = AquaOnPrimaryLight,
    primaryContainer = AquaPrimaryContainerLight,
    onPrimaryContainer = AquaOnPrimaryContainerLight,

    secondary = AquaSecondaryLight,
    onSecondary = AquaOnSecondaryLight,
    secondaryContainer = AquaSecondaryContainerLight,
    onSecondaryContainer = AquaOnSecondaryContainerLight,

    tertiary = AquaTertiaryLight,
    onTertiary = AquaOnTertiaryLight,
    tertiaryContainer = AquaTertiaryContainerLight,
    onTertiaryContainer = AquaOnTertiaryContainerLight,

    error = AquaErrorLight,
    onError = AquaOnErrorLight,
    errorContainer = AquaErrorContainerLight,
    onErrorContainer = AquaOnErrorContainerLight,

    background = AquaBackgroundLight,
    onBackground = AquaOnBackgroundLight,

    surface = AquaSurfaceLight,
    onSurface = AquaOnSurfaceLight,
    surfaceVariant = AquaSurfaceVariantLight,
    onSurfaceVariant = AquaOnSurfaceVariantLight,

    outline = AquaOutlineLight,
    outlineVariant = AquaOutlineVariantLight,
)

private val AquaDarkColorScheme = darkColorScheme(
    primary = AquaPrimaryDark,
    onPrimary = AquaOnPrimaryDark,
    primaryContainer = AquaPrimaryContainerDark,
    onPrimaryContainer = AquaOnPrimaryContainerDark,

    secondary = AquaSecondaryDark,
    onSecondary = AquaOnSecondaryDark,
    secondaryContainer = AquaSecondaryContainerDark,
    onSecondaryContainer = AquaOnSecondaryContainerDark,

    tertiary = AquaTertiaryDark,
    onTertiary = AquaOnTertiaryDark,
    tertiaryContainer = AquaTertiaryContainerDark,
    onTertiaryContainer = AquaOnTertiaryContainerDark,

    error = AquaErrorDark,
    onError = AquaOnErrorDark,
    errorContainer = AquaErrorContainerDark,
    onErrorContainer = AquaOnErrorContainerDark,

    background = AquaBackgroundDark,
    onBackground = AquaOnBackgroundDark,

    surface = AquaSurfaceDark,
    onSurface = AquaOnSurfaceDark,
    surfaceVariant = AquaSurfaceVariantDark,
    onSurfaceVariant = AquaOnSurfaceVariantDark,

    outline = AquaOutlineDark,
    outlineVariant = AquaOutlineVariantDark,
)

@Composable
fun OpenArcadeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep dynamic color or allow our signature theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> AquaDarkColorScheme
        else -> AquaLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}