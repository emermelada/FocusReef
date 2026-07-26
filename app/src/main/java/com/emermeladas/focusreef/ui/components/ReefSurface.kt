package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.ui.theme.HairlineBottom
import com.emermeladas.focusreef.ui.theme.HairlineTop
import com.emermeladas.focusreef.ui.theme.ReefShadowColor

/**
 * The single definition of "an elevated pane" in FocusReef — what every
 * dialog and content card sits on. Three cues, applied together, are what
 * separate a crafted dark surface from a flat Material default:
 *
 *  1. a tinted (deep-water, not black) drop shadow, so the pane lifts off
 *     the background instead of dissolving into it;
 *  2. a top-lit hairline border (bright at the rim, fading down) that draws
 *     a crisp edge in the dark;
 *  3. a whisper of vertical highlight on the fill, so it reads as a physical
 *     pane catching light from above rather than a flat rectangle.
 *
 * Higher [elevation] means a lighter fill plus a stronger shadow.
 */
@Composable
fun ReefSurface(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    shape: Shape = MaterialTheme.shapes.medium,
    elevation: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .reefSurface(containerColor, shape, elevation),
        content = content,
    )
}

/**
 * The elevated-pane treatment as a modifier, for surfaces that can't be a
 * [ReefSurface] box (e.g. a Material `Card`'s own modifier). Same three cues.
 */
@Composable
fun Modifier.reefSurface(
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    shape: Shape = MaterialTheme.shapes.medium,
    elevation: Dp = 4.dp,
): Modifier {
    // Lift the fill toward white a touch so higher panes are visibly lighter.
    val lift = (elevation.value / 24f).coerceIn(0f, 0.10f)
    val top = lerp(containerColor, Color.White, lift + 0.05f)
    val base = lerp(containerColor, Color.White, lift)

    return this
        .shadow(
            elevation = elevation,
            shape = shape,
            ambientColor = ReefShadowColor,
            spotColor = ReefShadowColor,
        )
        .clip(shape)
        .background(
            brush = Brush.verticalGradient(
                0f to top,
                0.35f to base,
                1f to base,
            ),
        )
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(listOf(HairlineTop, HairlineBottom)),
            shape = shape,
        )
}

/**
 * The top-lit hairline as a [BorderStroke], for Material `Card`s that draw
 * their own fill/shadow but still need the reef's crisp edge in the dark.
 * Pair it with a `surfaceContainerHigh` container so the card reads as a
 * clearly lighter pane than the background.
 */
@Composable
fun reefCardBorder(): BorderStroke = BorderStroke(
    width = 1.dp,
    brush = Brush.verticalGradient(listOf(HairlineTop, HairlineBottom)),
)
