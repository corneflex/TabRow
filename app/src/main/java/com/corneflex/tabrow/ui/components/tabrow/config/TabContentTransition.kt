package com.corneflex.tabrow.ui.components.tabrow.config

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
import androidx.compose.ui.unit.IntSize

// ─── Layer visual ─────────────────────────────────────────────────────────────

/**
 * Describes the render state of one content layer during a coordinated cross-fade.
 * Used by [TabContentTransition.Custom.visual] so you can control exactly how each
 * layer fades, scales, and translates.
 */
data class ContentLayerVisual(
    val alpha: Float,
    val scale: Float = 1f,
    val offsetXFactor: Float = 0f,
    val offsetYFactor: Float = 0f,
)

// ─── Transition ───────────────────────────────────────────────────────────────

/**
 * Controls how a tab's content animates when the selected state changes.
 *
 * Built-in presets cover the most common cases. For full control, use [Custom]:
 *
 * ```kotlin
 * TabContentTransition.Custom(
 *     enter = fadeIn() + slideInVertically { -it / 4 },
 *     exit  = fadeOut() + slideOutVertically { it / 4 },
 *     visual = { progress, entering ->
 *         ContentLayerVisual(alpha = progress, offsetYFactor = if (entering) 1f - progress else -(1f - progress))
 *     },
 * )
 * ```
 */
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

    /**
     * Fully custom transition. Open for subclassing if you want a reusable named variant.
     *
     * @param enter Enter transition for [AnimatedContent].
     * @param exit Exit transition for [AnimatedContent].
     * @param sizeSpec Optional size animation override.
     * @param clip Whether to clip the transition.
     * @param visual Visual for coordinated (adaptive) content cross-fades.
     */
    open class Custom(
        val enter: EnterTransition,
        val exit: ExitTransition,
        val sizeSpec: FiniteAnimationSpec<IntSize>? = null,
        val clip: Boolean = false,
        val visual: (progress: Float, entering: Boolean) -> ContentLayerVisual = { p, _ ->
            ContentLayerVisual(alpha = p)
        },
    ) : TabContentTransition()
}

// ─── Internal animation helpers ───────────────────────────────────────────────

internal fun TabContentTransition.contentTransform(
    sizeSpec: FiniteAnimationSpec<IntSize>,
): AnimatedContentTransitionScope<TabContentStyle>.() -> ContentTransform = {
    val transform = when (this@contentTransform) {
        TabContentTransition.None -> EnterTransition.None togetherWith ExitTransition.None
        TabContentTransition.Fade -> fadeIn(tween(160)) togetherWith fadeOut(tween(120))
        TabContentTransition.FadeScale ->
            (fadeIn(tween(160)) + scaleIn(initialScale = 0.92f)) togetherWith
                    (fadeOut(tween(120)) + scaleOut(targetScale = 0.92f))

        TabContentTransition.Scale ->
            scaleIn(initialScale = 0.88f) togetherWith scaleOut(targetScale = 0.88f)

        TabContentTransition.Slide,
        TabContentTransition.SlideLeft ->
            (slideInHorizontally { it / 2 } + fadeIn(tween(160))) togetherWith
                    (slideOutHorizontally { -it / 2 } + fadeOut(tween(120)))

        TabContentTransition.SlideRight ->
            (slideInHorizontally { -it / 2 } + fadeIn(tween(160))) togetherWith
                    (slideOutHorizontally { it / 2 } + fadeOut(tween(120)))

        TabContentTransition.SlideUp ->
            (slideInVertically { it / 2 } + fadeIn(tween(160))) togetherWith
                    (slideOutVertically { -it / 2 } + fadeOut(tween(120)))

        TabContentTransition.SlideDown ->
            (slideInVertically { -it / 2 } + fadeIn(tween(160))) togetherWith
                    (slideOutVertically { it / 2 } + fadeOut(tween(120)))

        TabContentTransition.FadeThrough ->
            fadeIn(tween(durationMillis = 180, delayMillis = 90)) togetherWith fadeOut(tween(90))

        TabContentTransition.Expand ->
            expandHorizontally(expandFrom = Alignment.CenterHorizontally) togetherWith
                    shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally)

        TabContentTransition.ExpandFade ->
            (expandHorizontally(expandFrom = Alignment.CenterHorizontally) + fadeIn(tween(160))) togetherWith
                    (shrinkHorizontally(shrinkTowards = Alignment.CenterHorizontally) + fadeOut(tween(120)))

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

internal fun TabContentTransition.layerVisual(progress: Float, entering: Boolean): ContentLayerVisual {
    if (this is TabContentTransition.Custom) return visual(progress, entering)
    val p = progress.coerceIn(0f, 1f)
    return when (this) {
        TabContentTransition.None      -> ContentLayerVisual(alpha = if (p >= 0.5f) 1f else 0f)
        TabContentTransition.Fade      -> ContentLayerVisual(alpha = p)
        TabContentTransition.FadeScale -> ContentLayerVisual(alpha = p, scale = lerp(0.92f, 1f, p))
        TabContentTransition.Scale     -> ContentLayerVisual(alpha = if (p >= 0.5f) 1f else 0f, scale = lerp(0.88f, 1f, p))
        TabContentTransition.Slide,
        TabContentTransition.SlideLeft  -> ContentLayerVisual(alpha = p, offsetXFactor = if (entering)  (1f - p) * 0.35f else -(1f - p) * 0.35f)
        TabContentTransition.SlideRight -> ContentLayerVisual(alpha = p, offsetXFactor = if (entering) -(1f - p) * 0.35f else  (1f - p) * 0.35f)
        TabContentTransition.SlideUp    -> ContentLayerVisual(alpha = p, offsetYFactor = if (entering)  (1f - p) * 0.35f else -(1f - p) * 0.35f)
        TabContentTransition.SlideDown  -> ContentLayerVisual(alpha = p, offsetYFactor = if (entering) -(1f - p) * 0.35f else  (1f - p) * 0.35f)
        TabContentTransition.FadeThrough -> ContentLayerVisual(
            alpha = if (entering) ((p - 0.5f) * 2f).coerceIn(0f, 1f) else (p * 2f).coerceIn(0f, 1f),
        )
        TabContentTransition.Expand,
        TabContentTransition.ExpandFade -> ContentLayerVisual(alpha = p)
        else -> ContentLayerVisual(alpha = p) // Custom handled above via early return
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction.coerceIn(0f, 1f)
