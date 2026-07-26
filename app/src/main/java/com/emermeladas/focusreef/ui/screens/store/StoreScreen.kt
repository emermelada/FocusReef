package com.emermeladas.focusreef.ui.screens.store

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.DecorationPlacement
import com.emermeladas.focusreef.data.model.DecorationSpecies
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.Progression
import com.emermeladas.focusreef.ui.components.DecorationPlacementOverlay
import com.emermeladas.focusreef.ui.components.DecorationSprite
import com.emermeladas.focusreef.ui.components.FishSprite
import com.emermeladas.focusreef.ui.components.LevelProgressRow
import com.emermeladas.focusreef.ui.components.ReefBuyButton
import com.emermeladas.focusreef.ui.components.ReefContentCard
import com.emermeladas.focusreef.ui.components.ReefErrorState
import com.emermeladas.focusreef.ui.components.ReefHeroCard
import com.emermeladas.focusreef.ui.components.ReefScreenScaffold
import com.emermeladas.focusreef.ui.components.ReefToast
import com.emermeladas.focusreef.ui.components.SettingsAction
import com.emermeladas.focusreef.ui.components.SectionLabel
import com.emermeladas.focusreef.ui.components.TankPickerDialog
import com.emermeladas.focusreef.ui.components.TokenIcon
import com.emermeladas.focusreef.ui.theme.ReefMotion
import com.emermeladas.focusreef.ui.theme.ReefSpacing
import com.emermeladas.focusreef.utils.GameConfig
import com.emermeladas.focusreef.utils.PlacementMath

/**
 * Merchandising order for the shelves: cheapest, earliest-unlocked first.
 *
 * The grid used to render in enum declaration order, which is an
 * implementation detail — it put a level-5 item between two starter ones and
 * the shelf read as random. Sorting by unlock level, then price, makes the
 * grid tell the progression story: what you can buy now sits at the top, what
 * you are working toward sits below it.
 */
private val fishShelf: List<FishSpecies> =
    FishSpecies.entries.sortedWith(compareBy({ it.unlockLevel }, { it.priceTokens }))

private val decorationShelf: List<DecorationSpecies> =
    DecorationSpecies.entries.sortedWith(compareBy({ it.unlockLevel }, { it.priceTokens }))

/** Fixed height of a card's sprite showcase, so every card starts alike. */
private val ShowcaseHeight = 84.dp

/**
 * Window 3 — the fish store: a token-balance hero, the three fish as a
 * card grid, and additional tanks.
 *
 * @param outerPadding insets from the app scaffold, spent as content padding
 *   so the grid scrolls under the navigation bar.
 * @param onOpenSettings opens the settings destination from the top bar.
 */
