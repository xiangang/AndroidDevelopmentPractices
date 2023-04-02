/**
 * 不带 ktx 后缀的为 java 依赖，核心功能在此库
 * 带 ktx 后缀为 kotlin 依赖，提供很多方便的扩展函数, ktx 默认引入不带 ktx 的库
 * 依赖关系可以使用：【 gradlew :app:dependencies --scan --configuration releaseRuntimeClasspath >dependenciesTree.txt 输出app模块依赖树 】
 * 查询包的历史版本：https://androidx.tech/artifacts/appcompat/appcompat/
 * support包和androidx包映射关系查询：https://developer.android.com/jetpack/androidx/migrate/artifact-mappings
 */
object AndroidX {
    /**
     * appcompat中默认引入了很多库(比如activity库、fragment库、core库、annotation库、drawerlayout库、appcompat-resources)
     * 如果想使用其中某个库的更新版本，可以单独引用，比如下面的vectordrawable
     * 提示：对于声明式依赖，同一个库的不同版本，gradle会自动使用最新版本来进行依赖替换、编译
     */
    const val appcompat = "androidx.appcompat:appcompat:1.4.0-alpha01"

    //core包+ktx扩展函数
    const val coreKtx = "androidx.core:core-ktx:1.7.0-alpha01"

    //activity+ktx扩展函数
    const val activityKtx = "androidx.activity:activity-ktx:1.3.1"

    //fragment+ktx扩展函数
    const val fragmentKtx = "androidx.fragment:fragment-ktx:1.4.0-alpha04"

    //约束布局
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.0"

    //卡片控件
    const val cardview = "androidx.cardview:cardview:1.0.0"

    //recyclerView
    const val recyclerView = "androidx.recyclerview:recyclerview:1.2.1"

    //swiperefreshlayout
    const val swiperefreshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01"

    //com.android.support:support-v4的androidx映射版本，关于其他支持库查看{https://developer.android.com/topic/libraries/support-library/packages}
    const val legacySupportV4 = "androidx.legacy:legacy-support-v4:1.0.0"

    /**
    multidex分包
    @description:提供了MultiDexApplication，它允许您在Android 4.4和更早的设备上使用不安全的multidex形式。
    21或更高版本默认启用multidex并且您不需要导入multidex库和设置配置文件。
     */
    const val multidex = "androidx.multidex:multidex:2.0.1"

    //sdk包下graphics.drawable下有一个VectorDrawable类，对于较高的版本不需要引入此库来支持基于XML矢量图形创建可绘制对象。
    const val vectordrawable = "androidx.vectordrawable:vectordrawable:1.1.0"

    //在kotlin中要添加Futures依赖项，参考：https://developer.android.com/jetpack/androidx/releases/concurrent
    const val concurrentFuturesKtx = "androidx.concurrent:concurrent-futures-ktx:1.1.0"
}

object Compose {
    const val version = "1.3.0"
    const val ui = "androidx.compose.ui:ui:$version"
    const val material = "androidx.compose.material:material:$version"
    const val materialIcons = "androidx.compose.material:material-icons-extended:$version"
    const val preview = "androidx.compose.ui:ui-tooling-preview:$version"
    const val material3 = "androidx.compose.material3:material3:1.0.0-alpha02"
    const val animation = "androidx.compose.animation:animation:$version"
    const val activity = "androidx.activity:activity-compose:1.4.0"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout-compose:1.0.0-beta0"
    const val test = "androidx.compose.ui:ui-test-junit4:$version"

    //use debugImplementation
    const val uiTooling = "androidx.compose.ui:ui-tooling:$version"
    const val splashscreen = "androidx.core:core-splashscreen:1.0.0-alpha01"
    //更多compose工具集查看 accompanist项目#https://github.com/google/accompanist
    private const val accompanistVersion = "0.24.13-rc"
    const val accompanistPager = "com.google.accompanist:accompanist-pager:$accompanistVersion"
    const val accompanistPagerIndicators = "com.google.accompanist:accompanist-pager-indicators:$accompanistVersion"
    const val accompanistSystemUiController = "com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion"
}

object Hilt {
    private const val version = "2.44"
    private const val hiltCompilerVersion = "1.0.0-beta01"
    const val common = "androidx.hilt:hilt-common:1.0.0"
    const val android = "com.google.dagger:hilt-android:$version"
    const val android_compiler = "com.google.dagger:hilt-android-compiler:$version"
    const val compiler = "androidx.hilt:hilt-compiler:$hiltCompilerVersion"
    const val navigation_fragment = "androidx.hilt:hilt-navigation-fragment:1.0.0"
    const val work = "androidx.hilt:hilt-work:1.0.0"
}


