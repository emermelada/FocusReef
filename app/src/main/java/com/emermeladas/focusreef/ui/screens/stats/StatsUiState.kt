package com.emermeladas.focusreef.ui.screens.stats

import androidx.annotation.StringRes
import com.emermeladas.focusreef.data.model.Progression

/**
 * Everything the Stats screen needs to render.
 *
 * @property isLoading True until the focus history arrives.
 * @property summary Precomputed stats; null while loading.
 * @property progression Player level/streak; null while loading.
 * @property errorRes Why the load failed, or null if it did not. When set,
 *   [isLoading] is false and [summary] is null: the screen must show the
 *   failure rather than an endless skeleton.
 * @property isRefreshing True while a user-initiated refresh is in flight.
 *   Distinct from [isLoading]: a refresh keeps the current data on screen and
 *   shows a spinner over it, rather than replacing it with a skeleton.
 */
data class StatsUiState(
    val isLoading: Boolean = true,
    val summary: StatsSummary? = null,
    val progression: Progression? = null,
    @param:StringRes val errorRes: Int? = null,
    val isRefreshing: Boolean = false,
)
