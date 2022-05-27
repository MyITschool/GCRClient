package com.elseboot3909.gcrclient.ui.main.search

import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.elseboot3909.gcrclient.R
import com.elseboot3909.gcrclient.entity.ProjectInfo
import com.elseboot3909.gcrclient.utils.Constants
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@ExperimentalMaterial3Api
@Composable
fun ProjectsList(navController: NavHostController, searchProjects: ArrayList<String>) {
    val context = LocalContext.current
    val initProjects = ArrayList(searchProjects)
    var selectedCount by remember { mutableStateOf(searchProjects.size) }
    var searchStr by rememberSaveable { mutableStateOf("") }
    val backgroundColor =
        if (isSystemInDarkTheme()) colorResource(R.color.accent_2_800) else colorResource(R.color.neutral_1_100)
    Scaffold(topBar = {
        TopAppBar(backgroundColor = backgroundColor) {
            val focusRequester = remember { FocusRequester() }
            BasicTextField(
                value = searchStr,
                onValueChange = { searchStr = it },
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                singleLine = true,
                textStyle = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { androidx.compose.material3.Text(text = "Select $selectedCount " + if (selectedCount == 1) "project" else "projects") },
                icon = {
                    Icon(
                        imageVector = if (selectedCount <= 1) Icons.Default.Done else Icons.Default.DoneAll,
                        contentDescription = null
                    )
                },
                onClick = {
                    (context as SearchActivity).let {
                        if (initProjects != searchProjects) {
                            it.intent.putExtra(Constants.SEARCH_PROJECTS_KEY, searchProjects)
                            it.setResult(Constants.SEARCH_ACQUIRED, it.intent)
                        }
                    }
                    navController.popBackStack()
                }
            )
        }) {
        Box(modifier = Modifier.padding(it)) {
            val model: ProjectsListViewModel by (context as SearchActivity).viewModels()
            val projectsMap: HashMap<String, ProjectInfo> by model.getProjects()
                .observeAsState(HashMap())
            val swipeRefreshState = rememberSwipeRefreshState(false)
            SwipeRefresh(state = swipeRefreshState, onRefresh = { model.refreshProjects() }) {
                LazyColumn(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)) {
                    items(projectsMap.keys.toList().sorted().map { n -> n }
                        .filter { n -> n.contains(searchStr) || searchStr.isEmpty() }) { projectName ->
                        val projectInfo = projectsMap[projectName]
                        var isSelected by remember {
                            mutableStateOf(searchProjects.contains(projectName))
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, bottom = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    isSelected = !isSelected
                                    if (searchProjects.contains(projectName)) {
                                        selectedCount--
                                        searchProjects.remove(projectName)
                                    } else {
                                        selectedCount++
                                        searchProjects.add(projectName)
                                    }
                                },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(
                                    0xFFE3F7EA
                                ) else colorResource(R.color.neutral_1_100)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp)
                                    .defaultMinSize(minHeight = 32.dp)
                                    .padding(top = 4.dp, bottom = 4.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = projectName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (projectInfo?.state?.isNotEmpty() == true) {
                                    Row {
                                        Text(
                                            text = "Project status: ",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Color.Black,
                                        )
                                        Text(
                                            text = projectInfo.state,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = when (projectInfo.state) {
                                                "ACTIVE" -> Color(0xFF68D593)
                                                "HIDDEN" -> Color(0xFFFF7272)
                                                else -> Color(0xFFDE7411)
                                            }
                                        )
                                    }
                                }
                                if (projectInfo?.description?.isNotEmpty() == true) {
                                    Text(
                                        text = "Description: " + projectInfo.description,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color.Black,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}