package com.corneflex.tabrow.ui.components.tabrow.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─── What each tab renders ────────────────────────────────────────────────────

sealed interface TabContentStyle {
    data object Text : TabContentStyle
    data object Icon : TabContentStyle
    data object Image : TabContentStyle
    data object IconText : TabContentStyle
    data object ImageText : TabContentStyle
}

sealed interface TabContentConfig {
    data object Text : TabContentConfig
    data object Icon : TabContentConfig
    data object Image : TabContentConfig
    data object IconText : TabContentConfig
    data object ImageText : TabContentConfig

    /** Morphs between [unselected] and [selected] content as the pager scrolls. */
    data class Adaptive(
        val unselected: TabContentStyle,
        val selected: TabContentStyle,
    ) : TabContentConfig
}

// ─── How selection changes are presented ─────────────────────────────────────

/** Controls when selected/unselected content swaps during a page transition. */
sealed class TabContentSwapPolicy {
    /** Cross-fades selected and unselected content continuously with pager progress. */
    data object Coordinated : TabContentSwapPolicy()
    /** Both tabs update simultaneously when the page settles. */
    data object Together : TabContentSwapPolicy()
    /** Deselect first, wait [delayMillis], then select the new tab. */
    data class DeselectThenSelect(val delayMillis: Long = 140L) : TabContentSwapPolicy()
}

/**
 * Groups all content-animation options so the main composable parameter list stays short.
 *
 * @param transition How content animates when the active tab changes.
 * @param swapPolicy When selected/unselected content is swapped.
 * @param iconOnlyHorizontalPadding Extra horizontal padding around icon-only tabs.
 */
data class TabContentOptions(
    val transition: TabContentTransition = TabContentTransition.FadeScale,
    val swapPolicy: TabContentSwapPolicy = TabContentSwapPolicy.Coordinated,
    val iconOnlyHorizontalPadding: Dp = 8.dp,
)

// ─── Internal helpers ─────────────────────────────────────────────────────────

internal fun TabContentConfig.styleFor(selected: Boolean): TabContentStyle = when (this) {
    TabContentConfig.Text -> TabContentStyle.Text
    TabContentConfig.Icon -> TabContentStyle.Icon
    TabContentConfig.Image -> TabContentStyle.Image
    TabContentConfig.IconText -> TabContentStyle.IconText
    TabContentConfig.ImageText -> TabContentStyle.ImageText
    is TabContentConfig.Adaptive -> if (selected) this.selected else this.unselected
}

internal fun TabContentConfig.hasAdaptiveContent(): Boolean =
    this is TabContentConfig.Adaptive && selected != unselected
