package com.emermeladas.focusreef.ui.screens.tanks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.Decoration
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.Tank
import com.emermeladas.focusreef.ui.components.DecorationPlacementOverlay
import com.emermeladas.focusreef.ui.components.ReefContentCard
import com.emermeladas.focusreef.ui.components.ReefErrorState
import com.emermeladas.focusreef.ui.components.ReefScreenScaffold
import com.emermeladas.focusreef.ui.components.ReefToast
import com.emermeladas.focusreef.ui.components.SettingsAction
import com.emermeladas.focusreef.ui.components.pressable
import com.emermeladas.focusreef.ui.components.ShimmerSkeleton
import com.emermeladas.focusreef.ui.components.TankDetailDialog
import com.emermeladas.focusreef.ui.components.TankPickerDialog
import com.emermeladas.focusreef.ui.components.TankSprite
import com.emermeladas.focusreef.ui.theme.ReefSpacing
import com.emermeladas.focusreef.utils.BiasPoint
import com.emermeladas.focusreef.utils.GameConfig

/**
 * Width-to-height ratio of a tank pane.
 *
 * The tank used to be a fixed 210dp tall, which meant it was a letterbox on a
 * tablet and nearly square on a small phone — the fish looked like they were
 * swimming in a different aquarium on every device. Deriving the height from
 * the width keeps the water the same shape everywhere.
 */
private const val TANK_ASPECT_RATIO = 16f / 10f

/**
 * Window 1 — the aquarium: every owned tank with its fish and slot usage.
 *
 * Tapping a tank opens [TankDetailDialog] with per-species counts and the
 * option to move fish to another tank.
 *
 * @param outerPadding insets from the app scaffold; spent as content padding
 *   so the list scrolls under the navigation bar rather than stopping at it.
 * @param onOpenSettings opens the settings destination from the top bar.
 */
