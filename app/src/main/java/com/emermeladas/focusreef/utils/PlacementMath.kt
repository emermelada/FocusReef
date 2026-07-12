package com.emermeladas.focusreef.utils

import com.emermeladas.focusreef.data.model.DecorationPlacement

/** A position inside a tank in BiasAlignment units (-1..1 on both axes). */
data class BiasPoint(val x: Float, val y: Float)

/**
 * Pure math for placing decorations inside a tank.
 *
 * Positions use Compose BiasAlignment units so they are independent of the
 * tank's rendered size. Kept free of Compose imports so it is trivially
 * unit-testable.
 */
object PlacementMath {

    /** Horizontal clamp so items never touch the tank walls. */
    const val BIAS_RANGE_X = 0.9f

    /** Floor items are bottom-edge aligned: their base sits in the sand strip. */
    const val FLOOR_Y_BIAS = 1f

    /** Highest a floating item may go (just under the surface). */
    const val FLOATING_MIN_Y = -0.85f

    /** Lowest a floating item may go — conservative so it never sinks into the sand. */
    const val FLOATING_MAX_Y = 0.55f

    /**
     * Converts a drag delta in pixels to a bias delta.
     *
     * BiasAlignment places an item's top-left at `(container - item) / 2 * (1 + bias)`,
     * so one pixel of drag equals `2 / (container - item)` bias.
     */
    fun dragDeltaToBiasDelta(deltaPx: Float, containerPx: Float, itemPx: Float): Float {
        val range = containerPx - itemPx
        if (range <= 0f) return 0f
        return deltaPx * 2f / range
    }

    /** Clamps [point] to where a [placement]-class decoration may live. */
    fun clamp(point: BiasPoint, placement: DecorationPlacement): BiasPoint = BiasPoint(
        x = point.x.coerceIn(-BIAS_RANGE_X, BIAS_RANGE_X),
        y = when (placement) {
            DecorationPlacement.FLOOR -> FLOOR_Y_BIAS
            DecorationPlacement.FLOATING -> point.y.coerceIn(FLOATING_MIN_Y, FLOATING_MAX_Y)
        },
    )

    /** Where a freshly bought decoration lands before the player places it. */
    fun defaultPosition(placement: DecorationPlacement): BiasPoint = when (placement) {
        DecorationPlacement.FLOOR -> BiasPoint(0f, FLOOR_Y_BIAS)
        DecorationPlacement.FLOATING -> BiasPoint(0f, -0.2f)
    }
}
