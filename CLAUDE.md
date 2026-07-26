# CLAUDE.md — FocusReef

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FocusReef is a Forest-style Android gamification app and the final piece of a larger productivity project:

```
Standing desk (hardware buttons) ──► NAS database (focus-block history) ──► FocusReef app (read-only)
```

- The desk pushes each completed **25-minute focus block** to a database on the NAS.
- FocusReef connects to that data to **read and visualize** the historical records.
- Every completed focus block earns the user **1 token**. Tokens are spent in an in-app store on fish and fish tanks, building up a personal aquarium over time.

**Explicit non-goal:** the app NEVER sends commands to the desk motors. Local hardware buttons handle desk control natively. FocusReef is a read-only dashboard + game layer.

## Tech Stack

Mirrors the structure and stack of the reference project [ArtCenter](https://github.com/emermelada/ArtCenter):

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3), single-activity
- **Build:** AGP 9.2.1 (built-in Kotlin 2.2.10), Gradle 9.4.1, compileSdk 36, minSdk 26 (java.time without desugaring). KSP for Hilt/Room — Hilt must stay ≥ 2.59.2 for AGP 9 compatibility.
- **Architecture:** MVVM — one `Screen` composable + one `ViewModel` + one `UiState` per feature
- **DI:** Hilt
- **Networking:** Retrofit (NAS REST API) — Gson converter
- **Local persistence:** Room (owned fish, tanks, purchase ledger). DataStore only when preferences appear.
- **Navigation:** Navigation Compose with a bottom navigation bar
- **Images:** Coil (add the dependency when real sprites arrive — not before)
- **Async:** Kotlin Coroutines + `StateFlow`

## Data Architecture

The NAS is the **read-only source of truth for focus blocks** (earned tokens). Everything the user buys is **app-side state stored locally in Room**.

```
availableTokens = tokensEarned(from NAS focus blocks) − tokensSpent(local Room ledger)
```

- NAS access goes through a small REST API in front of the NAS database, consumed with Retrofit.
- Until the NAS API exists, a **mock data source** implements the same repository interface with fake focus-block history. Screens and ViewModels must not know which implementation is active — Hilt swaps them.

## Package / Folder Structure

Follow the ArtCenter layout. Root package: `com.emermeladas.focusreef`.

```
app/src/main/java/com/emermeladas/focusreef/
├── data/
│   ├── model/          # Domain models: FocusBlock, FishSpecies, Fish, Tank, Wallet
│   ├── remote/         # NAS API: Retrofit service, DTOs, mock implementation
│   ├── local/          # Room: database, DAOs, entities (owned fish, tanks, purchases)
│   └── repositories/   # Repository interfaces + implementations
├── di/                 # Hilt modules: DatabaseModule, RepositoryModule. There is no
│                       # NetworkModule: the NAS address is a user setting, so Retrofit
│                       # is built on demand by data/remote/NasClient, keyed on the URL.
├── ui/
│   ├── screens/
│   │   ├── tanks/      # TanksScreen, TanksViewModel, TanksUiState
│   │   ├── stats/      # StatsScreen, StatsViewModel, StatsUiState
│   │   └── store/      # StoreScreen, StoreViewModel, StoreUiState
│   ├── components/     # Reusable: BottomNavBar, FishSprite, TankSprite, TankGrid, StatCard
│   ├── navigation/     # NavHost + route definitions
│   └── theme/          # Color, Theme, Type
├── utils/              # GameConfig (economy constants), date/time helpers
└── MainActivity.kt
```

Rules:
- One responsibility per file.
- Repositories are **interfaces** injected via Hilt; concrete implementations (real/mock) live beside them.
- ViewModels expose a single `StateFlow<UiState>`; composables collect it and render. **No business logic in composables.**

## Game Rules (single source of truth: `utils/GameConfig.kt`)

All economy numbers live as named constants in `GameConfig` so balancing is a one-file change.

- **Earning:** 1 completed 25-min focus block = **1 token**.
- **Tanks:** the user starts with **1 tank**. Each tank has **24 fish slots**. Additional tanks are purchasable in the store.
- **Fish species (3 kinds):**

| Species | Slots occupied | Price (tokens) |
|---------|----------------|----------------|
| Small   | 1              | `GameConfig.SMALL_FISH_PRICE` |
| Medium  | 3              | `GameConfig.MEDIUM_FISH_PRICE` |
| Large   | 5              | `GameConfig.LARGE_FISH_PRICE` |

- A purchase must fail gracefully (disabled button + reason) when the user lacks tokens or free slots.

## Screens (3 windows + bottom navigation bar)

1. **Tanks** — the aquarium view. Shows owned tanks with their fish swimming/placed inside and a slot-usage indicator (e.g., 17/24). This is the home screen.
2. **Stats** — study time visualizations aggregated **weekly, monthly, and annually**, computed from the NAS focus-block history.
3. **Store** — buy new tanks and the 3 fish species. Shows current token balance; items the user cannot afford (tokens or slots) render disabled with the reason.

Bottom navigation bar is always visible and switches between these three.

## Placeholder Sprites Policy

Real sprites are being made by the user. Until then:

- ALL fish/tank art renders through exactly two composables: `FishSprite` and `TankSprite` (in `ui/components/`).
- Placeholders are simple colored shapes sized by species (small/medium/large) — distinct color per species.
- When real sprites arrive, only these two components change. Screens must never draw fish/tank art directly.

## Design System

Defined entirely in `ui/theme/` — change it there, never inline in screens.

- **Color:** complete Material 3 tonal scheme (every role, light + dark) seeded from ocean blue `#00658E`; slate-blue secondary, coral tertiary. Non-scheme brand colors (tank water, sand, token gold, fish placeholders) also live in `Color.kt`. Dynamic color is deliberately disabled.
- **Type:** two bundled variable fonts — **Sora** for display/headline/title, **Inter** for body/label — mapped over the full M3 scale in `Type.kt`.
- **Shape:** rounded scale (6→28dp) in `Theme.kt`.
- **Charts:** single-hue (primary) column bars, baseline hairline in outlineVariant, a labelled gridline at the maximum, the peak's value always visible and tap to move the readout to any other bar — see `ui/components/ColumnChart.kt`.
- **Adaptive layout:** grid column counts are resources, not constants —
  `values/integers.xml` and `values-w600dp/integers.xml`. Screens read them with
  `integerResource(...)` and contain no width arithmetic.
- **Launcher icon:** adaptive vector (fish + waves) in `res/drawable/ic_launcher_*.xml`, consistent with the in-app sprite silhouette.

## Code Conventions

- **Document everything:** KDoc on all public classes, functions, and properties. Explanatory comments inside non-trivial logic.
- Modular and legible over clever. Small files, clear names.
- Prices, capacities, and durations are never hardcoded inline — always `GameConfig` constants.
- Follow the parent-directory workflow rules (plan mode for non-trivial tasks, `tasks/todo.md`, `tasks/lessons.md`, verify before done).
