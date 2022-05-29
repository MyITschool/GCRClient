package com.elseboot3909.gcrclient.ui.main

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.entity.ChangeInfoPreview
import com.elseboot3909.gcrclient.entity.convertInPreview
import com.elseboot3909.gcrclient.ui.change.ChangeActivity
import com.elseboot3909.gcrclient.ui.common.ChangesListItem
import com.elseboot3909.gcrclient.ui.main.search.SearchActivity
import com.elseboot3909.gcrclient.ui.theme.getBackgroundColor
import com.elseboot3909.gcrclient.utils.Constants
import kotlinx.coroutines.launch
import java.io.Serializable

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun Changes(drawerState: DrawerState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val changesModel: ChangesViewModel by (context as MainActivity).viewModels()
    val searchModel: SearchParamsViewModel by (context as MainActivity).viewModels()

    val listState = rememberLazyListState()

    Scaffold(topBar = {
        var strSearch by remember { mutableStateOf("") }
        val showProgress: Boolean by changesModel.getStatus().observeAsState(false)
        if (showProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            scope.launch { listState.scrollToItem(0) }
        }
        searchModel.getSearchStr().observe(context as MainActivity) { strSearch = it }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                .background(color = getBackgroundColor(), shape = RoundedCornerShape(48.dp))
                .clip(RoundedCornerShape(48.dp))
                .height(48.dp)
                .fillMaxWidth()
                .clickable {
                    context.let {
                        val intent = Intent(it, SearchActivity::class.java)
                        intent.putExtra(Constants.SEARCH_STRING_KEY, strSearch)
                        it.activityResultLauncher.launch(intent)
                    }
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
                text = strSearch.ifEmpty { "Search" },
                modifier = Modifier
                    .padding(start = 4.dp, end = 12.dp)
                    .align(Alignment.CenterVertically)
                    .wrapContentHeight()
            )
        }
    }) {
        Box(modifier = Modifier.padding(it)) {
            Content(changesModel, searchModel, listState)
        }
    }
}

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun Content(
    changesModel: ChangesViewModel,
    searchModel: SearchParamsViewModel,
    listState: LazyListState
) {
    val context = LocalContext.current
    val paramsList: String by searchModel.getQuery().observeAsState(searchModel.getQuery().value ?: "")
    Log.e(Constants.LOG_TAG, paramsList)
    var offset by remember { mutableStateOf(0) }
    val changesList: ArrayList<ChangeInfo> by changesModel.getChangesList(paramsList, offset).observeAsState(ArrayList())
    val changePreviewList = ArrayList<ChangeInfoPreview>()
    changesList.forEach {
        changePreviewList.add(convertInPreview(it))
    }

    Column(
        modifier = Modifier
            .padding(top = 6.dp)
            .fillMaxSize()
    ) {
        LazyColumn(state = listState) {
            itemsIndexed(items = changePreviewList) { index, item ->
                ChangesListItem(item) {
                    val intent =
                        Intent(context as MainActivity, ChangeActivity::class.java)
                    intent.putExtra("changeInfo", changesList[index] as Serializable)
                    context.activityResultLauncher.launch(intent)
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp)
                ) {
                    if (offset != 0) {
                        ExtendedFloatingActionButton(
                            onClick = { offset -= Constants.MAX_FETCHED_CHANGES },
                            content = {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NavigateBefore,
                                        contentDescription = null
                                    )
                                    Text(text = "Next ${offset - Constants.MAX_FETCHED_CHANGES + 1} .. $offset")
                                }
                            },
                            modifier = Modifier.align(Alignment.BottomStart)
                        )
                    }
                    if (changePreviewList.size == Constants.MAX_FETCHED_CHANGES) {
                        ExtendedFloatingActionButton(
                            onClick = { offset += Constants.MAX_FETCHED_CHANGES },
                            content = {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Next ${offset + Constants.MAX_FETCHED_CHANGES + 1} .. ${offset + Constants.MAX_FETCHED_CHANGES * 2}")
                                    Icon(
                                        imageVector = Icons.Default.NavigateNext,
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }
    }
}

