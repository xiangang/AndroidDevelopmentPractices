package com.nxg.im.contact

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDirections
import coil.compose.AsyncImage
import com.nxg.commonui.theme.ColorBackground
import com.nxg.commonui.theme.ColorText
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.mvvm.ktx.findMainActivityNavController
import com.nxg.mvvm.ui.BaseViewModelFragment
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class)
class ContactDetailFragment : BaseViewModelFragment() {

    private val contactViewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory()
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JetchatTheme {
                    Scaffold(
                        topBar = {
                            val uiState by contactViewModel.uiState.collectAsState()
                            CenterAlignedTopAppBar(
                                title = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        uiState.contactDetail?.friend?.nickname?.let {
                                            androidx.compose.material3.Text(
                                                text = it,
                                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                                            )
                                        }

                                    }
                                },
                                navigationIcon = {
                                    // Search icon
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Outlined.ArrowBack,
                                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .clickable(onClick = {
                                                findMainActivityNavController().navigateUp()
                                            })
                                            .padding(horizontal = 12.dp, vertical = 16.dp)
                                            .height(24.dp),
                                        contentDescription = stringResource(id = R.string.search)
                                    )
                                },
                                actions = {
                                    // Info icon
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Outlined.Info,
                                        tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .clickable(onClick = {
                                                //TODO
                                            })
                                            .padding(horizontal = 12.dp, vertical = 16.dp)
                                            .height(24.dp),
                                        contentDescription = stringResource(id = R.string.info)
                                    )
                                }
                            )
                        }
                    ) {
                        ContactDetailCompose(
                            contactViewModel = contactViewModel,
                            navController = findMainActivityNavController()
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun ContactDetailCompose(
    contactViewModel: ContactViewModel,
    navController: NavController? = null,
    onClick: (NavDirections) -> Unit = {}
) {
    val uiState by contactViewModel.uiState.collectAsState()
    uiState.contactDetail?.let {
        ContactUserInfoCompose(it, navController, onClick)
    }
}

@InternalCoroutinesApi
@Preview
@Composable
fun PreviewContactUserInfoCompose() {

}


@Composable
fun ContactUserInfoCompose(
    contactDetail: ContactDetail,
    navController: NavController? = null,
    onClick: (NavDirections) -> Unit = {}
) {
    val cornerSize by remember { mutableStateOf(CornerSize(4.dp)) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBackground.E5E9F2)
    ) {

        Row(modifier = Modifier
            .background(ColorBackground.Primary)
            .fillMaxWidth()
            .clickable {
            }
            .padding(10.dp, 10.dp, 10.dp, 10.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                modifier = Modifier
                    .size(60.dp)
                    .clip(
                        MaterialTheme.shapes.small.copy(
                            topStart = cornerSize,
                            topEnd = cornerSize,
                            bottomEnd = cornerSize,
                            bottomStart = cornerSize
                        )
                    ),
                model = contactDetail.friend.avatar,
                contentDescription = contactDetail.friend.nickname
            )
            Spacer(modifier = Modifier.size(10.dp))
            Column {
                Text("用户名：${contactDetail.friend.username}")
                Text("昵称：${contactDetail.friend.nickname}")
            }
        }
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                val request = NavDeepLinkRequest.Builder
                    .fromUri("android-app://com.nxg.app/conversation_chat_fragment/0?chatId=${contactDetail.friend.uuid}".toUri())
                    .build()
                navController?.navigate(request)
            },
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = ColorBackground.Primary,
                contentColor = ColorText.Normal
            )
        ) {
            Icon(
                Icons.Filled.Chat,
                contentDescription = "Chat",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "发消息")
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = {},
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = ColorBackground.Primary,
                contentColor = ColorText.Normal
            )
        ) {
            Icon(
                Icons.Filled.Videocam,
                contentDescription = "VideoCall",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = "音视频通话")
        }
    }

}
