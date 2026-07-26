package com.emermeladas.focusreef.data.model

import androidx.annotation.StringRes
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.utils.GameConfig

/**
 * The purchasable decoration items.
 *
 * The enum name is what gets persisted in Room, so renaming an entry is a
 * database migration — add new decorations instead of renaming existing ones.
 *
 * Names are string resources rather than Kotlin literals so the store can be
 * translated.
 *
 * @property displayNameRes Human-readable name shown in the UI.
 * @property priceTokens Store price in tokens.
 * @property unlockLevel Minimum player level required to buy this item.
 * @property placement Whether the item sits on the floor or floats in the water.
 */
enum class DecorationSpecies(
    @param:StringRes val displayNameRes: Int,
    val priceTokens: Long,
    val unlockLevel: Int = GameConfig.DEFAULT_UNLOCK_LEVEL,
    val placement: DecorationPlacement,
) {
    SHELL(
        displayNameRes = R.string.decoration_name_shell,
        priceTokens = GameConfig.SHELL_PRICE_TOKENS,
        placement = DecorationPlacement.FLOOR,
    ),
    ROCK(
        displayNameRes = R.string.decoration_name_rock,
        priceTokens = GameConfig.ROCK_PRICE_TOKENS,
        placement = DecorationPlacement.FLOOR,
    ),
    KELP(
        displayNameRes = R.string.decoration_name_kelp,
        priceTokens = GameConfig.KELP_PRICE_TOKENS,
        placement = DecorationPlacement.FLOOR,
    ),
    BUBBLER(
        displayNameRes = R.string.decoration_name_bubbler,
        priceTokens = GameConfig.BUBBLER_PRICE_TOKENS,
        unlockLevel = GameConfig.BUBBLER_UNLOCK_LEVEL,
        placement = DecorationPlacement.FLOATING,
    ),
    TREASURE_CHEST(
        displayNameRes = R.string.decoration_name_treasure_chest,
        priceTokens = GameConfig.CHEST_PRICE_TOKENS,
        unlockLevel = GameConfig.CHEST_UNLOCK_LEVEL,
        placement = DecorationPlacement.FLOOR,
    ),
    JELLYFISH(
        displayNameRes = R.string.decoration_name_jellyfish,
        priceTokens = GameConfig.JELLYFISH_PRICE_TOKENS,
        unlockLevel = GameConfig.JELLYFISH_UNLOCK_LEVEL,
        placement = DecorationPlacement.FLOATING,
    ),
}
