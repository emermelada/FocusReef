package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.ui.theme.fishColorFor

/**
 * PLACEHOLDER SPRITE — the single point where fish art is rendered.
 *
 * Each species draws its own vector silhouette (all facing LEFT), shaded
 * with a vertical light-from-above gradient so bodies read as round instead
 * of flat. When the real sprite images are ready, replace the Canvas below
 * with an Image/painter — every screen already goes through this composable,
 * so nothing else changes.
 *
 * @param species Decides the silhouette, size, and color.
 * @param modifier Position/flip modifiers from the caller (e.g. the tank).
 */
@Composable
fun FishSprite(
    species: FishSpecies,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(spriteSizeFor(species))) {
        val base = fishColorFor(species)
        when (species) {
            FishSpecies.SMALL -> drawClownfish(base)
            FishSpecies.NEON -> drawNeon(base)
            FishSpecies.BETTA -> drawBetta(base)
            FishSpecies.MEDIUM -> drawReefFish(base)
            FishSpecies.ANGELFISH -> drawAngelfish(base)
            FishSpecies.LIONFISH -> drawLionfish(base)
            FishSpecies.LARGE -> drawDeepSeaFish(base)
            FishSpecies.SHARK -> drawShark(base)
            FishSpecies.ORCA -> drawOrca(base)
        }
    }
}

/**
 * Placeholder canvas size per species — bigger fish take more slots and look
 * bigger in the tank. Keyed on slot size so new varieties need no sprite edits.
 */
fun spriteSizeFor(species: FishSpecies): DpSize = when (species.slots) {
    1 -> DpSize(30.dp, 18.dp)
    3 -> DpSize(46.dp, 28.dp)
    else -> DpSize(64.dp, 40.dp)
}

// ---- Shared shading helpers -------------------------------------------------

/** Sunlit top of a body color. */
private fun Color.lighten(fraction: Float): Color = lerp(this, Color.White, fraction)

/** Shaded belly of a body color. */
private fun Color.darken(fraction: Float): Color = lerp(this, Color.Black, fraction)

/** Light-from-above body shading between two vertical fractions of the canvas. */
private fun DrawScope.bodyBrush(base: Color, topFraction: Float, bottomFraction: Float): Brush =
    Brush.verticalGradient(
        0f to base.lighten(0.30f),
        0.5f to base,
        1f to base.darken(0.25f),
        startY = size.height * topFraction,
        endY = size.height * bottomFraction,
    )

/** Ringed eye with a pupil and a specular glint, so every fish looks alive. */
private fun DrawScope.drawEye(centerXFraction: Float, centerYFraction: Float, radiusFraction: Float = 0.11f) {
    val center = Offset(size.width * centerXFraction, size.height * centerYFraction)
    val radius = size.height * radiusFraction
    drawCircle(color = Color.White, radius = radius, center = center)
    drawCircle(color = Color(0xFF1B2430), radius = radius * 0.55f, center = center)
    drawCircle(
        color = Color.White,
        radius = radius * 0.2f,
        center = center + Offset(-radius * 0.25f, -radius * 0.3f),
    )
}

// ---- Small (1 slot) ---------------------------------------------------------

/** Clownfish: plump oval with two white bands and a rounded tail fan. */
private fun DrawScope.drawClownfish(base: Color) {
    val w = size.width
    val h = size.height
    // Tail fan.
    val tail = Path().apply {
        moveTo(w * 0.66f, h * 0.5f)
        quadraticTo(w * 1.02f, h * 0.02f, w * 0.97f, h * 0.5f)
        quadraticTo(w * 1.02f, h * 0.98f, w * 0.66f, h * 0.5f)
        close()
    }
    drawPath(tail, color = base.darken(0.12f))
    // Dorsal fin.
    val fin = Path().apply {
        moveTo(w * 0.28f, h * 0.22f)
        quadraticTo(w * 0.42f, h * -0.08f, w * 0.56f, h * 0.24f)
        close()
    }
    drawPath(fin, color = base.darken(0.10f))
    // Body.
    drawOval(
        brush = bodyBrush(base, 0.10f, 0.90f),
        topLeft = Offset(0f, h * 0.10f),
        size = Size(w * 0.72f, h * 0.80f),
    )
    // The two clown bands.
    for (x in listOf(0.235f, 0.46f)) {
        drawOval(
            color = Color.White.copy(alpha = 0.92f),
            topLeft = Offset(w * x, h * 0.13f),
            size = Size(w * 0.075f, h * 0.74f),
        )
    }
    drawEye(0.13f, 0.42f)
}

