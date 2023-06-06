plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    compileSdk = BuildConfig.compileSdk
    defaultConfig {
        minSdk = BuildConfig.minSdkVersion
        targetSdk = BuildConfig.targetSdkVersion
        testInstrumentationRunner = BuildConfig.testInstrumentationRunner
        consumerProguardFiles("consumer-rules.pro")
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
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
    implementation(Lifecycle.liveDataKtx)
    implementation(Lifecycle.viewModelKtx)
    implementation(Paging.runtimeKtx)
    implementation(Room.runtime)
    implementation(Room.paging)
    kapt(Room.compiler)
    implementation(Kotlin.Coroutines.core)
    implementation(Kotlin.Coroutines.json)
    implementation(Google.material)
    testImplementation(TestLib.junit)
    testImplementation(TestLib.androidJunit)
    testImplementation(TestLib.espresso)
    implementation(project(mapOf("path" to ":FastMvvm")))
    implementation("androidx.security:security-crypto:1.0.0")
    // For Identity Credential APIs
    implementation("androidx.security:security-identity-credential:1.0.0-alpha03")
    // For App Authentication APIs
    implementation("androidx.security:security-app-authenticator:1.0.0-alpha02")
    // For App Authentication API testing
    androidTestImplementation("androidx.security:security-app-authenticator:1.0.0-alpha02")

    /*// 通讯录功能组件
    implementation("com.netease.yunxin.kit.contact:contactkit-ui:9.0.0")
    // 圈组功能组件
    implementation("com.netease.yunxin.kit.qchat:qchatkit-ui:9.0.0")
    // 会话列表功能组件
    implementation("com.netease.yunxin.kit.conversation:conversationkit-ui:9.0.0")
    // 群组功能组件
    implementation("com.netease.yunxin.kit.team:teamkit-ui:9.0.0")
    // 聊天功能组件
    implementation("com.netease.yunxin.kit.chat:chatkit-ui:9.0.0")
    // 搜索功能组件
    implementation("com.netease.yunxin.kit.search:searchkit-ui:9.0.0")*/
}