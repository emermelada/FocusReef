package com.emermeladas.focusreef.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.emermeladas.focusreef.R

/**
 * The gear in a screen's top bar.
 *
 * Lives in one place so all three screens open settings from the same spot —
 * a control that moves between tabs is a control the player has to hunt for.
 */
@Composable
fun SettingsAction(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = stringResource(R.string.settings_open),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
