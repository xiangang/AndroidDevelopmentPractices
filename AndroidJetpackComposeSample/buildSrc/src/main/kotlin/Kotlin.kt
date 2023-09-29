object Kotlin {
    var kotlin_version = "1.8.10"

    //Kotlin 1.4 以后，您不再需要在 gradle 上声明 stdlib
    var stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    val test = "org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version"
    val gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    val reflect = "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"

    //协程
    object Coroutines {
        private const val version = "1.6.4"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        //https://github.com/Kotlin/kotlinx.serialization
        const val json = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
    }

}
