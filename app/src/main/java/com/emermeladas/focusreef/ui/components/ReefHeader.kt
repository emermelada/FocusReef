package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * The app's signature screen header: the title in Sora with a short wave
 * underline in primary. Every screen opens with this, which is most of what
 * makes the three windows read as one product.
 */
@Composable
fun ReefHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    val waveColor = MaterialTheme.colorScheme.primary
    Column(modifier = modifier) {
        androidx.compose.material3.Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
        // Two gentle wave crests, sized to sit under the first word.
        Canvas(
            modifier = Modifier
                .padding(top = 4.dp)
                .width(44.dp)
                .height(6.dp),
        ) {
            val midY = size.height / 2f
            val wave = Path().apply {
                moveTo(0f, midY)
                quadraticTo(size.width * 0.25f, -midY * 0.6f, size.width * 0.5f, midY)
                quadraticTo(size.width * 0.75f, size.height + midY * 0.6f, size.width, midY)
            }
            drawPath(
                path = wave,
                color = waveColor,
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
            )
        }
    }
}
