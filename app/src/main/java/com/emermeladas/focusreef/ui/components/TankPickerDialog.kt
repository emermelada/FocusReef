package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.Tank

/**
 * Dialog listing tanks so the player can pick one — used when buying a fish
 * or decoration (choose where it lives) and when moving a fish.
 *
 * Ineligible tanks are shown but disabled; what "eligible" means is up to the
 * caller (free fish slots, decoration cap, ...).
 *
 * @param tankEnabled Whether a tank can be picked.
 * @param supportingText Second line under each tank's name (e.g. free slots).
 */
@Composable
fun TankPickerDialog(
    title: String,
    tanks: List<Tank>,
    tankEnabled: (Tank) -> Boolean,
    supportingText: @Composable (Tank) -> String,
    onPick: (Tank) -> Unit,
    onDismiss: () -> Unit,
) {
    FocusReefDialog(
        title = title,
        onDismiss = onDismiss,
        buttons = {
            DialogAction(
                text = stringResource(R.string.picker_cancel),
                onClick = onDismiss,
            )
        },
    ) {
        Column {
            tanks.forEach { tank ->
                DialogListRow(
                    headline = tank.name,
                    supporting = supportingText(tank),
                    enabled = tankEnabled(tank),
                    onClick = { onPick(tank) },
                    leading = {
                        TankSprite(
                            tank = tank,
                            modifier = Modifier.size(width = 64.dp, height = 42.dp),
                            showContents = false,
                        )
                    },
                )
            }
        }
    }
}
