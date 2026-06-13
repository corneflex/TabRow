package com.corneflex.tabrow.ui.components.tabrow.config

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntSize

sealed interface IndicatorMotion {
    data object Slide : IndicatorMotion
    data object Snake : IndicatorMotion
    data object Bounce : IndicatorMotion
    data object Fade : IndicatorMotion
    data object None : IndicatorMotion
}

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
