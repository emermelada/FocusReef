package com.emermeladas.focusreef.ui.screens.tanks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.data.repositories.AquariumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Exposes the aquarium (tanks + fish) as a UI state stream.
 *
 * Pure projection of [AquariumRepository]; all mutations happen from the
 * store screen.
 */
@HiltViewModel
class TanksViewModel @Inject constructor(
    aquariumRepository: AquariumRepository,
) : ViewModel() {

    val uiState: StateFlow<TanksUiState> = aquariumRepository.observeTanks()
        .map { tanks -> TanksUiState(isLoading = false, tanks = tanks) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TanksUiState(),
        )
}
