package com.emermeladas.focusreef.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.ui.components.BreakdownRow
import com.emermeladas.focusreef.ui.components.ColumnChart
import com.emermeladas.focusreef.ui.components.ColumnChartEntry
import com.emermeladas.focusreef.ui.components.LevelProgressRow
import com.emermeladas.focusreef.ui.components.ReefContentCard
import com.emermeladas.focusreef.ui.components.ReefErrorState
import com.emermeladas.focusreef.ui.components.ReefHeroCard
import com.emermeladas.focusreef.ui.components.ReefScreenScaffold
import com.emermeladas.focusreef.ui.components.SettingsAction
import com.emermeladas.focusreef.ui.components.ShimmerSkeleton
import com.emermeladas.focusreef.ui.theme.ReefSpacing
import com.emermeladas.focusreef.utils.GameConfig
import com.emermeladas.focusreef.utils.formatMinutes
import com.emermeladas.focusreef.utils.formatMinutesShort
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A date formatter that follows the device language.
 *
 * These used to be top-level `val`s, which froze the locale at class-load time:
 * switch the phone to Spanish and the axis kept saying "Mon" until the process
 * was killed. Keying the `remember` on the configuration rebuilds them the
 * moment the language changes, and no more often than that.
 *
 * @param pattern a [DateTimeFormatter] pattern, e.g. `"EEE"`.
 */
@Composable
private fun rememberDateFormatter(pattern: String): DateTimeFormatter {
    val configuration = LocalConfiguration.current
    return remember(configuration) {
        DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    }
}

/** Day label for the weekly chart, e.g. "Mon". */
private const val DAY_PATTERN = "EEE"

/**
 * Month label for the yearly chart, e.g. "Jan".
 *
 * Deliberately not the single-letter `MMMMM` pattern: that renders the year as
 * `J F M A M J J A S O N D`, where four of the twelve labels are ambiguous and
 * the axis is unreadable without counting positions.
 */
private const val MONTH_PATTERN = "MMM"

/**
 * Window 2 — study statistics, one period at a time: a Week / Year /
 * All-time segmented control, a hero total for the selected period, and a
 * column chart of its breakdown.
 *
 * @param outerPadding insets from the app scaffold, spent as content padding.
 * @param onOpenSettings opens the settings destination from the top bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    outerPadding: PaddingValues,
    onOpenSettings: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = uiState.summary

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    // Week / Month / Year / All time. Month was specified from the start and
    // its string resource already existed unused — the tab was simply never
    // built, leaving a gap between "last 7 days" and "the whole year".
    val tabLabels = listOf(
        R.string.stats_tab_week,
        R.string.stats_tab_month,
        R.string.stats_tab_year,
        R.string.stats_tab_all_time,
    )

    ReefScreenScaffold(
        title = stringResource(R.string.stats_title),
        outerPadding = outerPadding,
        actions = { SettingsAction(onClick = onOpenSettings) },
    ) { contentPadding ->
        // The history lives on the NAS, so "is this up to date?" is a real
        // question the player can ask — pulling down is the expected way to
        // ask it, and it doubles as the recovery path after a failure.
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(ReefSpacing.md),
            ) {
                val errorRes = uiState.errorRes
                if (errorRes != null) {
                    ReefErrorState(messageRes = errorRes, onRetry = viewModel::refresh)
                    return@Column
                }

                if (summary == null) {
                    // Placeholder blocks sized like the content they become.
                    ShimmerSkeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp),
                    )
                    ShimmerSkeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp),
                    )
                    return@Column
                }

                uiState.progression?.let { progression ->
                    ReefHeroCard {
                        LevelProgressRow(progression = progression)
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

                val dayFormatter = rememberDateFormatter(DAY_PATTERN)
                val monthFormatter = rememberDateFormatter(MONTH_PATTERN)

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
                        heroLabel = stringResource(R.string.stats_this_month),
                        heroMinutes = summary.thisMonthMinutes,
                        chartTitle = stringResource(R.string.stats_chart_days_of_month),
                        chartEntries = summary.daysOfThisMonth.map { day ->
                            ColumnChartEntry(
                                label = day.date.dayOfMonth.toString(),
                                value = day.minutes,
                                valueText = formatMinutesShort(day.minutes),
                            )
                        },
                    )

                    2 -> PeriodSection(
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
    HeroTotalCard(label = heroLabel, minutes = heroMinutes)

    ReefContentCard {
        Text(
            text = chartTitle,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = ReefSpacing.md),
        )
        ColumnChart(entries = chartEntries)
    }
}

/**
 * All-time view: hero total (with block count) and a per-year list — a list
 * rather than a chart because there may be just one year of history.
 */
@Composable
private fun AllTimeSection(summary: StatsSummary) {
    HeroTotalCard(
        label = stringResource(R.string.stats_all_time),
        minutes = summary.allTimeMinutes,
        secondary = pluralStringResource(
            R.plurals.stats_focus_blocks,
            summary.totalBlocks,
            summary.totalBlocks,
        ),
    )

    ReefContentCard {
        Text(
            text = stringResource(R.string.stats_chart_years),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = ReefSpacing.sm),
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

/**
 * Big highlighted number for the selected period, with the equivalent
 * focus-block count underneath.
 */
@Composable
private fun HeroTotalCard(
    label: String,
    minutes: Long,
    secondary: String = (minutes / GameConfig.FOCUS_BLOCK_MINUTES).toInt().let { blocks ->
        pluralStringResource(R.plurals.stats_focus_blocks, blocks, blocks)
    },
) {
    ReefHeroCard(spacing = ReefSpacing.xs) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(ReefSpacing.sm),
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
