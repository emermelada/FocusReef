# FocusReef — UI/UX audit and remediation plan

Audit date: 2026-07-26. Scope: `ui/`, `res/`, manifest, and the model/config files that
feed the UI. Nothing was changed; this is analysis + a plan.

---

## 0. What is already good (don't touch it)

Being fair about the baseline matters, because most of the polish problems are *not* in
the part that got the most attention:

- **The tank scene is genuinely well made.** `TankSprite.kt` layers a water gradient, a
  light shaft, drifting caustics, a shadowed floor, a waved+grained sand bed, rising
  bubbles and a glass sheen, all driven off one frame clock that freezes under reduced
  motion and stops off-screen. The nine `FishSprite` silhouettes have real anatomy
  (gill slits, eye glints, belly sweeps, veil fins) and light-from-above shading.
- **Motion has a system.** `ReefMotion` + `ReducedMotion` + `pressable` are consistent,
  and the fish wander/hover/turn logic is subtle rather than bouncy.
- **The color and type scales are complete.** Every M3 role is defined in both modes;
  Sora/Inter are mapped across the full type scale.

**The aquarium is not why the app looks unfinished. The chrome around it is.**

---

## 1. Tier 1 — the "this isn't a shipped app" tells

These are the ones a stranger notices in the first ten seconds.

### 1.1 White flash on every cold start
`res/values/themes.xml:4`
```xml
<style name="Theme.FocusReef" parent="android:Theme.Material.Light.NoActionBar" />
```
Hardcoded **light** platform theme. No `values-night`. No `android:windowBackground`.
No `androidx.core:core-splashscreen` anywhere in the build. Result: launching in dark
mode gives a white window, then a dark UI slams in. This happens *before* the user sees
a single pixel of the good work, and it is the strongest single "prototype" signal in
the app.

### 1.2 The themed launcher icon is wrong
`res/mipmap-anydpi-v26/ic_launcher.xml`
```xml
<monochrome android:drawable="@drawable/ic_launcher_foreground" />
```
`<monochrome>` is pointed at the **colored** foreground. Android 13+ themed icons
require a flat single-color silhouette on transparent; feeding it a multi-color drawable
produces a muddy blob on the home screen. First impression, before launch.

### 1.3 A wall of sixteen identical bright-aqua buttons
`ui/theme/Color.kt:160-163` documents `ReefAccent = #2FD6C4` as:
> "Reserved for the single PRIMARY action on a surface (Buy, confirm)"

`StoreScreen.kt` then puts one on **every** fish card (9), **every** decoration card (6),
and the tank row — all on screen at once, all shouting at the same volume. It is also
outside the M3 scheme, so a saturated mint-aqua fights the ocean-blue primary
(`#00658E`) on a near-white background. When everything is primary, nothing is. This is
the biggest visual reason the Store reads as a cheap mobile game rather than a product.

### 1.4 Store items are in arbitrary order
`StoreScreen.kt:124` iterates `FishSpecies.entries`, i.e. declaration order:

| # | Species | Slots | Price | Unlock |
|---|---------|-------|-------|--------|
| 1 | Small fish | 1 | 4 | 1 |
| 2 | Medium fish | 3 | 10 | 1 |
| 3 | Large fish | 5 | 18 | 1 |
| 4 | Neon tetra | 1 | 6 | 2 |
| 5 | Angelfish | 3 | 14 | 3 |
| 6 | Betta | 1 | 9 | 4 |
| 7 | Reef shark | 5 | 30 | 5 |
| 8 | Lionfish | 3 | 22 | 7 |
| 9 | Orca | 5 | 45 | 9 |

Size and price zig-zag; locked items are scattered between buyable ones. No real store
does this — they sort by tier/price and separate "you can buy this now" from "you're
working toward this."

### 1.5 Placeholder product names shipped next to real ones
`data/model/FishSpecies.kt:22-24` — **"Small fish", "Medium fish", "Large fish"** sit in
the same grid as "Betta", "Lionfish", "Orca". Three names never got replaced. That
single inconsistency undoes a lot of the sprite work.

---

## 2. Tier 2 — layout and design-system defects

