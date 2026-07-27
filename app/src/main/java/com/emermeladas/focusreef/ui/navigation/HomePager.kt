package com.emermeladas.focusreef.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.emermeladas.focusreef.ui.screens.stats.StatsScreen
import com.emermeladas.focusreef.ui.screens.store.StoreScreen
import com.emermeladas.focusreef.ui.screens.tanks.TanksScreen

/**
 * The three bottom-navigation destinations laid side by side in a pager, so
 * they can be reached by swiping as well as by tapping the bar.
 *
 * Making the tabs *pages* rather than separate nav routes is what lets a drag
 * follow the finger: the neighbouring screen is already composed and moving,
 * instead of being swapped in after the gesture ends. The bottom bar and the
 * pager share one [pagerState], so tapping and swiping are the same action
 * expressed two ways and can never disagree about which tab is current.
 *
 * @param pagerState Shared with the bottom bar; owned by the app scaffold.
 * @param scaffoldPadding Insets the app scaffold consumed (the bottom bar),
 *   passed down so each screen can scroll content under it.
 * @param onOpenSettings Invoked by each screen's top-bar gear.
 */
@Composable
fun HomePager(
    pagerState: PagerState,
    scaffoldPadding: PaddingValues,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        // Keep the neighbours composed so a swipe reveals a finished screen
        // rather than a skeleton that fills in mid-gesture.
        beyondViewportPageCount = 1,
    ) { page ->
        when (FocusReefDestination.entries[page]) {
            FocusReefDestination.TANKS ->
                TanksScreen(outerPadding = scaffoldPadding, onOpenSettings = onOpenSettings)

            FocusReefDestination.STATS ->
                StatsScreen(outerPadding = scaffoldPadding, onOpenSettings = onOpenSettings)

            FocusReefDestination.STORE ->
                StoreScreen(outerPadding = scaffoldPadding, onOpenSettings = onOpenSettings)
        }
    }
}
