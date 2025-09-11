/*
 * Copyright 2021 The Android Open Source Project
 * Copyright 2023 Eliezer Graber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eygraber.compose.placeholder

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Contains default values used by [Modifier.placeholder] and [PlaceholderHighlight].
 */
public object PlaceholderDefaults {
  /**
   * The default [InfiniteRepeatableSpec] to use for [fade].
   */
  public val fadeAnimationSpec: InfiniteRepeatableSpec<Float> by lazy {
    infiniteRepeatable(
      animation = tween(delayMillis = 200, durationMillis = 600),
      repeatMode = RepeatMode.Reverse,
    )
  }

  /**
   * The default [InfiniteRepeatableSpec] to use for [shimmer].
   */
  public val shimmerAnimationSpec: InfiniteRepeatableSpec<Float> by lazy {
    infiniteRepeatable(
      animation = tween(durationMillis = 1700, delayMillis = 200),
      repeatMode = RepeatMode.Restart
    )
  }
}

/**
 * Draws some skeleton UI which is typically used whilst content is 'loading'.
 *
 * A version of this modifier which uses appropriate values for Material themed apps is available
 * in the 'Placeholder Material' library.
 *
 * You can provide a [PlaceholderHighlight] which runs an highlight animation on the placeholder.
 * The [shimmer] and [fade] implementations are provided for easy usage.
 *
 * A cross-fade transition will be applied to the content and placeholder UI when the [visible]
 * value changes. The transition can be customized via the [contentFadeAnimationSpec] and
 * [placeholderFadeAnimationSpec] parameters.
 *
 * You can find more information on the pattern at the Material Theming
 * [Placeholder UI](https://material.io/design/communication/launch-screen.html#placeholder-ui)
 * guidelines.
 *
 * @sample com.eygraber.compose.placeholder.sample.DocSample_Foundation_Placeholder
 *
 * @param visible whether the placeholder should be visible or not.
 * @param color the color used to draw the placeholder UI.
 * @param shape desired shape of the placeholder. Defaults to [RectangleShape].
 * @param highlight optional highlight animation.
 * @param placeholderFadeAnimationSpec The animation spec to use when fading the placeholder
 * on/off screen.
 * @param contentFadeAnimationSpec The animation spec to use when fading the content
 * on/off screen.
 */
public fun Modifier.placeholder(
  visible: Boolean,
  color: Color,
  shape: Shape = RectangleShape,
  highlight: PlaceholderHighlight? = null,
  placeholderFadeAnimationSpec: AnimationSpec<Float> = spring(),
  contentFadeAnimationSpec: AnimationSpec<Float> = spring(),
): Modifier = this then PlaceholderElement(
  visible = visible,
  color = color,
  shape = shape,
  highlight = highlight,
  placeholderFadeAnimationSpec = placeholderFadeAnimationSpec,
  contentFadeAnimationSpec = contentFadeAnimationSpec,
)

private data class PlaceholderElement(
  private val visible: Boolean,
  private val color: Color,
  private val shape: Shape,
  private val highlight: PlaceholderHighlight?,
  private val placeholderFadeAnimationSpec: AnimationSpec<Float>,
  private val contentFadeAnimationSpec: AnimationSpec<Float>,
) : ModifierNodeElement<PlaceholderNode>() {
  override fun create(): PlaceholderNode = PlaceholderNode(
    visible = visible,
    color = color,
    shape = shape,
    highlight = highlight,
    placeholderFadeAnimationSpec = placeholderFadeAnimationSpec,
    contentFadeAnimationSpec = contentFadeAnimationSpec
  )

  override fun update(node: PlaceholderNode) {
    node.apply {
      updateVisible(visible)
      updateColor(color)
      updateShape(shape)
      updateHighlight(highlight)
      updatePlaceholderFadeAnimationSpec(placeholderFadeAnimationSpec)
      updateContentFadeAnimationSpec(contentFadeAnimationSpec)
    }
  }

  override fun InspectorInfo.inspectableProperties() {
    name = "placeholder"
    value = visible
    properties["visible"] = visible
    properties["color"] = color
    properties["highlight"] = highlight
    properties["shape"] = shape
  }
}