### 2.1 Buy buttons don't bottom-align across a grid row
`StoreScreen.kt:307-370` (`FishCard`) and `:377-450` (`DecorationCard`). The inner
`Column` doesn't `fillMaxHeight()` and there is no `Spacer(Modifier.weight(1f))` before
the `Button`. `LazyVerticalGrid` stretches both cards in a row to the taller one, so as
soon as one name wraps to two lines its neighbour's button floats mid-card. Ragged
button baselines down the whole grid.

### 2.2 No screen chrome — the title just scrolls away
`ReefHeader` is a plain item inside the scroll content (`TanksScreen.kt:76`,
`StatsScreen.kt:79`, `StoreScreen.kt:110`). There is no `TopAppBar`, no status-bar
scrim, no scroll behavior. Combined with `enableEdgeToEdge()` (`MainActivity.kt:18`),
content slides unbroken under the system clock. And `FocusReefApp.kt:30-41` applies
`innerPadding` to a `Box` wrapping the whole `NavHost`, so no screen can consume insets
itself — content can never bleed under the nav bar the way a polished app does.

### 2.3 The hero of the app is a fixed 210dp strip
`TanksScreen.kt:238`. Hardcoded height, no `values-w600dp`, no landscape or tablet
consideration anywhere in the project. Rotate the phone and the aquarium becomes a
letterbox. Every tank is also the same height whether it holds 1 fish or 24.

### 2.4 Three competing surface treatments
`ReefSurface.kt` is documented as *"the single definition of an elevated pane in
FocusReef"* — and then the screens don't use it. `StatsScreen` and `StoreScreen` each
hand-build their own `primaryContainer` hero `Card` inline
(`StatsScreen.kt:97-110`, `:239-267`; `StoreScreen.kt:241-293`) with `reefCardBorder()`.
So the app has: raw `Card` + border, `ReefSurface`, and `Material Surface` (toast,
earnings) — three answers to one question, and the "single definition" is nearly unused.

### 2.5 Spacing is not on a scale
16 / 14 / 12 / 10 / 18 / 8 / 6 dp appear ad hoc: grid spacing 10dp
(`StoreScreen.kt:106`), stats column spacing 14dp (`StatsScreen.kt:77`), hero padding
18dp, card padding 12/14/16dp. The theme defines color, type and shape — but not space.
Inconsistent rhythm is subliminal and it is exactly what separates "designed" from
"assembled."

---

## 3. Tier 3 — UX and state gaps

### 3.1 There is no error state anywhere in the app
`StatsUiState.kt`, `TanksUiState.kt`, `StoreUiState.kt` all carry only `isLoading`.
`StatsViewModel` has no `.catch {}`. If the NAS is unreachable the Stats screen shimmers
**forever** — no message, no retry, no offline badge, no "last synced" timestamp. There
is no pull-to-refresh anywhere. The entire premise of the app is remote data.

### 3.2 Disabled Buy buttons never say why
`CLAUDE.md:88` requires *"disabled button + reason"*. Level-locked items do show
"Unlocks at level N" — but **can't afford** and **no free slots** render as a dead grey
button showing the price. Tap, nothing, no explanation. `StoreUiState.canBuyFish`
already evaluates the three conditions separately (`StoreUiState.kt:29-32`); the reason
exists in code and simply isn't surfaced.

### 3.3 No first-run model of the economy
`EmptyReefHint` (`TanksScreen.kt:163`) is three bubbles and one sentence. Nothing
explains what a token is, that they come from the desk's 25-minute blocks, what levels
gate, or what the streak multiplier does. A cold user has no mental model.

### 3.4 The monthly view promised by the spec doesn't exist
`CLAUDE.md:93` specifies weekly / monthly / annually. The tabs are **Week / Year /
All-time** (`StatsScreen.kt:66-70`), and `stats_this_month` is a dead string. There is
also no way to look at any *past* week or month — only the current one.

### 3.5 Chart values are hidden behind an undiscoverable tap
`ColumnChart.kt` has no y-axis, no gridlines, no value labels until a bar is tapped —
and nothing signals that bars are tappable. For a zero day the bar is a 3dp stub, making
the tap target ~23dp tall, well under the 48dp minimum. Selection also resets on every
`entries` change.

### 3.6 Twelve ambiguous month labels
`StatsScreen.kt:50` — `DateTimeFormatter.ofPattern("MMMMM")` gives
`J F M A M J J A S O N D`. Four ambiguous pairs. Unreadable as an axis.

