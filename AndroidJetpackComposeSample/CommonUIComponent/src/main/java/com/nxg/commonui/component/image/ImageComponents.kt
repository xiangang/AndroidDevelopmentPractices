package com.nxg.commonui.component.image

import com.nxg.commonui.component.R


data class ImageComponent(
    val imageGroupNameResId: Int,
    val imageNameResId: Int,
    val imageResId: Int,
    val dialogTitle: String = "",
    val dialogContent: String = ""
)


object ImageComponents {
    val imageComponents = mutableListOf<ImageComponent>()

    init {
        imageComponents.add(
            ImageComponent(
                R.string.nui_component_base_image_base,
                R.string.nui_component_base_image_base,
                R.drawable.nui_image_harmony_logo
            )
        )
        imageComponents.add(
            ImageComponent(
                R.string.nui_component_base_image_base,
                R.string.nui_component_base_image_custom,
                R.drawable.nui_image_harmony_logo
            )
        )
        imageComponents.add(
            ImageComponent(
                R.string.nui_component_base_image_base,
                R.string.nui_component_base_image_custom_circle,
                R.drawable.nui_image_harmony_logo
            )
        )
        imageComponents.add(
            ImageComponent(
                R.string.nui_component_base_image_base,
                R.string.nui_component_base_image_width_fit,
                R.drawable.nui_image_harmony_logo
            )
        )
        imageComponents.add(
            ImageComponent(
                R.string.nui_component_base_image_base,
                R.string.nui_component_base_image_custom_painter,
                R.drawable.nui_image_harmony_logo
            )
        )
        imageComponents.add(
            ImageComponent(
                R.string.nui_component_base_image_base,
                R.string.nui_component_base_image_network,
                R.drawable.nui_image_harmony_logo
            )
        )
        imageComponents.add(
            ImageComponent(
                R.string.nui_component_base_image_base,
                R.string.nui_component_base_image_network_placeholder,
                R.drawable.nui_image_harmony_logo
            )
        )
        imageComponents.add(
            ImageComponent(
                R.string.nui_component_base_image_base,
                R.string.nui_component_base_image_network_loading,
                R.drawable.nui_image_harmony_logo
            )
        )
        imageComponents.add(
            ImageComponent(
                R.string.nui_component_base_image_base,
                R.string.nui_component_base_image_network_state,
                R.drawable.nui_image_harmony_logo
            )
        )
    }
}
