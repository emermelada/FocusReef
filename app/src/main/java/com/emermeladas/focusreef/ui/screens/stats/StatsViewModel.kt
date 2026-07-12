package com.emermeladas.focusreef.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.data.repositories.FocusHistoryRepository
import com.emermeladas.focusreef.data.repositories.ProgressionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Turns the raw focus-block history into the aggregated stats the screen
 * shows. All the math lives in [StatsAggregator] and [ProgressionRepository].
 */
@HiltViewModel
class StatsViewModel @Inject constructor(
    focusHistoryRepository: FocusHistoryRepository,
    progressionRepository: ProgressionRepository,
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = combine(
        focusHistoryRepository.observeFocusBlocks(),
        progressionRepository.observeProgression(),
    ) { blocks, progression ->
        StatsUiState(
            isLoading = false,
            summary = StatsAggregator.aggregate(
                blocks = blocks,
                today = LocalDate.now(ZoneId.systemDefault()),
                zone = ZoneId.systemDefault(),
            ),
            progression = progression,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsUiState(),
        )
}
