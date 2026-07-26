package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.Progression

/**
 * Compact progression readout: level, streak with its multiplier, and a bar
 * showing progress towards the next level. Inherits the content color of
 * whatever container it sits in (hero cards, stats headers).
 */
@Composable
fun LevelProgressRow(
    progression: Progression,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.level_label, progression.level),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(
                    R.string.streak_label,
                    progression.currentStreakDays,
                    progression.currentMultiplierPercent / 100f,
                ),
                style = MaterialTheme.typography.labelLarge,
            )
        }
        // The same reef gauge the tank uses — one meter across the app, so
        // XP and tank capacity read as one language (no ticks for XP).
        CapacityGauge(
            fraction = if (progression.xpForNextLevel <= 0L) {
                0f
            } else {
                progression.xpIntoLevel.toFloat() / progression.xpForNextLevel
            },
            modifier = Modifier.fillMaxWidth(),
            ticks = 0,
            fillColor = LocalContentColor.current,
            trackColor = LocalContentColor.current.copy(alpha = 0.2f),
        )
        Text(
            text = stringResource(
                R.string.xp_progress,
                progression.xpIntoLevel,
                progression.xpForNextLevel,
            ),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
