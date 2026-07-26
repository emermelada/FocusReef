package com.emermeladas.focusreef.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.ui.theme.ReefMotion
import com.emermeladas.focusreef.utils.GameConfig
import kotlinx.coroutines.delay

/**
 * The app's quiet toast — FocusReef's replacement for the stock snackbar,
 * in the same family as [EarningsMoment]: a small surface with a bubble
 * mark and one short line, drifting up from the bottom, holding for
 * [GameConfig.TOAST_HOLD_MS], and slipping away.
 *
 * Give it the ViewModel's one-shot message res and its `onMessageShown`
 * callback; the toast owns the timing.
 */
@Composable
fun ReefToast(
    @StringRes messageRes: Int?,
    onShown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (messageRes != null) {
        LaunchedEffect(messageRes) {
            delay(GameConfig.TOAST_HOLD_MS.toLong())
            onShown()
        }
    }

    // Keep the last message around so the exit animation isn't blank.
    var shownRes by remember { mutableStateOf<Int?>(null) }
    if (messageRes != null && messageRes != shownRes) shownRes = messageRes

    AnimatedVisibility(
        visible = messageRes != null,
        enter = fadeIn(tween(ReefMotion.REVEAL_MS, easing = ReefMotion.RevealEasing)) +
            slideInVertically(
                animationSpec = tween(ReefMotion.REVEAL_MS, easing = ReefMotion.RevealEasing),
                initialOffsetY = { it / 2 },
            ),
        exit = fadeOut(tween(ReefMotion.REVEAL_MS)) +
            slideOutVertically(
                animationSpec = tween(ReefMotion.REVEAL_MS),
                targetOffsetY = { it / 2 },
            ),
        modifier = modifier,
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            shadowElevation = 4.dp,
            modifier = Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                // A toast appears without the player doing anything to focus
                // it and is gone in a couple of seconds — without a live
                // region a screen-reader user never learns their purchase
                // succeeded, or why it didn't. Polite, not assertive: it must
                // not cut off whatever is being read.
                .semantics { liveRegion = LiveRegionMode.Polite },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                // The bubble mark.
                Spacer(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.inversePrimary),
                )
                Spacer(modifier = Modifier.width(12.dp))
                shownRes?.let { res ->
                    Text(
                        text = stringResource(res),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
