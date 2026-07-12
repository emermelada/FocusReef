package com.emermeladas.focusreef.data.model

import com.emermeladas.focusreef.utils.GameConfig

/**
 * The purchasable decoration items.
 *
 * The enum name is what gets persisted in Room, so renaming an entry is a
 * database migration — add new decorations instead of renaming existing ones.
 *
 * @property displayName Human-readable name shown in the UI.
 * @property priceTokens Store price in tokens.
 * @property unlockLevel Minimum player level required to buy this item.
 * @property placement Whether the item sits on the floor or floats in the water.
 */
enum class DecorationSpecies(
    val displayName: String,
    val priceTokens: Long,
    val unlockLevel: Int = GameConfig.DEFAULT_UNLOCK_LEVEL,
    val placement: DecorationPlacement,
) {
    SHELL(
        displayName = "Seashell",
        priceTokens = GameConfig.SHELL_PRICE_TOKENS,
        placement = DecorationPlacement.FLOOR,
    ),
    ROCK(
        displayName = "Rock",
        priceTokens = GameConfig.ROCK_PRICE_TOKENS,
        placement = DecorationPlacement.FLOOR,
    ),
    KELP(
        displayName = "Kelp plant",
        priceTokens = GameConfig.KELP_PRICE_TOKENS,
        placement = DecorationPlacement.FLOOR,
    ),
    BUBBLER(
        displayName = "Bubbler",
        priceTokens = GameConfig.BUBBLER_PRICE_TOKENS,
        unlockLevel = GameConfig.BUBBLER_UNLOCK_LEVEL,
        placement = DecorationPlacement.FLOATING,
    ),
    TREASURE_CHEST(
        displayName = "Treasure chest",
        priceTokens = GameConfig.CHEST_PRICE_TOKENS,
        unlockLevel = GameConfig.CHEST_UNLOCK_LEVEL,
        placement = DecorationPlacement.FLOOR,
    ),
    JELLYFISH(
        displayName = "Jellyfish",
        priceTokens = GameConfig.JELLYFISH_PRICE_TOKENS,
        unlockLevel = GameConfig.JELLYFISH_UNLOCK_LEVEL,
        placement = DecorationPlacement.FLOATING,
    ),
}
