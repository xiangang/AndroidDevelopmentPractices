package com.nxg.commonui.theme

import androidx.compose.ui.graphics.Color

object ColorHex {
    const val Primary = 0xff3c9cff
    const val PrimaryDark = 0xff398ade
    const val PrimaryDisabled = 0xff9acafc
    const val PrimaryLight = 0xffecf5ff

    const val Success = 0xff5ac725
    const val SuccessDark = 0xff53c21d
    const val SuccessDisabled = 0xffa9e08f
    const val SuccessLight = 0xfff5fff0

    const val Warn = 0xfff9ae3d
    const val WarnDark = 0xfff1a532
    const val WarnDisabled = 0xfff9d39b
    const val WarnLight = 0xfffdf6ec

    const val Error = 0xfff56c6c
    const val ErrorDark = 0xffe45656
    const val ErrorDisabled = 0xfff7b2b2
    const val ErrorLight = 0xfffef0f0

    const val Info = 0xff909399
    const val InfoDark = 0xff767a82
    const val InfoDisabled = 0xffc4c6c9
    const val InfoLight = 0xfff4f4f5
}

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

//主色调
object ColorPrimary {
    val Primary = Color(ColorHex.Primary)
    val Dark = Color(ColorHex.PrimaryDark)
    val Disabled = Color(ColorHex.PrimaryDisabled)
    val Light = Color(ColorHex.PrimaryLight)
}

object ColorSuccess {
    val Primary = Color(ColorHex.Success)
    val Dark = Color(ColorHex.SuccessDark)
    val Disabled = Color(ColorHex.SuccessDisabled)
    val Light = Color(ColorHex.SuccessLight)
}

object ColorWarn {
    val Primary = Color(ColorHex.Warn)
    val Dark = Color(ColorHex.WarnDark)
    val Disabled = Color(ColorHex.WarnDisabled)
    val Light = Color(ColorHex.WarnLight)
}

object ColorError {
    val Primary = Color(ColorHex.Error)
    val Dark = Color(ColorHex.ErrorDark)
    val Disabled = Color(ColorHex.ErrorDisabled)
    val Light = Color(ColorHex.ErrorLight)
}

object ColorInfo {
    val Primary = Color(ColorHex.Info)
    val Dark = Color(ColorHex.InfoDark)
    val Disabled = Color(ColorHex.InfoDisabled)
    val Light = Color(ColorHex.InfoLight)
}

object ColorText {
    val Primary = Color(0xff303133)
    val Normal = Color(0xff606266)
    val Secondary = Color(0xff909399)
    val Placeholder = Color(0xffc0c4cc)
}

object ColorBorder {
    val LevelOne = Color(0xff9a9998)
    val LevelTwo = Color(0xffb4b3b1)
    val LevelThree = Color(0xffceccca)
    val LevelFour = Color(0xffe7e6e4)
}

object ColorBackground {
    val Primary = Color(0xfff3f4f6)
    val E5E9F2 = Color(0xffe5e9f2)
    val CED7E1 = Color(0xffced7e1)
    val CCFBFF = Color(0xFFCCFBFF)
    val EF96C5 = Color(0xffef96cf)
    val EAD6EE = Color(0xffead6ee)
    val A0F1EA = Color(0xffa0f1ea)
    val EEBD89 = Color(0xffeebd89)
    val D13ABD = Color(0xffd13abd)
}

object DarkColor {
    val primary: Color = ColorPrimary.Dark
    val primaryVariant: Color = ColorPrimary.Dark
    val secondary: Color = ColorPrimary.Primary
    val secondaryVariant: Color = secondary
    val background: Color = Color(0xFF121212)
    val surface: Color = Color(0xFF121212)
    val error: Color = ColorError.Dark
}




