package com.corneflex.tabrow.ui.components.tabrow.config

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

// ─── Placement ────────────────────────────────────────────────────────────────

enum class IndicatorPlacement { Bottom, Top, BehindContent }

// ─── Config ───────────────────────────────────────────────────────────────────

data class TabIndicatorConfig(
    val style: TabIndicatorStyle = TabIndicatorStyle.Pill(),
    val motion: IndicatorMotion? = null,
    val motionSpec: IndicatorMotionSpec = IndicatorMotionSpec(),
)

data class IndicatorMotionSpec(
    val bounceScale: Float = 1.08f,
    val fadeMinAlpha: Float = 0.55f,
)

// ─── Style base ───────────────────────────────────────────────────────────────

/**
 * Describes how the selection indicator looks and where it sits.
 *
 * All built-in variants are nested data classes so they work with `.copy()`.
 * Extend this class to create a fully custom indicator with any drawing logic:
 *
 * ```kotlin
 * class GradientIndicator : TabIndicatorStyle() {
 *     override val color = Color.Transparent
 *     override val border = null
 *     override val placement = IndicatorPlacement.BehindContent
 *     override val horizontalPadding = 12.dp
 *     override val height = 36.dp
 *     override val shape: Shape = RoundedCornerShape(999.dp)
 *
 *     override fun applyStyle(modifier: Modifier) =
 *         modifier.background(
 *             brush = Brush.horizontalGradient(listOf(Color.Blue, Color.Purple)),
 *             shape = shape,
 *         )
 * }
 * ```
 */
abstract class TabIndicatorStyle {
    abstract val color: Color
    abstract val border: BorderStroke?
    abstract val placement: IndicatorPlacement
    abstract val horizontalPadding: Dp
    abstract val height: Dp
    abstract val shape: Shape

    /** Minimum width the indicator must maintain. Override for border-only styles. */
    open val minimumUsefulWidth: Dp get() = 0.dp

    /** Clips [horizontalPadding] so the indicator never shrinks below [minimumUsefulWidth]. */
    fun effectiveHorizontalPadding(availableWidth: Dp): Dp {
        val max = ((availableWidth - minimumUsefulWidth) / 2f).coerceAtLeast(0.dp)
        return horizontalPadding.coerceAtMost(max)
    }

    /**
     * Applies this style's visual to [modifier]. Override to customise drawing — e.g. gradients,
     * multi-layer compositing, or canvas drawing — without changing any layout properties.
     */
    open fun applyStyle(modifier: Modifier): Modifier =
        modifier
            .background(color, shape)
            .let { if (border != null) it.border(border!!, shape) else it }

    // ─── Built-in variants ────────────────────────────────────────────────────

    data class Pill(
        override val color: Color = Color.Black,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.BehindContent,
        override val horizontalPadding: Dp = 12.dp,
        override val height: Dp = 36.dp,
        override val shape: Shape = RoundedCornerShape(999.dp),
    ) : TabIndicatorStyle()

    data class Rectangle(
        override val color: Color = Color.Black,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.BehindContent,
        override val horizontalPadding: Dp = 12.dp,
        override val height: Dp = 36.dp,
        override val shape: Shape = RoundedCornerShape(8.dp),
    ) : TabIndicatorStyle()

    data class Underline(
        override val color: Color = Color.Black,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.Bottom,
        override val horizontalPadding: Dp = 12.dp,
        override val height: Dp = 3.dp,
        override val shape: Shape = RoundedCornerShape(999.dp),
    ) : TabIndicatorStyle()

    data class Dash(
        override val color: Color = Color.Black,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.Bottom,
        override val horizontalPadding: Dp = 20.dp,
        override val height: Dp = 4.dp,
        override val shape: Shape = RoundedCornerShape(999.dp),
    ) : TabIndicatorStyle()

    data class Dot(
        override val color: Color = Color.Black,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.Bottom,
        val size: Dp = 7.dp,
    ) : TabIndicatorStyle() {
        override val horizontalPadding: Dp = 0.dp
        override val height: Dp get() = size
        override val shape: Shape = CircleShape
    }

    data class Border(
        override val color: Color = Color.Transparent,
        override val border: BorderStroke = BorderStroke(1.dp, Color.Black),
        override val placement: IndicatorPlacement = IndicatorPlacement.BehindContent,
        override val horizontalPadding: Dp = 12.dp,
        override val height: Dp = 36.dp,
        override val shape: Shape = RoundedCornerShape(999.dp),
    ) : TabIndicatorStyle()

    data class SideRoundedBorder(
        override val color: Color = Color.Transparent,
        override val border: BorderStroke = BorderStroke(1.dp, Color.Black),
        override val placement: IndicatorPlacement = IndicatorPlacement.BehindContent,
        override val horizontalPadding: Dp = 12.dp,
        override val height: Dp = 36.dp,
        override val shape: Shape = SideRoundedShape,
    ) : TabIndicatorStyle() {
        override val minimumUsefulWidth: Dp get() = height
    }

    data class TopBottomBorder(
        val lineColor: Color = Color.Black,
        val lineWidth: Dp = 1.dp,
        override val color: Color = Color.Transparent,
        override val border: BorderStroke? = null,
        override val placement: IndicatorPlacement = IndicatorPlacement.BehindContent,
        override val horizontalPadding: Dp = 12.dp,
        override val height: Dp = 36.dp,
        override val shape: Shape = RectangleShape,
    ) : TabIndicatorStyle() {
        override fun applyStyle(modifier: Modifier): Modifier =
            modifier.background(color, shape).drawBehind {
                val stroke = lineWidth.toPx()
                val center = stroke / 2f
                drawLine(lineColor, Offset(0f, center), Offset(size.width, center), stroke)
                drawLine(lineColor, Offset(0f, size.height - center), Offset(size.width, size.height - center), stroke)
            }
    }
}

// ─── Shapes ───────────────────────────────────────────────────────────────────

/** A pill shape with flat top/bottom edges and rounded left/right caps. */
object SideRoundedShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        if (size.width <= 0f || size.height <= 0f) {
            return Outline.Rectangle(Rect(0f, 0f, size.width, size.height))
        }
        val r = minOf(size.height / 2f, size.width / 2f)
        val top = (size.height - r * 2f) / 2f
        val bottom = top + r * 2f
        return Outline.Generic(Path().apply {
            moveTo(r, top)
            lineTo(size.width - r, top)
            arcTo(Rect(size.width - r * 2f, top, size.width, bottom), -90f, 180f, false)
            lineTo(r, bottom)
            arcTo(Rect(0f, top, r * 2f, bottom), 90f, 180f, false)
            close()
        })
    }
}
