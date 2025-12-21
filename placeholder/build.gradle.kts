import com.eygraber.conventions.compose.cmpTest
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
}

android {
  namespace = "com.eygraber.compose.placeholder"
}

kotlin {
  defaultKmpTargets(
    project = project
  )

  js {
    browser {
      testTask {
        enabled = false
      }
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(compose.foundation)
      implementation(libs.compose.uiUtil)
      implementation(libs.kotlinx.coroutines.core)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.kotlinx.coroutines.test)
      implementation(libs.test.kotest.assertions)

      @OptIn(ExperimentalComposeLibrary::class)
      implementation(compose.uiTest)
    }

    cmpTest.dependencies {
      @OptIn(ExperimentalComposeLibrary::class)
      implementation(compose.uiTest)
    }

    androidUnitTest.dependencies {
      implementation(libs.test.compose.android.uiJunit)
      implementation(libs.test.compose.android.uiTestManifest)
      implementation(libs.test.robolectric)
    }

    jvmTest {
      dependencies {
        implementation(compose.desktop.currentOs)
        implementation(compose.desktop.uiTestJUnit4)
      }
    }
  }
}

android {
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
    unitTests.all {
      it.useJUnit()
    }
    animationsDisabled = true
  }
}
