package off.kys.openarcade.core.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class AnalyticsData(
    val totalPlayTimeMs: Long,
    val dailyTrend: ImmutableList<PlayTimePoint>,
    val weeklyTrend: ImmutableList<PlayTimePoint>,
    val categoryDistribution: ImmutableList<CategoryDistribution>,
    val topGames: ImmutableList<TopGame>
)

@Immutable
data class PlayTimePoint(
    val label: String,
    val playTimeMs: Long,
    val timestamp: Long
)

@Immutable
data class CategoryDistribution(
    val category: GameCategory,
    val playTimeMs: Long,
    val percentage: Float
)

@Immutable
data class TopGame(
    val packageName: String,
    val title: String,
    val playTimeMs: Long,
    val primaryColorArgb: Int
)
