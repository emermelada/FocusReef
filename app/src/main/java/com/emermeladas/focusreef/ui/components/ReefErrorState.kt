package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.ui.theme.ReefSpacing

/**
 * What a screen shows when its data could not be loaded.
 *
 * Before this existed, an unreachable NAS left the shimmer skeleton pulsing
 * forever — the app looked like it was still working when it had in fact
 * given up. A visible failure with a way out is strictly better than an
 * animation that lies.
 *
 * @param messageRes What went wrong, in the player's terms.
 * @param onRetry Re-runs the failed load.
 */
@Composable
fun ReefErrorState(
    messageRes: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = ReefSpacing.xxl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ReefSpacing.md),
    ) {
        UnpluggedMark()
        Text(
            text = stringResource(R.string.error_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(messageRes),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        ReefPrimaryButton(
            text = stringResource(R.string.action_retry),
            onClick = onRetry,
        )
    }
}

/**
 * A severed connection drawn in the app's own hand: two wave crests with the
 * middle stroke missing. Purely decorative — the text beside it carries the
 * meaning, so it is hidden from screen readers.
 */
@Composable
private fun UnpluggedMark() {
    val markColor = MaterialTheme.colorScheme.outline
    Canvas(
        modifier = Modifier
            .size(64.dp, 28.dp)
            .clearAndSetSemantics {},
    ) {
        val midY = size.height / 2f
        val stroke = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)

        // Left crest, then a deliberate gap, then the right crest.
        val left = Path().apply {
            moveTo(0f, midY)
            quadraticTo(size.width * 0.14f, -midY * 0.5f, size.width * 0.32f, midY)
        }
        val right = Path().apply {
            moveTo(size.width * 0.68f, midY)
            quadraticTo(size.width * 0.86f, size.height + midY * 0.5f, size.width, midY)
        }
        drawPath(path = left, color = markColor, style = stroke)
        drawPath(path = right, color = markColor, style = stroke)
    }
}
