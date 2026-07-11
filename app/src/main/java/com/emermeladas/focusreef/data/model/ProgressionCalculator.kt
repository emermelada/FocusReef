package com.emermeladas.focusreef.data.model

import com.emermeladas.focusreef.utils.GameConfig
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.sqrt

/**
 * Pure derivation of [Progression] from the focus-block history.
 *
 * Each block earns [GameConfig.XP_PER_FOCUS_BLOCK] multiplied by the streak
 * tier its calendar day belongs to, so the whole progression is deterministic
 * and replayable from the history alone. Kept free of Android dependencies
 * (and with `today`/`zone` as parameters) so it is trivially unit-testable,
 * mirroring [com.emermeladas.focusreef.ui.screens.stats.StatsAggregator].
 */
object ProgressionCalculator {

    /**
     * @param blocks Raw focus-block history.
     * @param today The current date — passed in, never read from the clock here.
     * @param zone Timezone used to bucket blocks into calendar days.
     */
    fun calculate(blocks: List<FocusBlock>, today: LocalDate, zone: ZoneId): Progression {
        // Bucket block counts per calendar day; everything derives from this.
        val blocksPerDay = mutableMapOf<LocalDate, Int>()
        for (block in blocks) {
            val day = Instant.ofEpochMilli(block.startEpochMillis).atZone(zone).toLocalDate()
            blocksPerDay.merge(day, 1, Int::plus)
        }

        // Walk active days in order, tracking the streak each day belongs to.
        val activeDays = blocksPerDay.keys.sorted()
        var totalXp = 0L
        var streak = 0
        var previousDay: LocalDate? = null
        val streakByDay = mutableMapOf<LocalDate, Int>()
        for (day in activeDays) {
            streak = if (previousDay?.plusDays(1) == day) streak + 1 else 1
            streakByDay[day] = streak
            previousDay = day
            totalXp += blocksPerDay.getValue(day) *
                GameConfig.XP_PER_FOCUS_BLOCK * multiplierPercentFor(streak) / 100
        }

        // The visible streak ends today, or yesterday as grace before the
        // day's first block lands. XP above is unaffected by the grace.
        val currentStreak = streakByDay[today]
            ?: streakByDay[today.minusDays(1)]
            ?: 0

        val level = levelForXp(totalXp)
        val currentLevelFloor = xpToReachLevel(level)
        val nextLevelFloor = xpToReachLevel(level + 1)

        return Progression(
            totalXp = totalXp,
            level = level,
            xpIntoLevel = totalXp - currentLevelFloor,
            xpForNextLevel = nextLevelFloor - currentLevelFloor,
            currentStreakDays = currentStreak,
            // A block landing now would extend the streak through today.
            currentMultiplierPercent = multiplierPercentFor(
                if (streakByDay.containsKey(today)) currentStreak else currentStreak + 1,
            ),
        )
    }

    /** XP multiplier (percent) earned by a block whose day sits at [streakDays] in a streak. */
    fun multiplierPercentFor(streakDays: Int): Int = when {
        streakDays >= GameConfig.STREAK_TIER_3_DAYS -> GameConfig.STREAK_TIER_3_PERCENT
        streakDays >= GameConfig.STREAK_TIER_2_DAYS -> GameConfig.STREAK_TIER_2_PERCENT
        else -> GameConfig.BASE_MULTIPLIER_PERCENT
    }

    /** Total XP needed to be [level]; inverse of [levelForXp]. */
    fun xpToReachLevel(level: Int): Long =
        GameConfig.LEVEL_XP_FACTOR * (level - 1L) * (level - 1L)

    /** Level for [totalXp]: `1 + floor(sqrt(xp / factor))`. */
    fun levelForXp(totalXp: Long): Int {
        var level = 1 + sqrt(totalXp.toDouble() / GameConfig.LEVEL_XP_FACTOR).toInt()
        // Guard against floating-point rounding right at a threshold.
        while (xpToReachLevel(level + 1) <= totalXp) level++
        while (level > 1 && xpToReachLevel(level) > totalXp) level--
        return level
    }
}
