package off.kys.openarcade.core.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class SystemStatus(
    val batteryLevel: Int = 0,
    val storageUsagePercentage: Int = 0,
    val hasUsageStatsPermission: Boolean = false
)
