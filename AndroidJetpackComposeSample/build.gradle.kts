// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(NavigationLib.safeArgsGradlePlugin)
    }
}
println("Gradle.plugin ${Gradle.plugin}")
plugins {
    id("com.android.application") version ("7.2.1") apply (false)
    id("com.android.library") version ("7.2.1") apply (false)
    id("org.jetbrains.kotlin.android") version ("1.7.10") apply (false)
    id("org.jetbrains.kotlin.kapt") version ("1.7.10") apply (false)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.10" apply false
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
