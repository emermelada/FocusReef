package com.emermeladas.focusreef.data.model

/**
 * The player's token economy at a point in time.
 *
 * Earned tokens come exclusively from focus blocks recorded on the NAS;
 * spent tokens come from the local purchase ledger. The app never stores the
 * balance itself — it is always derived, so the NAS remains the source of
 * truth for earnings.
 *
 * @property earnedTokens Total tokens ever earned (1 per focus block).
 * @property spentTokens Total tokens ever spent in the store.
 */
data class Wallet(
    val earnedTokens: Long,
    val spentTokens: Long,
) {
    /** Tokens currently available to spend. */
    val availableTokens: Long
        get() = earnedTokens - spentTokens

    /** True if the player can pay [priceTokens]. */
    fun canAfford(priceTokens: Long): Boolean = availableTokens >= priceTokens
}
