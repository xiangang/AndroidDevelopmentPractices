plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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
    implementation(AndroidX.appcompat)
    implementation(AndroidX.constraintlayout)
    implementation(AndroidX.cardview)
    implementation(AndroidX.recyclerView)
    implementation(AndroidX.coreKtx)
    implementation(AndroidX.activityKtx)
    implementation(AndroidX.fragmentKtx)
    implementation(AndroidX.swiperefreshlayout)
    implementation(AndroidX.legacySupportV4)
    implementation(Lifecycle.liveDataKtx)
    implementation(Lifecycle.viewModelKtx)
    implementation(NavigationLib.fragmentKtx)
    implementation(NavigationLib.uiKtx)
    //implementation(NavigationLib.safeArgs)
    implementation(Google.material)
    implementation(Compose.ui)
    implementation(Compose.material)
    implementation(Compose.preview)
    implementation(Compose.activity)
    implementation(Compose.lifecycleViewModel)
    debugImplementation(Compose.uiTooling)
    androidTestImplementation(Compose.test)
    testImplementation(TestLib.junit)
    testImplementation(TestLib.androidJunit)
    testImplementation(TestLib.espresso)
    implementation(project(mapOf("path" to ":FFmpegMobile")))
    implementation(project(mapOf("path" to ":AcodecMobile")))
    implementation(project(mapOf("path" to ":AudioRecordUtils")))
}