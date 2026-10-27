package off.kys.openarcade.features.games_launcher.ui

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentSet
import off.kys.openarcade.core.domain.model.GameEntry
import off.kys.openarcade.core.domain.model.GameFilter
import off.kys.openarcade.core.domain.model.GameSortOption
import off.kys.openarcade.core.domain.model.LauncherSection
import off.kys.openarcade.features.settings.ui.GridColumns
import off.kys.openarcade.features.settings.ui.ScreenOrientation

data class GamesLauncherUiState(
    val filteredGames: ImmutableList<GameEntry> = persistentListOf(),
    val favoriteGames: ImmutableList<GameEntry> = persistentListOf(),
    val recentGames: ImmutableList<GameEntry> = persistentListOf(),
    val filters: ImmutableList<GameFilter> = persistentListOf(GameFilter.All),
    val selectedFilter: GameFilter = GameFilter.All,
    val selectedSort: GameSortOption = GameSortOption.TITLE_ASC,
    val visibleSections: ImmutableSet<LauncherSection> = LauncherSection.entries.toPersistentSet(),
    val gridColumns: GridColumns = GridColumns.Three,
    val screenOrientation: ScreenOrientation = ScreenOrientation.Auto,
    val immersiveMode: Boolean = false,
    val keepScreenOn: Boolean = false,
    val showScrollbar: Boolean = true,
    val hapticFeedback: Boolean = true,
    val reduceAnimations: Boolean = false,
    val launchAnimation: Boolean = true,
    val skipGameDetails: Boolean = false,
    val launchingGame: GameEntry? = null,
    val batteryLevel: Int = 0,
    val storageUsage: Int = 0,
    val hasUsageStatsPermission: Boolean = true,
    val showTagDialog: Boolean = false,
    val taggingGame: GameEntry? = null,
    val editingTags: ImmutableList<String> = persistentListOf(),
    val newTagDraft: String = ""
)

data class TagDialogState(
    val showTagDialog: Boolean = false,
    val taggingGame: GameEntry? = null,
    val editingTags: ImmutableList<String> = persistentListOf(),
    val newTagDraft: String = ""
)
