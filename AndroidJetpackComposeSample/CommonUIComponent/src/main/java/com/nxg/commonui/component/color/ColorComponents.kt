package com.nxg.commonui.component.color

import androidx.compose.ui.graphics.Color
import com.nxg.commonui.component.R
import com.nxg.commonui.theme.*


data class ColorComponent(
    val colorGroupNameResId: Int,
    val colorNameResId: Int,
    val color: Color,
    )

object ColorComponents {

    val colorComponents = mutableListOf<ColorComponent>()

    init {
        //主色调
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_primary,
                R.string.nui_component_base_color_primary_primary,
                ColorPrimary.Primary
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_primary,
                R.string.nui_component_base_color_primary_dark,
                ColorPrimary.Dark
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_primary,
                R.string.nui_component_base_color_primary_disabled,
                ColorPrimary.Disabled
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_primary,
                R.string.nui_component_base_color_primary_light,
                ColorPrimary.Light
            )
        )

        //error
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_error,
                R.string.nui_component_base_color_error_primary,
                ColorError.Primary
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_error,
                R.string.nui_component_base_color_error_dark,
                ColorError.Dark
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_error,
                R.string.nui_component_base_color_error_disabled,
                ColorError.Disabled
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_error,
                R.string.nui_component_base_color_error_light,
                ColorError.Light
            )
        )

        //Warn
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_warn,
                R.string.nui_component_base_color_warn_primary,
                ColorWarn.Primary
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_warn,
                R.string.nui_component_base_color_warn_dark,
                ColorWarn.Dark
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_warn,
                R.string.nui_component_base_color_warn_disabled,
                ColorWarn.Disabled
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_warn,
                R.string.nui_component_base_color_warn_light,
                ColorWarn.Light
            )
        )


        //Info
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_info,
                R.string.nui_component_base_color_info_primary,
                ColorInfo.Primary
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_info,
                R.string.nui_component_base_color_info_dark,
                ColorInfo.Dark
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_info,
                R.string.nui_component_base_color_info_disabled,
                ColorInfo.Disabled
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_info,
                R.string.nui_component_base_color_info_light,
                ColorInfo.Light
            )
        )


        //Success
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_success,
                R.string.nui_component_base_color_success_primary,
                ColorSuccess.Primary
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_success,
                R.string.nui_component_base_color_success_dark,
                ColorSuccess.Dark
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_success,
                R.string.nui_component_base_color_success_disabled,
                ColorSuccess.Disabled
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_success,
                R.string.nui_component_base_color_success_light,
                ColorSuccess.Light
            )
        )

        //Text
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_text,
                R.string.nui_component_base_color_text_primary,
                ColorText.Primary
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_text,
                R.string.nui_component_base_color_text_dark,
                ColorText.Normal
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_text,
                R.string.nui_component_base_color_text_disabled,
                ColorText.Secondary
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_text,
                R.string.nui_component_base_color_text_light,
                ColorText.Placeholder
            )
        )

        //Border
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_border,
                R.string.nui_component_base_color_border_primary,
                ColorBorder.LevelOne
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_border,
                R.string.nui_component_base_color_border_dark,
                ColorBorder.LevelTwo
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_border,
                R.string.nui_component_base_color_border_disabled,
                ColorBorder.LevelThree
            )
        )
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_border,
                R.string.nui_component_base_color_border_light,
                ColorBorder.LevelFour
            )
        )

        //Background
        colorComponents.add(
            ColorComponent(
                R.string.nui_component_base_color_background,
                R.string.nui_component_base_color_background_primary,
                ColorBackground.Primary
            )
        )
    }
}