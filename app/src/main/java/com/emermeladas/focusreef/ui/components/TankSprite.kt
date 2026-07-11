package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.ui.theme.TankSand
import com.emermeladas.focusreef.ui.theme.WaterBottom
import com.emermeladas.focusreef.ui.theme.WaterTop
import kotlin.random.Random

/**
 * PLACEHOLDER SPRITE — the single point where tank art is rendered.
 *
 * Draws a rounded water gradient with a sand strip and places the tank's fish
 * at deterministic pseudo-random positions (seeded by fish id, so fish don't
 * jump around on recomposition). Swap the background for real art here when
 * sprites are ready; screens never draw tanks themselves.
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

        // Fish, positioned deterministically from their id.
        tank.fish.forEach { fish ->
            val random = Random(fish.id)
            val horizontalBias = random.nextFloat() * 1.7f - 0.85f
            val verticalBias = random.nextFloat() * 1.5f - 0.8f
            val facesLeft = random.nextBoolean()

            FishSprite(
                species = fish.species,
                modifier = Modifier
                    .align(BiasAlignment(horizontalBias, verticalBias))
                    .graphicsLayer { if (facesLeft) scaleX = -1f },
            )
        }
    }
}
