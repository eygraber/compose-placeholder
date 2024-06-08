import com.android.build.api.dsl.ManagedVirtualDevice
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.android.test")
  kotlin("android")
  id("com.eygraber.conventions-kotlin-library")
  alias(libs.plugins.baselineprofile)
}

group = "baseline-profiles-material3"

android {
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  namespace = "com.eygraber.compose.placeholder.baseline.profiles.material3"

  defaultConfig {
    minSdk = libs.versions.android.sdk.baseline.min.get().toInt()
    targetSdk = libs.versions.android.sdk.target.get().toInt()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  targetProjectPath = ":samples:material3"

  testOptions.managedDevices.devices {
    register<ManagedVirtualDevice>("pixel6Api34") {
      device = "Pixel 6"
      apiLevel = 34
      systemImageSource = "google"
    }
  }
}

baselineProfile {
  managedDevices += "pixel6Api34"
  useConnectedDevices = false
}

dependencies {
  implementation(libs.androidx.baseline.profiles.benchmark.macro.junit4)
  implementation(libs.androidx.baseline.profiles.espresso.core)
  implementation(libs.androidx.baseline.profiles.junit)
  implementation(libs.androidx.baseline.profiles.uiautomator)
}

androidComponents {
  onVariants { v ->
    val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
    v.instrumentationRunnerArguments.put(
      "targetAppId",
      v.testedApks.map { artifactsLoader.load(it)?.applicationId }
    )
  }
}

gradleConventions {
  kotlin {
    explicitApiMode = ExplicitApiMode.Disabled
  }
}
