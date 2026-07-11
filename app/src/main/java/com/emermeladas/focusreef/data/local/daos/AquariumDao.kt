package com.emermeladas.focusreef.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.emermeladas.focusreef.data.local.entities.FishEntity
import com.emermeladas.focusreef.data.local.entities.TankEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data access for the aquarium state: tanks and the fish living in them.
 */
@Dao
interface AquariumDao {

    /** All tanks, oldest first, as a reactive stream. */
    @Query("SELECT * FROM tanks ORDER BY id")
    fun observeTanks(): Flow<List<TankEntity>>

    /** All owned fish, oldest first, as a reactive stream. */
    @Query("SELECT * FROM fish ORDER BY id")
    fun observeFish(): Flow<List<FishEntity>>

    /** Inserts a new tank and returns its generated id. */
    @Insert
    suspend fun insertTank(tank: TankEntity): Long

    /** Inserts a new fish and returns its generated id. */
    @Insert
    suspend fun insertFish(fish: FishEntity): Long
}
