import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.protobuf") version "0.8.17"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }

    // Generates the java Protobuf-lite code for the Protobufs in this project. See
    // https://github.com/google/protobuf-gradle-plugin#customizing-protobuf-compilation
    // for more information.
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
            }
        }
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
    implementation(Datastore.datastore)
    implementation(Datastore.protobuf_javalite)
    testImplementation(TestLib.junit)
    testImplementation(TestLib.androidJunit)
    testImplementation(TestLib.espresso)
    implementation(project(mapOf("path" to ":FastMvvm")))
    implementation(project(mapOf("path" to ":KtStateMachine")))
    implementation("androidx.security:security-crypto:1.0.0")
    // For Identity Credential APIs
    implementation("androidx.security:security-identity-credential:1.0.0-alpha03") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")
    }
    // For App Authentication APIs
    implementation("androidx.security:security-app-authenticator:1.0.0-alpha02")
    // For App Authentication API testing
    androidTestImplementation("androidx.security:security-app-authenticator:1.0.0-alpha02")
    api(Kotlin.reflect)
    implementation(Coil.coilVideo)
    implementation("io.github.TheMelody:gd_compose:1.0.2")
    implementation("com.amazonaws:aws-android-sdk-s3:2.73.0")

}