/** Neon tetra: a slim dart with a glowing lateral stripe and forked tail. */
private fun DrawScope.drawNeon(base: Color) {
    val w = size.width
    val h = size.height
    // Forked tail.
    val tail = Path().apply {
        moveTo(w * 0.70f, h * 0.5f)
        lineTo(w, h * 0.18f)
        lineTo(w * 0.86f, h * 0.5f)
        lineTo(w, h * 0.82f)
        close()
    }
    drawPath(tail, color = base.darken(0.15f))
    // Slim body.
    drawOval(
        brush = bodyBrush(base, 0.26f, 0.74f),
        topLeft = Offset(0f, h * 0.26f),
        size = Size(w * 0.76f, h * 0.48f),
    )
    // Dark back line.
    drawOval(
        color = base.darken(0.4f).copy(alpha = 0.55f),
        topLeft = Offset(w * 0.06f, h * 0.26f),
        size = Size(w * 0.62f, h * 0.14f),
    )
    // The glowing stripe neon tetras are named for.
    drawOval(
        color = base.lighten(0.55f),
        topLeft = Offset(w * 0.05f, h * 0.44f),
        size = Size(w * 0.64f, h * 0.12f),
    )
    drawEye(0.12f, 0.46f, radiusFraction = 0.10f)
}

/** Betta: tiny body, extravagant flowing translucent tail. */
private fun DrawScope.drawBetta(base: Color) {
    val w = size.width
    val h = size.height
    // Three overlapping tail veils, most transparent at the back.
    val veils = listOf(
        Triple(0.30f, 1.00f, 0.35f),
        Triple(0.36f, 0.92f, 0.55f),
        Triple(0.40f, 0.80f, 0.80f),
    )
    for ((startX, reach, alpha) in veils) {
        val veil = Path().apply {
            moveTo(w * startX, h * 0.5f)
            quadraticTo(w * reach, h * -0.15f, w * reach, h * 0.5f)
            quadraticTo(w * reach, h * 1.15f, w * startX, h * 0.5f)
            close()
        }
        drawPath(veil, color = base.copy(alpha = alpha))
    }
    // Flowing dorsal and ventral fins.
    val dorsal = Path().apply {
        moveTo(w * 0.16f, h * 0.28f)
        quadraticTo(w * 0.34f, h * -0.10f, w * 0.48f, h * 0.30f)
        close()
    }
    drawPath(dorsal, color = base.copy(alpha = 0.7f))
    val ventral = Path().apply {
        moveTo(w * 0.18f, h * 0.72f)
        quadraticTo(w * 0.34f, h * 1.10f, w * 0.46f, h * 0.70f)
        close()
    }
    drawPath(ventral, color = base.copy(alpha = 0.7f))
    // Compact body.
    drawOval(
        brush = bodyBrush(base, 0.24f, 0.76f),
        topLeft = Offset(0f, h * 0.24f),
        size = Size(w * 0.48f, h * 0.52f),
    )
    drawEye(0.10f, 0.44f, radiusFraction = 0.10f)
}

// ---- Medium (3 slots) -------------------------------------------------------

