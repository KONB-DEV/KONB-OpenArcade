package off.kys.openarcade.features.app_picker.ui

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList
import off.kys.openarcade.core.domain.model.AppInfo

data class AppPickerUiState(
    val apps: ImmutableList<AppInfo> = persistentListOf(),
    val trackedPackages: ImmutableSet<String> = persistentSetOf(),
    val searchQuery: String = "",
    val selectedPackages: ImmutableSet<String> = persistentSetOf(),
    val isLoading: Boolean = false,
    val isDone: Boolean = false,
    val showScrollbar: Boolean = true,
    val hapticFeedback: Boolean = true
) {
    val filteredApps: ImmutableList<AppInfo>
        get() = apps.filter { app ->
            val isTracked = trackedPackages.contains(app.packageName)

            // Show if not tracked
            val shouldShow = !isTracked

            shouldShow && (
                    app.label.contains(searchQuery, ignoreCase = true) ||
                            app.packageName.contains(searchQuery, ignoreCase = true)
                    )
        }.toPersistentList()
}
