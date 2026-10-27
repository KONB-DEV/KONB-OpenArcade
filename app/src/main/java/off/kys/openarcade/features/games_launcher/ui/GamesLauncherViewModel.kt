package off.kys.openarcade.features.games_launcher.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import off.kys.openarcade.core.data.local.preferences.ArcadePreferences
import off.kys.openarcade.core.domain.model.GameCategory
import off.kys.openarcade.core.domain.model.GameEntry
import off.kys.openarcade.core.domain.model.GameFilter
import off.kys.openarcade.core.domain.model.GameSortOption
import off.kys.openarcade.core.domain.model.LauncherSection
import off.kys.openarcade.core.domain.usecase.AddGamesUseCase
import off.kys.openarcade.core.domain.usecase.RefreshGamesUseCase
import off.kys.openarcade.core.domain.usecase.RemoveGameUseCase
import off.kys.openarcade.features.game_details.domain.LaunchGameUseCase
import off.kys.openarcade.features.games_launcher.domain.RefreshAllGameStatsUseCase
import off.kys.openarcade.features.games_launcher.domain.RenameGameUseCase
import off.kys.openarcade.features.games_launcher.domain.ToggleFavoriteUseCase
import off.kys.openarcade.features.games_launcher.domain.UpdateGameCategoryUseCase
import off.kys.openarcade.features.games_launcher.domain.UpdateGameIconUseCase
import off.kys.openarcade.features.settings.ui.GridColumns
import off.kys.openarcade.ui.main.AppState
import off.kys.openarcade.ui.main.AppViewModel
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class GamesLauncherViewModel(
    private val refreshGamesUseCase: RefreshGamesUseCase,
    private val refreshAllGameStatsUseCase: RefreshAllGameStatsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val removeGameUseCase: RemoveGameUseCase,
    private val renameGameUseCase: RenameGameUseCase,
    private val updateGameIconUseCase: UpdateGameIconUseCase,
    private val addGamesUseCase: AddGamesUseCase,
    private val updateGameCategoryUseCase: UpdateGameCategoryUseCase,
    private val launchGameUseCase: LaunchGameUseCase,
    private val prefs: ArcadePreferences,
    private val appViewModel: AppViewModel
) : ViewModel() {

    private val selectedFilter = MutableStateFlow<GameFilter>(GameFilter.All)
    private val _tagState = MutableStateFlow(TagDialogState())
    private val _launchingGame = MutableStateFlow<GameEntry?>(null)

    private val settingsParamsFlow = combine(
        combine(
            prefs.gridColumns,
            prefs.showScrollbar,
            prefs.hapticFeedback
        ) { grid, scroll, haptic -> Triple(grid, scroll, haptic) },
        combine(
            prefs.skipGameDetails,
            prefs.reduceAnimations,
            prefs.launchAnimation
        ) { skipDetails, reduceAnim, launchAnim -> Triple(skipDetails, reduceAnim, launchAnim) }
    ) { p1, p2 ->
        SettingsParams(
            grid = p1.first,
            scroll = p1.second,
            haptic = p1.third,
            skipDetails = p2.first,
            reduceAnim = p2.second,
            launchAnim = p2.third
        )
    }

    private val filterParamsFlow = combine(
        selectedFilter,
        prefs.selectedSort,
        prefs.visibleSections
    ) { filter, sort, sections ->
        FilterParams(filter, sort, sections)
    }

    val uiState: StateFlow<GamesLauncherUiState?> = combine(
        appViewModel.appState.filterIsInstance<AppState.Ready>(),
        _tagState,
        _launchingGame,
        filterParamsFlow,
        settingsParamsFlow
    ) { ready, tags, launchingGame, filterParams, extra ->

        val data = ready.data
        val filter = filterParams.filter
        val sort = filterParams.sort
        val sections = filterParams.sections

        val grid = extra.grid
        val scroll = extra.scroll
        val haptic = extra.haptic
        val skipDetails = extra.skipDetails
        val reduceAnim = extra.reduceAnim
        val launchAnim = extra.launchAnim

        val allGames = data.games
        val status = data.systemStatus

        val availableFilters = mutableListOf<GameFilter>()
        if (allGames.isNotEmpty()) {
            availableFilters.add(GameFilter.All)
            availableFilters.add(GameFilter.Installed)
            if (allGames.any { !it.isInstalled }) {
                availableFilters.add(GameFilter.Uninstalled)
            }
        }
        GameCategory.entries.forEach { category ->
            if (category != GameCategory.UNDEFINED && allGames.any { it.category == category }) {
                availableFilters.add(GameFilter.System(category))
            }
        }
        val custom = allGames.flatMap { it.customCategories }.distinct().sorted()
        custom.forEach { availableFilters.add(GameFilter.Custom(it)) }

        val favorites = mutableListOf<GameEntry>()
        val recent = mutableListOf<GameEntry>()
        val filtered = mutableListOf<GameEntry>()

        allGames.forEach { game ->
            if (game.isFavorite) favorites.add(game)
            if (game.lastPlayed > 0) recent.add(game)

            val matches = when (filter) {
                is GameFilter.All -> true
                is GameFilter.Installed -> game.isInstalled
                is GameFilter.Uninstalled -> !game.isInstalled
                is GameFilter.System -> game.category == filter.category
                is GameFilter.Custom -> filter.name in game.customCategories
            }
            if (matches) filtered.add(game)
        }

        val sortedFiltered = when (sort) {
            GameSortOption.TITLE_ASC -> filtered.sortedBy { it.displayName.lowercase() }
            GameSortOption.TITLE_DESC -> filtered.sortedByDescending { it.displayName.lowercase() }
            GameSortOption.LAST_PLAYED -> filtered.sortedByDescending { it.lastPlayed }
            GameSortOption.TOTAL_PLAY_TIME -> filtered.sortedByDescending { it.totalPlayTime }
        }

        GamesLauncherUiState(
            filteredGames = sortedFiltered.toPersistentList(),
            favoriteGames = favorites.sortedBy { it.displayName.lowercase() }.toPersistentList(),
            recentGames = recent.sortedByDescending { it.lastPlayed }.take(5).toPersistentList(),
            filters = availableFilters.toPersistentList(),
            selectedFilter = filter,
            selectedSort = sort,
            visibleSections = sections.toPersistentSet(),
            gridColumns = grid,
            screenOrientation = data.screenOrientation,
            immersiveMode = data.immersiveMode,
            keepScreenOn = data.keepScreenOn,
            showScrollbar = scroll,
            hapticFeedback = haptic,
            reduceAnimations = reduceAnim,
            launchAnimation = launchAnim,
            skipGameDetails = skipDetails,
            launchingGame = launchingGame,
            batteryLevel = status.batteryLevel,
            storageUsage = status.storageUsagePercentage,
            hasUsageStatsPermission = status.hasUsageStatsPermission,
            showTagDialog = tags.showTagDialog,
            taggingGame = tags.taggingGame,
            editingTags = tags.editingTags,
            newTagDraft = tags.newTagDraft
        )
    }.flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun onEvent(event: GamesLauncherUiEvent) = when (event) {
        is GamesLauncherUiEvent.FilterSelected -> {
            selectedFilter.value = event.filter
        }

        is GamesLauncherUiEvent.SortSelected -> {
            prefs.setSortOption(event.sort)
        }

        is GamesLauncherUiEvent.SectionVisibilityToggled -> {
            prefs.setSectionVisible(event.section, event.isVisible)
        }

        is GamesLauncherUiEvent.RefreshRequested -> refreshGames()
        is GamesLauncherUiEvent.GameClicked -> { /* Handled by UI */ }
        is GamesLauncherUiEvent.LaunchGameRequested -> {
            launchGame(event.packageName)
        }
        is GamesLauncherUiEvent.GrantPermissionClicked -> { /* Handled by UI */ }
        is GamesLauncherUiEvent.PermissionCheckRequested -> { /* Handled by status flow in AppViewModel */ }
        is GamesLauncherUiEvent.RefreshStats -> refreshStats()

        is GamesLauncherUiEvent.FavoriteToggled -> {
            viewModelScope.launch {
                toggleFavoriteUseCase(event.packageName, event.isFavorite)
            }
        }

        is GamesLauncherUiEvent.RemoveGame -> {
            viewModelScope.launch {
                removeGameUseCase(event.packageName)
            }
        }

        is GamesLauncherUiEvent.RenameRequested -> {
            viewModelScope.launch {
                renameGameUseCase(event.packageName, event.newTitle?.takeIf { it.isNotBlank() })
            }
        }

        is GamesLauncherUiEvent.ChangeIconRequested -> {
            viewModelScope.launch {
                updateGameIconUseCase(event.packageName, event.newIconPath)
            }
        }

        is GamesLauncherUiEvent.AppInfoRequested -> { /* Handled by UI */ }
        is GamesLauncherUiEvent.UninstallRequested -> { /* Handled by UI */ }

        is GamesLauncherUiEvent.AddGamesRequested -> {
            viewModelScope.launch {
                addGamesUseCase(event.packageNames)
                refreshGames()
            }
        }

        is GamesLauncherUiEvent.ManageTagsRequested -> {
            _tagState.update {
                it.copy(
                    showTagDialog = true,
                    taggingGame = event.game,
                    editingTags = event.game.customCategories,
                    newTagDraft = ""
                )
            }
        }

        is GamesLauncherUiEvent.TagDialogDismissed -> {
            _tagState.update { it.copy(showTagDialog = false) }
        }

        is GamesLauncherUiEvent.TagAdded -> {
            _tagState.update { state ->
                val trimmed = event.tag.trim()
                if (trimmed.isNotBlank() && trimmed !in state.editingTags) {
                    state.copy(
                        editingTags = (state.editingTags + trimmed).toPersistentList(),
                        newTagDraft = ""
                    )
                } else state
            }
        }

        is GamesLauncherUiEvent.TagRemoved -> {
            _tagState.update { state ->
                state.copy(
                    editingTags = (state.editingTags - event.tag).toPersistentList()
                )
            }
        }

        is GamesLauncherUiEvent.TagDraftChanged -> {
            _tagState.update { it.copy(newTagDraft = event.text) }
        }

        is GamesLauncherUiEvent.TagsSaved -> {
            val game = _tagState.value.taggingGame
            if (game != null) {
                viewModelScope.launch {
                    updateGameCategoryUseCase(game.packageName, _tagState.value.editingTags)
                    _tagState.update { it.copy(showTagDialog = false) }
                }
            } else {
                _tagState.update { it.copy(showTagDialog = false) }
            }
        }
    }

    private fun launchGame(packageName: String) {
        viewModelScope.launch {
            if (prefs.launchAnimation.value) {
                val game = uiState.value?.filteredGames?.find { it.packageName == packageName }
                    ?: uiState.value?.favoriteGames?.find { it.packageName == packageName }
                    ?: uiState.value?.recentGames?.find { it.packageName == packageName }
                val gameOrientation = launchGameUseCase.getGameOrientation(packageName)
                appViewModel.setForcedOrientation(gameOrientation)
                delay(150.milliseconds)
                _launchingGame.value = game
                delay(3150.milliseconds)
                launchGameUseCase(packageName)
                delay(1000.milliseconds)
                _launchingGame.value = null
                appViewModel.setForcedOrientation(null)
            } else {
                launchGameUseCase(packageName)
            }
        }
    }

    private fun refreshGames() {
        viewModelScope.launch {
            try {
                refreshGamesUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun refreshStats() {
        viewModelScope.launch {
            refreshAllGameStatsUseCase()
        }
    }

    private data class SettingsParams(
        val grid: GridColumns,
        val scroll: Boolean,
        val haptic: Boolean,
        val skipDetails: Boolean,
        val reduceAnim: Boolean,
        val launchAnim: Boolean
    )

    private data class FilterParams(
        val filter: GameFilter,
        val sort: GameSortOption,
        val sections: Set<LauncherSection>
    )
}
