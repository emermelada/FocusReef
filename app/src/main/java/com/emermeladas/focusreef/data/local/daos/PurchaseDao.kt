package com.emermeladas.focusreef.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.emermeladas.focusreef.data.local.entities.PurchaseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access for the purchase ledger (spent tokens).
 */
@Dao
interface PurchaseDao {

    /** Records a purchase in the ledger. */
    @Insert
    suspend fun insert(purchase: PurchaseEntity)

    /** Total tokens ever spent, as a reactive stream. Emits 0 for an empty ledger. */
    @Query("SELECT COALESCE(SUM(priceTokens), 0) FROM purchases")
    fun observeTotalSpent(): Flow<Long>

    /**
     * Wipes the ledger, refunding every token ever spent.
     *
     * Only the settings screen's "start over" calls this, and only in the same
     * transaction that deletes what those tokens bought — dropping one without
     * the other either invents or destroys currency.
     */
    @Query("DELETE FROM purchases")
    suspend fun deleteAll()
}
