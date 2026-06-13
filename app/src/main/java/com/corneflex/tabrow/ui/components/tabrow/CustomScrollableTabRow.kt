package com.corneflex.tabrow.ui.components.tabrow

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import com.corneflex.tabrow.ui.components.tabrow.config.IndicatorMotion
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentConfig
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentOptions
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentSwapPolicy
import com.corneflex.tabrow.ui.components.tabrow.config.TabIndicatorConfig
import com.corneflex.tabrow.ui.components.tabrow.config.TabRowMotion
import com.corneflex.tabrow.ui.components.tabrow.config.hasAdaptiveContent
import com.corneflex.tabrow.ui.components.tabrow.defaults.TabDefaults
import com.corneflex.tabrow.ui.components.tabrow.internal.CustomTab
import com.corneflex.tabrow.ui.components.tabrow.internal.IndicatorLayer
import com.corneflex.tabrow.ui.components.tabrow.internal.TabMeasurement
import com.corneflex.tabrow.ui.components.tabrow.model.TabColors
import com.corneflex.tabrow.ui.components.tabrow.model.TabItem
import com.corneflex.tabrow.ui.components.tabrow.model.TabStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A scrollable tab row driven by a [PagerState].
 *
 * Minimal usage:
 * ```kotlin
 * CustomScrollableTabRow(tabs = tabs, pagerState = pagerState)
 * ```
 *
 * Customised:
 * ```kotlin
 * CustomScrollableTabRow(
 *     tabs = tabs,
 *     pagerState = pagerState,
 *     colors = TabDefaults.colors(selectedContentColor = Color.Blue),
 *     style = TabDefaults.style(itemSpacing = 4.dp),
 *     contentOptions = TabDefaults.contentOptions(transition = TabContentTransition.Slide),
 *     indicator = TabDefaults.underlineIndicator(),
 *     motion = TabRowMotion.Playful,
 * )
 * ```
 *
 * @param tabs Items to display. Each [TabItem] may carry text, icon, or both.
 * @param pagerState Pager state that drives selection and indicator position.
 * @param content What each tab renders (text, icon, image, or adaptive morphing).
 * @param contentOptions Transition animation, swap policy, and icon sizing.
 * @param indicator Visual style and motion of the selection indicator.
 * @param motion Row-wide animation preset (scroll, color, size).
 * @param colors Content and container colors for selected/unselected states.
 * @param style Shape, text styles, borders, and layout sizing.
 * @param onTabClick Override the default pager scroll on tab tap.
 */
