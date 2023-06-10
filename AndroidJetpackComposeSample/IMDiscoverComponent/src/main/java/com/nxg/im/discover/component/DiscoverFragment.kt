package com.nxg.im.discover.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import com.nxg.im.commonui.components.messageFormatter
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseBusinessFragment

class DiscoverFragment : BaseBusinessFragment(), SimpleLogger {

    private val discoverViewModel: DiscoverViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                JetchatTheme {
                    val uiState by discoverViewModel.uiState.collectAsState()
                    DiscoverListCompose(uiState)
                }
            }
        }
    }
}

@Composable
fun DiscoverListCompose(uiState: DiscoveryUiState) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 1.dp)
            .fillMaxSize()
    ) {
        uiState.discoveryList.forEach {
            DiscoverItemCompose(
                it,
            ) { discovery ->
            }
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}

@Preview
@Composable
fun DiscoverListComposePreview() {
    val uiState = DiscoveryUiState(
        listOf(
            Discovery(Icons.Rounded.Camera, "朋友圈"),
            Discovery(Icons.Rounded.Camera, "朋友圈"),
            Discovery(Icons.Rounded.Camera, "朋友圈"),
            Discovery(Icons.Rounded.Camera, "朋友圈"),
            Discovery(Icons.Rounded.Camera, "朋友圈"),
            Discovery(Icons.Rounded.Camera, "朋友圈")
        )
    )
    DiscoverListCompose(uiState)
}

@Composable
fun DiscoverItemCompose(
    discovery: Discovery,
    onClick: (Discovery) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .shadow(
            elevation = 2.dp,
            shape = RectangleShape
        )
        .background(if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background else Color.White)
        .clickable {
            onClick(discovery)
        }
        .padding(10.dp, 10.dp, 10.dp, 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            discovery.icon,
            contentDescription = discovery.name,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(10.dp))
        Column {
            Text(
                text = discovery.name,
                style = MaterialTheme.typography.titleSmall,
                color = if (isSystemInDarkTheme()) Color.White else LocalContentColor.current
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Icon(
            Icons.Filled.KeyboardArrowRight,
            contentDescription = discovery.name,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }

}