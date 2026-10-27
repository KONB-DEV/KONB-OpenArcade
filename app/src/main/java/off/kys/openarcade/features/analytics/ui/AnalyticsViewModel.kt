package off.kys.openarcade.features.analytics.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import off.kys.openarcade.core.data.local.preferences.ArcadePreferences
import off.kys.openarcade.core.domain.repository.MediaRepository
import off.kys.openarcade.features.analytics.domain.GetAnalyticsDataUseCase

class AnalyticsViewModel(
    getAnalyticsDataUseCase: GetAnalyticsDataUseCase,
    private val mediaRepository: MediaRepository,
    prefs: ArcadePreferences
) : ViewModel() {

    private val _selectedInterval = MutableStateFlow(AnalyticsInterval.DAILY)
    private val _isDark = MutableStateFlow(false)

    val uiState: StateFlow<AnalyticsUiState> = combine(
        getAnalyticsDataUseCase(),
        _selectedInterval,
        _isDark,
        prefs.showScrollbar
    ) { data, interval, isDark, showScrollbar ->
        val chartData = when (interval) {
            AnalyticsInterval.DAILY -> data.dailyTrend
            AnalyticsInterval.WEEKLY -> data.weeklyTrend
        }.toImmutableList()

        val topGamesWithColors = data.topGames.map { game ->
            TopGameUiModel(
                game = game,
                adaptiveColor = mediaRepository.getAdaptiveColor(
                    Color(game.primaryColorArgb),
                    isDark
                )
            )
        }.toImmutableList()

        AnalyticsUiState(
            isLoading = false,
            data = data,
            selectedInterval = interval,
            showScrollbar = showScrollbar,
            chartData = chartData,
            topGamesWithColors = topGamesWithColors
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )

    fun onEvent(event: AnalyticsUiEvent) {
        when (event) {
            is AnalyticsUiEvent.RefreshRequested -> {
                // Flow-based data will refresh when active
            }
            is AnalyticsUiEvent.IntervalSelected -> {
                _selectedInterval.value = event.interval
            }
            is AnalyticsUiEvent.DarkModeChanged -> {
                _isDark.value = event.isDark
            }
            is AnalyticsUiEvent.BackClicked -> { /* Handled by UI */ }
        }
    }
}
