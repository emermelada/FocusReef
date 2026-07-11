package com.emermeladas.focusreef

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class; its only job is to host the Hilt dependency graph.
 */
@HiltAndroidApp
class FocusReefApplication : Application()
