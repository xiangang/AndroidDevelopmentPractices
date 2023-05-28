package com.nxg.im.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import coil.compose.AsyncImage
import com.nxg.commonui.theme.ColorBackground
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.mvvm.ktx.findMainActivityNavController
import com.nxg.mvvm.ui.BaseViewModelFragment
import kotlinx.coroutines.launch

class ContactListFragment : BaseViewModelFragment() {

    private val contactViewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JetchatTheme {
                    val scope = rememberCoroutineScope()
                    ContactListCompose(
                        contactViewModel,
                        findMainActivityNavController()
                    ) { navController, friend ->
                        scope.launch {
                            contactViewModel.loadContactDetail(friend.user)
                            //从壳跳转到联系人详情页
                            val request = NavDeepLinkRequest.Builder
                                .fromUri("android-app://com.nxg.app/contact_detail_fragment".toUri())
                                .build()
                            navController.navigate(request)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactListCompose(
    contactViewModel: ContactViewModel,
    navController: NavController,
    onClick: (NavController, Friend) -> Unit
) {
    val uiState by contactViewModel.uiState.collectAsState()
    LazyColumn(
        modifier = Modifier
            .background(ColorBackground.Primary)
            .fillMaxSize()
    ) {
        uiState.contactList.forEach { contact ->
            when (contact) {
                is Contact.ContactVerifyMsg -> {

                }

                is Contact.ContactBackList -> {

                }

                is Contact.ContactGroupChat -> {

                }

                is Contact.ContactFriendListHeader -> {
                    stickyHeader {

                    }
                }

                is Contact.ContactFriendList -> {
                    contact.data.forEach {
                        item {
                            ContactItemCompose(
                                it,
                                navController,
                                onClick
                            )
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun ContactItemCompose(
    friend: Friend,
    navController: NavController,
    onClick: (NavController, Friend) -> Unit
) {
    val cornerSize by remember { mutableStateOf(CornerSize(4.dp)) }
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick(navController, friend)
        }
        .padding(10.dp, 5.dp, 10.dp, 5.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            modifier = Modifier
                .size(40.dp)
                .clip(
                    MaterialTheme.shapes.small.copy(
                        topStart = cornerSize,
                        topEnd = cornerSize,
                        bottomEnd = cornerSize,
                        bottomStart = cornerSize
                    )
                ),
            model = friend.user.avatar,
            contentDescription = friend.user.nickname
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(friend.user.nickname)
        }
    }
}
