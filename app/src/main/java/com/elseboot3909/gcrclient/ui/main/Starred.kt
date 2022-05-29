package com.elseboot3909.gcrclient.ui.main

import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.ChangeInfo
import com.elseboot3909.gcrclient.entity.ChangeInfoPreview
import com.elseboot3909.gcrclient.entity.convertInPreview
import com.elseboot3909.gcrclient.ui.change.ChangeActivity
import com.elseboot3909.gcrclient.ui.common.ChangesListItem
import kotlinx.coroutines.launch
import java.io.Serializable

@ExperimentalMaterial3Api
@ExperimentalAnimationApi
@Composable
fun Starred(drawerState: DrawerState) {
    val context = LocalContext.current
    val backgroundColor =
        if (isSystemInDarkTheme()) colorResource(R.color.accent_2_800) else colorResource(R.color.neutral_1_100)
    var searchStr by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Scaffold(topBar = {
        TopAppBar(
            title = {
                BasicTextField(
                    value = searchStr,
                    onValueChange = { searchStr = it },
                    modifier = Modifier
                        .padding(start = 18.dp, end = 18.dp)
                        .wrapContentHeight()
                        .fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
                )
            },
            navigationIcon = {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null
                    )
                }
            },
            backgroundColor = backgroundColor
        )
    }) {
        Box(modifier = Modifier.padding(it)) {
            val model: StarredViewModel by (context as MainActivity).viewModels()
            val starredList: ArrayList<ChangeInfo> by model.getStarredList()
                .observeAsState(ArrayList())

            val starredPreviewList = ArrayList<ChangeInfoPreview>()
            starredList.forEach { item ->
                starredPreviewList.add(convertInPreview(item))
            }

            Column(modifier = Modifier.padding(top = 6.dp)) {
                LazyColumn {
                    itemsIndexed(starredPreviewList.map { n -> n }
                        .filter { n ->
                            n.subject.lowercase()
                                .contains(searchStr.lowercase()) || searchStr.isEmpty()
                        }) { index, item ->
                        ChangesListItem(item) {
                            val intent =
                                Intent(context as MainActivity, ChangeActivity::class.java)
                            intent.putExtra("changeInfo", starredList[index] as Serializable)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}