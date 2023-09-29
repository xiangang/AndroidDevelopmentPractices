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
include(":IMCore")
include(":IMCommonUI")
include(":IMConversationComponent")
include(":IMContactComponent")
include(":IMChatComponent")
include(":IMDiscoverComponent")
include(":IMCallComponent")
include(":FFmpegStudyComponent")
include(":SocketIOStudyComponent")
include(":IMUserComponent")
include(":SettingComponent")
include(":CommonUIComponent")
include(":WebrtcMobile")
//include(":LocalAAR:WebRTC")
include(":KtStateMachine")
