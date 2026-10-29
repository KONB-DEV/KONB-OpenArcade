package off.kys.openarcade.core.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import off.kys.openarcade.core.ui.theme.liquidGlassSurface

/**
 * Convenience wrapper around [rememberModalBottomSheetState] that skips
 * the half-expanded stop, matching the card-style presentation used across
 * the app (full sheet or dismissed — no intermediate state).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberArcadeSheetState(
    skipPartiallyExpanded: Boolean = true
): SheetState = rememberModalBottomSheetState(
    skipPartiallyExpanded = skipPartiallyExpanded
)

/**
 * Arcade-styled modal bottom sheet.
 *
 * Follows the same gradient-border / adaptive-color language as
 * [off.kys.openarcade.features.games_launcher.ui.components.GameGridCard], [ArcadeDialog], and [off.kys.openarcade.features.games_launcher.ui.components.FavoritesSection].
 *
 * @param onDismissRequest  Called when the user swipes down or taps the scrim.
 * @param sheetState        Hoist via [rememberArcadeSheetState].
 * @param title             Optional heading rendered below the drag handle.
 * @param subtitle          Optional secondary text below [title].
 * @param content           Sheet body — receives [ColumnScope] for natural
 *                          vertical stacking of chips, etc.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArcadeBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState = rememberArcadeSheetState(),
    title: String? = null,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val isExpanded = sheetState.currentValue == SheetValue.Expanded
    val handleWidth by animateDpAsState(
        targetValue = if (isExpanded) 48.dp else 32.dp,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
            stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
        ),
        label = "handleWidth"
    )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.Transparent,
        scrimColor = Color(0xFF03060C).copy(alpha = 0.70f),
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlassSurface(
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    alpha = 0.94f
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                primary.copy(alpha = 0.70f),
                                tertiary.copy(alpha = 0.40f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(handleWidth + 10.dp)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    primary.copy(alpha = 0.18f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .width(handleWidth)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    primary.copy(alpha = 0.55f),
                                    tertiary.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.horizontalGradient(
                                listOf(
                                    primary.copy(alpha = 0.40f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
            }

            if (title != null || subtitle != null) {
                val isDark = androidx.compose.foundation.isSystemInDarkTheme()
                val titleColor = if (isDark) Color(0xFFF4F7FB) else Color(0xFF0F172A)
                val subtitleColor = if (isDark) MaterialTheme.colorScheme.primary else Color(0xFF0284C7)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 4.dp)
                ) {
                    if (title != null) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = titleColor
                        )
                    }
                    if (subtitle != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = subtitleColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    primary.copy(alpha = 0.30f),
                                    tertiary.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            } else {
                Spacer(Modifier.height(12.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                content()
            }

            Spacer(Modifier.height(8.dp))

            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}