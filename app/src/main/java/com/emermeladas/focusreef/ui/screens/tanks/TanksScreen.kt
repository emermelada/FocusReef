package com.emermeladas.focusreef.ui.screens.tanks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.ui.components.TankDetailDialog
import com.emermeladas.focusreef.ui.components.TankPickerDialog
import com.emermeladas.focusreef.ui.components.TankSprite

/**
 * Window 1 — the aquarium: every owned tank with its fish and slot usage.
 *
 * Tapping a tank opens [TankDetailDialog] with per-species counts and the
 * option to move fish to another tank.
 */
@Composable
fun TanksScreen(
    viewModel: TanksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messageRes by viewModel.userMessageRes.collectAsStateWithLifecycle()

    // Dialog state is pure UI state, so it lives here, not in the ViewModel.
    var selectedTankId by remember { mutableStateOf<Long?>(null) }
    var speciesToMove by remember { mutableStateOf<FishSpecies?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    messageRes?.let { res ->
        val message = stringResource(res)
        LaunchedEffect(res, message) {
            snackbarHostState.showSnackbar(message)
            viewModel.onMessageShown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                TankCard(tank = tank, onClick = { selectedTankId = tank.id })
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    // Detail dialog for the tapped tank (fresh data straight from uiState).
    val selectedTank = uiState.tanks.firstOrNull { it.id == selectedTankId }
    val movingSpecies = speciesToMove

    if (selectedTank != null && movingSpecies == null) {
        TankDetailDialog(
            tank = selectedTank,
            canMoveSpecies = { species ->
                uiState.tanks.any { it.id != selectedTank.id && it.hasRoomFor(species) }
            },
            onMoveSpecies = { speciesToMove = it },
            onDismiss = { selectedTankId = null },
        )
    }

    // Destination picker once a species has been chosen to move.
    if (selectedTank != null && movingSpecies != null) {
        TankPickerDialog(
            title = stringResource(R.string.move_fish_title),
            tanks = uiState.tanks.filter { it.id != selectedTank.id },
            tankEnabled = { it.hasRoomFor(movingSpecies) },
            supportingText = { stringResource(R.string.tank_free_slots, it.freeSlots) },
            onPick = { destination ->
                viewModel.moveFish(movingSpecies, selectedTank.id, destination.id)
                speciesToMove = null
            },
            onDismiss = { speciesToMove = null },
        )
    }
}

/**
 * One tank: header row (name + slot usage) above its sprite. Tapping
 * anywhere opens the tank's detail dialog.
 */
@Composable
private fun TankCard(tank: Tank, onClick: () -> Unit) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
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
