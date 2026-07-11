package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.Tank

/**
 * Dialog listing tanks so the player can pick one — used both when buying a
 * fish (choose where it lives) and when moving a fish to another tank.
 *
 * Tanks without enough room for the fish are shown but disabled.
 *
 * @param requiredSlots Free slots the fish needs in the chosen tank.
 */
@Composable
fun TankPickerDialog(
    title: String,
    tanks: List<Tank>,
    requiredSlots: Int,
    onPick: (Tank) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                tanks.forEach { tank ->
                    val enabled = tank.freeSlots >= requiredSlots
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = enabled) { onPick(tank) }
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = tank.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (enabled) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            },
                        )
                        Text(
                            text = stringResource(R.string.tank_free_slots, tank.freeSlots),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (enabled) {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            },
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.picker_cancel))
            }
        },
    )
}
