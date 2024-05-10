import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.android.application")
  kotlin("android")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-placeholder"

android {
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  namespace = "com.eygraber.compose.placeholder.samples"

  defaultConfig {
    applicationId = "com.eygraber.compose.placeholder.samples"
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
  implementation(projects.placeholder)

  implementation(compose.foundation)

  implementation(libs.androidx.activity.compose)
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
