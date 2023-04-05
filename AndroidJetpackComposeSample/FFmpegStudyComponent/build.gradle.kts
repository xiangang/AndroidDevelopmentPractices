plugins {
    kotlin("kapt")
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
}


android {
    resourcePrefix = "av_"
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
        kotlinCompilerExtensionVersion = Compose.version
    }
}

dependencies {
    testApi(TestLib.junit)
    testApi(TestLib.espresso)
    testApi(Compose.uiTooling)
    androidTestApi(Compose.test)
    debugImplementation(Compose.uiTooling)
    implementation(Hilt.android)
    implementation(Hilt.navigation_fragment)
    kapt(Hilt.android_compiler)
    implementation(project(mapOf("path" to ":FastMvvm")))
    implementation(project(mapOf("path" to ":FFmpegMobile")))
    implementation(project(mapOf("path" to ":OpenCVMobile")))
    implementation(project(mapOf("path" to ":AudioRecordUtils")))
}