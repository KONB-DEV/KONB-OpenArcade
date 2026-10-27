package off.kys.openarcade.core.data.repository

import android.app.AppOpsManager
import android.app.Application
import android.app.usage.StorageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.Process
import android.os.StatFs
import android.os.storage.StorageManager
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import off.kys.openarcade.core.domain.model.GameEntry
import off.kys.openarcade.core.domain.model.SystemStatus
import off.kys.openarcade.core.domain.repository.SystemRepository
import off.kys.openarcade.core.util.GameScanner
import java.io.File

class SystemRepositoryImpl(
    private val application: Application,
    private val gameScanner: GameScanner
) : SystemRepository {

    override fun fetchInstalledGames(extraPackages: List<String>): List<GameEntry> {
        return gameScanner.fetchInstalledGames(extraPackages)
    }

    override fun getSystemStatus(): Flow<SystemStatus> = callbackFlow {
        val batteryReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                val batteryLevel = if (level != -1 && scale != -1) {
                    (level * 100 / scale.toFloat()).toInt()
                } else 0
                
                launch(Dispatchers.IO) {
                    trySend(SystemStatus(
                        batteryLevel = batteryLevel,
                        storageUsagePercentage = getStorageUsagePercentage(),
                        hasUsageStatsPermission = hasUsageStatsPermission()
                    ))
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        application.registerReceiver(batteryReceiver, filter)

        awaitClose {
            application.unregisterReceiver(batteryReceiver)
        }
    }.onStart {
        emit(SystemStatus(
            batteryLevel = 0,
            storageUsagePercentage = getStorageUsagePercentage(),
            hasUsageStatsPermission = hasUsageStatsPermission()
        ))
    }.flowOn(Dispatchers.IO)
    .conflate()

    override fun launchGame(packageName: String): Boolean {
        val pm = application.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(packageName)
        return if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(launchIntent)
            true
        } else {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "market://details?id=$packageName".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                application.startActivity(intent)
                true
            } catch (_: Exception) {
                val webIntent = Intent(Intent.ACTION_VIEW).apply {
                    data = "https://play.google.com/store/apps/details?id=$packageName".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    application.startActivity(webIntent)
                    true
                } catch (_: Exception) {
                    false
                }
            }
        }
    }

    override fun hasUsageStatsPermission(): Boolean {
        val appOps = application.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            @Suppress("DEPRECATION")
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                application.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                application.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    override fun getGameOrientation(packageName: String): Int {
        val pm = application.packageManager
        val intent = pm.getLaunchIntentForPackage(packageName) ?: return ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
        } else {
            @Suppress("DEPRECATION")
            pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }
        return resolveInfo?.activityInfo?.screenOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun getStorageUsagePercentage(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getStorageUsageOreo()
        } else {
            getStorageUsageLegacy()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStorageUsageOreo(): Int {
        return try {
            val storageStatsManager =
                application.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val totalBytes = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
            val freeBytes = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT)
            val usedBytes = totalBytes - freeBytes
            (usedBytes.toFloat() / totalBytes * 100).toInt()
        } catch (_: Exception) {
            getStorageUsageLegacy()
        }
    }

    private fun getStorageUsageLegacy(): Int = try {
        val stat = StatFs(File("/data").path)
        val totalBytes = stat.blockCountLong * stat.blockSizeLong
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
        val usedBytes = totalBytes - availableBytes
        (usedBytes.toFloat() / totalBytes * 100).toInt()
    } catch (_: Exception) {
        0
    }
}
