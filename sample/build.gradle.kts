import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

kotlin {
  kmpTargets(
    project = project,
    android = false,
    ios = false,
    jvm = true,
    js = false
  )

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.placeholder)
        implementation(projects.placeholderMaterial)
        implementation(compose.material)
        implementation(compose.materialIconsExtended)
        implementation(libs.imageLoader)
      }
    }
  }
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
