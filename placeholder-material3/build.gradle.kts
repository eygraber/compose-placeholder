plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

android {
  namespace = "com.eygraber.compose.placeholder.material3"
}

kotlin {
  kmpTargets(
    KmpTarget.Android,
    project = project
  )

  sourceSets {
    commonMain {
      dependencies {
        api(projects.placeholder)
        implementation(compose.material3)
      }
    }
  }
}
