// Top-level build file where you can add configuration options common to all sub-projects/modules.
/*plugins {
    id("de.fayard.buildSrcVersions") version "0.6.1"
}*/
// Don't put any code before the buildscript {} and plugins {} block
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        /*maven(url = "https://oss.sonatype.org/content/repositories/snapshots") {
            content {
                includeModule("com.google.dagger", "hilt-android-gradle-plugin")
            }
        }*/
    }
    dependencies {
        classpath(Gradle.plugin)
        classpath(Kotlin.plugin)
        classpath(NavigationLib.safeArgsGradlePlugin)
        classpath(Hilt.android_gradle_plugin)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }

}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java) {
    kotlinOptions {
        // Treat all Kotlin warnings as errors (disabled by default)
        allWarningsAsErrors =
            if (project.hasProperty("warningsAsErrors")) project.properties["warningsAsErrors"] as Boolean else false
        val freeCompilerArgsNew = mutableListOf<String>()
        freeCompilerArgsNew.add("-opt-in=kotlin.RequiresOptIn")
        freeCompilerArgsNew.add("-opt-in=kotlin.Experimental")
        freeCompilerArgsNew.add("-opt-in=kotlin.RequiresOptIn")
        freeCompilerArgsNew.add("-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        freeCompilerArgsNew.add("-opt-in=kotlinx.coroutines.FlowPreview")
        // Enable experimental coroutines APIs, including Flow
        freeCompilerArgs = freeCompilerArgsNew
    }
}
configurations {
    all { exclude(group = "xmlpull", module = "xmlpull") }
}