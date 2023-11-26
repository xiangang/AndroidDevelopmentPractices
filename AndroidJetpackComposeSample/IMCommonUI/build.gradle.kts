plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    compileSdk = BuildConfig.compileSdk
    defaultConfig {
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion
        testInstrumentationRunner = BuildConfig.testInstrumentationRunner
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Compose.kotlinCompilerExtensionVersion
    }
}

dependencies {
    testImplementation(TestLib.junit)
    androidTestImplementation(TestLib.espresso)
    androidTestImplementation(Compose.uiTooling)
    androidTestApi(Compose.test)
    debugImplementation(Compose.uiTooling)
    api(Compose.material3)
    api(Compose.unit)
    api(Compose.util)
    api(Compose.liveData)
    api(Compose.saveable)
    api(Compose.viewbinding)
    api(Compose.googlefonts)
    api(Compose.accompanistSystemUiController)
    api(Compose.activity)
    api(Lifecycle.runtimeCompose)
    api(Coil.coil)
    api(Coil.coilCompose)
    api(Coil.coilGif)
    api(Coil.coilSvg)
    api(Coil.coilVideo)
    api("io.github.TheMelody:gd_compose:1.0.2")
    api("io.github.leavesczy:matisse:1.1.3")
    api("com.google.android.exoplayer:exoplayer-core:2.18.5")
    api("com.google.android.exoplayer:exoplayer-ui:2.18.5")
}