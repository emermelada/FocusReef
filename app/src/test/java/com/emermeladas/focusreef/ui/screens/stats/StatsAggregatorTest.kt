package com.emermeladas.focusreef.ui.screens.stats

import com.emermeladas.focusreef.data.model.FocusBlock
import com.emermeladas.focusreef.utils.GameConfig
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pure-JVM tests for [StatsAggregator]. Uses a fixed `today` and UTC so the
 * results are stable regardless of when/where the tests run.
 */
class StatsAggregatorTest {

    private val zone = ZoneOffset.UTC

    /** Wednesday — so the week (Mon–Sun) spans 2026-07-06 to 2026-07-12. */
    private val today: LocalDate = LocalDate.of(2026, 7, 8)

    /** Builds one standard 25-minute block starting at 9:00 on [date]. */
    private fun blockOn(date: LocalDate, id: Long = 1): FocusBlock = FocusBlock(
        id = id,
        startEpochMillis = date.atTime(LocalTime.of(9, 0)).toInstant(zone).toEpochMilli(),
        durationMinutes = GameConfig.FOCUS_BLOCK_MINUTES,
    )

    @Test
    fun `empty history produces all zeros with zero-filled breakdowns`() {
        val summary = StatsAggregator.aggregate(emptyList(), today, zone)

        assertEquals(0, summary.totalBlocks)
        assertEquals(0L, summary.allTimeMinutes)
        assertEquals(7, summary.lastSevenDays.size)
        assertEquals(0L, summary.lastSevenDays.sumOf { it.minutes })
        // July → January..July of this year, all zero.
        assertEquals(7, summary.monthsOfThisYear.size)
        assertEquals(emptyList<YearStat>(), summary.perYear)
    }

    @Test
    fun `blocks inside and outside the week are split correctly`() {
        val blocks = listOf(
            blockOn(today, id = 1),                 // this week
            blockOn(today.minusDays(2), id = 2),    // Monday → this week
            blockOn(today.minusDays(3), id = 3),    // Sunday → previous week
        )

        val summary = StatsAggregator.aggregate(blocks, today, zone)

        val blockMinutes = GameConfig.FOCUS_BLOCK_MINUTES.toLong()
        assertEquals(2 * blockMinutes, summary.thisWeekMinutes)
        assertEquals(3 * blockMinutes, summary.thisMonthMinutes)
        assertEquals(3 * blockMinutes, summary.allTimeMinutes)
    }

    @Test
    fun `last seven days are oldest-first and zero-filled`() {
        val summary = StatsAggregator.aggregate(listOf(blockOn(today)), today, zone)

        assertEquals(today.minusDays(6), summary.lastSevenDays.first().date)
        assertEquals(today, summary.lastSevenDays.last().date)
        assertEquals(
            GameConfig.FOCUS_BLOCK_MINUTES.toLong(),
            summary.lastSevenDays.last().minutes,
        )
        assertEquals(0L, summary.lastSevenDays.first().minutes)
    }

    @Test
    fun `years accumulate separately and sort ascending`() {
        val blocks = listOf(
            blockOn(LocalDate.of(2025, 3, 1), id = 1),
            blockOn(LocalDate.of(2026, 1, 15), id = 2),
            blockOn(LocalDate.of(2025, 8, 20), id = 3),
        )

        val summary = StatsAggregator.aggregate(blocks, today, zone)

        val blockMinutes = GameConfig.FOCUS_BLOCK_MINUTES.toLong()
        assertEquals(
            listOf(
                YearStat(2025, 2 * blockMinutes),
                YearStat(2026, 1 * blockMinutes),
            ),
            summary.perYear,
        )
        assertEquals(blockMinutes, summary.thisYearMinutes)
    }
}
