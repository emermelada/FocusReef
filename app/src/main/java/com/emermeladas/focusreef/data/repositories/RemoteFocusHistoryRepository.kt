package com.emermeladas.focusreef.data.repositories

import com.emermeladas.focusreef.data.model.FocusBlock
import com.emermeladas.focusreef.data.remote.NasApiService
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Real focus-block history read from the NAS REST API.
 *
 * NOT BOUND YET: [com.emermeladas.focusreef.di.RepositoryModule] currently
 * binds [MockFocusHistoryRepository]. Once the NAS API is deployed, swap the
 * binding there and point NetworkModule's base URL at the NAS — nothing else
 * in the app changes.
 */
@Singleton
class RemoteFocusHistoryRepository @Inject constructor(
    private val api: NasApiService,
) : FocusHistoryRepository {

    override fun observeFocusBlocks(): Flow<List<FocusBlock>> = flow {
        emit(api.getFocusBlocks().map { it.toDomain() })
    }
}
