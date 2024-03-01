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

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.window.DialogWindowProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.graphics.HardwareRendererCompat
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@OptIn(ExperimentalTestApi::class)
/**
 * This is only necessary until https://github.com/robolectric/robolectric/issues/8071 is resolved
 */
internal fun SemanticsNodeInteraction.captureToImage(): ImageBitmap {
  val node = fetchSemanticsNode("Failed to capture a node to bitmap.")
  // Validate we are in popup
  val popupParentMaybe = node.findClosestParentNode(includeSelf = true) {
    it.config.contains(SemanticsProperties.IsPopup)
  }
  if(popupParentMaybe != null) {
    return processMultiWindowScreenshot(node)
  }

  val view = (node.root!! as ViewRootForTest).view

  // If we are in dialog use its window to capture the bitmap
  val dialogParentNodeMaybe = node.findClosestParentNode(includeSelf = true) {
    it.config.contains(SemanticsProperties.IsDialog)
  }
  var dialogWindow: Window? = null
  if(dialogParentNodeMaybe != null) {
    require(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      // TODO(b/163023027)
      "Cannot currently capture dialogs on API lower than 28!"
    }

    dialogWindow = findDialogWindowProviderInParent(view)?.window
      ?: throw IllegalArgumentException(
        "Could not find a dialog window provider to capture its bitmap"
      )
  }

  val windowToUse = dialogWindow ?: view.context.getActivityWindow()

  val nodeBounds = node.boundsInRoot
  val nodeBoundsRect = Rect(
    nodeBounds.left.roundToInt(),
    nodeBounds.top.roundToInt(),
    nodeBounds.right.roundToInt(),
    nodeBounds.bottom.roundToInt()
  )

  val locationInWindow = intArrayOf(0, 0)
  view.getLocationInWindow(locationInWindow)
  val x = locationInWindow[0]
  val y = locationInWindow[1]

  // Now these are bounds in window
  nodeBoundsRect.offset(x, y)

  return windowToUse.captureRegionToImage(nodeBoundsRect)
}

private fun SemanticsNode.findClosestParentNode(
  includeSelf: Boolean = false,
  selector: (SemanticsNode) -> Boolean
): SemanticsNode? {
  var currentParent = if(includeSelf) this else parent
  while(currentParent != null) {
    if(selector(currentParent)) {
      return currentParent
    }
    else {
      currentParent = currentParent.parent
    }
  }

  return null
}

@ExperimentalTestApi
private fun processMultiWindowScreenshot(
  node: SemanticsNode
): ImageBitmap {
  val nodePositionInScreen = findNodePosition(node)
  val nodeBoundsInRoot = node.boundsInRoot

  val combinedBitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()

  val finalBitmap = Bitmap.createBitmap(
    combinedBitmap,
    (nodePositionInScreen.x + nodeBoundsInRoot.left).roundToInt(),
    (nodePositionInScreen.y + nodeBoundsInRoot.top).roundToInt(),
    nodeBoundsInRoot.width.roundToInt(),
    nodeBoundsInRoot.height.roundToInt()
  )
  return finalBitmap.asImageBitmap()
}

private fun findNodePosition(
  node: SemanticsNode
): Offset {
  val view = (node.root!! as ViewRootForTest).view
  val locationOnScreen = intArrayOf(0, 0)
  view.getLocationOnScreen(locationOnScreen)
  val x = locationOnScreen[0]
  val y = locationOnScreen[1]

  return Offset(x.toFloat(), y.toFloat())
}

internal fun findDialogWindowProviderInParent(view: View): DialogWindowProvider? {
  if(view is DialogWindowProvider) {
    return view
  }
  val parent = view.parent ?: return null
  if(parent is View) {
    return findDialogWindowProviderInParent(parent)
  }
  return null
}

private fun Context.getActivityWindow(): Window {
  fun Context.getActivity(): Activity = when(this) {
    is Activity -> this
    is ContextWrapper -> this.baseContext.getActivity()
    else -> error(
      "Context is not an Activity context, but a ${javaClass.simpleName} context. " +
        "An Activity context is required to get a Window instance"
    )
  }
  return getActivity().window
}

private fun Window.captureRegionToImage(
  boundsInWindow: Rect,
): ImageBitmap =
  // Turn on hardware rendering, if necessary
  withDrawingEnabled {
    // Then we generate the bitmap
    generateBitmap(boundsInWindow).asImageBitmap()
  }

private fun <R> withDrawingEnabled(block: () -> R): R {
  val wasDrawingEnabled = HardwareRendererCompat.isDrawingEnabled()
  try {
    if(!wasDrawingEnabled) {
      HardwareRendererCompat.setDrawingEnabled(true)
    }
    return block.invoke()
  }
  finally {
    if(!wasDrawingEnabled) {
      HardwareRendererCompat.setDrawingEnabled(false)
    }
  }
}

private fun Window.generateBitmap(boundsInWindow: Rect): Bitmap {
  val destBitmap =
    Bitmap.createBitmap(
      boundsInWindow.width(),
      boundsInWindow.height(),
      Bitmap.Config.ARGB_8888
    )
  generateBitmapFromPixelCopy(boundsInWindow, destBitmap)
  return destBitmap
}

private fun Window.generateBitmapFromPixelCopy(boundsInWindow: Rect, destBitmap: Bitmap) {
  val latch = CountDownLatch(1)
  var copyResult = 0
  val onCopyFinished = PixelCopy.OnPixelCopyFinishedListener { result ->
    copyResult = result
    latch.countDown()
  }
  PixelCopy.request(
    this,
    boundsInWindow,
    destBitmap,
    onCopyFinished,
    Handler(Looper.getMainLooper())
  )

  if(!latch.await(1, TimeUnit.SECONDS)) {
    throw AssertionError("Failed waiting for PixelCopy!")
  }
  if(copyResult != PixelCopy.SUCCESS) {
    throw AssertionError("PixelCopy failed!")
  }
}
