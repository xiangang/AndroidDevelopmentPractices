package com.nxg.im.user.component.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import coil.compose.AsyncImage
import com.nxg.commonui.theme.ColorBackground
import com.nxg.commonui.theme.ColorError
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.im.core.module.user.User
import com.nxg.im.user.component.login.LoginViewModel
import com.nxg.im.user.component.login.LoginViewModelFactory
import com.nxg.mvvm.ktx.findMainActivityNavController
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseBusinessFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : BaseBusinessFragment(), SimpleLogger {

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
        profileViewModel.getLoginData()
        return ComposeView(requireContext()).apply {
            setContent {
                JetchatTheme {
                    val uiState by profileViewModel.uiState.collectAsState()
                    uiState.user?.let {
                        ProfileCompose(
                            it,
                            findMainActivityNavController()
                        ) { navController, friend ->

                        }
                    }
                }
            }
        }
    }


    @Composable
    fun ProfileCompose(
        user: User,
        navController: NavController,
        onClick: (NavController, User) -> Unit
    ) {
        val scope = rememberCoroutineScope()
        val cornerSize by remember { mutableStateOf(CornerSize(4.dp)) }
        logger.debug { "ProfileCompose: $user" }
        LazyColumn(
            modifier = Modifier
                .background(ColorBackground.Primary)
                .fillMaxSize()
        ) {
            item {
                Row(modifier = Modifier
                    .clickable {
                        onClick(navController, user)
                    }
                    .padding(10.dp, 10.dp, 10.dp, 0.dp)) {
                    AsyncImage(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(
                                MaterialTheme.shapes.small.copy(
                                    topStart = cornerSize,
                                    topEnd = cornerSize,
                                    bottomEnd = cornerSize,
                                    bottomStart = cornerSize
                                )
                            ),
                        model = user.avatar,
                        contentDescription = user.nickname
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Column {
                        Text(user.nickname)
                    }
                }
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            profileViewModel.logout()
                            loginViewModel.logout()
                            withContext(Dispatchers.Main) {
                                val request = NavDeepLinkRequest.Builder
                                    .fromUri("android-app://com.nxg.app/loginFragment".toUri())
                                    .build()
                                navController.popBackStack()
                                navController.navigate(request)
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = ColorError.Primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "退出登录")
                }
            }
        }
    }
}