@Composable
fun StoreScreen(
    outerPadding: PaddingValues,
    onOpenSettings: () -> Unit,
    viewModel: StoreViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messageRes by viewModel.userMessageRes.collectAsStateWithLifecycle()

    // A light haptic tap whenever a purchase actually lands (spent total
    // grows) — one central hook instead of one per buy button.
    val haptic = LocalHapticFeedback.current
    val spentTokens = uiState.wallet?.spentTokens
    var lastSpentTokens by remember { mutableStateOf<Long?>(null) }
    LaunchedEffect(spentTokens) {
        val last = lastSpentTokens
        if (GameConfig.HAPTICS_ENABLED && last != null && spentTokens != null && spentTokens > last) {
            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
        }
        if (spentTokens != null) lastSpentTokens = spentTokens
    }

    // Species waiting for the player to choose a destination tank.
    var speciesToBuy by remember { mutableStateOf<FishSpecies?>(null) }

    // Decoration waiting for the player to choose a destination tank.
    var decorationToBuy by remember { mutableStateOf<DecorationSpecies?>(null) }

    val pendingPlacement by viewModel.pendingPlacement.collectAsStateWithLifecycle()

    // Items the player cannot reach yet move to their own shelf at the bottom.
    // Interleaving them with buyable stock made the store read as broken —
    // half the grid greyed out with no explanation of the pattern. Collected
    // under "Coming up" the same items become a progression teaser.
    val (lockedFish, unlockedFish) = fishShelf.partition { uiState.isFishLocked(it) }
    val (lockedDecorations, unlockedDecorations) =
        decorationShelf.partition { uiState.isDecorationLocked(it) }

    ReefScreenScaffold(
        title = stringResource(R.string.store_title),
        outerPadding = outerPadding,
        actions = { SettingsAction(onClick = onOpenSettings) },
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(integerResource(R.integer.store_grid_columns)),
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(ReefSpacing.md),
                verticalArrangement = Arrangement.spacedBy(ReefSpacing.md),
            ) {
                val errorRes = uiState.errorRes
                if (errorRes != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ReefErrorState(messageRes = errorRes, onRetry = viewModel::retry)
                    }
                    return@LazyVerticalGrid
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

                items(unlockedFish, key = { it.name }) { species ->
                    FishCard(
                        species = species,
                        buyState = uiState.buyState(species),
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
                    SectionHeader(stringResource(R.string.store_section_decorations))
                }

                items(unlockedDecorations, key = { "deco_${it.name}" }) { species ->
                    DecorationCard(
                        species = species,
                        buyState = uiState.buyState(species),
                        onBuy = {
                            // Same shortcut as fish: a single eligible tank needs no picker.
                            val tanksWithRoom = uiState.tanks.filter { it.hasRoomForDecoration() }
                            if (uiState.tanks.size == 1 && tanksWithRoom.size == 1) {
                                viewModel.buyDecoration(species, tanksWithRoom.first().id)
                            } else {
                                decorationToBuy = species
                            }
                        },
                    )
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    SectionHeader(stringResource(R.string.store_section_tanks))
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    TankStoreItem(
                        buyState = uiState.tankBuyState,
                        onBuy = viewModel::buyTank,
                    )
                }

                if (lockedFish.isNotEmpty() || lockedDecorations.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SectionHeader(stringResource(R.string.store_section_coming_up))
                    }

                    items(lockedFish, key = { "locked_${it.name}" }) { species ->
                        FishCard(
                            species = species,
                            buyState = BuyState.LockedByLevel(species.unlockLevel),
                            onBuy = {},
                        )
                    }

                    items(lockedDecorations, key = { "locked_deco_${it.name}" }) { species ->
                        DecorationCard(
                            species = species,
                            buyState = BuyState.LockedByLevel(species.unlockLevel),
                            onBuy = {},
                        )
                    }
                }
            }

            // Purchase feedback as the app's quiet toast.
            ReefToast(
                messageRes = messageRes,
                onShown = viewModel::onMessageShown,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = outerPadding.calculateBottomPadding()),
            )
        }
    }

    // Tank chooser for the pending fish purchase.
    speciesToBuy?.let { species ->
        TankPickerDialog(
            title = stringResource(R.string.store_choose_tank),
            tanks = uiState.tanks,
            tankEnabled = { it.hasRoomFor(species) },
            supportingText = {
                pluralStringResource(R.plurals.tank_free_slots, it.freeSlots, it.freeSlots)
            },
            onPick = { tank ->
                viewModel.buyFish(species, tank.id)
                speciesToBuy = null
            },
            onDismiss = { speciesToBuy = null },
        )
    }

    // Tank chooser for the pending decoration purchase.
    decorationToBuy?.let { species ->
        TankPickerDialog(
            title = stringResource(R.string.store_choose_tank_decoration),
            tanks = uiState.tanks,
            tankEnabled = { it.hasRoomForDecoration() },
            supportingText = {
                stringResource(
                    R.string.tank_decoration_count,
                    it.decorations.size,
                    GameConfig.TANK_DECORATION_CAP,
                )
            },
            onPick = { tank ->
                viewModel.buyDecoration(species, tank.id)
                decorationToBuy = null
            },
            onDismiss = { decorationToBuy = null },
        )
    }

    // Placement mode for the decoration that was just bought.
    pendingPlacement?.let { pending ->
        val tank = uiState.tanks.firstOrNull { it.id == pending.tankId } ?: return@let
        DecorationPlacementOverlay(
            // Hide the decoration being placed or it would render twice.
            tank = tank.copy(
                decorations = tank.decorations.filterNot { it.id == pending.decorationId },
            ),
            species = pending.species,
            initialPosition = PlacementMath.defaultPosition(pending.species.placement),
            onConfirm = viewModel::confirmPlacement,
            onCancel = viewModel::dismissPlacement,
        )
    }
}

/**
 * Prominent balance card: label, big token amount, the coin, and the player's
 * level/streak progress (the level lives here because it gates purchases).
 */
@Composable
private fun BalanceHero(tokens: Long?, progression: Progression?) {
    ReefHeroCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                SectionLabel(text = stringResource(R.string.store_balance_label))
                if (tokens == null) {
                    Text(
                        text = stringResource(R.string.loading),
                        style = MaterialTheme.typography.displaySmall,
                    )
                } else {
                    // The balance counts to its new value instead of
                    // jumping — spending (or earning) tokens is felt.
                    val animatedTokens by animateIntAsState(
                        targetValue = tokens.toInt(),
                        animationSpec = tween(
                            ReefMotion.COUNT_MS,
                            easing = ReefMotion.RevealEasing,
                        ),
                        label = "balanceCount",
                    )
                    // High-emphasis hero: heavy weight, full-strength
                    // content color — the balance should command the card.
                    Text(
                        text = pluralStringResource(
                            R.plurals.store_balance,
                            animatedTokens,
                            animatedTokens,
                        ),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                }
            }
            TokenIcon(size = 44.dp)
        }
        progression?.let { LevelProgressRow(it) }
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
    buyState: BuyState,
    onBuy: () -> Unit,
) {
    StoreItemCard(
        buyState = buyState,
        name = stringResource(species.displayNameRes),
        subtitle = slotsText(species.slots),
        price = species.priceTokens,
        onBuy = onBuy,
        showcase = { FishSprite(species) },
    )
}