### 3.7 No settings screen at all
The NAS address is a compile-time constant (`NetworkModule.kt:23` —
`http://192.168.1.100:8080/api/`). No theme override, no in-app reduced-motion toggle,
no about/version, no data reset, no connection test. Every finished app has this screen.

---

## 4. Tier 4 — accessibility, i18n, copy correctness

### 4.1 The app is effectively invisible to TalkBack
`contentDescription` appears **three times in the entire codebase**, and all three are
`null`. Every `Canvas` — `FishSprite`, `TankSprite`, `DecorationSprite`, `TokenIcon`,
`CapacityGauge`, `ColumnChart`, the `ReefHeader` wave — is an unlabeled drawing. There
is no `semantics {}` block anywhere, no `stateDescription` on the capacity gauge, no
`heading()` on section labels, and no `liveRegion` on `ReefToast` or `EarningsMoment` —
so reward and failure messages are never announced.

### 4.2 Broken pluralization throughout
Exactly one real `<plurals>` exists (`earnings_moment_title`). Everything else is wrong
at n=1:
- `store_slots` / `store_slots_plural` — hand-rolled in `StoreScreen.kt:508`
- `tank_free_slots` → **"1 free slots"**
- `stats_focus_blocks` → **"1 focus blocks"**
- `tank_decoration_count`, `tank_slots_used`, `store_price`, `store_balance` — same

Grammatically wrong UI strings are one of the clearest amateur signals there is.

### 4.3 A magic number baked into a string
`strings.xml:65` — `"That tank already has 6 decorations"` while
`GameConfig.TANK_DECORATION_CAP = 6`. Change the constant and the message lies.

### 4.4 English-only, and product names can't be translated
No `values-es` despite a Spanish-speaking author. Worse, `FishSpecies.displayName` and
`DecorationSpecies.displayName` are **Kotlin string literals**, not resources — they
cannot be translated without a model refactor.

### 4.5 Buttons labeled with nouns, not actions
`store_price` and `store_balance` are the identical string `"%1$d tokens"`, so the CTA
reads **"4 tokens"** — a price tag, not an action. `store_buy` ("Buy") is defined and
never used.

### 4.6 Toast has no queue and no dismiss
`ReefToast.kt` holds for a fixed `TOAST_HOLD_MS` regardless of message length, a second
message replaces the first mid-animation, and it can't be swiped or tapped away.

---

## 5. Root causes (the four things actually worth fixing)

1. **The design system stops at the component boundary.** Color/type/shape are
   centralized; spacing, surfaces, buttons and hero cards are not — so screens
   improvise, and the improvisations disagree.
2. **The accent color has no hierarchy discipline.** One saturated color applied to
   every action flattens the whole information hierarchy.
3. **Only the happy path was designed.** Loading exists; error, empty-with-reason,
   offline, and "why is this disabled" do not.
4. **The platform shell was never finished.** Splash, themed icon, window background,
   insets, landscape, TalkBack, plurals, locales — the unglamorous 20% that is 80% of
   "does this feel professional."

---

## 6. The prompt / plan

Copy-paste the block below into a fresh Claude Code session in `FocusReef/`.

---

````markdown
# FocusReef — professional polish pass

You are working in the FocusReef Android project (Kotlin, Compose, M3, Hilt, Room).
Read `CLAUDE.md` and `tasks/ui-audit-and-plan.md` first — the audit contains the full
findings with file:line references. Follow the parent-directory workflow rules: plan
mode, `tasks/todo.md`, verify before done.

## Objective
Make the app read as a finished product. The aquarium rendering (`TankSprite`,
`FishSprite`, `DecorationSprite`, `ReefMotion`) is already good — **do not redesign it**.
Fix the chrome, the design-system gaps, the missing states, and the platform shell.

## Guardrails
- No new dependencies except: `androidx.core:core-splashscreen`, and
  `material3` pull-to-refresh if not already available.
- All economy numbers stay in `GameConfig`. All user-facing text goes to `strings.xml`.
- Every change to visual language lands in `ui/theme/` or `ui/components/` — never
  inline in a screen.
- KDoc everything public, per existing conventions.
- Run `rtk cargo`-equivalent for Android: `rtk ./gradlew :app:assembleDebug` and
  `rtk ./gradlew :app:testDebugUnitTest` after each phase. Do not report a phase done
  until both pass.

