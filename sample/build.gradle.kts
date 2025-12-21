import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt2")
}

kotlin {
  kmpTargets(
    KmpTarget.Jvm,
    project = project,
    ignoreDefaultTargets = true
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
