package com.emermeladas.focusreef.ui.screens.stats

/**
 * Everything the Stats screen needs to render.
 *
 * @property isLoading True until the focus history arrives.
 * @property summary Precomputed stats; null while loading.
 */
data class StatsUiState(
    val isLoading: Boolean = true,
    val summary: StatsSummary? = null,
)
