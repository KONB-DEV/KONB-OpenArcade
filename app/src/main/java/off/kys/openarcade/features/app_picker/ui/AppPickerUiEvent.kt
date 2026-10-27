package off.kys.openarcade.features.app_picker.ui

sealed interface AppPickerUiEvent {
    data class SearchQueryChanged(val query: String) : AppPickerUiEvent
    data class ToggleAppSelection(val packageName: String) : AppPickerUiEvent
    data object AddSelectedApps : AppPickerUiEvent
}