## Phase 1 — Platform shell (highest visible impact, lowest risk)
1. Rewrite `res/values/themes.xml`: parent `android:Theme.Material.DayNight.NoActionBar`,
   add `android:windowBackground` pointing at a color that matches
   `LightBackground #F6FAFE`. Add `res/values-night/themes.xml` with
   `DarkBackground #081319`. This kills the white cold-start flash.
2. Add `androidx.core:core-splashscreen`, an `Theme.FocusReef.Starting` style with the
   launcher icon and the reef background, and `installSplashScreen()` in
   `MainActivity.onCreate` before `super`.
3. Create a proper monochrome launcher layer: a new
   `drawable/ic_launcher_monochrome.xml` that is a single-color (`#FFFFFF`) fish+wave
   silhouette on transparent, and point `<monochrome>` at it in both
   `mipmap-anydpi-v26/*.xml`.
4. Move `Scaffold`'s `innerPadding` out of the wrapper `Box` in `FocusReefApp.kt`: pass
   the padding down so each screen applies it to its own `contentPadding`, letting
   scroll content bleed under the nav bar while the bar stays inset.

## Phase 2 — Design system completion
5. Add `ui/theme/Spacing.kt`: a `ReefSpacing` object with a 4dp-based scale
   (`xs=4, sm=8, md=12, lg=16, xl=24, xxl=32`). Replace every ad-hoc dp in the three
   screens with it. No raw spacing dp values left in `ui/screens/`.
6. Add `ui/components/ReefHeroCard.kt` — one composable for the `primaryContainer` hero
   pattern. Replace the three inline copies in `StatsScreen` (progression card, HeroCard)
   and `StoreScreen` (BalanceHero) with it.
7. Establish accent hierarchy in `ui/components/ReefButtons.kt`:
   - `ReefPrimaryButton` — `ReefAccent`, for the **one** dominant action on a surface.
   - `ReefBuyButton` — a **tonal** button (`secondaryContainer` fill, `primary` label)
     for the repeated store CTAs, with a `TokenIcon` and the price.
   Replace all three inline `Button(colors = ReefAccent)` blocks in `StoreScreen.kt`
   with `ReefBuyButton`. `ReefAccent` must appear at most once per visible screen.
8. Add a `ReefTopBar` (built on M3 `TopAppBar` wrapping the existing `ReefHeader`
   wave mark) with `enterAlwaysScrollBehavior`, and adopt it on all three screens so
   the title pins and a scrim appears under the status bar on scroll.

## Phase 3 — Content and merchandising
9. Rename the placeholder species in `FishSpecies.kt`: `SMALL` → "Clownfish",
   `MEDIUM` → "Blue tang" (or similar reef fish), `LARGE` → "Grouper". **Do not rename
   the enum constants** — they are persisted in Room. Only `displayName` changes.
10. Move `displayName` off the enum into string resources: add
    `FishSpecies.displayNameRes: Int` and `DecorationSpecies.displayNameRes: Int`,
    resolve with `stringResource` at call sites (`StoreScreen`, `TankDetailDialog`,
    `DecorationPlacementOverlay`).
11. Sort the store: render fish and decorations ordered by `unlockLevel`, then
    `priceTokens`. Group locked items into a collapsed "Coming up" section below the
    buyable ones, each showing its unlock level.
12. Fix `FishCard`/`DecorationCard` layout: `Column(Modifier.fillMaxHeight())` with
    `Spacer(Modifier.weight(1f))` before the button, so buttons bottom-align across
    every grid row. Give the name `minLines = 2` so single- and double-line names
    reserve the same space.

## Phase 4 — Missing states
13. Add `error: ReefError?` and `isRefreshing: Boolean` to all three UiStates. Add
    `.catch {}` in the ViewModels. Build `ui/components/ReefErrorState.kt` — an
    illustrated (reuse the bubble motif) message + Retry button.
14. Add pull-to-refresh to Stats and Tanks, with a "Last synced <relative time>" line
    under the header when data came from cache.
