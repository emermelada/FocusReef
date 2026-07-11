package com.emermeladas.focusreef.ui.screens.stats

import com.emermeladas.focusreef.data.model.FocusBlock
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

/** Minutes studied on one calendar day. */
data class DayStat(val date: LocalDate, val minutes: Long)

/** Minutes studied in one calendar month. */
data class MonthStat(val month: YearMonth, val minutes: Long)

/** Minutes studied in one calendar year. */
data class YearStat(val year: Int, val minutes: Long)

/**
 * Everything the stats screen shows, precomputed.
 */
data class StatsSummary(
    val totalBlocks: Int,
    val thisWeekMinutes: Long,
    val thisMonthMinutes: Long,
    val thisYearMinutes: Long,
    val allTimeMinutes: Long,
    /** The last 7 days including today, oldest first, zero-filled. */
    val lastSevenDays: List<DayStat>,
    /** January up to the current month of this year, zero-filled. */
    val monthsOfThisYear: List<MonthStat>,
    /** Every year with recorded activity, oldest first. */
    val perYear: List<YearStat>,
)

/**
 * Pure aggregation of focus blocks into the stats shown in the UI.
 *
 * Kept free of Android dependencies (and with `today`/`zone` as parameters)
 * so it is trivially unit-testable.
 */
object StatsAggregator {

    /**
     * @param blocks Raw focus-block history.
     * @param today The current date — passed in, never read from the clock here.
     * @param zone Timezone used to bucket blocks into calendar days.
     */
    fun aggregate(blocks: List<FocusBlock>, today: LocalDate, zone: ZoneId): StatsSummary {
        // Bucket minutes per calendar day once; every stat derives from this.
        val minutesPerDay = mutableMapOf<LocalDate, Long>()
        for (block in blocks) {
            val day = Instant.ofEpochMilli(block.startEpochMillis).atZone(zone).toLocalDate()
            minutesPerDay.merge(day, block.durationMinutes.toLong(), Long::plus)
        }

        val weekStart = today.with(DayOfWeek.MONDAY)
        val thisMonth = YearMonth.from(today)

        val lastSevenDays = (6 downTo 0).map { daysAgo ->
            val day = today.minusDays(daysAgo.toLong())
            DayStat(date = day, minutes = minutesPerDay[day] ?: 0L)
        }

        val monthsOfThisYear = (1..today.monthValue).map { monthNumber ->
            val month = YearMonth.of(today.year, monthNumber)
            MonthStat(
                month = month,
                minutes = minutesPerDay
                    .filterKeys { YearMonth.from(it) == month }
                    .values.sum(),
            )
        }

        val perYear = minutesPerDay.entries
            .groupBy({ it.key.year }, { it.value })
            .map { (year, minutes) -> YearStat(year = year, minutes = minutes.sum()) }
            .sortedBy { it.year }

        return StatsSummary(
            totalBlocks = blocks.size,
            thisWeekMinutes = minutesPerDay
                .filterKeys { !it.isBefore(weekStart) && !it.isAfter(today) }
                .values.sum(),
            thisMonthMinutes = minutesPerDay
                .filterKeys { YearMonth.from(it) == thisMonth }
                .values.sum(),
            thisYearMinutes = minutesPerDay
                .filterKeys { it.year == today.year }
                .values.sum(),
            allTimeMinutes = minutesPerDay.values.sum(),
            lastSevenDays = lastSevenDays,
            monthsOfThisYear = monthsOfThisYear,
            perYear = perYear,
        )
    }
}
