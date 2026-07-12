package com.emermeladas.focusreef.ui.screens.store

import com.emermeladas.focusreef.data.model.DecorationSpecies
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.Progression
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.data.model.Wallet
import com.emermeladas.focusreef.utils.GameConfig

/**
 * Everything the Store screen needs to render.
 *
 * @property isLoading True until wallet and tanks have both emitted.
 * @property wallet Current token economy; null while loading.
 * @property tanks Current tanks (used to know if a fish fits anywhere).
 * @property progression Player level/streak; null while loading.
 */
data class StoreUiState(
    val isLoading: Boolean = true,
    val wallet: Wallet? = null,
    val tanks: List<Tank> = emptyList(),
    val progression: Progression? = null,
) {
    /** True if [species] is still locked behind a level requirement. */
    fun isFishLocked(species: FishSpecies): Boolean =
        (progression?.level ?: 1) < species.unlockLevel

    /** True if the Buy button for [species] should be enabled. */
    fun canBuyFish(species: FishSpecies): Boolean =
        !isFishLocked(species) &&
            wallet?.canAfford(species.priceTokens) == true &&
            tanks.any { it.hasRoomFor(species) }

    /** True if [species] is still locked behind a level requirement. */
    fun isDecorationLocked(species: DecorationSpecies): Boolean =
        (progression?.level ?: 1) < species.unlockLevel

    /** True if the Buy button for decoration [species] should be enabled. */
    fun canBuyDecoration(species: DecorationSpecies): Boolean =
        !isDecorationLocked(species) &&
            wallet?.canAfford(species.priceTokens) == true &&
            tanks.any { it.hasRoomForDecoration() }

    /** True if the Buy button for a new tank should be enabled. */
    val canBuyTank: Boolean
        get() = wallet?.canAfford(GameConfig.TANK_PRICE_TOKENS) == true
}
