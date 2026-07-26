package com.emermeladas.focusreef.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.data.model.Fish
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.ui.theme.ReefMotion
import com.emermeladas.focusreef.ui.theme.TankPalette
import com.emermeladas.focusreef.ui.theme.tankPalette
import com.emermeladas.focusreef.utils.GameConfig
import com.emermeladas.focusreef.utils.rememberReducedMotion
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Horizontal swim range in alignment-bias units (−1 = left edge, 1 = right). */
private const val BIAS_RANGE_X = 0.85f

/** Vertical swim range, slightly smaller so fish stay off the sand strip. */
private const val BIAS_RANGE_Y = 0.72f

/** Corner radius of the tank glass. */
private val TANK_CORNER = 20.dp

/** Height of the sand floor, waves included. */
private val SAND_HEIGHT = 16.dp

/** Body sway while swimming, in degrees. */
private const val SWAY_SWIM_DEGREES = 4f

/** Residual sway while hovering, in degrees. */
private const val SWAY_HOVER_DEGREES = 1.5f

/** Vertical idle bob while hovering, in dp. */
private val BOB_AMPLITUDE = 2.5.dp

/** Number of drifting caustic light blobs. */
private const val CAUSTIC_COUNT = 4

/**
 * PLACEHOLDER SPRITE — the single point where tank art is rendered.
 *
 * A small living scene rather than a colored box: layered water gradient with
 * a light shaft and slow caustics, ambient bubbles, a wavy grained sand
 * floor, and a glass sheen in front. All ambient layers are driven by one
 * [rememberReefClock] frame clock, freeze under reduced motion / battery
 * saver, and stop when the tank leaves the screen. Swap the drawing for real
 * art here when sprites are ready; screens never draw tanks themselves.
 */
@Composable
fun TankSprite(
    tank: Tank,
    modifier: Modifier = Modifier,
    showContents: Boolean = true,
) {
    val palette = tankPalette()
    val reducedMotion = rememberReducedMotion()
    val clock = rememberReefClock(running = !reducedMotion)
    // Stable per-tank randomness for sand grain and bubble phases.
    val seed = remember(tank.id) { tank.id.toInt() }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(TANK_CORNER))
            .drawBehind { drawWaterScene(palette, clock.value, seed) }
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.12f),
                    ),
                ),
                shape = RoundedCornerShape(TANK_CORNER),
            ),
    ) {
        // Thumbnails (e.g. tank-picker rows) skip the contents: fixed-dp
        // sprites would overflow a tiny tank.
        if (showContents) {
            // Fish present when the tank first composed are "residents";
            // anyone appearing later (a purchase, a move) gets a settle-in
            // entrance from the surface.
            val residents = remember(tank.id) {
                tank.fish.mapTo(mutableSetOf()) { it.id }
            }
            // Decorations first so fish swim in front of them.
            tank.decorations.forEach { decoration ->
                DecorationSprite(
                    species = decoration.species,
                    modifier = Modifier.align(
                        BiasAlignment(decoration.xBias, decoration.yBias),
                    ),
                    clock = if (reducedMotion) null else clock,
                )
            }
            tank.fish.forEach { fish ->
                WanderingFish(
                    fish = fish,
                    clock = clock,
                    reducedMotion = reducedMotion,
                    entering = fish.id !in residents,
                )
                SideEffect { residents.add(fish.id) }
            }
        }

        // Front glass: bubbles rise in front of the fish; the sheen sits on
        // the glass itself, so it is drawn last.
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (!reducedMotion) drawAmbientBubbles(clock.value, seed)
            drawGlassSheen()
        }
    }
}

// ---- Water drawing ----------------------------------------------------------

/**
 * Everything behind the fish: water gradient, light shaft, caustics, floor
 * shading, and the sand bed. [time] is seconds from the tank clock — 0 under
 * reduced motion, which renders a calm static frame.
 */
