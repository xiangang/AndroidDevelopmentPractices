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
    implementation(Compose.material3)
    implementation(Compose.unit)
    implementation(Compose.util)
    implementation(Compose.liveData)
    implementation(Compose.saveable)
    implementation(Compose.viewbinding)
    implementation(Compose.googlefonts)
    implementation(Compose.accompanistSystemUiController)
    implementation(Compose.activity)
    implementation(Lifecycle.runtimeCompose)
    implementation(Coil.coil)
    implementation(Coil.coilCompose)
    implementation(Coil.coilGif)
    implementation(Coil.coilSvg)
    implementation(Coil.coilVideo)
    implementation ("io.github.TheMelody:gd_compose:1.0.2")
    implementation("com.github.leavesCZY:Matisse:1.0.4")
}