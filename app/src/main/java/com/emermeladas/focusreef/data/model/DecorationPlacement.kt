package com.emermeladas.focusreef.data.model

/**
 * Where a decoration is allowed to live inside a tank.
 */
enum class DecorationPlacement {
    /** Anchored to the sand strip: the player only chooses the horizontal spot. */
    FLOOR,

    /** Free x/y inside the water column, never buried in the sand. */
    FLOATING,
}
