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
        externalNativeBuild {
            cmake {
                cppFlags("")
            }
        }
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
    externalNativeBuild {
        cmake {
            path("src/main/cpp/CMakeLists.txt")
            version = "3.18.1"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
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
    implementation(Google.material)
    implementation(Lifecycle.liveDataKtx)
    implementation(Lifecycle.viewModelKtx)
    testImplementation(TestLib.junit)
    testImplementation(TestLib.androidJunit)
    testImplementation(TestLib.espresso)
    implementation("org.webrtc:google-webrtc:1.0.28513")
    implementation("org.java-websocket:Java-WebSocket:1.4.0")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("io.socket:socket.io-client:2.0.1") {
        exclude(group = "org.json", module = "json")
    }
}