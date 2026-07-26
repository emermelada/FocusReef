package com.emermeladas.focusreef.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.emermeladas.focusreef.ui.theme.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** The app's single DataStore file for small preferences. */
private val Context.reefDataStore by preferencesDataStore(name = "reef_preferences")

/**
 * Small persistent preferences that are neither game state (Room) nor NAS
 * data: the bookkeeping behind the welcome-back earnings moment, and
 * everything the player can change on the settings screen.
 */
@Singleton
class ReefPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val lastSeenEarnedTokensKey = longPreferencesKey("last_seen_earned_tokens")
    private val nasBaseUrlKey = stringPreferencesKey("nas_base_url")
    private val themeModeKey = stringPreferencesKey("theme_mode")
    private val reduceMotionKey = booleanPreferencesKey("reduce_motion")

    /**
     * The earned-token total the user has already been shown. Null until the
     * first launch stores a baseline — that first read must NOT trigger a
     * celebration for the whole history.
     */
    val lastSeenEarnedTokens: Flow<Long?> =
        context.reefDataStore.data.map { it[lastSeenEarnedTokensKey] }

    /** Records that the user has seen their earnings up to [value] tokens. */
    suspend fun setLastSeenEarnedTokens(value: Long) {
        context.reefDataStore.edit { it[lastSeenEarnedTokensKey] = value }
    }

    /**
     * Base URL of the NAS focus-history API, e.g. `http://192.168.1.20:8080/api/`.
     *
     * This used to be a constant compiled into the app, which meant the one
     * value that is different on every install was the one value the player
     * could not change. Empty means "not configured yet".
     */
    val nasBaseUrl: Flow<String> =
        context.reefDataStore.data.map { it[nasBaseUrlKey] ?: DEFAULT_NAS_BASE_URL }

    /** Stores the NAS base URL, normalised to end in a slash for Retrofit. */
    suspend fun setNasBaseUrl(url: String) {
        val trimmed = url.trim()
        val normalised = when {
            trimmed.isEmpty() -> ""
            trimmed.endsWith("/") -> trimmed
            else -> "$trimmed/"
        }
        context.reefDataStore.edit { it[nasBaseUrlKey] = normalised }
    }

    /** The player's light/dark preference. */
    val themeMode: Flow<ThemeMode> =
        context.reefDataStore.data.map { ThemeMode.fromName(it[themeModeKey]) }

    /** Stores the player's light/dark preference. */
    suspend fun setThemeMode(mode: ThemeMode) {
        context.reefDataStore.edit { it[themeModeKey] = mode.name }
    }

    /**
     * True when the player has asked for a still reef, independently of the
     * system accessibility setting. The two are OR-ed, never overridden: a
     * device-wide "remove animations" always wins.
     */
    val reduceMotion: Flow<Boolean> =
        context.reefDataStore.data.map { it[reduceMotionKey] == true }

    /** Stores the in-app reduced-motion preference. */
    suspend fun setReduceMotion(enabled: Boolean) {
        context.reefDataStore.edit { it[reduceMotionKey] = enabled }
    }

    companion object {
        /**
         * Ships empty rather than guessing a LAN address: a wrong default
         * produces a confusing timeout, an empty one produces an honest
         * "not configured yet" on the settings screen.
         */
        const val DEFAULT_NAS_BASE_URL = ""
    }
}
