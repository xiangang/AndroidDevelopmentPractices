plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
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
    val composeBom = platform(Compose.bom)
    implementation(composeBom)
    testImplementation(TestLib.junit)
    androidTestImplementation(TestLib.androidJunit)
    androidTestImplementation(TestLib.espresso)
    api(AndroidX.appcompat)
    api(AndroidX.constraintlayout)
    api(AndroidX.cardview)
    api(AndroidX.recyclerView)
    api(AndroidX.coreKtx)
    api(AndroidX.activityKtx)
    api(AndroidX.fragmentKtx)
    api(AndroidX.legacySupportV4)
    api(AndroidX.swiperefreshlayout)
    api(Lifecycle.liveDataKtx)
    api(Lifecycle.viewModelKtx)
    api(NavigationLib.fragmentKtx)
    api(NavigationLib.uiKtx)
    api(NavigationLib.compose)
    api(Google.material)
    api(Compose.ui)
    api(Compose.material)
    api(Compose.materialIcons)
    api(Compose.material3)
    api(Compose.unit)
    api(Compose.util)
    api(Compose.liveData)
    api(Compose.saveable)
    api(Compose.viewbinding)
    api(Compose.googlefonts)
    api(Compose.activity)
    api(Compose.preview)
    api(Compose.lifecycleViewModel)
    api(Compose.uiTooling)
    api(Compose.accompanistPager)
    api(Compose.accompanistPagerIndicators)
    api(Compose. accompanistSystemUiController)
    api(Lifecycle.runtimeCompose)
    api(Coil.coilCompose)
    api(Coil.coilGif)
    api(Coil.coilSvg)
}