/** Reef fish: deep oval, crescent tail, visible pectoral fin. */
private fun DrawScope.drawReefFish(base: Color) {
    val w = size.width
    val h = size.height
    // Crescent tail.
    val tail = Path().apply {
        moveTo(w * 0.68f, h * 0.5f)
        lineTo(w, h * 0.06f)
        quadraticTo(w * 0.88f, h * 0.5f, w, h * 0.94f)
        close()
    }
    drawPath(tail, color = base.darken(0.15f))
    // Sweeping dorsal fin.
    val dorsal = Path().apply {
        moveTo(w * 0.22f, h * 0.20f)
        quadraticTo(w * 0.44f, h * -0.12f, w * 0.62f, h * 0.24f)
        close()
    }
    drawPath(dorsal, color = base.darken(0.12f))
    // Body.
    drawOval(
        brush = bodyBrush(base, 0.10f, 0.92f),
        topLeft = Offset(0f, h * 0.10f),
        size = Size(w * 0.74f, h * 0.82f),
    )
    // Head band behind the eye.
    drawOval(
        color = base.darken(0.30f).copy(alpha = 0.45f),
        topLeft = Offset(w * 0.26f, h * 0.13f),
        size = Size(w * 0.08f, h * 0.76f),
    )
    // Pectoral fin.
    val pectoral = Path().apply {
        moveTo(w * 0.36f, h * 0.52f)
        quadraticTo(w * 0.50f, h * 0.66f, w * 0.38f, h * 0.78f)
        close()
    }
    drawPath(pectoral, color = base.darken(0.28f))
    drawEye(0.13f, 0.40f)
}

/** Angelfish: tall disc body with dramatic dorsal and anal fins. */
private fun DrawScope.drawAngelfish(base: Color) {
    val w = size.width
    val h = size.height
    // Small tail.
    val tail = Path().apply {
        moveTo(w * 0.60f, h * 0.5f)
        lineTo(w * 0.86f, h * 0.22f)
        lineTo(w * 0.86f, h * 0.78f)
        close()
    }
    drawPath(tail, color = base.darken(0.15f))
    // Tall swept dorsal (top) and anal (bottom) fins.
    val dorsal = Path().apply {
        moveTo(w * 0.24f, h * 0.26f)
        quadraticTo(w * 0.52f, h * -0.16f, w * 0.66f, h * 0.10f)
        quadraticTo(w * 0.54f, h * 0.20f, w * 0.48f, h * 0.32f)
        close()
    }
    drawPath(dorsal, color = base.darken(0.10f))
    val anal = Path().apply {
        moveTo(w * 0.24f, h * 0.74f)
        quadraticTo(w * 0.52f, h * 1.16f, w * 0.66f, h * 0.90f)
        quadraticTo(w * 0.54f, h * 0.80f, w * 0.48f, h * 0.68f)
        close()
    }
    drawPath(anal, color = base.darken(0.10f))
    // Disc body — taller than long.
    drawOval(
        brush = bodyBrush(base, 0.06f, 0.94f),
        topLeft = Offset(w * 0.02f, h * 0.06f),
        size = Size(w * 0.60f, h * 0.88f),
    )
    // Two thin vertical stripes.
    for (x in listOf(0.26f, 0.42f)) {
        drawOval(
            color = base.darken(0.45f).copy(alpha = 0.5f),
            topLeft = Offset(w * x, h * 0.10f),
            size = Size(w * 0.05f, h * 0.80f),
        )
    }
    drawEye(0.13f, 0.38f)
}

/** Lionfish: banded body under a fan of venomous dorsal spines. */
private fun DrawScope.drawLionfish(base: Color) {
    val w = size.width
    val h = size.height
    val spineColor = base.darken(0.20f)
    // Fan of dorsal spines.
    for (i in 0..5) {
        val baseX = 0.16f + i * 0.09f
        val tipX = baseX - 0.05f + i * 0.015f
        val spine = Path().apply {
            moveTo(w * baseX, h * 0.30f)
            lineTo(w * tipX, h * -0.02f)
            lineTo(w * (baseX + 0.035f), h * 0.30f)
            close()
        }
        drawPath(spine, color = spineColor.copy(alpha = 0.85f))
    }
    // Tail.
    val tail = Path().apply {
        moveTo(w * 0.66f, h * 0.5f)
        quadraticTo(w * 1.02f, h * 0.12f, w * 0.96f, h * 0.5f)
        quadraticTo(w * 1.02f, h * 0.88f, w * 0.66f, h * 0.5f)
        close()
    }
    drawPath(tail, color = base.copy(alpha = 0.8f))
    // Pectoral spines swept forward-down.
    for (i in 0..2) {
        val pec = Path().apply {
            moveTo(w * 0.30f, h * 0.55f)
            lineTo(w * (0.14f + i * 0.09f), h * 1.02f)
            lineTo(w * (0.20f + i * 0.09f), h * 1.00f)
            close()
        }
        drawPath(pec, color = spineColor.copy(alpha = 0.7f))
    }
    // Body.
    drawOval(
        brush = bodyBrush(base, 0.24f, 0.86f),
        topLeft = Offset(0f, h * 0.24f),
        size = Size(w * 0.70f, h * 0.62f),
    )
    // The lion's stripes.
    for (x in listOf(0.20f, 0.36f, 0.52f)) {
        drawOval(
            color = Color.White.copy(alpha = 0.35f),
            topLeft = Offset(w * x, h * 0.26f),
            size = Size(w * 0.05f, h * 0.58f),
        )
    }
    drawEye(0.11f, 0.46f, radiusFraction = 0.09f)
}

