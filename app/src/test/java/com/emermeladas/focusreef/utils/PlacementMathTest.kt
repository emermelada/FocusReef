package com.emermeladas.focusreef.utils

import com.emermeladas.focusreef.data.model.DecorationPlacement
import org.junit.Assert.assertEquals
import org.junit.Test

class PlacementMathTest {

    @Test
    fun `drag delta maps px to bias using the container-item range`() {
        // Container 300, item 100 → range 200 → 100px of drag = +1.0 bias.
        assertEquals(
            1f,
            PlacementMath.dragDeltaToBiasDelta(deltaPx = 100f, containerPx = 300f, itemPx = 100f),
        )
        assertEquals(
            -0.5f,
            PlacementMath.dragDeltaToBiasDelta(deltaPx = -50f, containerPx = 300f, itemPx = 100f),
        )
    }

    @Test
    fun `degenerate container returns zero delta`() {
        assertEquals(0f, PlacementMath.dragDeltaToBiasDelta(100f, containerPx = 80f, itemPx = 100f))
        assertEquals(0f, PlacementMath.dragDeltaToBiasDelta(100f, containerPx = 100f, itemPx = 100f))
    }

    @Test
    fun `floor clamp locks y to the sand and clamps x`() {
        val clamped = PlacementMath.clamp(BiasPoint(x = 2f, y = -0.4f), DecorationPlacement.FLOOR)

        assertEquals(PlacementMath.BIAS_RANGE_X, clamped.x)
        assertEquals(PlacementMath.FLOOR_Y_BIAS, clamped.y)
    }

    @Test
    fun `floating clamp keeps the item inside the water column`() {
        val tooHigh = PlacementMath.clamp(BiasPoint(0f, -2f), DecorationPlacement.FLOATING)
        val tooLow = PlacementMath.clamp(BiasPoint(-3f, 2f), DecorationPlacement.FLOATING)
        val inside = PlacementMath.clamp(BiasPoint(0.3f, 0.1f), DecorationPlacement.FLOATING)

        assertEquals(PlacementMath.FLOATING_MIN_Y, tooHigh.y)
        assertEquals(PlacementMath.FLOATING_MAX_Y, tooLow.y)
        assertEquals(-PlacementMath.BIAS_RANGE_X, tooLow.x)
        assertEquals(BiasPoint(0.3f, 0.1f), inside)
    }

    @Test
    fun `default positions respect each placement class`() {
        assertEquals(
            BiasPoint(0f, PlacementMath.FLOOR_Y_BIAS),
            PlacementMath.defaultPosition(DecorationPlacement.FLOOR),
        )
        val floating = PlacementMath.defaultPosition(DecorationPlacement.FLOATING)
        assertEquals(floating, PlacementMath.clamp(floating, DecorationPlacement.FLOATING))
    }
}
