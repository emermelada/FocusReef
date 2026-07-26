package com.emermeladas.focusreef.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * FocusReef's own navigation glyphs — hand-drawn 24dp vectors in one line
 * style (1.8 stroke, rounded caps/joins) instead of stock Material icons.
 *
 * Each destination has an outline (resting) and a filled (selected)
 * variant of the same silhouette: a fishbowl for Tanks, a rising wave for
 * Stats, a scallop shell for the Store. All paths are single-color so the
 * usual `Icon` tinting applies.
 */
object ReefIcons {

    /** Tanks, resting: a fishbowl with a waterline and a little fish. */
    val TanksOutline: ImageVector by lazy {
        reefIcon("reef.tanks.outline") {
            // Bowl: a circle with its top cut by the rim chord.
            strokePath {
                moveTo(8.2f, 6.8f)
                arcTo(7.1f, 7.1f, 0f, isMoreThanHalf = true, isPositiveArc = false, 15.8f, 6.8f)
                close()
            }
            // Waterline.
            strokePath {
                moveTo(8.6f, 9.7f)
                quadTo(10.3f, 8.7f, 12f, 9.7f)
                quadTo(13.7f, 10.7f, 15.4f, 9.7f)
            }
            // The resident fish (lens body + tail).
            strokePath {
                moveTo(9.4f, 13.7f)
                quadTo(11f, 12.3f, 12.6f, 13.7f)
                lineTo(13.9f, 12.8f)
                lineTo(13.9f, 14.6f)
                lineTo(12.6f, 13.7f)
                quadTo(11f, 15.1f, 9.4f, 13.7f)
                close()
            }
        }
    }

    /** Tanks, selected: the bowl filled solid, fish and a bubble cut out. */
    val TanksFilled: ImageVector by lazy {
        reefIcon("reef.tanks.filled") {
            fillPath(PathFillType.EvenOdd) {
                // Solid bowl.
                moveTo(8.2f, 6.8f)
                arcTo(7.1f, 7.1f, 0f, isMoreThanHalf = true, isPositiveArc = false, 15.8f, 6.8f)
                close()
                // Fish-shaped hole.
                moveTo(9.2f, 13.7f)
                quadTo(11f, 12.1f, 12.7f, 13.7f)
                lineTo(14.1f, 12.7f)
                lineTo(14.1f, 14.7f)
                lineTo(12.7f, 13.7f)
                quadTo(11f, 15.3f, 9.2f, 13.7f)
                close()
                // A rising bubble hole.
                moveTo(12f, 8.6f)
                arcTo(0.9f, 0.9f, 0f, isMoreThanHalf = true, isPositiveArc = true, 12.01f, 8.6f)
                close()
            }
        }
    }

    /** Stats, resting: a focus wave over its baseline. */
    val StatsOutline: ImageVector by lazy {
        reefIcon("reef.stats.outline") {
            strokePath {
                moveTo(4f, 19.2f)
                lineTo(20f, 19.2f)
            }
            strokePath {
                moveTo(4f, 13.5f)
                curveTo(6.5f, 7.5f, 9.5f, 7.5f, 12f, 12f)
                curveTo(14f, 15.5f, 16.5f, 15.5f, 20f, 9.5f)
            }
        }
    }

    /** Stats, selected: the same wave with the water under it filled. */
    val StatsFilled: ImageVector by lazy {
        reefIcon("reef.stats.filled") {
            strokePath {
                moveTo(4f, 19.2f)
                lineTo(20f, 19.2f)
            }
            fillPath {
                moveTo(4f, 13.5f)
                curveTo(6.5f, 7.5f, 9.5f, 7.5f, 12f, 12f)
                curveTo(14f, 15.5f, 16.5f, 15.5f, 20f, 9.5f)
                lineTo(20f, 17.4f)
                lineTo(4f, 17.4f)
                close()
            }
        }
    }

    /** Store, resting: a scallop shell with its ribs. */
    val StoreOutline: ImageVector by lazy {
        reefIcon("reef.store.outline") {
            strokePath {
                moveTo(12f, 19.6f)
                lineTo(5.4f, 11.2f)
                quadTo(6.2f, 8.2f, 8.4f, 6.9f)
                quadTo(10.1f, 5.6f, 12f, 5.8f)
                quadTo(13.9f, 5.6f, 15.6f, 6.9f)
                quadTo(17.8f, 8.2f, 18.6f, 11.2f)
                close()
            }
            // Ribs fanning from the hinge.
            strokePath {
                moveTo(12f, 19.6f)
                lineTo(8.4f, 6.9f)
            }
            strokePath {
                moveTo(12f, 19.6f)
                lineTo(12f, 5.8f)
            }
            strokePath {
                moveTo(12f, 19.6f)
                lineTo(15.6f, 6.9f)
            }
        }
    }

    /** Store, selected: the shell silhouette, solid. */
    val StoreFilled: ImageVector by lazy {
        reefIcon("reef.store.filled") {
            fillPath {
                moveTo(12f, 19.6f)
                lineTo(5.4f, 11.2f)
                quadTo(6.2f, 8.2f, 8.4f, 6.9f)
                quadTo(10.1f, 5.6f, 12f, 5.8f)
                quadTo(13.9f, 5.6f, 15.6f, 6.9f)
                quadTo(17.8f, 8.2f, 18.6f, 11.2f)
                close()
            }
        }
    }
}

/** Shared stroke weight of the icon set. */
private const val ICON_STROKE = 1.8f

/** 24dp builder preloaded with the reef icon-set conventions. */
private fun reefIcon(
    name: String,
    content: ImageVector.Builder.() -> Unit,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 24f,
    viewportHeight = 24f,
).apply(content).build()

/** One stroked subpath in the set's line style. */
private fun ImageVector.Builder.strokePath(pathBuilder: PathBuilder.() -> Unit) {
    path(
        fill = null,
        stroke = SolidColor(Color.Black),
        strokeLineWidth = ICON_STROKE,
        strokeLineCap = StrokeCap.Round,
        strokeLineJoin = StrokeJoin.Round,
        pathBuilder = pathBuilder,
    )
}

/** One filled subpath (tinted like everything else). */
private fun ImageVector.Builder.fillPath(
    fillType: PathFillType = PathFillType.NonZero,
    pathBuilder: PathBuilder.() -> Unit,
) {
    path(
        fill = SolidColor(Color.Black),
        pathFillType = fillType,
        pathBuilder = pathBuilder,
    )
}
