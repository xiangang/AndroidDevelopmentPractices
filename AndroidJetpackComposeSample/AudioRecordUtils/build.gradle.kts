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
    testImplementation(TestLib.junit)
    testImplementation(TestLib.espresso)
    debugImplementation(Compose.uiTooling)
    androidTestImplementation(Compose.test)
    implementation(AndroidX.appcompat)
    implementation(AndroidX.constraintlayout)
    implementation(AndroidX.cardview)
    implementation(AndroidX.recyclerView)
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.activityKtx)
    implementation(AndroidX.fragmentKtx)
    implementation(AndroidX.swiperefreshlayout)
    implementation(AndroidX.legacySupportV4)
    implementation(NavigationLib.fragmentKtx)
    implementation(NavigationLib.uiKtx)
    implementation(NavigationLib.compose)
    implementation(Lifecycle.liveDataKtx)
    implementation(Lifecycle.viewModelKtx)
    implementation(Lifecycle.runtimeKtx)
    implementation(Paging.runtimeKtx)
    implementation(Paging.compose)
    implementation(Google.material)
    implementation(Compose.ui)
    implementation(Compose.materialIcons)
    implementation(Compose.activity)
    implementation(Compose.preview)
    implementation(Compose.lifecycleViewModel)
    implementation(Compose.uiTooling)
    implementation(Coil.coilCompose)
    implementation(Coil.coilGif)
    implementation(Coil.coilSvg)
    implementation(ThirdParty.linkageRecyclerview)
    implementation(Hilt.android)
    kapt(Hilt.android_compiler)
    implementation(project(mapOf("path" to ":CommonUI")))
    implementation(project(mapOf("path" to ":CommonLib")))
    implementation(project(mapOf("path" to ":CommonUtils")))
    implementation(project(mapOf("path" to ":FastMvvm")))
}