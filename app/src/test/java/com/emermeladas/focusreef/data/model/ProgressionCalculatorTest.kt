package com.emermeladas.focusreef.data.model

import com.emermeladas.focusreef.utils.GameConfig
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionCalculatorTest {

    private val today: LocalDate = LocalDate.of(2026, 7, 8)
    private val zone = ZoneOffset.UTC

    private var nextId = 1L

    /** [count] blocks on [date], starting 09:00 UTC, 30 minutes apart. */
    private fun blocksOn(date: LocalDate, count: Int = 1): List<FocusBlock> =
        (0 until count).map { index ->
            FocusBlock(
                id = nextId++,
                startEpochMillis = date.atTime(LocalTime.of(9, 0))
                    .plusMinutes(index * 30L)
                    .toInstant(zone)
                    .toEpochMilli(),
                durationMinutes = GameConfig.FOCUS_BLOCK_MINUTES,
            )
        }

    @Test
    fun `empty history is level 1 with no xp and no streak`() {
        val progression = ProgressionCalculator.calculate(emptyList(), today, zone)

        assertEquals(0L, progression.totalXp)
        assertEquals(1, progression.level)
        assertEquals(0, progression.currentStreakDays)
        assertEquals(GameConfig.BASE_MULTIPLIER_PERCENT, progression.currentMultiplierPercent)
    }

    @Test
    fun `multiplier tiers follow streak length`() {
        assertEquals(100, ProgressionCalculator.multiplierPercentFor(1))
        assertEquals(100, ProgressionCalculator.multiplierPercentFor(2))
        assertEquals(150, ProgressionCalculator.multiplierPercentFor(3))
        assertEquals(150, ProgressionCalculator.multiplierPercentFor(6))
        assertEquals(200, ProgressionCalculator.multiplierPercentFor(7))
        assertEquals(200, ProgressionCalculator.multiplierPercentFor(30))
    }

    @Test
    fun `xp applies the multiplier of each day's streak position`() {
        // Days 1-2 at x1.0, day 3 at x1.5: 10 + 10 + 15.
        val blocks = blocksOn(today.minusDays(2)) + blocksOn(today.minusDays(1)) + blocksOn(today)

        val progression = ProgressionCalculator.calculate(blocks, today, zone)

        assertEquals(35L, progression.totalXp)
        assertEquals(3, progression.currentStreakDays)
        assertEquals(150, progression.currentMultiplierPercent)
    }

    @Test
    fun `a gap resets the streak but keeps earned xp`() {
        // 3-day streak long ago (10+10+15), then a lone active day (10).
        val blocks =
            blocksOn(today.minusDays(30)) +
                blocksOn(today.minusDays(29)) +
                blocksOn(today.minusDays(28)) +
                blocksOn(today)

        val progression = ProgressionCalculator.calculate(blocks, today, zone)

        assertEquals(45L, progression.totalXp)
        assertEquals(1, progression.currentStreakDays)
    }

    @Test
    fun `yesterday's streak still counts before today's first block`() {
        val blocks = blocksOn(today.minusDays(2)) + blocksOn(today.minusDays(1))

        val progression = ProgressionCalculator.calculate(blocks, today, zone)

        assertEquals(2, progression.currentStreakDays)
        // A block today would be the 3rd streak day: tier 2.
        assertEquals(150, progression.currentMultiplierPercent)
    }

    @Test
    fun `streak broken two days ago shows zero`() {
        val blocks = blocksOn(today.minusDays(5)) + blocksOn(today.minusDays(4))

        val progression = ProgressionCalculator.calculate(blocks, today, zone)

        assertEquals(0, progression.currentStreakDays)
    }

    @Test
    fun `level thresholds follow the quadratic curve`() {
        assertEquals(1, ProgressionCalculator.levelForXp(0L))
        assertEquals(1, ProgressionCalculator.levelForXp(99L))
        assertEquals(2, ProgressionCalculator.levelForXp(100L))
        assertEquals(2, ProgressionCalculator.levelForXp(399L))
        assertEquals(3, ProgressionCalculator.levelForXp(400L))
        assertEquals(10, ProgressionCalculator.levelForXp(8100L))
    }

    @Test
    fun `progress within the level band is reported`() {
        // 15 active days in a row, 1 block each: 2x10 + 4x15 + 9x20 = 260 XP.
        val blocks = (14 downTo 0).flatMap { daysAgo -> blocksOn(today.minusDays(daysAgo.toLong())) }

        val progression = ProgressionCalculator.calculate(blocks, today, zone)

        assertEquals(260L, progression.totalXp)
        assertEquals(2, progression.level)
        assertEquals(160L, progression.xpIntoLevel) // 260 - 100
        assertEquals(300L, progression.xpForNextLevel) // 400 - 100
    }

    @Test
    fun `appending blocks never lowers the level`() {
        var blocks = emptyList<FocusBlock>()
        var previousLevel = 1
        for (daysAgo in 60 downTo 0) {
            blocks = blocks + blocksOn(today.minusDays(daysAgo.toLong()), count = 2)
            val level = ProgressionCalculator.calculate(blocks, today, zone).level
            assertTrue("level dropped from $previousLevel to $level", level >= previousLevel)
            previousLevel = level
        }
    }
}
