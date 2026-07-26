package com.emermeladas.focusreef.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.BuildConfig
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.data.local.ReefPreferences
import com.emermeladas.focusreef.data.remote.NasClient
import com.emermeladas.focusreef.data.remote.NasConnectionResult
import com.emermeladas.focusreef.data.repositories.AquariumRepository
import com.emermeladas.focusreef.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Backs the settings screen: the NAS address, appearance preferences, and the
 * one destructive action in the app.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: ReefPreferences,
    private val nasClient: NasClient,
    private val aquariumRepository: AquariumRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(appVersion = BuildConfig.VERSION_NAME),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    /** One-shot confirmations (saved, reset done), shown as a toast. */
    private val _userMessageRes = MutableStateFlow<Int?>(null)
    val userMessageRes: StateFlow<Int?> = _userMessageRes.asStateFlow()

    /** True once the draft has been seeded, so refreshes stop clobbering typing. */
    private var draftInitialised = false

    init {
        viewModelScope.launch {
            combine(
                preferences.nasBaseUrl,
                preferences.themeMode,
                preferences.reduceMotion,
                ::Triple,
            ).collect { (url, theme, reduceMotion) ->
                _uiState.update { state ->
                    state.copy(
                        storedNasUrl = url,
                        // Seed the text field once. Re-seeding on every emission
                        // would overwrite the address the player is typing the
                        // moment any unrelated preference changes.
                        nasUrlDraft = if (draftInitialised) state.nasUrlDraft else url,
                        themeMode = theme,
                        reduceMotion = reduceMotion,
                    )
                }
                draftInitialised = true
            }
        }
    }

    /** Records a keystroke in the NAS address field. */
    fun onNasUrlChanged(value: String) {
        // A stale verdict about a different address is worse than none.
        _uiState.update { it.copy(nasUrlDraft = value, connectionReport = null) }
    }

    /** Persists the typed NAS address. */
    fun saveNasUrl() {
        val url = _uiState.value.nasUrlDraft
        viewModelScope.launch {
            preferences.setNasBaseUrl(url)
            _userMessageRes.value = R.string.settings_nas_saved
        }
    }

    /**
     * Calls the NAS once with the *typed* address, not the saved one, so the
     * player can check an address before committing to it.
     */
    fun testConnection() {
        val url = _uiState.value.nasUrlDraft
        _uiState.update { it.copy(isTestingConnection = true, connectionReport = null) }
        viewModelScope.launch {
            val report = when (val result = nasClient.testConnection(url)) {
                is NasConnectionResult.Success ->
                    ConnectionReport.Reached(result.blockCount)

                NasConnectionResult.NotConfigured ->
                    ConnectionReport.Failed(R.string.settings_nas_test_empty)

                NasConnectionResult.InvalidUrl ->
                    ConnectionReport.Failed(R.string.settings_nas_test_invalid)

                // The exception text is deliberately dropped: "failed to
                // connect to /192.168.1.100:8080" helps nobody who is not
                // already a network engineer.
                is NasConnectionResult.Unreachable ->
                    ConnectionReport.Failed(R.string.settings_nas_test_unreachable)
            }
            _uiState.update { it.copy(isTestingConnection = false, connectionReport = report) }
        }
    }

    /** Sets the light/dark override. */
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferences.setThemeMode(mode) }
    }

    /** Turns the in-app still-reef preference on or off. */
    fun setReduceMotion(enabled: Boolean) {
        viewModelScope.launch { preferences.setReduceMotion(enabled) }
    }

    /**
     * Empties the aquarium and refunds every spent token. The screen confirms
     * with a dialog first — this is the point of no return.
     */
    fun resetAquarium() {
        viewModelScope.launch {
            aquariumRepository.resetAquarium()
            _userMessageRes.value = R.string.settings_reset_done
        }
    }

    /** Clears the toast once it has been shown. */
    fun onMessageShown() {
        _userMessageRes.value = null
    }
}
