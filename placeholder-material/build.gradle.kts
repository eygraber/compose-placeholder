plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

android {
  namespace = "com.eygraber.compose.placeholder.material"
}

kotlin {
  explicitApi()

  kmpTargets(
    project = project,
    useDefaultTargetHierarchy = true,
    android = true,
    ios = true,
    jvm = true,
    js = true
  )

  sourceSets {
    commonMain {
      dependencies {
        api(projects.placeholder)
        implementation(compose.material)
      }
    }
  }
}
