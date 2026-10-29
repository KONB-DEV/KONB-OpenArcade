package off.kys.openarcade.features.games_launcher.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.sp
import off.kys.openarcade.R
import off.kys.openarcade.core.domain.model.LauncherSection
import off.kys.openarcade.core.ui.components.ArcadeBottomSheet
import off.kys.openarcade.core.ui.components.ArcadeToggle
import off.kys.openarcade.core.ui.components.rememberArcadeSheetState
import off.kys.openarcade.core.ui.util.squishElasticPress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionEditBottomSheet(
    visibleSections: Set<LauncherSection>,
    onDismissRequest: () -> Unit,
    onToggleSection: (LauncherSection, Boolean) -> Unit,
    sheetState: SheetState = rememberArcadeSheetState()
) {
    ArcadeBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        title = stringResource(R.string.customize_home),
        subtitle = stringResource(R.string.customize_home_subtitle)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            LauncherSection.entries.forEach { section ->
                SectionToggleItem(
                    section = section,
                    isVisible = section in visibleSections,
                    onToggle = { onToggleSection(section, it) }
                )
            }
        }
    }
}

@Composable
private fun SectionToggleItem(
    section: LauncherSection,
    isVisible: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val title = when (section) {
        LauncherSection.SYSTEM_STATUS -> stringResource(R.string.system_status)
        LauncherSection.ANALYTICS -> stringResource(R.string.analytics)
        LauncherSection.FAVORITES -> stringResource(R.string.favorites)
        LauncherSection.RECENT_ACTIVITY -> stringResource(R.string.recent_activity)
    }
    val iconRes = when (section) {
        LauncherSection.SYSTEM_STATUS -> R.drawable.round_bolt_24
        LauncherSection.ANALYTICS -> R.drawable.round_bar_chart_24
        LauncherSection.FAVORITES -> R.drawable.round_favorite_24
        LauncherSection.RECENT_ACTIVITY -> R.drawable.round_history_24
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val panelShape = RoundedCornerShape(20.dp)

    // Authentic One UI 9 Frosted Glass Acrylic Surface
    val basePanelColor = if (isDark) {
        if (isVisible) Color(0xFF16233B).copy(alpha = 0.88f)
        else Color(0xFF101726).copy(alpha = 0.65f)
    } else {
        if (isVisible) Color(0xFFFFFFFF).copy(alpha = 0.85f)
        else Color(0xFFF1F5F9).copy(alpha = 0.68f)
    }

    val panelBackground by animateColorAsState(
        targetValue = if (isPressed) {
            if (isVisible) (if (isDark) primary.copy(alpha = 0.25f) else Color(0xFFE0F2FE))
            else basePanelColor.copy(alpha = 0.95f)
        } else {
            basePanelColor
        },
        animationSpec = tween(180),
        label = "panelBg_${section.name}"
    )

    // Directional specular highlight border (Top-left 20% white/accent, Bottom-right 8% white)
    val rowBorderBrush = remember(isDark, isVisible, isPressed, primary, tertiary) {
        if (isDark) {
            Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to (if (isVisible) primary.copy(alpha = if (isPressed) 0.65f else 0.40f) else Color.White.copy(alpha = if (isPressed) 0.30f else 0.16f)),
                    0.45f to (if (isVisible) tertiary.copy(alpha = if (isPressed) 0.35f else 0.18f) else Color.White.copy(alpha = 0.08f)),
                    1.0f to Color.White.copy(alpha = 0.04f)
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        } else {
            Brush.linearGradient(
                colorStops = arrayOf(
                    0.0f to (if (isVisible) Color(0xFF0284C7).copy(alpha = if (isPressed) 0.60f else 0.35f) else Color(0xFF64748B).copy(alpha = if (isPressed) 0.35f else 0.20f)),
                    0.45f to (if (isVisible) Color(0xFF0EA5E9).copy(alpha = if (isPressed) 0.35f else 0.18f) else Color(0xFF94A3B8).copy(alpha = 0.12f)),
                    1.0f to Color(0xFFCBD5E1).copy(alpha = 0.15f)
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        }
    }

    // Icon badge colors
    val iconBadgeBackground by animateColorAsState(
        targetValue = if (isVisible) {
            if (isDark) primary.copy(alpha = 0.22f) else Color(0xFFE0F2FE)
        } else {
            if (isDark) Color.White.copy(alpha = 0.08f) else Color(0xFF0F172A).copy(alpha = 0.05f)
        },
        animationSpec = tween(200),
        label = "iconBadgeBg_${section.name}"
    )
    val iconTint by animateColorAsState(
        targetValue = if (isVisible) {
            if (isDark) primary else Color(0xFF0284C7)
        } else {
            if (isDark) Color(0xFFA0AEC0) else Color(0xFF64748B)
        },
        animationSpec = tween(200),
        label = "iconTint_${section.name}"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isDark) {
            if (isVisible) Color(0xFFF4F7FB) else Color(0xFFA0AEC0)
        } else {
            if (isVisible) Color(0xFF0F172A) else Color(0xFF475569)
        },
        animationSpec = tween(200),
        label = "labelColor_${section.name}"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .squishElasticPress(interactionSource = interactionSource)
            .clip(panelShape)
            .background(panelBackground)
            // One UI 9 Milky Substrate
            .background(
                if (isDark) Color(0xFF121212).copy(alpha = 0.14f)
                else Color(0xFFFFFFFF).copy(alpha = 0.10f)
            )
            .border(1.dp, rowBorderBrush, panelShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onToggle(!isVisible) }
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Frosted Glass Icon Badge
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    Brush.radialGradient(
                        listOf(
                            iconBadgeBackground,
                            iconBadgeBackground.copy(alpha = iconBadgeBackground.alpha * 0.35f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            iconTint.copy(alpha = 0.50f),
                            Color.White.copy(alpha = 0.12f)
                        )
                    ),
                    shape = RoundedCornerShape(13.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        // Section Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (isVisible) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.15.sp
            ),
            color = labelColor,
            modifier = Modifier.weight(1f)
        )

        // Custom arcade toggle
        ArcadeToggle(
            checked = isVisible,
            onCheckedChange = onToggle
        )
    }
}