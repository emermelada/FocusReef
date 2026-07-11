package com.emermeladas.focusreef.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.emermeladas.focusreef.ui.screens.stats.StatsScreen
import com.emermeladas.focusreef.ui.screens.store.StoreScreen
import com.emermeladas.focusreef.ui.screens.tanks.TanksScreen

/**
 * Maps each [FocusReefDestination] route to its screen composable.
 */
@Composable
fun FocusReefNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = FocusReefDestination.START.route,
        modifier = modifier,
    ) {
        composable(FocusReefDestination.TANKS.route) { TanksScreen() }
        composable(FocusReefDestination.STATS.route) { StatsScreen() }
        composable(FocusReefDestination.STORE.route) { StoreScreen() }
    }
}
