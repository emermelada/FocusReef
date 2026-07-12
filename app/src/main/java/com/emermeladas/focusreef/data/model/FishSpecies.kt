package com.emermeladas.focusreef.data.model

import com.emermeladas.focusreef.utils.GameConfig

/**
 * The purchasable fish species.
 *
 * The enum name is what gets persisted in Room, so renaming an entry is a
 * database migration — add new species instead of renaming existing ones.
 *
 * @property displayName Human-readable name shown in the UI.
 * @property slots Number of tank slots this species occupies.
 * @property priceTokens Store price in tokens.
 * @property unlockLevel Minimum player level required to buy this species.
 */
enum class FishSpecies(
    val displayName: String,
    val slots: Int,
    val priceTokens: Long,
    val unlockLevel: Int = GameConfig.DEFAULT_UNLOCK_LEVEL,
) {
    SMALL(displayName = "Small fish", slots = 1, priceTokens = GameConfig.SMALL_FISH_PRICE_TOKENS),
    MEDIUM(displayName = "Medium fish", slots = 3, priceTokens = GameConfig.MEDIUM_FISH_PRICE_TOKENS),
    LARGE(displayName = "Large fish", slots = 5, priceTokens = GameConfig.LARGE_FISH_PRICE_TOKENS),
    NEON(
        displayName = "Neon tetra",
        slots = 1,
        priceTokens = GameConfig.NEON_FISH_PRICE_TOKENS,
        unlockLevel = GameConfig.NEON_UNLOCK_LEVEL,
    ),
    ANGELFISH(
        displayName = "Angelfish",
        slots = 3,
        priceTokens = GameConfig.ANGELFISH_PRICE_TOKENS,
        unlockLevel = GameConfig.ANGELFISH_UNLOCK_LEVEL,
    ),
    BETTA(
        displayName = "Betta",
        slots = 1,
        priceTokens = GameConfig.BETTA_PRICE_TOKENS,
        unlockLevel = GameConfig.BETTA_UNLOCK_LEVEL,
    ),
    SHARK(
        displayName = "Reef shark",
        slots = 5,
        priceTokens = GameConfig.SHARK_PRICE_TOKENS,
        unlockLevel = GameConfig.SHARK_UNLOCK_LEVEL,
    ),
    LIONFISH(
        displayName = "Lionfish",
        slots = 3,
        priceTokens = GameConfig.LIONFISH_PRICE_TOKENS,
        unlockLevel = GameConfig.LIONFISH_UNLOCK_LEVEL,
    ),
    ORCA(
        displayName = "Orca",
        slots = 5,
        priceTokens = GameConfig.ORCA_PRICE_TOKENS,
        unlockLevel = GameConfig.ORCA_UNLOCK_LEVEL,
    ),
}
