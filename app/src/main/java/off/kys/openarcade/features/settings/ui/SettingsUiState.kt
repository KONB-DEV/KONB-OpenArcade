package off.kys.openarcade.features.settings.ui

data class SettingsUiState(
    val immersiveMode: Boolean = false,
    val keepScreenOn: Boolean = false,
    val showScrollbar: Boolean = true,
    val reduceAnimations: Boolean = false,
    val showFavoritesSection: Boolean = true,
    val showAnalyticsSection: Boolean = true,
    val showRecentSection: Boolean = true,
    val showSystemStatus: Boolean = true,
    val screenOrientation: ScreenOrientation = ScreenOrientation.Auto,
    val gridColumns: GridColumns = GridColumns.Three,
    val hapticFeedback: Boolean = true,
    val launchAnimation: Boolean = true,
    val skipGameDetails: Boolean = false,
)