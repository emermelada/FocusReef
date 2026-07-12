package com.emermeladas.focusreef.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emermeladas.focusreef.data.local.daos.AquariumDao
import com.emermeladas.focusreef.data.local.daos.PurchaseDao
import com.emermeladas.focusreef.data.local.entities.DecorationEntity
import com.emermeladas.focusreef.data.local.entities.FishEntity
import com.emermeladas.focusreef.data.local.entities.PurchaseEntity
import com.emermeladas.focusreef.data.local.entities.TankEntity

/**
 * Local Room database holding everything the player owns.
 *
 * The NAS holds the focus-block history (earnings); this database holds the
 * app-side game state: tanks, fish and the purchase ledger.
 */
@Database(
    entities = [
        TankEntity::class,
        FishEntity::class,
        PurchaseEntity::class,
        DecorationEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class FocusReefDatabase : RoomDatabase() {

    abstract fun aquariumDao(): AquariumDao

    abstract fun purchaseDao(): PurchaseDao

    companion object {
        /** Database file name on disk. */
        const val NAME = "focusreef.db"
    }
}
