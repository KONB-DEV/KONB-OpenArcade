package off.kys.openarcade.features.game_details.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import off.kys.openarcade.core.data.local.preferences.ArcadePreferences
import off.kys.openarcade.features.game_details.domain.GetGameByPackageUseCase
import off.kys.openarcade.features.game_details.domain.LaunchGameUseCase
import off.kys.openarcade.features.game_details.domain.RefreshGameStatsUseCase
import off.kys.openarcade.features.games_launcher.domain.UpdateGameCategoryUseCase
import kotlin.time.Duration.Companion.milliseconds

class GameDetailViewModel(
    private val packageName: String,
    getGameByPackageUseCase: GetGameByPackageUseCase,
    private val updateGameCategoryUseCase: UpdateGameCategoryUseCase,
    private val refreshGameStatsUseCase: RefreshGameStatsUseCase,
    private val launchGameUseCase: LaunchGameUseCase,
    prefs: ArcadePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = combine(
        getGameByPackageUseCase(packageName),
        prefs.showScrollbar,
        prefs.hapticFeedback,
        prefs.reduceAnimations,
        prefs.launchAnimation,
        _uiState
    ) { params: Array<Any?> ->
        val game = params[0] as off.kys.openarcade.core.domain.model.GameEntry?
        val showScrollbar = params[1] as Boolean
        val haptic = params[2] as Boolean
        val reduceAnim = params[3] as Boolean
        val launchAnim = params[4] as Boolean
        val state = params[5] as GameDetailUiState
        
        state.copy(
            game = game,
            showScrollbar = showScrollbar,
            hapticFeedback = haptic,
            reduceAnimations = reduceAnim,
            launchAnimation = launchAnim
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GameDetailUiState()
    )

    fun onEvent(event: GameDetailUiEvent) {
        when (event) {
            is GameDetailUiEvent.LaunchGame -> launchGame()
            is GameDetailUiEvent.RefreshStats -> refreshStats()
            is GameDetailUiEvent.OpenCategoryDialog -> {
                val currentCategories = uiState.value.game?.customCategories ?: emptyList()
                _uiState.update {
                    it.copy(
                        showCategoryDialog = true,
                        editingCategories = currentCategories.toPersistentList(),
                        newCategoryDraft = ""
                    )
                }
            }

            is GameDetailUiEvent.CloseCategoryDialog -> _uiState.update { it.copy(showCategoryDialog = false) }
            is GameDetailUiEvent.AddCategory -> _uiState.update { state ->
                val trimmed = event.category.trim()
                if (trimmed.isNotBlank() && trimmed !in state.editingCategories) {
                    state.copy(
                        editingCategories = (state.editingCategories + trimmed).toPersistentList(),
                        newCategoryDraft = ""
                    )
                } else state
            }

            is GameDetailUiEvent.RemoveCategory -> _uiState.update { state ->
                state.copy(
                    editingCategories = (state.editingCategories - event.category).toPersistentList()
                )
            }

            is GameDetailUiEvent.UpdateNewCategoryDraft -> _uiState.update {
                it.copy(
                    newCategoryDraft = event.text
                )
            }

            is GameDetailUiEvent.SaveCategories -> saveCategories()
        }
    }

    private fun launchGame() {
        viewModelScope.launch {
            if (uiState.value.launchAnimation) {
                _uiState.update { it.copy(isLaunching = true) }
                delay(3150.milliseconds)
                launchGameUseCase(packageName)
                delay(500.milliseconds)
                _uiState.update { it.copy(isLaunching = false) }
            } else {
                launchGameUseCase(packageName)
            }
        }
    }

    private fun refreshStats() {
        viewModelScope.launch {
            refreshGameStatsUseCase(packageName)
        }
    }

    private fun saveCategories() {
        viewModelScope.launch {
            updateGameCategoryUseCase(packageName, _uiState.value.editingCategories)
            _uiState.update { it.copy(showCategoryDialog = false) }
        }
    }
}
