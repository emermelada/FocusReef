package com.emermeladas.focusreef.data.model

/**
 * A fish tank owned by the player, together with the fish living in it.
 *
 * @property id Unique identifier (Room primary key).
 * @property name Display name, e.g. "Tank 1".
 * @property capacitySlots Total fish slots in this tank.
 * @property fish The fish currently living in this tank.
 */
data class Tank(
    val id: Long,
    val name: String,
    val capacitySlots: Int,
    val fish: List<Fish>,
) {
    /** Slots currently occupied by fish (species have different sizes). */
    val usedSlots: Int
        get() = fish.sumOf { it.species.slots }

    /** Slots still available for new fish. */
    val freeSlots: Int
        get() = capacitySlots - usedSlots

    /** True if a fish of [species] fits in this tank. */
    fun hasRoomFor(species: FishSpecies): Boolean = freeSlots >= species.slots
}
