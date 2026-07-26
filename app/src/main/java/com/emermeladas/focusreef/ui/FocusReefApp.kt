package com.emermeladas.focusreef.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.emermeladas.focusreef.ui.components.EarningsMoment
import com.emermeladas.focusreef.ui.components.FocusReefBottomBar
import com.emermeladas.focusreef.ui.navigation.FocusReefNavHost

/**
 * Root of the UI: a scaffold with the bottom navigation bar and the nav host
 * that swaps between the screens. The welcome-back earnings moment floats
 * above whichever screen is active.
 *
 * The scaffold's inner padding is deliberately **not** applied to the nav host.
 * It is handed to each screen, which spends it as `contentPadding` on its own
 * scrolling list so content scrolls *under* the navigation bar instead of
 * being cut off in a hard line above it. The top inset is left alone entirely:
 * each screen's own `ReefTopBar` consumes the status bar, which is what lets
 * the bar pin while content passes beneath it.
 */
@Composable
fun FocusReefApp(
    rewardViewModel: RewardViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val newlyEarnedTokens by rewardViewModel.newlyEarnedTokens.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { FocusReefBottomBar(navController) },
        // NavigationBar already pads itself for the gesture inset, and every
        // screen handles its own status bar, so the scaffold must not reserve
        // system-bar space a second time.
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            FocusReefNavHost(
                navController = navController,
                // Only the bottom matters: it is the height of the nav bar.
                scaffoldPadding = PaddingValues(
                    bottom = innerPadding.calculateBottomPadding(),
                ),
                modifier = Modifier.fillMaxSize(),
            )
            // The earnings moment is chrome, not content: it sits below the
            // status bar rather than scrolling with whichever screen is up.
            EarningsMoment(
                newTokens = newlyEarnedTokens,
                onDone = rewardViewModel::onEarningsMomentDone,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.statusBars),
            )
        }
    }
}
