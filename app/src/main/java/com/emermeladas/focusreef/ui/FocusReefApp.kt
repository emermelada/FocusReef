package com.emermeladas.focusreef.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.emermeladas.focusreef.ui.components.FocusReefBottomBar
import com.emermeladas.focusreef.ui.navigation.FocusReefNavHost

/**
 * Root of the UI: a scaffold with the bottom navigation bar and the nav host
 * that swaps between the three screens.
 */
@Composable
fun FocusReefApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { FocusReefBottomBar(navController) },
    ) { innerPadding ->
        FocusReefNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        )
    }
}
