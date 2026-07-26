package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.ui.theme.OnReefAccent
import com.emermeladas.focusreef.ui.theme.ReefAccent

/**
 * FocusReef's button system — four roles, used everywhere with no exceptions,
 * so actions read consistently instead of "tonal pill here, bare blue text
 * there." All share a defined 12dp radius (never full pills, which read as
 * default) and the app's press-scale.
 *
 *  - [ReefPrimaryButton]   the one saturated accent action per surface
 *                          (confirm a placement, retry a failed load).
 *                          **At most one per visible screen.**
 *  - [ReefBuyButton]       the *repeated* store CTA. Deliberately tonal, not
 *                          accent: a grid shows sixteen of these at once, and
 *                          sixteen saturated buttons is a wall of noise with
 *                          no hierarchy left. Price sits next to the coin so
 *                          the button reads as a cost, not a color.
 *  - [ReefSecondaryButton] a calm tonal action (Move, secondary choices).
 *  - [ReefTextAction]      a quiet text action for truly minor things
 *                          (Cancel/Close).
 */

/** Shared button radius — deliberately not a stadium/pill. */
private val ButtonShape: Shape = RoundedCornerShape(12.dp)

private val ButtonPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)

/** The primary call to action: filled with the saturated reef accent. */
@Composable
fun ReefPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = ButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = ReefAccent,
            contentColor = OnReefAccent,
        ),
        contentPadding = ButtonPadding,
        interactionSource = interactionSource,
        modifier = modifier.pressable(interactionSource),
    ) {
        Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/**
 * The store's repeated buy action.
 *
 * Tonal rather than accent on purpose — see the note at the top of this file.
 * When [price] is given the coin and the amount render inside the button, so
 * the label is an action ("Buy") and the price is data, instead of the button
 * being labelled with a bare number.
 *
 * @param label Action text, e.g. "Buy" — or the lock reason when disabled.
 * @param price Token cost; omit to render label only (e.g. a locked item).
 */
@Composable
fun ReefBuyButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    price: Long? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        shape = ButtonShape,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        interactionSource = interactionSource,
        modifier = modifier.pressable(interactionSource),
    ) {
        Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
        if (price != null) {
            Spacer(Modifier.width(6.dp))
            // Dim the coin along with the label when the action is unavailable.
            TokenIcon(
                size = 16.dp,
                modifier = Modifier.alpha(if (enabled) 1f else 0.38f),
            )
            Spacer(Modifier.width(3.dp))
            Text(
                text = price.toString(),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
            )
        }
    }
}

/** A calm secondary action: tonal, never competes with the primary. */
@Composable
fun ReefSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        shape = ButtonShape,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        interactionSource = interactionSource,
        modifier = modifier.pressable(interactionSource),
    ) {
        Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/** A quiet text action, reserved for minor choices (Cancel/Close). */
@Composable
fun ReefTextAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shape = ButtonShape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        interactionSource = interactionSource,
        modifier = modifier.pressable(interactionSource),
    ) {
        Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

