import com.google.protobuf.gradle.*

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.protobuf")
    id("kotlinx-serialization")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    compileSdk = Config.compileSdk

    defaultConfig {
        applicationId = Config.applicationId
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        versionCode = Config.versionCode
        versionName = Config.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    //Retrofit
    implementation (Dependencies.OkHttp.okHttp)
    implementation (Dependencies.Retrofit.retrofit)
    //Kotlinx Serialization
    implementation (Dependencies.Kotlin.serialization)
    implementation (Dependencies.Kotlin.converter)
    //navigation
    implementation (Dependencies.Navigation.fragment)
    implementation (Dependencies.Navigation.uiKtx)
    implementation (Dependencies.Navigation.viewModel)
    //Dagger.Hilt
    implementation (Dependencies.Hilt.android)
    kapt (Dependencies.Hilt.compiler)
    //DataStore
    implementation (Dependencies.ProtoDataStore.dataStore)
    implementation (Dependencies.ProtoDataStore.protobuf)
    implementation (Dependencies.ProtoDataStore.preferences)
    //ViewBindingDelegate
    implementation (Dependencies.ViewBindingDelegate.delegate)
    //SplashScreen
    implementation (Dependencies.UI.splashScreen)

    implementation (Dependencies.AndroidX.coreKtx)
    implementation (Dependencies.AndroidX.appCompat)
    implementation (Dependencies.AndroidX.constraintLayout)

    implementation (Dependencies.Google.material)


    testImplementation (Dependencies.Test.jUnit)
    androidTestImplementation (Dependencies.Test.extJUnit)
    androidTestImplementation (Dependencies.Test.espresso)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.11.0"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins{
                create("java") {
                    option("lite")
                }
            }
        }
    }
}