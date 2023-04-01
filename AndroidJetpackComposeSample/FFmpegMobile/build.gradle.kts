plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    compileSdk = BuildConfig.compileSdk

    defaultConfig {
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion
        testInstrumentationRunner = BuildConfig.testInstrumentationRunner
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags("")
                abiFilters("armeabi-v7a")
            }
        }
        ndk{
            abiFilters.clear()
            abiFilters +="armeabi-v7a"
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
    sourceSets {
        getByName("main"){
            jniLibs { srcDir("/src/main/cpp/libs") }
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

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation(Hilt.android)
    implementation(Hilt.lifecycle_viewmodel)
    kapt (Hilt.android_compiler)
    kapt (Hilt.compiler)
}