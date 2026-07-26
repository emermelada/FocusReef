package com.emermeladas.focusreef.ui.screens.tanks

import androidx.annotation.StringRes
import com.emermeladas.focusreef.data.model.Tank

/**
 * Everything the Tanks screen needs to render.
 *
 * @property isLoading True until the first emission from the database.
 * @property tanks The player's tanks with their fish, oldest first.
 * @property errorRes Why the aquarium could not be read, or null. When set,
 *   the screen shows a recoverable failure instead of a skeleton.
 */
data class TanksUiState(
    val isLoading: Boolean = true,
    val tanks: List<Tank> = emptyList(),
    @param:StringRes val errorRes: Int? = null,
)
