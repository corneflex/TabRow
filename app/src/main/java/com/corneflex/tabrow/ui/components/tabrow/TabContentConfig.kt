package com.corneflex.tabrow.ui.components.tabrow

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

sealed interface TabContentStyle {
    data object Text : TabContentStyle
    data object Icon : TabContentStyle
    data object Image : TabContentStyle
    data object IconText : TabContentStyle
    data object ImageText : TabContentStyle
}

data class TabContentMetrics(
    val iconOnlyHorizontalPadding: Dp = 8.dp,
)

sealed interface TabContentConfig {
    data object Text : TabContentConfig
    data object Icon : TabContentConfig
    data object Image : TabContentConfig
    data object IconText : TabContentConfig
    data object ImageText : TabContentConfig

    data class Adaptive(
        val unselected: TabContentStyle,
        val selected: TabContentStyle,
    ) : TabContentConfig
}

internal fun TabContentConfig.styleFor(selected: Boolean): TabContentStyle {
    return when (this) {
        TabContentConfig.Text -> TabContentStyle.Text
        TabContentConfig.Icon -> TabContentStyle.Icon
        TabContentConfig.Image -> TabContentStyle.Image
        TabContentConfig.IconText -> TabContentStyle.IconText
        TabContentConfig.ImageText -> TabContentStyle.ImageText
        is TabContentConfig.Adaptive -> if (selected) this.selected else this.unselected
    }
}

sealed class TabContentTransition {
    data object None : TabContentTransition()
    data object Fade : TabContentTransition()
    data object FadeScale : TabContentTransition()
    data object Scale : TabContentTransition()
    data object Slide : TabContentTransition()
    data object SlideLeft : TabContentTransition()
    data object SlideRight : TabContentTransition()
    data object SlideUp : TabContentTransition()
    data object SlideDown : TabContentTransition()
    data object FadeThrough : TabContentTransition()
    data object Expand : TabContentTransition()
    data object ExpandFade : TabContentTransition()
    data class Custom(
        val enter: EnterTransition,
        val exit: ExitTransition,
        val sizeSpec: FiniteAnimationSpec<IntSize>? = null,
        val clip: Boolean = false,
    ) : TabContentTransition()
}

sealed class TabContentSwapPolicy {
    data object Coordinated : TabContentSwapPolicy()
    data object Together : TabContentSwapPolicy()

    data class DeselectThenSelect(
        val delayMillis: Long = 140L,
    ) : TabContentSwapPolicy()
}

internal fun TabContentTransition.contentTransform(
    sizeSpec: FiniteAnimationSpec<IntSize>,
): AnimatedContentTransitionScope<TabContentStyle>.() -> ContentTransform {
    return {
        val transform = when (this@contentTransform) {
            TabContentTransition.None -> EnterTransition.None togetherWith ExitTransition.None
            TabContentTransition.Fade -> defaultFadeIn() togetherWith defaultFadeOut()
            TabContentTransition.FadeScale -> {
                (defaultFadeIn() + scaleIn(initialScale = 0.92f)) togetherWith
                    (defaultFadeOut() + scaleOut(targetScale = 0.92f))
            }

            TabContentTransition.Scale -> {
                scaleIn(initialScale = 0.88f) togetherWith scaleOut(targetScale = 0.88f)
            }

            TabContentTransition.Slide,
            TabContentTransition.SlideLeft -> {
                (slideInHorizontally { it / 2 } + defaultFadeIn()) togetherWith
                    (slideOutHorizontally { -it / 2 } + defaultFadeOut())
            }

            TabContentTransition.SlideRight -> {
                (slideInHorizontally { -it / 2 } + defaultFadeIn()) togetherWith
                    (slideOutHorizontally { it / 2 } + defaultFadeOut())
            }

            TabContentTransition.SlideUp -> {
                (slideInVertically { it / 2 } + defaultFadeIn()) togetherWith
                    (slideOutVertically { -it / 2 } + defaultFadeOut())
            }

            TabContentTransition.SlideDown -> {
                (slideInVertically { -it / 2 } + defaultFadeIn()) togetherWith
                    (slideOutVertically { it / 2 } + defaultFadeOut())
            }

            TabContentTransition.FadeThrough -> {
                fadeIn(animationSpec = tween(durationMillis = 180, delayMillis = 90)) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 90))
            }

            TabContentTransition.Expand -> {
                expandHorizontally(expandFrom = Alignment.CenterHorizontally) togetherWith
                    shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally)
            }

            TabContentTransition.ExpandFade -> {
                (expandHorizontally(expandFrom = Alignment.CenterHorizontally) + defaultFadeIn()) togetherWith
                    (shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally) + defaultFadeOut())
            }

            is TabContentTransition.Custom -> enter togetherWith exit
        }

        val custom = this@contentTransform as? TabContentTransition.Custom
        transform.using(
            SizeTransform(
                clip = custom?.clip ?: false,
                sizeAnimationSpec = { _, _ -> custom?.sizeSpec ?: sizeSpec },
            )
        )
    }
}

internal fun TabContentConfig.hasSelectionSpecificContent(): Boolean {
    return this is TabContentConfig.Adaptive && selected != unselected
}

private fun defaultFadeIn(): EnterTransition = fadeIn(animationSpec = tween(160))
private fun defaultFadeOut(): ExitTransition = fadeOut(animationSpec = tween(120))
