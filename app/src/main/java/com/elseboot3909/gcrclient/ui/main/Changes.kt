package com.elseboot3909.gcrclient.ui.main

import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.model.ChangesViewModel
import com.elseboot3909.gcrclient.ui.main.search.SearchActivity
import com.elseboot3909.gcrclient.utils.Constants
import kotlinx.coroutines.launch

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun Changes(drawerState: DrawerState) {
    val context = LocalContext.current
    val paramsList = remember { mutableStateOf(ArrayList<String>()) }
    if (paramsList.value.isEmpty()) paramsList.value.add("status:open")

    val model: ChangesViewModel by (context as MainActivity).viewModels()
    val changesList: ArrayList<ChangeInfo> by model.getChangesList(paramsList.value, 0).observeAsState(ArrayList())

    val backgroundColor =
        if (isSystemInDarkTheme()) colorResource(R.color.accent_2_800) else colorResource(R.color.neutral_1_100)
    val scope = rememberCoroutineScope()
    Scaffold(topBar = {
        val searchModel: SearchParamsViewModel by (context as MainActivity).viewModels()
        var strSearch by remember { mutableStateOf("") }
        searchModel.getSearchStr().observe(context as MainActivity) { strSearch = it }
        Row(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 6.dp)
                .background(color = backgroundColor, shape = RoundedCornerShape(48.dp))
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
            Column(modifier = Modifier.padding(top = 6.dp)) {
                LazyColumn {
                    itemsIndexed(changesList) { index, item ->
                        ChangesListItem(index, item)
                    }
                }
            }
        }
    }
}

