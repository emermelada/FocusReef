package com.emermeladas.focusreef.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room row for a decoration the player placed in a tank.
 *
 * @property speciesName Name of a [com.emermeladas.focusreef.data.model.DecorationSpecies]
 * enum entry. Stored as a string to keep the schema readable.
 * @property tankId The tank this decoration sits in (indexed for grouping).
 * @property xBias Horizontal position in BiasAlignment units (-1..1).
 * @property yBias Vertical position in BiasAlignment units (-1..1).
 */
@Entity(
    tableName = "decorations",
    indices = [Index("tankId")],
)
data class DecorationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val speciesName: String,
    val tankId: Long,
    val xBias: Float,
    val yBias: Float,
)
