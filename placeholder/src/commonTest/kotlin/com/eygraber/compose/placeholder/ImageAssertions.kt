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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.toPixelMap
import io.kotest.matchers.floats.plusOrMinus
import io.kotest.matchers.shouldBe

/**
 * Asserts that the color at a specific pixel in the bitmap at ([x], [y]) is [expected].
 */
fun PixelMap.assertPixelColor(expected: Color, x: Int, y: Int, tolerance: Float = 0.02f) {
  val color = this[x, y]
  color.red shouldBe (expected.red plusOrMinus tolerance)
  color.green shouldBe (expected.green plusOrMinus tolerance)
  color.blue shouldBe (expected.blue plusOrMinus tolerance)
  color.alpha shouldBe (expected.alpha plusOrMinus tolerance)
}

/**
 * Assert that all of the pixels in this image as of the [expected] color.
 */
fun ImageBitmap.assertPixels(expected: Color, tolerance: Float = 0.001f) {
  toPixelMap().buffer.forEach { pixel ->
    val color = Color(pixel)
    color.red shouldBe (expected.red plusOrMinus tolerance)
    color.green shouldBe (expected.green plusOrMinus tolerance)
    color.blue shouldBe (expected.blue plusOrMinus tolerance)
    color.alpha shouldBe (expected.alpha plusOrMinus tolerance)
  }
}

/**
 * Asserts that the colors at specific pixels in the vertices of bitmap is [expected].
 */
fun ImageBitmap.assertPixelsOfVertices(expected: Color) {
  toPixelMap().run {
    assertPixelColor(expected, 0, 0)
    assertPixelColor(expected, 0, height - 1)
    assertPixelColor(expected, width - 1, 0)
    assertPixelColor(expected, width - 1, height - 1)
  }
}
