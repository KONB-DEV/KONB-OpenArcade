package off.kys.openarcade.features.games_launcher.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.core.domain.model.LauncherSection
import off.kys.openarcade.core.domain.repository.MediaRepository
import off.kys.openarcade.core.ui.components.AmbientMeshBackground
import off.kys.openarcade.core.ui.components.ArcadeGridScrollbar
import off.kys.openarcade.core.ui.components.GameTagDialog
import off.kys.openarcade.core.ui.components.LaunchOverlay
import off.kys.openarcade.core.ui.theme.BentoTokens
import off.kys.openarcade.core.ui.theme.LocalFeaturedGameTint
import org.koin.compose.koinInject
import off.kys.openarcade.features.app_picker.ui.AppPickerScreen
import off.kys.openarcade.features.game_details.ui.GameDetailScreen
import off.kys.openarcade.features.games_launcher.ui.components.AnalyticsSection
import off.kys.openarcade.features.games_launcher.ui.components.EmptyGamesState
import off.kys.openarcade.features.games_launcher.ui.components.FavoritesSection
import off.kys.openarcade.features.games_launcher.ui.components.FilterChipsRow
import off.kys.openarcade.features.games_launcher.ui.components.GameGridCard
import off.kys.openarcade.features.games_launcher.ui.components.HeroBannerPager
import off.kys.openarcade.features.games_launcher.ui.components.LibraryHeader
import off.kys.openarcade.features.games_launcher.ui.components.RecentActivitySection
import off.kys.openarcade.features.games_launcher.ui.components.SectionEditBottomSheet
import off.kys.openarcade.features.games_launcher.ui.components.SystemStatusSection
import off.kys.openarcade.features.games_launcher.ui.components.UsagePermissionCard
import off.kys.openarcade.features.settings.ui.SettingsScreen
import off.kys.openarcade.ui.main.MainActivity
import org.koin.androidx.compose.koinViewModel

class GamesLauncherScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current
        val viewModel: GamesLauncherViewModel = koinViewModel(
            viewModelStoreOwner = context as MainActivity
        )
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val state = uiState ?: return

        val gridState = rememberLazyGridState()
        var showSectionEdit by remember { mutableStateOf(false) }
        var featuredColor by remember { mutableStateOf<androidx.compose.ui.graphics.Color?>(null) }
        val animatedFeaturedColor by animateColorAsState(
            targetValue = featuredColor ?: MaterialTheme.colorScheme.primary,
            animationSpec = tween(600),
            label = "featuredColorAnim"
        )

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            viewModel.onEvent(GamesLauncherUiEvent.PermissionCheckRequested)
            viewModel.onEvent(GamesLauncherUiEvent.RefreshStats)
        }

        if (showSectionEdit) {
            SectionEditBottomSheet(
                visibleSections = state.visibleSections,
                onDismissRequest = { showSectionEdit = false },
                onToggleSection = { section, visible ->
                    viewModel.onEvent(GamesLauncherUiEvent.SectionVisibilityToggled(section, visible))
                }
            )
        }

        if (state.showTagDialog) {
            GameTagDialog(
                editingTags = state.editingTags,
                newTagDraft = state.newTagDraft,
                accentColor = MaterialTheme.colorScheme.tertiary,
                onUpdateNewTagDraft = { viewModel.onEvent(GamesLauncherUiEvent.TagDraftChanged(it)) },
                onAddTag = { viewModel.onEvent(GamesLauncherUiEvent.TagAdded(it)) },
                onRemoveTag = { viewModel.onEvent(GamesLauncherUiEvent.TagRemoved(it)) },
                onSave = { viewModel.onEvent(GamesLauncherUiEvent.TagsSaved) },
                onDismiss = { viewModel.onEvent(GamesLauncherUiEvent.TagDialogDismissed) }
            )
        }

        CompositionLocalProvider(LocalFeaturedGameTint provides animatedFeaturedColor) {
            Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
                AmbientMeshBackground(
                    reduceAnimations = state.reduceAnimations
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(state.gridColumns.count),
                            modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = maxOf(innerPadding.calculateTopPadding(), 24.dp) + 16.dp,
                            start = BentoTokens.GridOuterPadding,
                            end = BentoTokens.GridOuterPadding,
                            bottom = innerPadding.calculateBottomPadding() + BentoTokens.GridOuterPadding
                        ),
                        horizontalArrangement = Arrangement.spacedBy(BentoTokens.GridGutter),
                        verticalArrangement = Arrangement.spacedBy(BentoTokens.GridGutter)
                    ) {
                        item(key = "hero_section", span = { GridItemSpan(maxLineSpan) }) {
                            Box {
                                AnimatedContent(
                                    targetState = state.filteredGames.isNotEmpty(),
                                    transitionSpec = {
                                        if (state.reduceAnimations) {
                                            fadeIn(snap()).togetherWith(fadeOut(snap()))
                                        } else {
                                            fadeIn().togetherWith(fadeOut())
                                        }
                                    },
                                    label = "hero_section_transition"
                                ) { hasGames ->
                                    if (hasGames) {
                                        HeroBannerPager(
                                            installedGames = state.filteredGames.filter { it.isInstalled },
                                            onInspectGame = { pkg ->
                                                // Always open game details when compass/inspect is pressed
                                                viewModel.onEvent(GamesLauncherUiEvent.GameClicked(pkg))
                                                navigator.push(GameDetailScreen(pkg))
                                            },
                                            onPlayGame = { pkg ->
                                                viewModel.onEvent(GamesLauncherUiEvent.LaunchGameRequested(pkg))
                                            },
                                            onFeaturedColorChanged = { color ->
                                                featuredColor = color
                                            },
                                            reduceAnimations = state.reduceAnimations
                                        )
                                    } else {
                                        EmptyGamesState()
                                    }
                                }
                            }
                        }

                        item(key = "filter_chips", span = { GridItemSpan(maxLineSpan) }) {
                            Box {
                                FilterChipsRow(
                                    uiState = state,
                                    onFilterSelected = { filter ->
                                        viewModel.onEvent(GamesLauncherUiEvent.FilterSelected(filter))
                                    },
                                    onSettingsClick = { navigator.push(SettingsScreen()) },
                                    onSectionsEdit = { showSectionEdit = true }
                                )
                            }
                        }

                        if (!state.hasUsageStatsPermission) {
                            item(key = "usage_permission", span = { GridItemSpan(maxLineSpan) }) {
                                Box {
                                    UsagePermissionCard {
                                        viewModel.onEvent(GamesLauncherUiEvent.GrantPermissionClicked)
                                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                                    }
                                }
                            }
                        }

                        item(key = "section_system_status", span = { GridItemSpan(maxLineSpan) }) {
                            AnimatedVisibility(
                                visible = LauncherSection.SYSTEM_STATUS in state.visibleSections,
                                enter = expandVertically(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(),
                                exit = shrinkVertically(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeOut()
                            ) {
                                SystemStatusSection(
                                    batteryLevel = state.batteryLevel,
                                    storageUsage = state.storageUsage
                                )
                            }
                        }

                        item(key = "section_analytics", span = { GridItemSpan(maxLineSpan) }) {
                            AnimatedVisibility(
                                visible = LauncherSection.ANALYTICS in state.visibleSections,
                                enter = expandVertically(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(),
                                exit = shrinkVertically(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeOut()
                            ) {
                                AnalyticsSection(state.filteredGames)
                            }
                        }

                        item(key = "section_favorites", span = { GridItemSpan(maxLineSpan) }) {
                            AnimatedVisibility(
                                visible = LauncherSection.FAVORITES in state.visibleSections && state.favoriteGames.isNotEmpty(),
                                enter = expandVertically(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(),
                                exit = shrinkVertically(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeOut()
                            ) {
                                FavoritesSection(
                                    games = state.favoriteGames,
                                    onGameClick = { pkg ->
                                        if (state.skipGameDetails) {
                                            viewModel.onEvent(GamesLauncherUiEvent.LaunchGameRequested(pkg))
                                        } else {
                                            viewModel.onEvent(GamesLauncherUiEvent.GameClicked(pkg))
                                            navigator.push(GameDetailScreen(pkg))
                                        }
                                    },
                                    hapticFeedbackEnabled = state.hapticFeedback,
                                    reduceAnimations = state.reduceAnimations
                                )
                            }
                        }

                        item(key = "section_recent_activity", span = { GridItemSpan(maxLineSpan) }) {
                            AnimatedVisibility(
                                visible = LauncherSection.RECENT_ACTIVITY in state.visibleSections && state.recentGames.isNotEmpty(),
                                enter = expandVertically(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeIn(),
                                exit = shrinkVertically(
                                    animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
                                ) + fadeOut()
                            ) {
                                RecentActivitySection(
                                    games = state.recentGames,
                                    onGameClick = { pkg ->
                                        if (state.skipGameDetails) {
                                            viewModel.onEvent(GamesLauncherUiEvent.LaunchGameRequested(pkg))
                                        } else {
                                            viewModel.onEvent(GamesLauncherUiEvent.GameClicked(pkg))
                                            navigator.push(GameDetailScreen(pkg))
                                        }
                                    },
                                    hapticFeedbackEnabled = state.hapticFeedback,
                                    reduceAnimations = state.reduceAnimations
                                )
                            }
                        }

                        if (state.filteredGames.isNotEmpty()) {
                            item(key = "library_header", span = { GridItemSpan(maxLineSpan) }) {
                                Box {
                                    LibraryHeader(
                                        gameCount = state.filteredGames.size,
                                        selectedSort = state.selectedSort,
                                        onSortSelected = { sort ->
                                            viewModel.onEvent(GamesLauncherUiEvent.SortSelected(sort))
                                        },
                                        onAddGamesClick = {
                                            navigator.push(AppPickerScreen())
                                        }
                                    )
                                }
                            }
                        }

                    items(state.filteredGames, key = { it.packageName }) { game ->
                        GameGridCard(
                            game = game,
                            onClick = {
                                if (state.skipGameDetails) {
                                    viewModel.onEvent(GamesLauncherUiEvent.LaunchGameRequested(game.packageName))
                                } else {
                                    viewModel.onEvent(GamesLauncherUiEvent.GameClicked(game.packageName))
                                    navigator.push(GameDetailScreen(game.packageName))
                                }
                            },
                            onEvent = { viewModel.onEvent(it) },
                            modifier = Modifier.animateItem(
                                fadeInSpec = if (state.reduceAnimations) snap() else tween(300),
                                fadeOutSpec = if (state.reduceAnimations) snap() else tween(300),
                                placementSpec = if (state.reduceAnimations) snap() else spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            ),
                            hapticFeedbackEnabled = state.hapticFeedback,
                            reduceAnimations = state.reduceAnimations
                        )
                    }
                }

                if (state.showScrollbar) {
                    ArcadeGridScrollbar(
                        gridState = gridState,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxSize()
                            .padding(
                                top = maxOf(innerPadding.calculateTopPadding(), 24.dp) + 16.dp,
                                bottom = innerPadding.calculateBottomPadding() + BentoTokens.GridOuterPadding,
                                end = 16.dp
                            )
                    )
                }
            }

                LaunchOverlay(
                    game = state.launchingGame,
                    isVisible = state.launchingGame != null,
                    reduceAnimations = state.reduceAnimations
                )
            }
        }
    }
}
}
