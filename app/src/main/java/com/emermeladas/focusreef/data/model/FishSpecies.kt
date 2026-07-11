package com.emermeladas.focusreef.data.model

import com.emermeladas.focusreef.utils.GameConfig

/**
 * The three purchasable fish species.
 *
 * The enum name is what gets persisted in Room, so renaming an entry is a
 * database migration — add new species instead of renaming existing ones.
 *
 * @property displayName Human-readable name shown in the UI.
 * @property slots Number of tank slots this species occupies.
 * @property priceTokens Store price in tokens.
 */
enum class FishSpecies(
    val displayName: String,
    val slots: Int,
    val priceTokens: Long,
) {
    SMALL(displayName = "Small fish", slots = 1, priceTokens = GameConfig.SMALL_FISH_PRICE_TOKENS),
    MEDIUM(displayName = "Medium fish", slots = 3, priceTokens = GameConfig.MEDIUM_FISH_PRICE_TOKENS),
    LARGE(displayName = "Large fish", slots = 5, priceTokens = GameConfig.LARGE_FISH_PRICE_TOKENS),
}
