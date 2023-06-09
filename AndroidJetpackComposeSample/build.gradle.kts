// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(NavigationLib.safeArgsGradlePlugin)
    }
}
plugins {
    id("com.android.application") version ("7.2.1") apply (false)
    id("com.android.library") version ("7.2.1") apply (false)
    id("org.jetbrains.kotlin.android") version (Kotlin.kotlin_version) apply (false)
    id("org.jetbrains.kotlin.kapt") version (Kotlin.kotlin_version) apply (false)
    id("org.jetbrains.kotlin.plugin.serialization") version Kotlin.kotlin_version apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

configurations.all {
    resolutionStrategy {
        force(Lifecycle.viewModelKtx)
    }
}