// ---- Large (5 slots) --------------------------------------------------------

/** Deep-sea fish: long shaded body, tall curved dorsal, forked tail. */
private fun DrawScope.drawDeepSeaFish(base: Color) {
    val w = size.width
    val h = size.height
    // Forked tail.
    val tail = Path().apply {
        moveTo(w * 0.70f, h * 0.5f)
        lineTo(w, h * 0.08f)
        lineTo(w * 0.88f, h * 0.5f)
        lineTo(w, h * 0.92f)
        close()
    }
    drawPath(tail, color = base.darken(0.15f))
    // Tall curved dorsal.
    val dorsal = Path().apply {
        moveTo(w * 0.20f, h * 0.22f)
        quadraticTo(w * 0.38f, h * -0.14f, w * 0.60f, h * 0.20f)
        quadraticTo(w * 0.42f, h * 0.16f, w * 0.20f, h * 0.22f)
        close()
    }
    drawPath(dorsal, color = base.darken(0.12f))
    // Body.
    drawOval(
        brush = bodyBrush(base, 0.14f, 0.90f),
        topLeft = Offset(0f, h * 0.14f),
        size = Size(w * 0.76f, h * 0.76f),
    )
    // Luminous lateral line.
    val lateral = Path().apply {
        moveTo(w * 0.10f, h * 0.52f)
        quadraticTo(w * 0.40f, h * 0.44f, w * 0.68f, h * 0.54f)
    }
    drawPath(
        lateral,
        color = base.lighten(0.5f).copy(alpha = 0.6f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = h * 0.045f),
    )
    // Pelvic fin.
    val pelvic = Path().apply {
        moveTo(w * 0.34f, h * 0.80f)
        quadraticTo(w * 0.46f, h * 1.02f, w * 0.54f, h * 0.82f)
        close()
    }
    drawPath(pelvic, color = base.darken(0.20f))
    drawEye(0.12f, 0.42f)
}

