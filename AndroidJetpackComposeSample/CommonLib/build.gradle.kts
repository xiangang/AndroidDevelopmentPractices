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
    api(NavigationLib.fragmentKtx)
    api(NavigationLib.uiKtx)
    api(NavigationLib.dynamic)

    //JetPack lifecycle
    api(Lifecycle.liveDataKtx)
    api(Lifecycle.viewModelKtx)
    api(Lifecycle.viewModelSavedState)
    api(Lifecycle.commonJava8)
    api(Lifecycle.service)
    api(Lifecycle.runtimeKtx)

    //JetPack Room
    api(Room.runtime)
    kapt(Room.compiler)
    api(Room.ktx)
    api(Room.rxjava3)
    api(Room.guava)

    //viewPager
    api(ViewPager.viewpager2)
    api(ViewPager.viewpager)

    //okhttp
    api(OkHttp.okhttp)
    api(OkHttp.urlConnection)
    api(OkHttp.loggingInterceptor)

    //retrofit
    api(Retrofit.retrofit)
    api(Retrofit.convertGson)

    //glide
    api(Glide.glide)
    api(Glide.compiler)

    //coil
    api(Coil.coil)

    //ThirdParty
    api(ThirdParty.brvah)
    api(ThirdParty.javapoet)
    api(ThirdParty.utilCodex)

    // Dagger & Hilt
    api(Hilt.android)
    kapt (Hilt.android_compiler)
    kapt (Hilt.compiler)

}