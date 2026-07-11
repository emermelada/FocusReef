package com.emermeladas.focusreef.ui.screens.store

import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.data.model.Wallet
import com.emermeladas.focusreef.utils.GameConfig

/**
 * Everything the Store screen needs to render.
 *
 * @property isLoading True until wallet and tanks have both emitted.
 * @property wallet Current token economy; null while loading.
 * @property tanks Current tanks (used to know if a fish fits anywhere).
 */
data class StoreUiState(
    val isLoading: Boolean = true,
    val wallet: Wallet? = null,
    val tanks: List<Tank> = emptyList(),
) {
    /** True if the Buy button for [species] should be enabled. */
    fun canBuyFish(species: FishSpecies): Boolean =
        wallet?.canAfford(species.priceTokens) == true &&
            tanks.any { it.hasRoomFor(species) }

    /** True if the Buy button for a new tank should be enabled. */
    val canBuyTank: Boolean
        get() = wallet?.canAfford(GameConfig.TANK_PRICE_TOKENS) == true
}
