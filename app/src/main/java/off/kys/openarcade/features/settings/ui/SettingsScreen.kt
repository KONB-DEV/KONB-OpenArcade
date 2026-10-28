package off.kys.openarcade.features.settings.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import off.kys.openarcade.BuildConfig
import off.kys.openarcade.R
import off.kys.openarcade.core.domain.model.LauncherSection
import off.kys.openarcade.core.ui.components.AmbientMeshBackground
import off.kys.openarcade.core.ui.components.ArcadeToggle
import off.kys.openarcade.core.ui.components.ScrollbarWidth
import off.kys.openarcade.core.ui.components.SectionHeader
import off.kys.openarcade.features.licenses.ui.LicensesScreen
import off.kys.openarcade.core.ui.theme.OpenArcadeTheme
import off.kys.openarcade.core.ui.util.squishElasticPress
import org.koin.androidx.compose.koinViewModel

private val SettingItemHeight = 64.dp
private val SettingHPad = 16.dp
private val SettingIconBadgeSize = 38.dp
private val SettingIconSize = 20.dp
private val SettingIconSpacing = 14.dp
private const val AnimDuration = 200

class SettingsScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val uriHandler = LocalUriHandler.current
        val viewModel: SettingsViewModel = koinViewModel()
        val state by viewModel.state.collectAsState()
        val listState = rememberLazyListState()
        val primary = MaterialTheme.colorScheme.primary
        val tertiary = MaterialTheme.colorScheme.tertiary

        BackHandler {
            navigator.pop()
        }

        Scaffold(
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Frosted Glass Circular Back Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                                .border(
                                    1.dp,
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.45f),
                                            primary.copy(alpha = 0.25f),
                                            Color.White.copy(alpha = 0.10f)
                                        )
                                    ),
                                    CircleShape
                                )
                                .clickable { navigator.pop() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_arrow_back_24),
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Top bar Title
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Symmetrical settings indicator badge
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(primary.copy(alpha = 0.12f))
                                .border(1.dp, primary.copy(alpha = 0.30f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.round_settings_24),
                                contentDescription = null,
                                tint = primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Delicate hairline separator
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .height(1.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color.Transparent,
                                        primary.copy(alpha = 0.40f),
                                        tertiary.copy(alpha = 0.20f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            AmbientMeshBackground(reduceAnimations = state.reduceAnimations) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {

                    item {
                        // Samsung One UI 9 Expansive Reachability Viewing Area
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 6.dp, end = 6.dp, top = 8.dp, bottom = 14.dp)
                        ) {
                            Text(
                                text = "Settings",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (-0.6).sp
                                ),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Preferences · Customization · Arcade Engine",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                            )
                        }
                    }

                    // ── Display ───────────────────────────────────────────────
                    item {
                        SettingsSectionHeader(title = "Display", accentColor = Color(0xFF38BDF8))
                        Spacer(Modifier.height(8.dp))
                    }

                    item {
                        SettingsGroup(accentColor = Color(0xFF38BDF8)) {
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_bolt_24),
                                title = "Immersive Mode",
                                subtitle = "Hide status & navigation bars while in launcher",
                                checked = state.immersiveMode,
                                accentColor = Color(0xFF38BDF8),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                reduceAnimations = state.reduceAnimations,
                                onCheckedChange = {
                                    viewModel.onEvent(SettingsUiEvent.ToggleImmersiveMode(it))
                                }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_favorite_24),
                                title = "Keep Screen On",
                                subtitle = "Prevent screen from sleeping in launcher",
                                checked = state.keepScreenOn,
                                accentColor = Color(0xFFFBBF24),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                reduceAnimations = state.reduceAnimations,
                                onCheckedChange = {
                                    viewModel.onEvent(SettingsUiEvent.ToggleKeepScreenOn(it))
                                }
                            )
                        }
                    }

                    // ── Screen Orientation ────────────────────────────────────
                    item { Spacer(Modifier.height(18.dp)) }
                    item {
                        SettingsSectionHeader(title = "Screen Orientation", accentColor = Color(0xFF34D399))
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        SettingsGroup(accentColor = Color(0xFF34D399)) {
                            OrientationSettingItem(
                                selected = state.screenOrientation,
                                accentColor = Color(0xFF34D399),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onSelect = {
                                    viewModel.onEvent(SettingsUiEvent.SetScreenOrientation(it))
                                }
                            )
                        }
                    }

                    // ── Grid Layout ───────────────────────────────────────────
                    item { Spacer(Modifier.height(18.dp)) }
                    item {
                        SettingsSectionHeader(title = "Grid Layout", accentColor = Color(0xFFA78BFA))
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        SettingsGroup(accentColor = Color(0xFFA78BFA)) {
                            GridColumnsSettingItem(
                                selected = state.gridColumns,
                                accentColor = Color(0xFFA78BFA),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onSelect = { viewModel.onEvent(SettingsUiEvent.SetGridColumns(it)) }
                            )
                        }
                    }

                    // ── Launcher Sections ─────────────────────────────────────
                    item { Spacer(Modifier.height(18.dp)) }
                    item {
                        SettingsSectionHeader(title = "Launcher Sections", accentColor = Color(0xFFF472B6))
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        SettingsGroup(accentColor = Color(0xFFF472B6)) {
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_favorite_24),
                                title = "Favorites",
                                subtitle = "Show pinned games at the top",
                                checked = state.showFavoritesSection,
                                accentColor = Color(0xFFF472B6),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                reduceAnimations = state.reduceAnimations,
                                onCheckedChange = {
                                    viewModel.onEvent(SettingsUiEvent.ToggleSection(LauncherSection.FAVORITES, it))
                                }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_bar_chart_24),
                                title = "Analytics",
                                subtitle = "Show play-time charts and stats",
                                checked = state.showAnalyticsSection,
                                accentColor = Color(0xFF2DD4BF),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                reduceAnimations = state.reduceAnimations,
                                onCheckedChange = {
                                    viewModel.onEvent(SettingsUiEvent.ToggleSection(LauncherSection.ANALYTICS, it))
                                }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_history_24),
                                title = "Recent Activity",
                                subtitle = "Show recently played games",
                                checked = state.showRecentSection,
                                accentColor = Color(0xFF60A5FA),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                reduceAnimations = state.reduceAnimations,
                                onCheckedChange = {
                                    viewModel.onEvent(SettingsUiEvent.ToggleSection(LauncherSection.RECENT_ACTIVITY, it))
                                }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_bolt_24),
                                title = "System Status",
                                subtitle = "Show battery, storage and RAM usage",
                                checked = state.showSystemStatus,
                                accentColor = Color(0xFF818CF8),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                reduceAnimations = state.reduceAnimations,
                                onCheckedChange = {
                                    viewModel.onEvent(SettingsUiEvent.ToggleSection(LauncherSection.SYSTEM_STATUS, it))
                                }
                            )
                        }
                    }

                    // ── Feel & Transitions ────────────────────────────────────
                    item { Spacer(Modifier.height(18.dp)) }
                    item {
                        SettingsSectionHeader(title = "Feel & Transitions", accentColor = Color(0xFFFB923C))
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        SettingsGroup(accentColor = Color(0xFFFB923C)) {
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_sports_esports_24),
                                title = "Haptic Feedback",
                                subtitle = "Tactile response on taps and card selection",
                                checked = state.hapticFeedback,
                                accentColor = Color(0xFFFB7185),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                reduceAnimations = state.reduceAnimations,
                                onCheckedChange = {
                                    viewModel.onEvent(SettingsUiEvent.ToggleHapticFeedback(it))
                                }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_explore_24),
                                title = "Launch Animation",
                                subtitle = "Animate app icon when launching a game",
                                checked = state.launchAnimation,
                                accentColor = Color(0xFF34D399),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                reduceAnimations = state.reduceAnimations,
                                onCheckedChange = {
                                    viewModel.onEvent(SettingsUiEvent.ToggleLaunchAnimation(it))
                                }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_bolt_24),
                                title = "Skip Game Details",
                                subtitle = "Launch games directly on click",
                                checked = state.skipGameDetails,
                                accentColor = Color(0xFFC084FC),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                reduceAnimations = state.reduceAnimations,
                                onCheckedChange = {
                                    viewModel.onEvent(SettingsUiEvent.ToggleSkipGameDetails(it))
                                }
                            )
                            SettingsGroupDivider()
                            ToggleSettingItem(
                                icon = painterResource(R.drawable.round_category_24),
                                title = "Reduce Animations",
                                subtitle = "Simplify UI transitions for performance",
                                checked = state.reduceAnimations,
                                accentColor = Color(0xFF94A3B8),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                reduceAnimations = state.reduceAnimations,
                                onCheckedChange = {
                                    viewModel.onEvent(SettingsUiEvent.ToggleReduceAnimations(it))
                                }
                            )
                        }
                    }

                    // ── About ─────────────────────────────────────────────────
                    item { Spacer(Modifier.height(18.dp)) }
                    item {
                        SettingsSectionHeader(title = "About", accentColor = Color(0xFF818CF8))
                        Spacer(Modifier.height(8.dp))
                    }
                    item {
                        SettingsGroup(accentColor = Color(0xFF818CF8)) {
                            NavigationSettingItem(
                                icon = painterResource(R.drawable.round_sports_esports_24),
                                title = "OpenArcade",
                                subtitle = "Version ${BuildConfig.VERSION_NAME} · Built with ♥",
                                accentColor = Color(0xFF38BDF8),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onClick = { uriHandler.openUri("https://github.com/kys0ff/OpenArcade") }
                            )
                            SettingsGroupDivider()
                            NavigationSettingItem(
                                icon = painterResource(R.drawable.round_explore_24),
                                title = "Licenses",
                                subtitle = "Open source libraries used in this app",
                                accentColor = Color(0xFFA78BFA),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onClick = { navigator += LicensesScreen() }
                            )
                            SettingsGroupDivider()
                            NavigationSettingItem(
                                icon = painterResource(R.drawable.round_close_24),
                                title = "Reset All Settings",
                                subtitle = "Restore default settings and view states",
                                isDestructive = true,
                                accentColor = Color(0xFFF43F5E),
                                hapticFeedbackEnabled = state.hapticFeedback,
                                onClick = { viewModel.onEvent(SettingsUiEvent.ResetAll) }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }

                // Arcade scrollbar overlay
                if (state.showScrollbar) {
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
    }
}
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    accentColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(start = 6.dp, top = 8.dp, bottom = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 14.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            ),
            color = accentColor
        )
    }
}

