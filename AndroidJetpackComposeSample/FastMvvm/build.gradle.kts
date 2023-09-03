plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("dagger.hilt.android.plugin")
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
    testApi(TestLib.junit)
    androidTestImplementation(TestLib.androidJunit)
    testApi(TestLib.espresso)
    androidTestApi(Compose.test)
    debugApi(Compose.uiTooling)
    api(Kotlin.stdlib)
    api(AndroidX.appcompat)
    api(AndroidX.constraintlayout)
    api(AndroidX.cardview)
    api(AndroidX.recyclerView)
    api(AndroidX.coreKtx)
    api(AndroidX.activityKtx)
    api(AndroidX.fragmentKtx)
    api(AndroidX.swiperefreshlayout)
    api(AndroidX.legacySupportV4)
    api(NavigationLib.fragmentKtx)
    api(NavigationLib.uiKtx)
    api(NavigationLib.compose)
    api(Lifecycle.liveDataKtx)
    api(Lifecycle.viewModelKtx)
    api(Lifecycle.runtimeKtx)
    api(Paging.runtimeKtx)
    api(Paging.compose)
    api(Google.material)
    api(Compose.ui)
    api(Compose.material3)
    api(Compose.activity)
    api(Compose.preview)
    api(Compose.lifecycleViewModel)
    api(ThirdParty.linkageRecyclerview)
    api(ThirdParty.utilCodex)
    api(Hilt.android)
    api(Hilt.navigation_fragment)
    api(Log.slf4jApi)
    api(Log.logbackAndroid)
    api(Log.kotlinLogging)
    api(Hilt.navigation_fragment)
    kapt(Hilt.android_compiler)
    api(project(mapOf("path" to ":CommonUI")))
    api(project(mapOf("path" to ":CommonLib")))
    api(project(mapOf("path" to ":CommonUtils")))
}