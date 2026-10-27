package off.kys.openarcade.core.domain.usecase

import off.kys.openarcade.core.domain.repository.GameRepository

class RefreshGamesUseCase(private val repository: GameRepository) {
    suspend operator fun invoke() = repository.refreshGames()
}
