package off.kys.openarcade.features.game_details.ui

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import off.kys.openarcade.core.domain.model.GameEntry

data class GameDetailUiState(
    val game: GameEntry? = null,
    val showCategoryDialog: Boolean = false,
    val editingCategories: ImmutableList<String> = persistentListOf(),
    val newCategoryDraft: String = "",
    val showScrollbar: Boolean = true,
    val hapticFeedback: Boolean = true,
    val reduceAnimations: Boolean = false,
    val launchAnimation: Boolean = true,
    val isLaunching: Boolean = false
)
