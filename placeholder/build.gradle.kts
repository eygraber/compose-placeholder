import com.eygraber.conventions.kotlin.kmp.androidUnitTest

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.baselineprofile)
}

android {
  namespace = "com.eygraber.compose.placeholder"
}

kotlin {
  defaultKmpTargets(
    project = project
  )

  sourceSets {
    commonMain {
      dependencies {
        implementation(compose.foundation)
        implementation(libs.compose.uiUtil)
        implementation(libs.kotlinx.coroutines.core)
      }
    }

    commonTest {
      dependencies {
        implementation(libs.kotlinx.coroutines.test)
        implementation(libs.test.kotest.assertions)
      }
    }

    androidUnitTest {
      dependencies {
        implementation(libs.test.compose.android.uiJunit)
        implementation(libs.test.compose.android.uiTestManifest)
        implementation(libs.test.robolectric)
      }
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
  @Suppress("UnstableApiUsage")
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
    unitTests.all {
      it.useJUnit()
    }
    animationsDisabled = true
  }

  dependencies {
    baselineProfile(projects.baselineProfiles.placeholder)
  }
}

baselineProfile {
  filter {
    include("com.eygraber.compose.placeholder.**")
  }
}
