package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** One bar of a [ColumnChart]. */
data class ColumnChartEntry(
    /** Short axis label under the bar, e.g. "Mon" or "J". */
    val label: String,
    /** Magnitude that decides the bar height. */
    val value: Long,
    /** Compact value shown when the bar is tapped, e.g. "2.5h". */
    val valueText: String,
)

/**
 * Minimal single-series column chart.
 *
 * One hue for one measure; values are revealed by tapping a bar (the touch
 * equivalent of a hover tooltip) instead of labeling every bar. Bars have
 * rounded tops anchored to a shared baseline; zero values keep a 3dp stub so
 * rest days remain visible.
 */
@Composable
fun ColumnChart(
    entries: List<ColumnChartEntry>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 150.dp,
) {
    var selectedIndex by remember(entries) { mutableStateOf<Int?>(null) }
    val maxValue = entries.maxOfOrNull { it.value }?.coerceAtLeast(1L) ?: 1L

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
    ) {
        entries.forEachIndexed { index, entry ->
            val isSelected = selectedIndex == index
            val barHeight = (chartHeight * (entry.value.toFloat() / maxValue))
                .coerceAtLeast(3.dp)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    // No ripple: the bar itself highlighting is the feedback.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        selectedIndex = if (isSelected) null else index
                    },
            ) {
                // Fixed-height slot so revealing a value never shifts the bars.
                Box(
                    modifier = Modifier.height(20.dp),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    if (isSelected) {
                        Text(
                            text = entry.valueText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Visible,
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .fillMaxWidth()
                        .height(barHeight)
                        .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.72f)
                            },
                        ),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = entry.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                )
            }
        }
    }
}
