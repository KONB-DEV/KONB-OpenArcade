package off.kys.openarcade.features.game_details.domain

import off.kys.openarcade.core.domain.repository.GameRepository

class RefreshGameStatsUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(packageName: String) =
        repository.refreshGameStats(packageName)
}
