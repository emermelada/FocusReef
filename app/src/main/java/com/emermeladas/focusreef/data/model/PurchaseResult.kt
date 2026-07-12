package com.emermeladas.focusreef.data.model

/**
 * Outcome of a store purchase attempt.
 *
 * Purchases are validated inside the repository (balance and tank space), so
 * ViewModels only need to map these results to user-facing messages.
 */
sealed interface PurchaseResult {

    /** The purchase succeeded and was persisted. */
    data object Success : PurchaseResult

    /** The player's available balance is lower than the item price. */
    data object NotEnoughTokens : PurchaseResult

    /** No tank has enough free slots for the requested fish. */
    data object NotEnoughSpace : PurchaseResult

    /** The player's level is below the item's unlock level. */
    data class LevelTooLow(val requiredLevel: Int) : PurchaseResult
}
