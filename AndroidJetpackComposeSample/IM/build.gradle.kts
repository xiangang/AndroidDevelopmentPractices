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
    implementation(Google.material)
    testImplementation(TestLib.junit)
    testImplementation(TestLib.androidJunit)
    testImplementation(TestLib.espresso)
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