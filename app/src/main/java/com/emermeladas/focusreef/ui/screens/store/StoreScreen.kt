package com.emermeladas.focusreef.ui.screens.store

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.emermeladas.focusreef.ui.components.FishSprite
import com.emermeladas.focusreef.ui.components.TankPickerDialog
import com.emermeladas.focusreef.utils.GameConfig

/**
 * Window 3 — the fish store: token balance on top, then the three fish
 * species and additional tanks.
 */
@Composable
fun StoreScreen(
    viewModel: StoreViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messageRes by viewModel.userMessageRes.collectAsStateWithLifecycle()

    // Species waiting for the player to choose a destination tank.
    var speciesToBuy by remember { mutableStateOf<FishSpecies?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    // Show purchase feedback once, then let the ViewModel clear it.
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.store_title),
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            item { BalanceCard(tokens = uiState.wallet?.availableTokens) }

            item { SectionHeader(stringResource(R.string.store_section_fish)) }
            FishSpecies.entries.forEach { species ->
                item(key = species.name) {
                    FishStoreItem(
                        species = species,
                        enabled = uiState.canBuyFish(species),
                        onBuy = {
                            // With a single tank there is nothing to choose;
                            // otherwise ask where the fish should live.
                            val tanksWithRoom = uiState.tanks.filter { it.hasRoomFor(species) }
                            if (uiState.tanks.size == 1 && tanksWithRoom.size == 1) {
                                viewModel.buyFish(species, tanksWithRoom.first().id)
                            } else {
                                speciesToBuy = species
                            }
                        },
                    )
                }
            }

            item { SectionHeader(stringResource(R.string.store_section_tanks)) }
            item {
                TankStoreItem(
                    enabled = uiState.canBuyTank,
                    onBuy = viewModel::buyTank,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    // Tank chooser for the pending fish purchase.
    speciesToBuy?.let { species ->
        TankPickerDialog(
            title = stringResource(R.string.store_choose_tank),
            tanks = uiState.tanks,
            requiredSlots = species.slots,
            onPick = { tank ->
                viewModel.buyFish(species, tank.id)
                speciesToBuy = null
            },
            onDismiss = { speciesToBuy = null },
        )
    }
}

/**
 * Prominent card showing the spendable token balance.
 */
@Composable
private fun BalanceCard(tokens: Long?) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.store_balance_label),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = tokens?.let { stringResource(R.string.store_balance, it) }
                    ?: stringResource(R.string.loading),
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

/**
 * One purchasable fish: placeholder sprite, name, slots, price, Buy button.
 */
@Composable
private fun FishStoreItem(
    species: FishSpecies,
    enabled: Boolean,
    onBuy: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Fixed-size box so rows align regardless of sprite size.
            Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                FishSprite(species)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(species.displayName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = slotsText(species.slots) + " · " +
                        stringResource(R.string.store_price, species.priceTokens),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(onClick = onBuy, enabled = enabled) {
                Text(stringResource(R.string.store_buy))
            }
        }
    }
}

/**
 * The purchasable extra tank.
 */
@Composable
private fun TankStoreItem(
    enabled: Boolean,
    onBuy: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.store_item_tank),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = stringResource(
                        R.string.store_item_tank_description,
                        GameConfig.TANK_CAPACITY_SLOTS,
                    ) + " · " + stringResource(R.string.store_price, GameConfig.TANK_PRICE_TOKENS),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(onClick = onBuy, enabled = enabled) {
                Text(stringResource(R.string.store_buy))
            }
        }
    }
}

/**
 * "1 slot" / "3 slots" with the right plural form.
 */
@Composable
private fun slotsText(slots: Int): String =
    if (slots == 1) stringResource(R.string.store_slots, slots)
    else stringResource(R.string.store_slots_plural, slots)

/**
 * Small section title between store groups.
 */
@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp),
    )
}
