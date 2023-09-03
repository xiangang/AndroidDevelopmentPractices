package com.nxg.commonui.component.cell

import android.annotation.SuppressLint
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.nxg.commonui.component.R
import com.nxg.commonui.component.layout.LayoutComponentList
import com.nxg.commonui.component.main.PreviewViewModel

/**
 * LayoutCompose组件
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CellCompose(
    navController: NavController,
    viewModel: PreviewViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nui_component_base_cell)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.nui_component_back)
                        )
                    }
                },
                actions = {
                    // RowScope here, so these icons will be placed horizontally
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(
                            Icons.Filled.Share,
                            contentDescription = stringResource(id = R.string.nui_component_desc)
                        )
                    }
                }
            )
        }
    ) {
        // Screen content
        LayoutComponentList()
    }
}