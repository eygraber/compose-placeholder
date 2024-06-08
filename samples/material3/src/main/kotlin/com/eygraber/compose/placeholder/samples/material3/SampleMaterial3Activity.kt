package com.eygraber.compose.placeholder.samples.material3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.fade
import com.eygraber.compose.placeholder.material3.placeholder
import com.eygraber.compose.placeholder.material3.shimmer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class SampleMaterial3Activity : ComponentActivity() {
  @OptIn(ExperimentalComposeUiApi::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      var showPlaceholder by remember { mutableStateOf(true) }

      LaunchedEffect(Unit) {
        launch {
          delay(3.seconds)
          showPlaceholder = false
        }
      }

      Box(
        modifier = Modifier.semantics {
          testTagsAsResourceId = true
        }
      ) {
        Column(
          verticalArrangement = Arrangement.SpaceBetween,
          modifier = Modifier.testTag("content")
        ) {
          BasicText(
            text = "Plain Placeholder",
            modifier = Modifier.placeholder(
              visible = showPlaceholder,
            )
          )

          BasicText(
            text = "Fade Placeholder",
            modifier = Modifier.placeholder(
              visible = showPlaceholder,
              highlight = PlaceholderHighlight.fade(),
            )
          )

          BasicText(
            text = "Shimmer Placeholder",
            modifier = Modifier.placeholder(
              visible = showPlaceholder,
              highlight = PlaceholderHighlight.shimmer(),
            )
          )
        }
      }
    }

    reportFullyDrawn()
  }
}
