package com.nxg.commonui.component.image

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.*
import coil.request.ImageRequest
import com.nxg.commonui.component.R
import com.nxg.commonui.component.main.PreviewViewModel
import com.nxg.commonui.theme.*
import kotlinx.coroutines.InternalCoroutinesApi

/**
 * 推片Compose组件
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ImageCompose(
    navController: NavController,
    viewModel: PreviewViewModel
) {
    val list by viewModel.imageComponentListStateFlow.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.nui_component_base_image)) },
                navigationIcon = {
                    // RowScope here, so these icons will be placed horizontally
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
        ImageComponentGrid(list)
    }

}


@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ImageComponentGrid(list: List<ImageComponent>) {
    Log.d("CommonUI", "IconComponentGrid: list -> $list")
    val clickPosition = remember { mutableStateOf(-1) }
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(
                ColorBackground.Primary
            )
            .padding(10.dp, 10.dp, 10.dp, 10.dp)
            .wrapContentHeight()
        //.height(80.dp)//LazyColumn嵌套LazyVerticalGrid，LazyVerticalGrid必须给一个固定的高度，否则 java.lang.IllegalStateException: Vertically scrollable component was measured with an infinity maximum height constraints, which is disallowed. One of the common reasons is nesting layouts like LazyColumn and Column(Modifier.verticalScroll()). If you want to add a header before the list of items please add a header as a separate item() before the main items() inside the LazyColumn scope. There are could be other reasons for this to happen: your ComposeView was added into a LinearLayout with some weight, you applied Modifier.wrapContentSize(unbounded = true) or wrote a custom layout. Please try to remove the source of infinite constraints in the hierarchy above the scrolling container.

    ) {
        items(list) { imageComponent ->
            ImageComponentGridItem(
                imageComponent,
                list.indexOf(imageComponent),
                onClick = { clickPosition.value = list.indexOf(imageComponent) })
        }
    }
    /*if (clickPosition.value > -1) {
        AlertDialog(
            backgroundColor = ColorBackground.Primary,
            onDismissRequest = {
                clickPosition.value < 0
            },
            title = {
                Text(if (clickPosition.value > -1) list[clickPosition.value].dialogTitle else "")
            },
            text = {
                Text(if (clickPosition.value > -1) list[clickPosition.value].dialogContent else "")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        clickPosition.value = -1
                    }
                ) {
                    Text(stringResource(id = R.string.nui_component_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        clickPosition.value = -1
                    }
                ) {
                    Text(stringResource(id = R.string.nui_component_close))
                }
            }
        )
    }*/
}


@Composable
fun ImageComponentGridItem(imageComponent: ImageComponent, index: Int, onClick: () -> Unit = {}) {
    Log.d("CommonUI", "ImageComponentGridItem: imageComponent -> $imageComponent")
    Card(
        modifier = Modifier
            .clickable(enabled = true, onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp, 10.dp, 10.dp, 10.dp),
                style = MaterialTheme.typography.subtitle1,
                color = ColorText.Primary,
                fontSize = 14.sp,
                text = stringResource(id = imageComponent.imageNameResId)
            )

            when (index) {
                0 -> Image(
                    painter = painterResource(imageComponent.imageResId),
                    contentDescription = stringResource(id = imageComponent.imageNameResId),
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 10.dp, 10.dp)
                        .align(Alignment.CenterHorizontally)
                        // Set image size to 40 dp
                        .size(100.dp)

                )
                1 -> Image(
                    painter = painterResource(imageComponent.imageResId),
                    contentDescription = stringResource(id = imageComponent.imageNameResId),
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 10.dp, 10.dp)
                        .align(Alignment.CenterHorizontally)
                        // Set image size to 100 dp
                        .size(100.dp)
                        // Clip image to be shaped as a circle
                        .clip(CircleShape)
                        .border(//多个border，前面，调用的后生效，后面调用的先生效
                            shape = CircleShape,
                            border = BorderStroke(4.dp, SolidColor(Color.Yellow))
                        )
                        .border(
                            shape = CircleShape,
                            border = BorderStroke(
                                width = 8.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Purple200,
                                        Purple500,
                                        Purple700,
                                        Teal200
                                    )
                                )
                            )
                        ),

                    )
                2 -> Image(
                    painter = painterResource(imageComponent.imageResId),
                    contentDescription = stringResource(id = imageComponent.imageNameResId),
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 10.dp, 10.dp)
                        .align(Alignment.CenterHorizontally)
                        // Set image size to 100 dp
                        .size(100.dp)
                        // Clip image to be shaped as a circle
                        .clip(RoundedCornerShape(10.dp))

                )
                3 -> Image(
                    painter = painterResource(imageComponent.imageResId),
                    contentDescription = stringResource(id = imageComponent.imageNameResId),
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .padding(
                            10.dp, 0.dp, 10.dp, 10.dp
                        )
                        .align(Alignment.CenterHorizontally)
                        // Set image size to 100 dp
                        .size(100.dp)

                )
                4 -> ImageCustomPainter()
                5 -> AsyncImage()
                6 -> AsyncImageUsePlaceholder()
                7 -> AsyncImageUseLoading()
                8 -> AsyncImageUseState()
            }
        }
    }
}

@Composable
fun ImageCustomPainter() {
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://picsum.photos/300/300")
            .build()
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.nui_component_base_image),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .padding(
                    10.dp, 0.dp, 10.dp, 10.dp
                )
                .align(Alignment.CenterHorizontally)
                .size(100.dp)

        )
    }


}

@Composable
fun AsyncImage() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        AsyncImage(
            model = "https://picsum.photos/300/300",
            contentDescription = null,
            modifier = Modifier
                .padding(
                    10.dp, 0.dp, 10.dp, 10.dp
                )
                .align(Alignment.CenterHorizontally)
                .size(100.dp)
        )
    }

}

@Composable
fun AsyncImageUsePlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://picsum.photos/300/300")
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.nui_image_harmony_logo),
            contentDescription = stringResource(R.string.nui_component_base_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(
                    10.dp, 0.dp, 10.dp, 10.dp
                )
                .align(Alignment.CenterHorizontally)
                .size(100.dp)
        )
    }

}

@Composable
fun AsyncImageUseLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        SubcomposeAsyncImage(
            model = "https://picsum.photos/300/300",
            loading = {
                CircularProgressIndicator()
            },
            contentDescription = stringResource(R.string.nui_component_base_image),
            modifier = Modifier
                .padding(
                    10.dp, 0.dp, 10.dp, 10.dp
                )
                .align(Alignment.CenterHorizontally)
                .size(100.dp)
        )
    }
}

@Composable
fun AsyncImageUseState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        SubcomposeAsyncImage(
            model = "https://picsum.photos/300/300",
            contentDescription = stringResource(R.string.nui_component_base_image),
            modifier = Modifier
                .padding(
                    10.dp, 0.dp, 10.dp, 10.dp
                )
                .align(Alignment.CenterHorizontally)
                .size(100.dp)
        ) {
            val state = painter.state
            if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                CircularProgressIndicator()
            } else {
                SubcomposeAsyncImageContent()
            }
        }
    }

}

@InternalCoroutinesApi
@Preview()
@Composable
fun PreviewImageComponentGridItem() {
    ImageComponentGridItem(
        ImageComponent(
            R.string.nui_component_base_image_base,
            R.string.nui_component_base_image_base,
            R.drawable.nui_image_harmony_logo
        ), 0
    )
}