/** Reef shark: torpedo silhouette, triangular dorsal, pale belly, gills. */
private fun DrawScope.drawShark(base: Color) {
    val w = size.width
    val h = size.height
    // Asymmetric tail — the upper lobe dominates.
    val tail = Path().apply {
        moveTo(w * 0.78f, h * 0.48f)
        lineTo(w * 0.99f, h * 0.06f)
        lineTo(w * 0.92f, h * 0.50f)
        lineTo(w * 0.99f, h * 0.78f)
        close()
    }
    drawPath(tail, color = base.darken(0.10f))
    // Dorsal fin.
    val dorsal = Path().apply {
        moveTo(w * 0.32f, h * 0.26f)
        lineTo(w * 0.44f, h * -0.02f)
        lineTo(w * 0.55f, h * 0.28f)
        close()
    }
    drawPath(dorsal, color = base.darken(0.08f))
    // Torpedo body.
    val body = Path().apply {
        moveTo(0f, h * 0.50f)
        quadraticTo(w * 0.18f, h * 0.16f, w * 0.46f, h * 0.24f)
        quadraticTo(w * 0.72f, h * 0.30f, w * 0.86f, h * 0.48f)
        quadraticTo(w * 0.70f, h * 0.72f, w * 0.42f, h * 0.76f)
        quadraticTo(w * 0.14f, h * 0.78f, 0f, h * 0.50f)
        close()
    }
    drawPath(body, brush = bodyBrush(base, 0.16f, 0.78f))
    // Pale belly.
    val belly = Path().apply {
        moveTo(w * 0.04f, h * 0.58f)
        quadraticTo(w * 0.40f, h * 0.70f, w * 0.80f, h * 0.52f)
        quadraticTo(w * 0.66f, h * 0.72f, w * 0.40f, h * 0.75f)
        quadraticTo(w * 0.16f, h * 0.76f, w * 0.04f, h * 0.58f)
        close()
    }
    drawPath(belly, color = Color.White.copy(alpha = 0.5f))
    // Pectoral fin.
    val pectoral = Path().apply {
        moveTo(w * 0.30f, h * 0.62f)
        lineTo(w * 0.40f, h * 0.95f)
        lineTo(w * 0.48f, h * 0.68f)
        close()
    }
    drawPath(pectoral, color = base.darken(0.12f))
    // Gill slits.
    for (i in 0..2) {
        drawLine(
            color = base.darken(0.35f),
            start = Offset(w * (0.235f + i * 0.028f), h * 0.38f),
            end = Offset(w * (0.225f + i * 0.028f), h * 0.56f),
            strokeWidth = h * 0.025f,
        )
    }
    drawEye(0.11f, 0.40f, radiusFraction = 0.07f)
}

/** Orca: stout black body, white eye patch and belly, tall dorsal. */
private fun DrawScope.drawOrca(base: Color) {
    val w = size.width
    val h = size.height
    // Tail flukes.
    val tail = Path().apply {
        moveTo(w * 0.80f, h * 0.46f)
        quadraticTo(w * 1.02f, h * 0.20f, w * 0.97f, h * 0.48f)
        quadraticTo(w * 1.02f, h * 0.76f, w * 0.80f, h * 0.52f)
        close()
    }
    drawPath(tail, color = base)
    // The tall dorsal fin orcas are known for.
    val dorsal = Path().apply {
        moveTo(w * 0.36f, h * 0.24f)
        quadraticTo(w * 0.40f, h * -0.18f, w * 0.52f, h * 0.10f)
        quadraticTo(w * 0.52f, h * 0.18f, w * 0.56f, h * 0.26f)
        close()
    }
    drawPath(dorsal, color = base)
    // Stout body.
    val body = Path().apply {
        moveTo(0f, h * 0.52f)
        quadraticTo(w * 0.14f, h * 0.18f, w * 0.46f, h * 0.22f)
        quadraticTo(w * 0.76f, h * 0.26f, w * 0.90f, h * 0.48f)
        quadraticTo(w * 0.72f, h * 0.76f, w * 0.40f, h * 0.80f)
        quadraticTo(w * 0.12f, h * 0.82f, 0f, h * 0.52f)
        close()
    }
    drawPath(body, brush = bodyBrush(base, 0.18f, 0.82f))
    // White belly sweep.
    val belly = Path().apply {
        moveTo(w * 0.03f, h * 0.60f)
        quadraticTo(w * 0.36f, h * 0.72f, w * 0.72f, h * 0.60f)
        quadraticTo(w * 0.58f, h * 0.78f, w * 0.36f, h * 0.80f)
        quadraticTo(w * 0.14f, h * 0.80f, w * 0.03f, h * 0.60f)
        close()
    }
    drawPath(belly, color = Color.White.copy(alpha = 0.92f))
    // The signature white eye patch.
    drawOval(
        color = Color.White.copy(alpha = 0.92f),
        topLeft = Offset(w * 0.14f, h * 0.30f),
        size = Size(w * 0.13f, h * 0.12f),
    )
    // Pectoral paddle.
    val pectoral = Path().apply {
        moveTo(w * 0.30f, h * 0.64f)
        quadraticTo(w * 0.36f, h * 1.02f, w * 0.48f, h * 0.70f)
        close()
    }
    drawPath(pectoral, color = base)
    drawEye(0.11f, 0.44f, radiusFraction = 0.07f)
}
