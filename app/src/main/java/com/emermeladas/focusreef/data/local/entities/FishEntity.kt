package com.emermeladas.focusreef.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room row for a fish the player owns.
 *
 * @property speciesName Name of a [com.emermeladas.focusreef.data.model.FishSpecies]
 * enum entry. Stored as a string to keep the schema readable.
 * @property tankId The tank this fish lives in (indexed for the per-tank grouping query).
 */
@Entity(
    tableName = "fish",
    indices = [Index("tankId")],
)
data class FishEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val speciesName: String,
    val tankId: Long,
)
