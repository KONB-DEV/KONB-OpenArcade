package off.kys.openarcade.features.analytics.ui

sealed class AnalyticsUiEvent {
    data object RefreshRequested : AnalyticsUiEvent()
    data class IntervalSelected(val interval: AnalyticsInterval) : AnalyticsUiEvent()
    data class DarkModeChanged(val isDark: Boolean) : AnalyticsUiEvent()
    data object BackClicked : AnalyticsUiEvent()
}
