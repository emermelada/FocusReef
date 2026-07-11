package com.emermeladas.focusreef.data.model

/**
 * Outcome of moving a fish between tanks.
 *
 * Like purchases, moves are validated inside the repository so ViewModels
 * only map results to user-facing messages.
 */
sealed interface MoveResult {

    /** The fish now lives in the destination tank. */
    data object Success : MoveResult

    /** The destination tank doesn't have enough free slots. */
    data object NotEnoughSpace : MoveResult

    /** No fish of that species remained in the source tank (stale UI). */
    data object NothingToMove : MoveResult
}
