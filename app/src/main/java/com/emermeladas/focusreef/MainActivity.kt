package com.emermeladas.focusreef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emermeladas.focusreef.ui.AppearanceViewModel
import com.emermeladas.focusreef.ui.FocusReefApp
import com.emermeladas.focusreef.ui.theme.FocusReefTheme
import com.emermeladas.focusreef.ui.theme.ThemeMode
import com.emermeladas.focusreef.utils.LocalReduceMotion
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single activity hosting the whole Compose UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val appearanceViewModel: AppearanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate so the launch theme hands over to
        // Theme.FocusReef instead of the splash theme sticking around.
        val splash = installSplashScreen()
        // Hold the splash for the one disk read that decides light or dark.
        // Without this the app draws a system-themed frame and then snaps to
        // the player's override — a flash on every single launch.
        splash.setKeepOnScreenCondition { !appearanceViewModel.state.value.isReady }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val appearance by appearanceViewModel.state.collectAsStateWithLifecycle()
            val darkTheme = when (appearance.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                // SYSTEM, and the brief pre-load null: follow the device.
                ThemeMode.SYSTEM, null -> isSystemInDarkTheme()
            }
            FocusReefTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(LocalReduceMotion provides appearance.reduceMotion) {
                    FocusReefApp()
                }
            }
        }
    }
}
