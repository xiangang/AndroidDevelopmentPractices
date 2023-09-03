package com.nxg.commonui.component.layout

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nxg.commonui.component.R
import com.nxg.commonui.component.main.PreviewViewModel
import com.nxg.commonui.component.text.TextComponentHeader
import com.nxg.commonui.theme.ColorBackground
import com.nxg.commonui.theme.ColorBorder
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * LayoutCompose组件
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LayoutCompose(
    navController: NavController,
    viewModel: PreviewViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nui_component_base_layout)) },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LayoutComponentList() {
    LazyColumn(
        modifier = Modifier
            .background(ColorBackground.Primary)
            .fillMaxHeight()
    ) {
        stickyHeader {
            TextComponentHeader("基础使用")
        }
        item {
            Column {
                Row(
                    modifier = Modifier
                        .padding(10.dp, 10.dp, 10.dp, 10.dp)
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {

                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelOne,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                    }

                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelTwo,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 10.dp, 10.dp)
                        .background(ColorBackground.Primary)
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {

                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelOne,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                    }

                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelTwo,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxHeight()
                            .weight(1f)
                    ) {

                    }


                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelThree,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 10.dp, 10.dp)
                        .background(ColorBackground.Primary)
                        .fillMaxWidth()
                        .height(30.dp),
                    verticalAlignment = Alignment.CenterVertically,

                    ) {

                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelOne,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                    }

                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelTwo,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxHeight()
                            .weight(1f)
                    ) {

                    }


                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelThree,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                    }

                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelFour,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                    }
                }
            }

        }

        stickyHeader {
            TextComponentHeader("分栏间隔")
        }
        item {
            Row(
                modifier = Modifier
                    .padding(10.dp, 10.dp, 10.dp, 10.dp)
                    .fillMaxWidth()
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                val padding = 10.dp
                Row(
                    modifier = Modifier
                        .background(
                            color = ColorBorder.LevelOne,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                }
                Spacer(Modifier.size(padding))
                Row(
                    modifier = Modifier
                        .background(
                            color = ColorBorder.LevelTwo,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .fillMaxHeight()
                        .weight(1f)
                ) {

                }
                Spacer(Modifier.size(padding))
                Row(
                    modifier = Modifier
                        .background(
                            color = ColorBorder.LevelThree,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                }
                Spacer(Modifier.size(padding))
                Row(
                    modifier = Modifier
                        .background(
                            color = ColorBorder.LevelFour,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                }
            }
        }
        stickyHeader {
            TextComponentHeader("混合布局")
        }
        item {
            Row(
                modifier = Modifier
                    .padding(10.dp, 10.dp, 10.dp, 10.dp)
                    .fillMaxWidth()
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                val padding = 10.dp
                Row(
                    modifier = Modifier
                        .background(
                            color = ColorBorder.LevelOne,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                }
                Spacer(Modifier.size(padding))
                Row(
                    modifier = Modifier
                        .background(
                            color = ColorBorder.LevelTwo,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .fillMaxHeight()
                        .weight(2f)
                ) {

                }
                Spacer(Modifier.size(padding))
                Row(
                    modifier = Modifier
                        .background(
                            color = ColorBorder.LevelThree,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .fillMaxHeight()
                        .weight(3f)
                ) {
                }
            }
        }

        stickyHeader {
            TextComponentHeader("分栏偏移")
        }
        item {
            Column {
                Row(
                    modifier = Modifier
                        .padding(10.dp, 10.dp, 10.dp, 10.dp)
                        .fillMaxWidth()
                        .height(30.dp),
                ) {
                    Spacer(Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelOne,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                    }
                    Spacer(Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelTwo,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxHeight()
                            .weight(1f)
                    ) {

                    }
                }

                Row(
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 10.dp, 10.dp)
                        .fillMaxWidth()
                        .height(30.dp),
                ) {
                    Row(

                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelOne,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                    }
                    Spacer(Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .background(
                                color = ColorBorder.LevelTwo,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .fillMaxHeight()
                            .weight(1f)
                    ) {

                    }
                    Spacer(Modifier.weight(1f))

                }
            }

        }

        stickyHeader {
            TextComponentHeader("对齐方式")
        }
        item {
            Column {
                Row(
                    modifier = Modifier
                        .padding(10.dp, 10.dp, 10.dp, 10.dp)
                        .fillMaxWidth()
                        .height(30.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = ColorBorder.LevelOne,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .fillMaxHeight()
                                .fillMaxWidth(0.5f)
                        ) {

                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = ColorBorder.LevelTwo,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .fillMaxHeight()
                                .fillMaxWidth(0.5f)
                        ) {

                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 10.dp, 10.dp)
                        .fillMaxWidth()
                        .height(30.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = ColorBorder.LevelOne,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .fillMaxHeight()
                                .fillMaxWidth(0.5f)
                        ) {

                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = ColorBorder.LevelTwo,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .fillMaxHeight()
                                .fillMaxWidth(0.5f)
                        ) {

                        }
                    }
                }
            }
        }
    }
}


@InternalCoroutinesApi
@Preview
@Composable
fun PreviewColorCompose() {
    LayoutComponentList()
}


