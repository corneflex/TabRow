package com.corneflex.tabrow.ui.components.tabrow.internal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.corneflex.tabrow.ui.components.tabrow.config.TabIndicatorStyle

internal fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction.coerceIn(0f, 1f)

internal fun lerpColor(start: Color, stop: Color, fraction: Float): Color = Color(
    red = lerp(start.red, stop.red, fraction),
    green = lerp(start.green, stop.green, fraction),
    blue = lerp(start.blue, stop.blue, fraction),
    alpha = lerp(start.alpha, stop.alpha, fraction),
)

@Composable
internal fun Float.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

internal fun Modifier.indicatorSurface(color: Color, shape: Shape, border: BorderStroke?): Modifier =
    background(color, shape).let { if (border != null) it.border(border, shape) else it }

internal fun Modifier.indicatorSurface(style: TabIndicatorStyle): Modifier =
    style.applyStyle(this)
