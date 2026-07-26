package com.emermeladas.focusreef.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.repositories.FocusHistoryRepository
import com.emermeladas.focusreef.data.repositories.ProgressionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * Turns the raw focus-block history into the aggregated stats the screen
 * shows. All the math lives in [StatsAggregator] and [ProgressionRepository].
 *
 * The history comes from the NAS and can simply be unreachable, so the stream
 * is built to fail visibly: [retry] re-subscribes, and any exception becomes
 * an error state instead of a flow that dies silently and leaves the screen
 * shimmering forever.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatsViewModel @Inject constructor(
    focusHistoryRepository: FocusHistoryRepository,
    progressionRepository: ProgressionRepository,
) : ViewModel() {

    /** Bumped by [refresh]; each new value re-subscribes to the sources. */
    private val retryTrigger = MutableStateFlow(0)

    /** True between a pull-to-refresh and the emission it produces. */
    private val isRefreshing = MutableStateFlow(false)

    val uiState: StateFlow<StatsUiState> = retryTrigger
        .flatMapLatest {
            combine(
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
        }
        .catch {
            emit(
                StatsUiState(
                    isLoading = false,
                    errorRes = R.string.error_history_unavailable,
                ),
            )
        }
        // Whatever the outcome — data or failure — the refresh is over.
        .onEach { isRefreshing.value = false }
        .combine(isRefreshing) { state, refreshing ->
            state.copy(isRefreshing = refreshing)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatsUiState(),
        )

    /**
     * Re-runs the load, whether the player pulled to refresh or tapped
     * "Try again" on the error state — both mean the same thing to the NAS.
     */
    fun refresh() {
        isRefreshing.value = true
        retryTrigger.update { it + 1 }
    }
}
