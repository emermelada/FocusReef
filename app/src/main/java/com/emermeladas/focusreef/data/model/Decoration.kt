package com.emermeladas.focusreef.data.model

/**
 * One placed decoration the player owns.
 *
 * @property xBias Horizontal position inside the tank in BiasAlignment units (-1..1).
 * @property yBias Vertical position inside the tank in BiasAlignment units (-1..1).
 */
data class Decoration(
    val id: Long,
    val species: DecorationSpecies,
    val tankId: Long,
    val xBias: Float,
    val yBias: Float,
)
