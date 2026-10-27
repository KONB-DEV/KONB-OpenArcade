package off.kys.openarcade.ui.main

import off.kys.openarcade.core.domain.model.GameEntry
import off.kys.openarcade.core.domain.model.SystemStatus
import off.kys.openarcade.features.settings.ui.ScreenOrientation

data class GameCoreData(
    val games: List<GameEntry>,
    val systemStatus: SystemStatus,
    val immersiveMode: Boolean,
    val keepScreenOn: Boolean,
    val screenOrientation: ScreenOrientation,
    val forcedOrientation: Int? = null,
    val reduceAnimations: Boolean,
    val launchAnimation: Boolean
)

sealed interface AppState {
    data object Loading : AppState
    data class Ready(val data: GameCoreData) : AppState
    data class Error(val message: String) : AppState
}