@Composable
private fun SettingsGroup(
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    content: @Composable () -> Unit
) {
    val primary = accentColor ?: MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val bentoShape = RoundedCornerShape(24.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(bentoShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.52f),
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.38f),
                        primary.copy(alpha = 0.28f),
                        Color.White.copy(alpha = 0.08f)
                    )
                ),
                bentoShape
            )
    ) {
        // Specular Top Sheen Highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.50f),
                            primary.copy(alpha = 0.45f),
                            tertiary.copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

/** Thin gradient divider between items inside a [SettingsGroup]. */
@Composable
private fun SettingsGroupDivider() {
    Box(
        modifier = Modifier
            .padding(start = SettingHPad + SettingIconBadgeSize + SettingIconSpacing)
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.07f),
                        Color.Transparent
                    )
                )
            )
    )
}

@Composable
private fun ToggleSettingItem(
    icon: Painter,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    accentColor: Color? = null,
    hapticFeedbackEnabled: Boolean = true,
    reduceAnimations: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    val hapticService = off.kys.openarcade.core.util.LocalHapticFeedbackService.current
    val primary = accentColor ?: MaterialTheme.colorScheme.primary

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val rowBackground by animateColorAsState(
        targetValue = if (isPressed)
            primary.copy(alpha = if (checked) 0.14f else 0.07f)
        else Color.Transparent,
        animationSpec = tween(if (reduceAnimations) 0 else AnimDuration),
        label = "toggleRowBg_$title"
    )

    val iconBadgeBg by animateColorAsState(
        targetValue = if (checked) primary.copy(alpha = 0.22f)
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f),
        animationSpec = tween(if (reduceAnimations) 0 else AnimDuration),
        label = "iconBadgeBg_$title"
    )
    val iconTint by animateColorAsState(
        targetValue = if (checked) primary
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
        animationSpec = tween(if (reduceAnimations) 0 else AnimDuration),
        label = "iconTint_$title"
    )
    val titleColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
        animationSpec = tween(if (reduceAnimations) 0 else AnimDuration),
        label = "titleColor_$title"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingItemHeight)
            .squishElasticPress(interactionSource = interactionSource, reduceAnimations = reduceAnimations)
            .background(rowBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (hapticFeedbackEnabled) {
                        try {
                            hapticService.toggleSwitch(!checked)
                        } catch (_: Throwable) {}
                    }
                    onCheckedChange(!checked)
                }
            )
            .padding(horizontal = SettingHPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SettingIconSpacing)
    ) {
        // Icon badge
        SettingIconBadge(
            icon = icon,
            tint = iconTint,
            background = Brush.radialGradient(
                listOf(
                    iconBadgeBg,
                    if (checked) primary.copy(alpha = 0.08f) else iconBadgeBg.copy(alpha = 0.4f)
                )
            ),
            borderColor = iconTint.copy(alpha = 0.40f)
        )

        // Labels
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (checked) FontWeight.SemiBold else FontWeight.Medium
                ),
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = if (checked) 0.75f else 0.55f
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        ArcadeToggle(
            checked = checked,
            onCheckedChange = null,
            hapticFeedbackEnabled = false,
            reduceAnimations = reduceAnimations
        )
    }
}

