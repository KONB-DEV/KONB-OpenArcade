package off.kys.openarcade.core.ui.util

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween

object AnimationUtils {
    /**
     * Returns a tween animation spec if [reduceAnimations] is false, otherwise returns snap.
     */
    fun <T> animationSpec(
        reduceAnimations: Boolean,
        durationMillis: Int = 300
    ): AnimationSpec<T> {
        return if (reduceAnimations) {
            snap()
        } else {
            tween(durationMillis)
        }
    }

    /**
     * Returns a duration in millis, returning 0 if [reduceAnimations] is true.
     */
    fun duration(reduceAnimations: Boolean, durationMillis: Int): Int {
        return if (reduceAnimations) 0 else durationMillis
    }
}
