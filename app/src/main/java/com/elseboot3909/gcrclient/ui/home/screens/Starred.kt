@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.home.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.entity.internal.ChangeInfoPreview
import com.elseboot3909.gcrclient.entity.internal.convertInPreview
import com.elseboot3909.gcrclient.repository.StarredRepository
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.home.screens.common.ChangesList
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.common.progress.ProgressBar
import com.elseboot3909.gcrclient.viewmodel.StarredViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
internal fun Starred(
    drawerState: DrawerState,
    masterNavCtl: NavHostController,
    sViewModel: StarredViewModel = getViewModel(owner = LocalContext.current as MasterActivity),
    sRepo: StarredRepository = get()
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Scaffold(topBar = {
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null
                    )
                }
            },
            backgroundColor = getBackgroundColor()
        )
    }) {
        val starredList = sViewModel.starredList.observeAsState(ArrayList())
        val changePreviewList = ArrayList<ChangeInfoPreview>()
        starredList.value.forEachIndexed { index, changeInfo ->
            changePreviewList.add(convertInPreview(changeInfo, index))
        }

        Box(modifier = Modifier.padding(it)) {
            ProgressBar()
            SwipeRefresh(
                state = SwipeRefreshState(false),
                onRefresh = { sRepo.loadStarredChanges() },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxSize()
            ) {
                ChangesList(changePreviewList, listState, masterNavCtl)
            }
        }
    }
}