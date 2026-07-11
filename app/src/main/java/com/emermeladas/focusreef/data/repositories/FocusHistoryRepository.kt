package com.emermeladas.focusreef.data.repositories

import com.emermeladas.focusreef.data.model.FocusBlock
import kotlinx.coroutines.flow.Flow

/**
 * Read-only access to the focus-block history recorded by the desk.
 *
 * The rest of the app must not know where the history comes from: today it is
 * [MockFocusHistoryRepository] (fake data), later it will be
 * [RemoteFocusHistoryRepository] (NAS REST API). The active implementation is
 * chosen in [com.emermeladas.focusreef.di.RepositoryModule].
 */
interface FocusHistoryRepository {

    /** The full focus-block history, newest data included, as a reactive stream. */
    fun observeFocusBlocks(): Flow<List<FocusBlock>>
}
