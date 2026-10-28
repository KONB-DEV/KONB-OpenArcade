package off.kys.openarcade.features.games_launcher.ui.components

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.core.domain.model.GameEntry
import off.kys.openarcade.core.domain.repository.MediaRepository
import off.kys.openarcade.core.ui.components.ArcadeCard
import off.kys.openarcade.core.ui.components.ArcadeGameIcon
import off.kys.openarcade.core.ui.theme.BentoTokens
import off.kys.openarcade.core.ui.util.squishElasticPress
import off.kys.openarcade.core.util.LocalHapticFeedbackService
import off.kys.openarcade.features.games_launcher.ui.GamesLauncherUiEvent
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GameGridCard(
    game: GameEntry,
    onClick: () -> Unit,
    onEvent: (GamesLauncherUiEvent) -> Unit,
    modifier: Modifier = Modifier,
    hapticFeedbackEnabled: Boolean = true,
    reduceAnimations: Boolean = false,
    mediaRepository: MediaRepository = koinInject()
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val hapticService = LocalHapticFeedbackService.current
    val isDark = isSystemInDarkTheme()
    val adaptivePrimary = mediaRepository.getAdaptiveColor(game.getPrimaryColor(), isDark)
    val adaptiveTertiary = mediaRepository.getAdaptiveColor(game.getTertiaryColor(), isDark)

    var showMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    val iconPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onEvent(GamesLauncherUiEvent.ChangeIconRequested(game.packageName, it.toString()))
        }
    }

    if (showRenameDialog) {
        RenameDialog(
            initialTitle = game.displayName,
            accentColor = adaptivePrimary,
            onConfirm = {
                onEvent(GamesLauncherUiEvent.RenameRequested(game.packageName, it))
                showRenameDialog = false
            },
            onRestore = {
                onEvent(GamesLauncherUiEvent.RenameRequested(game.packageName, null))
                showRenameDialog = false
            },
            onDismiss = { showRenameDialog = false }
        )
    }

    // Spring physics animation for card scale expansion & chevron rotation
    val expansionScale by animateFloatAsState(
        targetValue = if (isExpanded && !reduceAnimations) 1.018f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bentoCardExpansionScale"
    )

    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bentoChevronRotation"
    )

    val animatedBorderWidth by animateDpAsState(
        targetValue = if (isExpanded || game.isFavorite) 1.8.dp else 1.2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bentoBorderWidth"
    )

    val cardShape = BentoTokens.LargeTileShape

    ArcadeCard(
        modifier = modifier
            .fillMaxWidth()
            .squishElasticPress(reduceAnimations = reduceAnimations)
            .graphicsLayer {
                scaleX = expansionScale
                scaleY = expansionScale
            }
            .clip(cardShape)
            .combinedClickable(
                onClick = {
                    if (hapticFeedbackEnabled) {
                        hapticService.cardClick()
                    }
                    onClick()
                },
                onLongClick = {
                    if (hapticFeedbackEnabled) {
                        hapticService.cardLongPress()
                    }
                    showMenu = true
                }
            ),
        shape = cardShape,
        accentColor = if (game.isFavorite || isExpanded) adaptivePrimary else adaptiveTertiary,
        borderWidth = animatedBorderWidth,
        reduceAnimations = reduceAnimations
    ) {
        Box {
            Column {
                Box {
                    ArcadeGameIcon(
                        icon = game.customIconPath ?: game.cachedIconPath ?: game.packageName,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.25f),
                        primaryColor = adaptivePrimary,
                        tertiaryColor = adaptiveTertiary,
                        iconSize = 54.dp
                    )

                    // Top-right Tactile Expand Button
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(10.dp)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0x800D121D))
                            .border(
                                1.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.70f),
                                        adaptivePrimary.copy(alpha = 0.40f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                            .clickable {
                                if (hapticFeedbackEnabled) {
                                    if (isExpanded) hapticService.cardCollapse() else hapticService.cardExpand()
                                }
                                isExpanded = !isExpanded
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.round_expand_more_24),
                            contentDescription = "Expand Game Details",
                            tint = if (isExpanded) adaptivePrimary else Color.White.copy(alpha = 0.85f),
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(chevronRotation)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = BentoTokens.CardInnerPadding, vertical = BentoTokens.CompactInnerPadding)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = game.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        if (game.isFavorite) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .clip(CircleShape)
                                    .background(adaptivePrimary.copy(alpha = 0.22f))
                                    .border(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(
                                                Color.White.copy(alpha = 0.85f),
                                                adaptivePrimary.copy(alpha = 0.65f),
                                                Color.Transparent
                                            )
                                        ),
                                        CircleShape
                                    )
                                    .padding(5.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_favorite_24),
                                    contentDescription = null,
                                    tint = adaptivePrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))
                    val lastPlayedText = if (game.lastPlayed > 0) {
                        DateUtils.getRelativeTimeSpanString(
                            game.lastPlayed,
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                        ).toString()
                    } else {
                        stringResource(R.string.never_played)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(adaptivePrimary.copy(alpha = 0.75f))
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = lastPlayedText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.80f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Fluid Spring Physics Card Expansion Content
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ),
                        exit = shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) + fadeOut(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                        ) {
                            // Subtitle & Package identification
                            Text(
                                text = game.packageName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.60f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(Modifier.height(10.dp))

                            // Tactile Action Pills (Optimized for vertical/narrow mode so Play never squishes)
                            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                val isNarrowCard = maxWidth < 135.dp

                                if (isNarrowCard) {
                                    // Vertical / Narrow mode: Full-width Play button with single-line horizontal layout
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(34.dp)
                                            .clip(CircleShape)
                                            .background(adaptivePrimary)
                                            .clickable {
                                                if (hapticFeedbackEnabled) {
                                                    hapticService.actionLaunch()
                                                }
                                                onClick()
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(horizontal = 6.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.round_play_arrow_24),
                                                contentDescription = null,
                                                tint = Color(0xFF003640),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = stringResource(R.string.game_detail_play).uppercase(),
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.5.sp
                                                ),
                                                color = Color(0xFF003640),
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    }
                                } else {
                                    // Wide mode: Play button + Favorite button side-by-side
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(34.dp)
                                                .clip(CircleShape)
                                                .background(adaptivePrimary)
                                                .clickable {
                                                    if (hapticFeedbackEnabled) {
                                                        hapticService.actionLaunch()
                                                    }
                                                    onClick()
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            ) {
                                                Icon(
                                                    painter = painterResource(R.drawable.round_play_arrow_24),
                                                    contentDescription = null,
                                                    tint = Color(0xFF003640),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    text = stringResource(R.string.game_detail_play).uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        letterSpacing = 0.5.sp
                                                    ),
                                                    color = Color(0xFF003640),
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.12f))
                                                .border(
                                                    1.dp,
                                                    Color.White.copy(alpha = 0.20f),
                                                    CircleShape
                                                )
                                                .clickable {
                                                    if (hapticFeedbackEnabled) {
                                                        hapticService.toggleSwitch(!game.isFavorite)
                                                    }
                                                    onEvent(GamesLauncherUiEvent.FavoriteToggled(game.packageName, !game.isFavorite))
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(
                                                    if (game.isFavorite) R.drawable.round_favorite_24 else R.drawable.round_favorite_border_24
                                                ),
                                                contentDescription = null,
                                                tint = if (game.isFavorite) adaptivePrimary else Color.White.copy(alpha = 0.85f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            GameContextMenu(
                game = game,
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                onFavoriteToggle = {
                    onEvent(GamesLauncherUiEvent.FavoriteToggled(game.packageName, !game.isFavorite))
                },
                onRename = { showRenameDialog = true },
                onRemove = {
                    onEvent(GamesLauncherUiEvent.RemoveGame(game.packageName))
                },
                onManageTags = {
                    onEvent(GamesLauncherUiEvent.ManageTagsRequested(game))
                },
                onAppInfo = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", game.packageName, null)
                    }
                    context.startActivity(intent)
                },
                onUninstall = {
                    val intent = Intent(Intent.ACTION_DELETE).apply {
                        data = Uri.fromParts("package", game.packageName, null)
                    }
                    context.startActivity(intent)
                },
                onChangeIcon = {
                    iconPickerLauncher.launch("image/*")
                },
                onRestoreDefaultName = {
                    onEvent(GamesLauncherUiEvent.RenameRequested(game.packageName, null))
                },
                onRestoreDefaultIcon = {
                    onEvent(GamesLauncherUiEvent.ChangeIconRequested(game.packageName, null))
                }
            )
        }
    }
}