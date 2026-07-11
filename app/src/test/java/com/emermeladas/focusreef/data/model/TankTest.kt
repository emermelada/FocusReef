package com.emermeladas.focusreef.data.model

import com.emermeladas.focusreef.utils.GameConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Slot accounting: fish of different species occupy different slot counts.
 */
class TankTest {

    private fun tankWith(vararg species: FishSpecies): Tank = Tank(
        id = 1,
        name = "Tank 1",
        capacitySlots = GameConfig.TANK_CAPACITY_SLOTS,
        fish = species.mapIndexed { index, s ->
            Fish(id = index.toLong(), species = s, tankId = 1)
        },
    )

    @Test
    fun `used slots sums species sizes`() {
        val tank = tankWith(FishSpecies.SMALL, FishSpecies.MEDIUM, FishSpecies.LARGE)
        assertEquals(1 + 3 + 5, tank.usedSlots)
        assertEquals(GameConfig.TANK_CAPACITY_SLOTS - 9, tank.freeSlots)
    }

    @Test
    fun `hasRoomFor respects remaining capacity`() {
        // Fill with large fish until fewer than 5 slots remain.
        val largeFishCount = GameConfig.TANK_CAPACITY_SLOTS / FishSpecies.LARGE.slots
        val nearlyFull = tankWith(*Array(largeFishCount) { FishSpecies.LARGE })

        // 24 / 5 → 4 large fish = 20 slots used, 4 free.
        assertTrue(nearlyFull.hasRoomFor(FishSpecies.SMALL))
        assertTrue(nearlyFull.hasRoomFor(FishSpecies.MEDIUM))
        assertFalse(nearlyFull.hasRoomFor(FishSpecies.LARGE))
    }
}