15. Surface **why** a Buy button is disabled. Replace `canBuyFish(species): Boolean`
    with `buyState(species): BuyState` — a sealed type of `Available`,
    `LockedByLevel(level)`, `NotEnoughTokens(short)`, `NoSlots`. Render the reason as
    the button's supporting line (e.g. "Need 6 more tokens" / "No free slots"), and
    keep the button disabled.
16. Expand `EmptyReefHint` into a real first-run card: what a token is, that it comes
    from a completed 25-minute focus block, and what levels unlock — three short lines
    with the token, fish, and lock icons.

## Phase 5 — Stats depth
17. Add the **Month** tab the spec requires (`CLAUDE.md:93`); wire the dead
    `stats_this_month` string. Tabs become Week / Month / Year / All-time.
18. Fix the month axis: use `"MMM"` (Jan, Feb) with rotated or alternating labels
    instead of the ambiguous single-letter `"MMMMM"`.
19. Upgrade `ColumnChart`: always-visible value labels on the tallest bar and the
    selected bar, a faint max gridline with its value, a minimum 48dp tap target per
    column (pad the clickable, not the bar), and preserve selection across data changes
    by keying on the entry label rather than the index.

## Phase 6 — Settings
20. Add a fourth destination `Settings` (gear icon, `ReefIcons`) with:
    NAS base URL (DataStore-backed, replacing the constant in `NetworkModule`, with a
    "Test connection" action), theme override (System/Light/Dark), a reduced-motion
    toggle, app version, and a destructive "Reset aquarium" behind a confirm dialog.

## Phase 7 — Accessibility and i18n
21. Add `contentDescription`/`semantics` to every Canvas-based component: `FishSprite`
    ("Clownfish"), `TankSprite` ("Tank 1, 17 of 24 slots used"), `DecorationSprite`,
    `TokenIcon`, `CapacityGauge` (`stateDescription`), and give `ColumnChart` a
    per-bar `contentDescription` ("Monday, 2 hours 30 minutes").
22. Add `Modifier.semantics { liveRegion = Polite }` to `ReefToast` and
    `EarningsMoment`. Add `heading()` to `SectionLabel` and the top-bar title.
23. Convert every count string to a real `<plurals>`: `tank_free_slots`,
    `stats_focus_blocks`, `store_slots`, `tank_decoration_count`, `tank_slots_used`,
    `store_price`, `store_balance`. Delete `store_slots_plural` and the manual
    `slotsText()` helper in `StoreScreen.kt`.
24. Parameterize `store_msg_decoration_limit` with `GameConfig.TANK_DECORATION_CAP`.
25. Change the buy CTA copy from bare `"%1$d tokens"` to an action, e.g.
    `store_buy_price` = `"Buy · %1$d"` with the token icon. Remove the now-unused
    `store_buy`.
26. Add `res/values-es/strings.xml` with a full Spanish translation.

## Phase 8 — Adaptive layout
27. Replace the hardcoded 210dp tank height (`TanksScreen.kt:238`) with a height derived
    from `WindowSizeClass` / available width (e.g. a 16:9-ish aspect ratio, clamped).
28. Add a `values-w600dp` dimension set and switch the Tanks list to a 2-column grid and
    the Store to a 3-column grid at that width. Verify landscape phone doesn't letterbox
    the aquarium.

## Verification (required before reporting done)
- `rtk ./gradlew :app:assembleDebug` and `:app:testDebugUnitTest` both green.
- Launch cold in **dark** mode and confirm there is no white flash.
- Screenshot all four screens in light and dark, portrait and landscape.
- Enable TalkBack and swipe through every screen; every interactive element and every
  sprite must announce something meaningful.
- Set device language to Spanish and confirm no untranslated strings.
- Set a locale/state where n=1 (one free slot, one focus block) and confirm no
  "1 free slots"-style grammar.
- Kill the network (mock repo throws) and confirm every screen shows an error + Retry,
  not an infinite shimmer.
- Write the review section into `tasks/todo.md`.
````

---

## 7. Suggested sequencing if time is limited

If you only do three things, do these — they are ~80% of the perceived jump:

1. **Phase 1** (splash / night theme / monochrome icon) — one afternoon, and it fixes
   the very first thing anyone sees.
2. **Item 7 + 12** (accent hierarchy + card bottom-alignment) — turns the Store from a
   button wall into a catalog.
3. **Phase 4** (error states + disabled reasons) — the difference between a demo and an
   app you can hand to someone.
