# FocusReef — Iteration 6: kill the "default Material" look

Screens read as default dark Material: neutral-gray dialogs on a teal app, no
edges, over-rounded, muddy contrast, inconsistent buttons. Fix the visual
SYSTEM only. Plan: C:\Users\javie\.claude\plans\zippy-sparking-hoare.md

- [x] 1. Color.kt: dark neutral ladder rebuilt as deep desaturated teal
      (B>G>R at every step) — dialogs no longer read as stock gray; light
      containers get a faint cool tint; added ReefAccent/OnReefAccent,
      HairlineTop/Bottom, ReefShadowColor
- [x] 2. Theme.kt: shape scale tightened to 8/12/16/20/24
- [x] 3. ReefSurface.kt: reusable elevated pane (Modifier.reefSurface +
      ReefSurface box) — tinted shadow + top-lit hairline + highlight fill;
      plus reefCardBorder() for Material Cards
- [x] 4. FocusReefDialog now uses reefSurface(surfaceContainerHigh, large,
      12dp) instead of a flat Surface
- [x] 5. ReefButtons.kt: Primary(accent)/Secondary(tonal)/Text, all 12dp
      (no pills). Both Move buttons unified to ReefSecondaryButton; 3 store
      buy buttons → accent + 12dp; Cancel/Close via DialogAction text
- [x] 6. SectionLabel.kt (uppercase, tracked, primary tint) on store +
      tank-detail + balance headers; balance number now Bold high-emphasis
- [x] 6b. reefCardBorder + surfaceContainerHigh on store cards, hero, and
      Stats cards so panels separate from the background
- [x] 7. compileDebugKotlin + test + assembleDebug all green. PENDING:
      on-device screenshots (no device/emulator attached here)

## Iteration 6b — coherencia en todas las pantallas

- [x] Un solo medidor en toda la app: `CapacityGauge` generalizado
      (parámetros ticks/fillColor/trackColor); la barra de XP
      (`LevelProgressRow`) ya no usa `LinearProgressIndicator` por defecto —
      usa el mismo gauge que la capacidad del tanque (ticks = 0)
- [x] `DecorationPlacementOverlay` usa `DialogAction` (sistema de botones),
      no `Button`/`TextButton` crudos
- [x] `BreakdownRow` (barras por año en Stats) con el mismo degradado
      "agua" que el gauge y las barras del chart
- [x] compile + test + assembleDebug verde; instalado en dispositivo
      (43201JEKB13187) — pendiente eyeball del usuario (USB intermitente)

## Review — Iteration 6

Root cause of the "default" look was the neutral-gray surface ladder on a
teal app + edgeless flat panes. Fixed at the system level: teal-tinted dark
neutrals, one elevated-pane definition (ReefSurface / reefCardBorder), a
3-role button system, tighter radii, and a section-label type style. New:
ReefSurface.kt, ReefButtons.kt, SectionLabel.kt. Touched: Color.kt, Theme.kt,
FocusReefDialog, TankDetailDialog, StoreScreen, StatsScreen. No feature/
economy/layout changes.

---

# FocusReef — Iteration 5: the chrome pass (nav bar + small surfaces)

Only the chrome: nav bar and every small overlay. Stock Material out,
reef language in. Plan: C:\Users\javie\.claude\plans\zippy-sparking-hoare.md

- [x] 1. ReefIcons.kt: 3 custom glyphs (fishbowl+fish / wave-chart /
      scallop shell), outline+filled pairs on FocusReefDestination;
      waterline hairline on the bar; filled glyph when selected
- [x] 2. FocusReefDialog: rise-through-water entrance (fade+drift+scale,
      reduced-motion instant), ReefScrim water-tinted backdrop (platform
      dim disabled via DialogWindowProvider), ReefHeader wave under title,
      DialogAction (filled primary / quiet neutral, both pressable)
- [x] 3. TankDetailDialog: CapacityGauge (gradient water-fill, surface
      highlight, quarter ticks, fill-on-open) replaces stock progress bar;
      Move is a compact FilledTonalButton; Close via DialogAction
- [x] 4. DialogListRow upgraded for all dialogs: water-chip sprite frames,
      pressable rows (no ripple), disabled rows dim but keep the reason
      readable; picker Cancel via DialogAction
- [x] 5. ReefToast (bubble mark + line, drifts up, auto-holds) replaces
      both SnackbarHosts; TOAST_HOLD_MS in GameConfig
