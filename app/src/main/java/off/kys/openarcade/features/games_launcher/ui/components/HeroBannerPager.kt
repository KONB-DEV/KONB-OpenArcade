package off.kys.openarcade.features.games_launcher.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import off.kys.openarcade.core.ui.theme.BentoTokens
import off.kys.openarcade.core.ui.theme.rememberDynamicLogo5StopGradient
import coil.compose.AsyncImage
import off.kys.openarcade.R
import off.kys.openarcade.core.domain.model.GameEntry
import off.kys.openarcade.core.domain.repository.MediaRepository
import org.koin.compose.koinInject
import kotlin.math.absoluteValue

import androidx.compose.runtime.LaunchedEffect
import off.kys.openarcade.core.util.LocalHapticFeedbackService

@Composable
fun HeroBannerPager(
    modifier: Modifier = Modifier,
    installedGames: List<GameEntry>,
    onInspectGame: (String) -> Unit,
    onPlayGame: (String) -> Unit = onInspectGame,
    onFeaturedColorChanged: ((Color) -> Unit)? = null,
    reduceAnimations: Boolean = false,
    mediaRepository: MediaRepository = koinInject()
) {
    val hapticService = LocalHapticFeedbackService.current
    val isDark = isSystemInDarkTheme()
    val pagerCount = minOf(installedGames.size, 5)
    val pagerState = rememberPagerState { pagerCount }

    val context = LocalContext.current

    LaunchedEffect(pagerState.currentPage, installedGames, isDark) {
        hapticService.tabSelect()
        val currentGame = installedGames.getOrNull(pagerState.currentPage) ?: return@LaunchedEffect
        val dominantAdaptive = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val drawable: android.graphics.drawable.Drawable? = try {
                if (currentGame.customIconPath != null) {
                    android.graphics.BitmapFactory.decodeFile(currentGame.customIconPath)?.let {
                        android.graphics.drawable.BitmapDrawable(context.resources, it)
                    } ?: context.packageManager.getApplicationIcon(currentGame.packageName)
                } else {
                    context.packageManager.getApplicationIcon(currentGame.packageName)
                }
            } catch (_: Exception) {
                null
            }
            val top5 = mediaRepository.extractTop5ProminentColors(drawable, isDark = isDark)
            top5.firstOrNull() ?: mediaRepository.getAdaptiveColor(currentGame.getPrimaryColor(), isDark)
        }
        onFeaturedColorChanged?.invoke(dominantAdaptive)
    }

    val primaryAdaptive = remember(pagerState.currentPage, isDark) {
        val color = installedGames.getOrNull(pagerState.currentPage)?.getPrimaryColor() ?: Color.Cyan
        mediaRepository.getAdaptiveColor(color, isDark)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
        ) { page ->
            val game = installedGames.getOrNull(page) ?: return@HorizontalPager
            val iconModel = remember(game.packageName, game.customIconPath) {
                game.customIconPath
                    ?: try {
                        context.packageManager.getApplicationIcon(game.packageName)
                    } catch (_: Exception) {
                        null
                    }
            }

            // Extract top 5 prominent colors of the authentic game logo
            val top5Colors = remember(game.packageName, game.customIconPath, isDark) {
                val drawable: android.graphics.drawable.Drawable? = try {
                    if (game.customIconPath != null) {
                        android.graphics.BitmapFactory.decodeFile(game.customIconPath)?.let {
                            android.graphics.drawable.BitmapDrawable(context.resources, it)
                        } ?: context.packageManager.getApplicationIcon(game.packageName)
                    } else {
                        context.packageManager.getApplicationIcon(game.packageName)
                    }
                } catch (_: Exception) {
                    null
                }
                mediaRepository.extractTop5ProminentColors(drawable, isDark = isDark)
            }

            // 45-degree 5-stop animated linear gradient with soft breathing dynamics
            val dynamic5StopGradient = rememberDynamicLogo5StopGradient(
                colors = top5Colors,
                reduceAnimations = reduceAnimations,
                alpha = if (isDark) 0.55f else 0.42f
            )

            val pageOffset by remember(pagerState.currentPage) {
                derivedStateOf { (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction }
            }
            val gameAdaptivePrimary = top5Colors.firstOrNull() ?: remember(game.primaryColorArgb, isDark) {
                mediaRepository.getAdaptiveColor(game.getPrimaryColor(), isDark)
            }
            val gameAdaptiveTertiary = top5Colors.getOrNull(2) ?: remember(game.tertiaryColorArgb, isDark) {
                mediaRepository.getAdaptiveColor(game.getTertiaryColor(), isDark)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(BentoTokens.HeroShape)
                    .background(dynamic5StopGradient)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF101726).copy(alpha = if (isDark) 0.50f else 0.15f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.35f),
                                gameAdaptivePrimary.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        ),
                        shape = BentoTokens.HeroShape
                    )
            ) {
                // Atmospheric ambient background wash (clean and non-muddy)
                AsyncImage(
                    model = iconModel,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            if (!reduceAnimations) {
                                translationX = pageOffset * 40.dp.toPx()
                                scaleX = 1.25f
                                scaleY = 1.25f
                            }
                            alpha = 0.16f
                        },
                    contentScale = ContentScale.Crop
                )

                // Soft dark gradient scrim preserving clarity and high contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.0f to Color.Transparent,
                                    0.45f to Color.Black.copy(alpha = 0.25f),
                                    1.0f to Color.Black.copy(alpha = 0.78f)
                                )
                            )
                        )
                )

                // Centered, immaculate hero information with clear authentic logo
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top: Symmetrical pill category badge
                    val categoryStrings =
                        game.customCategories + if (game.category.displayNameRes != 0) {
                            listOf(stringResource(game.category.displayNameRes))
                        } else emptyList()

                    if (categoryStrings.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .border(
                                    1.dp,
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.40f),
                                            gameAdaptivePrimary.copy(alpha = 0.30f),
                                            Color.Transparent
                                        )
                                    ),
                                    CircleShape
                                )
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = categoryStrings.first().uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = androidx.compose.ui.unit.TextUnit(0.6f, androidx.compose.ui.unit.TextUnitType.Sp)
                                ),
                                color = gameAdaptivePrimary
                            )
                        }
                    } else {
                        Spacer(Modifier.height(24.dp))
                    }

                    // Middle: Prominent, Crystal-Clear Original Game Logo & Title
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        // 1. Crystal-clear Original Game Logo (100% sharp, distortion-free)
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(BentoTokens.MediumTileShape)
                                .background(Color.Black.copy(alpha = 0.35f))
                                .border(
                                    1.2.dp,
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.50f),
                                            gameAdaptivePrimary.copy(alpha = 0.40f),
                                            Color.White.copy(alpha = 0.12f)
                                        )
                                    ),
                                    BentoTokens.MediumTileShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = iconModel,
                                contentDescription = game.displayName,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(5.dp)
                                    .clip(BentoTokens.SmallBadgeShape),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        // 2. High-contrast bold title
                        Text(
                            text = game.displayName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black
                            ),
                            color = Color.White,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = if (categoryStrings.size > 1) {
                                categoryStrings.drop(1).joinToString(" · ")
                            } else {
                                "Installed & Optimized"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.75f),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Bottom: Floating Symmetrical Stadium Glass Player Capsule (inspired by Video 1!)
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.45f))
                            .border(
                                1.2.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.85f),
                                        gameAdaptivePrimary.copy(alpha = 0.60f),
                                        Color.White.copy(alpha = 0.20f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Left: Inspect glass circular button
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                                    .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                                    .clickable {
                                        hapticService.cardClick()
                                        onInspectGame(game.packageName)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.round_explore_24),
                                    contentDescription = "Inspect",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            // "PLAY" Stadium Pill Button
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                gameAdaptivePrimary.copy(alpha = 0.95f),
                                                gameAdaptiveTertiary.copy(alpha = 0.90f)
                                            )
                                        )
                                    )
                                    .border(
                                        1.dp,
                                        Brush.linearGradient(
                                            listOf(Color.White.copy(alpha = 0.90f), Color.Transparent)
                                        ),
                                        CircleShape
                                    )
                                    .clickable {
                                        hapticService.actionLaunch()
                                        onPlayGame(game.packageName)
                                    }
                                    .padding(horizontal = 26.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.round_play_arrow_24),
                                        contentDescription = stringResource(R.string.play_game, game.displayName),
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "PLAY",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = androidx.compose.ui.unit.TextUnit(0.3f, androidx.compose.ui.unit.TextUnitType.Sp)
                                        ),
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Symmetrical fluid spring page indicators centered horizontally
        if (pagerCount > 1) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp)
            ) {
                repeat(pagerCount) { index ->
                    val isActive = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (isActive) 24.dp else 6.dp,
                        animationSpec = if (reduceAnimations) snap() else androidx.compose.animation.core.spring(
                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                            stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
                        ),
                        label = "dotWidth"
                    )

                    Box(
                        modifier = Modifier
                            .size(width = dotWidth, height = 5.dp)
                            .clip(CircleShape)
                            .background(
                                if (isActive) primaryAdaptive
                                else Color.White.copy(alpha = 0.28f)
                            )
                    )
                }
            }
        }
    }
}

