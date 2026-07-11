package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.Tank

/**
 * Dialog opened by tapping a tank: shows how many fish of each species live
 * in it, with a Move action per species.
 *
 * @param canMoveSpecies Whether any *other* tank has room for that species —
 * decides if the Move button is enabled.
 * @param onMoveSpecies The player wants to move one fish of this species out.
 */
@Composable
fun TankDetailDialog(
    tank: Tank,
    canMoveSpecies: (FishSpecies) -> Boolean,
    onMoveSpecies: (FishSpecies) -> Unit,
    onDismiss: () -> Unit,
) {
    // Count fish per species, keeping the enum's small→large display order.
    val counts = tank.fish.groupingBy { it.species }.eachCount()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(tank.name) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(
                        R.string.tank_slots_used,
                        tank.usedSlots,
                        tank.capacitySlots,
                    ),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )

                if (tank.fish.isEmpty()) {
                    Text(
                        text = stringResource(R.string.tank_detail_empty),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                FishSpecies.entries.forEach { species ->
                    val count = counts[species] ?: return@forEach
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // Fixed-size box so rows align across sprite sizes.
                        Box(
                            modifier = Modifier.size(56.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            FishSprite(species)
                        }
                        Text(
                            text = stringResource(
                                R.string.tank_species_count,
                                species.displayName,
                                count,
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        TextButton(
                            onClick = { onMoveSpecies(species) },
                            enabled = canMoveSpecies(species),
                        ) {
                            Text(stringResource(R.string.tank_detail_move))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.tank_detail_close))
            }
        },
    )
}
