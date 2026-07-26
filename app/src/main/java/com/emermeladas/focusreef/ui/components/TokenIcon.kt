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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.ui.theme.TokenGold
import com.emermeladas.focusreef.ui.theme.TokenGoldDark
import com.emermeladas.focusreef.ui.theme.TokenGoldLight

/**
 * The focus-token coin: a dimensional gold disc — radial face shading, a
 * beveled two-tone rim, an engraved fish motif, and a specular glint.
 *
 * Purely decorative (always shown next to a written token amount), so it
 * needs no content description.
 */
@Composable
fun TokenIcon(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        // Face: lit from the upper left.
        drawCircle(
            brush = Brush.radialGradient(
                0f to TokenGoldLight,
                0.75f to TokenGold,
                1f to TokenGoldDark,
                center = center + Offset(-radius * 0.25f, -radius * 0.3f),
                radius = radius * 1.5f,
            ),
            radius = radius,
            center = center,
        )
        // Beveled rim: darker outer ring, lighter inner ring.
        drawCircle(
            color = TokenGoldDark,
            radius = radius * 0.94f,
            center = center,
            style = Stroke(width = radius * 0.11f),
        )
        drawCircle(
            color = TokenGoldLight.copy(alpha = 0.8f),
            radius = radius * 0.80f,
            center = center,
            style = Stroke(width = radius * 0.05f),
        )

        // Engraved fish motif: a small body + tail pressed into the face.
        val engraving = TokenGoldDark.copy(alpha = 0.75f)
        drawOval(
            color = engraving,
            topLeft = center + Offset(-radius * 0.42f, -radius * 0.20f),
            size = Size(radius * 0.62f, radius * 0.40f),
        )
        val tail = Path().apply {
            moveTo(center.x + radius * 0.16f, center.y)
            lineTo(center.x + radius * 0.44f, center.y - radius * 0.22f)
            lineTo(center.x + radius * 0.44f, center.y + radius * 0.22f)
            close()
        }
        drawPath(tail, color = engraving)

        // Specular glint: a soft off-center arc of caught light.
        drawArc(
            color = Color.White.copy(alpha = 0.55f),
            startAngle = 205f,
            sweepAngle = 55f,
            useCenter = false,
            topLeft = center - Offset(radius * 0.62f, radius * 0.62f),
            size = Size(radius * 1.24f, radius * 1.24f),
            style = Stroke(width = radius * 0.09f),
        )
    }
}
