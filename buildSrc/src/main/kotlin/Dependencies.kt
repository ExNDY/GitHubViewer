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
    }

    object Google {
        const val material = "com.google.android.material:material:1.5.0"
    }

    object UI {
        private const val markwonVersion = "4.9.6"

        const val splashScreen = "androidx.core:core-splashscreen:1.0.0-beta01"
        const val markwon = "implementation io.noties.markwon:core:${markwonVersion}"
        const val markwonRecycler = "implementation io.noties.markwon:recycler:${markwonVersion}"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.7.0"
        const val appCompat = "androidx.appcompat:appcompat:1.4.1"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.3"
    }
}