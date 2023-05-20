package com.nxg.im.contact

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import androidx.room.util.TableInfo
import coil.compose.AsyncImage
import com.nxg.commonui.theme.AndroidJetpackComposeSampleTheme
import com.nxg.commonui.theme.ColorBackground
import com.nxg.im.core.module.user.User
import com.nxg.mvvm.ui.BaseViewModelFragment

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
                AndroidJetpackComposeSampleTheme {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("通讯录") },
                            )
                        }
                    ) {
                        ContactDetailCompose(contactViewModel)
                    }
                }
            }
        }
    }
}


@Composable
fun ContactDetailCompose(
    contactViewModel: ContactViewModel,
    onClick: (NavDirections) -> Unit = {}
) {
    val uiState by contactViewModel.uiState.collectAsState()
    uiState.contactDetail?.let {
        ContactUserInfoCompose(it, onClick)
    }
}

@Composable
fun ContactUserInfoCompose(
    contactDetail: ContactDetail,
    onClick: (NavDirections) -> Unit = {}
) {
    val cornerSize by remember { mutableStateOf(CornerSize(4.dp)) }
    Row(modifier = Modifier
        .clickable {
            //onClick.invoke()
        }
        .padding(10.dp, 10.dp, 10.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
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
            model = contactDetail.user.avatar,
            contentDescription = contactDetail.user.nickname
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(contactDetail.user.nickname)
        }
    }
}
