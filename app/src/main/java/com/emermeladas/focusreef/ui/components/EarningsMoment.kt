package com.emermeladas.focusreef.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.ui.theme.ReefMotion
import com.emermeladas.focusreef.utils.GameConfig
import kotlinx.coroutines.delay

/**
 * The welcome-back reward moment: a calm card that surfaces from the top of
 * the screen when focus tokens were earned since the last visit, holds for
 * [GameConfig.EARNINGS_MOMENT_MS], and slips away. Tap dismisses it early.
 *
 * Deliberately quiet — a coin, a sentence, no confetti. The point is that
 * finished focus work is *noticed*, not that the app throws a party.
 */
@Composable
fun EarningsMoment(
    newTokens: Long,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visible = newTokens > 0
    if (visible) {
        LaunchedEffect(newTokens) {
            delay(GameConfig.EARNINGS_MOMENT_MS.toLong())
            onDone()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(ReefMotion.REVEAL_MS, easing = ReefMotion.RevealEasing)) +
            slideInVertically(
                animationSpec = tween(ReefMotion.REVEAL_MS, easing = ReefMotion.RevealEasing),
                initialOffsetY = { -it / 2 },
            ),
        exit = fadeOut(tween(ReefMotion.REVEAL_MS)) +
            slideOutVertically(
                animationSpec = tween(ReefMotion.REVEAL_MS),
                targetOffsetY = { -it / 2 },
            ),
        modifier = modifier,
    ) {
        // Remember the count so the card doesn't blank out mid-exit when the
        // value resets to 0.
        val shownTokens = remember { newTokens }
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            shadowElevation = 6.dp,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClickLabel = stringResource(R.string.earnings_moment_dismiss),
                    onClick = onDone,
                )
                // The whole point of this card is to tell the player they
                // earned something the moment they open the app. Without a
                // live region it announces itself only if they happen to
                // swipe onto it before it is dismissed.
                .semantics { liveRegion = LiveRegionMode.Polite },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
            ) {
                TokenIcon(size = 34.dp)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = pluralStringResource(
                            R.plurals.earnings_moment_title,
                            shownTokens.toInt(),
                            shownTokens,
                        ),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = stringResource(R.string.earnings_moment_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}
