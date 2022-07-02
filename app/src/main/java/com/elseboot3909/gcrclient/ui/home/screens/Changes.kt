@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)

package com.elseboot3909.gcrclient.ui.home.screens

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.entity.internal.ChangeInfoPreview
import com.elseboot3909.gcrclient.entity.internal.convertInPreview
import com.elseboot3909.gcrclient.repository.SearchParamsRepository
import com.elseboot3909.gcrclient.ui.MasterActivity
import com.elseboot3909.gcrclient.ui.MasterScreens
import com.elseboot3909.gcrclient.ui.common.getBackgroundColor
import com.elseboot3909.gcrclient.ui.common.progress.ProgressBar
import com.elseboot3909.gcrclient.ui.home.screens.common.ChangesList
import com.elseboot3909.gcrclient.utils.Constants
import com.elseboot3909.gcrclient.viewmodel.SearchParamsViewModel
import com.elseboot3909.gcrclient.viewmodel.ChangesListViewModel
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
    ProgressBar()
}

@Composable
private fun ChangesTopAppBar(
    drawerState: DrawerState,
    masterNavCtl: NavHostController,
    spRepo: SearchParamsRepository = get(),
    spViewModel: SearchParamsViewModel = getViewModel(owner = LocalContext.current as MasterActivity),
    clViewModel: ChangesListViewModel = getViewModel(owner = LocalContext.current as MasterActivity)
) {
    val scope = rememberCoroutineScope()
    val offset by spViewModel.offset.observeAsState(0)
    Scaffold(
        topBar = {
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
                    text = spViewModel.oldSearchString.value?.ifEmpty { "Search" } ?: "Search",
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
                val showPrevBtn by clViewModel.showPrevBtn.observeAsState(false)
                if (showPrevBtn) {
                    Button(
                        onClick = { spRepo.offset.value -= Constants.MAX_FETCHED_CHANGES },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .align(Alignment.BottomStart),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getBackgroundColor(),
                            contentColor = ButtonDefaults.filledTonalButtonColors()
                                .contentColor(true).value
                        )
                    ) {
                        Text(text = "Go ${offset - 24} to $offset")
                    }
                }
                val showNextBtn by clViewModel.showNextBtn.observeAsState(true)
                if (showNextBtn) {
                    Log.e(Constants.LOG_TAG, showNextBtn.toString())
                    Button(
                        onClick = { spRepo.offset.value += Constants.MAX_FETCHED_CHANGES },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .align(Alignment.BottomEnd)
                            .animateContentSize(animationSpec = tween(durationMillis = 275)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getBackgroundColor(),
                            contentColor = ButtonDefaults.filledTonalButtonColors()
                                .contentColor(true).value
                        )
                    ) {
                        Text(text = "Go ${offset + 26} to ${offset + 25 * 2}")
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = {
            ChangesContent(it, clViewModel, masterNavCtl)
        }
    )
}

@Composable
private fun ChangesContent(
    paddingValues: PaddingValues,
    clViewModel: ChangesListViewModel,
    masterNavCtl: NavHostController
) {
    val listState = rememberLazyListState()
    val changesList = clViewModel.changesList.observeAsState(ArrayList())

    val changePreviewList = ArrayList<ChangeInfoPreview>()
    changesList.value.forEachIndexed { index, changeInfo ->
        changePreviewList.add(convertInPreview(changeInfo, index))
    }

    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        SwipeRefresh(state = SwipeRefreshState(false), onRefresh = { clViewModel.loadChangesList() }) {
            ChangesList(changePreviewList, listState, masterNavCtl, true)
        }
    }
}
