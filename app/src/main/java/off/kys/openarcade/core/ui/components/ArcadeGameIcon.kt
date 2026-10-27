package off.kys.openarcade.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ArcadeGameIcon(
    icon: Any?, // Can be packageName (String) or customPath (String)
    contentDescription: String?,
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    tertiaryColor: Color = MaterialTheme.colorScheme.tertiary,
    iconSize: Dp = 48.dp
) {
    val context = LocalContext.current
    val imageModel = remember(icon) {
        val iconStr = icon as? String ?: return@remember icon
        if (iconStr.contains("/") || iconStr.contains("content://")) {
            // Likely a file path or URI
            iconStr
        } else {
            // Likely a package name, fetch drawable
            try {
                context.packageManager.getApplicationIcon(iconStr)
            } catch (_: Exception) {
                null
            }
        }
    }

    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        primaryColor.copy(alpha = 0.25f),
                        primaryColor.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Ambient soft color bloom behind icon
        Box(
            modifier = Modifier
                .size(iconSize + 16.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.35f),
                            tertiaryColor.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // One UI 9 / ColorOS 17 Squircle Icon Container
        Card(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
            ),
            modifier = Modifier
                .padding(vertical = 12.dp)
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.60f),
                            primaryColor.copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .size(iconSize)
                        .padding(6.dp),
                    contentScale = ContentScale.Fit
                )

                // Glass specular shine on icon surface
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.18f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }

        // Bottom liquid accent line
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(1.5.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            primaryColor.copy(alpha = 0.80f),
                            tertiaryColor.copy(alpha = 0.50f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}