@Composable
fun CustomScrollableTabRow(
    tabs: List<TabItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    content: TabContentConfig = TabContentConfig.Text,
    contentOptions: TabContentOptions = TabContentOptions(),
    indicator: TabIndicatorConfig = TabIndicatorConfig(),
    motion: TabRowMotion = TabRowMotion.Smooth,
    colors: TabColors = TabDefaults.colors(),
    style: TabStyle = TabDefaults.style(),
    onTabClick: ((Int) -> Unit)? = null,
) {
    if (tabs.isEmpty()) return

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val tabPositions = remember { mutableStateMapOf<Int, TabMeasurement>() }
    val selectedIndex = pagerState.currentPage.coerceIn(tabs.indices)
    var contentSelectedIndex by remember { mutableStateOf<Int?>(selectedIndex) }
    var viewportWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier = modifier
            .onSizeChanged { viewportWidth = it.width }
            .horizontalScroll(scrollState),
    ) {
        val indicatorMotion = indicator.motion ?: motion.indicatorMotion
        val pagerProgress by rememberPagerProgress(pagerState, tabs.size)
        val indicatorProgress =
            if (indicatorMotion == IndicatorMotion.None) pagerState.currentPage.toFloat() else pagerProgress

        LaunchedEffect(selectedIndex, content, contentOptions.swapPolicy) {
            if (!content.hasAdaptiveContent()) {
                contentSelectedIndex = selectedIndex
                return@LaunchedEffect
            }
            when (val policy = contentOptions.swapPolicy) {
                TabContentSwapPolicy.Coordinated,
                TabContentSwapPolicy.Together -> contentSelectedIndex = selectedIndex
                is TabContentSwapPolicy.DeselectThenSelect -> {
                    contentSelectedIndex = null
                    delay(policy.delayMillis)
                    contentSelectedIndex = selectedIndex
                }
            }
        }

        LaunchedEffect(selectedIndex, tabPositions.toMap(), viewportWidth, motion) {
            val tab = tabPositions[selectedIndex] ?: return@LaunchedEffect
            val target = (tab.center - viewportWidth / 2)
                .roundToInt()
                .coerceIn(0, scrollState.maxValue)
            if (indicatorMotion == IndicatorMotion.None || motion == TabRowMotion.None) {
                scrollState.scrollTo(target)
            } else {
                scrollState.animateScrollTo(target, animationSpec = motion.scrollSpec)
            }
        }

        IndicatorLayer(
            progress = indicatorProgress,
            tabPositions = tabPositions,
            indicator = indicator,
            motion = indicatorMotion,
        )

        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(style.edgePadding))
            tabs.forEachIndexed { index, tab ->
                val selectionFraction = coordinatedFraction(
                    index = index,
                    pageProgress = pagerProgress,
                    content = content,
                    policy = contentOptions.swapPolicy,
                )
                CustomTab(
                    item = tab,
                    selected = index == selectedIndex,
                    contentSelected = index == contentSelectedIndex,
                    selectionFraction = selectionFraction,
                    content = content,
                    contentOptions = contentOptions,
                    indicatorStyle = indicator.style,
                    colors = colors,
                    style = style,
                    motion = motion,
                    onPositioned = { coords ->
                        val pos = coords.positionInParent()
                        tabPositions[index] = TabMeasurement(
                            left = pos.x,
                            width = coords.size.width.toFloat(),
                        )
                    },
                    onClick = {
                        if (onTabClick != null) onTabClick(index)
                        else scope.launch {
                            pagerState.animateScrollToPageSmoothly(
                                page = index,
                                animationSpec = motion.scrollSpec,
                            )
                        }
                    },
                )
                if (index != tabs.lastIndex) {
                    Spacer(modifier = Modifier.width(style.itemSpacing))
                }
            }
            Spacer(modifier = Modifier.width(style.edgePadding))
        }
    }
}

// ─── Private helpers ──────────────────────────────────────────────────────────

/** Continuous `[0, tabCount - 1]` position of the pager, tracking page + swipe offset. */
@Composable
private fun rememberPagerProgress(
    pagerState: PagerState,
    tabCount: Int,
): State<Float> = remember(pagerState, tabCount) {
    derivedStateOf {
        (pagerState.currentPage + pagerState.currentPageOffsetFraction)
            .coerceIn(0f, (tabCount - 1).coerceAtLeast(0).toFloat())
    }
}

private fun coordinatedFraction(
    index: Int,
    pageProgress: Float,
    content: TabContentConfig,
    policy: TabContentSwapPolicy,
): Float? {
    if (policy != TabContentSwapPolicy.Coordinated || !content.hasAdaptiveContent()) return null
    return (1f - abs(index - pageProgress)).coerceIn(0f, 1f)
}

@OptIn(ExperimentalFoundationApi::class)
private suspend fun PagerState.animateScrollToPageSmoothly(
    page: Int,
    animationSpec: AnimationSpec<Float>,
) {
    if (pageCount == 0) return

    val targetPage = page.coerceIn(0, pageCount - 1)
    if (targetPage == currentPage && currentPageOffsetFraction == 0f) return

    val pageSizeWithSpacing = layoutInfo.pageSize + layoutInfo.pageSpacing
    if (pageSizeWithSpacing == 0) {
        scrollToPage(targetPage)
        return
    }

    scroll {
        updateTargetPage(targetPage)
        val displacement = getOffsetDistanceInPages(targetPage) * pageSizeWithSpacing
        var previousValue = 0f

        animate(0f, displacement, animationSpec = animationSpec) { currentValue, _ ->
            val consumed = scrollBy(currentValue - previousValue)
            previousValue += consumed
        }
    }
}
