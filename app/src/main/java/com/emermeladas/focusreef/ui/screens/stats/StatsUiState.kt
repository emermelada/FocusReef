package com.emermeladas.focusreef.ui.screens.stats

import com.emermeladas.focusreef.data.model.Progression

/**
 * Everything the Stats screen needs to render.
 *
 * @property isLoading True until the focus history arrives.
 * @property summary Precomputed stats; null while loading.
 * @property progression Player level/streak; null while loading.
 */
data class StatsUiState(
    val isLoading: Boolean = true,
    val summary: StatsSummary? = null,
    val progression: Progression? = null,
)