private class PlaceholderNode(
  private var visible: Boolean,
  private var color: Color,
  private var shape: Shape = RectangleShape,
  private var highlight: PlaceholderHighlight? = null,
  private var placeholderFadeAnimationSpec: AnimationSpec<Float>,
  private var contentFadeAnimationSpec: AnimationSpec<Float>,
) : DrawModifierNode, Modifier.Node() {
  private val crossfadeTransitionState = MutableTransitionState(visible).apply {
    targetState = visible
  }

  private val paint: Paint = Paint()

  private var contentAlpha: Float = if(visible) 0F else 1F
  private var placeholderAlpha: Float = if(visible) 1F else 0F

  // The current highlight animation progress
  private var highlightProgress: Float = 0F

  // Values used for caching purposes
  private var lastSize: Size = Size.Unspecified
  private var lastLayoutDirection: LayoutDirection? = null
  private var lastOutline: Outline? = null

  private var animationJob: Job? = null
  private var highlightJob: Job? = null

  fun updateVisible(visible: Boolean) {
    if(this.visible != visible) {
      this.visible = visible
      crossfadeTransitionState.targetState = visible
      coroutineScope.runAlphaAnimations()
      coroutineScope.runHighlightAnimation()
    }
  }

  fun updateColor(color: Color) {
    if(this.color != color) {
      this.color = color
    }
  }

  fun updateShape(shape: Shape) {
    if(this.shape != shape) {
      this.shape = shape
    }
  }

  fun updateHighlight(highlight: PlaceholderHighlight?) {
    if(this.highlight != highlight) {
      this.highlight = highlight
      coroutineScope.runHighlightAnimation()
    }
  }

  fun updatePlaceholderFadeAnimationSpec(
    placeholderFadeAnimationSpec: AnimationSpec<Float>
  ) {
    if(this.placeholderFadeAnimationSpec != placeholderFadeAnimationSpec) {
      this.placeholderFadeAnimationSpec = placeholderFadeAnimationSpec
    }
  }

  fun updateContentFadeAnimationSpec(
    contentFadeAnimationSpec: AnimationSpec<Float>
  ) {
    if(this.contentFadeAnimationSpec != contentFadeAnimationSpec) {
      this.contentFadeAnimationSpec = contentFadeAnimationSpec
    }
  }

  override fun onAttach() {
    coroutineScope.runAlphaAnimations()
    coroutineScope.runHighlightAnimation()
  }

  override fun onDetach() {
    animationJob?.cancel()
    highlightJob?.cancel()

    animationJob = null
    highlightJob = null
  }

  private val placeholderAnimation = Animatable(placeholderAlpha)
  private val contentAnimation = Animatable(contentAlpha)

  private fun CoroutineScope.runAlphaAnimations() {
    animationJob?.cancel()

    animationJob = launch {
      launch {
        placeholderAnimation.animateTo(
          targetValue = if(visible) 1F else 0F,
          placeholderFadeAnimationSpec,
        ) {
          val placeholderAlphaWas0 = placeholderAlpha < 0.01F
          placeholderAlpha = value
          if(placeholderAlphaWas0 && placeholderAlpha >= 0.01F && !visible) {
            coroutineScope.runHighlightAnimation()
          }
          invalidateDraw()
        }
      }

      launch {
        contentAnimation.animateTo(
          targetValue = if(visible) 0F else 1F,
          contentFadeAnimationSpec,
        ) {
          contentAlpha = value
          invalidateDraw()
        }
      }
    }
  }

  private val infiniteAnimation = Animatable(0F)

  private fun CoroutineScope.runHighlightAnimation() {
    highlightJob?.cancel()

    val isEffectivelyVisible = visible || placeholderAlpha >= 0.01F
    val animationSpec = highlight?.animationSpec
    if(isEffectivelyVisible && animationSpec != null) {
      highlightJob = launch {
        infiniteAnimation.snapTo(0F)
        infiniteAnimation.animateTo(1F, animationSpec) {
          highlightProgress = value
          invalidateDraw()
        }
      }
    }
  }

  override fun ContentDrawScope.draw() {
    val drawContent = ::drawContent

    // Draw the composable content first
    if(contentAlpha in 0.01F..0.99F) {
      // If the content alpha is between 1% and 99%, draw it in a layer with
      // the alpha applied
      paint.alpha = contentAlpha
      withLayer(paint) {
        drawContent()
      }
    }
    else if(contentAlpha >= 0.99F) {
      // If the content alpha is > 99%, draw it with no alpha
      drawContent()
    }

    if(placeholderAlpha in 0.01F..0.99F) {
      // If the placeholder alpha is between 1% and 99%, draw it in a layer with
      // the alpha applied
      paint.alpha = placeholderAlpha
      withLayer(paint) {
        lastOutline = drawPlaceholder(
          shape = shape,
          color = color,
          highlight = highlight,
          progress = highlightProgress,
          lastOutline = lastOutline,
          lastLayoutDirection = lastLayoutDirection,
          lastSize = lastSize,
        )
      }
    }
    else if(placeholderAlpha >= 0.99F) {
      // If the placeholder alpha is > 99%, draw it with no alpha
      lastOutline = drawPlaceholder(
        shape = shape,
        color = color,
        highlight = highlight,
        progress = highlightProgress,
        lastOutline = lastOutline,
        lastLayoutDirection = lastLayoutDirection,
        lastSize = lastSize,
      )
    }

    // Keep track of the last size & layout direction
    lastSize = size
    lastLayoutDirection = layoutDirection
  }
}

private fun DrawScope.drawPlaceholder(
  shape: Shape,
  color: Color,
  highlight: PlaceholderHighlight?,
  progress: Float,
  lastOutline: Outline?,
  lastLayoutDirection: LayoutDirection?,
  lastSize: Size?,
): Outline? {
  // shortcut to avoid Outline calculation and allocation
  if(shape === RectangleShape) {
    // Draw the initial background color
    drawRect(color = color)

    if(highlight != null) {
      drawRect(
        brush = highlight.brush(progress, size),
        alpha = highlight.alpha(progress),
      )
    }
    // We didn't create an outline so return null
    return null
  }

  // Otherwise we need to create an outline from the shape
  val outline = lastOutline.takeIf {
    size == lastSize && layoutDirection == lastLayoutDirection
  } ?: shape.createOutline(size, layoutDirection, this)

  // Draw the placeholder color
  drawOutline(outline = outline, color = color)

  if(highlight != null) {
    drawOutline(
      outline = outline,
      brush = highlight.brush(progress, size),
      alpha = highlight.alpha(progress),
    )
  }

  // Return the outline we used
  return outline
}

private inline fun DrawScope.withLayer(
  paint: Paint,
  drawBlock: DrawScope.() -> Unit,
) = drawIntoCanvas { canvas ->
  canvas.saveLayer(size.toRect(), paint)
  drawBlock()
  canvas.restore()
}
