package com.eygraber.compose.placeholder

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction

@OptIn(ExperimentalTestApi::class)
internal expect fun SemanticsNodeInteraction.captureToImage(): ImageBitmap
