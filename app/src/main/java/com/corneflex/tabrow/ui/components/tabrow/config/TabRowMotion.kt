package com.corneflex.tabrow.ui.components.tabrow.config

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntSize

/**
 * How the selection indicator travels between tabs.
 *
 * Built-in presets cover the common cases. For anything else, use [Custom] to
 * compute the indicator geometry yourself — this is the extension point that
 * mirrors [TabContentTransition.Custom] and [TabRowMotion.Custom].
 */
sealed interface IndicatorMotion {
    data object Slide : IndicatorMotion
    data object Snake : IndicatorMotion
    data object Bounce : IndicatorMotion
    data object Fade : IndicatorMotion
    data object None : IndicatorMotion

    /**
     * Fully custom motion. Receives the start/end geometry of the indicator and
     * the transition [fraction][IndicatorMotionScope.fraction], and returns the
     * interpolated geometry for the current frame.
     *
     * ```kotlin
     * // Indicator that leads with a slight overshoot
     * IndicatorMotion.Custom { s ->
     *     val eased = 1f - (1f - s.fraction).pow(3)
     *     IndicatorTransform(
     *         left = s.fromLeft + (s.toLeft - s.fromLeft) * eased,
     *         right = s.fromRight + (s.toRight - s.fromRight) * eased,
     *     )
     * }
     * ```
     */
    data class Custom(
        val transform: (scope: IndicatorMotionScope) -> IndicatorTransform,
    ) : IndicatorMotion
}

/** Start/end indicator geometry (in pixels) and the `[0, 1]` transition fraction. */
data class IndicatorMotionScope(
    val fromLeft: Float,
    val fromRight: Float,
    val toLeft: Float,
    val toRight: Float,
    val fraction: Float,
)

/** Interpolated indicator geometry for one frame, produced by an [IndicatorMotion]. */
data class IndicatorTransform(
    val left: Float,
    val right: Float,
    val scale: Float = 1f,
    val alpha: Float = 1f,
)

sealed class TabRowMotion(
    internal val indicatorMotion: IndicatorMotion,
    internal val colorSpec: FiniteAnimationSpec<Float>,
    internal val sizeSpec: FiniteAnimationSpec<IntSize>,
    internal val scrollSpec: FiniteAnimationSpec<Float>,
) {
    data object None : TabRowMotion(
        indicatorMotion = IndicatorMotion.None,
        colorSpec = TweenSpec(durationMillis = 0),
        sizeSpec = TweenSpec(durationMillis = 0),
        scrollSpec = TweenSpec(durationMillis = 0),
    )

    data object Smooth : TabRowMotion(
        indicatorMotion = IndicatorMotion.Slide,
        colorSpec = tween(durationMillis = 180),
        sizeSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        scrollSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
    )

    data object Snappy : TabRowMotion(
        indicatorMotion = IndicatorMotion.Slide,
        colorSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessHigh,
        ),
        sizeSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        scrollSpec = tween(durationMillis = 180, easing = FastOutSlowInEasing),
    )

    data object Playful : TabRowMotion(
        indicatorMotion = IndicatorMotion.Snake,
        colorSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        sizeSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        ),
        scrollSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
    )

    data class Custom(
        val motion: IndicatorMotion = IndicatorMotion.Slide,
        val colorAnimationSpec: FiniteAnimationSpec<Float> = tween(180),
        val sizeAnimationSpec: FiniteAnimationSpec<IntSize> = tween(220, easing = FastOutSlowInEasing),
        val scrollAnimationSpec: FiniteAnimationSpec<Float> = tween(260, easing = FastOutSlowInEasing),
    ) : TabRowMotion(
        indicatorMotion = motion,
        colorSpec = colorAnimationSpec,
        sizeSpec = sizeAnimationSpec,
        scrollSpec = scrollAnimationSpec,
    )
}
