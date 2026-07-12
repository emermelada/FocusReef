package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.Decoration
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.Tank

/**
 * Dialog opened by tapping a tank: shows how many fish of each species live
 * in it (with a Move action per species) and the placed decorations (with a
 * Move action that reopens placement mode).
 *
 * @param canMoveSpecies Whether any *other* tank has room for that species —
 * decides if the Move button is enabled.
 * @param onMoveSpecies The player wants to move one fish of this species out.
 * @param onRepositionDecoration The player wants to drag this decoration to a new spot.
 */
@Composable
fun TankDetailDialog(
    tank: Tank,
    canMoveSpecies: (FishSpecies) -> Boolean,
    onMoveSpecies: (FishSpecies) -> Unit,
    onRepositionDecoration: (Decoration) -> Unit,
    onDismiss: () -> Unit,
) {
    // Count fish per species, keeping the enum's small→large display order.
    val counts = tank.fish.groupingBy { it.species }.eachCount()

    FocusReefDialog(
        title = tank.name,
        onDismiss = onDismiss,
        buttons = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.tank_detail_close))
            }
        },
    ) {
        Column {
            SlotUsageStrip(tank)

            if (tank.fish.isEmpty()) {
                Text(
                    text = stringResource(R.string.tank_detail_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            FishSpecies.entries.forEach { species ->
                val count = counts[species] ?: return@forEach
                DialogListRow(
                    headline = species.displayName,
                    supporting = stringResource(R.string.tank_species_count_short, count),
                    leading = {
                        // Fixed-size box so rows align across sprite sizes.
                        Box(
                            modifier = Modifier.size(56.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            FishSprite(species)
                        }
                    },
                    trailing = {
                        TextButton(
                            onClick = { onMoveSpecies(species) },
                            enabled = canMoveSpecies(species),
                        ) {
                            Text(stringResource(R.string.tank_detail_move))
                        }
                    },
                )
            }

            if (tank.decorations.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.tank_detail_decorations),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                )
                tank.decorations.forEach { decoration ->
                    DialogListRow(
                        headline = decoration.species.displayName,
                        supporting = null,
                        leading = {
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                DecorationSprite(decoration.species)
                            }
                        },
                        trailing = {
                            TextButton(onClick = { onRepositionDecoration(decoration) }) {
                                Text(stringResource(R.string.tank_decoration_move))
                            }
                        },
                    )
                }
            }
        }
    }
}

/**
 * Labeled capacity bar: "n/24 slots" over a thin progress strip.
 */
@Composable
private fun SlotUsageStrip(tank: Tank) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = stringResource(R.string.tank_slots_used, tank.usedSlots, tank.capacitySlots),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        LinearProgressIndicator(
            progress = {
                if (tank.capacitySlots == 0) 0f
                else tank.usedSlots.toFloat() / tank.capacitySlots
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
        )
    }
}
