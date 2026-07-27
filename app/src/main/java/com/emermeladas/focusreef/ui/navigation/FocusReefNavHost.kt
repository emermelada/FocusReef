package com.emermeladas.focusreef.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.emermeladas.focusreef.ui.screens.settings.SettingsScreen
import com.emermeladas.focusreef.ui.theme.ReefMotion

/** Fraction of the screen width settings slides on its way in — a drift, not a fling. */
private const val SLIDE_FRACTION = 0.08f

/** The three tabs, living together in one swipeable pager. */
const val HOME_ROUTE = "home"

/**
 * Settings route.
 *
 * Deliberately not a [FocusReefDestination]: that enum drives the bottom bar,
 * and settings is a place you visit, not one of the three places you live.
 * It is reached from the gear in each screen's top bar and left with the back
 * arrow, the Back gesture, or by switching tabs.
 */
const val SETTINGS_ROUTE = "settings"

/**
 * Two destinations: the tab pager and settings on top of it.
 *
 * Switching tabs is *not* navigation here — it is a page change inside
 * [HomePager] — which is why the graph is this small. The only real journey in
 * the app is home → settings and back.
 *
 * @param pagerState Drives the tab pager; shared with the bottom bar.
 * @param scaffoldPadding insets the app scaffold consumed (the bottom bar).
 *   Passed down rather than applied here so each screen can bleed its scroll
 *   content under the bar while keeping the last item reachable.
 */
@Composable
fun FocusReefNavHost(
    navController: NavHostController,
    pagerState: PagerState,
    scaffoldPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE,
        modifier = modifier,
        enterTransition = { settingsEnter() },
        exitTransition = { homeExit() },
        popEnterTransition = { homeEnter() },
        popExitTransition = { settingsExit() },
    ) {
        composable(HOME_ROUTE) {
            HomePager(
                pagerState = pagerState,
                scaffoldPadding = scaffoldPadding,
                // launchSingleTop so repeated taps on the gear — from any tab —
                // can never stack settings on top of itself.
                onOpenSettings = {
                    navController.navigate(SETTINGS_ROUTE) { launchSingleTop = true }
                },
            )
        }
        composable(SETTINGS_ROUTE) {
            SettingsScreen(
                outerPadding = scaffoldPadding,
                onClose = { navController.popBackStack() },
            )
        }
    }
}

/** Settings arrives from the trailing edge, the direction Back will send it. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.settingsEnter(): EnterTransition =
    fadeIn(tween(ReefMotion.TAB_TRANSITION_MS, easing = ReefMotion.RevealEasing)) +
        slideInHorizontally(
            animationSpec = tween(ReefMotion.TAB_TRANSITION_MS, easing = ReefMotion.RevealEasing),
            initialOffsetX = { fullWidth -> (fullWidth * SLIDE_FRACTION).toInt() },
        )

/** …and leaves the same way it came. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.settingsExit(): ExitTransition =
    fadeOut(tween(ReefMotion.TAB_TRANSITION_MS)) +
        slideOutHorizontally(
            animationSpec = tween(ReefMotion.TAB_TRANSITION_MS),
            targetOffsetX = { fullWidth -> (fullWidth * SLIDE_FRACTION).toInt() },
        )

/** Home stays put underneath and only dims, so returning feels like uncovering. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.homeEnter(): EnterTransition =
    fadeIn(tween(ReefMotion.TAB_TRANSITION_MS, easing = ReefMotion.RevealEasing))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.homeExit(): ExitTransition =
    fadeOut(tween(ReefMotion.TAB_TRANSITION_MS))
