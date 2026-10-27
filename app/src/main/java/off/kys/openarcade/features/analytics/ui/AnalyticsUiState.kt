package off.kys.openarcade.features.analytics.ui

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import off.kys.openarcade.core.domain.model.AnalyticsData
import off.kys.openarcade.core.domain.model.PlayTimePoint
import off.kys.openarcade.core.domain.model.TopGame

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val data: AnalyticsData? = null,
    val selectedInterval: AnalyticsInterval = AnalyticsInterval.DAILY,
    val showScrollbar: Boolean = true,
    val chartData: ImmutableList<PlayTimePoint> = persistentListOf(),
    val topGamesWithColors: ImmutableList<TopGameUiModel> = persistentListOf()
)

data class TopGameUiModel(
    val game: TopGame,
    val adaptiveColor: androidx.compose.ui.graphics.Color
)

enum class AnalyticsInterval {
    DAILY, WEEKLY
}
