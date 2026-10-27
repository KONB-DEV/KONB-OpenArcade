package off.kys.openarcade.features.analytics.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.R
import off.kys.openarcade.features.analytics.ui.components.CategoryDistributionSection
import off.kys.openarcade.features.analytics.ui.components.PlayTimeTrendSection
import off.kys.openarcade.features.analytics.ui.components.TopGameItem
import off.kys.openarcade.core.ui.components.AnalyticsTopBar
import off.kys.openarcade.core.ui.components.ArcadeGridScrollbar
import off.kys.openarcade.core.ui.components.ArcadeScrollbar
import off.kys.openarcade.core.ui.components.LoadingScreen
import off.kys.openarcade.core.ui.components.SectionHeader
import off.kys.openarcade.core.ui.components.getDynamicColors
import org.koin.androidx.compose.koinViewModel

class AnalyticsScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AnalyticsViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val isDark = isSystemInDarkTheme()

        LaunchedEffect(isDark) {
            viewModel.onEvent(AnalyticsUiEvent.DarkModeChanged(isDark))
        }

        AnalyticsScreenContent(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            onBackClick = { navigator.pop() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnalyticsScreenContent(
    uiState: AnalyticsUiState,
    onEvent: (AnalyticsUiEvent) -> Unit,
    onBackClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val donutColors = getDynamicColors()

    Scaffold(
        topBar = {
            AnalyticsTopBar(
                onBackClick = onBackClick,
                primaryColor = colorScheme.primary,
                tertiaryColor = colorScheme.tertiary
            )
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isExpanded = maxWidth > 600.dp

            if (uiState.isLoading) {
                LoadingScreen(
                    message = stringResource(R.string.game_detail_loading),
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                if (isExpanded) {
                    AnalyticsExpandedLayout(
                        uiState = uiState,
                        onEvent = onEvent,
                        donutColors = donutColors
                    )
                } else {
                    AnalyticsCompactLayout(
                        uiState = uiState,
                        onEvent = onEvent,
                        donutColors = donutColors
                    )
                }
            }
        }
    }
}

@Composable
private fun AnalyticsCompactLayout(
    uiState: AnalyticsUiState,
    onEvent: (AnalyticsUiEvent) -> Unit,
    donutColors: List<androidx.compose.ui.graphics.Color>
) {
    val listState = rememberLazyListState()
    val data = uiState.data ?: return
    val maxPlayTime = remember(data.topGames) { data.topGames.firstOrNull()?.playTimeMs ?: 1L }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                PlayTimeTrendSection(
                    selectedInterval = uiState.selectedInterval,
                    chartData = uiState.chartData,
                    barColor = MaterialTheme.colorScheme.primary,
                    onIntervalSelected = { onEvent(AnalyticsUiEvent.IntervalSelected(it)) }
                )
            }

            item {
                CategoryDistributionSection(
                    data = data.categoryDistribution,
                    donutColors = donutColors
                )
            }

            item {
                SectionHeader(title = stringResource(R.string.top_games))
            }

            items(
                items = uiState.topGamesWithColors,
                key = { it.game.packageName }
            ) { model ->
                TopGameItem(
                    game = model.game,
                    maxPlayTime = maxPlayTime,
                    accentColor = model.adaptiveColor
                )
            }
        }

        if (uiState.showScrollbar) {
            ArcadeScrollbar(
                listState = listState,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxSize()
                    .padding(end = 16.dp)
            )
        }
    }
}

@Composable
private fun AnalyticsExpandedLayout(
    uiState: AnalyticsUiState,
    onEvent: (AnalyticsUiEvent) -> Unit,
    donutColors: List<androidx.compose.ui.graphics.Color>
) {
    val gridState = rememberLazyGridState()
    val data = uiState.data ?: return
    val maxPlayTime = remember(data.topGames) { data.topGames.firstOrNull()?.playTimeMs ?: 1L }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item(span = { GridItemSpan(1) }) {
                PlayTimeTrendSection(
                    selectedInterval = uiState.selectedInterval,
                    chartData = uiState.chartData,
                    barColor = MaterialTheme.colorScheme.primary,
                    onIntervalSelected = { onEvent(AnalyticsUiEvent.IntervalSelected(it)) }
                )
            }

            item(span = { GridItemSpan(1) }) {
                CategoryDistributionSection(
                    data = data.categoryDistribution,
                    donutColors = donutColors
                )
            }

            item(span = { GridItemSpan(2) }) {
                SectionHeader(title = stringResource(R.string.top_games))
            }

            items(
                items = uiState.topGamesWithColors,
                key = { it.game.packageName },
                span = { GridItemSpan(1) }
            ) { model ->
                TopGameItem(
                    game = model.game,
                    maxPlayTime = maxPlayTime,
                    accentColor = model.adaptiveColor
                )
            }
        }

        if (uiState.showScrollbar) {
            ArcadeGridScrollbar(
                gridState = gridState,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxSize()
                    .padding(end = 16.dp)
            )
        }
    }
}
