package com.emermeladas.focusreef.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.data.model.Fish
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.ui.theme.TankSand
import com.emermeladas.focusreef.ui.theme.WaterBottom
import com.emermeladas.focusreef.ui.theme.WaterTop
import kotlin.random.Random
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Horizontal swim range in alignment-bias units (−1 = left edge, 1 = right). */
private const val BIAS_RANGE_X = 0.85f

/** Vertical swim range, slightly smaller so fish stay off the sand strip. */
private const val BIAS_RANGE_Y = 0.72f

/**
 * PLACEHOLDER SPRITE — the single point where tank art is rendered.
 *
 * Draws a rounded water gradient with a sand strip; each fish wanders the
 * tank on its own (see [WanderingFish]). Swap the background for real art
 * here when sprites are ready; screens never draw tanks themselves.
 */
@Composable
fun TankSprite(
    tank: Tank,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.verticalGradient(listOf(WaterTop, WaterBottom)))
            .border(
                width = 2.dp,
                color = Color.White.copy(alpha = 0.35f),
                shape = RoundedCornerShape(20.dp),
            ),
    ) {
        // Sand floor.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(14.dp)
                .background(TankSand.copy(alpha = 0.85f)),
        )

        tank.fish.forEach { fish ->
            WanderingFish(fish)
        }
    }
}

/**
 * One fish that swims to a random point in the tank, pauses briefly, then
 * picks a new destination — forever. The sprite flips to face its direction
 * of travel.
 *
 * Randomness is seeded per fish id only for the *starting* position, so a
 * recomposition doesn't teleport anyone; the wandering itself is truly random.
 */
@Composable
private fun BoxScope.WanderingFish(fish: Fish) {
    // Stable start position per fish; wandering targets are unseeded.
    val random = remember(fish.id) { Random(fish.id) }
    val x = remember { Animatable(random.nextFloat() * 2 * BIAS_RANGE_X - BIAS_RANGE_X) }
    val y = remember { Animatable(random.nextFloat() * 2 * BIAS_RANGE_Y - BIAS_RANGE_Y) }
    // The placeholder sprite faces LEFT by default.
    var facingLeft by remember { mutableStateOf(random.nextBoolean()) }

    LaunchedEffect(fish.id) {
        while (isActive) {
            val targetX = random.nextFloat() * 2 * BIAS_RANGE_X - BIAS_RANGE_X
            val targetY = random.nextFloat() * 2 * BIAS_RANGE_Y - BIAS_RANGE_Y
            facingLeft = targetX < x.value

            // Different x/y durations make the path curve instead of beeline.
            val duration = 2_500 + random.nextInt(3_500)
            coroutineScope {
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
            // Hover in place a moment before swimming on.
            delay(300L + random.nextInt(1_700))
        }
    }

    FishSprite(
        species = fish.species,
        modifier = Modifier
            .align(BiasAlignment(x.value, y.value))
            .graphicsLayer { if (!facingLeft) scaleX = -1f },
    )
}
