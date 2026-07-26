package com.emermeladas.focusreef.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.Locale

/**
 * The app's signature section label — small, UPPERCASE, letter-spaced, in a
 * confident secondary tint. Used for every group heading ("Fish",
 * "Decorations", "Your balance") so headings read as a deliberate system
 * rather than default body text. Uppercasing is presentational; the source
 * string stays normal-case for accessibility/localisation.
 */
@Composable
fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.uppercase(Locale.getDefault()),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier,
    )
}
