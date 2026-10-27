package off.kys.openarcade.features.games_launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.R
import off.kys.openarcade.core.domain.model.GameSortOption
import off.kys.openarcade.core.ui.components.ArcadeDropdownMenu
import off.kys.openarcade.core.ui.components.ArcadeDropdownMenuItem

@Composable
fun LibraryHeader(
    gameCount: Int,
    selectedSort: GameSortOption,
    onSortSelected: (GameSortOption) -> Unit,
    onAddGamesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    val hapticService = off.kys.openarcade.core.util.LocalHapticFeedbackService.current
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.your_library).uppercase(),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = androidx.compose.ui.unit.TextUnit(0.6f, androidx.compose.ui.unit.TextUnitType.Sp)
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.width(8.dp))

        // Optical stadium glass count pill
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.50f),
                            primary.copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
                .padding(horizontal = 10.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.games_count, gameCount),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = primary
            )
        }

        Spacer(Modifier.weight(1f))

        // Samsung One UI Frosted Glass Disc Action Button for Add
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.22f),
                            primary.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    CircleShape
                )
                .clickable {
                    hapticService.cardClick()
                    onAddGamesClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.round_add_24),
                contentDescription = stringResource(R.string.add_games_desc),
                tint = primary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(8.dp))

        // Samsung One UI Frosted Glass Disc Action Button for Sort
        Box {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                .border(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.22f),
                            tertiary.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.05f)
                        )
                    ),
                    CircleShape
                )
                .clickable {
                    hapticService.detent()
                    sortMenuExpanded = true
                },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.round_sort_24),
                    contentDescription = stringResource(R.string.sort_button_desc),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }

            ArcadeDropdownMenu(
                expanded = sortMenuExpanded,
                onDismissRequest = { sortMenuExpanded = false }
            ) {
                ArcadeDropdownMenuItem(
                    text = stringResource(R.string.sort_title_asc),
                    selected = selectedSort == GameSortOption.TITLE_ASC,
                    onClick = {
                        onSortSelected(GameSortOption.TITLE_ASC)
                        sortMenuExpanded = false
                    }
                )
                ArcadeDropdownMenuItem(
                    text = stringResource(R.string.sort_title_desc),
                    selected = selectedSort == GameSortOption.TITLE_DESC,
                    onClick = {
                        onSortSelected(GameSortOption.TITLE_DESC)
                        sortMenuExpanded = false
                    }
                )
                ArcadeDropdownMenuItem(
                    text = stringResource(R.string.sort_last_played),
                    selected = selectedSort == GameSortOption.LAST_PLAYED,
                    onClick = {
                        onSortSelected(GameSortOption.LAST_PLAYED)
                        sortMenuExpanded = false
                    }
                )
                ArcadeDropdownMenuItem(
                    text = stringResource(R.string.sort_play_time),
                    selected = selectedSort == GameSortOption.TOTAL_PLAY_TIME,
                    onClick = {
                        onSortSelected(GameSortOption.TOTAL_PLAY_TIME)
                        sortMenuExpanded = false
                    }
                )
            }
        }
    }
}