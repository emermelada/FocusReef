package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.data.model.DecorationSpecies
import com.emermeladas.focusreef.ui.theme.TokenGold
import com.emermeladas.focusreef.ui.theme.decorationColorFor
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * PLACEHOLDER SPRITE — the single point where decoration art is rendered.
 *
 * When real sprites arrive, only this file changes: replace the Canvas
 * shapes with images, keep the signature.
 */
@Composable
fun DecorationSprite(
    species: DecorationSpecies,
    modifier: Modifier = Modifier,
    clock: State<Float>? = null,
) {
    val color = decorationColorFor(species)
    // Per-instance phase so identical species never move in lockstep.
    val phase = remember { Random.nextFloat() * 2f * PI.toFloat() }
    Canvas(
        modifier = modifier
            .size(decorationSizeFor(species))
            .graphicsLayer {
                // Ambient motion, only when the tank provides its clock
                // (store cards and dialogs stay static): kelp sways from its
                // roots, the jellyfish drifts and gently pulses its bell.
                val t = clock?.value ?: return@graphicsLayer
                when (species) {
                    DecorationSpecies.KELP -> {
                        transformOrigin = TransformOrigin(0.5f, 1f)
                        rotationZ = sin(t * 0.8f + phase) * 4f
                    }
                    DecorationSpecies.JELLYFISH -> {
                        translationY = sin(t * 0.5f + phase) * 4.dp.toPx()
                        scaleY = 1f + 0.04f * sin(t * 1.6f + phase)
                    }
                    else -> Unit
                }
            },
    ) {
        // Floor decorations cast a soft contact shadow so they sit ON the
        // sand instead of floating in front of it.
        if (species.isFloorDecoration()) {
            drawOval(
                color = Color.Black.copy(alpha = 0.18f),
                topLeft = Offset(size.width * 0.02f, size.height * 0.90f),
                size = Size(size.width * 0.96f, size.height * 0.12f),
            )
        }
        when (species) {
            DecorationSpecies.SHELL -> drawShell(color)
            DecorationSpecies.ROCK -> drawRock(color)
            DecorationSpecies.KELP -> drawKelp(color)
            DecorationSpecies.BUBBLER -> drawBubbler(color)
            DecorationSpecies.TREASURE_CHEST -> drawChest(color)
            DecorationSpecies.JELLYFISH -> drawJellyfish(color)
        }
        // A live bubbler releases a rising stream on top of its static art.
        if (species == DecorationSpecies.BUBBLER) {
            clock?.let { drawBubblerStream(color, it.value) }
        }
    }
}

/** Decorations that rest on the sand (and therefore cast a contact shadow). */
private fun DecorationSpecies.isFloorDecoration(): Boolean = when (this) {
    DecorationSpecies.SHELL,
    DecorationSpecies.ROCK,
    DecorationSpecies.KELP,
    DecorationSpecies.TREASURE_CHEST,
    -> true

    DecorationSpecies.BUBBLER,
    DecorationSpecies.JELLYFISH,
    -> false
}

/** Small bubbles rising from the bubbler's mouth, looping forever. */
private fun DrawScope.drawBubblerStream(color: Color, time: Float) {
    repeat(3) { i ->
        val progress = ((time / (2.2f + i * 0.5f)) + i * 0.37f) % 1f
        val alpha = (1f - progress) * 0.8f
        drawCircle(
            color = color.copy(alpha = color.alpha * alpha),
            radius = size.width * (0.05f + 0.05f * progress),
            center = Offset(
                x = size.width * (0.5f + 0.18f * sin(progress * 9f + i)),
                y = size.height * (0.30f - 0.34f * progress),
            ),
        )
    }
}

/** Placeholder canvas size per decoration. */
fun decorationSizeFor(species: DecorationSpecies): DpSize = when (species) {
    DecorationSpecies.SHELL -> DpSize(22.dp, 16.dp)
    DecorationSpecies.ROCK -> DpSize(34.dp, 22.dp)
    DecorationSpecies.KELP -> DpSize(28.dp, 44.dp)
    DecorationSpecies.BUBBLER -> DpSize(20.dp, 52.dp)
    DecorationSpecies.TREASURE_CHEST -> DpSize(38.dp, 28.dp)
    DecorationSpecies.JELLYFISH -> DpSize(30.dp, 40.dp)
}

/** Fan of arcs opening upwards. */
private fun DrawScope.drawShell(color: Color) {
    val stroke = Stroke(width = size.height * 0.14f)
    for (fraction in listOf(1f, 0.66f, 0.33f)) {
        drawArc(
            color = color,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(size.width * (1f - fraction) / 2f, size.height * (1f - fraction)),
            size = Size(size.width * fraction, size.height * 2f * fraction),
            style = stroke,
        )
    }
}

/** Two overlapping ovals, the back one darker. */
private fun DrawScope.drawRock(color: Color) {
    drawOval(
        color = color.copy(alpha = 0.7f),
        topLeft = Offset(size.width * 0.3f, size.height * 0.15f),
        size = Size(size.width * 0.7f, size.height * 0.85f),
    )
    drawOval(
        color = color,
        topLeft = Offset(0f, size.height * 0.35f),
        size = Size(size.width * 0.75f, size.height * 0.65f),
    )
}

/** Three tapered fronds growing from the base. */
private fun DrawScope.drawKelp(color: Color) {
    val base = Offset(size.width / 2f, size.height)
    for ((index, lean) in listOf(-0.35f, 0f, 0.35f).withIndex()) {
        val height = size.height * (0.7f + 0.3f * (index % 2))
        val top = Offset(size.width / 2f + size.width * lean, size.height - height)
        drawLine(
            color = color,
            start = base,
            end = top,
            strokeWidth = size.width * 0.18f,
        )
        drawCircle(
            color = color,
            radius = size.width * 0.12f,
            center = top,
        )
    }
}

/** Rising column of bubbles, bigger towards the surface. */
private fun DrawScope.drawBubbler(color: Color) {
    val positions = listOf(
        Offset(0.5f, 0.95f) to 0.10f,
        Offset(0.35f, 0.72f) to 0.13f,
        Offset(0.62f, 0.50f) to 0.16f,
        Offset(0.40f, 0.28f) to 0.19f,
        Offset(0.58f, 0.08f) to 0.22f,
    )
    for ((position, radius) in positions) {
        drawCircle(
            color = color,
            radius = size.width * radius,
            center = Offset(size.width * position.x, size.height * position.y),
        )
    }
}

/** Brown box with a golden lid strip. */
private fun DrawScope.drawChest(color: Color) {
    drawRect(
        color = color,
        topLeft = Offset(0f, size.height * 0.35f),
        size = Size(size.width, size.height * 0.65f),
    )
    drawArc(
        color = TokenGold,
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(0f, 0f),
        size = Size(size.width, size.height * 0.7f),
    )
}

/** Translucent dome with trailing tentacles. */
private fun DrawScope.drawJellyfish(color: Color) {
    drawArc(
        color = color.copy(alpha = 0.85f),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = true,
        topLeft = Offset(0f, 0f),
        size = Size(size.width, size.height * 0.8f),
    )
    val tentacleTop = size.height * 0.4f
    for (fraction in listOf(0.2f, 0.4f, 0.6f, 0.8f)) {
        drawLine(
            color = color.copy(alpha = 0.6f),
            start = Offset(size.width * fraction, tentacleTop),
            end = Offset(size.width * fraction, size.height),
            strokeWidth = size.width * 0.06f,
        )
    }
}
