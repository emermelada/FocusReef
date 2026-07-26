package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.ui.theme.ReefSpacing

/**
 * The one definition of a "hero" pane — the highlighted `primaryContainer`
 * card that carries a screen's single most important fact (the token balance,
 * the period total, the player's level).
 *
 * Stats and Store had grown three near-identical hand-built copies of this,
 * each with slightly different padding; anything that changes about the hero
 * treatment now changes in one place.
 *
 * @param spacing Vertical gap between stacked children.
 */
@Composable
fun ReefHeroCard(
    modifier: Modifier = Modifier,
    spacing: Dp = ReefSpacing.md,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        border = reefCardBorder(),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ReefSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(spacing),
            content = content,
        )
    }
}

/**
 * A plain content pane — the recessive counterpart to [ReefHeroCard], for
 * charts, lists and store items that should sit *below* the hero in the
 * visual hierarchy rather than competing with it.
 */
@Composable
fun ReefContentCard(
    modifier: Modifier = Modifier,
    padding: Dp = ReefSpacing.lg,
    spacing: Dp = 0.dp,
    contentModifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        border = reefCardBorder(),
        modifier = modifier,
    ) {
        Column(
            modifier = contentModifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(spacing),
            content = content,
        )
    }
}
