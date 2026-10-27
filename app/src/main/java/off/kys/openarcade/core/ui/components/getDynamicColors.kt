package off.kys.openarcade.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun getDynamicColors(): ImmutableList<Color> {
    val colorScheme = MaterialTheme.colorScheme

    return persistentListOf(
        colorScheme.primary,
        colorScheme.secondary,
        colorScheme.inversePrimary,
        colorScheme.primaryContainer,
        colorScheme.tertiary,
        colorScheme.error
    )
}
