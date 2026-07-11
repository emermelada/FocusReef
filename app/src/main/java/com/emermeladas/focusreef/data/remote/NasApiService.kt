package com.emermeladas.focusreef.data.remote

import com.emermeladas.focusreef.data.remote.dto.FocusBlockDto
import retrofit2.http.GET

/**
 * Retrofit definition of the NAS REST API.
 *
 * The NAS exposes the desk's focus-block history read-only; the app never
 * writes to it. This service is not used yet — the app currently runs on
 * [com.emermeladas.focusreef.data.repositories.MockFocusHistoryRepository]
 * until the NAS API is deployed. Swap the binding in
 * [com.emermeladas.focusreef.di.RepositoryModule] when it is.
 */
interface NasApiService {

    /** Returns the full focus-block history recorded by the desk. */
    @GET("focus-blocks")
    suspend fun getFocusBlocks(): List<FocusBlockDto>
}
