package com.emermeladas.focusreef.utils

/**
 * Single source of truth for every number in the game economy.
 *
 * Balancing the game (prices, capacities, earn rate) must only ever require
 * touching this file — never hardcode these values elsewhere.
 */
object GameConfig {

    /** Duration of one focus block as produced by the desk. */
    const val FOCUS_BLOCK_MINUTES: Int = 25

    /** Tokens earned per completed focus block. */
    const val TOKENS_PER_FOCUS_BLOCK: Long = 1L

    /** Fish slots available in every tank. */
    const val TANK_CAPACITY_SLOTS: Int = 24

    /** Name given to the tank the player starts with. */
    const val STARTER_TANK_NAME: String = "Tank 1"

    /** Price of an additional tank, in tokens. */
    const val TANK_PRICE_TOKENS: Long = 60L

    /** Price of a small fish (1 slot), in tokens. */
    const val SMALL_FISH_PRICE_TOKENS: Long = 4L

    /** Price of a medium fish (3 slots), in tokens. */
    const val MEDIUM_FISH_PRICE_TOKENS: Long = 10L

    /** Price of a large fish (5 slots), in tokens. */
    const val LARGE_FISH_PRICE_TOKENS: Long = 18L

    // Progression (XP, streak multiplier, levels) -----------------------------

    /** Base XP granted per completed focus block, before the streak multiplier. */
    const val XP_PER_FOCUS_BLOCK: Long = 10L

    /** Multiplier applied while the streak is below [STREAK_TIER_2_DAYS], in percent. */
    const val BASE_MULTIPLIER_PERCENT: Int = 100

    /** Streak length (days) at which the second multiplier tier kicks in. */
    const val STREAK_TIER_2_DAYS: Int = 3

    /** XP multiplier for streaks of at least [STREAK_TIER_2_DAYS], in percent. */
    const val STREAK_TIER_2_PERCENT: Int = 150

    /** Streak length (days) at which the top multiplier tier kicks in. */
    const val STREAK_TIER_3_DAYS: Int = 7

    /** XP multiplier for streaks of at least [STREAK_TIER_3_DAYS], in percent. */
    const val STREAK_TIER_3_PERCENT: Int = 200

    /** Total XP required to be level n is `LEVEL_XP_FACTOR * (n - 1)^2`. */
    const val LEVEL_XP_FACTOR: Long = 100L
}