- [x] 6. Verify: compileDebugKotlin + test + assembleDebug green.
      PENDING: on-device eyeball (no device attached)

## Review — Iteration 5

Chrome only, as requested. New: ReefIcons.kt, CapacityGauge.kt,
ReefToast.kt. Reworked: FocusReefDialog (shell + DialogListRow +
DialogAction), FocusReefDestination (glyph pairs), FocusReefBottomBar
(waterline + filled selection), TankDetailDialog, TankPickerDialog,
both screens' feedback. ReefScrim added to Color.kt.

---

# FocusReef — Iteration 4: polish pass — "a quiet reef seen through glass"

Elevate from functional prototype to handcrafted/calm/alive. Depth and ambient
life over animation noise. Hard constraints kept: art only via the 3 sprite
composables, tuning in GameConfig, colors/type in theme, MVVM intact.
Full plan: C:\Users\javie\.claude\plans\zippy-sparking-hoare.md

- [x] 0. Foundation: ui/theme/Motion.kt (motion vocabulary),
      utils/ReducedMotion.kt (animator-scale + battery saver gate),
      GameConfig presentation-tuning section, Modifier.pressable()
- [x] 1. Tank as living scene: layered water gradient + light shaft +
      caustics + ambient bubbles + textured sand + glass sheen; fish sway,
      idle bob, smooth flip, size-scaled speed; dark = deep-ocean water set
- [x] 2. Sprites: species-distinct fish silhouettes w/ form shading;
      dimensional token coin (bevel, glint, engraved fish); decoration
      contact shadows + gated kelp sway / bubbler stream / jellyfish drift
- [x] 3. Reward moments: animated balance count-down + purchase haptic
      (central spentTokens hook); new-fish settle-in from the surface;
      DataStore lastSeenEarnedTokens + "you earned N tokens" overlay
- [x] 4. Cohesion: ReefHeader (wave-motif) on all screens; styled bottom
      nav (lifted icons, primary pill); direction-aware tab transitions;
      gradient/staggered chart bars; shimmer skeletons; designed empty state
- [x] 5. Tactility sweep: pressable on tank cards + buy buttons (no ripple
      over water); reduced-motion static frames everywhere
- [x] 6. Verify: compileDebugKotlin + full `gradlew test` green;
      assembleDebug OK. PENDING (needs a device — none attached): on-device
      screenshots light+dark, purchase flow, reduced-motion eyeball

## Review — Iteration 4

All six phases landed and compile; unit tests pass untouched (no economy
logic changed). New files: Motion.kt, ReducedMotion.kt, ReefClock.kt,
Pressable.kt, ReefHeader.kt, ShimmerSkeleton.kt, EarningsMoment.kt,
ReefPreferences.kt, RewardViewModel.kt. Reworked: TankSprite (living
scene), FishSprite (9 silhouettes), TokenIcon, DecorationSprite,
ColumnChart, FocusReefBottomBar, FocusReefNavHost, FocusReefApp, the three
screens, Color.kt (TankPalette light/dark), GameConfig (presentation
tuning), strings.xml (+DataStore dep). Design rationale in lessons.md.
Visual verification on a real device is the one open item.

---

# FocusReef — Iteration 3: progression, new species, decorations, dialog polish

User decisions (2026-07-12): XP with streak multiplier (level never drops); new
fish varieties unlocked by level; decorations placed by dragging inside a
placement mode with Confirm/Cancel, repositionable from the tank detail dialog.

- [x] 1. Progression core: ProgressionCalculator (pure, XP from history with
      streak multiplier), Progression model, ProgressionRepository, GameConfig
      constants, mock streak tail, unit tests
- [x] 2. Six new fish species (Neon 2, Angelfish 3, Betta 4, Reef shark 5,
      Lionfish 7, Orca 9) + level gating: unlockLevel, PurchaseResult.LevelTooLow,
      locked store cards, LevelProgressRow in Store hero + Stats
- [x] 3. Dialog foundation: FocusReefDialog + DialogListRow; rebuild
      TankPickerDialog (generic tankEnabled/supportingText) and TankDetailDialog
- [x] 4. Decorations data layer: DecorationSpecies catalog (Shell/Rock/Kelp
      floor; Bubbler/Jellyfish floating; Chest floor), Room v2 + migration,
      buyDecoration (commits at buy time, default position), PlacementMath + tests
- [x] 5. Decorations UI: DecorationSprite, TankSprite renders decorations,
      store section, drag-to-place overlay, reposition from tank detail
