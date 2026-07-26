package com.emermeladas.focusreef.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive

/**
 * A continuous per-composition clock in seconds, driving every ambient layer
 * of a tank (caustics, bubbles, fish sway) from one frame callback instead of
 * one animation per element.
 *
 * Read the value inside a draw or `graphicsLayer` lambda so each tick costs
 * only a redraw, never a recomposition. When [running] is false (reduced
 * motion) the clock stays at 0 and everything driven by it renders one calm
 * static frame. The clock also stops automatically while the tank is
 * offscreen, because the effect leaves composition.
 */
@Composable
fun rememberReefClock(running: Boolean): State<Float> {
    val clock = remember { mutableFloatStateOf(0f) }
    if (running) {
        LaunchedEffect(Unit) {
            val startNanos = withFrameNanos { it }
            while (isActive) {
                withFrameNanos { now ->
                    clock.floatValue = (now - startNanos) / 1_000_000_000f
                }
            }
        }
    }
    return clock
}
