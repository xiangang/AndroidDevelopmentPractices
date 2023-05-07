plugins {
    kotlin("kapt")
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
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
    testImplementation(TestLib.junit)
    androidTestImplementation(TestLib.androidJunit)
    androidTestImplementation(TestLib.espresso)
    androidTestImplementation(Compose.uiTooling)
    implementation(project(mapOf("path" to ":FastMvvm")))
    implementation(project(mapOf("path" to ":IM")))
    implementation("androidx.security:security-crypto:1.0.0")
    // For Identity Credential APIs
    implementation("androidx.security:security-identity-credential:1.0.0-alpha03")
    // For App Authentication APIs
    implementation("androidx.security:security-app-authenticator:1.0.0-alpha02")
    // For App Authentication API testing
    androidTestImplementation("androidx.security:security-app-authenticator:1.0.0-alpha01")

}