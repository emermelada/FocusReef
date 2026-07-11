package com.emermeladas.focusreef.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.data.repositories.FocusHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Turns the raw focus-block history into the aggregated stats the screen
 * shows. All the math lives in [StatsAggregator].
 */
@HiltViewModel
class StatsViewModel @Inject constructor(
    focusHistoryRepository: FocusHistoryRepository,
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = focusHistoryRepository.observeFocusBlocks()
        .map { blocks ->
            StatsUiState(
                isLoading = false,
                summary = StatsAggregator.aggregate(
                    blocks = blocks,
                    today = LocalDate.now(ZoneId.systemDefault()),
                    zone = ZoneId.systemDefault(),
                ),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsUiState(),
        )
}
