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

    //glide
    implementation(Glide.glide)
    implementation(Glide.compiler)
    implementation(ThirdParty.javapoet)

    //Hilt
    /*implementation(Hilt.hilt_android)
    kapt(Hilt.hilt_compiler)
    kapt(Hilt.hilt_android_compiler)*/

    /*// Dagger & Hilt
    implementation("com.google.dagger:hilt-android:2.40")
    kapt("com.google.dagger:hilt-android-compiler:2.40")
    implementation("androidx.hilt:hilt-common:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")
    implementation("androidx.hilt:hilt-work:1.0.0")*/




    /*implementation("com.google.dagger:hilt-android:2.40")
    kapt("com.google.dagger:hilt-android-compiler:2.40")
    implementation("androidx.hilt:hilt-common:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0") i
    mplementation("androidx.hilt:hilt-work:1.0.0")*/


}