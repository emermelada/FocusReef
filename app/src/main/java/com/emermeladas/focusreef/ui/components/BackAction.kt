package com.emermeladas.focusreef.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.emermeladas.focusreef.R

/**
 * The back arrow in a screen's top bar.
 *
 * The counterpart to [SettingsAction]: any screen you can open from the gear
 * needs a visible way back, because the system Back gesture alone leaves the
 * screen looking like a dead end.
 *
 * @param contentDescription Spoken label, so each screen can name what it closes.
 */
@Composable
fun BackAction(
    onClick: () -> Unit,
    contentDescription: String = stringResource(R.string.action_back),
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
