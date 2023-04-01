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
        kotlinCompilerExtensionVersion = Compose.version
    }
}

dependencies {
    testImplementation(TestLib.junit)
    testImplementation(TestLib.espresso)
    debugImplementation(Compose.uiTooling)
    androidTestImplementation(Compose.test)
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
    api(Compose.material)
    api(Compose.activity)
    api(Compose.preview)
    api(Compose.lifecycleViewModel)
    api(Compose.uiTooling)
    api(ThirdParty.linkageRecyclerview)
    api(ThirdParty.utilCodex)
    api(Hilt.android)
    api(Hilt.lifecycle_viewmodel)
    kapt(Hilt.android_compiler)
    kapt(Hilt.compiler)
    api(project(mapOf("path" to ":CommonUI")))
    api(project(mapOf("path" to ":CommonLib")))
    api(project(mapOf("path" to ":CommonUtils")))
}