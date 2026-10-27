package off.kys.openarcade.core.domain.usecase

import off.kys.openarcade.core.domain.model.GameEntry
import off.kys.openarcade.core.domain.repository.GameRepository

class AddGamesUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(packageNames: List<String>) {
        packageNames.forEach { pkg ->
            repository.addGame(
                GameEntry(
                    packageName = pkg,
                    title = "",
                    category = off.kys.openarcade.core.domain.model.GameCategory.UTILITY,
                    primaryColorArgb = 0,
                    onPrimaryColorArgb = 0,
                    secondaryColorArgb = 0,
                    tertiaryColorArgb = 0,
                    isManuallyAdded = true
                )
            )
        }
    }
}