private fun DrawScope.drawWaterScene(palette: TankPalette, time: Float, seed: Int) {
    // Water column: surface light falling away to the deep.
    drawRect(
        brush = Brush.verticalGradient(
            0f to palette.surface,
            0.55f to palette.mid,
            1f to palette.deep,
        ),
    )

    // Light shaft: a soft cone widening from the top-center third.
    val shaft = Path().apply {
        moveTo(size.width * 0.30f, 0f)
        lineTo(size.width * 0.62f, 0f)
        lineTo(size.width * 0.86f, size.height * 0.78f)
        lineTo(size.width * 0.06f, size.height * 0.78f)
        close()
    }
    drawPath(
        path = shaft,
        brush = Brush.verticalGradient(
            0f to Color.White.copy(alpha = 0.14f),
            0.85f to Color.Transparent,
            endY = size.height * 0.78f,
        ),
    )

    // Caustics: large, faint light blobs drifting on slow sine paths.
    repeat(CAUSTIC_COUNT) { i ->
        val phase = i * 1.7f
        val cx = size.width * (0.5f + 0.38f * sin(time * 0.11f + phase))
        val cy = size.height * (0.16f + 0.15f * i + 0.05f * sin(time * 0.07f + phase * 2f))
        val radius = size.width * (0.16f + 0.03f * sin(time * 0.09f + phase))
        drawCircle(
            brush = Brush.radialGradient(
                0f to Color.White.copy(alpha = 0.07f),
                1f to Color.Transparent,
                center = Offset(cx, cy),
                radius = radius,
            ),
            radius = radius,
            center = Offset(cx, cy),
        )
    }

    // The floor falls into shadow.
    drawRect(
        brush = Brush.verticalGradient(
            0f to Color.Transparent,
            1f to Color.Black.copy(alpha = 0.22f),
            startY = size.height * 0.55f,
        ),
        topLeft = Offset(0f, size.height * 0.55f),
        size = Size(size.width, size.height * 0.45f),
    )

    drawSandBed(palette, seed)

    // Water surface: a soft shimmer just under the rim, brightest mid-tank.
    drawRect(
        brush = Brush.horizontalGradient(
            0f to Color.Transparent,
            0.5f to Color.White.copy(alpha = 0.30f),
            1f to Color.Transparent,
        ),
        topLeft = Offset(size.width * 0.08f, 7.dp.toPx()),
        size = Size(size.width * 0.84f, 2.dp.toPx()),
    )
}

/** Sand with a gently waved top edge and per-tank grain speckles. */
private fun DrawScope.drawSandBed(palette: TankPalette, seed: Int) {
    val sandHeight = SAND_HEIGHT.toPx()
    val topY = size.height - sandHeight
    val waveAmp = 2.5.dp.toPx()

    val bed = Path().apply {
        moveTo(0f, topY + waveAmp)
        // Two soft dunes across the width.
        quadraticTo(size.width * 0.25f, topY - waveAmp, size.width * 0.5f, topY + waveAmp * 0.4f)
        quadraticTo(size.width * 0.75f, topY + waveAmp * 1.6f, size.width, topY - waveAmp * 0.5f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
    drawPath(path = bed, color = palette.sand)
    // A hint of sunlight caught on the dune crests.
    drawPath(
        path = bed,
        brush = Brush.verticalGradient(
            0f to Color.White.copy(alpha = 0.25f),
            1f to Color.Transparent,
            startY = topY - waveAmp,
            endY = topY + sandHeight * 0.5f,
        ),
    )

    // Grain: stable speckles seeded per tank, light and dark two-tone.
    val random = Random(seed)
    repeat(42) {
        val x = random.nextFloat() * size.width
        val y = topY + waveAmp + random.nextFloat() * (sandHeight - waveAmp * 1.5f)
        val light = random.nextBoolean()
        drawCircle(
            color = if (light) Color.White.copy(alpha = 0.28f) else Color.Black.copy(alpha = 0.13f),
            radius = (0.6f + random.nextFloat()) * 1.1.dp.toPx() * 0.7f,
            center = Offset(x, y),
        )
    }
}

// ---- Front glass ------------------------------------------------------------

/** Ambient bubbles rising with a sine wobble, phased per tank. */
private fun DrawScope.drawAmbientBubbles(time: Float, seed: Int) {
    val random = Random(seed + 1)
    repeat(GameConfig.TANK_AMBIENT_BUBBLE_COUNT) { i ->
        // Per-bubble fixed traits.
        val riseSeconds = (
            GameConfig.BUBBLE_RISE_MIN_MS +
                random.nextInt(GameConfig.BUBBLE_RISE_MAX_MS - GameConfig.BUBBLE_RISE_MIN_MS)
            ) / 1000f
        val phase = random.nextFloat()
        val baseX = size.width * (0.12f + 0.76f * random.nextFloat())
        val radius = (1.2f + random.nextFloat() * 1.6f).dp.toPx()

        // Progress 0 (floor) → 1 (surface), looping.
        val progress = ((time / riseSeconds) + phase) % 1f
        val y = size.height * (0.95f - 0.88f * progress)
        val wobble = 3.dp.toPx() * sin(time * (1.1f + 0.3f * i) + phase * 2f * PI.toFloat())
        // Fade in near the floor, fade out approaching the surface.
        val alpha = (progress * 8f).coerceAtMost(1f) * (1f - progress).coerceAtMost(0.35f) / 0.35f

        val center = Offset(baseX + wobble, y)
        drawCircle(
            color = Color.White.copy(alpha = 0.35f * alpha),
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx()),
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.18f * alpha),
            radius = radius * 0.45f,
            center = center + Offset(-radius * 0.3f, -radius * 0.3f),
        )
    }
}

/** A faint diagonal reflection band across the upper glass. */
private fun DrawScope.drawGlassSheen() {
    val sheen = Path().apply {
        moveTo(size.width * 0.58f, 0f)
        lineTo(size.width * 0.78f, 0f)
        lineTo(size.width * 0.30f, size.height * 0.62f)
        lineTo(size.width * 0.16f, size.height * 0.62f)
        close()
    }
    drawPath(
        path = sheen,
        brush = Brush.verticalGradient(
            0f to Color.White.copy(alpha = 0.10f),
            1f to Color.Transparent,
            endY = size.height * 0.62f,
        ),
    )
}

