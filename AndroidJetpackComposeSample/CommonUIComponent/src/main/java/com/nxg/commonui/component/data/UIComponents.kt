package com.nxg.commonui.component.data

import com.nxg.commonui.component.R

data class UIComponent(
    val componentIconResId: Int,
    val componentGroupNameResId: Int,
    val componentNameResId: Int,
    val route: String
)

object UIComponents {

    val uiComponents = mutableListOf<UIComponent>()

    init {
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_color, R.string.nui_component_base, R.string.nui_component_base_color,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_icon, R.string.nui_component_base, R.string.nui_component_base_icon,
                RouteHub.ICON
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_image, R.string.nui_component_base, R.string.nui_component_base_image,
                RouteHub.IMAGE
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_button, R.string.nui_component_base, R.string.nui_component_base_button,
                RouteHub.BUTTON
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_text, R.string.nui_component_base, R.string.nui_component_base_text,
                RouteHub.TEXT
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_layout, R.string.nui_component_base, R.string.nui_component_base_layout,
                RouteHub.LAYOUT
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_cell, R.string.nui_component_base, R.string.nui_component_base_cell,
                RouteHub.CELL
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_badge, R.string.nui_component_base, R.string.nui_component_base_badge,
                RouteHub.BADGE
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_tag, R.string.nui_component_base, R.string.nui_component_base_tag,
                RouteHub.TAG
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_loading, R.string.nui_component_base, R.string.nui_component_base_loading,
                RouteHub.LOADING
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_loading_page, R.string.nui_component_base, R.string.nui_component_base_loading_page,
                RouteHub.LOADING
            )
        )

        uiComponents.add(
            UIComponent(R.drawable.nui_ic_form, R.string.nui_component_form, R.string.nui_component_form_form,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_calendar, R.string.nui_component_form, R.string.nui_component_form_calendar,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_keyboard, R.string.nui_component_form, R.string.nui_component_form_keyboard,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_picker, R.string.nui_component_form, R.string.nui_component_form_picker,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_datetime_picker, R.string.nui_component_form, R.string.nui_component_form_datetime_picker,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_rate, R.string.nui_component_form, R.string.nui_component_form_rate,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_search, R.string.nui_component_form, R.string.nui_component_form_search,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_number_box, R.string.nui_component_form, R.string.nui_component_form_number_box,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_upload, R.string.nui_component_form, R.string.nui_component_form_upload,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_code, R.string.nui_component_form, R.string.nui_component_form_code,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_field, R.string.nui_component_form, R.string.nui_component_form_input,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_textarea, R.string.nui_component_form, R.string.nui_component_form_textarea,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_checkbox, R.string.nui_component_form, R.string.nui_component_form_checkbox,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_radio, R.string.nui_component_form, R.string.nui_component_form_radio,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_switch, R.string.nui_component_form, R.string.nui_component_form_switch,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_slider, R.string.nui_component_form, R.string.nui_component_form_slider,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_album, R.string.nui_component_form, R.string.nui_component_form_album,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_list, R.string.nui_component_data, R.string.nui_component_data_list,
                RouteHub.COLOR
            )
        )

        uiComponents.add(
            UIComponent(R.drawable.nui_ic_progress, R.string.nui_component_data, R.string.nui_component_data_progress,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_count_down, R.string.nui_component_data, R.string.nui_component_data_countdown,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_count_to, R.string.nui_component_data, R.string.nui_component_data_count_to,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_tooltip, R.string.nui_component_feedback, R.string.nui_component_feedback_tool_tips,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_action_sheet, R.string.nui_component_feedback, R.string.nui_component_feedback_action_sheet,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_alert, R.string.nui_component_feedback, R.string.nui_component_feedback_alert,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_toast, R.string.nui_component_feedback, R.string.nui_component_feedback_toast,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_notice_bar, R.string.nui_component_feedback, R.string.nui_component_feedback_notice_bar,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_notify, R.string.nui_component_feedback, R.string.nui_component_feedback_notify,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_swipe_action, R.string.nui_component_feedback, R.string.nui_component_feedback_swipe_action,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_collapse, R.string.nui_component_feedback, R.string.nui_component_feedback_collapse,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_popup, R.string.nui_component_feedback, R.string.nui_component_feedback_popup,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_modal, R.string.nui_component_feedback, R.string.nui_component_feedback_modal,
                RouteHub.COLOR
            )
        )

        uiComponents.add(
            UIComponent(R.drawable.nui_ic_scroll_list, R.string.nui_component_layout, R.string.nui_component_layout_scroll_list,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_line, R.string.nui_component_layout, R.string.nui_component_layout_line,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_mask, R.string.nui_component_layout, R.string.nui_component_layout_overlay,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_no_network, R.string.nui_component_layout, R.string.nui_component_layout_no_network,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_grid, R.string.nui_component_layout, R.string.nui_component_layout_grid,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_swiper, R.string.nui_component_layout, R.string.nui_component_layout_swiper,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_skeleton, R.string.nui_component_layout, R.string.nui_component_layout_skeleton,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_sticky, R.string.nui_component_layout, R.string.nui_component_layout_sticky,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_divider, R.string.nui_component_layout, R.string.nui_component_layout_divider,
                RouteHub.COLOR
            )
        )

        uiComponents.add(
            UIComponent(R.drawable.nui_ic_tabbar, R.string.nui_component_navigation, R.string.nui_component_navigation_tab_bar,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_back_top, R.string.nui_component_navigation, R.string.nui_component_navigation_back_top,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_navbar, R.string.nui_component_navigation, R.string.nui_component_navigation_nav_bar,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_tabs, R.string.nui_component_navigation, R.string.nui_component_navigation_tab,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_subsection, R.string.nui_component_navigation, R.string.nui_component_navigation_sub_section,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_index_list, R.string.nui_component_navigation, R.string.nui_component_navigation_index_list,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_steps, R.string.nui_component_navigation, R.string.nui_component_navigation_steps,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_empty, R.string.nui_component_navigation, R.string.nui_component_navigation_empty,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_parse, R.string.nui_component_other, R.string.nui_component_other_parse,
                RouteHub.COLOR
            )
        )

        uiComponents.add(
            UIComponent(R.drawable.nui_ic_code, R.string.nui_component_other, R.string.nui_component_other_code_input,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_loadmore, R.string.nui_component_other, R.string.nui_component_other_load_more,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_read_more, R.string.nui_component_other, R.string.nui_component_other_read_more,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_gap, R.string.nui_component_other, R.string.nui_component_other_gap,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_avatar, R.string.nui_component_other, R.string.nui_component_other_avatar,
                RouteHub.COLOR
            )
        )
        uiComponents.add(
            UIComponent(R.drawable.nui_ic_link, R.string.nui_component_other, R.string.nui_component_other_link,
                RouteHub.COLOR
            )
        )
        uiComponents.add(UIComponent(R.drawable.nui_ic_transition, R.string.nui_component_other, R.string.nui_component_other_transition, RouteHub.COLOR))
    }
}