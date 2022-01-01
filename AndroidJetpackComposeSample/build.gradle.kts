// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("de.fayard.buildSrcVersions") version "0.6.1"
}
// Don't put any code before the buildscript {} and plugins {} block
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath(Gradle.plugin)
        classpath(Kotlin.plugin)
        classpath(NavigationLib.safeArgsGradlePlugin)
        //classpath(Hilt.gradlePlugin)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}