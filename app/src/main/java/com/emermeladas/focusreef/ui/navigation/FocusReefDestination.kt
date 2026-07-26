package com.emermeladas.focusreef.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.emermeladas.focusreef.R
import com.emermeladas.focusreef.ui.theme.ReefIcons

/**
 * The three bottom-navigation destinations.
 *
 * Each carries its route, tab label, and the reef glyph pair from
 * [ReefIcons] — outline at rest, filled while selected.
 */
enum class FocusReefDestination(
    val route: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector,
) {
    TANKS("tanks", R.string.tab_tanks, ReefIcons.TanksOutline, ReefIcons.TanksFilled),
    STATS("stats", R.string.tab_stats, ReefIcons.StatsOutline, ReefIcons.StatsFilled),
    STORE("store", R.string.tab_store, ReefIcons.StoreOutline, ReefIcons.StoreFilled),
    ;

    companion object {
        /** The tab the app opens on. */
        val START = TANKS
    }
}
