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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.emermeladas.focusreef.ui.screens.settings.SettingsScreen
import com.emermeladas.focusreef.ui.screens.stats.StatsScreen
import com.emermeladas.focusreef.ui.screens.store.StoreScreen
import com.emermeladas.focusreef.ui.screens.tanks.TanksScreen
import com.emermeladas.focusreef.ui.theme.ReefMotion

/** Fraction of the screen width a tab slides during the switch — a drift, not a fling. */
private const val SLIDE_FRACTION = 0.08f

/**
 * Settings route.
 *
 * Deliberately not a [FocusReefDestination]: that enum drives the bottom bar,
 * and settings is a place you visit, not one of the three places you live.
 * It is reached from the gear in each screen's top bar and left with Back.
 */
const val SETTINGS_ROUTE = "settings"

/** Tab order of a route, for deciding which way the switch should slide. */
private fun tabIndexOf(entry: NavBackStackEntry?): Int =
    FocusReefDestination.entries.indexOfFirst { it.route == entry?.destination?.route }

/**
 * Maps each [FocusReefDestination] route to its screen composable.
 *
 * Tab switches crossfade with a slight horizontal drift toward the tapped
 * tab (moving right in the bar slides content leftward, and vice versa), so
 * navigation feels like panning across one space rather than swapping pages.
 *
 * @param scaffoldPadding insets the app scaffold consumed (the bottom bar).
 *   Passed down rather than applied here so each screen can bleed its scroll
 *   content under the bar while keeping the last item reachable.
 */
@Composable
fun FocusReefNavHost(
    navController: NavHostController,
    scaffoldPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = FocusReefDestination.START.route,
        modifier = modifier,
        enterTransition = { reefEnter() },
        exitTransition = { reefExit() },
        popEnterTransition = { reefEnter() },
        popExitTransition = { reefExit() },
    ) {
        val openSettings = { navController.navigate(SETTINGS_ROUTE) }
        composable(FocusReefDestination.TANKS.route) {
            TanksScreen(outerPadding = scaffoldPadding, onOpenSettings = openSettings)
        }
        composable(FocusReefDestination.STATS.route) {
            StatsScreen(outerPadding = scaffoldPadding, onOpenSettings = openSettings)
        }
        composable(FocusReefDestination.STORE.route) {
            StoreScreen(outerPadding = scaffoldPadding, onOpenSettings = openSettings)
        }
        composable(SETTINGS_ROUTE) {
            SettingsScreen(outerPadding = scaffoldPadding)
        }
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.reefEnter(): EnterTransition {
    val towardEnd = tabIndexOf(targetState) >= tabIndexOf(initialState)
    return fadeIn(tween(ReefMotion.TAB_TRANSITION_MS, easing = ReefMotion.RevealEasing)) +
        slideInHorizontally(
            animationSpec = tween(ReefMotion.TAB_TRANSITION_MS, easing = ReefMotion.RevealEasing),
            initialOffsetX = { fullWidth ->
                val offset = (fullWidth * SLIDE_FRACTION).toInt()
                if (towardEnd) offset else -offset
            },
        )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.reefExit(): ExitTransition {
    val towardEnd = tabIndexOf(targetState) >= tabIndexOf(initialState)
    return fadeOut(tween(ReefMotion.TAB_TRANSITION_MS)) +
        slideOutHorizontally(
            animationSpec = tween(ReefMotion.TAB_TRANSITION_MS),
            targetOffsetX = { fullWidth ->
                val offset = (fullWidth * SLIDE_FRACTION).toInt()
                if (towardEnd) -offset else offset
            },
        )
}
