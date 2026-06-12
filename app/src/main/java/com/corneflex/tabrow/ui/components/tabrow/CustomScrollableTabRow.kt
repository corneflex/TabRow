package com.corneflex.tabrow.ui.components.tabrow

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun CustomScrollableTabRow(
    tabs: List<TabItem>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    content: TabContentConfig = TabContentConfig.Text,
    contentMetrics: TabContentMetrics = TabContentMetrics(),
    contentTransition: TabContentTransition = TabContentTransition.FadeScale,
    contentSwapPolicy: TabContentSwapPolicy = TabContentSwapPolicy.Coordinated,
    indicator: TabIndicatorConfig = TabIndicatorConfig(),
    motion: TabRowMotion = TabRowMotion.Smooth,
    tabStyle: TabStyle = TabStyle.default(
        selectedContentColor = MaterialTheme.colorScheme.onPrimary,
        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ),
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
        val progress by rememberIndicatorProgress(pagerState, tabs.size, indicatorMotion)
        val contentProgress by rememberPagerProgress(pagerState, tabs.size)

        LaunchedEffect(selectedIndex, content, contentSwapPolicy) {
            if (!content.hasSelectionSpecificContent()) {
                contentSelectedIndex = selectedIndex
                return@LaunchedEffect
            }

            when (contentSwapPolicy) {
                TabContentSwapPolicy.Coordinated -> contentSelectedIndex = selectedIndex
                TabContentSwapPolicy.Together -> contentSelectedIndex = selectedIndex
                is TabContentSwapPolicy.DeselectThenSelect -> {
                    contentSelectedIndex = null
                    delay(contentSwapPolicy.delayMillis)
                    contentSelectedIndex = selectedIndex
                }
            }
        }

        LaunchedEffect(selectedIndex, tabPositions.toMap(), viewportWidth, motion) {
            val selectedTab = tabPositions[selectedIndex] ?: return@LaunchedEffect
            val centeredScroll = selectedTab.center - viewportWidth / 2
            val targetScroll = centeredScroll.roundToInt()
                .coerceIn(0, scrollState.maxValue)

            if (indicatorMotion == IndicatorMotion.None || motion == TabRowMotion.None) {
                scrollState.scrollTo(targetScroll)
            } else {
                scrollState.animateScrollTo(
                    value = targetScroll,
                    animationSpec = motion.scrollSpec,
                )
            }
        }

        IndicatorLayer(
            progress = progress,
            tabPositions = tabPositions,
            indicator = indicator,
            motion = indicatorMotion,
        )

        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            tabs.forEachIndexed { index, tab ->
                val selectionFraction = coordinatedSelectionFraction(
                    index = index,
                    pageProgress = contentProgress,
                    content = content,
                    policy = contentSwapPolicy,
                )
                CustomTab(
                    item = tab,
                    selected = index == selectedIndex,
                    contentSelected = index == contentSelectedIndex,
                    selectionFraction = selectionFraction,
                    content = content,
                    contentMetrics = contentMetrics,
                    contentTransition = contentTransition,
                    indicatorStyle = indicator.style,
                    style = tabStyle,
                    motion = motion,
                    onPositioned = { coordinates ->
                        val position = coordinates.positionInParent()
                        tabPositions[index] = TabMeasurement(
                            left = position.x,
                            width = coordinates.size.width.toFloat(),
                        )
                    },
                    onClick = {
                        if (onTabClick != null) {
                            onTabClick(index)
                        } else {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        }
                    },
                )
                if (index != tabs.lastIndex) {
                    Spacer(modifier = Modifier.width(tabStyle.itemSpacing))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
private fun rememberIndicatorProgress(
    pagerState: PagerState,
    tabCount: Int,
    motion: IndicatorMotion,
): androidx.compose.runtime.State<Float> {
    val rawProgress by remember {
        derivedStateOf {
            (pagerState.currentPage + pagerState.currentPageOffsetFraction)
                .coerceIn(0f, (tabCount - 1).coerceAtLeast(0).toFloat())
        }
    }

    return if (motion == IndicatorMotion.None) {
        remember { derivedStateOf { pagerState.currentPage.toFloat() } }
    } else {
        remember { derivedStateOf { rawProgress } }
    }
}

@Composable
private fun rememberPagerProgress(
    pagerState: PagerState,
    tabCount: Int,
): androidx.compose.runtime.State<Float> {
    return remember {
        derivedStateOf {
            (pagerState.currentPage + pagerState.currentPageOffsetFraction)
                .coerceIn(0f, (tabCount - 1).coerceAtLeast(0).toFloat())
        }
    }
}

private fun coordinatedSelectionFraction(
    index: Int,
    pageProgress: Float,
    content: TabContentConfig,
    policy: TabContentSwapPolicy,
): Float? {
    if (policy != TabContentSwapPolicy.Coordinated || !content.hasSelectionSpecificContent()) {
        return null
    }

    return (1f - abs(index - pageProgress)).coerceIn(0f, 1f)
}

@Composable
private fun BoxScope.IndicatorLayer(
    progress: Float,
    tabPositions: Map<Int, TabMeasurement>,
    indicator: TabIndicatorConfig,
    motion: IndicatorMotion,
) {
    val density = LocalDensity.current
    val position = indicatorPosition(
        progress = progress,
        tabPositions = tabPositions,
        style = indicator.style,
        motion = motion,
        motionSpec = indicator.motionSpec,
        minWidthPx = with(density) {
            indicator.style.minimumUsefulWidth.toPx()
        },
        horizontalPaddingPx = with(density) {
            indicator.style.horizontalPadding.toPx()
        },
    ) ?: return

    val style = indicator.style
    val alignment = when (style.placement) {
        IndicatorPlacement.Top -> Alignment.TopStart
        IndicatorPlacement.Bottom -> Alignment.BottomStart
        IndicatorPlacement.BehindContent -> Alignment.CenterStart
    }

    Box(
        modifier = Modifier
            .align(alignment)
            .offset { IntOffset(position.left.roundToInt(), 0) }
            .width(position.width.toDp())
            .height(style.height)
            .scale(position.scale)
            .alpha(position.alpha)
            .indicatorSurface(style),
    )
}

@Composable
private fun CustomTab(
    item: TabItem,
    selected: Boolean,
    contentSelected: Boolean,
    selectionFraction: Float?,
    content: TabContentConfig,
    contentMetrics: TabContentMetrics,
    contentTransition: TabContentTransition,
    indicatorStyle: TabIndicatorStyle,
    style: TabStyle,
    motion: TabRowMotion,
    modifier: Modifier = Modifier,
    onPositioned: (LayoutCoordinates) -> Unit,
    onClick: () -> Unit,
) {
    val visualSelection = selectionFraction ?: if (selected) 1f else 0f
    val animatedSelectionProgress by animateFloatAsState(
        targetValue = visualSelection,
        animationSpec = motion.colorSpec,
        label = "Tab selection progress",
    )
    val selectedProgress = selectionFraction ?: animatedSelectionProgress
    val contentColor = lerpColor(
        start = style.unselectedContentColor,
        stop = style.selectedContentColor,
        fraction = selectedProgress,
    )
    val containerColor = lerpColor(
        start = style.unselectedContainerColor,
        stop = style.selectedContainerColor,
        fraction = selectedProgress,
    )
    val border = if (selected) style.selectedBorder else style.unselectedBorder
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .then(if (selectionFraction == null) Modifier.animateContentSize(animationSpec = motion.sizeSpec) else Modifier)
            .onGloballyPositioned(onPositioned)
            .defaultMinSize(minHeight = style.minHeight)
            .indicatorSurface(
                color = containerColor,
                shape = style.shape,
                border = border,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .semantics { this.selected = selected },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = style.horizontalPadding, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (selectionFraction != null && content is TabContentConfig.Adaptive) {
                CoordinatedTabContent(
                    item = item,
                    unselectedStyle = item.resolveContentStyle(content.unselected),
                    selectedStyle = item.resolveContentStyle(content.selected),
                    unselectedColor = style.unselectedContentColor,
                    selectedColor = style.selectedContentColor,
                    fraction = selectionFraction,
                    transition = contentTransition,
                    metrics = contentMetrics,
                )
            } else {
                AnimatedContent(
                    targetState = item.resolveContentStyle(content.styleFor(contentSelected)),
                    transitionSpec = contentTransition.contentTransform(motion.sizeSpec),
                    contentAlignment = Alignment.Center,
                    label = "Tab content",
                ) { contentStyle ->
                    TabContent(
                        item = item,
                        style = contentStyle,
                        contentColor = contentColor,
                        metrics = contentMetrics,
                    )
                }
            }
        }

        RippleIndicatorLayer(
            indicatorStyle = indicatorStyle,
            interactionSource = interactionSource,
        )
    }
}

@Composable
private fun BoxScope.RippleIndicatorLayer(
    indicatorStyle: TabIndicatorStyle,
    interactionSource: MutableInteractionSource,
) {
    val alignment = when (indicatorStyle.placement) {
        IndicatorPlacement.Top -> Alignment.TopCenter
        IndicatorPlacement.Bottom -> Alignment.BottomCenter
        IndicatorPlacement.BehindContent -> Alignment.Center
    }

    BoxWithConstraints(
        modifier = Modifier.matchParentSize(),
        contentAlignment = alignment,
    ) {
        val sizeModifier = when (indicatorStyle) {
            is TabIndicatorStyle.Dot -> Modifier.size(indicatorStyle.size)
            else -> Modifier
                .fillMaxWidth()
                .padding(horizontal = indicatorStyle.effectiveHorizontalPadding(maxWidth))
                .height(indicatorStyle.height)
        }

        Box(
            modifier = Modifier
                .wrapContentSize(alignment)
                .then(sizeModifier)
                .clip(indicatorStyle.shape)
                .indication(interactionSource, LocalIndication.current),
        )
    }
}

@Composable
private fun CoordinatedTabContent(
    item: TabItem,
    unselectedStyle: TabContentStyle,
    selectedStyle: TabContentStyle,
    unselectedColor: Color,
    selectedColor: Color,
    fraction: Float,
    transition: TabContentTransition,
    metrics: TabContentMetrics,
) {
    val progress = fraction.coerceIn(0f, 1f)
    val unselectedVisual = transition.layerVisual(
        progress = 1f - progress,
        entering = false,
    )
    val selectedVisual = transition.layerVisual(
        progress = progress,
        entering = true,
    )

    Layout(
        content = {
            Box(
                modifier = Modifier.graphicsLayer(
                    alpha = unselectedVisual.alpha,
                    scaleX = unselectedVisual.scale,
                    scaleY = unselectedVisual.scale,
                ),
                contentAlignment = Alignment.Center,
            ) {
                TabContent(
                    item = item,
                    style = unselectedStyle,
                    contentColor = unselectedColor,
                    metrics = metrics,
                )
            }
            Box(
                modifier = Modifier.graphicsLayer(
                    alpha = selectedVisual.alpha,
                    scaleX = selectedVisual.scale,
                    scaleY = selectedVisual.scale,
                ),
                contentAlignment = Alignment.Center,
            ) {
                TabContent(
                    item = item,
                    style = selectedStyle,
                    contentColor = selectedColor,
                    metrics = metrics,
                )
            }
        },
    ) { measurables, constraints ->
        val relaxedConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val unselectedPlaceable = measurables[0].measure(relaxedConstraints)
        val selectedPlaceable = measurables[1].measure(relaxedConstraints)
        val width = lerp(
            start = unselectedPlaceable.width.toFloat(),
            stop = selectedPlaceable.width.toFloat(),
            fraction = progress,
        ).roundToInt().coerceAtLeast(0)
        val height = maxOf(unselectedPlaceable.height, selectedPlaceable.height)

        layout(width, height) {
            val unselectedX = (width - unselectedPlaceable.width) / 2 +
                (unselectedVisual.offsetXFactor * width).roundToInt()
            val selectedX = (width - selectedPlaceable.width) / 2 +
                (selectedVisual.offsetXFactor * width).roundToInt()
            val unselectedY = (height - unselectedPlaceable.height) / 2 +
                (unselectedVisual.offsetYFactor * height).roundToInt()
            val selectedY = (height - selectedPlaceable.height) / 2 +
                (selectedVisual.offsetYFactor * height).roundToInt()

            unselectedPlaceable.placeRelative(unselectedX, unselectedY)
            selectedPlaceable.placeRelative(selectedX, selectedY)
        }
    }
}

private fun TabContentTransition.layerVisual(
    progress: Float,
    entering: Boolean,
): ContentLayerVisual {
    val p = progress.coerceIn(0f, 1f)

    return when (this) {
        TabContentTransition.None -> ContentLayerVisual(
            alpha = if (p >= 0.5f) 1f else 0f,
        )
        TabContentTransition.Fade -> ContentLayerVisual(alpha = p)
        TabContentTransition.FadeScale -> ContentLayerVisual(
            alpha = p,
            scale = lerp(0.92f, 1f, p),
        )
        TabContentTransition.Scale -> ContentLayerVisual(
            alpha = if (p >= 0.5f) 1f else 0f,
            scale = lerp(0.88f, 1f, p),
        )
        TabContentTransition.Slide,
        TabContentTransition.SlideLeft -> ContentLayerVisual(
            alpha = p,
            offsetXFactor = if (entering) (1f - p) * 0.35f else -(1f - p) * 0.35f,
        )
        TabContentTransition.SlideRight -> ContentLayerVisual(
            alpha = p,
            offsetXFactor = if (entering) -(1f - p) * 0.35f else (1f - p) * 0.35f,
        )
        TabContentTransition.SlideUp -> ContentLayerVisual(
            alpha = p,
            offsetYFactor = if (entering) (1f - p) * 0.35f else -(1f - p) * 0.35f,
        )
        TabContentTransition.SlideDown -> ContentLayerVisual(
            alpha = p,
            offsetYFactor = if (entering) -(1f - p) * 0.35f else (1f - p) * 0.35f,
        )
        TabContentTransition.FadeThrough -> ContentLayerVisual(
            alpha = if (entering) ((p - 0.5f) * 2f).coerceIn(0f, 1f) else (p * 2f).coerceIn(0f, 1f),
        )
        TabContentTransition.Expand -> ContentLayerVisual(alpha = 1f)
        TabContentTransition.ExpandFade -> ContentLayerVisual(alpha = p)
        is TabContentTransition.Custom -> ContentLayerVisual(alpha = p)
    }
}

private data class ContentLayerVisual(
    val alpha: Float,
    val scale: Float = 1f,
    val offsetXFactor: Float = 0f,
    val offsetYFactor: Float = 0f,
)

@Composable
private fun TabContent(
    item: TabItem,
    style: TabContentStyle,
    contentColor: Color,
    metrics: TabContentMetrics,
) {
    when (style) {
        TabContentStyle.Text -> item.text?.let {
            Text(
                text = it,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
            )
        }

        TabContentStyle.Icon -> item.icon?.let {
            Icon(
                imageVector = it,
                contentDescription = item.contentDescription,
                tint = contentColor,
                modifier = Modifier
                    .padding(horizontal = metrics.iconOnlyHorizontalPadding)
                    .size(20.dp),
            )
        }

        TabContentStyle.Image -> item.image?.let {
            Image(
                painter = it,
                contentDescription = item.contentDescription,
                modifier = Modifier.size(24.dp),
            )
        }

        TabContentStyle.IconText -> IconTextContent(item, contentColor)
        TabContentStyle.ImageText -> ImageTextContent(item, contentColor)
    }
}

@Composable
private fun IconTextContent(item: TabItem, contentColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item.icon?.let {
            Icon(
                imageVector = it,
                contentDescription = item.contentDescription,
                tint = contentColor,
                modifier = Modifier.size(18.dp),
            )
        }
        item.text?.let {
            Text(
                text = it,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
private fun ImageTextContent(item: TabItem, contentColor: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        item.image?.let {
            Image(
                painter = it,
                contentDescription = item.contentDescription,
                modifier = Modifier.size(24.dp),
            )
        }
        item.text?.let {
            Text(
                text = it,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

private fun indicatorPosition(
    progress: Float,
    tabPositions: Map<Int, TabMeasurement>,
    style: TabIndicatorStyle,
    motion: IndicatorMotion,
    motionSpec: IndicatorMotionSpec,
    minWidthPx: Float,
    horizontalPaddingPx: Float,
): IndicatorPosition? {
    val fromIndex = floor(progress).toInt()
    val toIndex = ceil(progress).toInt()
    val from = tabPositions[fromIndex] ?: return null
    val to = tabPositions[toIndex] ?: from
    val fraction = progress - fromIndex

    val fromBounds = from.indicatorBounds(
        horizontalPaddingPx = horizontalPaddingPx,
        minimumUsefulWidthPx = minWidthPx,
    )
    val toBounds = to.indicatorBounds(
        horizontalPaddingPx = horizontalPaddingPx,
        minimumUsefulWidthPx = minWidthPx,
    )
    val fromLeft = fromBounds.left
    val fromRight = fromBounds.right
    val toLeft = toBounds.left
    val toRight = toBounds.right

    val (left, right) = when (motion) {
        IndicatorMotion.Snake -> snakeBounds(fromLeft, fromRight, toLeft, toRight, fraction)
        else -> {
            val currentLeft = lerp(fromLeft, toLeft, fraction)
            val currentRight = lerp(fromRight, toRight, fraction)
            currentLeft to currentRight
        }
    }

    val bounce = if (motion == IndicatorMotion.Bounce) {
        1f + (sin(fraction * PI).toFloat() * (motionSpec.bounceScale - 1f))
    } else {
        1f
    }
    val alpha = if (motion == IndicatorMotion.Fade) {
        motionSpec.fadeMinAlpha + kotlin.math.abs(fraction - 0.5f) * (2f * (1f - motionSpec.fadeMinAlpha))
    } else {
        1f
    }

    val width = if (style is TabIndicatorStyle.Dot) {
        style.size.value
    } else {
        (right - left).coerceAtLeast(0f)
    }

    return IndicatorPosition(
        left = if (style is TabIndicatorStyle.Dot) lerp(from.center, to.center, fraction) - width / 2f else left,
        width = width,
        scale = bounce,
        alpha = alpha.coerceIn(0f, 1f),
    )
}

private fun TabMeasurement.indicatorBounds(
    horizontalPaddingPx: Float,
    minimumUsefulWidthPx: Float,
): IndicatorBounds {
    val maxInset = ((width - minimumUsefulWidthPx) / 2f)
        .coerceAtLeast(0f)
    val inset = horizontalPaddingPx.coerceIn(0f, maxInset)
    return IndicatorBounds(
        left = left + inset,
        right = right - inset,
    )
}

private val TabIndicatorStyle.minimumUsefulWidth: Dp
    get() = when (this) {
        is TabIndicatorStyle.SideRoundedBorder -> height
        else -> 0.dp
    }

private fun TabIndicatorStyle.effectiveHorizontalPadding(availableWidth: Dp): Dp {
    val maxPadding = ((availableWidth - minimumUsefulWidth) / 2f)
        .coerceAtLeast(0.dp)
    return horizontalPadding.coerceAtMost(maxPadding)
}

private data class IndicatorBounds(
    val left: Float,
    val right: Float,
)

private fun snakeBounds(
    fromLeft: Float,
    fromRight: Float,
    toLeft: Float,
    toRight: Float,
    fraction: Float,
): Pair<Float, Float> {
    return if (fraction < 0.5f) {
        fromLeft to lerp(fromRight, toRight, fraction * 2f)
    } else {
        lerp(fromLeft, toLeft, (fraction - 0.5f) * 2f) to toRight
    }
}

private fun Modifier.indicatorSurface(
    color: Color,
    shape: Shape,
    border: BorderStroke?,
): Modifier {
    return this
        .background(color = color, shape = shape)
        .then(if (border != null) Modifier.border(border, shape) else Modifier)
}

private fun Modifier.indicatorSurface(style: TabIndicatorStyle): Modifier {
    return if (style is TabIndicatorStyle.TopBottomBorder) {
        this
            .background(color = style.color, shape = style.shape)
            .drawBehind {
                val strokeWidth = style.lineWidth.toPx()
                val strokeCenter = strokeWidth / 2f
                drawLine(
                    color = style.lineColor,
                    start = Offset(0f, strokeCenter),
                    end = Offset(size.width, strokeCenter),
                    strokeWidth = strokeWidth,
                )
                drawLine(
                    color = style.lineColor,
                    start = Offset(0f, size.height - strokeCenter),
                    end = Offset(size.width, size.height - strokeCenter),
                    strokeWidth = strokeWidth,
                )
            }
    } else {
        indicatorSurface(
            color = style.color,
            shape = style.shape,
            border = style.border,
        )
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction.coerceIn(0f, 1f)
}

private fun lerpColor(start: Color, stop: Color, fraction: Float): Color {
    return Color(
        red = lerp(start.red, stop.red, fraction),
        green = lerp(start.green, stop.green, fraction),
        blue = lerp(start.blue, stop.blue, fraction),
        alpha = lerp(start.alpha, stop.alpha, fraction),
    )
}

private fun TabItem.resolveContentStyle(preferred: TabContentStyle): TabContentStyle {
    val hasText = text != null
    val hasIcon = icon != null
    val hasImage = image != null

    return when {
        preferred == TabContentStyle.Text && hasText -> preferred
        preferred == TabContentStyle.Icon && hasIcon -> preferred
        preferred == TabContentStyle.Image && hasImage -> preferred
        preferred == TabContentStyle.IconText && hasIcon && hasText -> preferred
        preferred == TabContentStyle.ImageText && hasImage && hasText -> preferred
        hasText -> TabContentStyle.Text
        hasIcon -> TabContentStyle.Icon
        hasImage -> TabContentStyle.Image
        else -> TabContentStyle.Text
    }
}

@Composable
private fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}

private data class TabMeasurement(
    val left: Float,
    val width: Float,
) {
    val right: Float = left + width
    val center: Float = left + width / 2f
}

private data class IndicatorPosition(
    val left: Float,
    val width: Float,
    val scale: Float,
    val alpha: Float,
)
