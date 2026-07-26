package com.emermeladas.focusreef.data.model

import androidx.annotation.StringRes
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.utils.GameConfig

/**
 * The purchasable fish species.
 *
 * The enum name is what gets persisted in Room, so renaming an entry is a
 * database migration — add new species instead of renaming existing ones.
 * That is why [SMALL], [MEDIUM] and [LARGE] keep their size-based constant
 * names while presenting as real species: the constant is storage, the label
 * is product.
 *
 * Names are string resources rather than Kotlin literals so the store can be
 * translated — a name baked into the enum is a name that can never be
 * localized.
 *
 * @property displayNameRes Human-readable name shown in the UI.
 * @property slots Number of tank slots this species occupies.
 * @property priceTokens Store price in tokens.
 * @property unlockLevel Minimum player level required to buy this species.
 */
enum class FishSpecies(
    @param:StringRes val displayNameRes: Int,
    val slots: Int,
    val priceTokens: Long,
    val unlockLevel: Int = GameConfig.DEFAULT_UNLOCK_LEVEL,
) {
    SMALL(
        displayNameRes = R.string.fish_name_small,
        slots = 1,
        priceTokens = GameConfig.SMALL_FISH_PRICE_TOKENS,
    ),
    MEDIUM(
        displayNameRes = R.string.fish_name_medium,
        slots = 3,
        priceTokens = GameConfig.MEDIUM_FISH_PRICE_TOKENS,
    ),
    LARGE(
        displayNameRes = R.string.fish_name_large,
        slots = 5,
        priceTokens = GameConfig.LARGE_FISH_PRICE_TOKENS,
    ),
    NEON(
        displayNameRes = R.string.fish_name_neon,
        slots = 1,
        priceTokens = GameConfig.NEON_FISH_PRICE_TOKENS,
        unlockLevel = GameConfig.NEON_UNLOCK_LEVEL,
    ),
    ANGELFISH(
        displayNameRes = R.string.fish_name_angelfish,
        slots = 3,
        priceTokens = GameConfig.ANGELFISH_PRICE_TOKENS,
        unlockLevel = GameConfig.ANGELFISH_UNLOCK_LEVEL,
    ),
    BETTA(
        displayNameRes = R.string.fish_name_betta,
        slots = 1,
        priceTokens = GameConfig.BETTA_PRICE_TOKENS,
        unlockLevel = GameConfig.BETTA_UNLOCK_LEVEL,
    ),
    SHARK(
        displayNameRes = R.string.fish_name_shark,
        slots = 5,
        priceTokens = GameConfig.SHARK_PRICE_TOKENS,
        unlockLevel = GameConfig.SHARK_UNLOCK_LEVEL,
    ),
    LIONFISH(
        displayNameRes = R.string.fish_name_lionfish,
        slots = 3,
        priceTokens = GameConfig.LIONFISH_PRICE_TOKENS,
        unlockLevel = GameConfig.LIONFISH_UNLOCK_LEVEL,
    ),
    ORCA(
        displayNameRes = R.string.fish_name_orca,
        slots = 5,
        priceTokens = GameConfig.ORCA_PRICE_TOKENS,
        unlockLevel = GameConfig.ORCA_UNLOCK_LEVEL,
    ),
}
