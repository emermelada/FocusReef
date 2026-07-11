package com.emermeladas.focusreef.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.emermeladas.focusreef.R

/**
 * The three top-level destinations shown in the bottom navigation bar.
 *
 * Declaration order is display order.
 */
enum class FocusReefDestination(
    val route: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    TANKS(route = "tanks", labelRes = R.string.tab_tanks, icon = Icons.Filled.Home),
    STATS(route = "stats", labelRes = R.string.tab_stats, icon = Icons.Filled.DateRange),
    STORE(route = "store", labelRes = R.string.tab_store, icon = Icons.Filled.ShoppingCart),
    ;

    companion object {
        /** Where the app starts: the aquarium. */
        val START = TANKS
    }
}
