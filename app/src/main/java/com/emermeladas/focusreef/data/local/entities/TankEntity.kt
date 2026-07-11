package com.emermeladas.focusreef.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room row for a tank the player owns.
 *
 * Capacity is not stored: every tank has [com.emermeladas.focusreef.utils.GameConfig.TANK_CAPACITY_SLOTS]
 * slots, so persisting it would just create a second source of truth.
 */
@Entity(tableName = "tanks")
data class TankEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
)
