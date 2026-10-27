package off.kys.openarcade.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import off.kys.openarcade.core.data.local.preferences.ArcadePreferences
import off.kys.openarcade.core.domain.usecase.GetGamesUseCase
import off.kys.openarcade.core.domain.usecase.GetSystemStatusUseCase
import off.kys.openarcade.core.domain.usecase.RefreshGamesUseCase
import off.kys.openarcade.features.games_launcher.domain.RefreshAllGameStatsUseCase

class AppViewModel(
    prefs: ArcadePreferences,
    getGamesUseCase: GetGamesUseCase,
    getSystemStatusUseCase: GetSystemStatusUseCase,
    private val refreshGamesUseCase: RefreshGamesUseCase,
    private val refreshAllGameStatsUseCase: RefreshAllGameStatsUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(true)
    private val _forcedOrientation = MutableStateFlow<Int?>(null)

    val appState: StateFlow<AppState> = combine(
        getGamesUseCase(),
        getSystemStatusUseCase(),
        prefs.immersiveMode,
        prefs.keepScreenOn,
        prefs.reduceAnimations,
        prefs.launchAnimation,
        prefs.screenOrientation,
        _isRefreshing,
        _forcedOrientation
    ) { params: Array<Any?> ->
        @Suppress("UNCHECKED_CAST")
        val games = params[0] as List<off.kys.openarcade.core.domain.model.GameEntry>
        val status = params[1] as off.kys.openarcade.core.domain.model.SystemStatus
        val immersive = params[2] as Boolean
        val keepOn = params[3] as Boolean
        val reduceAnim = params[4] as Boolean
        val launchAnim = params[5] as Boolean
        val orientation = params[6] as off.kys.openarcade.features.settings.ui.ScreenOrientation
        val refreshing = params[7] as Boolean
        val forcedOrientation = params[8] as Int?

        AppStateData(
            data = GameCoreData(
                games = games,
                systemStatus = status,
                immersiveMode = immersive,
                keepScreenOn = keepOn,
                reduceAnimations = reduceAnim,
                launchAnimation = launchAnim,
                screenOrientation = orientation,
                forcedOrientation = forcedOrientation
            ),
            isRefreshing = refreshing,
            hasGames = games.isNotEmpty()
        )
    }.map { stateData ->
        if (stateData.isRefreshing && !stateData.hasGames) {
            AppState.Loading
        } else {
            AppState.Ready(stateData.data)
        }
    }.onStart {
        emit(AppState.Loading)
    }.catch { e ->
        emit(AppState.Error(e.message ?: "Unknown Error"))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppState.Loading
    )

    init {
        initialLoad()
    }

    private fun initialLoad() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshGamesUseCase()
                refreshAllGameStatsUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun setForcedOrientation(orientation: Int?) {
        _forcedOrientation.value = orientation
    }

    private data class AppStateData(
        val data: GameCoreData,
        val isRefreshing: Boolean,
        val hasGames: Boolean
    )

    private data class Sextuple<A, B, C, D, E, F>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D,
        val fifth: E,
        val sixth: F
    )
}
