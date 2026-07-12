package com.emermeladas.focusreef.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.ui.components.BreakdownRow
import com.emermeladas.focusreef.ui.components.ColumnChart
import com.emermeladas.focusreef.ui.components.LevelProgressRow
import com.emermeladas.focusreef.ui.components.ColumnChartEntry
import com.emermeladas.focusreef.utils.GameConfig
import com.emermeladas.focusreef.utils.formatMinutes
import com.emermeladas.focusreef.utils.formatMinutesShort
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Day label for the weekly chart, e.g. "Mon". */
private val dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

/** Narrow month label for the monthly chart, a single letter e.g. "J". */
private val monthFormatter = DateTimeFormatter.ofPattern("MMMMM", Locale.getDefault())

/**
 * Window 2 — study statistics, one period at a time: a Week / Year /
 * All-time segmented control, a hero total for the selected period, and a
 * column chart of its breakdown.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = uiState.summary

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabLabels = listOf(
        R.string.stats_tab_week,
        R.string.stats_tab_year,
        R.string.stats_tab_all_time,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = stringResource(R.string.stats_title),
            style = MaterialTheme.typography.titleLarge,
        )

        if (summary == null) {
            Text(stringResource(R.string.loading))
            return@Column
        }

        uiState.progression?.let { progression ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                LevelProgressRow(
                    progression = progression,
                    modifier = Modifier.padding(18.dp),
                )
            }
        }

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            tabLabels.forEachIndexed { index, labelRes ->
                SegmentedButton(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = tabLabels.size,
                    ),
                ) {
                    Text(stringResource(labelRes))
                }
            }
        }

        when (selectedTab) {
            0 -> PeriodSection(
                heroLabel = stringResource(R.string.stats_this_week),
                heroMinutes = summary.thisWeekMinutes,
                chartTitle = stringResource(R.string.stats_chart_last_7_days),
                chartEntries = summary.lastSevenDays.map { day ->
                    ColumnChartEntry(
                        label = day.date.format(dayFormatter),
                        value = day.minutes,
                        valueText = formatMinutesShort(day.minutes),
                    )
                },
            )

            1 -> PeriodSection(
                heroLabel = stringResource(R.string.stats_this_year),
                heroMinutes = summary.thisYearMinutes,
                chartTitle = stringResource(R.string.stats_chart_months),
                chartEntries = summary.monthsOfThisYear.map { month ->
                    ColumnChartEntry(
                        label = month.month.format(monthFormatter),
                        value = month.minutes,
                        valueText = formatMinutesShort(month.minutes),
                    )
                },
            )

            else -> AllTimeSection(summary)
        }
    }
}

/**
 * A hero total card followed by the period's column chart.
 */
@Composable
private fun PeriodSection(
    heroLabel: String,
    heroMinutes: Long,
    chartTitle: String,
    chartEntries: List<ColumnChartEntry>,
) {
    HeroCard(label = heroLabel, minutes = heroMinutes)

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = chartTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 14.dp),
            )
            ColumnChart(entries = chartEntries)
        }
    }
}

/**
 * All-time view: hero total (with block count) and a per-year list — a list
 * rather than a chart because there may be just one year of history.
 */
@Composable
private fun AllTimeSection(summary: StatsSummary) {
    HeroCard(
        label = stringResource(R.string.stats_all_time),
        minutes = summary.allTimeMinutes,
        secondary = stringResource(R.string.stats_focus_blocks, summary.totalBlocks),
    )

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.stats_chart_years),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            val yearMax = summary.perYear.maxOfOrNull { it.minutes }?.coerceAtLeast(1L) ?: 1L
            summary.perYear.forEach { year ->
                BreakdownRow(
                    label = year.year.toString(),
                    valueText = formatMinutes(year.minutes),
                    fraction = year.minutes.toFloat() / yearMax,
                )
            }
        }
    }
}

/**
 * Big highlighted number for the selected period, with the equivalent
 * focus-block count underneath.
 */
@Composable
private fun HeroCard(
    label: String,
    minutes: Long,
    secondary: String = stringResource(
        R.string.stats_focus_blocks,
        minutes / GameConfig.FOCUS_BLOCK_MINUTES,
    ),
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
            )
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = formatMinutes(minutes),
                    style = MaterialTheme.typography.displaySmall,
                )
            }
            Text(
                text = secondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
