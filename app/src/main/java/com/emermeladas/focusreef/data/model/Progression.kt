package com.emermeladas.focusreef.data.model

/**
 * The player's XP/level state, fully derived from the focus-block history.
 *
 * Nothing here is ever stored: because the history is append-only and the
 * level thresholds are monotone, the level can only ever go up.
 */
data class Progression(
    /** Total XP accumulated over the whole history (streak multipliers applied). */
    val totalXp: Long,
    /** Current level, starting at 1. Never decreases. */
    val level: Int,
    /** XP gathered inside the current level band. */
    val xpIntoLevel: Long,
    /** Total XP the current level band spans; reaching it means level + 1. */
    val xpForNextLevel: Long,
    /** Consecutive active days ending today (or yesterday, as grace before the day's first block). */
    val currentStreakDays: Int,
    /** The XP multiplier a block completed right now would earn, in percent. */
    val currentMultiplierPercent: Int,
)
