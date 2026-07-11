package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.ui.theme.TokenGold
import com.emermeladas.focusreef.ui.theme.TokenGoldDark
import com.emermeladas.focusreef.ui.theme.TokenGoldLight

/**
 * The focus-token coin: a simple gold disc with a rim and inner highlight.
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
        drawCircle(color = TokenGold, radius = radius)
        drawCircle(
            color = TokenGoldDark,
            radius = radius - radius * 0.06f,
            style = Stroke(width = radius * 0.12f),
        )
        drawCircle(color = TokenGoldLight, radius = radius * 0.58f)
    }
}
