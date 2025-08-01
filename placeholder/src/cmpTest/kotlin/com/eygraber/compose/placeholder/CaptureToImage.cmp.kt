package com.eygraber.compose.placeholder

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage as skikoCaptureToImage

@OptIn(ExperimentalTestApi::class)
internal actual fun SemanticsNodeInteraction.captureToImage(): ImageBitmap = skikoCaptureToImage()
