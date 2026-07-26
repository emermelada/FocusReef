package com.emermeladas.focusreef.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.ui.theme.ReefMotion
import com.emermeladas.focusreef.utils.rememberReducedMotion

/**
 * The reef's capacity gauge — a rounded track that fills like water rather
 * than a stock progress bar: gradient fill with a surface highlight, quarter
 * tick marks, and a gentle fill-up animation when it appears (instant under
 * reduced motion). Purely decorative next to its written "n/24" label, so it
 * carries no semantics of its own.
 */
@Composable
fun CapacityGauge(
    fraction: Float,
    modifier: Modifier = Modifier,
    ticks: Int = 3,
    fillColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
) {
    val reducedMotion = rememberReducedMotion()
    val fill = remember { Animatable(if (reducedMotion) fraction else 0f) }
    LaunchedEffect(fraction) {
        fill.animateTo(
            targetValue = fraction.coerceIn(0f, 1f),
            animationSpec = tween(ReefMotion.REVEAL_MS + 150, easing = ReefMotion.RevealEasing),
        )
    }

    // Ticks read on top of the fill; a translucent dark notch works on any hue.
    val tickColor = Color.Black.copy(alpha = 0.22f)

    Canvas(modifier = modifier.height(10.dp)) {
        val radius = CornerRadius(size.height / 2f)
        drawRoundRect(color = trackColor, cornerRadius = radius)

        val fillWidth = size.width * fill.value
        if (fillWidth > 0f) {
            // Water filling the track, darker toward its leading edge.
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    0f to fillColor.copy(alpha = 0.75f),
                    1f to fillColor,
                    endX = fillWidth,
                ),
                size = Size(fillWidth.coerceAtLeast(size.height), size.height),
                cornerRadius = radius,
            )
            // A thin light catch along the water's surface.
            val inset = size.height * 0.5f
            if (fillWidth > inset * 2.5f) {
                drawLine(
                    color = Color.White.copy(alpha = 0.35f),
                    start = Offset(inset, size.height * 0.30f),
                    end = Offset(fillWidth - inset, size.height * 0.30f),
                    strokeWidth = 1.dp.toPx(),
                    cap = StrokeCap.Round,
                )
            }
        }

        // Optional evenly-spaced ticks so "how full" reads at a glance
        // (used by the tank slot gauge; the XP bar passes ticks = 0).
        for (tick in 1..ticks) {
            val x = size.width * tick / (ticks + 1)
            drawLine(
                color = tickColor,
                start = Offset(x, size.height * 0.2f),
                end = Offset(x, size.height * 0.8f),
                strokeWidth = 1.dp.toPx(),
            )
        }
    }
}
