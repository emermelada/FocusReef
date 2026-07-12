package com.emermeladas.focusreef.ui.screens.store

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.Progression
import com.emermeladas.focusreef.ui.components.FishSprite
import com.emermeladas.focusreef.ui.components.LevelProgressRow
import com.emermeladas.focusreef.ui.components.TankPickerDialog
import com.emermeladas.focusreef.ui.components.TokenIcon
import com.emermeladas.focusreef.utils.GameConfig

/**
 * Window 3 — the fish store: a token-balance hero, the three fish as a
 * card grid, and additional tanks.
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
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = stringResource(R.string.store_title),
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                BalanceHero(
                    tokens = uiState.wallet?.availableTokens,
                    progression = uiState.progression,
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(stringResource(R.string.store_section_fish))
            }

            items(FishSpecies.entries, key = { it.name }) { species ->
                FishCard(
                    species = species,
                    enabled = uiState.canBuyFish(species),
                    locked = uiState.isFishLocked(species),
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

            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeader(stringResource(R.string.store_section_tanks))
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
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
            tankEnabled = { it.hasRoomFor(species) },
            supportingText = { stringResource(R.string.tank_free_slots, it.freeSlots) },
            onPick = { tank ->
                viewModel.buyFish(species, tank.id)
                speciesToBuy = null
            },
            onDismiss = { speciesToBuy = null },
        )
    }
}

/**
 * Prominent balance card: label, big token amount, the coin, and the player's
 * level/streak progress (the level lives here because it gates purchases).
 */
@Composable
private fun BalanceHero(tokens: Long?, progression: Progression?) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.store_balance_label),
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = tokens?.let { stringResource(R.string.store_balance, it) }
                            ?: stringResource(R.string.loading),
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
                TokenIcon(size = 44.dp)
            }
            progression?.let { LevelProgressRow(it) }
        }
    }
}

/**
 * One purchasable fish as a shop card: a water-tinted showcase with the
 * sprite, name, slot size and a price button. Locked species render dimmed
 * with a padlock and the required level instead of the price.
 */
@Composable
private fun FishCard(
    species: FishSpecies,
    enabled: Boolean,
    locked: Boolean,
    onBuy: () -> Unit,
) {
    Card {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Sprite showcase; same height for all species so cards align.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Box(modifier = Modifier.alpha(if (locked) 0.35f else 1f)) {
                    FishSprite(species)
                }
                if (locked) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
            Text(
                text = species.displayName,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = slotsText(species.slots),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = onBuy,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = if (locked) {
                        stringResource(R.string.store_locked_level, species.unlockLevel)
                    } else {
                        stringResource(R.string.store_price, species.priceTokens)
                    },
                )
            }
        }
    }
}

/**
 * The purchasable extra tank (full-width card).
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
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Button(onClick = onBuy, enabled = enabled) {
                Text(stringResource(R.string.store_price, GameConfig.TANK_PRICE_TOKENS))
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
