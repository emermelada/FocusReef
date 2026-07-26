package com.emermeladas.focusreef.data.remote

import com.emermeladas.focusreef.data.local.ReefPreferences
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Outcome of asking the NAS whether it is there. */
sealed interface NasConnectionResult {
    /** The API answered, with this many focus blocks on record. */
    data class Success(val blockCount: Int) : NasConnectionResult

    /** No address has been entered yet. */
    data object NotConfigured : NasConnectionResult

    /** The address is not a URL Retrofit can use. */
    data object InvalidUrl : NasConnectionResult

    /** The address is well-formed but nothing usable answered. */
    data class Unreachable(val detail: String) : NasConnectionResult
}

/**
 * Builds the NAS API client from the address the player configured.
 *
 * The base URL used to be a compile-time constant, so the one value that
 * differs on every install was the one value nobody could change. It now
 * lives in [ReefPreferences], which means the Retrofit instance cannot be a
 * plain singleton — it is cached here and rebuilt whenever the stored URL
 * changes.
 */
@Singleton
class NasClient @Inject constructor(
    private val preferences: ReefPreferences,
) {

    private var cachedUrl: String? = null
    private var cachedService: NasApiService? = null

    /**
     * The API client for the currently configured address.
     *
     * @throws IllegalStateException when no address has been configured, so a
     *   missing setting surfaces as a normal repository failure (and therefore
     *   as the app's error state) rather than as a silent empty history.
     */
    suspend fun service(): NasApiService {
        val url = preferences.nasBaseUrl.first()
        check(url.isNotEmpty()) { "No NAS address configured" }
        return serviceFor(url)
    }

    /**
     * Calls the NAS once and reports what happened, for the settings screen's
     * "test connection" button. Never throws: every failure is a result.
     */
    suspend fun testConnection(url: String): NasConnectionResult {
        val normalised = url.trim().let { if (it.endsWith("/")) it else "$it/" }
        if (url.isBlank()) return NasConnectionResult.NotConfigured
        val service = try {
            serviceFor(normalised)
        } catch (e: IllegalArgumentException) {
            // Retrofit rejects a malformed base URL at build time.
            return NasConnectionResult.InvalidUrl
        }
        return try {
            NasConnectionResult.Success(service.getFocusBlocks().size)
        } catch (e: Exception) {
            // Anything from DNS failure to a 500 to malformed JSON: from the
            // player's side these are all "the desk didn't answer".
            NasConnectionResult.Unreachable(e.message ?: e.javaClass.simpleName)
        }
    }

    /** Returns the cached client for [url], building it on first use. */
    @Synchronized
    private fun serviceFor(url: String): NasApiService {
        cachedService?.takeIf { cachedUrl == url }?.let { return it }
        val service = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NasApiService::class.java)
        cachedUrl = url
        cachedService = service
        return service
    }
}
