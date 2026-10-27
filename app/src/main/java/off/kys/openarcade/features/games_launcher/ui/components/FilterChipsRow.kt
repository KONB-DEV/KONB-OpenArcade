package off.kys.openarcade.features.games_launcher.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.persistentListOf
import off.kys.openarcade.R
import off.kys.openarcade.core.domain.model.GameCategory
import off.kys.openarcade.core.domain.model.GameFilter
import off.kys.openarcade.features.games_launcher.ui.GamesLauncherUiState
import off.kys.openarcade.core.ui.theme.OpenArcadeTheme


private val ChipHeight = 32.dp
private val ChipHorizontalPadding = 12.dp
private val ChipIconSize = 16.dp
private val ChipIconSpacing = 4.dp
private const val AnimDuration = 200

private val TrailingClusterWidth = 92.dp


@Composable
fun FilterChipsRow(
    modifier: Modifier = Modifier,
    uiState: GamesLauncherUiState,
    onFilterSelected: (GameFilter) -> Unit,
    onSettingsClick: () -> Unit,
    onSectionsEdit: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val hapticService = off.kys.openarcade.core.util.LocalHapticFeedbackService.current
    val background = MaterialTheme.colorScheme.background

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ChipHeight + 16.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                start = 0.dp,
                end = TrailingClusterWidth + 8.dp,
                top = 8.dp,
                bottom = 8.dp
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                count = uiState.filters.size,
                key = { index ->
                    when (val filter = uiState.filters[index]) {
                        is GameFilter.All -> "all"
                        is GameFilter.Installed -> "installed"
                        is GameFilter.Uninstalled -> "uninstalled"
                        is GameFilter.System -> "system_${filter.category.name}"
                        is GameFilter.Custom -> "custom_${filter.name}"
                    }
                }
            ) { index ->
                val filter = uiState.filters[index]
                ArcadeFilterChip(
                    label = when (filter) {
                        is GameFilter.All -> stringResource(R.string.filter_all)
                        is GameFilter.Installed -> stringResource(R.string.category_installed)
                        is GameFilter.Uninstalled -> stringResource(R.string.category_uninstalled)
                        is GameFilter.System -> stringResource(filter.category.displayNameRes)
                        is GameFilter.Custom -> filter.name
                    },
                    selected = uiState.selectedFilter == filter,
                    onClick = {
                        if (uiState.hapticFeedback) {
                            hapticService.tabSelect()
                        }
                        onFilterSelected(filter)
                    }
                )
            }
        }

        // Clean Samsung One UI Frosted Action Dock (no artificial dark drop shadow or scrim block)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.70f))
                .border(
                    1.dp,
                    Color.White.copy(alpha = 0.16f),
                    CircleShape
                )
                .padding(3.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChipActionButton(
                    iconRes = R.drawable.round_settings_24,
                    contentDescription = "Settings",
                    onClick = {
                        if (uiState.hapticFeedback) {
                            hapticService.cardClick()
                        }
                        onSettingsClick()
                    },
                    isPrimary = true
                )

                ChipActionButton(
                    iconRes = R.drawable.round_category_24,
                    contentDescription = "Edit sections",
                    onClick = {
                        if (uiState.hapticFeedback) {
                            hapticService.detent()
                        }
                        onSectionsEdit()
                    },
                    isPrimary = false
                )
            }
        }
    }
}

@Composable
private fun ChipActionButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    isPrimary: Boolean = true
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "chipActionScale"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            isPrimary -> primary.copy(alpha = if (isPressed) 0.35f else 0.20f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isPressed) 0.70f else 0.45f)
        },
        animationSpec = tween(AnimDuration),
        label = "actionBtnContainer"
    )

    val iconTint by animateColorAsState(
        targetValue = if (isPrimary) {
            if (isPressed) Color.White else primary
        } else {
            if (isPressed) primary else MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(AnimDuration),
        label = "actionBtnIcon"
    )

    val borderBrush = if (isPrimary) {
        Brush.verticalGradient(
            listOf(
                primary.copy(alpha = if (isPressed) 0.95f else 0.80f),
                primary.copy(alpha = 0.40f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = if (isPressed) 0.30f else 0.18f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    }

    Box(
        modifier = Modifier
            .size(34.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(containerColor, CircleShape)
            .border(1.dp, borderBrush, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(17.dp)
        )
    }
}

@Composable
fun ArcadeFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
        ),
        label = "filterChipScale"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            selected -> primary.copy(alpha = if (isPressed) 0.38f else 0.24f)
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isPressed) 0.60f else 0.38f)
        },
        animationSpec = tween(AnimDuration),
        label = "chipContainer"
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(AnimDuration),
        label = "chipLabel"
    )
    val iconTint by animateColorAsState(
        targetValue = if (selected) primary else Color.Transparent,
        animationSpec = tween(AnimDuration),
        label = "chipIcon"
    )

    val borderBrush = if (selected) {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = if (isPressed) 0.45f else 0.30f),
                primary.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.08f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.16f),
                Color.White.copy(alpha = 0.04f)
            )
        )
    }

    val leadingSlotWidth by animateDpAsState(
        targetValue = if (selected) ChipIconSize + ChipIconSpacing else 0.dp,
        animationSpec = tween(AnimDuration),
        label = "chipLeadingSlot"
    )

    Box(
        modifier = modifier
            .height(34.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(containerColor, CircleShape)
            .border(width = 1.dp, brush = borderBrush, shape = CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(leadingSlotWidth)
                    .clipToBounds(),
                contentAlignment = Alignment.CenterStart
            ) {
                this@Row.AnimatedVisibility(
                    visible = selected,
                    enter = fadeIn(tween(AnimDuration)),
                    exit = fadeOut(tween(AnimDuration))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.round_check_24),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(ChipIconSize)
                        )
                        Spacer(Modifier.width(ChipIconSpacing))
                    }
                }
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                ),
                color = labelColor,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterChipsRowPreview() {
    OpenArcadeTheme {
        FilterChipsRow(
            uiState = GamesLauncherUiState(
                filters = persistentListOf(
                    GameFilter.All,
                    GameFilter.Installed,
                    GameFilter.Uninstalled,
                    GameFilter.System(GameCategory.UTILITY),
                    GameFilter.Custom("Favorites"),
                    GameFilter.Custom("Action")
                ),
                selectedFilter = GameFilter.All
            ),
            onFilterSelected = {},
            onSettingsClick = {},
            onSectionsEdit = {}
        )
    }
}
