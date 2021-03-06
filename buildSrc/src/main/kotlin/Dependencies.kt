object Dependencies {
    object Hilt {
        private const val version = "2.41"
        const val android = "com.google.dagger:hilt-android:$version"
        const val compiler = "com.google.dagger:hilt-android-compiler:$version"
    }

    object ViewBindingDelegate {
        private const val version = "1.5.6"
        const val delegate = "com.github.kirich1409:viewbindingpropertydelegate:$version"
    }

    object Retrofit {
        private const val version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$version"
        const val scalars = "com.squareup.retrofit2:converter-scalars:$version"
    }

    object OkHttp {
        private const val version = "4.9.3"
        const val okHttp = "com.squareup.okhttp3:okhttp:$version"
    }

    object Kotlin {
        private const val version = "1.3.2"
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"

        private const val converterVersion = "0.8.0"
        const val converter =
            "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$converterVersion"
    }

    object Test {
        const val jUnit = "junit:junit:4.13.2"
        const val extJUnit = "androidx.test.ext:junit:1.1.3"
        const val espresso = "androidx.test.espresso:espresso-core:3.4.0"
    }

    object Debug{
        const val timber = "com.jakewharton.timber:timber:5.0.1"
    }

    object ProtoDataStore {
        private const val version = "1.0.0"
        const val dataStore = "androidx.datastore:datastore:$version"
        const val preferences = "androidx.datastore:datastore-preferences:$version"

        const val protobuf = "com.google.protobuf:protobuf-javalite:3.11.0"
    }

    object Navigation {
        private const val version = "2.4.1"
        const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
        const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"

        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
    }

    object Google {
        const val material = "com.google.android.material:material:1.5.0"
    }

    object UI {
        const val splashScreen = "androidx.core:core-splashscreen:1.0.0-beta01"
        const val coil = "io.coil-kt:coil:2.0.0-rc02"
    }

    object Markdown{
        private const val markwonVersion = "4.6.2"

        const val markwon = "io.noties.markwon:core:${markwonVersion}"
        const val markwonRecycler = "io.noties.markwon:recycler:${markwonVersion}"
        const val markwonLatex = "io.noties.markwon:ext-latex:${markwonVersion}"
        const val markwonStrikethrough = "io.noties.markwon:ext-strikethrough:${markwonVersion}"
        const val markwonTables = "io.noties.markwon:ext-tables:${markwonVersion}"
        const val markwonHtml = "io.noties.markwon:html:${markwonVersion}"
        const val markwonGlide = "io.noties.markwon:image-glide:${markwonVersion}"
        const val markwonInlineParser = "io.noties.markwon:inline-parser:${markwonVersion}"
        const val markwonLinkify = "io.noties.markwon:linkify:${markwonVersion}"
        const val markwonSimple = "io.noties.markwon:simple-ext:${markwonVersion}"
        const val markdownImage = "io.noties.markwon:image:${markwonVersion}"

        const val gif = "pl.droidsonroids.gif:android-gif-drawable:1.2.24"
        const val svg = "com.caverock:androidsvg-aar:1.4"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.7.0"
        const val appCompat = "androidx.appcompat:appcompat:1.4.1"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.3"
    }
}