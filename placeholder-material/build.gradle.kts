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
    androidNamespace = "com.eygraber.compose.placeholder.material",
  )

  js {
    // an executable binary is needed so that webpack bundles the Skiko runtime for Compose UI tests
    binaries.executable()
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    binaries.executable()
  }

  sourceSets {
    commonMain {
      dependencies {
        api(projects.placeholder)
        implementation(libs.compose.material)
      }
    }
  }
}
