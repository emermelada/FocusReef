package com.emermeladas.focusreef.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Append-only ledger of everything the player has bought.
 *
 * The wallet balance is derived as (tokens earned on the NAS) − SUM(this
 * table), so purchases are never deleted — that would refund tokens.
 *
 * @property description What was bought, e.g. "Medium fish" (for debugging/history UIs).
 * @property priceTokens Tokens paid.
 * @property epochMillis When the purchase happened.
 */
@Entity(tableName = "purchases")
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val description: String,
    val priceTokens: Long,
    val epochMillis: Long,
)
