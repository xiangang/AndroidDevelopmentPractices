pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
        maven { setUrl("https://jitpack.io") }
    }
}
rootProject.name = "AndroidJetpackComposeSample"
include(":app")
include(":FastMvvm")
include(":CommonLib")
include(":CommonUI")
include(":CommonUtils")
include(":FFmpegMobile")
include(":RtmpMobile")
include(":YuvUtil")
include(":OpenCVMobile")
include(":AudioRecordUtils")
include(":SocketIOMobile")
include(":IMCore")
include(":IMConversation")
include(":IMContact")
include(":IMChat")
include(":IMConversation")
include(":FFmpegStudyComponent")
include(":SocketIOStudyComponent")
include(":UserComponent")
include(":SettingComponent")
include(":NoticeComponent")
include(":CommonUIComponent")
include(":WebrtcMobile")