- [x] 6. Verify build + tests, logical commits, push

## Review (2026-07-12)

All five workstreams implemented and verified: `assembleDebug` +
`testDebugUnitTest` green after every commit (24 unit tests, 15 new).

Key decisions:
- XP is derived purely from the focus-block history (like tokens), so the
  level can never drop; multiplier tiers x1/x1.5(3d)/x2(7d), level = 1 +
  floor(sqrt(xp/100)). Mock demo user lands ~level 14.
- Decoration purchase commits at buy time (append-only ledger, crash-safe);
  placement mode only adjusts position, Cancel keeps the default spot.
- Positions stored in BiasAlignment units → independent of rendered size.

Known follow-ups:
- Manual on-device check of the v1→v2 migration (hand-written SQL,
  exportSchema=false; consider flipping exportSchema=true next iteration).
- Real sprites: FishSprite/TankSprite/DecorationSprite are the only render
  points to touch.
- Snackbar strings for decoration purchase reuse the fish ones where shared.

---

# FocusReef — Iteration 2: tank management + fish animation

- [x] Store: choose destination tank when buying a fish (TankPickerDialog; skipped when only one tank has room)
- [x] Tank detail dialog on tap: per-species counts + Move action
- [x] Move fish between tanks (AquariumRepository.moveFish, validated: destination space, stale-UI guard)
- [x] Fish wander the tank (Animatable x/y per fish, random targets, direction-aware flip)
- [x] Verify build + tests, commit, push (verified 2026-07-12: assembleDebug + testDebugUnitTest green)

---

# FocusReef — Initial app development

## Plan

- [x] 1. Gradle scaffold copied from fihgame template (wrapper, catalogs, AGP 9.2.1 / Kotlin 2.2.10), renamed to FocusReef, package `com.emermeladas.focusreef`
- [x] 2. Dependencies: Hilt (KSP), Room, Retrofit, Navigation Compose, ViewModel Compose, Coroutines
- [x] 3. Domain models + `GameConfig` economy constants
- [x] 4. Data layer: mock NAS data source (+ Retrofit service for later), Room (tanks, fish, purchase ledger), repositories, Hilt modules
- [x] 5. UI foundation: ocean theme, navigation (3 destinations + bottom bar), placeholder sprite components
- [x] 6. Screens: Tanks, Stats (weekly/monthly/annual), Store
- [x] 7. Unit tests for pure logic (stats aggregation, wallet math, tank slots)
- [x] 8. Verify: `gradlew assembleDebug` + unit tests green
- [x] 9. Logical commits

## Decisions

