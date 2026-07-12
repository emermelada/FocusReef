package com.emermeladas.focusreef.ui.theme

import androidx.compose.ui.graphics.Color
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

val LightSurfaceDim = Color(0xFFD7DADF)
val LightSurfaceBright = Color(0xFFF6FAFE)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF0F4F9)
val LightSurfaceContainer = Color(0xFFEAEEF3)
val LightSurfaceContainerHigh = Color(0xFFE4E9EE)
val LightSurfaceContainerHighest = Color(0xFFDFE3E8)

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

val DarkBackground = Color(0xFF101417)
val DarkOnBackground = Color(0xFFDFE3E7)
val DarkSurface = Color(0xFF101417)
val DarkOnSurface = Color(0xFFDFE3E7)
val DarkSurfaceVariant = Color(0xFF41474D)
val DarkOnSurfaceVariant = Color(0xFFC1C7CE)

val DarkOutline = Color(0xFF8B9198)
val DarkOutlineVariant = Color(0xFF41474D)

val DarkInverseSurface = Color(0xFFDFE3E7)
val DarkInverseOnSurface = Color(0xFF2D3135)
val DarkInversePrimary = Color(0xFF00658E)

val DarkSurfaceDim = Color(0xFF101417)
val DarkSurfaceBright = Color(0xFF363A3E)
val DarkSurfaceContainerLowest = Color(0xFF0B0F12)
val DarkSurfaceContainerLow = Color(0xFF181C20)
val DarkSurfaceContainer = Color(0xFF1C2024)
val DarkSurfaceContainerHigh = Color(0xFF262A2E)
val DarkSurfaceContainerHighest = Color(0xFF313539)

// ---- Tank water (used by TankSprite, not part of the Material scheme) ------

/** Water surface (top of the tank gradient). */
val WaterTop = Color(0xFF4FB3E8)

/** Deep water (bottom of the tank gradient). */
val WaterBottom = Color(0xFF08476C)

/** Sand strip at the bottom of the tank. */
val TankSand = Color(0xFFE3CE97)

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
