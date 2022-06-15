@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.home.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.entity.internal.ChangeInfoPreview
import com.elseboot3909.gcrclient.entity.internal.convertInPreview
import com.elseboot3909.gcrclient.ui.MasterScreens
import com.elseboot3909.gcrclient.ui.common.ChangesList
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.common.progress.ProgressBar
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.viewmodel.home.ChangesViewModel
import com.elseboot3909.gcrclient.repository.search.SearchParamsRepository
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.getViewModel

@Composable
internal fun Changes(
    drawerState: DrawerState,
    masterNavCtl: NavHostController,
) {
    ChangesTopAppBar(drawerState, masterNavCtl)
}

@Composable
private fun ChangesTopAppBar(
    drawerState: DrawerState,
    masterNavCtl: NavHostController,
    searchRepo: SearchParamsRepository = get(),
    changesModel: ChangesViewModel = getViewModel()
) {
    val scope = rememberCoroutineScope()
    val offset by searchRepo.offset.collectAsState()
    val changesList = changesModel.changesList.observeAsState(ArrayList())
    Scaffold(
        topBar = {
            ProgressBar()
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    .background(color = getBackgroundColor(), shape = RoundedCornerShape(48.dp))
                    .clip(RoundedCornerShape(48.dp))
                    .height(48.dp)
                    .fillMaxWidth()
                    .clickable {
                        masterNavCtl.navigate(route = MasterScreens.SearchScreen.route)
                    }
                    .padding(start = 4.dp)
            ) {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null
                    )
                }
                Text(
                    text = searchRepo.searchString.ifEmpty { "Search" },
                    modifier = Modifier
                        .padding(start = 4.dp, end = 12.dp)
                        .align(Alignment.CenterVertically)
                        .wrapContentHeight()
                )
            }
        },
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                if (offset != 0) {
                    Button(
                        onClick = { searchRepo.offset.value -= Constants.MAX_FETCHED_CHANGES },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .align(Alignment.BottomStart),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getBackgroundColor(),
                            contentColor = ButtonDefaults.filledTonalButtonColors()
                                .contentColor(true).value
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 22.dp)
                    ) {
                        Text(text = "Go ${offset - 24} to $offset")
                    }
                }
                if (changesList.value.size >= Constants.MAX_FETCHED_CHANGES) {
                    Button(
                        onClick = { searchRepo.offset.value += Constants.MAX_FETCHED_CHANGES },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .align(Alignment.BottomEnd)
                            .animateContentSize(animationSpec = tween(durationMillis = 275)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getBackgroundColor(),
                            contentColor = ButtonDefaults.filledTonalButtonColors()
                                .contentColor(true).value
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 22.dp)
                    ) {
                        Text(text = "Go ${offset + 26} to ${offset + 25 * 2}")
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = {
            ChangesContent(it, changesModel, masterNavCtl)
        }
    )
}

@Composable
private fun ChangesContent(
    paddingValues: PaddingValues,
    changesModel: ChangesViewModel,
    masterNavCtl: NavHostController
) {
    val listState = rememberLazyListState()
    val changesList = changesModel.changesList.observeAsState(ArrayList())

    val changePreviewList = ArrayList<ChangeInfoPreview>()
    changesList.value.forEachIndexed { index, changeInfo ->
        changePreviewList.add(convertInPreview(changeInfo, index))
    }

    Box(
        modifier = Modifier.padding(paddingValues).fillMaxSize()
    ) {
        SwipeRefresh(state = SwipeRefreshState(false), onRefresh = { changesModel.loadChangesList() }) {
            ChangesList(changePreviewList, listState, masterNavCtl, true)
        }
    }
}