// ---- Fish -------------------------------------------------------------------

/**
 * One fish that swims to a random point in the tank, pauses briefly, then
 * picks a new destination — forever. Bigger species take longer per leg
 * (see [GameConfig.FISH_SWIM_PER_SLOT_MS]), so they read as heavier and more
 * deliberate. The body sways gently while swimming, bobs while hovering, and
 * turns by animating through the flip instead of hard-mirroring.
 *
 * Randomness is seeded per fish id only for the *starting* position, so a
 * recomposition doesn't teleport anyone; the wandering itself is truly random.
 * Under reduced motion the fish rests at its seeded spot, perfectly still.
 */
@Composable
private fun BoxScope.WanderingFish(
    fish: Fish,
    clock: State<Float>,
    reducedMotion: Boolean,
    entering: Boolean = false,
) {
    // Stable start position per fish; wandering targets are unseeded.
    val random = remember(fish.id) { Random(fish.id) }
    // A fish bought/moved just now drifts down from the surface and fades
    // in; the flag is captured once so recompositions can't cancel it.
    val arriveFromSurface = remember(fish.id) { entering && !reducedMotion }
    val homeX = remember(fish.id) { random.nextFloat() * 2 * BIAS_RANGE_X - BIAS_RANGE_X }
    val homeY = remember(fish.id) { random.nextFloat() * 2 * BIAS_RANGE_Y - BIAS_RANGE_Y }
    val x = remember { Animatable(homeX) }
    val y = remember { Animatable(if (arriveFromSurface) -1.4f else homeY) }
    val presence = remember { Animatable(if (arriveFromSurface) 0f else 1f) }
    // Sway/bob phase offset so fish never oscillate in sync.
    val phase = remember(fish.id) { random.nextFloat() * 2f * PI.toFloat() }
    // The placeholder sprite faces LEFT at scaleX 1; −1 mirrors it right.
    val facing = remember { Animatable(if (random.nextBoolean()) 1f else -1f) }
    var swimming by remember { mutableStateOf(false) }

    // Sway shrinks and the idle bob grows when the fish stops to hover.
    val swayDegrees by animateFloatAsState(
        targetValue = when {
            reducedMotion -> 0f
            swimming -> SWAY_SWIM_DEGREES
            else -> SWAY_HOVER_DEGREES
        },
        animationSpec = ReefMotion.gentleSpring(),
        label = "fishSway",
    )
    val bobFraction by animateFloatAsState(
        targetValue = if (!reducedMotion && !swimming) 1f else 0f,
        animationSpec = ReefMotion.gentleSpring(),
        label = "fishBob",
    )

    if (!reducedMotion) {
        LaunchedEffect(fish.id) {
            // Settle-in entrance before the wandering starts.
            if (arriveFromSurface) {
                coroutineScope {
                    launch { presence.animateTo(1f, tween(600, easing = ReefMotion.RevealEasing)) }
                    launch { y.animateTo(homeY, ReefMotion.settleSpring()) }
                }
            }
            while (isActive) {
                val targetX = random.nextFloat() * 2 * BIAS_RANGE_X - BIAS_RANGE_X
                val targetY = random.nextFloat() * 2 * BIAS_RANGE_Y - BIAS_RANGE_Y
                val duration = GameConfig.FISH_SWIM_MIN_MS +
                    random.nextInt(GameConfig.FISH_SWIM_JITTER_MS) +
                    fish.species.slots * GameConfig.FISH_SWIM_PER_SLOT_MS

                swimming = true
                // Different x/y durations make the path curve instead of
                // beeline; the turn animates alongside the first stretch.
                coroutineScope {
                    launch {
                        facing.animateTo(
                            targetValue = if (targetX < x.value) 1f else -1f,
                            animationSpec = ReefMotion.gentleSpring(),
                        )
                    }
                    launch {
                        x.animateTo(targetX, tween(duration, easing = FastOutSlowInEasing))
                    }
                    launch {
                        y.animateTo(
                            targetY,
                            tween(duration + random.nextInt(1_200), easing = FastOutSlowInEasing),
                        )
                    }
                }
                swimming = false
                // Hover in place a moment before swimming on.
                delay(
                    GameConfig.FISH_HOVER_MIN_MS.toLong() +
                        random.nextInt(GameConfig.FISH_HOVER_JITTER_MS),
                )
            }
        }
    }

    FishSprite(
        species = fish.species,
        modifier = Modifier
            .align(BiasAlignment(x.value, y.value))
            .graphicsLayer {
                // Clock reads happen here, in the draw phase — ambient sway
                // and bob never trigger recomposition.
                val t = clock.value
                alpha = presence.value
                scaleX = facing.value
                rotationZ = sin(t * 2.1f + phase) * swayDegrees
                translationY = sin(t * 1.3f + phase) * BOB_AMPLITUDE.toPx() * bobFraction
            },
    )
}
