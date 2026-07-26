package com.emermeladas.focusreef.ui.theme

import androidx.annotation.StringRes
import com.emermeladas.focusreef.R

/**
 * How the player wants FocusReef to pick its light/dark appearance.
 *
 * [SYSTEM] is the default and the right answer for almost everyone; the
 * explicit overrides exist because an aquarium is a mood, and some people
 * want the dark reef at noon.
 */
enum class ThemeMode(@param:StringRes val labelRes: Int) {
    /** Follow the device's light/dark setting. */
    SYSTEM(R.string.settings_theme_system),

    /** Always the light reef. */
    LIGHT(R.string.settings_theme_light),

    /** Always the dark reef. */
    DARK(R.string.settings_theme_dark),
    ;

    companion object {
        /**
         * Parses a stored name back to a mode, falling back to [SYSTEM] for
         * anything unrecognised — a preferences file written by a future
         * version must never crash an older one.
         */
        fun fromName(name: String?): ThemeMode =
            entries.firstOrNull { it.name == name } ?: SYSTEM
    }
}
