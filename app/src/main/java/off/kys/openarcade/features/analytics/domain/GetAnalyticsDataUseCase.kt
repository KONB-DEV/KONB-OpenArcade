package off.kys.openarcade.features.analytics.domain

import off.kys.openarcade.core.domain.repository.GameRepository

class GetAnalyticsDataUseCase(private val repository: GameRepository) {
    operator fun invoke() = repository.getAnalyticsData()
}
