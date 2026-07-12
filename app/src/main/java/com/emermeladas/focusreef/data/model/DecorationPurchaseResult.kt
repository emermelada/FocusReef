package com.emermeladas.focusreef.data.model

/**
 * Outcome of a decoration purchase attempt.
 */
sealed interface DecorationPurchaseResult {

    /** Bought and placed at the default spot; [decorationId] lets the UI open placement mode. */
    data class Success(val decorationId: Long) : DecorationPurchaseResult

    /** The wallet balance does not cover the price. */
    data object NotEnoughTokens : DecorationPurchaseResult

    /** The chosen tank already holds the decoration cap. */
    data object TankFull : DecorationPurchaseResult

    /** The player's level is below the item's unlock level. */
    data class LevelTooLow(val requiredLevel: Int) : DecorationPurchaseResult
}
