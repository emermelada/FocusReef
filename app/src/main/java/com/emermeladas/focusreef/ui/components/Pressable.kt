package com.emermeladas.focusreef.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.emermeladas.focusreef.ui.theme.ReefMotion

/**
 * Gives any clickable surface the app's shared press state: a subtle scale
 * down to [ReefMotion.PRESS_SCALE] while held, springing back on release.
 *
 * Pass the same [InteractionSource] the surface's `clickable`/`Card` uses so
 * both react to the identical press. Purely visual — hit targets and
 * semantics are untouched.
 */
fun Modifier.pressable(interactionSource: InteractionSource): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) ReefMotion.PRESS_SCALE else 1f,
        animationSpec = ReefMotion.gentleSpring(),
        label = "pressableScale",
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
