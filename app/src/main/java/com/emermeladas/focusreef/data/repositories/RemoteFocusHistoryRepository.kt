package com.emermeladas.focusreef.data.repositories

import com.emermeladas.focusreef.data.model.FocusBlock
import com.emermeladas.focusreef.data.remote.NasClient
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Real focus-block history read from the NAS REST API.
 *
 * NOT BOUND YET: [com.emermeladas.focusreef.di.RepositoryModule] currently
 * binds [MockFocusHistoryRepository]. Once the NAS API is deployed, swap the
 * binding there — the NAS address itself is no longer a code change, it is
 * the field on the settings screen.
 */
@Singleton
class RemoteFocusHistoryRepository @Inject constructor(
    private val nasClient: NasClient,
) : FocusHistoryRepository {

    override fun observeFocusBlocks(): Flow<List<FocusBlock>> = flow {
        emit(nasClient.service().getFocusBlocks().map { it.toDomain() })
    }
}
