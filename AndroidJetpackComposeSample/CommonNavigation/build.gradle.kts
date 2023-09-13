plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}


android {
      = BuildConfig.compileSdk

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

}

dependencies {

    //JetPack navigation
    implementation(NavigationLib.fragmentKtx)
    implementation(NavigationLib.uiKtx)
    implementation(NavigationLib.dynamic)

    //JetPack lifecycle
    implementation(Lifecycle.liveDataKtx)
    implementation(Lifecycle.viewModelKtx)
    implementation(Lifecycle.viewModelSavedState)
    implementation(Lifecycle.commonJava8)
    implementation(Lifecycle.service)
    implementation(Lifecycle.runtimeKtx)

    // Dagger & Hilt
    implementation(Hilt.android)
    kapt (Hilt.android_compiler)
    kapt (Hilt.compiler)

}