package com.emermeladas.focusreef.ui.screens.tanks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.ui.components.TankSprite
import androidx.compose.ui.unit.dp

/**
 * Window 1 — the aquarium: every owned tank with its fish and slot usage.
 */
@Composable
fun TanksScreen(
    viewModel: TanksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.tanks_title),
                style = MaterialTheme.typography.titleLarge,
            )
        }
        items(uiState.tanks, key = { it.id }) { tank ->
            TankCard(tank)
        }
        // Gentle onboarding hint while the aquarium is still empty.
        if (!uiState.isLoading && uiState.tanks.all { it.fish.isEmpty() }) {
            item {
                Text(
                    text = stringResource(R.string.tanks_empty_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * One tank: header row (name + slot usage) above its sprite.
 */
@Composable
private fun TankCard(tank: Tank) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = tank.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(
                    R.string.tank_slots_used,
                    tank.usedSlots,
                    tank.capacitySlots,
                ),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        TankSprite(
            tank = tank,
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
        )
    }
}
