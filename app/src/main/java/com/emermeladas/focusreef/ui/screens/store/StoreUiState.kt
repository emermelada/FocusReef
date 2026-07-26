package com.emermeladas.focusreef.ui.screens.store

import androidx.annotation.StringRes
import com.emermeladas.focusreef.data.model.DecorationSpecies
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.Progression
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.data.model.Wallet
import com.emermeladas.focusreef.utils.GameConfig

/**
 * Why a store item can or cannot be bought right now.
 *
 * This deliberately replaces the old `canBuyX(): Boolean`. A boolean can only
 * grey a button out; it throws away the one thing the player actually needs,
 * which is *why*. The project's own spec requires a disabled purchase to state
 * its reason, and that is impossible to honour downstream of a Boolean.
 */
sealed interface BuyState {

    /** The player can buy this item now. */
    data object Available : BuyState

    /** Hidden behind a level requirement. */
    data class LockedByLevel(val requiredLevel: Int) : BuyState

    /** Affordable eventually, but the wallet is short by [missingTokens]. */
    data class NotEnoughTokens(val missingTokens: Long) : BuyState

    /** No tank has room for this item. */
    data object NoSlots : BuyState

    /** True only for [Available] — for the button's `enabled` flag. */
    val isAvailable: Boolean get() = this is Available
}

/**
 * Everything the Store screen needs to render.
 *
 * @property isLoading True until wallet and tanks have both emitted.
 * @property wallet Current token economy; null while loading.
 * @property tanks Current tanks (used to know if a fish fits anywhere).
 * @property progression Player level/streak; null while loading.
 * @property errorRes Why the load failed, or null if it did not.
 */
data class StoreUiState(
    val isLoading: Boolean = true,
    val wallet: Wallet? = null,
    val tanks: List<Tank> = emptyList(),
    val progression: Progression? = null,
    @param:StringRes val errorRes: Int? = null,
) {
    /** The player's level, defaulting to the starting level while loading. */
    private val level: Int get() = progression?.level ?: 1

    /** True if [species] is still locked behind a level requirement. */
    fun isFishLocked(species: FishSpecies): Boolean = level < species.unlockLevel

    /** True if [species] is still locked behind a level requirement. */
    fun isDecorationLocked(species: DecorationSpecies): Boolean = level < species.unlockLevel

    /** Whether [species] can be bought, and if not, what is blocking it. */
    fun buyState(species: FishSpecies): BuyState = resolveBuyState(
        unlockLevel = species.unlockLevel,
        price = species.priceTokens,
        hasRoom = tanks.any { it.hasRoomFor(species) },
    )

    /** Whether decoration [species] can be bought, and if not, why not. */
    fun buyState(species: DecorationSpecies): BuyState = resolveBuyState(
        unlockLevel = species.unlockLevel,
        price = species.priceTokens,
        hasRoom = tanks.any { it.hasRoomForDecoration() },
    )

    /** Whether another tank can be bought. Tanks never run out of room. */
    val tankBuyState: BuyState
        get() = resolveBuyState(
            unlockLevel = GameConfig.DEFAULT_UNLOCK_LEVEL,
            price = GameConfig.TANK_PRICE_TOKENS,
            hasRoom = true,
        )

    /**
     * The blocking reasons in the order the player can act on them: a level
     * gate cannot be solved by spending, and tokens cannot be solved by
     * freeing slots, so the earliest blocker is the useful one to report.
     */
    private fun resolveBuyState(
        unlockLevel: Int,
        price: Long,
        hasRoom: Boolean,
    ): BuyState {
        if (level < unlockLevel) return BuyState.LockedByLevel(unlockLevel)
        // While the wallet is still loading nothing is buyable, but the honest
        // reason is "not yet known", so report the gentler token shortfall.
        val available = wallet?.availableTokens ?: 0L
        if (available < price) return BuyState.NotEnoughTokens(price - available)
        if (!hasRoom) return BuyState.NoSlots
        return BuyState.Available
    }
}
