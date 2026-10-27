package off.kys.openarcade.core.util

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import off.kys.openarcade.core.domain.model.GameCategory
import off.kys.openarcade.core.domain.model.GameEntry

class GameScanner(private val context: Context) {
    fun fetchInstalledGames(extraPackages: List<String> = emptyList()): List<GameEntry> {
        Log.d("GameScanner", "Scanning for games. Extra packages: $extraPackages")
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val launcherActivities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }

        Log.d("GameScanner", "Found ${launcherActivities.size} launcher activities")

        val detectedGames = mutableListOf<GameEntry>()
        val seenPackages = mutableSetOf<String>()

        for (resolveInfo in launcherActivities) {
            val activityInfo = resolveInfo.activityInfo
            val appInfo = activityInfo.applicationInfo
            val packageName = appInfo.packageName
            
            if (seenPackages.contains(packageName)) continue

            val isGame = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appInfo.category == ApplicationInfo.CATEGORY_GAME
            } else {
                @Suppress("DEPRECATION")
                (appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0
            }

            if (isGame || extraPackages.contains(packageName)) {
                Log.d("GameScanner", "Detected game: $packageName (isGame: $isGame)")
                val title = appInfo.loadLabel(pm).toString()
                val icon = appInfo.loadIcon(pm)
                
                val lastUpdateTime = try {
                    pm.getPackageInfo(packageName, 0).lastUpdateTime
                } catch (_: Exception) {
                    0L
                }

                val palette = try {
                    Palette.from(icon.toBitmap()).generate()
                } catch (_: Exception) {
                    null
                }
                val primarySwatch = palette?.vibrantSwatch ?: palette?.dominantSwatch
                val secondarySwatch = palette?.mutedSwatch ?: palette?.lightVibrantSwatch
                val tertiarySwatch = palette?.darkVibrantSwatch ?: palette?.darkMutedSwatch

                val primaryColor = primarySwatch?.rgb ?: 0xFF2A2A2A.toInt()
                val onPrimaryColor = primarySwatch?.titleTextColor ?: 0xFFFFFFFF.toInt()
                val secondaryColor = secondarySwatch?.rgb ?: primaryColor
                val tertiaryColor = tertiarySwatch?.rgb ?: primaryColor

                detectedGames.add(
                    GameEntry(
                        packageName = packageName,
                        title = title,
                        category = if (isGame) GameCategory.UNDEFINED else GameCategory.UTILITY,
                        isInstalled = true,
                        primaryColorArgb = primaryColor,
                        onPrimaryColorArgb = onPrimaryColor,
                        secondaryColorArgb = secondaryColor,
                        tertiaryColorArgb = tertiaryColor,
                        lastAppUpdateTime = lastUpdateTime,
                        isManuallyAdded = !isGame
                    )
                )
                seenPackages.add(packageName)
            }
        }

        // UI fallback if user lacks mobile games
        if (detectedGames.isEmpty() && extraPackages.isEmpty()) {
            Log.d("GameScanner", "No games found, using fallback")
            val icon = context.applicationInfo.loadIcon(pm)
            val palette = try {
                Palette.from(icon.toBitmap()).generate()
            } catch (_: Exception) {
                null
            }
            val primarySwatch = palette?.vibrantSwatch ?: palette?.dominantSwatch

            val primaryColor = primarySwatch?.rgb ?: 0xFF2A2A2A.toInt()

            return listOf(
                GameEntry(
                    packageName = context.packageName,
                    title = "System Deck (Fallback)",
                    category = GameCategory.UTILITY,
                    primaryColorArgb = primaryColor,
                    onPrimaryColorArgb = primarySwatch?.titleTextColor ?: 0xFFFFFFFF.toInt(),
                    secondaryColorArgb = palette?.mutedSwatch?.rgb ?: primaryColor,
                    tertiaryColorArgb = palette?.darkVibrantSwatch?.rgb ?: primaryColor
                )
            )
        }

        Log.d("GameScanner", "Scan complete. Found ${detectedGames.size} games")
        return detectedGames
    }
}