@Composable
fun TanksScreen(
    outerPadding: PaddingValues,
    onOpenSettings: () -> Unit,
    viewModel: TanksViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messageRes by viewModel.userMessageRes.collectAsStateWithLifecycle()

    // Dialog state is pure UI state, so it lives here, not in the ViewModel.
    var selectedTankId by remember { mutableStateOf<Long?>(null) }
    var speciesToMove by remember { mutableStateOf<FishSpecies?>(null) }
    var placingDecoration by remember { mutableStateOf<Decoration?>(null) }

    ReefScreenScaffold(
        title = stringResource(R.string.tanks_title),
        outerPadding = outerPadding,
        actions = { SettingsAction(onClick = onOpenSettings) },
    ) { contentPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // One aquarium per row on a phone, two once the window is wide
            // enough (tablet, or a phone on its side) — a lone tank stretched
            // across a landscape screen leaves the fish stranded mid-ocean.
            LazyVerticalGrid(
                columns = GridCells.Fixed(integerResource(R.integer.tank_grid_columns)),
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(ReefSpacing.lg),
                horizontalArrangement = Arrangement.spacedBy(ReefSpacing.lg),
            ) {
                // Messages about the whole screen span it, whatever the width.
                val fullWidth: LazyGridItemSpanScope.() -> GridItemSpan =
                    { GridItemSpan(maxLineSpan) }

                val errorRes = uiState.errorRes
                if (errorRes != null) {
                    item(span = fullWidth) {
                        ReefErrorState(messageRes = errorRes, onRetry = viewModel::retry)
                    }
                } else if (uiState.isLoading && uiState.tanks.isEmpty()) {
                    // A tank-shaped shimmer while the aquarium loads.
                    item {
                        ShimmerSkeleton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(TANK_ASPECT_RATIO),
                            shape = RoundedCornerShape(20.dp),
                        )
                    }
                }
                items(uiState.tanks, key = { it.id }) { tank ->
                    TankCard(tank = tank, onClick = { selectedTankId = tank.id })
                }
                // Gentle onboarding hint while the aquarium is still empty.
                // Not when the load failed: an empty reef the player never
                // built should not be presented as a first-run milestone.
                if (errorRes == null &&
                    !uiState.isLoading &&
                    uiState.tanks.all { it.fish.isEmpty() }
                ) {
                    item(span = fullWidth) {
                        EmptyReefHint()
                    }
                }
            }

            ReefToast(
                messageRes = messageRes,
                onShown = viewModel::onMessageShown,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = outerPadding.calculateBottomPadding()),
            )
        }
    }

    // Detail dialog for the tapped tank (fresh data straight from uiState).
    val selectedTank = uiState.tanks.firstOrNull { it.id == selectedTankId }
    val movingSpecies = speciesToMove

    if (selectedTank != null && movingSpecies == null && placingDecoration == null) {
        TankDetailDialog(
            tank = selectedTank,
            canMoveSpecies = { species ->
                uiState.tanks.any { it.id != selectedTank.id && it.hasRoomFor(species) }
            },
            onMoveSpecies = { speciesToMove = it },
            onRepositionDecoration = { placingDecoration = it },
            onDismiss = { selectedTankId = null },
        )
    }

    // Destination picker once a species has been chosen to move.
    if (selectedTank != null && movingSpecies != null) {
        TankPickerDialog(
            title = stringResource(R.string.move_fish_title),
            tanks = uiState.tanks.filter { it.id != selectedTank.id },
            tankEnabled = { it.hasRoomFor(movingSpecies) },
            supportingText = {
                pluralStringResource(R.plurals.tank_free_slots, it.freeSlots, it.freeSlots)
            },
            onPick = { destination ->
                viewModel.moveFish(movingSpecies, selectedTank.id, destination.id)
                speciesToMove = null
            },
            onDismiss = { speciesToMove = null },
        )
    }

    // Placement mode to reposition an already-owned decoration. Cancel keeps
    // the saved position (nothing was written yet).
    placingDecoration?.let { decoration ->
        val tank = uiState.tanks.firstOrNull { it.id == decoration.tankId } ?: return@let
        DecorationPlacementOverlay(
            // Hide the decoration being moved or it would render twice.
            tank = tank.copy(
                decorations = tank.decorations.filterNot { it.id == decoration.id },
            ),
            species = decoration.species,
            initialPosition = BiasPoint(decoration.xBias, decoration.yBias),
            onConfirm = { position ->
                viewModel.repositionDecoration(decoration.id, position)
                placingDecoration = null
            },
            onCancel = { placingDecoration = null },
        )
    }
}

/**
 * Empty-aquarium state.
 *
 * This is the only surface that explains the game's economy, so it says it
 * outright: focus blocks become tokens, tokens become fish. A new player who
 * only sees "your reef is empty" has been told a fact, not given a next step.
 */
@Composable
private fun EmptyReefHint() {
    ReefContentCard(
        padding = ReefSpacing.xl,
        spacing = ReefSpacing.sm,
        modifier = Modifier.fillMaxWidth(),
        contentModifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(ReefSpacing.xs),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            listOf(6.dp, 10.dp, 7.dp).forEachIndexed { index, bubbleSize ->
                Box(
                    modifier = Modifier
                        .padding(top = if (index == 1) 0.dp else 6.dp)
                        .size(bubbleSize)
                        .clip(CircleShape)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.25f + index * 0.15f,
                            ),
                        ),
                )
            }
        }
        Spacer(modifier = Modifier.height(ReefSpacing.xs))
        Text(
            text = stringResource(R.string.tanks_empty_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = stringResource(
                R.string.tanks_empty_body,
                GameConfig.FOCUS_BLOCK_MINUTES,
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/**
 * One tank: header row (name + slot usage) above its sprite. Tapping
 * anywhere opens the tank's detail dialog.
 */
@Composable
private fun TankCard(tank: Tank, onClick: () -> Unit) {
    // Shared press state: the whole card dips slightly while held (no
    // ripple — a wash of ink over the water would break the illusion).
    val interactionSource = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .pressable(interactionSource)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                // Without a label TalkBack offers a bare "double tap to
                // activate" on a card whose only affordance is visual.
                onClickLabel = stringResource(R.string.tank_open_details),
                onClick = onClick,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ReefSpacing.sm),
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
                .aspectRatio(TANK_ASPECT_RATIO),
        )
    }
}
