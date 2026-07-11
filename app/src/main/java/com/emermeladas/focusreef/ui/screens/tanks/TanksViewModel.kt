package com.emermeladas.focusreef.ui.screens.tanks

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.model.FishSpecies
import com.emermeladas.focusreef.data.model.MoveResult
import com.emermeladas.focusreef.data.repositories.AquariumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Exposes the aquarium (tanks + fish) as a UI state stream and handles
 * moving fish between tanks.
 *
 * Move outcomes surface as a one-shot message ([userMessageRes]) shown in a
 * snackbar and acknowledged via [onMessageShown].
 */
@HiltViewModel
class TanksViewModel @Inject constructor(
    private val aquariumRepository: AquariumRepository,
) : ViewModel() {

    val uiState: StateFlow<TanksUiState> = aquariumRepository.observeTanks()
        .map { tanks -> TanksUiState(isLoading = false, tanks = tanks) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TanksUiState(),
        )

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
}
