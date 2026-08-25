package com.eygraber.compose.placeholder

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

// Robolectric has no android-all jar for targetSdk yet, so run against the newest SDK it supports
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Config.NEWEST_SDK])
class AndroidPlaceholderHighlightTest : PlaceholderHighlightTest()
