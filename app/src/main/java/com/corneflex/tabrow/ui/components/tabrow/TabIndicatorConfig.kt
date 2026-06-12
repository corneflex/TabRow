package com.corneflex.tabrow.ui.components.tabrow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class IndicatorPlacement {
    Bottom,
    Top,
    BehindContent,
}

data class TabIndicatorConfig(
    val style: TabIndicatorStyle = TabIndicatorStyle.Pill(),
    val motion: IndicatorMotion? = null,
    val motionSpec: IndicatorMotionSpec = IndicatorMotionSpec(),
) {
    companion object {
        fun pill(
            color: Color = Color.Black,
            motion: IndicatorMotion? = null,
        ) = TabIndicatorConfig(
            style = TabIndicatorStyle.Pill(color = color),
            motion = motion,
        )

        fun underline(
            color: Color = Color.Black,
            motion: IndicatorMotion? = IndicatorMotion.Snake,
        ) = TabIndicatorConfig(
            style = TabIndicatorStyle.Underline(color = color),
            motion = motion,
        )

        fun dot(
            color: Color = Color.Black,
            motion: IndicatorMotion? = IndicatorMotion.Bounce,
        ) = TabIndicatorConfig(
            style = TabIndicatorStyle.Dot(color = color),
            motion = motion,
        )
    }
}

data class IndicatorMotionSpec(
    val bounceScale: Float = 1.08f,
    val fadeMinAlpha: Float = 0.55f,
)

sealed class TabIndicatorStyle(
    open val color: Color,
    open val border: BorderStroke?,
    open val placement: IndicatorPlacement,
    open val horizontalPadding: Dp,
) {
    data class Pill(
        override val color: Color = Color.Black,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.BehindContent,
        override val horizontalPadding: Dp = 12.dp,
        val height: Dp = 36.dp,
        val shape: Shape = RoundedCornerShape(999.dp),
    ) : TabIndicatorStyle(color, border, placement, horizontalPadding)

    data class Rectangle(
        override val color: Color = Color.Black,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.BehindContent,
        override val horizontalPadding: Dp = 12.dp,
        val height: Dp = 36.dp,
        val shape: Shape = RoundedCornerShape(8.dp),
    ) : TabIndicatorStyle(color, border, placement, horizontalPadding)

    data class Underline(
        override val color: Color = Color.Black,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.Bottom,
        override val horizontalPadding: Dp = 12.dp,
        val height: Dp = 3.dp,
        val shape: Shape = RoundedCornerShape(999.dp),
    ) : TabIndicatorStyle(color, border, placement, horizontalPadding)

    data class Dash(
        override val color: Color = Color.Black,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.Bottom,
        override val horizontalPadding: Dp = 20.dp,
        val height: Dp = 4.dp,
        val shape: Shape = RoundedCornerShape(999.dp),
    ) : TabIndicatorStyle(color, border, placement, horizontalPadding)

    data class Dot(
        override val color: Color = Color.Black,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.Bottom,
        val size: Dp = 7.dp,
    ) : TabIndicatorStyle(color, border, placement, horizontalPadding = 0.dp)

    data class Border(
        override val color: Color = Color.Transparent,
        override val border: BorderStroke = BorderStroke(1.dp, Color.Black),
        override val placement: IndicatorPlacement = IndicatorPlacement.BehindContent,
        override val horizontalPadding: Dp = 12.dp,
        val height: Dp = 36.dp,
        val shape: Shape = RoundedCornerShape(999.dp),
    ) : TabIndicatorStyle(color, border, placement, horizontalPadding)
}

internal val TabIndicatorStyle.height: Dp
    get() = when (this) {
        is TabIndicatorStyle.Pill -> height
        is TabIndicatorStyle.Rectangle -> height
        is TabIndicatorStyle.Underline -> height
        is TabIndicatorStyle.Dash -> height
        is TabIndicatorStyle.Dot -> size
        is TabIndicatorStyle.Border -> height
    }

internal val TabIndicatorStyle.shape: Shape
    get() = when (this) {
        is TabIndicatorStyle.Pill -> shape
        is TabIndicatorStyle.Rectangle -> shape
        is TabIndicatorStyle.Underline -> shape
        is TabIndicatorStyle.Dash -> shape
        is TabIndicatorStyle.Dot -> CircleShape
        is TabIndicatorStyle.Border -> shape
    }
