package off.kys.openarcade.features.app_picker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import off.kys.openarcade.core.data.local.preferences.ArcadePreferences
import off.kys.openarcade.core.domain.usecase.AddGamesUseCase
import off.kys.openarcade.core.domain.usecase.GetGamesUseCase
import off.kys.openarcade.core.domain.usecase.RefreshGamesUseCase
import off.kys.openarcade.features.app_picker.domain.GetAppListUseCase

class AppPickerViewModel(
    private val getAppListUseCase: GetAppListUseCase,
    private val addGamesUseCase: AddGamesUseCase,
    private val refreshGamesUseCase: RefreshGamesUseCase,
    private val getGamesUseCase: GetGamesUseCase,
    private val prefs: ArcadePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppPickerUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadApps()
        observeTrackedGames()
        observePreferences()
    }

    private fun observePreferences() {
        viewModelScope.launch {
            prefs.showScrollbar.collect { show ->
                _uiState.update { it.copy(showScrollbar = show) }
            }
        }
        viewModelScope.launch {
            prefs.hapticFeedback.collect { enabled ->
                _uiState.update { it.copy(hapticFeedback = enabled) }
            }
        }
    }

    private fun observeTrackedGames() {
        viewModelScope.launch {
            getGamesUseCase().collect { games ->
                val tracked = games.map { it.packageName }.toPersistentSet()
                _uiState.update { it.copy(trackedPackages = tracked) }
            }
        }
    }

    private fun loadApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val apps = getAppListUseCase()
            _uiState.update { it.copy(apps = apps.toPersistentList(), isLoading = false) }
        }
    }

    fun onEvent(event: AppPickerUiEvent) {
        when (event) {
            is AppPickerUiEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
            }
            is AppPickerUiEvent.ToggleAppSelection -> {
                _uiState.update { state ->
                    val newSelection = state.selectedPackages.toMutableSet()
                    if (newSelection.contains(event.packageName)) {
                        newSelection.remove(event.packageName)
                    } else {
                        newSelection.add(event.packageName)
                    }
                    state.copy(selectedPackages = newSelection.toPersistentSet())
                }
            }
            AppPickerUiEvent.AddSelectedApps -> {
                addSelectedApps()
            }
        }
    }

    private fun addSelectedApps() {
        val selected = _uiState.value.selectedPackages
        if (selected.isEmpty()) return

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val packagesToAdd = selected.filter { it !in _uiState.value.trackedPackages }

            if (packagesToAdd.isNotEmpty()) {
                addGamesUseCase(packagesToAdd)
            }

            refreshGamesUseCase()
            _uiState.update { it.copy(isDone = true, isLoading = false) }
        }
    }
}
