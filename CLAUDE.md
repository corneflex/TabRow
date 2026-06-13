# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Install on connected device
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Architecture

Single-module Android app (Kotlin + Jetpack Compose) used as a playground for `CustomScrollableTabRow`.

**Entry point:** `MainActivity.kt` — hosts a `HorizontalPager` and an interactive playground for configuring the tab row.

The `ui/components/tabrow/` package is split into four subpackages. Kotlin `internal` visibility is module-scoped, so the `internal/` package stays hidden from consumers while still being reachable across subpackages.

### `tabrow/` (root) — public entry point

| File | Purpose |
|---|---|
| `CustomScrollableTabRow.kt` | Main composable — slim orchestrator (~120 lines) |

### `model/` — pure data, no Compose animation deps

| File | Purpose |
|---|---|
| `TabItem.kt` | Data model: text, icon, image, contentDescription |
| `TabColors.kt` | Selected/unselected content and container colors |
| `TabStyle.kt` | Shape, text styles, borders, sizing, and padding (`horizontalPadding`, `verticalPadding`, `itemSpacing`, `edgePadding`) |

### `config/` — behavior & animation configuration

| File | Purpose |
|---|---|
| `TabContentConfig.kt` | What to render (`TabContentConfig`), swap policy (`TabContentSwapPolicy`), and sizing (`TabContentOptions`) |
| `TabContentTransition.kt` | `AnimatedContent` transitions + `ContentLayerVisual` for coordinated cross-fades; `Custom` open class |
| `TabIndicatorConfig.kt` | Indicator shape variants (`TabIndicatorStyle`), placement, motion spec |
| `TabRowMotion.kt` | Row-wide presets (`Smooth`, `Snappy`, `Playful`, `None`, `Custom`); `IndicatorMotion` (+ `Custom`), `IndicatorMotionScope`, `IndicatorTransform` |

### `defaults/` — public factory & fluent API

| File | Purpose |
|---|---|
| `TabDefaults.kt` | Factory object: `colors()`, `style()`, `contentOptions()`, `indicator()`, `pillIndicator()`, etc. |
| `TabRowExtensions.kt` | Copy-helper extensions (`withMotion`, `withShape`, `toTabItems`, …) |

### `internal/` — implementation details (not public API)

| File | Responsibility |
|---|---|
| `TabRowLayout.kt` | `CustomTab`, `IndicatorLayer`, `RippleIndicatorLayer`, `CoordinatedTabContent` composables |
| `TabContentRenderer.kt` | `TabContent`, `IconTextContent`, `ImageTextContent`, `resolveContentStyle` |
| `TabIndicatorPositioner.kt` | Indicator position interpolation (`indicatorPosition`), `TabMeasurement`, `IndicatorPosition`, `snakeBounds` |
| `TabRowUtils.kt` | `lerp`, `lerpColor`, `Modifier.indicatorSurface` extensions, `Float.toDp()` |

### Key design points

- `TabColors` + `TabStyle` replace the old single `TabStyle` class — mirroring the Material3 `ButtonColors` / `ButtonDefaults` pattern.
- `TabContentOptions` groups `transition`, `swapPolicy`, and icon/image sizing so the composable parameter list stays short.
- `TabDefaults` is the single entry point for all configuration — users can start with defaults and override only what they need. Keep its factory params in sync with the `model`/`config` data classes.
- The indicator position is computed by interpolating `TabMeasurement` entries (tracked via `Modifier.onGloballyPositioned`) using `pagerState.currentPageOffsetFraction`. A single `rememberPagerProgress` drives both the indicator and coordinated content.
- **Three `Custom` extension points** for full extensibility: `TabContentTransition.Custom` (content animation), `IndicatorMotion.Custom` (indicator travel — returns an `IndicatorTransform` per frame from an `IndicatorMotionScope`), and `TabRowMotion.Custom` (row-wide specs).
- New indicator shapes: subclass `TabIndicatorStyle` as a `data class` and implement `shape`, `color`, `border`, `placement`, `horizontalPadding`, and `height`.

### Minimal usage

```kotlin
CustomScrollableTabRow(tabs = tabs, pagerState = pagerState)
```

### Customised usage

```kotlin
CustomScrollableTabRow(
    tabs = tabs,
    pagerState = pagerState,
    colors = TabDefaults.colors(selectedContentColor = Color.Blue),
    style = TabDefaults.style(itemSpacing = 4.dp),
    contentOptions = TabDefaults.contentOptions(transition = TabContentTransition.Slide),
    indicator = TabDefaults.underlineIndicator(),
    motion = TabRowMotion.Playful,
)
```
