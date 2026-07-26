package com.emermeladas.focusreef.ui.screens.tanks

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.MoveResult
import com.emermeladas.focusreef.data.repositories.AquariumRepository
import com.emermeladas.focusreef.utils.BiasPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Exposes the aquarium (tanks + fish) as a UI state stream and handles
 * moving fish between tanks.
 *
 * Move outcomes surface as a one-shot message ([userMessageRes]) shown in a
 * snackbar and acknowledged via [onMessageShown].
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TanksViewModel @Inject constructor(
    private val aquariumRepository: AquariumRepository,
) : ViewModel() {

    /** Bumped by [retry]; each new value re-subscribes to the database. */
    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<TanksUiState> = retryTrigger
        .flatMapLatest { aquariumRepository.observeTanks() }
        .map { tanks -> TanksUiState(isLoading = false, tanks = tanks) }
        // Room is local and rarely fails, but when it does the alternative is
        // a skeleton that shimmers forever with no way to recover.
        .catch {
            emit(
                TanksUiState(
                    isLoading = false,
                    errorRes = R.string.error_aquarium_unavailable,
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TanksUiState(),
        )

    /** Re-runs the failed load after the player taps "Try again". */
    fun retry() {
        retryTrigger.update { it + 1 }
    }

    private val _userMessageRes = MutableStateFlow<Int?>(null)

    /** String resource of a pending move-feedback message, or null. */
    @get:StringRes
    val userMessageRes: StateFlow<Int?> = _userMessageRes.asStateFlow()

    /** Moves one fish of [species] from one tank to another. */
    fun moveFish(species: FishSpecies, fromTankId: Long, toTankId: Long) {
        viewModelScope.launch {
            val result = aquariumRepository.moveFish(species, fromTankId, toTankId)
            _userMessageRes.value = when (result) {
                MoveResult.Success -> R.string.tank_msg_moved
                MoveResult.NotEnoughSpace -> R.string.tank_msg_move_no_space
                MoveResult.NothingToMove -> R.string.tank_msg_nothing_to_move
            }
        }
    }

    /** Called by the screen once the message has been displayed. */
    fun onMessageShown() {
        _userMessageRes.value = null
    }

    /** Persists a decoration's new position after placement mode. */
    fun repositionDecoration(decorationId: Long, position: BiasPoint) {
        viewModelScope.launch {
            aquariumRepository.updateDecorationPosition(
                decorationId = decorationId,
                xBias = position.x,
                yBias = position.y,
            )
        }
    }
}
