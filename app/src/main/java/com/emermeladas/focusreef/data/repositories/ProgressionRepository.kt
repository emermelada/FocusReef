package com.emermeladas.focusreef.data.repositories

import com.emermeladas.focusreef.data.model.Progression
import com.emermeladas.focusreef.data.model.ProgressionCalculator
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Exposes the player's XP/streak/level, derived on the fly from the
 * focus-block history. Like the wallet, progression is never stored.
 */
interface ProgressionRepository {

    /** The current progression as a reactive stream; updates with the history. */
    fun observeProgression(): Flow<Progression>
}

/**
 * Default [ProgressionRepository]: maps the focus history through
 * [ProgressionCalculator] using the device clock and timezone.
 */
@Singleton
class ProgressionRepositoryImpl @Inject constructor(
    private val focusHistoryRepository: FocusHistoryRepository,
) : ProgressionRepository {

    override fun observeProgression(): Flow<Progression> =
        focusHistoryRepository.observeFocusBlocks().map { blocks ->
            val zone = ZoneId.systemDefault()
            ProgressionCalculator.calculate(blocks, LocalDate.now(zone), zone)
        }
}
