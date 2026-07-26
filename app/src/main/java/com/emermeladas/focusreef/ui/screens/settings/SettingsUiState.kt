package com.emermeladas.focusreef.ui.screens.settings

import androidx.annotation.StringRes
import com.emermeladas.focusreef.ui.theme.ThemeMode

/**
 * Result of the last "test connection", as the settings screen shows it.
 *
 * Success is its own type because its message is a plural keyed on the block
 * count, and the failures are plain strings — squeezing both into one
 * `@StringRes` field would mean the screen guessing which lookup to use.
 */
sealed interface ConnectionReport {
    /** The NAS answered, with this many focus blocks on record. */
    data class Reached(val blockCount: Int) : ConnectionReport

    /** Something went wrong; [messageRes] says what, in the player's terms. */
    data class Failed(@param:StringRes val messageRes: Int) : ConnectionReport
}

/**
 * Everything the settings screen renders.
 *
 * [nasUrlDraft] is deliberately separate from the stored value: the player is
 * mid-typing an address for most of the time this screen is open, and every
 * keystroke must not be persisted and retried against the network.
 */
data class SettingsUiState(
    val nasUrlDraft: String = "",
    val storedNasUrl: String = "",
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val reduceMotion: Boolean = false,
    val isTestingConnection: Boolean = false,
    val connectionReport: ConnectionReport? = null,
    val appVersion: String = "",
) {
    /** True when the draft differs from what is saved. */
    val hasUnsavedUrl: Boolean get() = nasUrlDraft.trim().trimEnd('/') !=
        storedNasUrl.trim().trimEnd('/')
}
