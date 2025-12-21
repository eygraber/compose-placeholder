package com.eygraber.compose.placeholder.sample.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val ArrowDownward: ImageVector
  get() {
    if(internalArrowDownward != null) {
      return requireNotNull(internalArrowDownward)
    }
    internalArrowDownward = materialIcon(name = "Filled.ArrowDownward") {
      materialPath {
        moveTo(20.0f, 12.0f)
        lineToRelative(-1.41f, -1.41f)
        lineTo(13.0f, 16.17f)
        verticalLineTo(4.0f)
        horizontalLineToRelative(-2.0f)
        verticalLineToRelative(12.17f)
        lineToRelative(-5.58f, -5.59f)
        lineTo(4.0f, 12.0f)
        lineToRelative(8.0f, 8.0f)
        lineToRelative(8.0f, -8.0f)
        close()
      }
    }
    return requireNotNull(internalArrowDownward)
  }

private var internalArrowDownward: ImageVector? = null
