# Lessons — FocusReef

## Iteration 6 (kill the default look) — what actually reads as "professional"

- **The #1 "default Material" tell is neutral-gray surfaces on a colored
  app.** FocusReef's dark ladder was pure gray (#101417→#313539) while the
  app is teal, so every dialog looked foreign/stock. Fix: every dark neutral
  now carries a deep desaturated teal undertone (B>G>R). A brand's "dark
  gray" must be a very desaturated version of the brand hue, never neutral.
- **Elevated panes need a real edge in the dark.** Flat fills with no border
  and a black shadow dissolve into a dark background. The reef pane =
  tinted (deep-water, not black) shadow + a top-lit hairline border (bright
  rim fading down) + a whisper of top-highlight on the fill. One definition
  (`ReefSurface` / `Modifier.reefSurface` / `reefCardBorder()`); reuse it —
  never hand-roll a card's separation again.
- **Full-pill buttons + max rounding read as cheap/template.** Buttons use a
  defined 12dp radius, and the shape scale was tightened (8/12/16/20/24).
  M3 `Button` defaults to a stadium shape — you must pass `shape` to escape
  the pill.
- **Consistency beats variety in buttons.** Two different "Move" styles in
  one dialog screamed default. Three roles only — Primary (one saturated
  accent per surface), Secondary (tonal), Text (minor) — used with zero
  exceptions.
- **Hierarchy: heroes heavy, sections labeled.** Big numbers go Bold/high-
  emphasis; group headings become a small UPPERCASE letter-spaced primary-
  tinted `SectionLabel`. Uppercase in the composable, not the source string.

## Iteration 5 (chrome pass) — design decisions worth keeping

## Iteration 5 (chrome pass) — design decisions worth keeping

- **The chrome carries the brand.** Stock Material icons/dialogs/snackbars
  are what make an app read "generic" even when the content is custom. The
  nav glyphs are hand-built ImageVectors in ReefIcons (1.8 stroke, rounded
  caps, outline at rest / filled selected). Any future icon must join that
  set — never import `Icons.Filled.*` again.
- **One dialog shell, one entrance.** FocusReefDialog owns the scrim
  (platform dim killed via `DialogWindowProvider.window.setDimAmount(0f)`,
  replaced with ReefScrim blue-black), the rise-through-water entrance, the
  wave-motif title, and the action styles (DialogAction). New dialogs get
  all of it for free; never call `Dialog`/`AlertDialog` directly.
- **Scrim-tap dismissal + full-size Dialog:** with
  `usePlatformDefaultWidth = false`, taps on the card bubble up to the
  scrim's clickable — the card must consume clicks with a no-op clickable.
- **Disabled rows must explain themselves.** DialogListRow dims the chip
  and headline but keeps the supporting line at full contrast — it carries
  the reason (e.g. "0 free slots").

## Iteration 4 (polish pass) — design decisions worth keeping

## Iteration 4 (polish pass) — design decisions worth keeping

- **One clock, many layers.** All ambient tank motion (caustics, bubbles,
  fish sway, decoration sway) is driven by a single `rememberReefClock`
  frame callback, read only inside draw/`graphicsLayer` lambdas. Ticks cost
  a redraw, never a recomposition. Never add a per-element
  `rememberInfiniteTransition` inside the tank.
- **Reduced motion is a first-class path, not an afterthought.**
  `rememberReducedMotion()` (animator scale 0 or battery saver) freezes the
  clock, skips wander/entrance effects and bubbles. Every future ambient
  flourish must render a sensible static frame when the clock stays at 0.
- **Motion vocabulary lives in `ReefMotion`** (ui/theme/Motion.kt): gentle
  springs, `RevealEasing`, shared durations. World-feel numbers (swim/hover
  ms, bubble counts, haptics flag) live in `GameConfig`. Don't inline curves
  or timing constants in screens.
- **Reward signals hook data, not buttons.** The purchase haptic watches
  `wallet.spentTokens` increasing (one central hook), and the welcome-back
  moment compares `earnedTokens` against a DataStore baseline persisted
  *immediately* so process death can't re-celebrate. First launch stores a
  baseline silently — never congratulate the whole history.
- **Entrance flags must be captured once.** New-fish settle-in remembers
  `entering` per fish id (`remember(fish.id)`) so recompositions can't
  cancel or replay the entrance; residents are the ids present at first
  tank composition.
- **No ripple over water.** Tank cards use `Modifier.pressable` +
  `indication = null`; a rectangular ink ripple across the water breaks the
  scene. Press-scale is the shared feedback everywhere.

## Tooling gotchas (Windows / this machine)

- `gradlew` needs `JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"`
  exported in the shell — it is NOT set globally. A bare `./gradlew ... |
  tail` exits 0 even when gradle fails (pipe takes tail's exit code):
  always grep for `BUILD SUCCESSFUL` / `^e:` instead of trusting exit codes.
- No AVDs exist locally; visual verification happens on the user's device.
