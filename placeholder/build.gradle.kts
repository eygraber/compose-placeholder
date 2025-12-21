import com.android.build.api.dsl.androidLibrary
import com.eygraber.conventions.compose.cmpTest

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-kmp-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.compose.placeholder",
  )

  androidLibrary {
    withHostTest {
      isIncludeAndroidResources = true
    }

    withDeviceTest {
      animationsDisabled = true
    }
  }

  js {
    browser {
      testTask {
        enabled = false
      }
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.foundation)
      implementation(libs.compose.uiUtil)
      implementation(libs.kotlinx.coroutines.core)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.kotlinx.coroutines.test)
      implementation(libs.test.kotest.assertions)

      implementation(libs.compose.uiTest)
    }

    cmpTest.dependencies {
      implementation(libs.compose.uiTest)
    }

    named("androidHostTest").dependencies {
      implementation(libs.test.compose.android.uiJunit)
      implementation(libs.test.compose.android.uiTestManifest)
      implementation(libs.test.robolectric)
    }

    jvmTest {
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(libs.test.compose.desktop.uiJunit)
      }
    }
  }
}
