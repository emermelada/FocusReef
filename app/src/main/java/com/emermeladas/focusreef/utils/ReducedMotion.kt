package com.emermeladas.focusreef.utils

import android.content.Context
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * The player's in-app "still reef" preference, provided at the root of the UI.
 *
 * Defaults to false so any composable used outside the app's root (previews,
 * tests) behaves normally instead of silently freezing.
 */
val LocalReduceMotion = compositionLocalOf { false }

/**
 * True when ambient/decorative animation should be skipped: the user has
 * disabled animations system-wide (animator duration scale = 0, which is what
 * the "remove animations" accessibility toggle sets), battery saver is on, or
 * they have asked for a still reef in FocusReef's own settings.
 *
 * The three are OR-ed, never overridden — a device-wide accessibility setting
 * outranks anything this app offers, so the in-app toggle can only ever add
 * stillness, not take it away.
 *
 * Ambient layers (caustics, bubbles, fish sway) check this and degrade to a
 * calm static scene. Motion is decoration in FocusReef — never the only
 * signal — so nothing is lost functionally.
 *
 * The system half is read once per composition; a mid-session change to the
 * device settings applies the next time the screen is (re)entered, which is
 * plenty for a decorative gate. The in-app half applies immediately.
 */
@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    val systemPrefersStillness = remember {
        val animatorScale = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        )
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        animatorScale == 0f || powerManager?.isPowerSaveMode == true
    }
    return systemPrefersStillness || LocalReduceMotion.current
}
