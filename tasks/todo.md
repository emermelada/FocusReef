# FocusReef — Iteration 2: tank management + fish animation

- [x] Store: choose destination tank when buying a fish (TankPickerDialog; skipped when only one tank has room)
- [x] Tank detail dialog on tap: per-species counts + Move action
- [x] Move fish between tanks (AquariumRepository.moveFish, validated: destination space, stale-UI guard)
- [x] Fish wander the tank (Animatable x/y per fish, random targets, direction-aware flip)
- [ ] Verify build + tests, commit, push

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
- NAS API: bind `RemoteFocusHistoryRepository` in RepositoryModule + set base
  URL in NetworkModule.
