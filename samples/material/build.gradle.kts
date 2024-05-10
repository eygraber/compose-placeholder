import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.android.application")
  kotlin("android")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
  alias(libs.plugins.baselineprofile)
}

group = "samples-material"

android {
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  namespace = "com.eygraber.compose.placeholder.samples.material"

  defaultConfig {
    applicationId = "com.eygraber.compose.placeholder.samples.material"
    minSdk = libs.versions.android.sdk.min.get().toInt()
    targetSdk = libs.versions.android.sdk.target.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }

  lint {
    checkDependencies = true
    checkReleaseBuilds = false
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

dependencies {
  implementation(projects.placeholderMaterial)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.baseline.profiles.profileinstaller)

  baselineProfile(projects.baselineProfiles.placeholder)
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