// ─── Orientation picker item ──────────────────────────────────────────────────

@Composable
private fun OrientationSettingItem(
    selected: ScreenOrientation,
    onSelect: (ScreenOrientation) -> Unit,
    accentColor: Color? = null,
    hapticFeedbackEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val primary = accentColor ?: MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SettingHPad, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SettingIconSpacing)
        ) {
            SettingIconBadge(
                icon = painterResource(ScreenOrientation.Auto.iconRes),
                tint = primary,
                background = Brush.radialGradient(
                    listOf(primary.copy(alpha = 0.22f), primary.copy(alpha = 0.05f))
                ),
                borderColor = primary.copy(alpha = 0.45f)
            )
            Column {
                Text(
                    text = "Screen Orientation",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Controls how the launcher rotates",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                )
            }
        }

        // Orientation chip row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ScreenOrientation.entries.forEach { orientation ->
                val isSelected = selected == orientation
                val chipShape = RoundedCornerShape(14.dp)
                val chipContainer by animateColorAsState(
                    targetValue = if (isSelected)
                        primary.copy(alpha = 0.24f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    animationSpec = tween(AnimDuration),
                    label = "oriChip_$orientation"
                )
                val chipLabel by animateColorAsState(
                    targetValue = if (isSelected)
                        primary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f),
                    animationSpec = tween(AnimDuration),
                    label = "oriLabel_$orientation"
                )
                val chipBorderStart by animateColorAsState(
                    targetValue = if (isSelected) primary.copy(alpha = 0.85f)
                    else Color.White.copy(alpha = 0.12f),
                    animationSpec = tween(AnimDuration),
                    label = "oriBorder_$orientation"
                )

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(chipShape)
                        .background(chipContainer, chipShape)
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(chipBorderStart, chipBorderStart.copy(alpha = 0.20f))
                            ),
                            chipShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                if (hapticFeedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onSelect(orientation)
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(orientation.iconRes),
                        contentDescription = null,
                        tint = chipLabel,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = orientation.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold
                            else FontWeight.Medium
                        ),
                        color = chipLabel
                    )
                }
            }
        }
    }
}

