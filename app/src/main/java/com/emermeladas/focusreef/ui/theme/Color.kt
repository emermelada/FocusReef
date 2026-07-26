package com.emermeladas.focusreef.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.emermeladas.focusreef.data.model.DecorationSpecies
import com.emermeladas.focusreef.data.model.FishSpecies

/*
 * FocusReef color system.
 *
 * A complete Material 3 tonal palette seeded from deep ocean blue (#00658E),
 * with a slate-blue secondary, a coral tertiary for warm accents, and full
 * neutral/surface ladders so every component (cards, nav bar, dialogs)
 * renders on deliberate colors — nothing falls back to Material defaults.
 */

// ---- Light scheme -----------------------------------------------------------

val LightPrimary = Color(0xFF00658E)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFC7E7FF)
val LightOnPrimaryContainer = Color(0xFF001E2E)

val LightSecondary = Color(0xFF4F616E)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFD2E5F5)
val LightOnSecondaryContainer = Color(0xFF0B1D29)

val LightTertiary = Color(0xFF9C4332)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFFFDAD3)
val LightOnTertiaryContainer = Color(0xFF3E0500)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

val LightBackground = Color(0xFFF6FAFE)
val LightOnBackground = Color(0xFF181C20)
val LightSurface = Color(0xFFF6FAFE)
val LightOnSurface = Color(0xFF181C20)
val LightSurfaceVariant = Color(0xFFDDE3EA)
val LightOnSurfaceVariant = Color(0xFF41474D)

val LightOutline = Color(0xFF71787E)
val LightOutlineVariant = Color(0xFFC1C7CE)

val LightInverseSurface = Color(0xFF2D3135)
val LightInverseOnSurface = Color(0xFFEEF1F6)
val LightInversePrimary = Color(0xFF85CFFF)

// Light containers keep a faint cool (ocean) tint rather than pure gray, so
// the family reads as water even in daylight.
val LightSurfaceDim = Color(0xFFD3DEE6)
val LightSurfaceBright = Color(0xFFF4FAFE)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFEDF4F9)
val LightSurfaceContainer = Color(0xFFE6EFF5)
val LightSurfaceContainerHigh = Color(0xFFDEEAF1)
val LightSurfaceContainerHighest = Color(0xFFD7E4EC)

// ---- Dark scheme ------------------------------------------------------------

val DarkPrimary = Color(0xFF85CFFF)
val DarkOnPrimary = Color(0xFF00344A)
val DarkPrimaryContainer = Color(0xFF004C69)
val DarkOnPrimaryContainer = Color(0xFFC7E7FF)

val DarkSecondary = Color(0xFFB6C9D8)
val DarkOnSecondary = Color(0xFF21323E)
val DarkSecondaryContainer = Color(0xFF374955)
val DarkOnSecondaryContainer = Color(0xFFD2E5F5)

val DarkTertiary = Color(0xFFFFB4A4)
val DarkOnTertiary = Color(0xFF5F160A)
val DarkTertiaryContainer = Color(0xFF7D2C1D)
val DarkOnTertiaryContainer = Color(0xFFFFDAD3)

val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

// The dark neutrals are deliberately NOT neutral: every surface step carries
// a deep desaturated teal/navy undertone (B > G > R) so panels read as
// lighter panes of the same water, never as stock Material gray.
val DarkBackground = Color(0xFF081319)
val DarkOnBackground = Color(0xFFDCE7EE)
val DarkSurface = Color(0xFF081319)
val DarkOnSurface = Color(0xFFDCE7EE)
val DarkSurfaceVariant = Color(0xFF334955)
val DarkOnSurfaceVariant = Color(0xFFA6BCC9)

val DarkOutline = Color(0xFF6E8794)
val DarkOutlineVariant = Color(0xFF31454F)

val DarkInverseSurface = Color(0xFFDCE7EE)
val DarkInverseOnSurface = Color(0xFF15272F)
val DarkInversePrimary = Color(0xFF00658E)

val DarkSurfaceDim = Color(0xFF081319)
val DarkSurfaceBright = Color(0xFF274451)
val DarkSurfaceContainerLowest = Color(0xFF040D12)
val DarkSurfaceContainerLow = Color(0xFF0F2029)
val DarkSurfaceContainer = Color(0xFF132833)
val DarkSurfaceContainerHigh = Color(0xFF1B3542)
val DarkSurfaceContainerHighest = Color(0xFF244251)

// ---- Tank water & sand (used by TankSprite, not part of the Material scheme)

