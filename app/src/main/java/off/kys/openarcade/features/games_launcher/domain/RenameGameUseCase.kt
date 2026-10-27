package off.kys.openarcade.features.games_launcher.domain

import off.kys.openarcade.core.domain.repository.GameRepository

class RenameGameUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(packageName: String, newTitle: String?) =
        repository.updateCustomTitle(packageName, newTitle)
}
