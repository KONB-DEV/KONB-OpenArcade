package off.kys.openarcade.core.domain.usecase

import off.kys.openarcade.core.domain.repository.SystemRepository

class GetSystemStatusUseCase(private val repository: SystemRepository) {
    operator fun invoke() = repository.getSystemStatus()
}
