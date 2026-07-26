package com.emermeladas.focusreef.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.material3.MaterialTheme
import com.emermeladas.focusreef.utils.rememberReducedMotion

/**
 * Loading placeholder: a soft container block with a slow light sweep — the
 * app's answer to a spinner. Compose one per block of content being awaited
 * (a card, a chart, a tank) with roughly its final size, so nothing jumps
 * when the data lands. Static under reduced motion.
 */
@Composable
fun ShimmerSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
) {
    val baseColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val sweepColor = MaterialTheme.colorScheme.surfaceContainerLowest

    val brush = if (rememberReducedMotion()) {
        Brush.linearGradient(listOf(baseColor, baseColor))
    } else {
        val transition = rememberInfiniteTransition(label = "shimmer")
        val progress by transition.animateFloat(
            initialValue = -1f,
            targetValue = 2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1_600, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "shimmerSweep",
        )
        Brush.linearGradient(
            colors = listOf(baseColor, sweepColor, baseColor),
            start = Offset(progress * 900f, 0f),
            end = Offset((progress + 1f) * 900f, 220f),
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush),
    )
}