// ─── Grid column picker item ──────────────────────────────────────────────────

@Composable
private fun GridColumnsSettingItem(
    selected: GridColumns,
    onSelect: (GridColumns) -> Unit,
    accentColor: Color? = null,
    hapticFeedbackEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val primary = accentColor ?: MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SettingHPad, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SettingIconSpacing)
        ) {
            SettingIconBadge(
                icon = painterResource(R.drawable.round_category_24),
                tint = primary,
                background = Brush.radialGradient(
                    listOf(primary.copy(alpha = 0.22f), primary.copy(alpha = 0.05f))
                ),
                borderColor = primary.copy(alpha = 0.45f)
            )
            Column {
                Text(
                    text = "Grid Columns",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Games per row on the launcher grid",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                )
            }
        }

        // Column count selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            GridColumns.entries.forEach { col ->
                val isSelected = selected == col
                val chipShape = RoundedCornerShape(14.dp)
                val container by animateColorAsState(
                    targetValue = if (isSelected)
                        primary.copy(alpha = 0.24f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    animationSpec = tween(AnimDuration),
                    label = "gridChip_$col"
                )
                val labelColor by animateColorAsState(
                    targetValue = if (isSelected)
                        primary
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f),
                    animationSpec = tween(AnimDuration),
                    label = "gridLabel_$col"
                )
                val borderStroke by animateColorAsState(
                    targetValue = if (isSelected) primary.copy(alpha = 0.85f)
                    else Color.White.copy(alpha = 0.12f),
                    animationSpec = tween(AnimDuration),
                    label = "gridBorder_$col"
                )

                // Visual mini grid preview
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .clip(chipShape)
                        .background(container, chipShape)
                        .border(
                            1.dp,
                            Brush.linearGradient(
                                listOf(borderStroke, borderStroke.copy(alpha = 0.20f))
                            ),
                            chipShape
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                if (hapticFeedbackEnabled) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                                onSelect(col)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Mini dot grid preview
                        val dotSize = 6.dp
                        val dotSpacing = 3.dp
                        repeat(2) {
                            Row(horizontalArrangement = Arrangement.spacedBy(dotSpacing)) {
                                repeat(col.count) {
                                    Box(
                                        modifier = Modifier
                                            .size(dotSize)
                                            .clip(CircleShape)
                                            .background(labelColor.copy(alpha = if (isSelected) 0.90f else 0.45f))
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = col.count.toString(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold
                                else FontWeight.Medium
                            ),
                            color = labelColor
                        )
                    }
                }
            }
        }
    }
}

