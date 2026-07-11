package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.ui.theme.fishColorFor

/**
 * PLACEHOLDER SPRITE — the single point where fish art is rendered.
 *
 * Draws a simple vector fish (body + tail + eye) colored and sized per
 * species. When the real sprite images are ready, replace the Canvas below
 * with an Image/painter — every screen already goes through this composable,
 * so nothing else changes.
 *
 * @param species Decides the placeholder's size and color.
 * @param modifier Position/flip modifiers from the caller (e.g. the tank).
 */
@Composable
fun FishSprite(
    species: FishSpecies,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(spriteSizeFor(species))) {
        val bodyColor = fishColorFor(species)
        // Body: an ellipse over the left ~72% of the canvas.
        val bodyWidth = size.width * 0.72f
        drawOval(
            color = bodyColor,
            topLeft = Offset(0f, size.height * 0.12f),
            size = Size(bodyWidth, size.height * 0.76f),
        )
        // Tail: a triangle attached to the right of the body.
        val tail = Path().apply {
            moveTo(bodyWidth * 0.94f, size.height * 0.5f)
            lineTo(size.width, size.height * 0.08f)
            lineTo(size.width, size.height * 0.92f)
            close()
        }
        drawPath(path = tail, color = bodyColor)
        // Eye: near the nose, with a white ring so it reads on any body color.
        val eyeCenter = Offset(bodyWidth * 0.25f, size.height * 0.42f)
        drawCircle(color = Color.White, radius = size.height * 0.11f, center = eyeCenter)
        drawCircle(color = Color(0xFF1B2430), radius = size.height * 0.055f, center = eyeCenter)
    }
}

/**
 * Placeholder canvas size per species — bigger fish take more slots and look
 * bigger in the tank.
 */
fun spriteSizeFor(species: FishSpecies): DpSize = when (species) {
    FishSpecies.SMALL -> DpSize(30.dp, 18.dp)
    FishSpecies.MEDIUM -> DpSize(46.dp, 28.dp)
    FishSpecies.LARGE -> DpSize(64.dp, 40.dp)
}
