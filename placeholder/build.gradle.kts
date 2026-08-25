import com.eygraber.conventions.compose.cmpTest
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

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

  android {
    androidResources.enable = true

    withHostTest {
      isIncludeAndroidResources = true
    }

    withDeviceTest {
      animationsDisabled = true
    }
  }

  js {
    // an executable binary is needed so that webpack bundles the Skiko runtime for Compose UI tests
    binaries.executable()

    browser {
      testTask {
        enabled = false
      }
    }
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    binaries.executable()
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

tasks.withType(Test::class.java).configureEach {
  maxHeapSize = "4g"
}
