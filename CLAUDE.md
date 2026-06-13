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

### Public API — `ui/components/tabrow/`

| File | Purpose |
|---|---|
| `CustomScrollableTabRow.kt` | Main composable — slim orchestrator (~120 lines) |
| `TabDefaults.kt` | Factory object: `colors()`, `style()`, `contentOptions()`, `indicator()`, `pillIndicator()`, etc. |
| `TabColors.kt` | Selected/unselected content and container colors |
| `TabStyle.kt` | Shape, text styles, borders, sizing, and item spacing |
| `TabContentConfig.kt` | What to render (`TabContentConfig`), how to animate it (`TabContentTransition`, `TabContentSwapPolicy`), and sizing (`TabContentOptions`) |
| `TabIndicatorConfig.kt` | Indicator shape variants (`TabIndicatorStyle`), placement, and motion |
| `TabRowMotion.kt` | Row-wide animation presets (`Smooth`, `Snappy`, `Playful`, `None`, `Custom`) |
| `TabItem.kt` | Data model: text, icon, image, contentDescription |

### Internal implementation — same package

| File | Responsibility |
|---|---|
| `TabRowLayout.kt` | `CustomTab`, `IndicatorLayer`, `RippleIndicatorLayer`, `CoordinatedTabContent` composables |
| `TabContentRenderer.kt` | `TabContent`, `IconTextContent`, `ImageTextContent`, `resolveContentStyle` |
| `TabIndicatorPositioner.kt` | Indicator position interpolation (`indicatorPosition`), `TabMeasurement`, `IndicatorPosition`, `snakeBounds` |
| `TabRowUtils.kt` | `lerp`, `lerpColor`, `Modifier.indicatorSurface` extensions, `Float.toDp()` |

### Key design points

- `TabColors` + `TabStyle` replace the old single `TabStyle` class — mirroring the Material3 `ButtonColors` / `ButtonDefaults` pattern.
- `TabContentOptions` groups `transition`, `swapPolicy`, and `iconOnlyHorizontalPadding` so the composable parameter list stays short.
- `TabDefaults` is the single entry point for all configuration — users can start with defaults and override only what they need.
- The indicator position is computed by interpolating `TabMeasurement` entries (tracked via `Modifier.onGloballyPositioned`) using `pagerState.currentPageOffsetFraction`.
- `TabContentTransition.Custom` and `TabRowMotion.Custom` accept arbitrary animation specs for full extensibility.
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
