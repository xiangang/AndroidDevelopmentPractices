package com.nxg.androidsample.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.findNavController
import com.nxg.androidsample.R
import com.nxg.im.chat.component.MainViewModel
import com.nxg.im.commonui.components.JetchatIcon
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.im.contact.component.ContactListCompose
import com.nxg.im.contact.component.ContactViewModel
import com.nxg.im.contact.component.ContactViewModelFactory
import com.nxg.im.conversation.component.ConversationListCompose
import com.nxg.im.conversation.component.ConversationViewModel
import com.nxg.im.conversation.component.ConversationViewModelFactory
import com.nxg.im.discover.component.DiscoverListCompose
import com.nxg.im.discover.component.DiscoverViewModel
import com.nxg.im.user.component.login.LoginViewModel
import com.nxg.im.user.component.login.LoginViewModelFactory
import com.nxg.im.user.component.profile.ProfileCompose
import com.nxg.im.user.component.profile.ProfileViewModel
import com.nxg.im.user.component.profile.ProfileViewModelFactory
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseViewModelFragment
import kotlinx.coroutines.launch

class KtChatShellFragment : BaseViewModelFragment(), SimpleLogger {

    private val viewModel: MainViewModel by activityViewModels()

    private val ktChatViewModel: KtChatViewModel by activityViewModels()

    private val conversationViewModel: ConversationViewModel by activityViewModels {
        ConversationViewModelFactory()
    }

    private val contactViewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory()
    }

    private val discoverViewModel: DiscoverViewModel by activityViewModels()

    private val profileViewModel: ProfileViewModel by activityViewModels {
        ProfileViewModelFactory()
    }

    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                KtChatCompose()
            }
        }
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun KtChatCompose() {
        JetchatTheme {
            val ktChatUIState by ktChatViewModel.uiState.collectAsState()
            val navHostController = rememberNavController()
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = ktChatUIState.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        },
                        modifier = Modifier,
                        navigationIcon = {
                            JetchatIcon(
                                contentDescription = stringResource(id = R.string.navigation_drawer_open),
                                modifier = Modifier
                                    .size(64.dp)
                                    .clickable(onClick = {
                                        viewModel.openDrawer()
                                    })
                                    .padding(16.dp)
                            )
                        },
                        actions = {
                            // Search icon
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        //TODO
                                    })
                                    .padding(horizontal = 12.dp, vertical = 16.dp)
                                    .height(24.dp),
                                contentDescription = stringResource(id = R.string.search)
                            )
                            // Info icon
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        //TODO
                                    })
                                    .padding(horizontal = 12.dp, vertical = 16.dp)
                                    .height(24.dp),
                                contentDescription = stringResource(id = R.string.add)
                            )
                        }
                    )
                },
                bottomBar = {
                    var currentSelect by remember {
                        mutableStateOf(0)
                    }
                    NavigationBar {
                        ktChatViewModel.navigationBars.forEachIndexed { index, screen ->
                            val name = resources.getString(screen.nameResourceId)
                            NavigationBarItem(
                                selected = index == currentSelect,
                                onClick = {
                                    currentSelect = index
                                    ktChatViewModel.changeTitle(name)
                                    navHostController.navigate(screen.route)
                                },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = name
                                    )
                                },
                                label = {
                                    Text(
                                        text = (name)
                                    )
                                },
                            )
                        }
                    }
                }
            ) {
                NavHost(
                    navController = navHostController,
                    startDestination = "chat"
                ) {
                    composable("chat") {
                        conversationViewModel.refresh()
                        val scope = rememberCoroutineScope()
                        ConversationListCompose(
                            conversationViewModel,
                            findNavController()
                        ) { navController, conversation ->
                            scope.launch {
                                conversationViewModel.loadConversation(conversation)
                                val request = NavDeepLinkRequest.Builder
                                    .fromUri("android-app://com.nxg.app/conversation_chat_fragment/${conversation.chatType}?chatId=${conversation.chatId}".toUri())
                                    .build()
                                navController.navigate(request)
                            }
                        }
                    }
                    composable("contact") {
                        val scope = rememberCoroutineScope()
                        ContactListCompose(
                            contactViewModel,
                            findNavController()
                        ) { navController, friend ->
                            scope.launch {
                                contactViewModel.loadContactDetail(friend)
                                //从壳跳转到联系人详情页
                                val request = NavDeepLinkRequest.Builder
                                    .fromUri("android-app://com.nxg.app/contact_detail_fragment".toUri())
                                    .build()
                                navController.navigate(request)
                            }
                        }
                    }
                    composable("discover") {
                        val discoverUIState by discoverViewModel.uiState.collectAsState()
                        DiscoverListCompose(discoverUIState)
                    }
                    composable("profile") {
                        profileViewModel.getLoginData()
                        val profileUIState by profileViewModel.uiState.collectAsState()
                        Log.i("TAG", "KtChatCompose: profileUIState ${profileUIState.user}")
                        profileUIState.user?.let {
                            ProfileCompose(
                                profileViewModel, loginViewModel,
                                it,
                                findNavController()
                            ) { _, friend ->

                            }
                        }
                    }
                }
            }
        }
    }
}