// ─── Navigation item ──────────────────────────────────────────────────────────

@Composable
private fun NavigationSettingItem(
    icon: Painter,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    accentColor: Color? = null,
    isDestructive: Boolean = false,
    hapticFeedbackEnabled: Boolean = true
) {
    val haptic = LocalHapticFeedback.current
    val primary = accentColor ?: MaterialTheme.colorScheme.primary
    val error = MaterialTheme.colorScheme.error
    val accent = if (isDestructive) error else primary

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val rowBackground by animateColorAsState(
        targetValue = if (isPressed) accent.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = tween(AnimDuration),
        label = "navRowBg_$title"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isPressed) accent else accent.copy(alpha = 0.85f),
        animationSpec = tween(AnimDuration),
        label = "navIconTint_$title"
    )
    val chevronAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 0.40f,
        animationSpec = tween(AnimDuration),
        label = "navChevron_$title"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SettingItemHeight)
            .background(rowBackground)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (hapticFeedbackEnabled) {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    onClick()
                }
            )
            .padding(horizontal = SettingHPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SettingIconSpacing)
    ) {
        SettingIconBadge(
            icon = icon,
            tint = iconTint,
            background = Brush.radialGradient(
                listOf(accent.copy(alpha = 0.20f), accent.copy(alpha = 0.05f))
            ),
            borderColor = iconTint.copy(alpha = 0.40f)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isDestructive) error
                else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Trailing chevron
        Icon(
            painter = painterResource(R.drawable.round_arrow_back_24),
            contentDescription = null,
            tint = accent.copy(alpha = chevronAlpha),
            modifier = Modifier
                .size(16.dp)
                .alpha(chevronAlpha)
                .graphicsLayer(rotationZ = 180f)
        )
    }
}

