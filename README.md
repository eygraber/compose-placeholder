# Placeholder

[![Maven Central](https://img.shields.io/maven-central/v/com.eygraber/compose-placeholder)](https://search.maven.org/search?q=g:com.eygraber+a:compose-placeholder)

A Compose multiplatform library (android, ios, jvm, and js) which provides a [modifier][modifier] for displaying 'placeholder' UI while content is loading.

In July 2023, the Accompanist project deprecated the placeholder artifact. This library was forked and published to replace it. See [the migration guide](https://github.com/eygraber/compose-placeholder/wiki/Accompanist-Migration-Guide)

More information on the UX provided by this library can be found on the Material Theming [Placeholder UI](https://material.io/design/communication/launch-screen.html#placeholder-ui) guidelines.

There are actually three versions of the library available:

* *Placeholder Foundation*: Provides the base functionality and depends on Jetpack Compose Foundation. This version requires the app to provide all of the colors to display.
* *Placeholder Material*. This uses the foundation library above, but also provides sensible default colors using your app's Material color palette.
* *Placeholder Material 3*. This uses the foundation library above, but also provides sensible default colors using your app's Material 3 color palette.

> [!NOTE]
You only need to use one of the libraries, and most apps should use **Placeholder Material** or **Placeholder Material 3**. The APIs of the libraries are (mostly) equivalent with only the imports being different. Where possible we have provided equivalent code samples below.

## Gradle

```kotlin
repositories {
  mavenCentral()
}

dependencies {
  // If you're using Material, use compose-placeholder-material
  implementation("com.eygraber:compose-placeholder-material:1.0.7")

  // If you're using Material 3, use compose-placeholder-material3
  implementation("com.eygraber:compose-placeholder-material3:1.0.7")

  // Otherwise use the foundation version
  implementation("com.eygraber:compose-placeholder:1.0.7")
}
```

### Library Snapshots

Snapshots of the current development version of this library are available, which track the latest commit.

```kotlin
repositories {
  maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
  implementation("com.eygraber:compose-placeholder:1.0.8-SNAPSHOT-SNAPSHOT-SNAPSHOT-SNAPSHOT-SNAPSHOT-SNAPSHOT-SNAPSHOT-SNAPSHOT-SNAPSHOT")
}
```

## Basic usage

At the most basic usage, the modifier will draw a shape over your composable content, filled with the provided color.

![Basic Placeholder demo](https://github.com/eygraber/compose-placeholder/blob/master/docs/res/basic.jpg?raw=true)

### "Placeholder Material"

    ``` kotlin
    import com.eygraber.compose.placeholder.material.placeholder

    Text(
        text = "Content to display after content has loaded",
        modifier = Modifier
            .padding(16.dp)
            .placeholder(visible = true)
    )
    ```

### "Placeholder Foundation"

    ``` kotlin
    import com.eygraber.compose.placeholder.placeholder

    Text(
        text = "Content to display after content has loaded",
        modifier = Modifier
            .padding(16.dp)
            .placeholder(
                visible = true,
                color = Color.Gray,
                // optional, defaults to RectangleShape
                shape = RoundedCornerShape(4.dp),
            )
    )
    ```

## Placeholder highlights

The library also provides some 'highlight' animations to entertain the user while they are waiting. There are two provided by the library, but you can also provide your own.

### Fade

![Fade Placeholder demo](https://github.com/eygraber/compose-placeholder/blob/master/docs/res/fade.gif?raw=true)

This highlight fades a color over the entire placeholder in and out.

### "Placeholder Material"

    ``` kotlin
    import com.eygraber.compose.placeholder.PlaceholderHighlight
    import com.eygraber.compose.placeholder.material.placeholder
    import com.eygraber.compose.placeholder.material.fade

    Text(
        text = "Content to display after content has loaded",
        modifier = Modifier
            .padding(16.dp)
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.fade(),
            )
    )
    ```

### "Placeholder Foundation"

    ``` kotlin
    import com.eygraber.compose.placeholder.PlaceholderHighlight
    import com.eygraber.compose.placeholder.placeholder
    import com.eygraber.compose.placeholder.fade

    Text(
        text = "Content to display after content has loaded",
        modifier = Modifier
            .padding(16.dp)
            .placeholder(
                visible = true,
                color = Color.Gray,
                // optional, defaults to RectangleShape
                shape = RoundedCornerShape(4.dp),
                highlight = PlaceholderHighlight.fade(
                    highlightColor = Color.White,
                ),
            )
    )
    ```

### Shimmer

![Shimmer Placeholder demo](https://github.com/eygraber/compose-placeholder/blob/master/docs/res/shimmer.gif?raw=true)

This displays a gradient shimmer effect which emanates from the top-start corner.

### "Placeholder Material"

    ``` kotlin
    import com.eygraber.compose.placeholder.PlaceholderHighlight
    import com.eygraber.compose.placeholder.material.placeholder
    import com.eygraber.compose.placeholder.material.shimmer

    Text(
        text = "Content to display after content has loaded",
        modifier = Modifier
            .padding(16.dp)
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.shimmer(),
            )
    )
    ```

### "Placeholder Foundation"

    ``` kotlin
    import com.eygraber.compose.placeholder.PlaceholderHighlight
    import com.eygraber.compose.placeholder.placeholder
    import com.eygraber.compose.placeholder.shimmer

    Text(
        text = "Content to display after content has loaded",
        modifier = Modifier
            .padding(16.dp)
            .placeholder(
                visible = true,
                color = Color.Gray,
                // optional, defaults to RectangleShape
                shape = RoundedCornerShape(4.dp),
                highlight = PlaceholderHighlight.shimmer(
                    highlightColor = Color.White,
                ),
            )
    )
    ```

## Contributions

Please contribute! We will gladly review any pull requests.

## License

```
Copyright 2021 The Android Open Source Project
Copyright 2023 Eliezer Graber
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

[modifier]: https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier
