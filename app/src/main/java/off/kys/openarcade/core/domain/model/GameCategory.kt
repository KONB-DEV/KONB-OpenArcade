package off.kys.openarcade.core.domain.model

import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import off.kys.openarcade.R

@Serializable
enum class GameCategory(@param:StringRes val displayNameRes: Int) {
    UNDEFINED(0),
    UTILITY(R.string.category_utility)
}