package off.kys.openarcade.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import off.kys.openarcade.core.domain.model.GameEntry
import off.kys.openarcade.core.domain.repository.GameRepository

class GetGamesUseCase(private val repository: GameRepository) {
    operator fun invoke(): Flow<List<GameEntry>> = repository.getGames()
}
