package off.kys.openarcade.features.games_launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import off.kys.openarcade.core.ui.components.ArcadeCard
import off.kys.openarcade.core.ui.components.SectionHeader
import off.kys.openarcade.core.ui.theme.BentoTokens

@Composable
fun SystemStatusSection(
    batteryLevel: Int,
    storageUsage: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionHeader(
            title = stringResource(R.string.system_status),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                title = stringResource(R.string.battery),
                value = stringResource(R.string.battery_percentage, batteryLevel),
                percentage = batteryLevel.toFloat(),
                icon = R.drawable.round_bolt_24,
                color = if (batteryLevel > 20) Color(0xFF00E5FF) else MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
            StatusCard(
                title = stringResource(R.string.storage),
                value = stringResource(R.string.storage_used, storageUsage),
                percentage = storageUsage.toFloat(),
                icon = R.drawable.round_explore_24,
                color = Color(0xFFFF9100),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatusCard(
    title: String,
    value: String,
    percentage: Float,
    icon: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val bentoShape = BentoTokens.LargeTileShape
    ArcadeCard(
        modifier = modifier,
        shape = bentoShape,
        accentColor = color
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Precision Concentric Luminous Dial Gauge (inspired by video concentric compass rings)
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.size(46.dp)) {
                    val stroke = 3.dp.toPx()
                    val centerOffset = Offset(size.width / 2f, size.height / 2f)
                    val arcRadius = (size.width - stroke) / 2f

                    // 1. Outer subtle concentric groove track
                    drawCircle(
                        color = Color.White.copy(alpha = 0.08f),
                        radius = arcRadius,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                    )

                    // 2. Glowing luminous progress arc
                    val sweep = (percentage.coerceIn(0f, 100f) / 100f) * 360f
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(
                                color.copy(alpha = 0.40f),
                                color,
                                Color.White
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = stroke,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    )

                    // 3. Inner neon glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(
                                color.copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            center = centerOffset,
                            radius = arcRadius
                        ),
                        radius = arcRadius,
                        center = centerOffset
                    )
                }

                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = androidx.compose.ui.unit.TextUnit(0.6f, androidx.compose.ui.unit.TextUnitType.Sp)
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.70f)
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black
                    ),
                    color = color
                )
            }
        }
    }
}
