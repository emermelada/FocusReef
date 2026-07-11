# FocusReef 🐠

A Forest-style Android app that gamifies deep work. Part of a larger standing-desk project:

```
Standing desk ──► NAS database (25-min focus blocks) ──► FocusReef (read-only dashboard + game)
```

Every completed 25-minute focus block earns a token. Tokens buy fish and fish tanks, growing a personal aquarium the more you focus. The app never controls the desk — it only visualizes the history stored on the NAS.

## Features

- **Tanks** — your aquarium: tanks with fish, starting with one 24-slot tank
- **Stats** — study time aggregated weekly, monthly, and annually
- **Store** — spend tokens on new tanks and three fish species (small/medium/large occupying 1/3/5 slots)

## Stack

Kotlin · Jetpack Compose · MVVM · Hilt · Retrofit · Room · Navigation Compose

See [CLAUDE.md](CLAUDE.md) for the full specification and architecture conventions.