object ViewPager {
    //viewpager
    const val viewpager = "androidx.viewpager:viewpager:1.0.0"

    //viewpager2
    const val viewpager2 = "androidx.viewpager2:viewpager2:1.1.0-beta01"
}

object Paging {
    private const val version = "3.1.1"
    const val runtime = "androidx.paging:paging-runtime:$version"
    const val runtimeKtx = "androidx.paging:paging-runtime-ktx:$version"

    // optional - RxJava2 support
    const val rxjava2 = "androidx.paging:paging-rxjava2:$version"
    const val rxjava2Ktx = "androidx.paging:paging-rxjava2-ktx:$version"

    // optional - RxJava3 support
    const val rxjava3 = "androidx.paging:paging-rxjava3:$version"

    // optional - Guava ListenableFuture support
    const val guava = "androidx.paging:paging-guava:$version"

    // alternatively - without Android dependencies for tests
    const val testingCommon = "androidx.paging:paging-common:$version"
    const val testingCommonKtx = "androidx.paging:paging-common-ktx:$version"

    // optional - Jetpack Compose integration
    const val compose = "androidx.paging:paging-compose:1.0.0-alpha14"
}

object Lifecycle {
    private const val version = "2.4.0-alpha03"

    @Deprecated("lifecycle-extensions 已弃用，截至到目前最后一个版本2.2.0，ViewModelProviders.of()被废弃了，使用ViewModelProvider(ViewModelStoreOwner)")
    const val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"

    const val livedata = "androidx.lifecycle:lifecycle-livedata:$version"
    const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"

    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel:$version"
    const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"

    //Saved state module for ViewModel
    const val viewModelSavedState =
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:$version"

    //Annotation processor 注释处理器
    //use kapt,not implementation
    const val compiler = "androidx.lifecycle:lifecycle-compiler:$version"

    // if using Java8, use the following instead of lifecycle-compiler
    //提供了DefaultLifecycleObserver接口
    const val commonJava8 = "androidx.lifecycle:lifecycle-common-java8:$version"

    //helpers for implementing LifecycleOwner in a Service
    const val service = "androidx.lifecycle:lifecycle-service:$version"

    //ProcessLifecycleOwner provides a lifecycle for the whole application process
    const val process = "androidx.lifecycle:lifecycle-process:$version"

    const val runtime = "androidx.lifecycle:lifecycle-runtime:$version"
    const val runtimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
}


object NavigationLib {
    //这个版本支持多返回栈了
    private const val version = "2.4.2"

    //const val fragment = "androidx.navigation:navigation-fragment:$version"
    const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$version"

    //const val ui = "androidx.navigation:navigation-ui:$version"
    const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"

    const val safeArgs =
        "androidx.navigation:navigation-safe-args-generator:$version"

    //classpath("${AndroidX.Navigation.safeArgsGradlePlugin}")
    const val safeArgsGradlePlugin =
        "androidx.navigation:navigation-safe-args-gradle-plugin:$version"

    // Dynamic Feature Module Support
    const val dynamic =
        "androidx.navigation:navigation-dynamic-features-fragment:$version"
    const val dynamicRuntime =
        "androidx.navigation:navigation-dynamic-features-runtime:$version"

    // Testing Navigation
    const val testing = "androidx.navigation:navigation-testing:$version"

    //Jetpack Compose Integration
    const val compose = "androidx.navigation:navigation-compose:$version"
}


object Room {
    private const val version = "2.4.0-alpha01"

    const val runtime = "androidx.room:room-runtime:$version"

    // for java use annotationProcessor , for kotlin use kapt
    const val compiler = "androidx.room:room-compiler:$version"

    // optional - Kotlin Extensions and Coroutines support for Room
    const val ktx = "androidx.room:room-ktx:$version"

    // optional - RxJava support for Room
    const val rxjava2 = "androidx.room:room-rxjava2:$version"
    const val rxjava3 = "androidx.room:room-rxjava3:$version"

    // optional - Guava support for Room, including Optional and ListenableFuture
    const val guava = "androidx.room:room-guava:$version"

    //Testing Room
    const val testing = "androidx.room:room-testing:$version"
}

object Camera {
    private const val version = "1.0.1"

    const val camera2 = "androidx.camera:camera-camera2:$version"

    const val core = "androidx.camera:camera-core:$version"

    const val lifecycle = "androidx.camera:camera-lifecycle:$version"

    const val view = "androidx.camera:camera-view:1.0.0-alpha27"
}
