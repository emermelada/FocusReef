package com.emermeladas.focusreef.data.model

/**
 * A fish the player owns, living in one of their tanks.
 *
 * @property id Unique identifier (Room primary key).
 * @property species Which of the three species this fish is.
 * @property tankId Identifier of the [Tank] it lives in.
 */
data class Fish(
    val id: Long,
    val species: FishSpecies,
    val tankId: Long,
)
