plugins {
    kotlin("kapt")
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.dagger.hilt.android")
}


android {
    compileSdk = BuildConfig.compileSdk
    //compileSdkPreview = BuildConfig.compileSdkPreview
    buildToolsVersion = BuildConfig.buildToolsVersion

    defaultConfig {
        applicationId = BuildConfig.applicationId
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion
        versionCode = BuildConfig.versionCode
        versionName = BuildConfig.versionName
        testInstrumentationRunner = BuildConfig.testInstrumentationRunner
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            abiFilters.addAll(arrayListOf("armeabi-v7a", "arm64-v8a"))
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
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
    packagingOptions {
        resources.excludes += "META-INF/gradle/incremental.annotation.processors"
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
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
    implementation(Compose.material)
    implementation(Compose.activity)
    implementation(Compose.preview)
    implementation(Compose.lifecycleViewModel)
    implementation(Compose.uiTooling)
    implementation(ThirdParty.linkageRecyclerview)
    implementation(Hilt.android)
    implementation(Hilt.navigation_fragment)
    kapt(Hilt.android_compiler)
    implementation(project(mapOf("path" to ":FastMvvm")))
    implementation(project(mapOf("path" to ":FFmpegMobile")))
    implementation(project(mapOf("path" to ":RtmpMobile")))
    implementation(project(mapOf("path" to ":OpenCVMobile")))
    implementation(project(mapOf("path" to ":AudioRecordUtils")))
    implementation(project(mapOf("path" to ":YuvUtil")))
    implementation(project(mapOf("path" to ":WebrtcMobile")))
    implementation(project(mapOf("path" to ":IM")))
    implementation(project(mapOf("path" to ":CommonUIComponent")))
    implementation(project(mapOf("path" to ":FFmpegStudyComponent")))
    implementation(project(mapOf("path" to ":SocketIOStudyComponent")))
    implementation(project(mapOf("path" to ":UserComponent")))
    implementation(project(mapOf("path" to ":SettingComponent")))
    implementation(project(mapOf("path" to ":NoticeComponent")))

}