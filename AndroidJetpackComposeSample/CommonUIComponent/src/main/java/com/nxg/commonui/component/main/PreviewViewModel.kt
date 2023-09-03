package com.nxg.commonui.component.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxg.commonui.component.color.ColorComponent
import com.nxg.commonui.component.color.ColorComponents
import com.nxg.commonui.component.data.UIComponent
import com.nxg.commonui.component.data.UIComponents
import com.nxg.commonui.component.icon.IconComponent
import com.nxg.commonui.component.icon.IconComponents
import com.nxg.commonui.component.image.ImageComponent
import com.nxg.commonui.component.image.ImageComponents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreviewViewModel : ViewModel() {

    private val _uiComponentListStateFlow = MutableStateFlow(mutableListOf<UIComponent>())
    val uiComponentListStateFlow = _uiComponentListStateFlow.asStateFlow()

    private fun onUIComponentListStateFlow(list: MutableList<UIComponent>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _uiComponentListStateFlow.emit(list)
            }
        }
    }

    private val _colorComponentListStateFlow =
        MutableStateFlow(mutableListOf<ColorComponent>())
    val colorComponentListStateFlow = _colorComponentListStateFlow.asStateFlow()

    private fun onColorComponentListStateFlow(list: MutableList<ColorComponent>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _colorComponentListStateFlow.emit(list)
            }
        }
    }

    private val _iconComponentListStateFlow = MutableStateFlow(mutableListOf<IconComponent>())
    val iconComponentListStateFlow = _iconComponentListStateFlow.asStateFlow()

    private fun onIconComponentListStateFlow(list: MutableList<IconComponent>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _iconComponentListStateFlow.emit(list)
            }
        }
    }


    private val _imageComponentListStateFlow = MutableStateFlow(mutableListOf<ImageComponent>())
    val imageComponentListStateFlow = _imageComponentListStateFlow.asStateFlow()

    private fun onImageComponentListStateFlow(list: MutableList<ImageComponent>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                _imageComponentListStateFlow.emit(list)
            }
        }
    }


    init {
        onUIComponentListStateFlow(UIComponents.uiComponents)
        onColorComponentListStateFlow(ColorComponents.colorComponents)
        onIconComponentListStateFlow(IconComponents.iconComponents)
        onImageComponentListStateFlow(ImageComponents.imageComponents)
    }
}