/**
 * One purchasable decoration as a shop card — same anatomy as [FishCard]
 * so the grid stays visually consistent.
 */
@Composable
private fun DecorationCard(
    species: DecorationSpecies,
    buyState: BuyState,
    onBuy: () -> Unit,
) {
    StoreItemCard(
        buyState = buyState,
        name = stringResource(species.displayNameRes),
        subtitle = stringResource(
            when (species.placement) {
                DecorationPlacement.FLOOR -> R.string.store_decoration_floor
                DecorationPlacement.FLOATING -> R.string.store_decoration_floating
            },
        ),
        price = species.priceTokens,
        onBuy = onBuy,
        showcase = { DecorationSprite(species) },
    )
}

/**
 * The shared anatomy of a grid shop card, extracted so fish and decorations
 * cannot drift apart: showcase, name, subtitle, then the buy button.
 *
 * The card fills its row's height and pushes the button down with a flexible
 * spacer. Without that, a one-line name and a two-line name produce buttons
 * at two different heights side by side, which is the single most visible
 * "unfinished" tell in a grid.
 */
@Composable
private fun StoreItemCard(
    buyState: BuyState,
    name: String,
    subtitle: String,
    price: Long,
    onBuy: () -> Unit,
    showcase: @Composable () -> Unit,
) {
    val locked = buyState is BuyState.LockedByLevel
    ReefContentCard(
        padding = ReefSpacing.md,
        spacing = ReefSpacing.sm,
        modifier = Modifier.fillMaxHeight(),
        contentModifier = Modifier.fillMaxHeight(),
    ) {
        // Sprite showcase; same height for all species so cards align.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ShowcaseHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Box(modifier = Modifier.alpha(if (locked) 0.35f else 1f)) {
                showcase()
            }
            if (buyState is BuyState.LockedByLevel) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = stringResource(
                        R.string.store_locked_level,
                        buyState.requiredLevel,
                    ),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
        }
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            // Reserve two lines: a longer name must not shove this card's
            // button out of line with its neighbour's.
            minLines = 2,
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.weight(1f))
        ReefBuyButton(
            label = if (buyState is BuyState.LockedByLevel) {
                stringResource(R.string.store_locked_short, buyState.requiredLevel)
            } else {
                stringResource(R.string.store_buy)
            },
            price = if (locked) null else price,
            onClick = onBuy,
            enabled = buyState.isAvailable,
            modifier = Modifier.fillMaxWidth(),
        )
        // A disabled button on its own is a dead end; the reason turns it into
        // a goal. The level gate is already spelled out on the button itself.
        BuyBlockerReason(buyState)
    }
}

/**
 * The one-line explanation under an unavailable buy button. Renders nothing
 * when the item is buyable, or when the button label already says why.
 */
@Composable
private fun BuyBlockerReason(buyState: BuyState) {
    val reason = when (buyState) {
        is BuyState.NotEnoughTokens ->
            stringResource(R.string.store_reason_need_more, buyState.missingTokens)

        BuyState.NoSlots -> stringResource(R.string.store_reason_no_room)
        BuyState.Available, is BuyState.LockedByLevel -> null
    } ?: return

    Text(
        text = reason,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

/**
 * The purchasable extra tank (full-width card).
 */
@Composable
private fun TankStoreItem(
    buyState: BuyState,
    onBuy: () -> Unit,
) {
    ReefContentCard(
        padding = ReefSpacing.lg,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ReefSpacing.lg),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.store_item_tank),
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.store_item_tank_description,
                        GameConfig.TANK_CAPACITY_SLOTS,
                        GameConfig.TANK_CAPACITY_SLOTS,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                BuyBlockerReason(buyState)
            }
            ReefBuyButton(
                label = stringResource(R.string.store_buy),
                price = GameConfig.TANK_PRICE_TOKENS,
                onClick = onBuy,
                enabled = buyState.isAvailable,
            )
        }
    }
}

/**
 * "1 slot" / "3 slots", pluralised by the platform.
 *
 * This used to branch on `slots == 1` across two separate string resources,
 * which hard-codes English's two-form rule into the code — Polish needs three
 * forms and Arabic six, and neither could ever be expressed that way.
 */
@Composable
private fun slotsText(slots: Int): String =
    pluralStringResource(R.plurals.store_slots, slots, slots)

/**
 * Small section title between store groups.
 */
@Composable
private fun SectionHeader(text: String) {
    SectionLabel(
        text = text,
        modifier = Modifier.padding(top = ReefSpacing.sm),
    )
}
