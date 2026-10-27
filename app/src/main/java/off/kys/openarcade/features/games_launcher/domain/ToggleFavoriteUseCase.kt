package off.kys.openarcade.features.games_launcher.domain

import off.kys.openarcade.core.domain.repository.GameRepository

class ToggleFavoriteUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(packageName: String, isFavorite: Boolean) =
        repository.updateFavoriteStatus(packageName, isFavorite)
}
