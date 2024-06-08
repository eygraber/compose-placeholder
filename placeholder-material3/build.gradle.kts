plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.baselineprofile)
}

android {
  namespace = "com.eygraber.compose.placeholder.material3"

  dependencies {
    baselineProfile(projects.baselineProfiles.material3)
  }
}

kotlin {
  defaultKmpTargets(
    project = project
  )

  sourceSets {
    commonMain {
      dependencies {
        api(projects.placeholder)
        api(compose.material3)
      }
    }
  }
}

baselineProfile {
  filter {
    include("com.eygraber.compose.placeholder.material3.**")
  }
}
