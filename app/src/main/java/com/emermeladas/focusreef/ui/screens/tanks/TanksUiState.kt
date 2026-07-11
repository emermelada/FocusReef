package com.emermeladas.focusreef.ui.screens.tanks

import com.emermeladas.focusreef.data.model.Tank

/**
 * Everything the Tanks screen needs to render.
 *
 * @property isLoading True until the first emission from the database.
 * @property tanks The player's tanks with their fish, oldest first.
 */
data class TanksUiState(
    val isLoading: Boolean = true,
    val tanks: List<Tank> = emptyList(),
)