// ─── Icon badge ───────────────────────────────────────────────────────────────

/** Reusable icon badge — radial wash + gradient border. */
@Composable
private fun SettingIconBadge(
    icon: Painter,
    tint: Color,
    background: Brush,
    borderColor: Color
) {
    val badgeShape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .size(SettingIconBadgeSize)
            .clip(badgeShape)
            .background(background)
            .border(
                1.dp,
                Brush.linearGradient(listOf(borderColor, borderColor.copy(alpha = 0.15f))),
                badgeShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(SettingIconSize)
        )
    }
}

/**
 * Gradient scrollbar overlay that reads from [LazyListState].
 * Mirrors the primary→tertiary language; fades out when the list is at rest.
 */
@Composable
fun ArcadeScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    width: Dp = ScrollbarWidth
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val isScrolling = listState.isScrollInProgress
    val thumbAlpha by animateFloatAsState(
        targetValue = if (isScrolling) 0.85f else 0.30f,
        animationSpec = tween(if (isScrolling) 100 else 600),
        label = "scrollbarAlpha"
    )

    Box(
        modifier = modifier.drawWithContent {
            drawContent()

            val totalItems = listState.layoutInfo.totalItemsCount
            if (totalItems == 0) return@drawWithContent

            val visibleItems = listState.layoutInfo.visibleItemsInfo
            val firstVisible = visibleItems.firstOrNull() ?: return@drawWithContent

            val viewportH = size.height
            val thumbH = (viewportH * (visibleItems.size.toFloat() / totalItems))
                .coerceAtLeast(40.dp.toPx())
            val scrollFraction = firstVisible.index.toFloat() / (totalItems - visibleItems.size)
                .coerceAtLeast(1)
            val thumbY = (scrollFraction * (viewportH - thumbH)).coerceIn(0f, viewportH - thumbH)

            drawRoundRect(
                brush = Brush.verticalGradient(
                    listOf(
                        primary.copy(alpha = thumbAlpha),
                        tertiary.copy(alpha = thumbAlpha * 0.60f)
                    )
                ),
                topLeft = Offset(size.width - width.toPx(), thumbY),
                size = Size(width.toPx(), thumbH),
                cornerRadius = CornerRadius(width.toPx() / 2)
            )
        }
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SettingsScreenPreview() {
    OpenArcadeTheme {
        var state by remember { mutableStateOf(SettingsUiState()) }
        val listState = rememberLazyListState()

        Box(Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item {
                    SectionHeader("Display")
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    SettingsGroup {
                        ToggleSettingItem(
                            icon = painterResource(R.drawable.round_bolt_24),
                            title = "Immersive Mode",
                            subtitle = "Hide status & navigation bars",
                            checked = state.immersiveMode,
                            onCheckedChange = { state = state.copy(immersiveMode = it) }
                        )
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    SectionHeader("Screen Orientation")
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    SettingsGroup {
                        OrientationSettingItem(
                            selected = state.screenOrientation,
                            onSelect = { state = state.copy(screenOrientation = it) }
                        )
                    }
                }
                item { Spacer(Modifier.height(20.dp)) }
                item {
                    SectionHeader("Grid Layout")
                    Spacer(Modifier.height(8.dp))
                }
                item {
                    SettingsGroup {
                        GridColumnsSettingItem(
                            selected = state.gridColumns,
                            onSelect = { state = state.copy(gridColumns = it) }
                        )
                    }
                }
            }
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
