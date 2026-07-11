package com.emermeladas.focusreef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.emermeladas.focusreef.ui.FocusReefApp
import com.emermeladas.focusreef.ui.theme.FocusReefTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single activity hosting the whole Compose UI.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            FocusReefTheme {
                FocusReefApp()
            }
        }
    }
}
