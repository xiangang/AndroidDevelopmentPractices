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

    //JetPack Room
    implementation(Room.runtime)
    kapt(Room.compiler)
    implementation(Room.ktx)
    implementation(Room.rxjava3)
    implementation(Room.guava)

    //viewPager
    implementation(ViewPager.viewpager2)
    implementation(ViewPager.viewpager)

    //okhttp
    implementation(OkHttp.okhttp)
    implementation(OkHttp.urlConnection)
    implementation(OkHttp.loggingInterceptor)

    //okhttp
    implementation(Retrofit.retrofit)
    implementation(Retrofit.convertGson)

    //glide
    implementation(Glide.glide)
    implementation(Glide.compiler)

    //coil
    implementation(Coil.coil)

    //ThirdParty
    implementation(ThirdParty.brvah)
    implementation(ThirdParty.javapoet)
    implementation(ThirdParty.utilCodex)

    // Dagger & Hilt
    implementation(Hilt.android)
    kapt (Hilt.android_compiler)
    kapt (Hilt.compiler)

}