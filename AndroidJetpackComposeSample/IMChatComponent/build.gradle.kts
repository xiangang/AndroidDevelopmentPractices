plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("dagger.hilt.android.plugin")
}

android {
    resourcePrefix = "im_chat_"
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
    testImplementation(TestLib.junit)
    androidTestImplementation(TestLib.espresso)
    androidTestImplementation(Compose.uiTooling)
    androidTestApi(Compose.test)
    debugImplementation(Compose.uiTooling)
    implementation(Hilt.android)
    implementation(Hilt.navigation_fragment)
    kapt(Hilt.android_compiler)
    implementation(Compose.material3)
    implementation(Compose.unit)
    implementation(Compose.util)
    implementation(Compose.liveData)
    implementation(Compose.saveable)
    implementation(Compose.viewbinding)
    implementation(Compose.googlefonts)
    implementation(Lifecycle.runtimeCompose)
    implementation(project(mapOf("path" to ":FastMvvm")))
    implementation(project(mapOf("path" to ":IMCore")))
    implementation(project(mapOf("path" to ":IMCommonUI")))
}