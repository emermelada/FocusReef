package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.DecorationPlacement
import com.emermeladas.focusreef.data.model.DecorationSpecies
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.utils.BiasPoint
import com.emermeladas.focusreef.utils.PlacementMath

/**
 * Placement mode: a big view of [tank] where the player drags the decoration
 * to its spot, then confirms or cancels.
 *
 * Floor items only move horizontally (their base stays in the sand); floating
 * items move freely inside the water column. All coordinate math lives in
 * [PlacementMath]; this composable only plumbs gestures and state.
 *
 * @param tank The destination tank; pass it WITHOUT the decoration being
 * placed (or it would render twice).
 * @param initialPosition Starting spot: the default for new purchases, the
 * saved position when repositioning.
 */
@Composable
fun DecorationPlacementOverlay(
    tank: Tank,
    species: DecorationSpecies,
    initialPosition: BiasPoint,
    onConfirm: (BiasPoint) -> Unit,
    onCancel: () -> Unit,
) {
    var position by remember { mutableStateOf(PlacementMath.clamp(initialPosition, species.placement)) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    val density = LocalDensity.current
    val itemSize = decorationSizeFor(species)
    val itemWidthPx = with(density) { itemSize.width.toPx() }
    val itemHeightPx = with(density) { itemSize.height.toPx() }

    FocusReefDialog(
        title = stringResource(R.string.placement_title, species.displayName),
        onDismiss = onCancel,
        buttons = {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.placement_cancel))
            }
            Button(onClick = { onConfirm(position) }) {
                Text(stringResource(R.string.placement_confirm))
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .onSizeChanged { containerSize = it }
                .pointerInput(species) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        position = PlacementMath.clamp(
                            BiasPoint(
                                x = position.x + PlacementMath.dragDeltaToBiasDelta(
                                    dragAmount.x,
                                    containerSize.width.toFloat(),
                                    itemWidthPx,
                                ),
                                y = position.y + PlacementMath.dragDeltaToBiasDelta(
                                    dragAmount.y,
                                    containerSize.height.toFloat(),
                                    itemHeightPx,
                                ),
                            ),
                            species.placement,
                        )
                    }
                },
        ) {
            TankSprite(tank = tank, modifier = Modifier.matchParentSize())
            DecorationSprite(
                species = species,
                modifier = Modifier.align(BiasAlignment(position.x, position.y)),
            )
        }
        Text(
            text = stringResource(
                when (species.placement) {
                    DecorationPlacement.FLOOR -> R.string.placement_hint_floor
                    DecorationPlacement.FLOATING -> R.string.placement_hint_floating
                },
            ),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
