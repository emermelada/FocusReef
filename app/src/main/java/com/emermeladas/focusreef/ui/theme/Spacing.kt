package com.emermeladas.focusreef.ui.theme

import androidx.compose.ui.unit.dp

/**
 * The spacing scale.
 *
 * The theme already centralizes color, type and shape; without a space scale
 * screens improvise (10dp here, 14dp there, 18dp somewhere else) and the
 * rhythm of the app quietly falls apart. Every gap, pad and inset in
 * `ui/screens/` comes from here.
 *
 * The scale is a 4dp grid with the steps that actually earn their place —
 * resist adding "just one more" value between two of these; if a layout needs
 * something in between, it usually needs a different step, not a new one.
 */
object ReefSpacing {

    /** 4dp — hairline gaps: an icon from its label, a caption from its value. */
    val xs = 4.dp

    /** 8dp — inside small controls, between tightly related lines of text. */
    val sm = 8.dp

    /** 12dp — inside compact cards, between items in a dense grid. */
    val md = 12.dp

    /** 16dp — the default: screen margins, card padding, list item spacing. */
    val lg = 16.dp

    /** 24dp — inside hero surfaces, between distinct groups of content. */
    val xl = 24.dp

    /** 32dp — between major sections that should read as separate. */
    val xxl = 32.dp
}
