package off.kys.openarcade.core.domain.usecase

import off.kys.openarcade.core.domain.repository.GameRepository

class RemoveGameUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(packageName: String) =
        repository.deleteGame(packageName)
}
