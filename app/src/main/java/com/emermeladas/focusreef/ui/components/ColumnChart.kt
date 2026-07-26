package com.emermeladas.focusreef.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.ui.theme.ReefMotion
import com.emermeladas.focusreef.utils.rememberReducedMotion

/** One bar of a [ColumnChart]. */
data class ColumnChartEntry(
    /** Short axis label under the bar, e.g. "Mon" or "12". */
    val label: String,
    /** Magnitude that decides the bar height. */
    val value: Long,
    /** Compact value shown above the bar, e.g. "2.5h". */
    val valueText: String,
)

/** Height of the slot above each bar that holds its value. */
private val ValueSlotHeight = 20.dp

/** Most axis labels that fit before they start colliding. */
private const val MAX_AXIS_LABELS = 8

/**
 * Minimal single-series column chart.
 *
 * One hue for one measure, bars with rounded tops anchored to a shared
 * baseline hairline; zero values keep a 3dp stub so rest days remain visible.
 *
 * The chart always states its own scale: a gridline at the top carries the
 * maximum, and the tallest bar shows its value without being asked. Tapping
 * any bar moves the readout to it. Previously *every* value was hidden behind
 * a tap, which meant a chart that told you nothing until you discovered an
 * unadvertised gesture.
 */
@Composable
fun ColumnChart(
    entries: List<ColumnChartEntry>,
    modifier: Modifier = Modifier,
    chartHeight: Dp = 150.dp,
) {
    // Selection is keyed on the label, not the index: when the data refreshes
    // (a new day rolls over, a pull-to-refresh lands) index 3 becomes a
    // different day, and the readout would silently move to another bar.
    var selectedLabel by remember(entries.map { it.label }) { mutableStateOf<String?>(null) }
    val maxValue = entries.maxOfOrNull { it.value }?.coerceAtLeast(1L) ?: 1L
    val peak = entries.maxByOrNull { it.value }

    // Bars grow from the baseline when the chart (or its data) appears,
    // left to right with a slight stagger. One shared progress drives all
    // bars; reduced motion renders them fully grown.
    val reducedMotion = rememberReducedMotion()
    val reveal = remember(entries) { Animatable(if (reducedMotion) 1f else 0f) }
    LaunchedEffect(entries) {
        if (reveal.value < 1f) {
            reveal.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    ReefMotion.REVEAL_MS + 250,
                    easing = ReefMotion.RevealEasing,
                ),
            )
        }
    }

    // With a month of data every label cannot fit, so thin them evenly rather
    // than letting them overlap into a grey smear.
    val labelStep = ((entries.size + MAX_AXIS_LABELS - 1) / MAX_AXIS_LABELS).coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Gridline at the height of the tallest bar, labelled with its
            // value — drawn first so bars sit on top of it.
            if (peak != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = ValueSlotHeight)
                        .clearAndSetSemantics {},
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = peak.valueText,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                entries.forEachIndexed { index, entry ->
                    val isSelected = selectedLabel == entry.label
                    // Show the peak's value when the player has not chosen one.
                    val showsValue = isSelected ||
                        (selectedLabel == null && entry.label == peak?.label)

                    // Later bars start growing slightly after earlier ones.
                    val stagger = if (entries.size > 1) index / (entries.size * 3f) else 0f
                    val growth = ((reveal.value - stagger) / (1f - stagger)).coerceIn(0f, 1f)
                    val barHeight = (chartHeight * (entry.value.toFloat() / maxValue) * growth)
                        .coerceAtLeast(3.dp)

                    val barDescription = "${entry.label}: ${entry.valueText}"

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier
                            .weight(1f)
                            // The whole column is the tap target, not just the
                            // bar: a 3dp stub for a rest day was a 3dp target.
                            .height(ValueSlotHeight + chartHeight)
                            // No ripple: the bar highlighting is the feedback.
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                            ) {
                                selectedLabel = if (isSelected) null else entry.label
                            }
                            // One announcement per bar; the label and value
                            // Texts inside would otherwise be read separately.
                            .semantics(mergeDescendants = true) {
                                contentDescription = barDescription
                            },
                    ) {
                        // Fixed-height slot so revealing a value never shifts bars.
                        Box(
                            modifier = Modifier.height(ValueSlotHeight),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            if (showsValue) {
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
                        // Soft vertical gradient — brighter at the top, like the
                        // water column; selection brings the bar to full tone.
                        val barBrush = if (isSelected) {
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                                ),
                            )
                        } else {
                            Brush.verticalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.80f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.50f),
                                ),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .fillMaxWidth()
                                .height(barHeight)
                                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp))
                                .background(barBrush),
                        )
                    }
                }
            }
        }

        // Recessive baseline separating bars from axis labels.
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(modifier = Modifier.height(6.dp))

        // Axis labels, mirroring the bars' weights so they stay aligned. The
        // bars already announce their own labels, so this row is decorative
        // to a screen reader.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clearAndSetSemantics {},
        ) {
            entries.forEachIndexed { index, entry ->
                Text(
                    text = if (index % labelStep == 0) entry.label else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
