package off.kys.openarcade.features.games_launcher.domain

import off.kys.openarcade.core.domain.repository.GameRepository

class UpdateGameCategoryUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(packageName: String, customCategories: List<String>) =
        repository.updateCustomCategories(packageName, customCategories)
}
