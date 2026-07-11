package com.emermeladas.focusreef.utils

/**
 * Formats a duration in minutes for display, e.g. "45 min", "3 h 20 min", "12 h".
 */
fun formatMinutes(totalMinutes: Long): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return when {
        hours == 0L -> "$minutes min"
        minutes == 0L -> "$hours h"
        else -> "$hours h $minutes min"
    }
}
