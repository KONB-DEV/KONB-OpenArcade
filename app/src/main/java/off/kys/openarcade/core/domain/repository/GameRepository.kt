package off.kys.openarcade.core.domain.repository

import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import off.kys.openarcade.core.domain.model.AnalyticsData
import off.kys.openarcade.core.domain.model.GameEntry

interface GameRepository {
    fun getGames(): Flow<ImmutableList<GameEntry>>
    fun getGameByPackageName(packageName: String): Flow<GameEntry?>
    suspend fun refreshGames()
    suspend fun refreshGameStats(packageName: String)
    suspend fun refreshAllGameStats()
    suspend fun updateCustomCategories(packageName: String, customCategories: List<String>)
    suspend fun updateFavoriteStatus(packageName: String, isFavorite: Boolean)
    suspend fun updateCustomTitle(packageName: String, customTitle: String?)
    suspend fun updateCustomIconPath(packageName: String, customIconPath: String?)
    suspend fun deleteGame(packageName: String)
    suspend fun addGame(game: GameEntry)
    fun getAnalyticsData(): Flow<AnalyticsData>
}
