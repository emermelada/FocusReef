package com.emermeladas.focusreef.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.emermeladas.focusreef.ui.components.EarningsMoment
import com.emermeladas.focusreef.ui.components.FocusReefBottomBar
import com.emermeladas.focusreef.ui.navigation.FocusReefDestination
import com.emermeladas.focusreef.ui.navigation.FocusReefNavHost
import com.emermeladas.focusreef.ui.navigation.HOME_ROUTE
import com.emermeladas.focusreef.utils.rememberReducedMotion
import kotlinx.coroutines.launch

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
 *
 * The tab pager's state is owned here because two things read it: the pager
 * itself and the bottom bar. Keeping it at their common parent is what makes a
 * swipe and a tap indistinguishable to the rest of the app.
 */
@Composable
fun FocusReefApp(
    rewardViewModel: RewardViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val newlyEarnedTokens by rewardViewModel.newlyEarnedTokens.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(
        initialPage = FocusReefDestination.START.ordinal,
        pageCount = { FocusReefDestination.entries.size },
    )
    val scope = rememberCoroutineScope()
    val reduceMotion = rememberReducedMotion()

    // Follows the settling page, not the scroll offset, so the bar commits to
    // the new tab as soon as the swipe is decided instead of flickering.
    val currentTab = FocusReefDestination.entries[pagerState.targetPage]

    Scaffold(
        bottomBar = {
            FocusReefBottomBar(
                selected = currentTab,
                onSelect = { destination ->
                    // Changing screen also leaves settings: there is one
                    // settings screen, not one waiting behind each tab.
                    navController.popBackStack(HOME_ROUTE, inclusive = false)
                    scope.launch {
                        if (reduceMotion) {
                            pagerState.scrollToPage(destination.ordinal)
                        } else {
                            pagerState.animateScrollToPage(destination.ordinal)
                        }
                    }
                },
            )
        },
        // NavigationBar already pads itself for the gesture inset, and every
        // screen handles its own status bar, so the scaffold must not reserve
        // system-bar space a second time.
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            FocusReefNavHost(
                navController = navController,
                pagerState = pagerState,
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
