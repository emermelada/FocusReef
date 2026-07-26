package com.emermeladas.focusreef.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring

/**
 * FocusReef motion vocabulary — the only source of springs, easings, and UI
 * transition durations.
 *
 * The app's motion voice is "underwater": slow starts, soft landings, nothing
 * snappy or bouncy. Every animated component picks from this object so all
 * motion across screens feels like it belongs to one body of water.
 * (Gameplay-feel tuning — bubble rates, swim speeds — lives in `GameConfig`.)
 */
object ReefMotion {

    /** Scale applied by `Modifier.pressable` while a surface is held. */
    const val PRESS_SCALE: Float = 0.97f

    /** Duration for switching between the three bottom-nav tabs. */
    const val TAB_TRANSITION_MS: Int = 280

    /** Duration for content reveals (chart bars growing, cards fading in). */
    const val REVEAL_MS: Int = 450

    /** Duration for animated number changes (token balance counting down). */
    const val COUNT_MS: Int = 700

    /**
     * Symmetric ease-in-out for ambient drifting (caustic blobs, bubble
     * wobble) — approximates a sine wave so loops have no visible seam.
     */
    val DriftEasing: Easing = CubicBezierEasing(0.45f, 0f, 0.55f, 1f)

    /** Decelerating easing for one-way reveals (enter, grow, fade-in). */
    val RevealEasing: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

    /**
     * The default interactive spring: barely under-damped so surfaces settle
     * quickly with only a hint of life. Used for press states and selections.
     */
    fun <T> gentleSpring(): SpringSpec<T> = spring(
        dampingRatio = 0.85f,
        stiffness = Spring.StiffnessMediumLow,
    )

    /**
     * The arrival spring: a slow, soft settle with one small overshoot, for
     * things entering the world (a new fish drifting into its tank).
     */
    fun <T> settleSpring(): SpringSpec<T> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow,
    )
}
