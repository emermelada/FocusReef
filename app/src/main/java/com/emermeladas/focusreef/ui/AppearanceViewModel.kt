package com.emermeladas.focusreef.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emermeladas.focusreef.data.local.ReefPreferences
import com.emermeladas.focusreef.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * How the app should look, resolved before the first frame.
 *
 * @param themeMode null until the stored preference has been read. The
 *   splash screen is held for that first read, so the app never renders a
 *   light frame and then snaps to dark.
 */
data class AppearanceState(
    val themeMode: ThemeMode? = null,
    val reduceMotion: Boolean = false,
) {
    /** True once the stored preferences are known and the UI can be drawn. */
    val isReady: Boolean get() = themeMode != null
}

/**
 * Root-level appearance preferences (theme override, reduced motion).
 *
 * Owned by [com.emermeladas.focusreef.MainActivity] rather than by the
 * settings screen: these decide how *everything* is drawn, so they must be
 * known before the first composition and must survive leaving settings.
 */
@HiltViewModel
class AppearanceViewModel @Inject constructor(
    preferences: ReefPreferences,
) : ViewModel() {

    val state: StateFlow<AppearanceState> =
        combine(preferences.themeMode, preferences.reduceMotion) { mode, reduceMotion ->
            AppearanceState(themeMode = mode, reduceMotion = reduceMotion)
        }.stateIn(
            scope = viewModelScope,
            // Eagerly: the splash screen is waiting on this value, and there
            // is no subscriber yet at the moment it starts waiting.
            started = SharingStarted.Eagerly,
            initialValue = AppearanceState(),
        )
}
