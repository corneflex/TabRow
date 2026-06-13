package com.corneflex.tabrow.ui.components.tabrow.internal

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import com.corneflex.tabrow.ui.components.tabrow.config.IndicatorMotion
import com.corneflex.tabrow.ui.components.tabrow.config.IndicatorPlacement
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentConfig
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentOptions
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentStyle
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentTransition
import com.corneflex.tabrow.ui.components.tabrow.config.TabIndicatorConfig
import com.corneflex.tabrow.ui.components.tabrow.config.TabIndicatorStyle
import com.corneflex.tabrow.ui.components.tabrow.config.TabRowMotion
import com.corneflex.tabrow.ui.components.tabrow.config.contentTransform
import com.corneflex.tabrow.ui.components.tabrow.config.layerVisual
import com.corneflex.tabrow.ui.components.tabrow.config.styleFor
import com.corneflex.tabrow.ui.components.tabrow.model.TabColors
import com.corneflex.tabrow.ui.components.tabrow.model.TabItem
import com.corneflex.tabrow.ui.components.tabrow.model.TabStyle
import androidx.compose.ui.text.lerp as lerpTextStyle
import kotlin.math.roundToInt

@Composable
internal fun BoxScope.IndicatorLayer(
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
        minWidthPx = with(density) { indicator.style.minimumUsefulWidth.toPx() },
        horizontalPaddingPx = with(density) { indicator.style.horizontalPadding.toPx() },
        dotSizePx = if (indicator.style is TabIndicatorStyle.Dot) {
            with(density) { (indicator.style as TabIndicatorStyle.Dot).size.toPx() }
        } else 0f,
    ) ?: return

    val alignment = when (indicator.style.placement) {
        IndicatorPlacement.Top -> Alignment.TopStart
        IndicatorPlacement.Bottom -> Alignment.BottomStart
        IndicatorPlacement.BehindContent -> Alignment.CenterStart
    }

    Box(
        modifier = Modifier
            .align(alignment)
            .offset { IntOffset(position.left.roundToInt(), 0) }
            .width(position.width.toDp())
            .height(indicator.style.height)
            .scale(position.scale)
            .alpha(position.alpha)
            .indicatorSurface(indicator.style),
    )
}

@Composable
internal fun CustomTab(
    item: TabItem,
    selected: Boolean,
    contentSelected: Boolean,
    selectionFraction: Float?,
    content: TabContentConfig,
    contentOptions: TabContentOptions,
    indicatorStyle: TabIndicatorStyle,
    colors: TabColors,
    style: TabStyle,
    motion: TabRowMotion,
    modifier: Modifier = Modifier,
    onPositioned: (LayoutCoordinates) -> Unit,
    onClick: () -> Unit,
) {
    val visualSelection = selectionFraction ?: if (selected) 1f else 0f
    val animatedSelection by animateFloatAsState(
        targetValue = visualSelection,
        animationSpec = motion.colorSpec,
        label = "Tab selection",
    )
    val selectedProgress = selectionFraction ?: animatedSelection

    val contentColor = lerpColor(colors.unselectedContentColor, colors.selectedContentColor, selectedProgress)
    val containerColor = lerpColor(colors.unselectedContainerColor, colors.selectedContainerColor, selectedProgress)

    val unselectedTextStyle: TextStyle = style.unselectedTextStyle ?: MaterialTheme.typography.labelLarge
    val selectedTextStyle: TextStyle = style.selectedTextStyle ?: unselectedTextStyle
    val textStyle = lerpTextStyle(unselectedTextStyle, selectedTextStyle, selectedProgress)

    val border = if (selected) style.selectedBorder else style.unselectedBorder
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .then(
                if (selectionFraction == null) Modifier.animateContentSize(animationSpec = motion.sizeSpec)
                else Modifier
            )
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
            modifier = Modifier.padding(horizontal = style.horizontalPadding, vertical = style.verticalPadding),
            contentAlignment = Alignment.Center,
        ) {
            if (selectionFraction != null && content is TabContentConfig.Adaptive) {
                CoordinatedTabContent(
                    item = item,
                    unselectedStyle = item.resolveContentStyle(content.unselected),
                    selectedStyle = item.resolveContentStyle(content.selected),
                    colors = colors,
                    unselectedTextStyle = unselectedTextStyle,
                    selectedTextStyle = selectedTextStyle,
                    fraction = selectionFraction,
                    transition = contentOptions.transition,
                    options = contentOptions,
                )
            } else {
                AnimatedContent(
                    targetState = item.resolveContentStyle(content.styleFor(contentSelected)),
                    transitionSpec = contentOptions.transition.contentTransform(motion.sizeSpec),
                    contentAlignment = Alignment.Center,
                    label = "Tab content",
                ) { contentStyle ->
                    TabContent(
                        item = item,
                        style = contentStyle,
                        contentColor = contentColor,
                        textStyle = textStyle,
                        options = contentOptions,
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
        val sizeModifier = if (indicatorStyle is TabIndicatorStyle.Dot) {
            Modifier.size(indicatorStyle.size)
        } else {
            Modifier
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
internal fun CoordinatedTabContent(
    item: TabItem,
    unselectedStyle: TabContentStyle,
    selectedStyle: TabContentStyle,
    colors: TabColors,
    unselectedTextStyle: TextStyle,
    selectedTextStyle: TextStyle,
    fraction: Float,
    transition: TabContentTransition,
    options: TabContentOptions,
) {
    val progress = fraction.coerceIn(0f, 1f)
    val unselectedVisual = transition.layerVisual(progress = 1f - progress, entering = false)
    val selectedVisual = transition.layerVisual(progress = progress, entering = true)

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
                    contentColor = colors.unselectedContentColor,
                    textStyle = unselectedTextStyle,
                    options = options,
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
                    contentColor = colors.selectedContentColor,
                    textStyle = selectedTextStyle,
                    options = options,
                )
            }
        },
    ) { measurables, constraints ->
        val relaxed = constraints.copy(minWidth = 0, minHeight = 0)
        val unselectedP = measurables[0].measure(relaxed)
        val selectedP = measurables[1].measure(relaxed)
        val width = lerp(
            start = unselectedP.width.toFloat(),
            stop = selectedP.width.toFloat(),
            fraction = progress,
        ).roundToInt().coerceAtLeast(0)
        val height = maxOf(unselectedP.height, selectedP.height)

        layout(width, height) {
            val unselectedX = (width - unselectedP.width) / 2 +
                (unselectedVisual.offsetXFactor * width).roundToInt()
            val selectedX = (width - selectedP.width) / 2 +
                (selectedVisual.offsetXFactor * width).roundToInt()
            val unselectedY = (height - unselectedP.height) / 2 +
                (unselectedVisual.offsetYFactor * height).roundToInt()
            val selectedY = (height - selectedP.height) / 2 +
                (selectedVisual.offsetYFactor * height).roundToInt()

            unselectedP.placeRelative(unselectedX, unselectedY)
            selectedP.placeRelative(selectedX, selectedY)
        }
    }
}
