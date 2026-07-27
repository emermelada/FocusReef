package com.emermeladas.focusreef.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.emermeladas.focusreef.ui.theme.ReefSpacing
import androidx.compose.foundation.layout.WindowInsets as ComposeWindowInsets

/**
 * The app's screen header, as real chrome rather than a scrolling item.
 *
 * It keeps the signature of the old inline header — the title in Sora with a
 * short wave crest under it — but lives in a [TopAppBar], so it pins to the
 * top, consumes the status bar inset, and picks up a container tint as
 * content scrolls beneath it. That tint is the piece that was missing: under
 * edge-to-edge, content used to slide under the system clock with nothing
 * separating them.
 *
 * @param title The screen name.
 * @param scrollBehavior Supplied by [ReefScreenScaffold]; drives the scroll tint.
 * @param navigationIcon Optional leading icon button — a way out of screens
 *   that are visited rather than lived in (settings), empty on the tabs.
 * @param actions Optional trailing icon buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReefTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    // Announced as a heading so TalkBack users can jump by
                    // heading instead of swiping through every control.
                    modifier = Modifier.semantics { heading() },
                )
                ReefWaveMark()
            }
        },
        navigationIcon = navigationIcon,
        actions = actions,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            // Once content is scrolled under the bar it needs a surface of its
            // own, or the title sits on top of moving content.
            scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        modifier = modifier,
    )
}

/** Two gentle wave crests in primary — the app's signature mark. */
@Composable
private fun ReefWaveMark() {
    val waveColor = MaterialTheme.colorScheme.primary
    Canvas(
        modifier = Modifier
            .padding(top = ReefSpacing.xs)
            .width(44.dp)
            .height(6.dp),
    ) {
        val midY = size.height / 2f
        val wave = Path().apply {
            moveTo(0f, midY)
            quadraticTo(size.width * 0.25f, -midY * 0.6f, size.width * 0.5f, midY)
            quadraticTo(size.width * 0.75f, size.height + midY * 0.6f, size.width, midY)
        }
        drawPath(
            path = wave,
            color = waveColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round),
        )
    }
}

/**
 * The shell every screen sits in: a pinned [ReefTopBar] over scrollable
 * content, with all the inset arithmetic done once here instead of three
 * slightly-different times in three screens.
 *
 * The [content] lambda receives the padding it should hand to its scrolling
 * container as `contentPadding` — top clears the app bar, bottom clears the
 * navigation bar, sides are the standard screen margin. Passing it as
 * *content* padding rather than wrapping the list in `Modifier.padding` is
 * what lets content scroll under both bars instead of being clipped by them.
 *
 * @param title Screen name shown in the bar.
 * @param outerPadding Padding from the app-level scaffold (the bottom bar).
 * @param navigationIcon Optional leading icon button in the bar (e.g. a back
 *   arrow for screens outside the bottom navigation).
 * @param actions Optional trailing icon buttons in the bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReefScreenScaffold(
    title: String,
    outerPadding: PaddingValues,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (contentPadding: PaddingValues) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current

    Scaffold(
        topBar = {
            ReefTopBar(
                title = title,
                scrollBehavior = scrollBehavior,
                navigationIcon = navigationIcon,
                actions = actions,
            )
        },
        // The top bar consumes the status bar itself; the bottom comes from
        // the app scaffold via outerPadding. Nothing to add here.
        contentWindowInsets = ComposeWindowInsets(0),
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        content(
            PaddingValues(
                start = ReefSpacing.lg + outerPadding.calculateStartPadding(layoutDirection),
                end = ReefSpacing.lg + outerPadding.calculateEndPadding(layoutDirection),
                top = innerPadding.calculateTopPadding() + ReefSpacing.sm,
                bottom = outerPadding.calculateBottomPadding() + ReefSpacing.xl,
            ),
        )
    }
}
