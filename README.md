# TabRow

A Jetpack Compose playground for a custom scrollable tab row linked to `PagerState`.

![TabRow playground screenshot](docs/screenshot.png)

## Highlights

- Scrollable tab row with pager-driven selection.
- Content modes for text, icons, images, icon + text, and adaptive selected/unselected content.
- Coordinated content transitions for text-to-icon and icon-to-text states.
- Customizable indicators: pill, rectangle, underline, dash, dot, border, side-rounded border, and top/bottom border.
- Indicator motion presets including slide, snake, bounce, fade, and none.
- Configurable tab spacing, tab padding, indicator padding, placement, color, border, fill, and shape.
- Interactive playground with presets for quick visual testing.

## Core API

```kotlin
CustomScrollableTabRow(
    tabs = tabs,
    pagerState = pagerState,
    content = TabContentConfig.Adaptive(
        unselected = TabContentStyle.Text,
        selected = TabContentStyle.Icon,
    ),
    contentMetrics = TabContentMetrics(
        iconOnlyHorizontalPadding = 8.dp,
    ),
    contentTransition = TabContentTransition.FadeScale,
    contentSwapPolicy = TabContentSwapPolicy.Coordinated,
    indicator = TabIndicatorConfig(
        style = TabIndicatorStyle.SideRoundedBorder(
            border = BorderStroke(1.dp, Color.Black),
            horizontalPadding = 8.dp,
        ),
        motion = IndicatorMotion.Slide,
    ),
    motion = TabRowMotion.Smooth,
    tabStyle = TabStyle.default(
        selectedContentColor = Color.Black,
        unselectedContentColor = Color.Gray,
    ).copy(
        selectedTextStyle = MaterialTheme.typography.titleMedium,
        unselectedTextStyle = MaterialTheme.typography.labelLarge,
    ),
)
```

## Run

```bash
./gradlew assembleDebug
```

Install the debug APK on a connected Android device or emulator:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Project

The reusable tab row implementation lives in:

- `app/src/main/java/com/corneflex/tabrow/ui/components/tabrow/CustomScrollableTabRow.kt`
- `app/src/main/java/com/corneflex/tabrow/ui/components/tabrow/TabContentConfig.kt`
- `app/src/main/java/com/corneflex/tabrow/ui/components/tabrow/TabIndicatorConfig.kt`
- `app/src/main/java/com/corneflex/tabrow/ui/components/tabrow/TabRowMotion.kt`
