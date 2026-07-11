package com.emermeladas.focusreef.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.ui.components.BreakdownRow
import com.emermeladas.focusreef.ui.components.StatTile
import com.emermeladas.focusreef.utils.formatMinutes
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Day label for the weekly breakdown, e.g. "Mon 6". */
private val dayFormatter = DateTimeFormatter.ofPattern("EEE d", Locale.getDefault())

/** Month label for the yearly breakdown, e.g. "Jan". */
private val monthFormatter = DateTimeFormatter.ofPattern("MMM", Locale.getDefault())

/**
 * Window 2 — study statistics: headline totals plus weekly, monthly and
 * yearly breakdowns of focus time.
 */
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = uiState.summary

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.stats_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }

        if (summary == null) {
            item { Text(stringResource(R.string.loading)) }
            return@LazyColumn
        }

        // Headline tiles: two rows of two.
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(
                    label = stringResource(R.string.stats_this_week),
                    value = formatMinutes(summary.thisWeekMinutes),
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = stringResource(R.string.stats_this_month),
                    value = formatMinutes(summary.thisMonthMinutes),
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile(
                    label = stringResource(R.string.stats_this_year),
                    value = formatMinutes(summary.thisYearMinutes),
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = stringResource(R.string.stats_all_time),
                    value = formatMinutes(summary.allTimeMinutes),
                    secondary = stringResource(R.string.stats_focus_blocks, summary.totalBlocks),
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Last 7 days.
        item { SectionHeader(stringResource(R.string.stats_weekly_breakdown)) }
        val weekMax = summary.lastSevenDays.maxOf { it.minutes }.coerceAtLeast(1L)
        items(summary.lastSevenDays, key = { it.date.toEpochDay() }) { day ->
            BreakdownRow(
                label = day.date.format(dayFormatter),
                valueText = formatMinutes(day.minutes),
                fraction = day.minutes.toFloat() / weekMax,
            )
        }

        // Months of the current year.
        item {
            SectionHeader(
                stringResource(
                    R.string.stats_monthly_breakdown,
                    summary.monthsOfThisYear.firstOrNull()?.month?.year ?: 0,
                ),
            )
        }
        val monthMax = summary.monthsOfThisYear.maxOfOrNull { it.minutes }?.coerceAtLeast(1L) ?: 1L
        items(summary.monthsOfThisYear, key = { it.month.toString() }) { month ->
            BreakdownRow(
                label = month.month.format(monthFormatter),
                valueText = formatMinutes(month.minutes),
                fraction = month.minutes.toFloat() / monthMax,
            )
        }

        // Per year, all time.
        item { SectionHeader(stringResource(R.string.stats_yearly_breakdown)) }
        val yearMax = summary.perYear.maxOfOrNull { it.minutes }?.coerceAtLeast(1L) ?: 1L
        items(summary.perYear, key = { it.year }) { year ->
            BreakdownRow(
                label = year.year.toString(),
                valueText = formatMinutes(year.minutes),
                fraction = year.minutes.toFloat() / yearMax,
            )
        }
    }
}

/**
 * Small section title separating the breakdown lists.
 */
@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 4.dp),
    )
}