/**
 * The colors that make one theme's water: three vertical gradient stops plus
 * the sand floor. Light theme reads as a sunlit lagoon; dark theme as the
 * same reef after sundown — deeper and moodier, not merely inverted.
 */
data class TankPalette(
    /** Water at the surface (top gradient stop). */
    val surface: Color,
    /** Mid-depth water (middle gradient stop). */
    val mid: Color,
    /** Deep water at the floor (bottom gradient stop). */
    val deep: Color,
    /** Sand floor base color. */
    val sand: Color,
)

/** Daylight water: bright surface falling to a clear deep blue. */
val LightTankPalette = TankPalette(
    surface = Color(0xFF56BEEF),
    mid = Color(0xFF1E7CB0),
    deep = Color(0xFF0A4568),
    sand = Color(0xFFE3CE97),
)

/** Night water: the deep-ocean mood — dimmer surface, near-black depths. */
val DarkTankPalette = TankPalette(
    surface = Color(0xFF2E7FB2),
    mid = Color(0xFF104A70),
    deep = Color(0xFF03202F),
    sand = Color(0xFFC2A975),
)

/** The [TankPalette] for the current theme. */
@Composable
fun tankPalette(): TankPalette =
    if (isSystemInDarkTheme()) DarkTankPalette else LightTankPalette

/**
 * Dialog scrim: deep water instead of Material's flat black, so even the
 * backdrop behind a dialog belongs to the reef. Applied with alpha at the
 * call site; the blue-black base works over both themes.
 */
val ReefScrim = Color(0xFF03202F)

// ---- Accent, edges, and depth (used by ReefSurface / the button system) ----

/**
 * The one saturated call-to-action color — a bright aqua that pops against
 * the teal surfaces. Reserved for the single PRIMARY action on a surface
 * (Buy, confirm); never used as a fill for passive chrome.
 */
val ReefAccent = Color(0xFF2FD6C4)

/** High-contrast content drawn on top of [ReefAccent]. */
val OnReefAccent = Color(0xFF00382F)

/** Top edge of an elevated pane's hairline border (light catches the rim). */
val HairlineTop = Color(0x2EFFFFFF)

/** Bottom edge of the hairline border (fades toward the shadow). */
val HairlineBottom = Color(0x0FFFFFFF)

/** Tinted ambient shadow beneath elevated panes — deep water, not black. */
val ReefShadowColor = Color(0xFF02141D)

// ---- Token coin (used by TokenIcon) -----------------------------------------

/** Coin face. */
val TokenGold = Color(0xFFE6A817)

/** Coin rim/border. */
val TokenGoldDark = Color(0xFFB07B0A)

/** Coin inner highlight. */
val TokenGoldLight = Color(0xFFF7CC55)

// ---- Placeholder fish colors ------------------------------------------------

/**
 * Placeholder body color per species until real sprites exist.
 *
 * Species are never identified by color alone — the sprite size already
 * encodes the species, so these hues are just flavor.
 */
/** Placeholder body color for each decoration until real sprites arrive. */
fun decorationColorFor(species: DecorationSpecies): Color = when (species) {
    DecorationSpecies.SHELL -> Color(0xFFEFB8C3)          // pink shell
    DecorationSpecies.ROCK -> Color(0xFF8B8E93)           // gray rock
    DecorationSpecies.KELP -> Color(0xFF3E8E4E)           // kelp green
    DecorationSpecies.BUBBLER -> Color(0xCCFFFFFF)        // white-alpha bubbles
    DecorationSpecies.TREASURE_CHEST -> Color(0xFF8A5A2B) // chest brown
    DecorationSpecies.JELLYFISH -> Color(0xFFB9A7E8)      // lavender jelly
}

fun fishColorFor(species: FishSpecies): Color = when (species) {
    FishSpecies.SMALL -> Color(0xFFF28C28)     // clownfish orange
    FishSpecies.MEDIUM -> Color(0xFF2E9E6B)    // reef green
    FishSpecies.LARGE -> Color(0xFF7B68CD)     // deep-sea violet
    FishSpecies.NEON -> Color(0xFF35D0EE)      // electric cyan
    FishSpecies.ANGELFISH -> Color(0xFFF2C230) // golden yellow
    FishSpecies.BETTA -> Color(0xFFD64562)     // ruby
    FishSpecies.SHARK -> Color(0xFF7E93A6)     // steel gray
    FishSpecies.LIONFISH -> Color(0xFFC7502F)  // burnt coral
    FishSpecies.ORCA -> Color(0xFF2E3A46)      // deep slate
}
