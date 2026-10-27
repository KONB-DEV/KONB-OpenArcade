package off.kys.openarcade.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AppInfo(
    val packageName: String,
    val label: String
)
