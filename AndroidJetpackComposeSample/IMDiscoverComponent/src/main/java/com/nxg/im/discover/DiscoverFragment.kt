package com.nxg.im.discover

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import com.nxg.im.commonui.R
import com.nxg.im.commonui.theme.JetchatTheme
import com.nxg.mvvm.logger.SimpleLogger
import com.nxg.mvvm.ui.BaseBusinessFragment

class DiscoverFragment : BaseBusinessFragment(), SimpleLogger {

    private val discoverViewModel: DiscoverViewModel by activityViewModels()

    @OptIn(ExperimentalMaterial3Api::class)
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
                            CenterAlignedTopAppBar(
                                title = {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        androidx.compose.material3.Text(
                                            text = "发现",
                                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                                        )

                                    }
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
                                        contentDescription = stringResource(id = R.string.info)
                                    )
                                }
                            )
                        }
                    ) {
                        val uiState by discoverViewModel.uiState.collectAsState()
                        uiState.discoveryList.forEach {

                        }

                    }
                }
            }
        }
    }
}
