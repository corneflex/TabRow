package com.corneflex.tabrow.ui.components.tabrow.internal

import com.corneflex.tabrow.ui.components.tabrow.config.IndicatorMotion
import com.corneflex.tabrow.ui.components.tabrow.config.IndicatorMotionSpec
import com.corneflex.tabrow.ui.components.tabrow.config.TabIndicatorStyle
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sin

internal data class TabMeasurement(
    val left: Float,
    val width: Float,
) {
    val right: Float = left + width
    val center: Float = left + width / 2f
}

internal data class IndicatorPosition(
    val left: Float,
    val width: Float,
    val scale: Float,
    val alpha: Float,
)

internal fun indicatorPosition(
    progress: Float,
    tabPositions: Map<Int, TabMeasurement>,
    style: TabIndicatorStyle,
    motion: IndicatorMotion,
    motionSpec: IndicatorMotionSpec,
    minWidthPx: Float,
    horizontalPaddingPx: Float,
    dotSizePx: Float = 0f,
): IndicatorPosition? {
    val fromIndex = floor(progress).toInt()
    val toIndex = ceil(progress).toInt()
    val from = tabPositions[fromIndex] ?: return null
    val to = tabPositions[toIndex] ?: from
    val fraction = progress - fromIndex

    val fromBounds = from.indicatorBounds(horizontalPaddingPx, minWidthPx)
    val toBounds = to.indicatorBounds(horizontalPaddingPx, minWidthPx)

    val (left, right) = when (motion) {
        IndicatorMotion.Snake -> snakeBounds(
            fromBounds.left, fromBounds.right,
            toBounds.left, toBounds.right,
            fraction,
        )
        else -> lerp(fromBounds.left, toBounds.left, fraction) to
            lerp(fromBounds.right, toBounds.right, fraction)
    }

    val scale = if (motion == IndicatorMotion.Bounce) {
        1f + sin(fraction * PI).toFloat() * (motionSpec.bounceScale - 1f)
    } else 1f

    val alpha = if (motion == IndicatorMotion.Fade) {
        motionSpec.fadeMinAlpha + abs(fraction - 0.5f) * (2f * (1f - motionSpec.fadeMinAlpha))
    } else 1f

    val isDot = style is TabIndicatorStyle.Dot
    val width = if (isDot) dotSizePx else (right - left).coerceAtLeast(0f)

    return IndicatorPosition(
        left = if (isDot) lerp(from.center, to.center, fraction) - width / 2f else left,
        width = width,
        scale = scale,
        alpha = alpha.coerceIn(0f, 1f),
    )
}

private data class IndicatorBounds(val left: Float, val right: Float)

private fun TabMeasurement.indicatorBounds(paddingPx: Float, minWidthPx: Float): IndicatorBounds {
    val inset = paddingPx.coerceIn(0f, ((width - minWidthPx) / 2f).coerceAtLeast(0f))
    return IndicatorBounds(left = left + inset, right = right - inset)
}

private fun snakeBounds(
    fromLeft: Float, fromRight: Float,
    toLeft: Float, toRight: Float,
    fraction: Float,
): Pair<Float, Float> = if (fraction < 0.5f) {
    fromLeft to lerp(fromRight, toRight, fraction * 2f)
} else {
    lerp(fromLeft, toLeft, (fraction - 0.5f) * 2f) to toRight
}
