package off.kys.openarcade.features.game_details.domain

import off.kys.openarcade.core.domain.repository.SystemRepository

class LaunchGameUseCase(private val repository: SystemRepository) {
    operator fun invoke(packageName: String) = repository.launchGame(packageName)
    fun getGameOrientation(packageName: String) = repository.getGameOrientation(packageName)
}
