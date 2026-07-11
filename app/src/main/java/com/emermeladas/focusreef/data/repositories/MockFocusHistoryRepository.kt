package com.emermeladas.focusreef.data.repositories

import com.emermeladas.focusreef.data.model.FocusBlock
import com.emermeladas.focusreef.utils.GameConfig
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Fake focus-block history used until the NAS REST API is deployed.
 *
 * Generates a deterministic (fixed seed) year of study sessions ending today,
 * so the earned-token balance and the stats screens stay stable across app
 * restarts and look realistic during development.
 */
@Singleton
class MockFocusHistoryRepository @Inject constructor() : FocusHistoryRepository {

    /** Generated once per process; deterministic thanks to the fixed seed. */
    private val blocks: List<FocusBlock> by lazy { generateHistory() }

    override fun observeFocusBlocks(): Flow<List<FocusBlock>> = flowOf(blocks)

    /**
     * Builds ~1 year of history: each day has a 20% chance of being a rest day,
     * otherwise 1–8 blocks starting at 9:00 with a 5-minute break between them.
     */
    private fun generateHistory(): List<FocusBlock> {
        val random = Random(SEED)
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)
        val result = mutableListOf<FocusBlock>()
        var nextId = 1L

        for (daysAgo in HISTORY_DAYS downTo 0) {
            val day = today.minusDays(daysAgo.toLong())
            // Always consume the draw so history before the tail stays identical,
            // then force the last few days active so the streak UI has something
            // to show when demoing.
            val restDayDraw = random.nextInt(100) < REST_DAY_PERCENT
            val isRestDay = restDayDraw && daysAgo > FORCED_ACTIVE_TAIL_DAYS
            if (isRestDay) continue

            val blockCount = 1 + random.nextInt(MAX_BLOCKS_PER_DAY)
            var start = day.atTime(LocalTime.of(9, 0)).atZone(zone)
            repeat(blockCount) {
                result += FocusBlock(
                    id = nextId++,
                    startEpochMillis = start.toInstant().toEpochMilli(),
                    durationMinutes = GameConfig.FOCUS_BLOCK_MINUTES,
                )
                // Next block starts after the focus block plus a short break.
                start = start.plusMinutes((GameConfig.FOCUS_BLOCK_MINUTES + 5).toLong())
            }
        }
        return result
    }

    private companion object {
        const val SEED = 42L
        const val HISTORY_DAYS = 365
        const val REST_DAY_PERCENT = 20
        const val MAX_BLOCKS_PER_DAY = 8

        /** The most recent days are never rest days, so a current streak exists. */
        const val FORCED_ACTIVE_TAIL_DAYS = 5
    }
}
