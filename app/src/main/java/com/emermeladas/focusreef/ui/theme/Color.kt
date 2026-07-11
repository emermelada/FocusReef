package com.emermeladas.focusreef.ui.theme

import androidx.compose.ui.graphics.Color
import com.emermeladas.focusreef.data.model.FishSpecies

// ---- Material scheme (ocean look) ------------------------------------------

// Light scheme
val OceanPrimary = Color(0xFF00658E)
val OceanOnPrimary = Color(0xFFFFFFFF)
val OceanPrimaryContainer = Color(0xFFC7E7FF)
val OceanOnPrimaryContainer = Color(0xFF001E2E)
val OceanSecondary = Color(0xFF4F616E)
val OceanOnSecondary = Color(0xFFFFFFFF)
val OceanSecondaryContainer = Color(0xFFD2E5F5)
val OceanOnSecondaryContainer = Color(0xFF0B1D29)
val OceanTertiary = Color(0xFF8B4A2B)
val OceanOnTertiary = Color(0xFFFFFFFF)
val OceanSurfaceLight = Color(0xFFF6FAFE)
val OceanOnSurfaceLight = Color(0xFF181C20)

// Dark scheme
val OceanPrimaryDark = Color(0xFF85CFFF)
val OceanOnPrimaryDark = Color(0xFF00344A)
val OceanPrimaryContainerDark = Color(0xFF004C69)
val OceanOnPrimaryContainerDark = Color(0xFFC7E7FF)
val OceanSecondaryDark = Color(0xFFB6C9D8)
val OceanOnSecondaryDark = Color(0xFF21323E)
val OceanSecondaryContainerDark = Color(0xFF374955)
val OceanOnSecondaryContainerDark = Color(0xFFD2E5F5)
val OceanTertiaryDark = Color(0xFFFFB599)
val OceanOnTertiaryDark = Color(0xFF531F02)
val OceanSurfaceDark = Color(0xFF101417)
val OceanOnSurfaceDark = Color(0xFFDFE3E7)

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
    FishSpecies.SMALL -> Color(0xFFF28C28)  // clownfish orange
    FishSpecies.MEDIUM -> Color(0xFF2E9E6B) // reef green
    FishSpecies.LARGE -> Color(0xFF7B68CD)  // deep-sea violet
}
