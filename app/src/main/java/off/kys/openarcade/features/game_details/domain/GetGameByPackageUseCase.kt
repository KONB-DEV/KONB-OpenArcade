package off.kys.openarcade.features.game_details.domain

import kotlinx.coroutines.flow.Flow
import off.kys.openarcade.core.domain.model.GameEntry
import off.kys.openarcade.core.domain.repository.GameRepository

class GetGameByPackageUseCase(private val repository: GameRepository) {
    operator fun invoke(packageName: String): Flow<GameEntry?> = 
        repository.getGameByPackageName(packageName)
}
