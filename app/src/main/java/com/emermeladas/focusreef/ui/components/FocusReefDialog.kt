package com.emermeladas.focusreef.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.emermeladas.focusreef.ui.theme.ReefMotion
import com.emermeladas.focusreef.ui.theme.ReefScrim
import com.emermeladas.focusreef.utils.rememberReducedMotion

/**
 * The app's shared dialog shell, replacing [androidx.compose.material3.AlertDialog]s.
 *
 * Every dialog in FocusReef surfaces the same way: a water-tinted scrim (the
 * platform's flat black dim is disabled), the card rising gently with a fade
 * and a whisper of scale, and the signature wave motif under the title.
 * Reduced motion renders it in place instantly. Tapping the scrim dismisses.
 *
 * @param title Dialog headline, drawn through [ReefHeader].
 * @param onDismiss Called for scrim taps and back presses.
 * @param buttons Action row content — use [DialogAction] for the app look.
 * @param content Body of the dialog.
 */
@Composable
fun FocusReefDialog(
    title: String,
    onDismiss: () -> Unit,
    buttons: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    val reducedMotion = rememberReducedMotion()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        // The reef draws its own scrim; kill the window's black dim.
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect { dialogWindow?.setDimAmount(0f) }

        // Rise-through-water entrance: fade + upward drift + slight scale.
        var entered by remember { mutableStateOf(reducedMotion) }
        LaunchedEffect(Unit) { entered = true }
        val enterProgress by animateFloatAsState(
            targetValue = if (entered) 1f else 0f,
            animationSpec = tween(ReefMotion.REVEAL_MS, easing = ReefMotion.RevealEasing),
            label = "dialogEnter",
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = enterProgress }
                .background(ReefScrim.copy(alpha = 0.52f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
        ) {
            Box(
                modifier = Modifier
                    .padding(28.dp)
                    .widthIn(max = 400.dp)
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = (1f - enterProgress) * 14.dp.toPx()
                        val scale = 0.96f + 0.04f * enterProgress
                        scaleX = scale
                        scaleY = scale
                    }
                    // The signature elevated pane: tinted shadow, top-lit
                    // hairline, subtle highlight — the dialog no longer reads
                    // as a flat gray Material sheet.
                    .reefSurface(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        shape = MaterialTheme.shapes.large,
                        elevation = 12.dp,
                    )
                    // Consume taps so they don't fall through to the scrim.
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ReefHeader(title = title)
                    content()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        content = buttons,
                    )
                }
            }
        }
    }
}

/**
 * One row inside a dialog list: a leading sprite framed on a soft
 * water-tinted chip, headline + optional supporting line, and an optional
 * trailing action. Rows with [onClick] get the shared press-scale (no
 * ripple); disabled rows dim but keep their supporting text readable — it
 * carries the *reason* they are disabled.
 */
@Composable
fun DialogListRow(
    headline: String,
    supporting: String?,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    leading: @Composable () -> Unit,
    trailing: (@Composable () -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .pressable(interactionSource)
                        .clip(MaterialTheme.shapes.medium)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = enabled,
                            onClick = onClick,
                        )
                } else {
                    Modifier
                },
            )
            .padding(vertical = 4.dp),
    ) {
        // The sprite sits on a calm chip of water.
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(
                        alpha = if (enabled) 0.45f else 0.20f,
                    ),
                )
                .graphicsLayer { alpha = if (enabled) 1f else 0.45f },
        ) {
            leading()
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = headline,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                },
            )
            if (supporting != null) {
                Text(
                    text = supporting,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        trailing?.invoke()
    }
}

/**
 * A dialog action in the app's shared style, delegating to the button
 * system: [primary] is the saturated accent CTA, otherwise a quiet text
 * action (Cancel/Close).
 */
@Composable
fun DialogAction(
    text: String,
    onClick: () -> Unit,
    primary: Boolean = false,
    enabled: Boolean = true,
) {
    if (primary) {
        ReefPrimaryButton(text = text, onClick = onClick, enabled = enabled)
    } else {
        ReefTextAction(text = text, onClick = onClick, enabled = enabled)
    }
}