- Package `com.emermeladas.focusreef` (matches Javier's convention from fihgame/ArtCenter, not `com.javier.*`)
- minSdk 26 (template had 24; java.time needs 26+ without desugaring)
- Coil and DataStore deferred until actually needed (sprites/preferences) — keeps the build lean
- Spent tokens = purchase ledger table; balance = mock-earned − SUM(ledger)

## Review

Full v1 implemented and verified on 2026-07-11: `:app:assembleDebug` and
`:app:testDebugUnitTest` both green (9 unit tests).

Build issues hit and fixed:
1. AGP 9 built-in Kotlin rejects KSP's use of `kotlin.sourceSets` →
   `android.disallowKotlinSourceSets=false` in gradle.properties.
2. core-ktx 1.19 / lifecycle 2.11 (from the fihgame template catalog) require
   compileSdk 37, but only android-36.1 is installed → pinned core-ktx 1.17.0
   and lifecycle 2.9.2. Revert when SDK 37 gets installed.

Known follow-ups:
- `hiltViewModel()` import is deprecated (moved to
  androidx.hilt.lifecycle.viewmodel.compose) — 3 warnings, swap when bumping
  hilt-navigation-compose.
- Real sprites: replace the bodies of `FishSprite`/`TankSprite` only.
- NAS API: bind `RemoteFocusHistoryRepository` in RepositoryModule. The base URL
  is no longer a build constant — it is typed into Settings and stored in
  DataStore (see the UI overhaul review below).

---

# Review — UI/UX overhaul (2026-07-26)

Executed `tasks/ui-audit-and-plan.md` end to end: 28 items across 8 phases.

## What changed, by phase

**1 — Platform shell.** Real splash screen via `core-splashscreen`, held on the
first preference read so a dark-mode launch never flashes white. Night theme
values, monochrome launcher icon, edge-to-edge.

**2 — Design system.** Accent hierarchy (`ReefPrimaryButton` /
`ReefSecondaryButton` / `ReefTextAction`), shared `ReefScreenScaffold`,
`ReefContentCard`, spacing scale used everywhere instead of inline dp.

**3 — Content and merchandising.** Fish and decorations got real names
(Clownfish, not "Small fish"), a "Coming up" shelf for locked items, and
disabled Buy buttons that say *why* ("3 more to go", "No room in your tanks").

**4 — Missing states.** Every screen now has an error state with Retry, a
shimmer skeleton, and a first-run empty state that explains the earn-and-spend
loop. Cold flows use a `retryTrigger` + `flatMapLatest` + `.catch {}` idiom, so
Retry genuinely re-subscribes.

**5 — Stats depth.** `ColumnChart` rewritten: a labelled gridline at the
maximum, the peak's value always visible, tap any bar to move the readout,
axis labels thinned to at most 8 so a 31-day month stays legible.

**6 — Settings.** New screen reached from a gear in every top bar — deliberately
*not* a fourth tab, because CLAUDE.md fixes the bottom bar at three
destinations. Holds the NAS address (with a live "Test connection"), a
theme override, the still-reef switch, the version, and the one destructive
action. Making the NAS address genuinely runtime-configurable meant Retrofit
could no longer be a plain `@Singleton`: `di/NetworkModule.kt` was deleted and
replaced by `NasClient`, which caches a Retrofit instance keyed on the URL.

**7 — Accessibility and i18n.** Seven hand-rolled `if (n == 1)` strings became
real `<plurals>`. Live regions on the toast, the earnings moment and the
connection verdict. Section titles marked `heading()`, decorative sprite text
`clearAndSetSemantics {}`, `onClickLabel` on tank cards. Full `values-es`
translation added, including the Spanish `many` plural category.

**8 — Adaptive layout.** Tank height derives from a 16:10 aspect ratio rather
than a hardcoded 210dp. Column counts moved into `values/integers.xml` +
`values-w600dp/integers.xml`: Tanks 1→2 columns, Store 2→3. The Tanks list is
now a `LazyVerticalGrid` whose error and onboarding items span the full width.

## Judgement calls worth remembering

- **Reset refunds tokens, it does not erase history.** The focus-block record
  belongs to the desk; the game has no business deleting it.
- **The accent button in the reset dialog is the *safe* option.** Nothing about
  a destructive dialog should invite a reflexive tap on the destructive verb.
- **Reduced motion is OR-ed, never overridden.** A device-wide accessibility
  setting outranks the in-app toggle.
- **`store_msg_decoration_limit` lost its number.** It said "already has 6
  decorations" while the real cap is a `GameConfig` constant, so rebalancing
  made the app lie. Toasts carry no format arguments, so the string now states
  the fact rather than the figure.

## Verification

Green:
- `:app:assembleDebug` — BUILD SUCCESSFUL.
- `:app:testDebugUnitTest` — BUILD SUCCESSFUL.
- `:app:lintDebug` — 0 errors. Lint also caught three real bugs during this
  pass, all fixed: `MissingTranslation` (fixed with `translatable="false"` on
  the product name and the sample address), `MissingQuantity` (Spanish `many`),
  and `ConstantLocale` — the Stats date formatters were top-level `val`s that
  froze the locale at class-load, so switching the phone to Spanish left the
  chart axis in English until the process died. They are now built inside a
  `remember(LocalConfiguration.current)`.

Remaining lint noise, all deliberate: 12 `GradleDependency` + 8
`NewerVersionAvailable` (version bumps are blocked on SDK 37, see above), 3
`PluralsCandidate` false positives (`17/24 slots` is a ratio, not a count),
1 `RedundantLabel`, `OldTargetApi`, `ObsoleteSdkInt`.

**Not verified — needs a device or emulator, and there is neither on this
machine** (`adb devices` empty, `emulator -list-avds` empty). These are the
plan's remaining checklist items and still need a human pass:
- Cold launch in dark mode, confirming no white flash.
- Screenshots of all four screens, light and dark, portrait and landscape —
  including that the new 2/3-column layouts do not letterbox the aquarium.
- A TalkBack sweep of every screen.
- Device language set to Spanish.
- An n=1 state (one free slot, one focus block) for plural grammar.
- Network killed, confirming error + Retry rather than an infinite shimmer.
