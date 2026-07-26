package com.emermeladas.focusreef.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emermeladas.focusreef.ui.navigation.FocusReefDestination
import com.emermeladas.focusreef.ui.theme.ReefMotion

/**
 * Bottom navigation bar with the three app destinations, dressed for the
 * reef: a low surface tone, a primary-tinted selection pill, and icons that
 * surface gently (a small rise + grow) when their tab is chosen.
 *
 * Uses the standard multi-backstack pattern: state of each tab is saved and
 * restored when switching, and reselecting a tab does not stack duplicates.
 */
@Composable
fun FocusReefBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    // A hairline "waterline" along the bar's top edge — the surface of the
    // water the reef glyphs sit under.
    val waterlineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.drawBehind {
            drawLine(
                color = waterlineColor,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx(),
            )
        },
    ) {
        FocusReefDestination.entries.forEach { destination ->
            val selected = currentDestination?.hierarchy
                ?.any { it.route == destination.route } == true

            // The chosen tab's icon floats up toward the surface a touch.
            val lift by animateFloatAsState(
                targetValue = if (selected) 1f else 0f,
                animationSpec = ReefMotion.gentleSpring(),
                label = "tabLift",
            )

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.icon,
                        contentDescription = stringResource(destination.labelRes),
                        modifier = Modifier.graphicsLayer {
                            translationY = -2.dp.toPx() * lift
                            val scale = 1f + 0.08f * lift
                            scaleX = scale
                            scaleY = scale
                        },
                    )
                },
                label = { Text(stringResource(destination.labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        }
    }
}
