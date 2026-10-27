package off.kys.openarcade.features.games_launcher.domain

import off.kys.openarcade.core.domain.repository.GameRepository

class RefreshAllGameStatsUseCase(private val repository: GameRepository) {
    suspend operator fun invoke() = repository.refreshAllGameStats()
}
