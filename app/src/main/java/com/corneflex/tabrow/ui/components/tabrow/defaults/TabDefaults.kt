package com.corneflex.tabrow.ui.components.tabrow.defaults

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.corneflex.tabrow.ui.components.tabrow.config.IndicatorMotion
import com.corneflex.tabrow.ui.components.tabrow.config.IndicatorMotionSpec
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentOptions
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentSwapPolicy
import com.corneflex.tabrow.ui.components.tabrow.config.TabContentTransition
import com.corneflex.tabrow.ui.components.tabrow.config.TabIndicatorConfig
import com.corneflex.tabrow.ui.components.tabrow.config.TabIndicatorStyle
import com.corneflex.tabrow.ui.components.tabrow.model.TabColors
import com.corneflex.tabrow.ui.components.tabrow.model.TabStyle

/**
 * Factory object for all tab row configuration types.
 *
 * Use these to build configuration objects with sensible defaults while
 * only overriding the values you care about.
 *
 * ```kotlin
 * CustomScrollableTabRow(
 *     tabs = tabs,
 *     pagerState = pagerState,
 *     colors = TabDefaults.colors(selectedContentColor = Color.Blue),
 *     style = TabDefaults.style(itemSpacing = 4.dp),
 *     indicator = TabDefaults.indicator(TabIndicatorStyle.Underline()),
 * )
 * ```
 */
object TabDefaults {

    // ─── Colors ───────────────────────────────────────────────────────────────

    @Composable
    fun colors(
        selectedContentColor: Color = MaterialTheme.colorScheme.primary,
        unselectedContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        selectedContainerColor: Color = Color.Transparent,
        unselectedContainerColor: Color = Color.Transparent,
    ) = TabColors(
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        selectedContainerColor = selectedContainerColor,
        unselectedContainerColor = unselectedContainerColor,
    )

    /** Colors for a filled selected state (e.g. pill with accent background). */
    fun filledColors(
        selectedContentColor: Color,
        unselectedContentColor: Color,
        selectedContainerColor: Color,
        unselectedContainerColor: Color = Color.Transparent,
    ) = TabColors(
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
        selectedContainerColor = selectedContainerColor,
        unselectedContainerColor = unselectedContainerColor,
    )

    /** Colors for a tab row where selection is expressed only through color, no indicator fill. */
    fun outlinedColors(
        selectedContentColor: Color,
        unselectedContentColor: Color,
    ) = TabColors(
        selectedContentColor = selectedContentColor,
        unselectedContentColor = unselectedContentColor,
    )

    // ─── Style ────────────────────────────────────────────────────────────────

    fun style(
        shape: Shape = RoundedCornerShape(999.dp),
        selectedTextStyle: TextStyle? = null,
        unselectedTextStyle: TextStyle? = null,
        selectedBorder: BorderStroke? = null,
        unselectedBorder: BorderStroke? = null,
        minHeight: Dp = 44.dp,
        horizontalPadding: Dp = 16.dp,
        itemSpacing: Dp = 8.dp,
    ) = TabStyle(
        shape = shape,
        selectedTextStyle = selectedTextStyle,
        unselectedTextStyle = unselectedTextStyle,
        selectedBorder = selectedBorder,
        unselectedBorder = unselectedBorder,
        minHeight = minHeight,
        horizontalPadding = horizontalPadding,
        itemSpacing = itemSpacing,
    )

    /** Style preset for tabs with an explicit border around each item. */
    fun outlinedStyle(
        selectedBorderColor: Color,
        unselectedBorderColor: Color,
        shape: Shape = RoundedCornerShape(999.dp),
        borderWidth: Dp = 1.dp,
        minHeight: Dp = 44.dp,
        horizontalPadding: Dp = 16.dp,
        itemSpacing: Dp = 8.dp,
    ) = TabStyle(
        shape = shape,
        selectedBorder = BorderStroke(borderWidth, selectedBorderColor),
        unselectedBorder = BorderStroke(borderWidth, unselectedBorderColor),
        minHeight = minHeight,
        horizontalPadding = horizontalPadding,
        itemSpacing = itemSpacing,
    )

    // ─── Content options ──────────────────────────────────────────────────────

    fun contentOptions(
        transition: TabContentTransition = TabContentTransition.FadeScale,
        swapPolicy: TabContentSwapPolicy = TabContentSwapPolicy.Coordinated,
        iconOnlyHorizontalPadding: Dp = 8.dp,
    ) = TabContentOptions(
        transition = transition,
        swapPolicy = swapPolicy,
        iconOnlyHorizontalPadding = iconOnlyHorizontalPadding,
    )

    // ─── Indicator ────────────────────────────────────────────────────────────

    fun indicator(
        style: TabIndicatorStyle = TabIndicatorStyle.Pill(),
        motion: IndicatorMotion? = null,
        motionSpec: IndicatorMotionSpec = IndicatorMotionSpec(),
    ) = TabIndicatorConfig(
        style = style,
        motion = motion,
        motionSpec = motionSpec,
    )

    fun pillIndicator(
        color: Color = Color.Black,
        motion: IndicatorMotion? = null,
        horizontalPadding: Dp = 12.dp,
    ) = TabIndicatorConfig(
        style = TabIndicatorStyle.Pill(color = color, horizontalPadding = horizontalPadding),
        motion = motion,
    )

    fun underlineIndicator(
        color: Color = Color.Black,
        motion: IndicatorMotion? = IndicatorMotion.Snake,
        horizontalPadding: Dp = 12.dp,
    ) = TabIndicatorConfig(
        style = TabIndicatorStyle.Underline(color = color, horizontalPadding = horizontalPadding),
        motion = motion,
    )

    fun dotIndicator(
        color: Color = Color.Black,
        motion: IndicatorMotion? = IndicatorMotion.Bounce,
    ) = TabIndicatorConfig(
        style = TabIndicatorStyle.Dot(color = color),
        motion = motion,
    )
}
