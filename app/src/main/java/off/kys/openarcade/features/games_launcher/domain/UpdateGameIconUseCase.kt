package off.kys.openarcade.features.games_launcher.domain

import off.kys.openarcade.core.domain.repository.GameRepository

class UpdateGameIconUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(packageName: String, newIconPath: String?) =
        repository.updateCustomIconPath(packageName, newIconPath)
}
