package off.kys.openarcade.core.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.staticCompositionLocalOf
import off.kys.openarcade.core.data.local.preferences.ArcadePreferences

/**
 * Contract for nuanced, tactile haptic responses tailored to the Liquid UI experience.
 * Emulates physical mechanical switches, liquid glass friction, and spring detents.
 */
interface HapticFeedbackService {
    fun cardClick()
    fun cardExpand()
    fun cardCollapse()
    fun cardLongPress()
    fun navigationTransition()
    fun tabSelect()
    fun toggleSwitch(checked: Boolean)
    fun detent()
    fun actionLaunch()
    fun success()
    fun error()
}

/**
 * Concrete implementation utilizing Android's modern [VibratorManager] and [VibrationEffect] APIs,
 * dynamically respecting user settings stored in [ArcadePreferences].
 */
class HapticFeedbackServiceImpl(
    private val context: Context,
    private val prefs: ArcadePreferences
) : HapticFeedbackService {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private fun isHapticsEnabled(): Boolean = prefs.hapticFeedback.value

    private fun vibratePredefinedOrOneShot(predefinedEffect: Int, durationMs: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        try {
            if (!isHapticsEnabled()) return
            val v = vibrator ?: return
            if (!v.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createPredefined(predefinedEffect))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(durationMs)
            }
        } catch (_: Throwable) {
            // Graceful fallback for devices with proprietary/restricted haptic drivers or security restrictions
        }
    }

    private fun vibrateWaveform(timings: LongArray, amplitudes: IntArray) {
        try {
            if (!isHapticsEnabled()) return
            val v = vibrator ?: return
            if (!v.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(timings, -1)
            }
        } catch (_: Throwable) {
            // Graceful fallback
        }
    }

    override fun cardClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibratePredefinedOrOneShot(VibrationEffect.EFFECT_CLICK, 18L, 160)
        } else {
            vibratePredefinedOrOneShot(0, 18L, 160)
        }
    }

    override fun cardExpand() {
        // Double-tick pop simulating liquid spring expansion
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrateWaveform(
                longArrayOf(0, 12, 20, 22),
                intArrayOf(0, 140, 0, 220)
            )
        } else {
            vibratePredefinedOrOneShot(0, 24L, 200)
        }
    }

    override fun cardCollapse() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibratePredefinedOrOneShot(VibrationEffect.EFFECT_TICK, 14L, 110)
        } else {
            vibratePredefinedOrOneShot(0, 14L, 110)
        }
    }

    override fun cardLongPress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibratePredefinedOrOneShot(VibrationEffect.EFFECT_HEAVY_CLICK, 45L, 240)
        } else {
            vibratePredefinedOrOneShot(0, 45L, 240)
        }
    }

    override fun navigationTransition() {
        // Fluid, ultra-soft nudge matching spring slide/scale screen transition
        if (!isHapticsEnabled()) return
        val v = vibrator ?: return
        if (!v.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(12L, 85))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(12L)
            }
        } catch (_: Exception) {
            // Graceful fallback
        }
    }

    override fun tabSelect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibratePredefinedOrOneShot(VibrationEffect.EFFECT_TICK, 10L, 90)
        } else {
            vibratePredefinedOrOneShot(0, 10L, 90)
        }
    }

    override fun toggleSwitch(checked: Boolean) {
        if (checked) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibratePredefinedOrOneShot(VibrationEffect.EFFECT_CLICK, 20L, 180)
            } else {
                vibratePredefinedOrOneShot(0, 20L, 180)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibratePredefinedOrOneShot(VibrationEffect.EFFECT_TICK, 14L, 110)
            } else {
                vibratePredefinedOrOneShot(0, 14L, 110)
            }
        }
    }

    override fun detent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibratePredefinedOrOneShot(VibrationEffect.EFFECT_HEAVY_CLICK, 35L, 210)
        } else {
            vibratePredefinedOrOneShot(0, 35L, 210)
        }
    }

    override fun actionLaunch() {
        // Energetic twin-pulse sequence for game launch sequence
        vibrateWaveform(
            longArrayOf(0, 25, 30, 45),
            intArrayOf(0, 190, 0, 255)
        )
    }

    override fun success() {
        vibrateWaveform(
            longArrayOf(0, 20, 35, 35),
            intArrayOf(0, 160, 0, 230)
        )
    }

    override fun error() {
        vibrateWaveform(
            longArrayOf(0, 30, 40, 30, 40, 30),
            intArrayOf(0, 220, 0, 220, 0, 220)
        )
    }
}

/**
 * No-op fallback implementation for previews and environments where haptic services are unavailable.
 */
class NoOpHapticFeedbackService : HapticFeedbackService {
    override fun cardClick() {}
    override fun cardExpand() {}
    override fun cardCollapse() {}
    override fun cardLongPress() {}
    override fun navigationTransition() {}
    override fun tabSelect() {}
    override fun toggleSwitch(checked: Boolean) {}
    override fun detent() {}
    override fun actionLaunch() {}
    override fun success() {}
    override fun error() {}
}

/**
 * CompositionLocal providing access to [HapticFeedbackService] throughout the UI hierarchy.
 */
val LocalHapticFeedbackService = staticCompositionLocalOf<HapticFeedbackService> {
    NoOpHapticFeedbackService()
}
