package off.kys.openarcade.features.games_launcher.ui

import off.kys.openarcade.core.domain.model.GameEntry
import off.kys.openarcade.core.domain.model.GameFilter
import off.kys.openarcade.core.domain.model.GameSortOption
import off.kys.openarcade.core.domain.model.LauncherSection

sealed interface GamesLauncherUiEvent {
    data class FilterSelected(val filter: GameFilter) : GamesLauncherUiEvent
    data class SortSelected(val sort: GameSortOption) : GamesLauncherUiEvent
    data class SectionVisibilityToggled(val section: LauncherSection, val isVisible: Boolean) : GamesLauncherUiEvent
    data class GameClicked(val packageName: String) : GamesLauncherUiEvent
    data class LaunchGameRequested(val packageName: String) : GamesLauncherUiEvent
    data object GrantPermissionClicked : GamesLauncherUiEvent
    data object RefreshRequested : GamesLauncherUiEvent
    data object PermissionCheckRequested : GamesLauncherUiEvent
    data object RefreshStats : GamesLauncherUiEvent

    data class FavoriteToggled(val packageName: String, val isFavorite: Boolean) : GamesLauncherUiEvent
    data class RemoveGame(val packageName: String) : GamesLauncherUiEvent
    data class RenameRequested(val packageName: String, val newTitle: String?) : GamesLauncherUiEvent
    data class ChangeIconRequested(val packageName: String, val newIconPath: String?) : GamesLauncherUiEvent
    data class AppInfoRequested(val packageName: String) : GamesLauncherUiEvent
    data class UninstallRequested(val packageName: String) : GamesLauncherUiEvent
    data class AddGamesRequested(val packageNames: List<String>) : GamesLauncherUiEvent

    data class ManageTagsRequested(val game: GameEntry) : GamesLauncherUiEvent
    data object TagDialogDismissed : GamesLauncherUiEvent
    data class TagAdded(val tag: String) : GamesLauncherUiEvent
    data class TagRemoved(val tag: String) : GamesLauncherUiEvent
    data class TagDraftChanged(val text: String) : GamesLauncherUiEvent
    data object TagsSaved : GamesLauncherUiEvent
}